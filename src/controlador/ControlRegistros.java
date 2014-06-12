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
import main.IOFile;
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
import vista.VistaRegistros;

/**
 *
 * @author JuanM
 */
public class ControlRegistros implements ActionListener {

    VistaRegistros vista;

    /**Constructor que asocia la vista al controlador
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
                    Registro registro = null;
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
                    }
                    if (registro != null) {
                        añadirRegistroAVista(registro);
                        Coordinador.getInstance().setEstadoLabel(
                                "Registro creado correctamente", Color.BLUE);
                    } else {
                        Coordinador.getInstance().setEstadoLabel(
                                "Datos incorrectos", Color.RED);
                    }

                } catch (NumberFormatException e) {
                    Coordinador.getInstance().setEstadoLabel(
                            "Datos incorrectos", Color.RED);
                }

                break;
            case VistaRegistros.MODIFICARREGISTRO:
                if (modificarRegistro(vista.getRegistroSeleccionado())) {
                    Coordinador.getInstance().setEstadoLabel(
                            "Registro modificado correctamente", Color.BLUE);
                    vista.eliminarRegistroDeTabla();
                } else {
                    Coordinador.getInstance().setEstadoLabel(
                            "Datos incorrectos", Color.RED);
                }
                break;
            case VistaRegistros.ELIMINARREGISTRO:
                if (vista.getRegistroSeleccionado() != -1) {
                    int confirmDialogRegistro = JOptionPane.showConfirmDialog(null,
                            "¿Está seguro de que desea eliminar el registro seleccionado?",
                            "Aviso",
                            JOptionPane.YES_NO_OPTION);
                    if (confirmDialogRegistro == JOptionPane.YES_OPTION) {
                        if (eliminarRegistro(vista.getRegistroSeleccionado())) {
                            vista.eliminarRegistroDeTabla();
                            Coordinador.getInstance().setEstadoLabel(
                                    "Registro eliminado correctamente", Color.BLUE);
                        } else {
                            Coordinador.getInstance().setEstadoLabel(
                                    "Selecciona un registro de la tabla", Color.RED);
                        }
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
                    Coordinador.getInstance().mostrarBarraProgreso(true);

                    ImportarRegistros imReg;
                    (imReg = new ImportarRegistros(fc.getSelectedFile().getPath())).execute();

                }
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
     * @return Registro si ha sido creado correctamente, null en otro caso
     */
    public static Registro crearRegistro(Integer dorsal,
            String nombrePrueba, String nombreEquipo, Boolean sorteo,
            Double segundos, Integer minutos, Integer horas) {

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
        if (nombrePrueba != null
                && !(dorsal == null && prueba.getTipo().equals(TipoPrueba.Individual.toString()))
                && !(nombreEquipo == null && prueba.getTipo().equals(TipoPrueba.Equipo.toString()))) {

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
            } else {
                // Obtenemosel equipo
                EquipoJpa equipojpa = new EquipoJpa();
                Equipo equipo = equipojpa.findByNombreAndCompeticion(nombreEquipo,
                        Coordinador.getInstance().getSeleccionada().getId());
                g = grupojpa.findByEquipoCompeticion(Coordinador.getInstance().getSeleccionada().getId(), equipo.getId());
                registro.setEquipoId(equipo);
                i = inscripcionjpa.findInscripcionByCompeticionByGrupo(
                    Coordinador.getInstance().getSeleccionada().getId(), g.getId());
            }

            // Establecemos los datos del registro comunes
            registro.setInscripcionId(i);
            registro.setPruebaId(prueba);
            registro.setSorteo(sorteo ? 1 : 0);

            // Comprueba que la prueba no es de tipo tiempo
            if (prueba.getTiporesultado().equals(TipoResultado.Distancia.toString())
                    || prueba.getTiporesultado().equals(TipoResultado.Numerica.toString())) {
                if (segundos == null) {
                    return null;
                }
                registro.setNum(segundos);
                // Si es de tipo Tiempo formateamos la hora, minutos y segundos    
            } else if (prueba.getTiporesultado().equals(TipoResultado.Tiempo.toString())) {
                // Obtenemos un objeto Date a partir de los parámetros
                Date date = getTiempo(segundos, minutos, horas);
                if (date == null) {
                    return null;
                }
                registro.setTiempo(date);
            }
            // Creamos el registro en la base de datos
            registrojpa.create(registro);
        }
        return registro;
    }

    private void añadirRegistroAVista(Registro r) {
        String formatDate = "HH:mm:ss.S";
        SimpleDateFormat dt = new SimpleDateFormat(formatDate);
        // Si es un equipo 
        if (r.getPruebaId().getTipo().equals(TipoPrueba.Equipo.toString())) {
            vista.añadirRegistroATabla(new Object[]{r.getId(), r.getEquipoId().getId(),
                r.getEquipoId().getNombre() + " (E)",
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
     *
     * @return true si se ha podido eliminar el registro
     */
    public boolean eliminarRegistro(Integer registroid) {

        RegistroJpa registrojpa = new RegistroJpa();
        try {
            registrojpa.destroy(registroid);
        } catch (NonexistentEntityException ex) {
            return false;
        }
        return true;
    }

    /**
     * Modifica la marca del registro cuyo id es "registroid"
     *
     * @param registroid Id del registro que se va a modificar
     *
     * @return true si se ha podido eliminar el registro
     */
    public boolean modificarRegistro(Integer registroid) {

        RegistroJpa registrojpa = new RegistroJpa();

        // Obtenemos el objeto Registro a partir de su Id
        Registro registro = registrojpa.findRegistro(registroid);
        if (registro != null) {
            String formatDate = "HH:mm:ss.S";
            // Si no es de tipo Tiempo
            if (registro.getPruebaId().getTiporesultado().equals("Distancia")
                    || registro.getPruebaId().getTiporesultado().equals("Numérica")) {
                if (vista.getSegundos().length() == 0) {
                    return false;
                }
                // Establecemos el nuevo registro
                registro.setNum(Double.valueOf(vista.getSegundos()));
                // Si es de tipo Tiempo formateamos la hora, minutos y segundos
            } else if (registro.getPruebaId().getTiporesultado().equals("Tiempo")) {
                Date date = null;
                try {
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
                }
                // Establecemos el tiempo formateado
                registro.setTiempo(date);
            }
            try {
                // Guardamos los cambios en la base de datos
                registrojpa.edit(registro);
            } catch (Exception ex) {
                return false;
            }

            // Actualizamos la vista
            SimpleDateFormat dt = new SimpleDateFormat(formatDate);
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
            return true;
        }
        return false;
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
        List<Registro> registros;

        // Obtenemos los diferentes filtros de la vista
        String grupo = vista.getFiltroGrupoComboBox().getSelectedItem().toString();
        String prueba = vista.getFiltroPruebasComboBox().getSelectedItem().toString();
        String participante = vista.getFiltroParticipante();

        // Obtenemos la competición seleccionada
        Integer competicionSeleccionada = Coordinador.getInstance().getSeleccionada().getId();

        // Aplicamos los filtros según lo seleccionado
        if (grupo.equals("Todos")) {
            if (prueba.equals("Todas")) {
                if (participante.equals("Equipos")) {
                    registros = registrojpa.findByCompeticionEquipo(competicionSeleccionada);
                } else if (participante.equals("Individuales")) {
                    registros = registrojpa.findByCompeticionIndividual(competicionSeleccionada);
                } else {
                    registros = registrojpa.findByCompeticion(competicionSeleccionada);
                }
            } else {
                Prueba p = pruebajpa.findPruebaByNombreCompeticion(prueba,
                        Coordinador.getInstance().getControladorPrincipal().getSeleccionada().getId());
                if (participante.equals("Equipos")) {
                    registros = registrojpa.findByCompeticionPruebaEquipo(competicionSeleccionada, p.getId());
                } else if (participante.equals("Individuales")) {
                    registros = registrojpa.findByCompeticionPruebaIndividual(competicionSeleccionada, p.getId());
                } else {
                    registros = registrojpa.findByCompeticionPrueba(competicionSeleccionada, p.getId());
                }
            }
        } else {
            Grupo g = grupojpa.findGrupoByNombreAndCompeticion(grupo,
                      Coordinador.getInstance().getSeleccionada().getId());
            if (prueba.equals("Todas")) {
                if (participante.equals("Equipos")) {
                    registros = registrojpa.findByCompeticionGrupoEquipo(competicionSeleccionada, g.getId());
                } else if (participante.equals("Individuales")) {
                    registros = registrojpa.findByCompeticionGrupoIndividual(competicionSeleccionada, g.getId());
                } else {
                    registros = registrojpa.findByCompeticionGrupo(competicionSeleccionada, g.getId());
                }
            } else {
                Prueba p = pruebajpa.findPruebaByNombreCompeticion(prueba,
                        Coordinador.getInstance().getControladorPrincipal().getSeleccionada().getId());
                if (participante.equals("Equipos")) {
                    registros = registrojpa.findByCompeticionGrupoPruebaEquipo(competicionSeleccionada, g.getId(), p.getId());
                } else if (participante.equals("Individuales")) {
                    registros = registrojpa.findByCompeticionGrupoPruebaIndividual(competicionSeleccionada, g.getId(), p.getId());
                } else {
                    registros = registrojpa.findByCompeticionGrupoPrueba(competicionSeleccionada, g.getId(), p.getId());
                }
            }
        }

        // Actualizamos la vista
        Coordinador.getInstance().getControladorPrincipal().cargarTablaRegistros(registros);
    }

}
