package main;

import controlador.ControlPruebas;
import controlador.ControlRegistros;
import controlador.Coordinador;
import controlador.InputException;
import modelo.TipoPrueba;
import modelo.TipoResultado;
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
import modelo.Participante;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
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
                if (!hoja.getSheetName().equals("Tipos")) {
                    Iterator<Row> iteradorFila = hoja.iterator();
                    
                    Row fila = null;/* = iteradorFila.next();
                    fila = iteradorFila.next(); //Cabecera, no contiene datos
                    fila = iteradorFila.next();*/     //Fila donde se encuentra los datos de las pruebas
                    for(int i = 0; i<= PRIMERA_FILA - hoja.getFirstRowNum();i++){
                        fila = iteradorFila.next();
                    }
                    if (fila.getLastCellNum() < PRIMERA_COLUMNA_PRUEBA + NUM_PARAMPRUEBA + 1) {
                        // Se pasa a la siguiente hoja, parametros no correctos
                    } else {
                        int columna = PRIMERA_COLUMNA_PRUEBA;
                        Cell celda = fila.getCell(columna);
                        try {
                            String nombrePrueba = celda.getStringCellValue();
                            columna++;
                            TipoPrueba tipoPrueba = TipoPrueba.valueOf(fila.getCell(columna).getStringCellValue());
                            columna++;
                            TipoResultado tipoResultado = TipoResultado.valueOf(fila.getCell(columna).getStringCellValue());

                            PruebaJpa pruebajpa = new PruebaJpa();
                            prueba = pruebajpa.findPruebaByNombreCompeticion(nombrePrueba, Coordinador.getInstance().getSeleccionada().getId());
                            if (prueba == null) {
                                try {
                                    prueba = ControlPruebas.crearPrueba(Coordinador.getInstance().getSeleccionada(),nombrePrueba, tipoPrueba.toString(), tipoResultado.toString());
                                } catch (InputException ex) {
                                    //throw new InputException(ex.getMessage());
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
                        fila = iteradorFila.next(); // Cabecera
                        if (prueba != null) {
                            columna = PRIMERA_COLUMNA;

                            while (iteradorFila.hasNext()) {
                                fila = iteradorFila.next();
                                // Si es una prueba individual..
                                if (prueba.getTipo().equals(TipoPrueba.Individual.toString())) {
                                    try {
                                        // Leemos el dorsal
                                        Double doubleData = fila.getCell(columna).getNumericCellValue();
                                        Integer dorsal = doubleData.intValue();
                                        ParticipanteJpa participanteJpa = new ParticipanteJpa();
                                        Participante participante = participanteJpa.findByDorsalAndCompeticion(dorsal,
                                                Coordinador.getInstance().getSeleccionada().getId());
                                        // Si existe una persona con ese dorsal se crean los registros, sino se pasa al siguiente dorsal
                                        if (participante != null) {
                                            columna++;
                                            while (columna < fila.getLastCellNum()) {
                                                //Se lee el registro(marca)
                                                celda = fila.getCell(columna);
                                                if (celda != null) {
                                                    // Se comprueba el tipo de Prueba y se crea un registro con los datos correspondientes
                                                    if (prueba.getTiporesultado().equals(TipoResultado.Tiempo.toString())) {
                                                        String tiempo = celda.toString();
                                                        if (tiempo.length() > 0) {
                                                            try {
                                                                ControlRegistros.crearRegistro(
                                                                        Coordinador.getInstance().getSeleccionada(),
                                                                        dorsal,
                                                                        prueba.getNombre(),
                                                                        null,
                                                                        false,
                                                                        ControlRegistros.getSegundos(tiempo),
                                                                        ControlRegistros.getMinutos(tiempo),
                                                                        ControlRegistros.getHoras(tiempo));
                                                            } catch (InputException ex) {
                                                                throw new InputException(ex.getMessage());
                                                            }
                                                        }
                                                    } else {
                                                        String marca = celda.toString();
                                                        try {
                                                            ControlRegistros.crearRegistro(
                                                                    Coordinador.getInstance().getSeleccionada(),
                                                                    dorsal,
                                                                    prueba.getNombre(),
                                                                    null,
                                                                    false,
                                                                    Double.valueOf(marca),
                                                                    null,
                                                                    null);
                                                        } catch (InputException ex) {
                                                            throw new InputException(ex.getMessage());
                                                        }
                                                    }
                                                }
                                                columna++;
                                            }
                                        } else {
                                            //throw new InputException("Dorsal no encontrado");
                                        }
                                    } catch (NumberFormatException nfe) {
                                        //throw new InputException("Formato numérico no válido");
                                    } finally {
                                        columna = PRIMERA_COLUMNA;
                                    }
                                } else {
                                    try {
                                        // Leemos el nombre del equipo
                                        data = fila.getCell(columna).getStringCellValue();
                                        String nombreEquipo = data.toString();
                                        EquipoJpa equipoJpa = new EquipoJpa();
                                        Equipo equipo = equipoJpa.findByNombreAndCompeticion(nombreEquipo,
                                                Coordinador.getInstance().getSeleccionada().getId());
                                        // Si existe un equipo con ese nombre se crean los registros 
                                        if (equipo != null) {
                                            columna++;
                                            while (columna < fila.getLastCellNum()) {
                                                // Si la prueba es de tipo tiempo
                                                celda = fila.getCell(columna);

                                                if (celda != null) {
                                                    if (prueba.getTiporesultado().equals(TipoResultado.Tiempo.toString())) {
                                                        data = celda.getStringCellValue();
                                                        //System.out.println("Tiempo:" + data);
                                                        if (data.length() > 0) {
                                                            try {
                                                                ControlRegistros.crearRegistro(
                                                                        Coordinador.getInstance().getSeleccionada(),
                                                                        null,
                                                                        prueba.getNombre(),
                                                                        nombreEquipo,
                                                                        false,
                                                                        ControlRegistros.getSegundos(String.valueOf(data)),
                                                                        ControlRegistros.getMinutos(String.valueOf(data)),
                                                                        ControlRegistros.getHoras(String.valueOf(data)));
                                                            } catch (InputException ex) {

                                                            }
                                                        }
                                                    } else {
                                                        String marca = celda.toString();
                                                        try {
                                                            ControlRegistros.crearRegistro(
                                                                    Coordinador.getInstance().getSeleccionada(),
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
                                        } else {
                                            //throw new InputException("Equipo no encontrado");
                                        }
                                    } catch (NumberFormatException nfe) {
                                        //throw new InputException("Formato numérico no válido");
                                    } finally {
                                        columna = PRIMERA_COLUMNA;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            file.close();
        } catch (FileNotFoundException ex) {
            throw new InputException("Archivo no encontrado");
        } catch (IOException ex) {
            throw new InputException("Archivo no válido");
        } catch (InvalidFormatException ex) {
            throw new InputException("Formato de archivo no válido");
        }
        return null;
    }

}
