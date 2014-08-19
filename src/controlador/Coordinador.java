package controlador;

import modelo.entities.RolUsuario;
import modelo.entities.TipoResultado;
import modelo.entities.TipoPrueba;
import modelo.dao.AdministradoJpa;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import main.DataBaseHelper;
import modelo.entities.Competicion;
import modelo.entities.Equipo;
import modelo.entities.Grupo;
import modelo.entities.Prueba;
import modelo.entities.Registro;
import modelo.entities.Usuario;
import modelo.dao.EquipoJpa;
import modelo.dao.GrupoJpa;
import modelo.dao.ParticipanteJpa;
import modelo.dao.PruebaJpa;
import modelo.dao.RegistroJpa;
import modelo.dao.UsuarioJpa;
import modelo.entities.Participante;
import vista.PanelLogin;
import vista.PanelPrincipal;
import vista.ParticipantesTab;
import vista.RegistrosTab;
import vista.UsuariosTab;
import vista.interfaces.VistaLogin;

/**
 *
 * @author JuanM
 */
public class Coordinador {

    private JFrame jf;
    private VistaLogin login;
    private PanelPrincipal panelPrincipal;
    private static final Coordinador coordinador = new Coordinador();
    private ControlPrincipal controladorPrincipal;
    private ControlLogin controladorLogin;
    private Usuario usuario;

    public static Coordinador getInstance() {
        return coordinador;
    }

    private Coordinador() {

    }

