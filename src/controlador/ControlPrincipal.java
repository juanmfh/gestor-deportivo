package controlador;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import modelo.Administrado;
import modelo.Competicion;
import modelo.Compuesta;
import modelo.Equipo;
import modelo.Prueba;
import dao.AdministradoJpa;
import dao.CompeticionJpa;
import dao.CompuestaJpa;
import dao.PruebaJpa;
import modelo.Grupo;
import modelo.Inscripcion;
import modelo.Registro;
import dao.EquipoJpa;
import dao.GrupoJpa;
import dao.InscripcionJpa;
import dao.ParticipanteJpa;
import dao.RegistroJpa;
import dao.UsuarioJpa;
import dao.exceptions.IllegalOrphanException;
import dao.exceptions.NonexistentEntityException;
import java.awt.Frame;
import modelo.Participante;
import modelo.Usuario;
import vista.DialogoCrearCompeticion;
import vista.DialogoImprimirResultados;
import vista.EquiposTab;
import vista.GruposTab;
import vista.PanelPrincipal;
import vista.ParticipantesTab;
import vista.RegistrosTab;
import vista.UsuariosTab;
import vista.VistaPrincipal;

/**
 *
 * @author JuanM
 */
public class ControlPrincipal implements ActionListener {

    private PanelPrincipal vista;

    private Competicion seleccionada;

    // Vistas de las pestañas
    private ParticipantesTab participantesTabPanel;
    private GruposTab gruposTabPanel;
    private EquiposTab equiposTabPanel;
    private RegistrosTab registrosTabPanel;
    private UsuariosTab usuariosTabPanel;

    // Controladores de las pestañas
    private ControlGrupos controlGrupos;
    private ControlParticipantes controlParticipantes;
    private ControlEquipos controlEquipos;
    private ControlRegistros controlRegistros;
    private ControlPruebas controlPruebas;
    private ControlUsuarios controlUsuarios;

    /**
     * Constructor que asocia la vista al controlador
     *
     * @param vista Vista del controlador (Interfaz)
     */
    public ControlPrincipal(PanelPrincipal vista) {
        this.vista = vista;
        controlPruebas = new ControlPruebas(vista.getGeneralTabPanel());
        vista.getGeneralTabPanel().controlador(controlPruebas);
        seleccionada = null;
    }

    // GETTERS AND SETTERS
    public GruposTab getGruposTabPanel() {
        return gruposTabPanel;
    }

    public ParticipantesTab getParticipantesTabPanel() {
        return participantesTabPanel;
    }

    public UsuariosTab getUsuariosTabPanel() {
        return usuariosTabPanel;
    }

    public EquiposTab getEquiposTabPanel() {
        return equiposTabPanel;
    }

    public RegistrosTab getRegistrosTabPanel() {
        return registrosTabPanel;
    }

    public void setParticipantesTabPanel(ParticipantesTab participantesTabPanel) {
        this.participantesTabPanel = participantesTabPanel;
    }

    public void setGruposTabPanel(GruposTab gruposTabPanel) {
        this.gruposTabPanel = gruposTabPanel;
    }

    public void setEquiposTabPanel(EquiposTab equiposTabPanel) {
        this.equiposTabPanel = equiposTabPanel;
    }

    public void setRegistrosTabPanel(RegistrosTab registrosTabPanel) {
        this.registrosTabPanel = registrosTabPanel;
    }

    public Competicion getSeleccionada() {
        return seleccionada;
    }

    public void setSeleccionada(Competicion seleccionada) {
        this.seleccionada = seleccionada;
    }

    public VistaPrincipal getVista() {
        return vista;
    }

    public void setVista(PanelPrincipal vista) {
        this.vista = vista;
    }

    /**
     * Carga la lista de competiciones en la interfaz principal según las
     * competiciones a las que tiene acceso un usuario, si es admin a todas.
     *
     * @param usuario Usuario
     */
    public void cargarListaCompeticiones(Usuario usuario) {
        List<String> competiciones;
        if (RolUsuario.values()[usuario.getRol()].equals(RolUsuario.Administrador)) {
            CompeticionJpa competicionjpa = new CompeticionJpa();
            competiciones = competicionjpa.findAllCompeticionNames();
        } else {
            AdministradoJpa administradojpa = new AdministradoJpa();
            competiciones = administradojpa.findCompeticionesByUser(usuario.getId());

        }
        vista.cargarListaCompeticiones(competiciones);
    }

