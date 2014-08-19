package controlador;

import modelo.entities.TipoResultado;
import modelo.entities.TipoPrueba;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import modelo.logicaNegocio.RegistroService.ImportarRegistros;
import modelo.entities.Equipo;
import modelo.entities.Grupo;
import modelo.entities.Participante;
import modelo.entities.Prueba;
import modelo.entities.Registro;
import modelo.dao.EquipoJpa;
import modelo.dao.GrupoJpa;
import modelo.dao.ParticipanteJpa;
import modelo.dao.PruebaJpa;
import modelo.dao.RegistroJpa;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import modelo.entities.Competicion;
import modelo.logicaNegocio.RegistroService;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import vista.DialogoImprimirResultados;
import vista.interfaces.VistaRegistros;

/**
 *
 * @author JuanM
 */
public class ControlRegistros implements ActionListener {

    VistaRegistros vista;

    /**
     * Constructor que asocia la vista al controlador
     *
     * @param vista Vista del controlador (Interfaz)
     */
    public ControlRegistros(VistaRegistros vista) {
        this.vista = vista;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();
        switch (command) {
            case VistaRegistros.CREARREGISTRO:
                try {
                    PruebaJpa pruebajpa = new PruebaJpa();
                    Prueba prueba = pruebajpa.findPruebaByNombreCompeticion(vista.getPrueba(), Coordinador.getInstance().getSeleccionada().getId());
                    Registro registro;
                    if (prueba != null) {
                        if (prueba.getTipo().equals(TipoPrueba.Individual.toString())) {
                            registro = RegistroService.crearRegistro(
                                    Coordinador.getInstance().getSeleccionada(),
                                    vista.getDorsalParticipante(),
                                    vista.getPrueba(),
                                    null,
                                    vista.getSorteo() == 1,
                                    vista.getSegundos() == null ? null : Double.parseDouble(vista.getSegundos()),
                                    vista.getMinutos() == null ? null : Integer.parseInt(vista.getMinutos()),
                                    vista.getHoras() == null ? null : Integer.parseInt(vista.getHoras()));
                        } else {
                            registro = RegistroService.crearRegistro(
                                    Coordinador.getInstance().getSeleccionada(),
                                    null,
                                    vista.getPrueba(),
                                    vista.getEquipo(),
                                    vista.getSorteo() == 1,
                                    vista.getSegundos() == null ? null : Double.parseDouble(vista.getSegundos()),
                                    vista.getMinutos() == null ? null : Integer.parseInt(vista.getMinutos()),
                                    vista.getHoras() == null ? null : Integer.parseInt(vista.getHoras()));
                        }
                        //Actualizamos la vista
                        añadirRegistroAVista(registro);
                        Coordinador.getInstance().setEstadoLabel("Registro creado correctamente", Color.BLUE);
                    } else {
                        Coordinador.getInstance().setEstadoLabel("Prueba no válida", Color.RED);
                    }
                } catch (NumberFormatException e) {
                    Coordinador.getInstance().setEstadoLabel("Error en el formato numérico del registro", Color.RED);
                } catch (InputException ex) {
                    Coordinador.getInstance().setEstadoLabel(ex.getMessage(), Color.RED);
                }

                break;
            case VistaRegistros.MODIFICARREGISTRO:
                Registro registro = null;
                try {
                    registro = RegistroService.modificarRegistro(vista.getRegistroSeleccionado(),
                            Double.valueOf(vista.getSegundos()),
                            Integer.parseInt(vista.getMinutos()),
                            Integer.parseInt(vista.getHoras()));
                } catch (InputException ex) {
                    Coordinador.getInstance().setEstadoLabel(ex.getMessage(), Color.RED);
                }
                Coordinador.getInstance().setEstadoLabel(
                        "Registro modificado correctamente", Color.BLUE);
                vista.eliminarRegistroDeTabla();
                // Actualizamos la vista
                SimpleDateFormat dt = new SimpleDateFormat("HH:mm:ss.S");
                if (registro.getEquipoId() != null) {
                    vista.añadirRegistroATabla(new Object[]{registro.getId(), registro.getEquipoId().getId(),
                        registro.getEquipoId().getNombre() + " (E)",
                        registro.getPruebaId().getNombre()
                        + (registro.getSorteo() == 1
                        ? " (Sorteo)" : ""),
                        registro.getPruebaId().getTiporesultado().equals("Tiempo")
                        ? dt.format(registro.getTiempo())
                        : registro.getNum(), registro.getNumIntento()});
                } else {
                    vista.añadirRegistroATabla(new Object[]{registro.getId(),
                        registro.getParticipanteId().getDorsal(),
                        registro.getParticipanteId().getApellidos()
                        + ", " + registro.getParticipanteId().getNombre(),
                        registro.getPruebaId().getNombre()
                        + (registro.getSorteo() == 1 ? " (Sorteo)" : ""),
                        registro.getPruebaId().getTiporesultado().equals("Tiempo")
                        ? dt.format(registro.getTiempo())
                        : registro.getNum(),
                        registro.getNumIntento()});
                }
                break;
            case VistaRegistros.ELIMINARREGISTRO:
                if (vista.getRegistroSeleccionado() != -1) {
                    int confirmDialogRegistro = JOptionPane.showConfirmDialog(null,
                            "¿Está seguro de que desea eliminar el registro seleccionado?",
                            "Aviso",
                            JOptionPane.YES_NO_OPTION);
                    if (confirmDialogRegistro == JOptionPane.YES_OPTION) {
                        try {
                            RegistroService.eliminarRegistro(vista.getRegistroSeleccionado());
                        } catch (InputException ex) {
                            Coordinador.getInstance().setEstadoLabel(
                                    ex.getMessage(), Color.RED);
                        }
                        vista.eliminarRegistroDeTabla();
                        Coordinador.getInstance().setEstadoLabel(
                                "Registro eliminado correctamente", Color.BLUE);
                    }
                }
                break;
            case VistaRegistros.LIMPIAR:
                vista.limpiarFormulario();
                break;
            case VistaRegistros.FILTRAR:
                filtrarResultados();
                break;
            case VistaRegistros.IMPORTARREGISTROS:
                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fc.setDialogTitle("Abrir");
                int res = fc.showOpenDialog(null);
                if (res == JFileChooser.APPROVE_OPTION) {
                    Coordinador.getInstance().setEstadoLabel("Importando registros ...", Color.BLACK);
                    //Coordinador.getInstance().mostrarBarraProgreso(true);
                    ImportarRegistros imReg;
                    (imReg = new ImportarRegistros(fc.getSelectedFile().getPath())).execute();
                }
                break;
            case VistaRegistros.CREARPLANTILLA:

                DialogoImprimirResultados dialog
                        = new DialogoImprimirResultados(new Frame(), true, "Excel");
                dialog.setMinimumSize(new Dimension(420, 320));
                Dimension dimension
                        = Toolkit.getDefaultToolkit().getScreenSize();
                dialog.setLocation(dimension.width / 2
                        - dialog.getSize().width / 2, dimension.height / 2 - dialog.getSize().height / 2);
                ActionListener controladorDialog = new ControlCrearFicheros(dialog);
                dialog.controlador(controladorDialog);
                PruebaJpa pruebajpa = new PruebaJpa();
                GrupoJpa grupojpa = new GrupoJpa();
                List<String> pruebas = pruebajpa.findNombresPruebasByCompeticon(Coordinador.getInstance().getSeleccionada());
                List<String> grupos = grupojpa.findNombresGruposByCompeticion(Coordinador.getInstance().getSeleccionada());
                dialog.asignarListaPruebas(pruebas);
                dialog.asignarListaGrupos(grupos);
                dialog.setVisible(true);
                break;
        }
    }

    

    

