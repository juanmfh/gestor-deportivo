package controlador;

import modelo.RolUsuario;
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
import modelo.Competicion;
import modelo.Usuario;
import vista.VistaUsuarios;

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
                    Usuario usuario = crearUsuario(vista.getNombreDeUsuario(), new String(vista.getContraseña()), vista.getRol(), vista.getCompeticionesConAcceso());
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
                for (String s : competiciones) {
                    vista.añadirCompeticionConAcceso(s);
                    vista.eliminarCompeticion(s);
                    if (vista.getUsuarioSeleccionado() != -1) {
                        try {
                            darAccesoACompeticion(vista.getUsuarioSeleccionado(), s);
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
                            quitarAccesoACompeticion(vista.getUsuarioSeleccionado(), s);
                        } catch (InputException ex) {
                            Coordinador.getInstance().setEstadoLabel(ex.getMessage(), Color.RED);
                        }
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
    public static Usuario crearUsuario(String nick, String password, RolUsuario rol, List<Object> competicionesConAcceso) throws InputException {
        Usuario usuario = null;

        if (nick != null && nick.length() > 0) {
            if (password != null && password.length() > 0) {
                if (rol != null) {

                    UsuarioJpa usuariojpa = new UsuarioJpa();

                    // Buscamos que el nick no este ya en uso
                    usuario = usuariojpa.findUsuariobyNick(nick);

                    // Si no existe un usuario con ese nick se puede crear
                    if (usuario == null) {
                        usuario = new Usuario();
                        usuario.setNick(nick);
                        usuario.setPassword(password);
                        usuario.setRol(rol.ordinal());
                        usuariojpa.create(usuario);

                        //Le damos permiso al usuario en las competiciones
                        if (competicionesConAcceso != null) {

                            AdministradoJpa administradoJpa = new AdministradoJpa();
                            CompeticionJpa competicionJpa = new CompeticionJpa();
                            for (Object competicionString : competicionesConAcceso) {
                                Administrado administrado = new Administrado();
                                administrado.setCompeticionId(competicionJpa.findCompeticionByName(competicionString.toString()));
                                administrado.setUsuarioId(usuario);
                                administradoJpa.create(administrado);
                            }
                        }
                    } else {
                        throw new InputException("Nombre de usuario ocupado");
                    }
                    return usuario;
                } else {
                    throw new InputException("Rol no válido");
                }
            } else {
                throw new InputException("Contraseña no válida");
            }
        } else {
            throw new InputException("Nombre de usuario no válido");
        }

    }

    public static void eliminarUsuario(Integer usuarioId) throws InputException {

        if (usuarioId != null) {
            UsuarioJpa usuarioJpa = new UsuarioJpa();
            Usuario usuario = usuarioJpa.findUsuario(usuarioId);

            if (usuario != null) {

                //Miramos a ver si es el único usuario con rol de admin
                List<Usuario> usuarios = usuarioJpa.findByRol(RolUsuario.Administrador);
                if (RolUsuario.values()[usuario.getRol()].toString().equals(RolUsuario.Administrador.toString()) && (usuarios == null || usuarios.size() <= 1)) {
                    throw new InputException("No se puede eliminar el último administrador del sistema");
                } else {
                    //Buscamos todas las competiciones que administra el usuario seleccionado
                    AdministradoJpa administradoJpa = new AdministradoJpa();
                    List<Administrado> administrados = administradoJpa.findByUsuario(usuarioId);
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
                    usuarioJpa.destroy(usuarioId);
                } catch (IllegalOrphanException | NonexistentEntityException ex) {
                    throw new InputException("No se pudo eliminar el usuario seleccionado");
                }
            } else {
                throw new InputException("Usuario no encontrado");
            }
        } else {
            throw new InputException("Usuario no válido");
        }
    }

    public static Usuario modificarUsuario(Integer usuarioId, String nuevoNick, String nuevaPassword, RolUsuario nuevoRol) throws InputException {

        if (usuarioId != null) {

            UsuarioJpa usuariojpa = new UsuarioJpa();
            Usuario usuario = usuariojpa.findUsuario(usuarioId);

            if (usuario != null) {
                //Comprobamos que el nick y la contraseña son válidos
                if (nuevoNick != null && nuevoNick.length() > 0) {
                    if (nuevaPassword != null && nuevaPassword.length() > 0) {

                        Usuario temp = usuariojpa.findUsuariobyNick(nuevoNick);
                        if (temp != null && temp.getId() != usuario.getId()) {
                            throw new InputException("Nombre de usuario ocupado");
                        } else {
                            usuario.setNick(nuevoNick);
                            usuario.setPassword(nuevaPassword);

                            if (nuevoRol != null) {
                                //Miramos a ver si es el único usuario con rol de admin
                                List<Usuario> usuarios = usuariojpa.findByRol(RolUsuario.Administrador);
                                if ((RolUsuario.values()[usuario.getRol()].toString().equals(RolUsuario.Administrador.toString()) && (usuarios == null || usuarios.size() <= 1) && !nuevoRol.equals(RolUsuario.Administrador))) {
                                    throw new InputException("No se puede eliminar el último administrador del sistema");
                                } else {
                                    usuario.setRol(nuevoRol.ordinal());
                                }
                                try {
                                    usuariojpa.edit(usuario);
                                    return usuario;
                                } catch (Exception ex) {
                                    throw new InputException("No se pudo modificar el usuario seleccionado");
                                }
                            } else {
                                throw new InputException("Rol no válido");
                            }
                        }
                    } else {
                        throw new InputException("Contraseña no válida");
                    }
                } else {
                    throw new InputException("Nombre de usuario no válido");
                }
            } else {
                throw new InputException("Usuario no encontrado");
            }
        } else {
            throw new InputException("Usuario no válido");
        }
    }

    /**
     * Da permiso para administrar/ver una competición determinada a un usuario
     *
     * @param usuarioId Identificador del usuario
     * @param nombreCompeticion Nombre de la competicion
     * @throws controlador.InputException
     */
    public static void darAccesoACompeticion(Integer usuarioId, String nombreCompeticion) throws InputException {

        if (usuarioId != null) {
            if (nombreCompeticion != null) {
                AdministradoJpa administradoJpa = new AdministradoJpa();
                Administrado administrado = new Administrado();

                UsuarioJpa usuarioJpa = new UsuarioJpa();
                Usuario usuario = usuarioJpa.findUsuario(usuarioId);

                CompeticionJpa competicionJpa = new CompeticionJpa();
                Competicion competicion = competicionJpa.findCompeticionByName(nombreCompeticion);

                if (usuario != null) {
                    if (competicion != null) {

                        administrado.setUsuarioId(usuario);
                        administrado.setCompeticionId(competicion);
                        administradoJpa.create(administrado);
                    } else {
                        throw new InputException("Competición no encontrada");
                    }
                } else {
                    throw new InputException("Usuario no encontrado");
                }
            } else {
                throw new InputException("Competición no válida");
            }
        } else {
            throw new InputException("Usuario no válido");
        }
    }

    /**
     * Elimina el permiso para administrar/ver una competición determinada a un
     * usuario
     *
     * @param usuarioId
     * @param nombreCompeticion Nombre de la competicion
     * @throws controlador.InputException
     */
    public static void quitarAccesoACompeticion(Integer usuarioId, String nombreCompeticion) throws InputException {

        if (usuarioId != null) {
            if (nombreCompeticion != null) {
                AdministradoJpa administradoJpa = new AdministradoJpa();
                Administrado administrado = administradoJpa.findByCompeticionAndUsuario(usuarioId, nombreCompeticion);

                if (administrado != null) {
                    try {
                        administradoJpa.destroy(administrado.getId());
                    } catch (NonexistentEntityException ex) {
                        throw new InputException("No se puede quitar el acceso a esa competición");
                    }
                }else{
                    throw new InputException("El usuario no tiene permiso en esta competición");
                }
            } else {
                throw new InputException("Competición no válida");
            }
        } else {
            throw new InputException("Usuario no válido");
        }
    }

}
