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
import java.util.logging.Level;
import java.util.logging.Logger;
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
        }finally{
            Coordinador.getInstance().getControladorPrincipal().cargarTablaParticipantes();
            Coordinador.getInstance().mostrarBarraProgreso(false);
        }

    }

    @Override
    protected void process(List chunks) {
        Coordinador.getInstance().mostrarBarraProgreso(true);
    }

    @Override
    protected Void doInBackground() throws InputException {

        publish((Void) null);
        Workbook excel = null;
        WorkbookSettings ws = new WorkbookSettings();
        ws.setEncoding("CP1250");
        try {
            int dorsal = 1;
            excel = Workbook.getWorkbook(new File(ruta), ws);

            String data;
            for (int numHoja = 0; numHoja < excel.getNumberOfSheets(); numHoja++) {

                Sheet hoja = excel.getSheet(numHoja);
                int numFilas = hoja.getRows();
                int numColumnas = hoja.getColumns();

                int fila = 2;
                int columna = 5;

                List<String> nombresPruebas = new ArrayList();
                PruebaJpa pruebajpa = new PruebaJpa();
                Prueba prueba;
                // Obtenemos el nombre de las pruebas
                while (columna < numColumnas) {
                    data = hoja.getCell(columna, fila).getContents();

                    if (data.length() > 0) {
                        nombresPruebas.add(data);
                        prueba = pruebajpa.findPruebaByNombreCompeticion(data, Coordinador.getInstance().getSeleccionada().getId());
                        // Se crea una nueva prueba, por defecto se crea de tipo individual y con un resultado numérico.
                        // Esto se podrá modificar luego manualmente en el programa
                        if (prueba == null) {
                            try {
                                ControlPruebas.crearPrueba(data, TipoPrueba.Individual.toString(), TipoResultado.Numerica.toString());
                            } catch (InputException ex) {

                            }
                        }
                    }
                    columna++;
                }

                fila++;
                columna = 1;

                // Iteramos sobre los participantes
                while (fila < numFilas) {
                    try {
                        ParticipanteJpa participantejpa = new ParticipanteJpa();
                        Participante participante = new Participante();
                        // Leemos los apellidos
                        data = hoja.getCell(columna, fila).getContents();
                        //System.out.println("APELLIDOS: " + data);
                        participante.setApellidos(data);
                        columna++;
                        if (data.length() > 0) {
                            // Leemos el nombre
                            data = hoja.getCell(columna, fila).getContents();
                            //System.out.println("NOMBRE: " + data);
                            // QUITAR ESTO
                            if (data == null) {
                                data = " ";
                            }
                            participante.setNombre(data);
                            columna++;
                            // Leemos el nombre del grupo
                            GrupoJpa grupojpa = new GrupoJpa();
                            Grupo grupo;
                            data = hoja.getCell(columna, fila).getContents();
                            //System.out.println("GRUPO: " + data);
                            grupo = grupojpa.findGrupoByNombreAndCompeticion(data, Coordinador.getInstance().getSeleccionada().getId());
                            if (grupo == null) {
                                grupo = ControlGrupos.crearGrupo(data, null);
                            }
                            participante.setGrupoId(grupo);
                            columna++;
                            // Leeemos el nombre del equipo
                            EquipoJpa equipojpa = new EquipoJpa();
                            Equipo equipo;
                            data = hoja.getCell(columna, fila).getContents();
                            //System.out.println("EQUIPO: " + data);
                            if (data.length() > 0) {
                                equipo = equipojpa.findByNombreAndCompeticion(data, Coordinador.getInstance().getSeleccionada().getId());
                                if (equipo == null) {
                                    equipo = ControlEquipos.crearEquipo(data, grupo.getNombre());
                                }
                                participante.setEquipoId(equipo);
                            }
                            columna++;
                            // Leemos la prueba asignada al participante
                            boolean pruebaAsignada = false;
                            while (!pruebaAsignada && columna <= nombresPruebas.size() + 5) {
                                data = hoja.getCell(columna, fila).getContents();
                                //System.out.println("PRUEBAASIGNADA: [" + data + "]");
                                if (!data.equals("")) {
                                    Prueba pr = pruebajpa.findPruebaByNombreCompeticion(nombresPruebas.get(columna - 5),
                                            Coordinador.getInstance().getSeleccionada().getId());
                                    //System.out.println("PRUEBA: " + pr.getNombre());
                                    participante.setPruebaasignada(pr);
                                    pruebaAsignada = true;
                                }
                                columna++;

                            }
                            //participante.setDorsal(dorsal++);
                            participantejpa.create(participante);
                            participante.setDorsal(participante.getId());
                            participantejpa.edit(participante);
                        }
                    } catch (RuntimeException ex) {
                        throw new InputException(ex.getMessage());
                    } catch (Exception ex) {
                        throw new InputException(ex.getMessage());
                    } finally {
                        columna = 1;
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
