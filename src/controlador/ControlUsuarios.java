package controlador;

import modelo.entities.RolUsuario;
import modelo.logicaNegocio.UsuarioService;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JOptionPane;
import modelo.entities.Usuario;
import vista.interfaces.VistaUsuarios;

/**
 *
 * @author JuanM
 */
public class ControlUsuarios implements ActionListener {

    private final VistaUsuarios vista;

    /**
     * Constructor que asocia la vista al controlador
     *
     * @param vista Vista del controlador (Interfaz)
     */
    public ControlUsuarios(VistaUsuarios vista) {
        this.vista = vista;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();
        switch (command) {
            case VistaUsuarios.CREARUSUARIO:
                try {
                    Usuario usuario = UsuarioService.crearUsuario(vista.getNombreDeUsuario(), new String(vista.getContraseña()), vista.getRol(), vista.getCompeticionesConAcceso());
                    // Actualizamos la vista
                    vista.limpiarFormularioUsuario();
                    vista.añadirUsuarioATabla(new Object[]{
                        usuario.getId(),
                        usuario.getNick(),
                        RolUsuario.values()[usuario.getRol()],});

                    Coordinador.getInstance().setEstadoLabel(
                            "Usuario creado correctamente", Color.BLUE);
                } catch (InputException ex) {
                    Coordinador.getInstance().setEstadoLabel(ex.getMessage(), Color.RED);
                }
                break;
            case VistaUsuarios.MODIFICARUSUARIO:
                try {
                    Usuario usuario = UsuarioService.modificarUsuario(vista.getUsuarioSeleccionado(), vista.getNombreDeUsuario(), new String(vista.getContraseña()), vista.getRol());
                    // Actualizamos la vista
                    vista.eliminarUsuarioSeleccionado();
                    vista.añadirUsuarioATabla(new Object[]{
                        usuario.getId(),
                        usuario.getNick(),
                        RolUsuario.values()[usuario.getRol()]
                    });
                    Coordinador.getInstance().setEstadoLabel(
                            "Usuario modificado correctamente", Color.BLUE);
                } catch (InputException ex) {
                    Coordinador.getInstance().setEstadoLabel(ex.getMessage(), Color.RED);
                }
                break;
            case VistaUsuarios.ELIMINARUSUARIO:
                int confirmDialog = JOptionPane.showConfirmDialog(null,
                        "¿Está seguro de que desea eliminar el grupo seleccionado?",
                        "Aviso",
                        JOptionPane.YES_NO_OPTION);
                if (confirmDialog == JOptionPane.YES_OPTION) {
                    try {
                        UsuarioService.eliminarUsuario(vista.getUsuarioSeleccionado());
                        Coordinador.getInstance().setEstadoLabel(
                                "Usuario eliminado correctamente", Color.BLUE);
                        vista.eliminarUsuarioSeleccionado();
                        vista.limpiarFormularioUsuario();
                    } catch (InputException ex) {
                        Coordinador.getInstance().setEstadoLabel(ex.getMessage(), Color.RED);
                    }
                }
                break;
            case VistaUsuarios.LIMPIARFORMULARIO:
                vista.limpiarFormularioUsuario();
                break;
            case VistaUsuarios.INCLUIRCOMPETICION:
                List<String> competiciones = vista.getCompeticionesSeleccionadas();
                for (String s : competiciones) {
                    vista.añadirCompeticionConAcceso(s);
                    vista.eliminarCompeticion(s);
                    if (vista.getUsuarioSeleccionado() != -1) {
                        try {
                            UsuarioService.darAccesoACompeticion(vista.getUsuarioSeleccionado(), s);
                        } catch (InputException ex) {
                            Coordinador.getInstance().setEstadoLabel(ex.getMessage(), Color.RED);
                        }
                    }
                }
                break;
            case VistaUsuarios.EXCLUIRCOMPETICION:
                List<String> comp = vista.getCompeticionesConAccesoSeleccionadas();
                for (String s : comp) {
                    vista.añadirCompeticion(s);
                    vista.eliminarCompeticionConAcceso(s);
                    if (vista.getUsuarioSeleccionado() != -1) {
                        try {
                            UsuarioService.quitarAccesoACompeticion(vista.getUsuarioSeleccionado(), s);
                        } catch (InputException ex) {
                            Coordinador.getInstance().setEstadoLabel(ex.getMessage(), Color.RED);
                        }
                    }
                }
                break;
        }
    }

    

}