    /**
     * Devuelve un objeto Competicion a partir del nombre de la competicion
     *
     * @param nombre Nombre de la competicion
     * @return Competicion
     */
    public static Competicion getCompeticion(String nombre) {
        Competicion res;
        CompeticionJpa compjpa = new CompeticionJpa();
        res = compjpa.findCompeticionByName(nombre);
        return res;
    }

    /**
     * Establece como seleccionada a la competicion cuyo nombre es el pasado
     * como parámetro y cargar los datos en el TabGeneral
     *
     * @param nombreCompeticion Nombre de la competicion
     */
    public void cargarCompeticionGeneral(String nombreCompeticion) {
        this.seleccionada = getCompeticion(nombreCompeticion);
        vista.cargarCompeticionTabGeneral();

    }

    /**
     * Elimina la Competicion y todo sus datos (grupos, participantes,
     * resultados) dado su nombre
     *
     * @param nombreCompeticion Nombre de la competicion
     * @throws controlador.InputException
     */
    public void eliminarCompeticion(String nombreCompeticion) throws InputException {

        CompeticionJpa competicionjpa = new CompeticionJpa();
        // Obtenemos la competicion a partir de su nombre
        Competicion c = competicionjpa.findCompeticionByName(nombreCompeticion);

        // Se comprueba que la competición existe
        if (c != null) {
            CompuestaJpa compuestajpa = new CompuestaJpa();
            List<Compuesta> compuesta = compuestajpa.findCompuestaByCompeticion(c.getId());
            
            AdministradoJpa admjpa = new AdministradoJpa();
            List<Administrado> administra = admjpa.findAdministradoByCompeticion(c);
            
            InscripcionJpa inscripcionjpa = new InscripcionJpa();
            List<Inscripcion> inscripciones = inscripcionjpa.findInscripcionByCompeticion(c.getId());

            try {
                for (Administrado temp : administra) {
                    admjpa.destroy(temp.getId());
                }
                RegistroJpa registrosjpa = new RegistroJpa();
                List<Integer> gruposIds = new ArrayList<>();
                for (Inscripcion insc : inscripciones) {
                    gruposIds.add(insc.getGrupoId().getId());
                    List<Registro> registros = registrosjpa.findByInscripcion(insc.getId());
                    if (registros != null) {
                        // Eliminamos todos los registros
                        for (Registro r : registros) {
                            registrosjpa.destroy(r.getId());
                        }
                    }
                    inscripcionjpa.destroy(insc.getId());
                }

                GrupoJpa grupojpa = new GrupoJpa();
                for (Integer g : gruposIds) {
                    Grupo grupo = grupojpa.findGrupo(g);
                    if (grupo != null) {
                        if (grupo.getInscripcionCollection().isEmpty()
                                && grupo.getGrupoId() == null) {
                            eliminarGrupo(grupo);
                        }
                    }
                }
                PruebaJpa pruebajpa = new PruebaJpa();
                for (Compuesta comp : compuesta) {
                    compuestajpa.destroy(comp.getId());
                    pruebajpa.destroy(comp.getPruebaId().getId());
                }

                competicionjpa.destroy(c.getId());
            } catch (dao.exceptions.IllegalOrphanException | dao.exceptions.NonexistentEntityException e) {
                throw new InputException("No se pudo eliminar algún dato de la competición seleccionada");
            }
        } else {
            throw new InputException("Competición no encontrada");
        }

    }

