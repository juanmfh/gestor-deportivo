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
import javax.swing.SwingWorker;
import modelo.Equipo;
import modelo.Prueba;
import modelo.Registro;
import dao.EquipoJpa;
import dao.ParticipanteJpa;
import dao.PruebaJpa;
import dao.RegistroJpa;
import dao.exceptions.NonexistentEntityException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.Participante;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 *
 * @author JuanM
 */
public class ImportarRegistros extends SwingWorker<Void, Void> {

    String ruta;
    
    private static final int PRIMERA_FILA = 2;
    private static final int PRIMERA_COLUMNA_PRUEBA = 2;
    private static final int PRIMERA_COLUMNA = 1;
    private static final int NUM_PARAMPRUEBA = 2;

    public ImportarRegistros(String rutaFichero) {
        ruta = rutaFichero;
    }

    @Override
    protected void done() {
        try {
            get();
            Coordinador.getInstance().setEstadoLabel("Registros importardos", Color.BLUE);
        } catch (InterruptedException | ExecutionException ex) {
            Coordinador.getInstance().setEstadoLabel(ex.getMessage(), Color.RED);
        } finally {
            RegistroJpa registrojpa = new RegistroJpa();
            List<Registro> registros = registrojpa.findByCompeticion(Coordinador.getInstance().getSeleccionada().getId());
            Coordinador.getInstance().getControladorPrincipal().cargarTablaRegistros(registros);
            Coordinador.getInstance().mostrarBarraProgreso(false);
            Coordinador.getInstance().getControladorPrincipal().cargarGruposEnRegistros();
            Coordinador.getInstance().getControladorPrincipal().cargarPruebasEnRegistros();
        }

    }

    @Override
    protected void process(List chunks) {
        Coordinador.getInstance().mostrarBarraProgreso(true);
    }

    @Override
    protected Void doInBackground() throws InputException {

        // Actualiza la interfaz (muestra la barra de estado)
        publish((Void) null);
        try {
            FileInputStream file = new FileInputStream(new File(ruta));
            Workbook workbook = WorkbookFactory.create(file);
            
            String data;
            Prueba prueba = null;
            
            // Por cada hoja del archivo excel...
            for (int numHoja = 0; numHoja < workbook.getNumberOfSheets(); numHoja++) {
                // Obtenemos el número de filas, columnas y la hoja.
                Sheet hoja = workbook.getSheetAt(numHoja);
                // Si la hoja es la de Tipos se descarta (hoja donde se encuentran las listas de tipos de prueba)
                if (!hoja.getHeader().equals("Tipos")) {
                
                    Iterator<Row> iteradorFila = hoja.iterator();
                    Row fila = iteradorFila.next(); //Cabecera, no contiene datos
                    fila = iteradorFila.next();     //Fila donde se encuentra los datos de las pruebas


                }  
            }
        } catch (FileNotFoundException ex) {
            throw new InputException("Archivo no encontrado");
        } catch (IOException ex) {
            throw new InputException("Archivo no válido");
        } catch (InvalidFormatException ex) {
            throw new InputException("Formato de archivo no válido");
        }
        
        
        /*Workbook excel = null;
        try {
            excel = Workbook.getWorkbook(new File(ruta));
            String data;
            Prueba prueba = null;
            // Por cada hoja del archivo excel...
            for (int numHoja = 0; numHoja < excel.getNumberOfSheets(); numHoja++) {

                // Obtenemos el número de filas, columnas y la hoja.
                Sheet hoja = excel.getSheet(numHoja);
                // Si la hoja es la de Tipos se descarta (hoja donde se encuentran las listas de tipos de prueba)
                if (!hoja.getName().equals("Tipos")) {
                    int numFilas = hoja.getRows();
                    int numColumnas = hoja.getColumns();

                    // Se establecen los índices en las celdas iniciales de datos
                    int fila = PRIMERA_FILA;
                    int columna = PRIMERA_COLUMNA_PRUEBA;

                    // Obtenemos la prueba con sus parámetros y si no existe se crea
                    if (columna + NUM_PARAMPRUEBA <= numColumnas) {
                        try {
                            String nombrePrueba = hoja.getCell(fila, columna).getContents().toString();
                            fila++;
                            TipoPrueba tipoPrueba = TipoPrueba.valueOf(hoja.getCell(fila, columna).getContents().toString());
                            fila++;
                            TipoResultado tipoResultado = TipoResultado.valueOf(hoja.getCell(fila, columna).getContents().toString());

                            PruebaJpa pruebajpa = new PruebaJpa();
                            prueba = pruebajpa.findPruebaByNombreCompeticion(nombrePrueba, Coordinador.getInstance().getSeleccionada().getId());
                            if (prueba == null) {
                                try {
                                    prueba = ControlPruebas.crearPrueba(nombrePrueba, tipoPrueba.toString(), tipoResultado.toString());
                                } catch (InputException ex) {
                                    throw new InputException(ex.getMessage());
                                }
                            } else {
                                // Si la prueba ya existe, se modifica
                                prueba.setTipo(tipoPrueba.toString());
                                prueba.setTiporesultado(tipoResultado.toString());
                                try {
                                    pruebajpa.edit(prueba);
                                } catch (NonexistentEntityException ex) {
                                    //throw new InputException("Prueba no encontrada");
                                } catch (Exception ex) {
                                    //throw new InputException(ex.getMessage());
                                }
                            }
                        } catch (IllegalArgumentException iae) {
                            //throw new InputException(iae.getMessage());
                        }
                    }
                    // Si la prueba es válida...
                    if (prueba != null) {
                        columna = PRIMERA_COLUMNA;
                        
                        while (fila < numFilas) {
                            // Si es una prueba individual..
                            if (prueba.getTipo().equals(TipoPrueba.Individual.toString())) {
                                try {
                                    // Leemos el dorsal
                                    data = hoja.getCell(columna, fila).getContents();
                                    Integer dorsal = Integer.parseInt(data.toString());
                                    ParticipanteJpa participanteJpa = new ParticipanteJpa();
                                    Participante participante = participanteJpa.findByDorsalAndCompeticion(dorsal,
                                            Coordinador.getInstance().getSeleccionada().getId());
                                    // Si existe una persona con ese dorsal se crean los registros, sino se pasa al siguiente dorsal
                                    if (participante != null) {
                                        columna++;
                                        while (columna < numColumnas) {                                            
                                            data = hoja.getCell(columna, fila).getContents();
                                            if (!data.isEmpty()) {
                                                // Se comprueba el tipo de Prueba y se crea un registro con los datos correspondientes
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
                                                        //throw new InputException(ex.getMessage());
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
                                                        //throw new InputException(ex.getMessage());
                                                    }
                                                }
                                            }
                                            columna++;
                                        }
                                    }else{
                                        //throw new InputException("Dorsal no encontrado");
                                    }
                                } catch (NumberFormatException nfe) {
                                    //throw new InputException("Formato numérico no válido");
                                } finally {
                                    columna = PRIMERA_COLUMNA;
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
                                    // Si existe un equipo con ese nombre se crean los registros 
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
                                    }else{
                                        //throw new InputException("Equipo no encontrado");
                                    }
                                } catch (NumberFormatException nfe) {
                                    //throw new InputException("Formato numérico no válido");
                                } finally {
                                    fila++;
                                    columna = PRIMERA_COLUMNA;
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException | BiffException ex) {
            throw new InputException("Error al abrir el archivo. Formato no válido");
        } finally {
            if (excel != null) {
                excel.close();
            }
        }*/
        return null;
    }

}
