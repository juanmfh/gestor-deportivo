package main;

import controlador.ControlEquipos;
import controlador.ControlGrupos;
import controlador.ControlParticipantes;
import controlador.ControlPruebas;
import controlador.Coordinador;
import controlador.InputException;
import controlador.TipoPrueba;
import controlador.TipoResultado;
import dao.EquipoJpa;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import javax.swing.SwingWorker;
import modelo.Grupo;
import dao.GrupoJpa;
import dao.ParticipanteJpa;
import dao.PruebaJpa;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.Equipo;
import modelo.Participante;
import modelo.Prueba;
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
public class ImportarParticipantes extends SwingWorker<Void, Void> {

    String ruta;

    private static final int PRIMERA_FILA = 2;
    private static final int PRIMERA_COLUMNA = 1;
    private static final int PRIMERA_COLUMNA_PRUEBAS = PRIMERA_COLUMNA + 5;

    public ImportarParticipantes(String rutaFichero) {
        ruta = rutaFichero;
    }

    @Override
    protected void done() {
        try {
            get();
            Coordinador.getInstance().setEstadoLabel("Participantes importados", Color.BLUE);

        } catch (InterruptedException | ExecutionException ex) {
            Coordinador.getInstance().setEstadoLabel(ex.getMessage(), Color.RED);
        } finally {
            Coordinador.getInstance().getControladorPrincipal().cargarTablaParticipantes();
            Coordinador.getInstance().mostrarBarraProgreso(false);
            Coordinador.getInstance().getControladorPrincipal().cargarGruposEnParticipantes();
            Coordinador.getInstance().getControladorPrincipal().cargarPruebasEnParticipantes();
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
            // Por cada hoja del archivo excel...
            for (int numHoja = 0; numHoja < workbook.getNumberOfSheets(); numHoja++) {

                // Obtenemos el número de filas, columnas y la hoja.
                Sheet hoja = workbook.getSheetAt(numHoja);
                Iterator<Row> iteradorFila = hoja.iterator();

                Row fila = iteradorFila.next(); //Cabecera, no contiene datos
                fila = iteradorFila.next();     //Fila donde se encuentra los nombres de las pruebas

                Iterator<Cell> iteradorCelda = fila.cellIterator();
                List<String> nombresPruebas = new ArrayList();
                PruebaJpa pruebajpa = new PruebaJpa();
                // Se comprueba si hay pruebas o no
                if (fila.getLastCellNum() < PRIMERA_COLUMNA_PRUEBAS) {
                    // No hay pruebas que añadir
                } else {
                    int columna = PRIMERA_COLUMNA_PRUEBAS;

                    Prueba prueba;
                    // Obtenemos el nombre de las pruebas
                    while (columna < fila.getLastCellNum()) {
                        // Se obtiene el nombre de la prueba y se comprueba que no es vacío
                        Cell celda = fila.getCell(columna);
                        //System.out.println(celda.getStringCellValue());
                        if (celda.getStringCellValue().length() > 0) {
                            // Se añade a una lista de nombres de pruebas que será utilizada posteriormente
                            nombresPruebas.add(celda.getStringCellValue());
                            prueba = pruebajpa.findPruebaByNombreCompeticion(celda.getStringCellValue(), Coordinador.getInstance().getSeleccionada().getId());
                            if (prueba == null) {
                                try {
                                    // Se crea una nueva prueba en caso de que no exista, por defecto se crea de tipo individual y con un resultado numérico.
                                    // Se podrá modificar luego manualmente en el programa o al importar registros
                                    ControlPruebas.crearPrueba(Coordinador.getInstance().getSeleccionada(),celda.getStringCellValue(), TipoPrueba.Individual.toString(), TipoResultado.Numerica.toString());
                                } catch (InputException ex) {

                                }
                            }
                        }
                        columna++;
                    }
                }
                while (iteradorFila.hasNext()) {
                    fila = iteradorFila.next();
                    int columna = PRIMERA_COLUMNA;
                    ParticipanteJpa participantejpa = new ParticipanteJpa();
                    Participante participante = new Participante();
                    //Leemos el dorsal
                    Cell celda = fila.getCell(columna);
                    if (celda != null) {
                        double dorsal = celda.getNumericCellValue();
                        if (ControlParticipantes.dorsalLibre((int) dorsal, Coordinador.getInstance().getSeleccionada())) {
                            participante.setDorsal((int) dorsal);
                            columna++;
                            // Leemos los apellidos
                            celda = fila.getCell(columna);
                            if (celda != null) {
                                data = celda.getStringCellValue();

                                // Si este campo está vacío se pasa a la siguiente fila
                                if (data.length() > 0) {
                                    participante.setApellidos(data);
                                    columna++;

                                    // Leemos el nombre
                                    data = fila.getCell(columna).getStringCellValue();
                                    // Si el nombre está vacío se pone un espacio ya que en la base de datos es un campo obligatorio
                                    if (data == null) {
                                        data = " ";
                                    }
                                    participante.setNombre(data);
                                    columna++;

                                    // Leemos el nombre del grupo
                                    GrupoJpa grupojpa = new GrupoJpa();
                                    Grupo grupo;
                                    data = fila.getCell(columna).getStringCellValue();
                        // Si este campo no esta vacío se busca el grupo y si no existe se crea
                                    // En caso de que este vacío se pasa al siguiente participante
                                    if (data != null) {
                                        grupo = grupojpa.findGrupoByNombreAndCompeticion(data, Coordinador.getInstance().getSeleccionada().getId());
                                        if (grupo == null) {
                                            grupo = ControlGrupos.crearGrupo(Coordinador.getInstance().getSeleccionada(),data, null);
                                        }
                                        participante.setGrupoId(grupo);
                                        columna++;

                                        // Leeemos el nombre del equipo
                                        data = fila.getCell(columna).getStringCellValue();
                                        if (data != null) {
                                            EquipoJpa equipojpa = new EquipoJpa();
                                            Equipo equipo;
                                            equipo = equipojpa.findByNombreAndCompeticion(data, Coordinador.getInstance().getSeleccionada().getId());
                                            if (equipo == null) {
                                                equipo = ControlEquipos.crearEquipo(data, grupo.getNombre());
                                            }
                                            participante.setEquipoId(equipo);
                                        }
                                        columna++;

                                        // Leemos la prueba asignada al participante
                                        boolean pruebaAsignada = false;
                                        while (!pruebaAsignada && columna <= nombresPruebas.size() + PRIMERA_COLUMNA_PRUEBAS) {
                                            data = fila.getCell(columna).getStringCellValue();
                                            if (!data.equals("")) {
                                                Prueba pr = pruebajpa.findPruebaByNombreCompeticion(nombresPruebas.get(columna - PRIMERA_COLUMNA_PRUEBAS),
                                                        Coordinador.getInstance().getSeleccionada().getId());
                                                participante.setPruebaasignada(pr);
                                                pruebaAsignada = true;
                                            }
                                            columna++;

                                        }

                                        participantejpa.create(participante);
                                        /*// Se pone un dorsal automáticamente (el id del participante)
                                         participante.setDorsal(participante.getId());
                                         participantejpa.edit(participante);*/
                                    }
                                }
                            }
                        }
                    }
                }
            }
            file.close();
        } catch (IOException ex) {
            throw new InputException("Archivo no válido");
        } catch (InvalidFormatException ex) {
            throw new InputException("Formato de archivo no válido");
        } catch (Exception ex) {
            throw new InputException(ex.getMessage());
        }

        return null;
    }
}