    /**
     * Elimina un grupo g y todos sus subgrupos, participantes y registros
     *
     * @param g Grupo a eliminar
     */
    public void eliminarGrupo(Grupo g) throws InputException {
        GrupoJpa grupojpa = new GrupoJpa();
        InscripcionJpa inscrjpa = new InscripcionJpa();
        RegistroJpa registrosjpa = new RegistroJpa();

        // Obtenemos la lista de subgrupos y los eliminamos
        List<Grupo> subgrupos = grupojpa.findGrupoByGrupoId(g.getId());
        for (Grupo subgrupo : subgrupos) {
            eliminarGrupo(subgrupo);
        }
        try {
            Inscripcion i = inscrjpa.findInscripcionByCompeticionByGrupo(
                    seleccionada.getId(), g.getId());
            if (i != null) {
                // Obtenemos los registros de este grupo y los eliminamos
                List<Registro> registros = registrosjpa.findByInscripcion(i.getId());
                if (registros != null) {
                    for (Registro r : registros) {
                        registrosjpa.destroy(r.getId());
                    }
                }
                // Eliminamos la inscripción en este grupo
                inscrjpa.destroy(i.getId());
            }
            // Eliminamos los participantes
            eliminarParticipantes(g);
            // Eliminamos el grupo
            grupojpa.destroy(g.getId());
        } catch (IllegalOrphanException | NonexistentEntityException | InputException ex) {
            throw new InputException(ex.getMessage());
        }
    }