    private void añadirRegistroAVista(Registro r) {
        String formatDate = "HH:mm:ss.S";
        SimpleDateFormat dt = new SimpleDateFormat(formatDate);
        // Si es un equipo 
        if (r.getPruebaId().getTipo().equals(TipoPrueba.Equipo.toString())) {
            vista.añadirRegistroATabla(new Object[]{r.getId(), r.getEquipoId().getId(),
                r.getEquipoId().getNombre(),
                r.getPruebaId().getNombre()
                + (r.getSorteo() == 1 ? " (Sorteo)" : ""),
                r.getPruebaId().getTiporesultado().equals(TipoResultado.Tiempo.toString())
                ? dt.format(r.getTiempo()) : r.getNum(),
                r.getNumIntento()});
            // Si es un participante individual
        } else {
            vista.añadirRegistroATabla(new Object[]{r.getId(),
                r.getParticipanteId().getDorsal(),
                r.getParticipanteId().getApellidos()
                + ", " + r.getParticipanteId().getNombre(),
                r.getPruebaId().getNombre()
                + (r.getSorteo() == 1 ? " (Sorteo)" : ""),
                r.getPruebaId().getTiporesultado().equals(TipoResultado.Tiempo.toString())
                ? dt.format(r.getTiempo())
                : r.getNum(),
                r.getNumIntento()});

        }
    }

    

