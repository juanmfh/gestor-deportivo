package controlador;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import main.ImportarRegistros;
import modelo.Equipo;
import modelo.Grupo;
import modelo.Inscripcion;
import modelo.Participante;
import modelo.Prueba;
import modelo.Registro;
import dao.EquipoJpa;
import dao.GrupoJpa;
import dao.InscripcionJpa;
import dao.ParticipanteJpa;
import dao.PruebaJpa;
import dao.RegistroJpa;
import dao.exceptions.NonexistentEntityException;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.util.ArrayList;
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
                            registro = crearRegistro(
                                    vista.getDorsalParticipante(),
                                    vista.getPrueba(),
                                    null,
                                    vista.getSorteo() == 1,
                                    vista.getSegundos() == null ? null : Double.parseDouble(vista.getSegundos()),
                                    vista.getMinutos() == null ? null : Integer.parseInt(vista.getMinutos()),
                                    vista.getHoras() == null ? null : Integer.parseInt(vista.getHoras()));
                        } else {
                            registro = crearRegistro(
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
                    registro = modificarRegistro(vista.getRegistroSeleccionado());
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
                            eliminarRegistro(vista.getRegistroSeleccionado());
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
                ActionListener controladorDialog = new ControlImprimirResultados(dialog);
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

    /**
     * Crea un registro nuevo
     *
     * @param dorsal dorsal del participante, si es un equipo null
     * @param nombrePrueba nombre de la prueba
     * @param nombreEquipo Nombre del equipo si el registro es de un equipo,
     * null en otro caso
     * @param sorteo Indica si la prueba ha sido seleccionada por sorteo
     * @param segundos Marca en segundos o la distancia o número si la prueba no
     * es de tiempo
     * @param minutos Marca en minutos, si la prueba no es de tiempo null
     * @param horas Marca en horas, si la prueba no es de tiempo null
     * @return Registro si ha sido creado correctamente
     * @throws controlador.InputException
     */
    public static Registro crearRegistro(Integer dorsal,
            String nombrePrueba, String nombreEquipo, Boolean sorteo,
            Double segundos, Integer minutos, Integer horas) throws InputException {

        Registro registro = null;
        RegistroJpa registrojpa = new RegistroJpa();
        InscripcionJpa inscripcionjpa = new InscripcionJpa();
        GrupoJpa grupojpa = new GrupoJpa();
        ParticipanteJpa participantejpa = new ParticipanteJpa();
        PruebaJpa pruebajpa = new PruebaJpa();

        // Obtenemos la prueba a partir del nombre
        Prueba prueba = pruebajpa.findPruebaByNombreCompeticion(nombrePrueba,
                Coordinador.getInstance().getSeleccionada().getId());

        // Comprobamos que los parámetros son correctos
        if (prueba != null) {
            if (!(dorsal == null && prueba.getTipo().equals(TipoPrueba.Individual.toString()))) {
                if (!(nombreEquipo == null && prueba.getTipo().equals(TipoPrueba.Equipo.toString()))) {

                    Participante participante;
                    Grupo g;
                    registro = new Registro();
                    Inscripcion i;
                    // Si se ha seleccionado un participante individual
                    if (prueba.getTipo().equals(TipoPrueba.Individual.toString())) {
                        // Obtenemos el participante
                        participante = participantejpa.findByDorsalAndCompeticion(dorsal, Coordinador.getInstance().getSeleccionada().getId());
                        g = grupojpa.findByParticipanteCompeticion(Coordinador.getInstance().getSeleccionada().getId(), participante.getId());
                        registro.setParticipanteId(participante);
                        i = inscripcionjpa.findInscripcionByCompeticionByGrupo(
                                Coordinador.getInstance().getSeleccionada().getId(), g.getId());
                        registro.setNumIntento(registrojpa.findMaxNumIntentoParticipante(i.getId(),
                                prueba.getId(), participante.getId()) + 1);
                    } else {
                        // Obtenemosel equipo
                        EquipoJpa equipojpa = new EquipoJpa();
                        Equipo equipo = equipojpa.findByNombreAndCompeticion(nombreEquipo,
                                Coordinador.getInstance().getSeleccionada().getId());
                        g = grupojpa.findByEquipoCompeticion(Coordinador.getInstance().getSeleccionada().getId(), equipo.getId());
                        registro.setEquipoId(equipo);
                        i = inscripcionjpa.findInscripcionByCompeticionByGrupo(
                                Coordinador.getInstance().getSeleccionada().getId(), g.getId());
                        registro.setNumIntento(registrojpa.findMaxNumIntentoEquipo(i.getId(),
                                prueba.getId(), equipo.getId()) + 1);
                    }

                    // Establecemos los datos del registro comunes
                    registro.setInscripcionId(i);
                    registro.setPruebaId(prueba);
                    registro.setSorteo(sorteo ? 1 : 0);

                    // Comprueba que la prueba no es de tipo tiempo
                    if (prueba.getTiporesultado().equals(TipoResultado.Distancia.toString())
                            || prueba.getTiporesultado().equals(TipoResultado.Numerica.toString())) {
                        if (segundos == null) {
                            throw new InputException("Formato del registro no válido");
                        }
                        registro.setNum(segundos);
                        // Si es de tipo Tiempo formateamos la hora, minutos y segundos    
                    } else if (prueba.getTiporesultado().equals(TipoResultado.Tiempo.toString())) {
                        // Obtenemos un objeto Date a partir de los parámetros
                        Date date = getTiempo(segundos, minutos, horas);
                        if (date == null) {
                            throw new InputException("Formato de tiempo no válido");
                        }
                        registro.setTiempo(date);
                    }
                    // Creamos el registro en la base de datos
                    registrojpa.create(registro);
                } else {
                    throw new InputException("Equipo no válido");
                }
            } else {
                throw new InputException("Participante no válido");
            }
        } else {
            throw new InputException("Prueba no válida");
        }
        return registro;
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

    public static Integer getHoras(String tiempo) {
        try {
            String aux1, aux2;
            aux1 = tiempo.substring(0, tiempo.indexOf(":"));
            aux2 = tiempo.substring(tiempo.indexOf(":") + 1);
            if (aux2.contains(":")) {
                Integer res = Integer.parseInt(aux1);
                if (res >= 0) {
                    return res;
                } else {
                    return null;
                }
            } else {
                return 0;
            }
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            return null;
        }
    }

    public static Integer getMinutos(String tiempo) {
        try {
            if (tiempo.contains(":")) {
                String aux1, aux2;
                aux1 = tiempo.substring(0, tiempo.indexOf(":"));
                aux2 = tiempo.substring(tiempo.indexOf(":") + 1);
                Integer res = null;
                if (aux2.contains(":")) {
                    aux1 = aux2.substring(0, aux2.indexOf(":"));
                    res = Integer.parseInt(aux1);
                } else {
                    res = Integer.parseInt(aux1);

                }
                if (res >= 0 && res < 60) {
                    return res;
                } else {
                    return null;
                }
            } else if (Double.parseDouble(tiempo) > 0) {
                return 0;
            } else {
                return null;
            }
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            return null;
        }
    }

    public static Double getSegundos(String tiempo) {
        try {
            if (tiempo.contains(":")) {
                String aux1, aux2;
                aux2 = tiempo.substring(tiempo.indexOf(":") + 1);
                Double res = null;
                if (aux2.contains(":")) {
                    aux1 = aux2.substring(aux2.indexOf(":") + 1);
                    res = Double.parseDouble(aux1);
                } else {
                    res = Double.parseDouble(aux2);
                }
                if (res >= 0 && res < 60) {
                    return res;
                } else {
                    return null;
                }
            } else if (Double.parseDouble(tiempo) > 0) {
                return Double.parseDouble(tiempo);
            } else {
                return null;
            }
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            return null;
        }
    }

    public static Date getTiempo(Double segundos, Integer minutos, Integer horas) {
        Date res = null;
        try {
            String formatDate = "HH:mm:ss.S";
            String horasSt;
            if (horas == null) {
                horasSt = "00";
            } else {
                horasSt = Integer.toString(horas);
                if (horasSt.length() == 0) {
                    horasSt = "00";
                } else if (horasSt.length() == 1) {
                    horasSt = "0" + horasSt;
                }
            }
            String minutosSt;
            if (minutos == null) {
                minutosSt = "00";
            } else {
                minutosSt = Integer.toString(minutos);
                if (minutosSt.length() == 0) {
                    minutosSt = "00";
                } else if (minutosSt.length() == 1) {
                    minutosSt = "0" + minutosSt;
                }
            }
            String segundosSt;
            if (segundos == null) {
                segundosSt = "00.0";
            } else {
                segundosSt = Double.toString(segundos);
                if (segundosSt.length() == 0) {
                    segundosSt = "00.0";
                } else {
                    segundosSt = Double.toString(Double.parseDouble(segundosSt));
                    if (Double.parseDouble(segundosSt) > 59.999) {
                        return null;
                    }
                    if (segundosSt.charAt(2) == '.') {
                        String decimales = segundosSt.substring(2);
                        for (int j = 2; j < decimales.length(); j++) {
                            formatDate += "S";
                        }
                    }
                }
            }
            res = new SimpleDateFormat(formatDate).parse(
                    horasSt + ":" + minutosSt + ":" + segundosSt);

        } catch (ParseException | NumberFormatException ex) {
            return res;
        }
        return res;
    }

    /**
     * Elimina un registro cuyo id es "registroid"
     *
     * @param registroid Id del registro a eliminar
     * @throws controlador.InputException
     */
    public void eliminarRegistro(Integer registroid) throws InputException {

        RegistroJpa registrojpa = new RegistroJpa();
        try {
            registrojpa.destroy(registroid);
        } catch (NonexistentEntityException ex) {
            throw new InputException("Registro no encontrado");
        }
    }

    /**
     * Modifica la marca del registro cuyo id es "registroid"
     *
     * @param registroid Id del registro que se va a modificar
     *
     * @return el Registro modificado
     * @throws controlador.InputException
     */
    public Registro modificarRegistro(Integer registroid) throws InputException {

        RegistroJpa registrojpa = new RegistroJpa();

        // Obtenemos el objeto Registro a partir de su Id
        Registro registro = registrojpa.findRegistro(registroid);
        if (registro != null) {
            String formatDate = "HH:mm:ss.S";
            // Si no es de tipo Tiempo
            if (registro.getPruebaId().getTiporesultado().equals("Distancia")
                    || registro.getPruebaId().getTiporesultado().equals("Numérica")) {
                if (vista.getSegundos().length() == 0) {
                    throw new InputException("Formato del registro no válido");
                }
                // Establecemos el nuevo registro
                registro.setNum(Double.valueOf(vista.getSegundos()));
                // Si es de tipo Tiempo formateamos la hora, minutos y segundos
            } else if (registro.getPruebaId().getTiporesultado().equals("Tiempo")) {
                Date date = getTiempo(Double.parseDouble(vista.getSegundos()), Integer.parseInt(vista.getMinutos()), Integer.parseInt(vista.getHoras()));
                /*try {
                 String horas = vista.getHoras();
                 if (horas.length() == 0) {
                 horas = "00";
                 } else if (horas.length() == 1) {
                 horas = "0" + horas;
                 }
                 String minutos = vista.getMinutos();
                 if (minutos.length() == 0) {
                 minutos = "00";
                 } else if (minutos.length() == 1) {
                 minutos = "0" + minutos;
                 }
                 String segundos = vista.getSegundos();
                 if (segundos.length() == 0) {
                 segundos = "00.0";
                 } else {
                 segundos = Double.toString(Double.parseDouble(segundos));
                 if (Double.parseDouble(segundos) > 59.999) {
                 return false;
                 }
                 if (segundos.charAt(2) == '.') {
                 String decimales = segundos.substring(2);
                 //System.out.println(decimales);

                 for (int j = 2; j < decimales.length(); j++) {
                 formatDate += "S";
                 }
                 }
                 }
                 date = new SimpleDateFormat(formatDate).parse(
                 horas + ":" + minutos + ":" + segundos);

                 } catch (ParseException ex) {
                 System.out.println("ParseException");
                 return false;
                 } catch (NumberFormatException e) {
                 return false;
                 }*/
                if (date != null) {
                    // Establecemos el tiempo formateado
                    registro.setTiempo(date);
                } else {
                    throw new InputException("Formato de tiempo no válido");
                }

            }
            try {
                // Guardamos los cambios en la base de datos
                registrojpa.edit(registro);
            } catch (Exception ex) {
                throw new InputException(ex.getMessage());
            }
            return registro;
        }
        throw new InputException("Registro no encontrado");
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

}