    /**
     * Elimina los participantes de un grupo g (No elimina los registros)
     *
     * @param g Grupo al que pertenecen los participantes
     */
    public void eliminarParticipantes(Grupo g) throws InputException {

        ParticipanteJpa participantejpa = new ParticipanteJpa();

        // Elimina los equipos
        eliminarEquiposGrupo(g);
        List<Participante> participantes = participantejpa.findParticipantesByGrupo(g.getId());
        try {
            for (Participante participante : participantes) {
                // Eliminamos el participante
                participantejpa.destroy(participante.getId());
            }
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(ControlPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Elimina todos los equipos de un grupo g (No sus participantes)
     *
     * @param g Grupo al que pertenecen los equipos
     * @throws controlador.InputException
     */
    public void eliminarEquiposGrupo(Grupo g) throws InputException {

        EquipoJpa equipojpa = new EquipoJpa();

        // Obtenemos la lista de equipos que participan en el grupo g
        List<Equipo> equipos = equipojpa.findByGrupo(g.getId());
        try {
            // Por cada participacion de un equipo
            for (Equipo e : equipos) {
                equipojpa.destroy(e.getId());
            }
        } catch (NonexistentEntityException ex) {
            throw new InputException("Equipo no encontrado");
        }
    }

    /**
     * Obtiene una lista de los Grupos de una Competicion c
     *
     * @param c Competicion
     * @return List<Grupo> Lista de grupos
     */
    public List<Grupo> gruposByCompeticion(Competicion c) {
        GrupoJpa grupojpa = new GrupoJpa();
        List<Grupo> grupos = grupojpa.findGruposByCompeticion(c);
        return grupos;
    }

    /**
     * Carga en el comboBox de Tab de Equipos los grupos de la competicion
     * seleccionada
     */
    public void cargarGruposEnEquipos() {
        List<Grupo> grupos = gruposByCompeticion(seleccionada);
        equiposTabPanel.getGruposComboBox().removeAllItems();
        if (grupos != null) {
            for (Grupo g : grupos) {
                equiposTabPanel.getGruposComboBox().addItem(g.getNombre());
            }
        }
    }

    /**
     * Carga en el Tab de Registros los grupos de la competición seleccionada
     */
    public void cargarGruposEnRegistros() {
        List<Grupo> grupos = gruposByCompeticion(seleccionada);
        registrosTabPanel.getGruposComboBox().removeAllItems();
        registrosTabPanel.getFiltroGrupoComboBox().removeAllItems();
        registrosTabPanel.getFiltroGrupoComboBox().addItem("Todos");
        if (grupos != null) {
            for (Grupo g : grupos) {
                registrosTabPanel.getFiltroGrupoComboBox().addItem(g.getNombre());
                registrosTabPanel.getGruposComboBox().addItem(g.getNombre());
            }
        }
    }

    /**
     * Carga en el tab de Registros las pruebas de la competición seleccionada
     */
    public void cargarPruebasEnRegistros() {
        PruebaJpa pruebajpa = new PruebaJpa();
        List<Prueba> pruebas = pruebajpa.findPruebasByCompeticon(seleccionada);
        registrosTabPanel.getPruebasComboBox().removeAllItems();
        registrosTabPanel.getFiltroPruebasComboBox().removeAllItems();
        //registrosTabPanel.getFiltroPruebasComboBox().addItem("Todas");
        if (pruebas != null) {
            for (Prueba p : pruebas) {
                registrosTabPanel.getPruebasComboBox().addItem(p.getNombre());
                registrosTabPanel.getFiltroPruebasComboBox().addItem(p.getNombre());
            }
        }
    }

    /**
     * Carga en el comboBox del Tab de Grupos los subGrupos disponibles
     */
    public void cargarSubGruposComboBox() {

        List<Grupo> grupos = gruposByCompeticion(seleccionada);
        gruposTabPanel.getSubgrupoDeComboBox().removeAllItems();
        if (grupos != null) {
            gruposTabPanel.getSubgrupoDeComboBox().addItem("Ninguno");
            for (Grupo g : grupos) {
                gruposTabPanel.getSubgrupoDeComboBox().addItem(g.getNombre());
            }
        }
    }

    /**
     * Carga la tabla de Equipos de la competición seleccionada
     */
    public void cargarTablaEquipos() {
        EquipoJpa equipojpa = new EquipoJpa();
        // Obtenemos la lista de equipos
        List<Equipo> equipos = equipojpa.findByCompeticion(Coordinador.getInstance().getSeleccionada().getId());
        GrupoJpa grupojpa = new GrupoJpa();

        // Limpiamos la tabla
        int count = equiposTabPanel.getModeloEquiposTable().getRowCount();
        for (int i = 0; i < count; i++) {
            equiposTabPanel.getModeloEquiposTable().removeRow(0);
        }

        // Añadimos una fila por cada equipo
        if (equipos != null) {
            for (Equipo e : equipos) {
                Grupo g = grupojpa.findByEquipoCompeticion(
                        Coordinador.getInstance().getSeleccionada().getId(),
                        e.getId());
                equiposTabPanel.getModeloEquiposTable().addRow(new Object[]{
                    e.getId(),
                    e.getNombre(),
                    g.getNombre(),
                    e.getParticipanteCollection().size()});
            }
        }
    }

    /**
     * Carga en el Tab de Registros los registros pasados como parámetro
     *
     * @param registros Lista de registros que se cargarán en la tabla
     */
    public void cargarTablaRegistros(List<Registro> registros) {

        // Limpia la tabla
        int count = registrosTabPanel.getModeloRegistrosTable().getRowCount();
        for (int i = 0; i < count; i++) {
            registrosTabPanel.getModeloRegistrosTable().removeRow(0);
        }

        if (registros != null) {

            SimpleDateFormat dt = new SimpleDateFormat("HH:mm:ss.S");
            // Por cada registro crea una fila con sus datos
            for (Registro r : registros) {
                if (r.getEquipoId() != null) {
                    registrosTabPanel.getModeloRegistrosTable().addRow(
                            new Object[]{r.getId(),
                                r.getEquipoId().getId(),
                                r.getEquipoId().getNombre(),
                                r.getPruebaId().getNombre() + (r.getSorteo() == 1
                                ? " (Sorteo)" : ""),
                                r.getPruebaId().getTiporesultado().equals("Tiempo")
                                ? dt.format(r.getTiempo()).toString()
                                : r.getNum(), r.getNumIntento()});
                } else {
                    registrosTabPanel.getModeloRegistrosTable().addRow(
                            new Object[]{r.getId(),
                                r.getParticipanteId().getDorsal(),
                                r.getParticipanteId().getApellidos()
                                + ", " + r.getParticipanteId().getNombre(),
                                r.getPruebaId().getNombre() + (r.getSorteo() == 1
                                ? " (Sorteo)" : ""),
                                r.getPruebaId().getTiporesultado().equals("Tiempo")
                                ? dt.format(r.getTiempo()).toString()
                                : r.getNum(), r.getNumIntento()});
                }
            }
        }
    }

    /**
     * Carga en el Tab de Grupos la tabla de grupos de la competición
     * seleccionada
     */
    public void cargarTablaGrupos() {

        // Obtenemos la lista de grupos de la competición seleccionada
        GrupoJpa grupojpa = new GrupoJpa();
        List<Grupo> grupos = grupojpa.findGruposByCompeticion(seleccionada);

        //Limpia la tabla
        int count = gruposTabPanel.getModeloGruposTable().getRowCount();
        for (int i = 0; i < count; i++) {
            gruposTabPanel.getModeloGruposTable().removeRow(0);
        }

        // Añade una fila por cada grupo
        if (grupos != null) {
            for (Grupo g : grupos) {
                gruposTabPanel.getModeloGruposTable().addRow(
                        new Object[]{g.getId(),
                            g.getNombre(),
                            g.getGrupoId() != null
                            ? g.getGrupoId().getNombre() : null});
            }
        }
    }

    /**
     * Carga en el Tab de Participantes el comboBox de Grupos
     */
    public void cargarGruposEnParticipantes() {

        // Obtenemos la lista de grupos de la competición seleccionada
        GrupoJpa grupojpa = new GrupoJpa();
        List<Grupo> grupos = grupojpa.findGruposByCompeticion(seleccionada);

        // Limpiamos el combobox
        participantesTabPanel.getGrupoComboBox().removeAllItems();
        // Añadimos el nombre de cada grupo al combobox
        if (grupos != null) {
            for (Grupo g : grupos) {
                participantesTabPanel.getGrupoComboBox().addItem(g.getNombre());
            }
        }
    }

    /**
     * Carga en el Tab de Participantes el comboBox de Equipos
     *
     * @param grupo Nombre del grupo
     */
    public void cargarEquipoEnParticipantes(String grupo) {
        EquipoJpa equipojpa = new EquipoJpa();
        GrupoJpa grupojpa = new GrupoJpa();

        // Obtenemos el grupo por el nombre
        Grupo g = grupojpa.findGrupoByNombreAndCompeticion(grupo,
                Coordinador.getInstance().getSeleccionada().getId());
        if (g != null) {
            // Obtenemos la lista de equipos del grupo anterior
            List<Equipo> equipos = equipojpa.findByGrupo(g.getId());
            // Limpiamos el comboBox
            participantesTabPanel.getEquipoComboBox().removeAllItems();
            // Añadimos el nombre de cada equipo y un componente "Ninguno"
            if (equipos != null) {
                participantesTabPanel.getEquipoComboBox().addItem("Ninguno");
                for (Equipo e : equipos) {
                    participantesTabPanel.getEquipoComboBox().addItem(e.getNombre());
                }
            }
        }
    }

    /**
     * Carga en el Tab de Participantes el comboBox de Pruebas
     */
    public void cargarPruebasEnParticipantes() {

        PruebaJpa pruebajpa = new PruebaJpa();

        // Obtenemos la lista de pruebas de la competición
        List<Prueba> pruebas = pruebajpa.findPruebasByCompeticon(seleccionada);
        // Limpiamos el comboBox
        participantesTabPanel.getPruebaComboBox().removeAllItems();
        // Añadimos el nombre de cada prueba y un componente "Ninguno"
        if (pruebas != null) {
            participantesTabPanel.getPruebaComboBox().addItem("Ninguna");
            for (Prueba p : pruebas) {
                participantesTabPanel.getPruebaComboBox().addItem(p.getNombre());
            }
        }
    }

    /**
     * Carga en el tab de Registros el comboBox de participantes (individuales)
     *
     * @param grupo Nombre del grupo al que pertenecen los participantes
     * @param nombrePrueba Nombre de la prueba. Se utiliza para saber si es
     * individual o en equipo
     */
    public void cargarParticipantesEnRegistros(String grupo, String nombrePrueba, boolean participantesAsignados) {

        GrupoJpa grupojpa = new GrupoJpa();
        PruebaJpa pruebajpa = new PruebaJpa();
        ParticipanteJpa participantejpa = new ParticipanteJpa();
        // Obtenemos el grupo a partir del nombre
        Grupo g = grupojpa.findGrupoByNombreAndCompeticion(grupo, seleccionada.getId());
        // Obtenemos la prueba a partir del nombre
        Prueba prueba = pruebajpa.findPruebaByNombreCompeticion(nombrePrueba, seleccionada.getId());
        if (g != null && prueba != null) {
            // Si la prueba es individual
            if (prueba.getTipo().equals(TipoPrueba.Individual.toString())) {
                List<Participante> participantes;
                // Obtenemos solo los participantes que tienen dicha prueba como asignada
                if (participantesAsignados) {
                    participantes = participantejpa.findParticipantesByGrupoPruebaAsignada(g.getId(), prueba);
                } else {
                    // Obtenemos todas los participantes de este grupo
                    participantes = participantejpa.findParticipantesByGrupo(g.getId());
                }
                // Limpiamos el combobox
                registrosTabPanel.getParticipantesComboBox().removeAllItems();
                // Añadimos cada participante 
                if (participantes != null) {
                    for (Participante p : participantes) {
                        registrosTabPanel.getParticipantesComboBox().addItem(
                                p.getDorsal() + ": " + p.getApellidos()
                                + ", " + p.getNombre());
                    }
                }
            } else {
                EquipoJpa equipojpa = new EquipoJpa();
                // Obtenemos los equipos de ese grupo
                List<Equipo> equipos = equipojpa.findByGrupo(g.getId());

                // Limpiamos el comboBox
                registrosTabPanel.getParticipantesComboBox().removeAllItems();
                // Añadimos el nombre de cada equipo 
                if (equipos != null) {
                    for (Equipo e : equipos) {
                        registrosTabPanel.getParticipantesComboBox().addItem(e.getNombre());
                    }
                }
            }
        }
    }

    /**
     * Carga en el tab de participantes la tabla de participantes
     */
    public void cargarTablaParticipantes() {
        ParticipanteJpa participantejpa = new ParticipanteJpa();
        GrupoJpa grupojpa = new GrupoJpa();

        // Obtenemos una lista de grupos de la competición seleccionada
        List<Grupo> grupos = grupojpa.findGruposByCompeticion(seleccionada);

        // Limpiamos la tabla
        int count = participantesTabPanel.getModeloParticipantesTable().getRowCount();
        for (int i = 0; i < count; i++) {
            participantesTabPanel.getModeloParticipantesTable().removeRow(0);
        }
        // Añadimos una fila por cada participante con sus datos
        if (grupos != null) {
            for (Grupo g : grupos) {
                List<Participante> participantes = participantejpa.findParticipantesByGrupo(g.getId());
                for (Participante p : participantes) {
                    participantesTabPanel.getModeloParticipantesTable().addRow(
                            new Object[]{p.getId(),
                                p.getDorsal(),
                                p.getApellidos(),
                                p.getNombre(),
                                g.getNombre(),
                                p.getEquipoId() != null
                                ? p.getEquipoId().getNombre()
                                : "Ninguno",
                                p.getPruebaasignada() != null ? p.getPruebaasignada().getNombre() : ""});
                }
            }
        }
    }

    /**
     * Carga en el tab de Adm. de Usuarios la tabla de usuarios
     */
    public void cargarTablaUsuarios() {
        UsuarioJpa usuariojpa = new UsuarioJpa();
        List<Usuario> usuarios = usuariojpa.findUsuarioEntities();
        // Limpiamos la tabla
        int count = usuariosTabPanel.getModeloUsuariosTable().getRowCount();
        for (int i = 0; i < count; i++) {
            usuariosTabPanel.getModeloUsuariosTable().removeRow(0);
        }

        if (usuarios != null) {
            for (Usuario u : usuarios) {
                usuariosTabPanel.getModeloUsuariosTable().addRow(new Object[]{
                    u.getId(),
                    u.getNick(),
                    RolUsuario.values()[u.getRol()]});
            }
        }
    }

    public void cargarListaCompeticionesTabUsuarios() {
        CompeticionJpa competicionjpa = new CompeticionJpa();
        List<String> competiciones = competicionjpa.findAllCompeticionNames();
        usuariosTabPanel.eliminarTodasCompeticiones();
        if (competiciones != null) {
            for (String s : competiciones) {
                usuariosTabPanel.añadirCompeticion(s);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();
        switch (command) {
            case VistaPrincipal.CREARCOMPETICION:

                // Abrimos un diálogo para crear la competición
                java.awt.EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        DialogoCrearCompeticion dialog
                                = new DialogoCrearCompeticion("Crear competición",
                                        new javax.swing.JFrame(), true);
                        dialog.setMinimumSize(new Dimension(650, 400));
                        Dimension dimension
                                = Toolkit.getDefaultToolkit().getScreenSize();
                        dialog.setLocation(dimension.width / 2
                                - dialog.getSize().width / 2, dimension.height / 2 - dialog.getSize().height / 2);
                        ActionListener controladorDialog = new ControlCrearCompeticion(dialog);
                        dialog.controlador(controladorDialog);
                        dialog.setVisible(true);
                    }
                });
                break;
            case VistaPrincipal.MODIFICARCOMPETICION:
                // Abrimos un diálogo para modificar la competición
                if (vista.getCompeticionSelected() != null) {
                    java.awt.EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {

                            DialogoCrearCompeticion dialog
                                    = new DialogoCrearCompeticion(
                                            "Modificar competición",
                                            new javax.swing.JFrame(), true);
                            dialog.setMinimumSize(new Dimension(650, 400));
                            Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
                            dialog.setLocation(dimension.width / 2
                                    - dialog.getSize().width / 2, dimension.height / 2 - dialog.getSize().height / 2);
                            ActionListener controladorDialog = new ControlModificarCompeticion(dialog);
                            dialog.controlador(controladorDialog);
                            seleccionada = getCompeticion(vista.getCompeticionSelected());
                            dialog.cargarDatosCompeticion(seleccionada);
                            dialog.setVisible(true);
                        }
                    });
                }
                break;
            case VistaPrincipal.ELIMINARCOMPETICION:
                // Mostramos un mensaje de confirmación
                int confirmDialog = JOptionPane.showConfirmDialog(null,
                        "¿Está seguro de que desea eliminar la competición seleccionada?",
                        "Aviso",
                        JOptionPane.YES_NO_OPTION);
                if (confirmDialog == JOptionPane.YES_OPTION) {
                    // Confirmación aceptada
                    if (vista.getCompeticionSelected() != null) {
                        try {
                            // Eliminamos la competición
                            eliminarCompeticion(vista.getCompeticionSelected());
                        } catch (InputException ex) {
                            Coordinador.getInstance().setEstadoLabel(ex.getMessage(),Color.RED);
                        }
                            // Actualizamos la vista
                            vista.eliminarCompeticionSeleccionada();
                            vista.getTabbedPane().setSelectedIndex(0);
                            Coordinador.getInstance().setEstadoLabel(
                                    "Competición eliminada correctamente",
                                    Color.BLUE);
                            if (participantesTabPanel != null) {
                                vista.getTabbedPane().removeTabAt(
                                        vista.getTabbedPane().indexOfTab("Participantes"));
                                participantesTabPanel = null;
                            }
                            if (gruposTabPanel != null) {
                                vista.getTabbedPane().removeTabAt(
                                        vista.getTabbedPane().indexOfTab("Grupos"));
                                gruposTabPanel = null;
                            }
                            if (equiposTabPanel != null) {
                                vista.getTabbedPane().removeTabAt(
                                        vista.getTabbedPane().indexOfTab("Equipos"));
                                equiposTabPanel = null;
                            }
                            if (registrosTabPanel != null) {
                                vista.getTabbedPane().removeTabAt(
                                        vista.getTabbedPane().indexOfTab("Registros"));
                                registrosTabPanel = null;
                            }
                            if (usuariosTabPanel != null) {
                                vista.getTabbedPane().removeTabAt(
                                        vista.getTabbedPane().indexOfTab("Usuarios"));
                                usuariosTabPanel = null;
                            }
                        }
                        Coordinador.getInstance().getControladorPrincipal().vista.limpiarDatosCompeticion();
                }
                break;

            case VistaPrincipal.ABRIRPARTICIPANTES:
                if (seleccionada != null) {
                    if (participantesTabPanel == null) {
                        participantesTabPanel = new ParticipantesTab();
                        controlParticipantes = new ControlParticipantes(participantesTabPanel);
                        participantesTabPanel.controlador(controlParticipantes);
                        vista.getTabbedPane().addTab("Participantes", participantesTabPanel);
                    }
                    vista.getTabbedPane().setSelectedIndex(
                            vista.getTabbedPane().indexOfTab("Participantes"));
                }
                break;
            case VistaPrincipal.ABRIRREGISTROS:
                if (seleccionada != null) {
                    if (registrosTabPanel == null) {
                        registrosTabPanel = new RegistrosTab();
                        controlRegistros = new ControlRegistros((registrosTabPanel));
                        registrosTabPanel.controlador(controlRegistros);
                        vista.getTabbedPane().addTab("Registros", registrosTabPanel);
                    }
                    vista.getTabbedPane().setSelectedIndex(
                            vista.getTabbedPane().indexOfTab("Registros"));
                    Coordinador.getInstance().getControladorPrincipal().registrosTabPanel.limpiarFormulario();
                }
                break;
            case VistaPrincipal.ABRIRGRUPOS:
                if (seleccionada != null) {
                    if (gruposTabPanel == null) {
                        gruposTabPanel = new GruposTab();
                        controlGrupos = new ControlGrupos(gruposTabPanel);
                        gruposTabPanel.controlador(controlGrupos);
                        vista.getTabbedPane().addTab("Grupos", gruposTabPanel);
                    }
                    vista.getTabbedPane().setSelectedIndex(
                            vista.getTabbedPane().indexOfTab("Grupos"));
                }
                break;
            case VistaPrincipal.ABRIREQUIPOS:
                if (seleccionada != null) {
                    if (equiposTabPanel == null) {
                        equiposTabPanel = new EquiposTab();
                        controlEquipos = new ControlEquipos(equiposTabPanel);
                        equiposTabPanel.controlador(controlEquipos);
                        vista.getTabbedPane().addTab("Equipos", equiposTabPanel);
                    }
                    vista.getTabbedPane().setSelectedIndex(
                            vista.getTabbedPane().indexOfTab("Equipos"));
                }
                break;
            case VistaPrincipal.ABRIRPRUEBAS:
                if (seleccionada != null) {
                    vista.getTabbedPane().setSelectedIndex(
                            vista.getTabbedPane().indexOfTab("General"));
                }
                break;
            case VistaPrincipal.IMPRIMIRPDF:
                java.awt.EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        DialogoImprimirResultados dialog
                                = new DialogoImprimirResultados(new Frame(), true, "PDF");
                        dialog.setMinimumSize(new Dimension(420, 320));
                        Dimension dimension
                                = Toolkit.getDefaultToolkit().getScreenSize();
                        dialog.setLocation(dimension.width / 2
                                - dialog.getSize().width / 2, dimension.height / 2 - dialog.getSize().height / 2);
                        ActionListener controladorDialog = new ControlImprimirResultados(dialog);
                        dialog.controlador(controladorDialog);
                        PruebaJpa pruebajpa = new PruebaJpa();
                        GrupoJpa grupojpa = new GrupoJpa();
                        List<String> pruebas = pruebajpa.findNombresPruebasByCompeticon(seleccionada);
                        List<String> grupos = grupojpa.findNombresGruposByCompeticion(seleccionada);
                        dialog.asignarListaPruebas(pruebas);
                        dialog.asignarListaGrupos(grupos);
                        dialog.setVisible(true);
                    }
                });
                break;
            case VistaPrincipal.ABRIRUSUARIOS:
                if (usuariosTabPanel == null) {
                    usuariosTabPanel = new UsuariosTab();
                    controlUsuarios = new ControlUsuarios(usuariosTabPanel);
                    usuariosTabPanel.controlador(controlUsuarios);
                    vista.getTabbedPane().addTab("Usuarios", usuariosTabPanel);
                }
                vista.getTabbedPane().setSelectedIndex(
                        vista.getTabbedPane().indexOfTab("Usuarios"));
                break;
            case VistaPrincipal.CERRARSESION:
                // Mostramos un mensaje de confirmación
                confirmDialog = JOptionPane.showConfirmDialog(null,
                        "¿Está seguro de que desea cerrar sesión?",
                        "Aviso",
                        JOptionPane.YES_NO_OPTION);
                if (confirmDialog == JOptionPane.YES_OPTION) {
                    Coordinador.getInstance().inicializarLogin();
                }
                break;
        }
    }

}
