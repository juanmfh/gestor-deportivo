package main;

import controlador.ControlPruebas;
import controlador.ControlRegistros;
import controlador.Coordinador;
import controlador.InputException;
import controlador.TipoPrueba;
import controlador.TipoResultado;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import modelo.Equipo;
import modelo.Prueba;
import modelo.Registro;
import dao.EquipoJpa;
import dao.ParticipanteJpa;
import dao.PruebaJpa;
import dao.RegistroJpa;
import dao.exceptions.NonexistentEntityException;
import modelo.Participante;

/**
 *
 * @author JuanM
 */
public class ImportarRegistros extends SwingWorker<Void, Void> {

    String ruta;

    public ImportarRegistros(String rutaFichero) {
        ruta = rutaFichero;
    }

    @Override
    protected void done() {
        RegistroJpa registrojpa = new RegistroJpa();
        List<Registro> registros = registrojpa.findByCompeticion(Coordinador.getInstance().getSeleccionada().getId());
        Coordinador.getInstance().getControladorPrincipal().cargarTablaRegistros(registros);
        Coordinador.getInstance().setEstadoLabel("Registros importardos", Color.BLUE);
        Coordinador.getInstance().mostrarBarraProgreso(false);
    }

    @Override
    protected Void doInBackground(){
        Workbook excel = null;
        try {
            excel = Workbook.getWorkbook(new File(ruta));
            String data;
            Prueba prueba = null;
            for (int numHoja = 0; numHoja < excel.getNumberOfSheets(); numHoja++) {

                Sheet hoja = excel.getSheet(numHoja);
                if (!hoja.getName().equals("Tipos")) {
                    int numFilas = hoja.getRows();
                    int numColumnas = hoja.getColumns();

                    int fila = 2;
                    int columna = 2;

                    // Obtenemos la prueba con sus parámetros y si no existe se crea
                    if (columna + 2 <= numColumnas) {
                        try {
                            String nombrePrueba = hoja.getCell(columna, fila).getContents().toString();
                            TipoPrueba tipoPrueba = TipoPrueba.valueOf(hoja.getCell(columna + 1, fila).getContents().toString());
                            TipoResultado tipoResultado = TipoResultado.valueOf(hoja.getCell(columna + 2, fila).getContents().toString());

                            PruebaJpa pruebajpa = new PruebaJpa();
                            prueba = pruebajpa.findPruebaByNombreCompeticion(nombrePrueba, Coordinador.getInstance().getSeleccionada().getId());
                            if (prueba == null){
                                try {
                                    prueba = ControlPruebas.crearPrueba(nombrePrueba, tipoPrueba.toString(), tipoResultado.toString());
                                } catch (InputException ex) {
                                }
                            }else {
                                // Si la prueba ya existe, se modifica con el tipo adecuado
                                prueba.setTipo(tipoPrueba.toString());
                                prueba.setTiporesultado(tipoResultado.toString());
                                try {
                                    pruebajpa.edit(prueba);
                                } catch (NonexistentEntityException ex) {
                                    
                                } catch (Exception ex) {
                                    
                                }
                            }
                        } catch (IllegalArgumentException iae) {
                        }
                    }
                    if (prueba != null) {
                        columna = 1;
                        fila = 4;
                        while (fila < numFilas) {
                            // Si es una prueba individual
                            if (prueba.getTipo().equals(TipoPrueba.Individual.toString())) {
                                try {
                                    // Leemos el dorsal
                                    data = hoja.getCell(columna, fila).getContents();
                                    Integer dorsal = Integer.parseInt(data.toString());
                                    ParticipanteJpa participanteJpa = new ParticipanteJpa();
                                    Participante participante = participanteJpa.findByDorsalAndCompeticion(dorsal,
                                            Coordinador.getInstance().getSeleccionada().getId());
                                    // Si existe una persona con ese dorsal
                                    if (participante != null) {
                                        columna++;
                                        while (columna < numColumnas) {
                                            // Si la prueba es de tipo tiempo
                                            data = hoja.getCell(columna, fila).getContents();
                                            if (!data.isEmpty()) {
                                                if (prueba.getTiporesultado().equals(TipoResultado.Tiempo.toString())) {
                                                    String tiempo = data.toString();
                                                    try {
                                                        ControlRegistros.crearRegistro(
                                                                dorsal,
                                                                prueba.getNombre(),
                                                                null,
                                                                false,
                                                                ControlRegistros.getSegundos(tiempo),
                                                                ControlRegistros.getMinutos(tiempo),
                                                                ControlRegistros.getHoras(tiempo));
                                                    } catch (InputException ex) {
                                                        
                                                    }
                                                } else {
                                                    String marca = data.toString();
                                                    try {
                                                        ControlRegistros.crearRegistro(
                                                                dorsal,
                                                                prueba.getNombre(),
                                                                null,
                                                                false,
                                                                Double.valueOf(marca),
                                                                null,
                                                                null);
                                                    } catch (InputException ex) {
                                                        
                                                    }
                                                }
                                            }
                                            columna++;
                                        }
                                        columna = 1;
                                    }
                                } catch (NumberFormatException nfe) {

                                } finally {
                                    fila++;
                                }
                                // Si es una prueba de equipos    
                            } else {
                                try {
                                    // Leemos el nombre del equipo
                                    data = hoja.getCell(columna, fila).getContents();
                                    String nombreEquipo = data.toString();
                                    EquipoJpa equipoJpa = new EquipoJpa();
                                    Equipo equipo = equipoJpa.findByNombreAndCompeticion(nombreEquipo,
                                            Coordinador.getInstance().getSeleccionada().getId());
                                    // Si existe una persona con ese dorsal
                                    if (equipo != null) {
                                        columna++;
                                        while (columna < numColumnas) {
                                            // Si la prueba es de tipo tiempo
                                            data = hoja.getCell(columna, fila).getContents();
                                            if (!data.isEmpty()) {
                                                if (prueba.getTiporesultado().equals(TipoResultado.Tiempo.toString())) {
                                                    String tiempo = data.toString();
                                                    try {
                                                        ControlRegistros.crearRegistro(
                                                                null,
                                                                prueba.getNombre(),
                                                                nombreEquipo,
                                                                false,
                                                                ControlRegistros.getSegundos(tiempo),
                                                                ControlRegistros.getMinutos(tiempo),
                                                                ControlRegistros.getHoras(tiempo));
                                                    } catch (InputException ex) {
                                                        
                                                    }
                                                } else {
                                                    String marca = data.toString();
                                                    try {
                                                        ControlRegistros.crearRegistro(
                                                                null,
                                                                prueba.getNombre(),
                                                                nombreEquipo,
                                                                false,
                                                                Double.valueOf(marca),
                                                                null,
                                                                null);
                                                    } catch (InputException ex) {
                                                        
                                                    }
                                                }
                                            }
                                            columna++;
                                        }
                                        columna = 1;
                                    }
                                } catch (NumberFormatException nfe) {

                                } finally {
                                    fila++;
                                }

                            }
                        }
                    }
                }
            }
        } catch (IOException | BiffException ex) {
            Logger.getLogger(IOFile.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (excel != null) {
                excel.close();
            }
        }
        return null;
    }

    public static void importarRegistrosDeExcel(String rutaFichero) {

    }

}