    /**
     * Filtra los resultados de la base de datos en función de varios factores:
     * participantes, grupos y resultados individuales o por equipos.
     *
     */
    public void filtrarResultados() {

        RegistroJpa registrojpa = new RegistroJpa();
        PruebaJpa pruebajpa = new PruebaJpa();
        GrupoJpa grupojpa = new GrupoJpa();
        List<Registro> registros = null;

        // Obtenemos los diferentes filtros de la vista
        String grupo = vista.getFiltroGrupoComboBox().getSelectedItem().toString();
        String prueba = vista.getFiltroPruebasComboBox().getSelectedItem().toString();
        //String participante = vista.getFiltroParticipante();

        // Obtenemos la competición seleccionada
        Integer competicionSeleccionada = Coordinador.getInstance().getSeleccionada().getId();

        Prueba p = pruebajpa.findPruebaByNombreCompeticion(prueba,
                Coordinador.getInstance().getControladorPrincipal().getSeleccionada().getId());
        if (p != null) {
            // Si se ha seleccionado todos los grupos
            if (grupo.equals("Todos")) {
                if (p.getTipo().equals(TipoPrueba.Equipo.toString())) {
                    if (vista.mejoresMarcasCheckBoxIsSelected()) {
                        registros = new ArrayList();
                        if (p.getTiporesultado().equals(TipoResultado.Tiempo.toString())) {
                            List<Equipo> equipos = registrojpa.findEquiposConRegistrosTiempo(competicionSeleccionada, p.getId());
                            for (Equipo e : equipos) {
                                List<Registro> registrosP = registrojpa.findRegistroByEquipoPruebaCompeticionOrderByTiempo(competicionSeleccionada, p.getId(), e.getId());
                                if (registrosP != null) {
                                    registros.add(registrosP.get(0));
                                }
                            }
                        } else {
                            List<Equipo> equipos = registrojpa.findEquiposConRegistrosNum(competicionSeleccionada, p.getId());
                            for (Equipo e : equipos) {
                                List<Registro> registrosP = registrojpa.findRegistroByEquipoPruebaCompeticionOrderByNum(competicionSeleccionada, p.getId(), e.getId());
                                if (registrosP != null) {
                                    registros.add(registrosP.get(0));
                                }
                            }
                        }
                    } else {
                        registros = registrojpa.findByCompeticionPruebaEquipo(competicionSeleccionada, p.getId());
                    }
                } else if (p.getTipo().equals(TipoPrueba.Individual.toString())) {
                    if (vista.mejoresMarcasCheckBoxIsSelected()) {
                        registros = new ArrayList();
                        if (p.getTiporesultado().equals(TipoResultado.Tiempo.toString())) {
                            List<Participante> participantes = registrojpa.findParticipantesConRegistrosTiempo(competicionSeleccionada, p.getId());
                            for (Participante e : participantes) {
                                List<Registro> registrosP = registrojpa.findRegistroByParticipantePruebaCompeticionOrderByTiempo(competicionSeleccionada, p.getId(), e.getId());
                                if (registrosP != null) {
                                    registros.add(registrosP.get(0));
                                }
                            }
                        } else {
                            List<Participante> participantes = registrojpa.findParticipantesConRegistrosNum(competicionSeleccionada, p.getId());
                            for (Participante e : participantes) {
                                List<Registro> registrosP = registrojpa.findRegistroByParticipantePruebaCompeticionOrderByNum(competicionSeleccionada, p.getId(), e.getId());
                                if (registrosP != null) {
                                    registros.add(registrosP.get(0));
                                }
                            }
                        }
                    } else {
                        registros = registrojpa.findByCompeticionPruebaIndividual(competicionSeleccionada, p.getId());
                    }
                }
            } else {
                Grupo g = grupojpa.findGrupoByNombreAndCompeticion(grupo,
                        Coordinador.getInstance().getSeleccionada().getId());
                if (g != null) {
                    if (p.getTipo().equals(TipoPrueba.Equipo.toString())) {
                        if (vista.mejoresMarcasCheckBoxIsSelected()) {
                            List<String> grupos = new ArrayList();
                            grupos.add(g.getNombre());
                            registros = new ArrayList();
                            if (p.getTiporesultado().equals(TipoResultado.Tiempo.toString())) {
                                List<Equipo> equipos = registrojpa.findEquiposConRegistrosTiempoByGrupo(competicionSeleccionada, p.getId(), grupos);
                                for (Equipo e : equipos) {
                                    List<Registro> registrosP = registrojpa.findRegistroByEquipoPruebaCompeticionOrderByTiempo(competicionSeleccionada, p.getId(), e.getId());
                                    if (registrosP != null) {
                                        registros.add(registrosP.get(0));
                                    }
                                }
                            } else {
                                List<Equipo> equipos = registrojpa.findEquiposConRegistrosNumByGrupo(competicionSeleccionada, p.getId(), grupos);
                                for (Equipo e : equipos) {
                                    List<Registro> registrosP = registrojpa.findRegistroByEquipoPruebaCompeticionOrderByNum(competicionSeleccionada, p.getId(), e.getId());
                                    if (registrosP != null) {
                                        registros.add(registrosP.get(0));
                                    }
                                }
                            }

                        } else {
                            registros = registrojpa.findByCompeticionGrupoPruebaEquipo(competicionSeleccionada, g.getId(), p.getId());
                        }
                    } else if (p.getTipo().equals(TipoPrueba.Individual.toString())) {
                        if (vista.mejoresMarcasCheckBoxIsSelected()) {
                            List<String> grupos = new ArrayList();
                            grupos.add(g.getNombre());
                            registros = new ArrayList();
                            if (p.getTiporesultado().equals(TipoResultado.Tiempo.toString())) {
                                List<Participante> participantes = registrojpa.findParticipantesConRegistrosTiempoByGrupos(competicionSeleccionada, p.getId(), grupos);
                                for (Participante e : participantes) {
                                    List<Registro> registrosP = registrojpa.findRegistroByParticipantePruebaCompeticionOrderByTiempo(competicionSeleccionada, p.getId(), e.getId());
                                    if (registrosP != null) {
                                        registros.add(registrosP.get(0));
                                    }
                                }
                            } else {
                                List<Participante> participantes = registrojpa.findParticipantesConRegistrosNumByGrupo(competicionSeleccionada, p.getId(), grupos);
                                for (Participante e : participantes) {
                                    List<Registro> registrosP = registrojpa.findRegistroByParticipantePruebaCompeticionOrderByNum(competicionSeleccionada, p.getId(), e.getId());
                                    if (registrosP != null) {
                                        registros.add(registrosP.get(0));
                                    }
                                }
                            }
                        } else {
                            registros = registrojpa.findByCompeticionGrupoPruebaIndividual(competicionSeleccionada, g.getId(), p.getId());
                        }
                    }
                }
            }
        }

        // Actualizamos la vista
        Coordinador.getInstance().getControladorPrincipal().cargarTablaRegistros(registros);
    }

    public static void crearPlantillaFileChooser(List<String> nombrePruebas, List<String> nombreGrupos, Boolean participantesAsignados) throws InputException {
        Competicion c = Coordinador.getInstance().getControladorPrincipal().getSeleccionada();
        if (c != null) {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setDialogTitle("Guardar en");
            int res = fc.showSaveDialog(null);
            if (res == JFileChooser.APPROVE_OPTION) {
                RegistroService.CrearPlantillaExcel cPExcel;
                (cPExcel = new RegistroService.CrearPlantillaExcel(fc.getSelectedFile().getPath(), c, nombrePruebas, nombreGrupos, participantesAsignados)).execute();
            }
        } else {
            throw new InputException("Competición no seleccionada");

        }
    }

    

}