    /**
     * Inicializa la base de datos y carga la pantalla de login
     *
     */
    public void inicializar() {
        DataBaseHelper dbhelper = null;
        try {
            // Inicializa la base de datos
            dbhelper = new DataBaseHelper();
            dbhelper.iniDB();

        } catch (SQLException ex) { 
            // Error al conectar con la base de datos. Mensaje de error
            JOptionPane optionPane = new JOptionPane("La aplicación ya está abierta. Cierra e inténtalo de nuevo.", JOptionPane.ERROR_MESSAGE);
            JDialog dialog = optionPane.createDialog("Error al inicializar la base de datos");
            dialog.setAlwaysOnTop(true);
            dialog.setVisible(true);
            System.exit(0);

        } finally {
            try {
                // Cerramos la conexión
                if (dbhelper != null) {
                    dbhelper.close();
                }
            } catch (SQLException sqlException) {

            }
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Cargamos la pantalla de Login

                jf = new JFrame("Aplicación para Gestión de Actividades Físicas y Deportivas");
                login = new PanelLogin();
                controladorLogin = new ControlLogin(login);
                login.controlador(controladorLogin);
                jf.setContentPane((JPanel) login);
                jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                jf.setMinimumSize(new Dimension(1024, 768));
                Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
                jf.setLocation(dimension.width / 2 - jf.getSize().width / 2, dimension.height / 2 - jf.getSize().height / 2);
                jf.pack();
                jf.setVisible(true);
            }
        });
    }

    // GETTERS AND SETTERS
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Competicion getSeleccionada() {
        if (controladorPrincipal != null) {
            return controladorPrincipal.getSeleccionada();
        } else {
            return null;
        }
    }

    public PanelPrincipal getPanelPrincipal() {
        return panelPrincipal;
    }

    public ControlPrincipal getControladorPrincipal() {
        return controladorPrincipal;
    }

    public void setControladorPrincipal(ControlPrincipal controladorPrincipal) {
        this.controladorPrincipal = controladorPrincipal;
    }

    public ControlLogin getControladorLogin() {
        return controladorLogin;
    }

    public void setControladorLogin(ControlLogin controladorLogin) {
        this.controladorLogin = controladorLogin;
    }

    public void inicializarLogin() {
        panelPrincipal = null;
        controladorPrincipal = null;
        jf.getContentPane().removeAll();
        login = new PanelLogin();
        controladorLogin = new ControlLogin(login);
        login.controlador(controladorLogin);
        jf.setContentPane((JPanel) login);
        jf.revalidate();
    }

    /**
     * Método llamado cuando el Login es correcto para cargar el programa
     * principal
     *
     */
    public void loginOK() {
        panelPrincipal = new PanelPrincipal();
        controladorPrincipal = new ControlPrincipal(panelPrincipal);
        panelPrincipal.controlador(controladorPrincipal);
        controladorPrincipal.cargarListaCompeticiones(usuario);
        if (!(RolUsuario.values()[usuario.getRol()]).equals(RolUsuario.Administrador)) {
            panelPrincipal.habilitarBotonesAdmin(false);
        }
        jf.getContentPane().removeAll();
        jf.setContentPane((JPanel) panelPrincipal);
        jf.revalidate();

    }

    /**
     * Añade la competición creada a la lista de competiciones, la establece
     * como seleccionada y actualiza el estado.
     *
     * @param competicion Competicion creada
     */
    public void actualizarVistaCompeticionCreada(Competicion competicion) {
        controladorPrincipal.setSeleccionada(competicion);
        panelPrincipal.añadirCompeticion(competicion.getNombre());
        controladorPrincipal.getVista().setFocusList(0);
        controladorPrincipal.getVista().setEstadoLabel(
                "Competición creada correctamente", Color.BLUE);
    }

    /**
     * Modifica el nombre de la competición en la lista de competiciones y
     * actualiza el estado.
     *
     * @param competicion
     */
    public void actualizarVistaCompeticionModificada(Competicion competicion) {
        panelPrincipal.eliminarCompeticionSeleccionada();
        panelPrincipal.añadirCompeticion(competicion.getNombre());
        controladorPrincipal.getVista().setFocusList(0);
        controladorPrincipal.getVista().setEstadoLabel(
                "Competición modificada correctamente", Color.BLUE);
    }

    /**
     * Carga en la vista la tabla de pruebas de la competicion c
     *
     * @param c Competicion de la que se cargaran las pruebas
     */
    public void cargarTablaPruebasCompeticion(Competicion c) {

        if (c != null) {
            List<Prueba> lista;
            PruebaJpa prujpa = new PruebaJpa();
            // Obtenemos de la base de datos una lista de las pruebas de la competicion c
            lista = prujpa.findPruebasByCompeticon(c);

            // Borramos todas las filas de la tabla
            limpiarTablaPruebas();
            int count = panelPrincipal.getGeneralTabPanel().getModeloPruebasTable().getRowCount();
            for (int i = 0; i < count; i++) {
                panelPrincipal.getGeneralTabPanel().getModeloPruebasTable().removeRow(0);
            }

            // Añadimos las nuevas filas a la tabla a partir de la lista de pruebas
            for (Prueba p : lista) {
                panelPrincipal.getGeneralTabPanel().getModeloPruebasTable().addRow(
                        new Object[]{
                            p.getId(),
                            p.getNombre(),
                            p.getTipo(),
                            p.getTiporesultado()});
            }
        }
    }

    /**
     * Elimina todas las filas de la tabla de pruebas
     */
    public void limpiarTablaPruebas() {
        int count = panelPrincipal.getGeneralTabPanel().getModeloPruebasTable().getRowCount();
        for (int i = 0; i < count; i++) {
            panelPrincipal.getGeneralTabPanel().getModeloPruebasTable().removeRow(0);
        }
    }

    /**
     * Elimina todas las filas de la tabla de registros
     */
    public void limpiarTablaRegistros() {
        int count = controladorPrincipal.getRegistrosTabPanel().getModeloRegistrosTable().getRowCount();
        for (int i = 0; i < count; i++) {
            controladorPrincipal.getRegistrosTabPanel().getModeloRegistrosTable().removeRow(0);
        }
    }

    /**
     * Establece un mensaje en la parte inferior del programa
     *
     * @param estado Mensaje que se mostrará
     * @param color Color del mensaje
     */
    public void setEstadoLabel(String estado, Color color) {
        controladorPrincipal.getVista().setEstadoLabel(estado, color);
    }

    public void mostrarBarraProgreso(Boolean mostrar) {
        controladorPrincipal.getVista().mostrarBarraProgreso(mostrar);
    }

    /**
     * Carga en el formulario de participantes los datos del participante cuyo
     * id es participanteid
     *
     * @param participanteid Id del participante a cargar
     */
    public void cargarFormularioParticipante(Integer participanteid) {

        ParticipanteJpa participantejpa = new ParticipanteJpa();
        Participante participante = participantejpa.findParticipante(participanteid);
        if (participante != null) {
            ParticipantesTab pt = controladorPrincipal.getParticipantesTabPanel();
            pt.setNombreParticipante(participante.getNombre());
            pt.setApellidosParticipante(participante.getApellidos());
            pt.setEdadParticipante(participante.getEdad());
            pt.setDorsalParticipante(participante.getDorsal());
            pt.setGrupoParticipante(participante.getGrupoId().getNombre());
            pt.setEquipoParticipante(participante.getEquipoId() != null
                    ? participante.getEquipoId().getNombre() : "Ninguno");
            pt.setSexoParticipante(participante.getSexo());
            pt.setPruebaAsignadaParticipante(participante.getPruebaasignada() != null
                    ? participante.getPruebaasignada().getNombre() : "Ninguna");
        }
    }

    /**
     * Carga en el formulario de usuarios con los datos del usuario cuyo id es
     * usuarioid
     *
     * @param usuarioid Id del usuario a cargar
     */
    public void cargarFormularioUsuario(Integer usuarioid) {

        UsuarioJpa usuariojpa = new UsuarioJpa();
        Usuario usuario = usuariojpa.findUsuario(usuarioid);

        if (usuario != null) {
            UsuariosTab ut = controladorPrincipal.getUsuariosTabPanel();
            ut.setNombreUsuario(usuario.getNick());
            ut.setContraseñaUsuario(usuario.getPassword());
            ut.setRolUsuario(RolUsuario.values()[usuario.getRol()]);

            controladorPrincipal.cargarListaCompeticionesTabUsuarios();
            AdministradoJpa administradoJpa = new AdministradoJpa();
            List<String> competicionesConAcceso = administradoJpa.findCompeticionesByUser(usuarioid);
            if (competicionesConAcceso != null) {
                for (String s : competicionesConAcceso) {
                    ut.eliminarCompeticion(s);
                    ut.añadirCompeticionConAcceso(s);
                }
            }
        }
    }

    /**
     * Carga los datos de una prueba cuyo id es pruebaid en el formulario
     *
     * @param pruebaid Id de la prueba
     */
    public void cargarFormularioPrueba(Integer pruebaid) {
        PruebaJpa pruebajpa = new PruebaJpa();

        Prueba p = pruebajpa.findPrueba(pruebaid);
        if (p != null) {
            controladorPrincipal.getVista().getGeneralTabPanel().setNombrePrueba(p.getNombre());
            controladorPrincipal.getVista().getGeneralTabPanel().setTipoPrueba(p.getTipo());
            controladorPrincipal.getVista().getGeneralTabPanel().setTipoResultado(p.getTiporesultado());
        }
    }

    /**
     * Carga los datos de un grupo cuyo id es grupoid en el formulario
     *
     * @param grupoid Id del grupo
     */
    public void cargarFormularioGrupo(Integer grupoid) {
        GrupoJpa grupojpa = new GrupoJpa();

        Grupo g = grupojpa.findGrupo(grupoid);
        if (g != null) {
            controladorPrincipal.getGruposTabPanel().setNombreGrupo(g.getNombre());
            controladorPrincipal.getGruposTabPanel().setSubGrupoDe(
                    g.getGrupoId() != null
                    ? g.getGrupoId().getNombre() : "Ninguno");
        }
    }

    /**
     * Carga los datos de un equipo cuyo id es "equipoid" en el formulario
     *
     * @param equipoid Id del equipo
     */
    public void cargarFormularioEquipo(Integer equipoid) {
        EquipoJpa equipojpa = new EquipoJpa();
        GrupoJpa grupojpa = new GrupoJpa();

        Equipo equipo = equipojpa.findEquipo(equipoid);

        if (equipo != null) {
            Grupo g = grupojpa.findByEquipoCompeticion(
                    getControladorPrincipal().getSeleccionada().getId(),
                    equipo.getId());
            controladorPrincipal.getEquiposTabPanel().setNombreEquipo(equipo.getNombre());
            controladorPrincipal.getEquiposTabPanel().setGrupoDelEquipo(g.getNombre());
        }
    }

    /**
     * Carga los datos de un registro cuyo id es "registroSeleccionado" en el
     * formulario
     *
     * @param registroId Id del registro
     */
    public void cargarFormularioRegistro(Integer registroId) {

        // Buscamos el registro a partir de su id
        RegistroJpa registrojpa = new RegistroJpa();
        Registro registro = registrojpa.findRegistro(registroId);

        // Si existe cargamos el formulario
        if (registro != null) {
            RegistrosTab rt = controladorPrincipal.getRegistrosTabPanel();
            rt.setGrupoParticipante(registro.getInscripcionId().getGrupoId().getNombre().toString());

            controladorPrincipal.getRegistrosTabPanel().setPrueba(registro.getPruebaId().getNombre());
            if (registro.getPruebaId().getTipo().equals(TipoPrueba.Individual.toString())) {
                rt.setParticipante(registro.getParticipanteId().getDorsal()
                        + ": " + registro.getParticipanteId().getApellidos()
                        + ", " + registro.getParticipanteId().getNombre());
            } else {
                rt.setParticipante(registro.getEquipoId().getNombre());
                //rt.setParticipante("Equipo");
            }

            if (registro.getPruebaId().getTiporesultado().equals(TipoResultado.Tiempo.toString())) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH");
                rt.setHoras(dateFormat.format(registro.getTiempo()));
                dateFormat.applyPattern("mm");
                rt.setMinutos(dateFormat.format(registro.getTiempo()));
                dateFormat.applyPattern("ss.S");
                rt.setSegundos(dateFormat.format(registro.getTiempo()));
            } else {
                rt.setSegundos(registro.getNum().toString());
            }

        }
    }

    /**
     * Devuelve el tipo de prueba dado el nombre de una prueba
     *
     * @param prueba Nombre de la prueba
     * @return 0: Tiempo, 1: Distancia, 2:Num, -1 Error
     */
    public TipoResultado tipoResultado(String prueba) {
        PruebaJpa pruebajpa = new PruebaJpa();
        Prueba p = pruebajpa.findPruebaByNombreCompeticion(prueba,
                Coordinador.getInstance().getSeleccionada().getId());
        if (p == null) {
            return null;
        }
        return TipoResultado.valueOf(p.getTiporesultado());
    }

    public TipoPrueba tipoPrueba(String nombrePrueba) {
        PruebaJpa pruebajpa = new PruebaJpa();
        Prueba p = pruebajpa.findPruebaByNombreCompeticion(nombrePrueba,
                Coordinador.getInstance().getSeleccionada().getId());
        if (p == null) {
            return null;
        }
        return TipoPrueba.valueOf(p.getTipo());
    }

    /**
     * A partir del nombre de una prueba obtiene la prueba y comprueba si es de
     * tipo tiempo
     *
     * @param prueba Nombre de la prueba
     * @return true si la prueba es de tipo Tiempo
     */
    public boolean pruebaDeTiempo(String prueba) {
        PruebaJpa pruebajpa = new PruebaJpa();
        Prueba p = pruebajpa.findPruebaByNombreCompeticion(prueba,
                Coordinador.getInstance().getSeleccionada().getId());
        if (p != null) {
            return p.getTiporesultado().equals(TipoResultado.Tiempo.toString());
        } else {
            return false;
        }
    }
}
