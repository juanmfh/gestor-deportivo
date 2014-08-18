package controlador;

import modelo.TipoResultado;
import modelo.TipoPrueba;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import main.ImportarRegistros;
import modelo.Equipo;
import modelo.Grupo;
import modelo.Participante;
import modelo.Prueba;
import modelo.Registro;
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
import modelo.Competicion;
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
import vista.VistaRegistros;

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
                            registro = RegistroJpa.crearRegistro(
                                    Coordinador.getInstance().getSeleccionada(),
                                    vista.getDorsalParticipante(),
                                    vista.getPrueba(),
                                    null,
                                    vista.getSorteo() == 1,
                                    vista.getSegundos() == null ? null : Double.parseDouble(vista.getSegundos()),
                                    vista.getMinutos() == null ? null : Integer.parseInt(vista.getMinutos()),
                                    vista.getHoras() == null ? null : Integer.parseInt(vista.getHoras()));
                        } else {
                            registro = RegistroJpa.crearRegistro(
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
                    registro = RegistroJpa.modificarRegistro(vista.getRegistroSeleccionado(),
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
                            RegistroJpa.eliminarRegistro(vista.getRegistroSeleccionado());
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
                CrearPlantillaExcel cPExcel;
                (cPExcel = new CrearPlantillaExcel(fc.getSelectedFile().getPath(), c, nombrePruebas, nombreGrupos, participantesAsignados)).execute();
            }
        } else {
            throw new InputException("Competición no seleccionada");

        }
    }

    public static class CrearPlantillaExcel extends SwingWorker<Void, Void> {

        private final String path;
        private final Competicion competicion;
        private final List<String> nombrePruebas;
        private final List<String> nombreGrupos;
        private final boolean participantesAsignados;

        private static final int PRIMERA_FILA = 2;
        private static final int PRIMERA_COLUMNA_PRUEBA = 2;
        private static final int PRIMERA_COLUMNA = 1;
        private static final int NUM_PARAMPRUEBA = 2;

        public CrearPlantillaExcel(String path, Competicion c, List<String> nombrePruebas, List<String> nombreGrupos, boolean participantesAsignados) {
            this.path = path;
            this.competicion = c;
            this.nombrePruebas = nombrePruebas;
            this.nombreGrupos = nombreGrupos;
            this.participantesAsignados = participantesAsignados;
        }

        @Override
        protected void done() {
            try {
                get();
                Coordinador.getInstance().setEstadoLabel("Plantilla creada correctamente", Color.BLUE);

            } catch (InterruptedException | ExecutionException ex) {
                Coordinador.getInstance().setEstadoLabel(ex.getMessage(), Color.RED);
            } finally {
                Coordinador.getInstance().mostrarBarraProgreso(false);
            }

        }

        @Override
        protected void process(List chunks) {
            Coordinador.getInstance().setEstadoLabel("Creando plantilla... ", Color.BLACK);
            Coordinador.getInstance().mostrarBarraProgreso(true);
        }

        @Override
        protected Void doInBackground() throws FileNotFoundException, IOException {
            // Actualiza la interfaz (muestra la barra de estado)
            publish((Void) null);

            Workbook wb = new XSSFWorkbook();
            FileOutputStream fileOut;
            fileOut = new FileOutputStream(path + "/" + competicion.getNombre() + "_registros.xlsx");

            // Se cargan las Pruebas de las que se van a generar resultados
            List<Prueba> pruebas = null;
            PruebaJpa pruebajpa = new PruebaJpa();
            if (nombrePruebas == null) {
                pruebas = pruebajpa.findPruebasByCompeticon(Coordinador.getInstance().getSeleccionada());
            } else {
                pruebas = new ArrayList();
                for (String nombrePrueba : nombrePruebas) {
                    pruebas.add(pruebajpa.findPruebaByNombreCompeticion(nombrePrueba, Coordinador.getInstance().getSeleccionada().getId()));
                }
            }

            // Estilo de las celdas
            XSSFCellStyle cs1 = (XSSFCellStyle) wb.createCellStyle();
            cs1.setFillForegroundColor(IndexedColors.GOLD.getIndex());
            cs1.setFillPattern(CellStyle.SOLID_FOREGROUND);
            cs1.setBorderBottom(BorderStyle.MEDIUM);
            cs1.setBorderLeft(BorderStyle.MEDIUM);
            cs1.setBorderRight(BorderStyle.MEDIUM);
            cs1.setBorderTop(BorderStyle.MEDIUM);
            XSSFFont f = (XSSFFont) wb.createFont();
            f.setBold(true);
            f.setColor(IndexedColors.BLACK.getIndex());
            cs1.setFont(f);

            XSSFCellStyle cs2 = (XSSFCellStyle) wb.createCellStyle();
            cs2.setFillForegroundColor(IndexedColors.TAN.getIndex());
            cs2.setFillPattern(CellStyle.SOLID_FOREGROUND);
            cs2.setBorderBottom(BorderStyle.THIN);
            cs2.setBorderLeft(BorderStyle.THIN);
            cs2.setBorderRight(BorderStyle.THIN);
            cs2.setBorderTop(BorderStyle.THIN);

            // Por cada prueba
            for (Prueba prueba : pruebas) {
                //Creamos una hoja nueva
                Sheet hoja = wb.createSheet(prueba.getNombre());

                // Fila de Cabeceras
                Row fila = hoja.createRow(PRIMERA_FILA - 1);
                Cell celda = fila.createCell(PRIMERA_COLUMNA_PRUEBA);
                celda.setCellValue("Prueba");
                celda.setCellStyle(cs1);
                celda = fila.createCell(PRIMERA_COLUMNA_PRUEBA + 1);
                celda.setCellValue("Tipo");
                celda.setCellStyle(cs1);
                celda = fila.createCell(PRIMERA_COLUMNA_PRUEBA + 2);
                celda.setCellValue("Resultado");
                celda.setCellStyle(cs1);

                //Datos de la prueba
                fila = hoja.createRow(PRIMERA_FILA);
                celda = fila.createCell(PRIMERA_COLUMNA_PRUEBA);
                celda.setCellValue(prueba.getNombre());
                celda.setCellStyle(cs2);
                celda = fila.createCell(PRIMERA_COLUMNA_PRUEBA + 1);
                celda.setCellValue(prueba.getTipo());
                celda.setCellStyle(cs2);
                celda = fila.createCell(PRIMERA_COLUMNA_PRUEBA + 2);
                celda.setCellValue(prueba.getTiporesultado());
                celda.setCellStyle(cs2);

                // Segunda fila de cabeceras
                fila = hoja.createRow(PRIMERA_FILA + 1);
                if (prueba.getTipo().equals(TipoPrueba.Individual.toString())) {
                    celda = fila.createCell(PRIMERA_COLUMNA - 1);
                    celda.setCellValue("Participante");
                    celda.setCellStyle(cs1);
                    celda = fila.createCell(PRIMERA_COLUMNA);
                    celda.setCellValue("Dorsal");
                    celda.setCellStyle(cs1);
                } else {
                    celda = fila.createCell(PRIMERA_COLUMNA);
                    celda.setCellValue("Equipo");
                    celda.setCellStyle(cs1);
                }
                celda = fila.createCell(PRIMERA_COLUMNA + 1);
                celda.setCellValue("Intento 1");
                celda.setCellStyle(cs1);
                celda = fila.createCell(PRIMERA_COLUMNA + 2);
                celda.setCellValue("Intento 2");
                celda.setCellStyle(cs1);
                celda = fila.createCell(PRIMERA_COLUMNA + 3);
                celda.setCellValue("Intento 3");
                celda.setCellStyle(cs1);

                int contadorFila = PRIMERA_FILA + 2;
                // Grupos
                ParticipanteJpa participanteJpa = new ParticipanteJpa();
                EquipoJpa equipoJpa = new EquipoJpa();
                GrupoJpa grupoJpa = new GrupoJpa();
                List<Grupo> grupos;

                if (prueba.getTipo().equals(TipoPrueba.Individual.toString())) {
                    if (nombreGrupos == null) {
                        grupos = grupoJpa.findGruposByCompeticion(competicion);
                    } else {
                        grupos = new ArrayList();
                        for (String nombreGrupo : nombreGrupos) {
                            Grupo g = grupoJpa.findGrupoByNombreAndCompeticion(nombreGrupo, competicion.getId());
                            grupos.add(g);
                        }
                    }
                    for (Grupo g : grupos) {
                        List<Participante> aux;
                        if (participantesAsignados) {
                            aux = participanteJpa.findParticipantesByGrupoPruebaAsignada(g.getId(), prueba);
                        } else {
                            aux = participanteJpa.findParticipantesByGrupo(g.getId());
                        }
                        for (Participante particip : aux) {
                            fila = hoja.createRow(contadorFila++);
                            celda = fila.createCell(PRIMERA_COLUMNA - 1);
                            celda.setCellValue(particip.getApellidos() + ", " + particip.getNombre());
                            celda.setCellStyle(cs2);
                            celda = fila.createCell(PRIMERA_COLUMNA);
                            celda.setCellValue(particip.getDorsal());
                            celda.setCellStyle(cs2);

                            for (int i = 1; i <= 3; i++) {
                                celda = fila.createCell(PRIMERA_COLUMNA + i);
                                celda.setCellStyle(cs2);
                            }
                        }
                    }
                } else if (prueba.getTipo().equals(TipoPrueba.Equipo.toString())) {
                    List<Equipo> equipos;
                    if (nombreGrupos == null) {
                        equipos = equipoJpa.findByCompeticion(competicion.getId());
                    } else {
                        equipos = new ArrayList();
                        for (String nombreGrupo : nombreGrupos) {
                            Grupo grupo = grupoJpa.findGrupoByNombreAndCompeticion(nombreGrupo, competicion.getId());
                            List<Equipo> aux = equipoJpa.findByGrupo(grupo.getId());
                            for (Equipo e : aux) {
                                equipos.add(e);
                            }
                        }
                    }
                    for (Equipo equipo : equipos) {
                        fila = hoja.createRow(contadorFila++);
                        celda = fila.createCell(PRIMERA_COLUMNA);
                        celda.setCellValue(equipo.getNombre());
                        celda.setCellStyle(cs2);

                        for (int i = 1; i <= 3; i++) {
                            celda = fila.createCell(PRIMERA_COLUMNA + i);
                            celda.setCellStyle(cs2);
                        }
                    }
                }
            }
            wb.write(fileOut);
            fileOut.close();
            return null;
        }

    }

}
