package main;

import controlador.ControlEquipos;
import controlador.ControlGrupos;
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
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import modelo.Grupo;
import dao.GrupoJpa;
import dao.ParticipanteJpa;
import dao.PruebaJpa;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import jxl.WorkbookSettings;
import modelo.Equipo;
import modelo.Participante;
import modelo.Prueba;

/**
 *
 * @author JuanM
 */
public class ImportarParticipantes extends SwingWorker<Void, Void> {

    String ruta;

    private static final int PRIMERA_FILA = 2;
    private static final int PRIMERA_COLUMNA = 1;
    private static final int PRIMERA_COLUMNA_PRUEBAS = PRIMERA_COLUMNA + 4;

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

        Workbook excel = null;
        WorkbookSettings ws = new WorkbookSettings();
        ws.setEncoding("CP1250");
        try {
            excel = Workbook.getWorkbook(new File(ruta), ws);

            String data;
            // Por cada hoja del archivo excel...
            for (int numHoja = 0; numHoja < excel.getNumberOfSheets(); numHoja++) {

                // Obtenemos el número de filas, columnas y la hoja.
                Sheet hoja = excel.getSheet(numHoja);
                int numFilas = hoja.getRows();
                int numColumnas = hoja.getColumns();

                // Se establecen los índices en las celdas iniciales donde se encuentran los nombres de las pruebas
                int fila = PRIMERA_FILA;
                int columna = PRIMERA_COLUMNA_PRUEBAS;

                List<String> nombresPruebas = new ArrayList();
                PruebaJpa pruebajpa = new PruebaJpa();
                Prueba prueba;
                // Obtenemos el nombre de las pruebas
                while (columna < numColumnas) {
                    // Se obtiene el nombre de la prueba y se comprueba que no es vacío
                    data = hoja.getCell(columna, fila).getContents();
                    if (data.length() > 0) {
                        // Se añade a una lista de nombres de pruebas que será utilizada posteriormente
                        nombresPruebas.add(data);
                        prueba = pruebajpa.findPruebaByNombreCompeticion(data, Coordinador.getInstance().getSeleccionada().getId());
                        if (prueba == null) {
                            try {
                                // Se crea una nueva prueba en caso de que no exista, por defecto se crea de tipo individual y con un resultado numérico.
                                // Se podrá modificar luego manualmente en el programa o al importar registros
                                ControlPruebas.crearPrueba(data, TipoPrueba.Individual.toString(), TipoResultado.Numerica.toString());
                            } catch (InputException ex) {

                            }
                        }
                    }
                    columna++;
                }

                fila++;
                columna = PRIMERA_COLUMNA;

                // Iteramos sobre los participantes
                while (fila < numFilas) {
                    try {
                        ParticipanteJpa participantejpa = new ParticipanteJpa();
                        Participante participante = new Participante();
                        // Leemos los apellidos
                        data = hoja.getCell(columna, fila).getContents();

                        // Si este campo está vacío se pasa a la siguiente fila
                        if (data.length() > 0) {
                            participante.setApellidos(data);
                            columna++;

                            // Leemos el nombre
                            data = hoja.getCell(columna, fila).getContents();
                            // Si el nombre está vacío se pone un espacio ya que en la base de datos es un campo obligatorio
                            if (data == null) {
                                data = " ";
                            }
                            participante.setNombre(data);
                            columna++;

                            // Leemos el nombre del grupo
                            GrupoJpa grupojpa = new GrupoJpa();
                            Grupo grupo;
                            data = hoja.getCell(columna, fila).getContents();
                            // Si este campo no esta vacío se busca el grupo y si no existe se crea
                            // En caso de que este vacío se pasa al siguiente participante
                            if (data != null) {
                                grupo = grupojpa.findGrupoByNombreAndCompeticion(data, Coordinador.getInstance().getSeleccionada().getId());
                                if (grupo == null) {
                                    grupo = ControlGrupos.crearGrupo(data, null);
                                }
                                participante.setGrupoId(grupo);
                                columna++;

                                // Leeemos el nombre del equipo
                                data = hoja.getCell(columna, fila).getContents();
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
                                    data = hoja.getCell(columna, fila).getContents();
                                    if (!data.equals("")) {
                                        Prueba pr = pruebajpa.findPruebaByNombreCompeticion(nombresPruebas.get(columna - PRIMERA_COLUMNA_PRUEBAS),
                                                Coordinador.getInstance().getSeleccionada().getId());
                                        participante.setPruebaasignada(pr);
                                        pruebaAsignada = true;
                                    }
                                    columna++;

                                }
                                participantejpa.create(participante);
                                // Se pone un dorsal automáticamente (el id del participante)
                                participante.setDorsal(participante.getId());
                                participantejpa.edit(participante);
                            } else {
                                //throw new InputException("Error: el campo grupo es obligatorio");
                            }
                        } else {
                            //throw new InputException("Error: el campo apellidos es obligatorio");
                        }
                    } catch (RuntimeException ex) {
                        throw new InputException(ex.getMessage());
                    } catch (Exception ex) {
                        throw new InputException(ex.getMessage());
                    } finally {
                        columna = PRIMERA_COLUMNA;
                        fila++;
                    }
                }
            }

        } catch (IOException | BiffException ex) {
            throw new InputException("Error al abrir el archivo. Formato no válido");
        } finally {
            if (excel != null) {
                excel.close();
            }
        }
        return null;
    }
}
