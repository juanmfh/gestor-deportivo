package controlador;

import dao.AdministradoJpa;
import dao.CompeticionJpa;
import dao.UsuarioJpa;
import dao.exceptions.IllegalOrphanException;
import dao.exceptions.NonexistentEntityException;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import modelo.Administrado;
import modelo.Usuario;
import vista.VistaUsuarios;

/**
 *
 * @author JuanM
 */
public class ControlUsuarios implements ActionListener {

    private VistaUsuarios vista;

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
                    Usuario usuario = crearUsuario(vista.getNombreDeUsuario(), new String(vista.getContraseña()), vista.getRol(), vista.getCompeticionesConAccesoSeleccionadas());
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
                    Usuario usuario = modificarUsuario(vista.getUsuarioSeleccionado(), vista.getNombreDeUsuario(), new String(vista.getContraseña()), vista.getRol());
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
                        eliminarUsuario(vista.getUsuarioSeleccionado());
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
                if (vista.getUsuarioSeleccionado() != -1) {
                    for (String s : competiciones) {
                        vista.añadirCompeticionConAcceso(s);
                        vista.eliminarCompeticion(s);
                    }
                }
                break;
            case VistaUsuarios.EXCLUIRCOMPETICION:
                List<String> comp = vista.getCompeticionesConAccesoSeleccionadas();
                if (vista.getUsuarioSeleccionado() != -1) {
                    for (String s : comp) {
                        vista.añadirCompeticion(s);
                        vista.eliminarCompeticionConAcceso(s);
                    }
                }
                break;
        }
    }

    /**
     * Crea un nuevo usuario en la aplicación
     *
     * @param nick String con el nombre del usuario
     * @param password String con la contraseña del usuario
     * @param rol Enumerado que indica el rol del usuario
     * @param competicionesConAcceso Lista de competiciones en las que el
     * usuario tiene acceso
     * @return Usuario
     * @throws InputException
     */
    public static Usuario crearUsuario(String nick, String password, RolUsuario rol, List<String> competicionesConAcceso) throws InputException {
        Usuario usuario = null;

        if (nick != null && nick.length() > 0 && password != null && password.length() > 0) {
            UsuarioJpa usuariojpa = new UsuarioJpa();

            // Buscamos que el nick no este ya en uso
            usuario = usuariojpa.findUsuariobyNick(nick);

            // Si no existe un usuario con ese nick se puede crear
            if (usuario == null) {
                usuario = new Usuario();
                usuario.setNick(nick);
                usuario.setPassword(password);
                usuario.setRol(rol.ordinal());

                //Le damos permiso al usuario en las competiciones
                if (competicionesConAcceso != null) {
                    AdministradoJpa administradoJpa = new AdministradoJpa();
                    CompeticionJpa competicionJpa = new CompeticionJpa();
                    for (String competicionString : competicionesConAcceso) {
                        Administrado administrado = new Administrado();
                        administrado.setCompeticionId(competicionJpa.findCompeticionByName(competicionString));
                        administrado.setUsuarioId(usuario);
                        administradoJpa.create(administrado);
                    }
                }

                usuariojpa.create(usuario);
            } else {
                throw new InputException("Nombre de usuario ocupado");
            }
        } else {
            throw new InputException("Nombre de usuario o contraseña no válidos");
        }
        return usuario;
    }

    private void eliminarUsuario(Integer usuarioSeleccionado) throws InputException {
        UsuarioJpa usuarioJpa = new UsuarioJpa();
        Usuario usuario = usuarioJpa.findUsuario(usuarioSeleccionado);

        if (usuario != null) {

            //Miramos a ver si es el único usuario con rol de admin
            List<Usuario> usuarios = usuarioJpa.findByRol(RolUsuario.Administrador);
            if (RolUsuario.values()[usuario.getRol()].toString().equals(RolUsuario.Administrador.toString()) && (usuarios == null || usuarios.size() <= 1)) {
                throw new InputException("No se puede eliminar el último administrador del sistema");
            } else {
                //Buscamos todas las competiciones que administra el usuario seleccionado
                AdministradoJpa administradoJpa = new AdministradoJpa();
                List<Administrado> administrados = administradoJpa.findByUsuario(usuarioSeleccionado);
                if (administrados != null) {
                    for (Administrado a : administrados) {
                        try {
                            administradoJpa.destroy(a.getId());
                        } catch (NonexistentEntityException ex) {
                            Logger.getLogger(ControlUsuarios.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
            try {
                usuarioJpa.destroy(usuarioSeleccionado);
            } catch (IllegalOrphanException | NonexistentEntityException ex) {
                throw new InputException("No se pudo eliminar el usuario seleccionado");
            }
        } else {
            throw new InputException("No se ha seleccionado un usuario válido");
        }
    }

    private Usuario modificarUsuario(Integer usuarioSeleccionado, String nuevoNick, String nuevaPassword, RolUsuario nuevoRol) throws InputException {

        UsuarioJpa usuariojpa = new UsuarioJpa();
        Usuario usuario = usuariojpa.findUsuario(usuarioSeleccionado);

        if (usuario != null) {
            //Comprobamos que el nick y la contraseña son válidos
            if (nuevoNick != null && nuevoNick.length() > 0 && nuevaPassword != null && nuevaPassword.length() > 0) {
                Usuario temp = usuariojpa.findUsuariobyNick(nuevoNick);
                if (temp != null && temp.getId() != usuario.getId()) {
                    throw new InputException("Nombre de usuario ocupado");
                } else {
                    usuario.setNick(nuevoNick);
                    usuario.setPassword(nuevaPassword);

                    //Miramos a ver si es el único usuario con rol de admin
                    List<Usuario> usuarios = usuariojpa.findByRol(RolUsuario.Administrador);
                    if (nuevoRol==null || (RolUsuario.values()[usuario.getRol()].toString().equals(RolUsuario.Administrador.toString()) && (usuarios == null || usuarios.size() <= 1) && !nuevoRol.equals(RolUsuario.Administrador))) {
                        throw new InputException("No se puede eliminar el último administrador del sistema");
                    }else{
                        usuario.setRol(nuevoRol.ordinal());
                    }
                    try {
                        usuariojpa.edit(usuario);
                        return usuario;
                    } catch (Exception ex) {
                        throw new InputException("No se pudo modificar el usuario seleccionado");
                    }
                }
            } else {
                throw new InputException("Nombre de usuario o contraseña no válidos");
            }
        } else {
            throw new InputException("No se ha seleccionado un usuario válido");
        }
    }

}
