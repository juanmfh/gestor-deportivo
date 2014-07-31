package testcases;

import controlador.ControlCompeticiones;
import controlador.ControlUsuarios;
import controlador.InputException;
import dao.UsuarioJpa;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import modelo.Administrado;
import modelo.Competicion;
import modelo.RolUsuario;
import modelo.Usuario;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author JuanM
 */
public class UsuariosTest {

    private static UsuarioJpa usuarioJpa;

    @BeforeClass
    public static void setUpClass() {
        usuarioJpa = new UsuarioJpa();
    }

    // PRUEBAS SOBRE CREAR USUARIO
    
    @Test
    public void crearUsuarioNickNull() {
        try {
            ControlUsuarios.crearUsuario(null, null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Nombre de usuario no válido");
        }
    }

    @Test
    public void crearUsuarioContraseñaNull() {
        try {
            ControlUsuarios.crearUsuario("user1", null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Contraseña no válida");
        }
    }

    @Test
    public void crearUsuarioNickVacio() {
        try {
            ControlUsuarios.crearUsuario("", null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Nombre de usuario no válido");
        }
    }

    @Test
    public void crearUsuarioContraseñaVacia() {
        try {
            ControlUsuarios.crearUsuario("user1", "", null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Contraseña no válida");
        }
    }

    @Test
    public void crearUsuarioRolNull() {
        try {
            ControlUsuarios.crearUsuario("user1", "pass1", null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Rol no válido");
        }
    }

    @Test
    public void crearUsuarioCompeticionesNull() throws InputException {
        ControlUsuarios.crearUsuario("user1", "pass1", RolUsuario.Invitado, null);
        Usuario u = usuarioJpa.findUsuariobyNick("user1");
        assertEquals(u.getAdministradoCollection().size(), 0);
        assertEquals(u.getRol().intValue(), RolUsuario.Invitado.ordinal());
        ControlUsuarios.eliminarUsuario(u.getId());
    }

    @Test
    public void crearUsuarioConAcceso1Competicion() throws InputException {
        Competicion competicion = ControlCompeticiones.crearCompeticion("comp1", null, null, null, null, null);
        List<Object> listaCompeticiones = new ArrayList();
        listaCompeticiones.add(competicion.getNombre());
        ControlUsuarios.crearUsuario("user1", "pass1", RolUsuario.Gestor, listaCompeticiones);
        Usuario u = usuarioJpa.findUsuariobyNick("user1");
        assertEquals(u.getAdministradoCollection().size(), 1);
        assertEquals(u.getAdministradoCollection().iterator().next().getCompeticionId().getId(), competicion.getId());
        assertEquals(u.getRol().intValue(), RolUsuario.Gestor.ordinal());
        ControlUsuarios.eliminarUsuario(u.getId());
        ControlCompeticiones.eliminarCompeticion(competicion.getNombre());
    }

    @Test
    public void crearUsuarioConAccesoVariasCompeticiones() throws InputException {
        Competicion competicion = ControlCompeticiones.crearCompeticion("comp1", null, null, null, null, null);
        Competicion competicion2 = ControlCompeticiones.crearCompeticion("comp2", null, null, null, null, null);
        List<Object> listaCompeticiones = new ArrayList();
        listaCompeticiones.add(competicion.getNombre());
        listaCompeticiones.add(competicion2.getNombre());
        ControlUsuarios.crearUsuario("user1", "pass1", RolUsuario.Gestor, listaCompeticiones);
        Usuario u = usuarioJpa.findUsuariobyNick("user1");
        assertEquals(u.getAdministradoCollection().size(), 2);
        Iterator<Administrado> i = u.getAdministradoCollection().iterator();
        assertEquals(i.next().getCompeticionId().getId(), competicion.getId());
        assertEquals(i.next().getCompeticionId().getId(), competicion2.getId());
        assertEquals(u.getRol().intValue(), RolUsuario.Gestor.ordinal());
        ControlUsuarios.eliminarUsuario(u.getId());
        ControlCompeticiones.eliminarCompeticion(competicion.getNombre());
        ControlCompeticiones.eliminarCompeticion(competicion2.getNombre());
    }

     // PRUEBAS SOBRE MODIFICAR USUARIO
    @Test
    public void modificarUsuarioUsuarioNull() {
        try {
            ControlUsuarios.modificarUsuario(null, null, null, null);
            fail("Debería haber lanzando InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Usuario no válido");
        }
    }

    @Test
    public void modificarUsuarioNombreNull() throws InputException {
        Usuario u = ControlUsuarios.crearUsuario("user1", "pass1", RolUsuario.Invitado, null);
        try {
            ControlUsuarios.modificarUsuario(u.getId(), null, null, null);
            fail("Debería haber lanzando InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Nombre de usuario no válido");
            ControlUsuarios.eliminarUsuario(u.getId());
        }
    }

    @Test
    public void modificarUsuarioContraseñaNull() throws InputException {
        Usuario u = ControlUsuarios.crearUsuario("user1", "pass1", RolUsuario.Invitado, null);
        try {
            ControlUsuarios.modificarUsuario(u.getId(), "user2", null, null);
            fail("Debería haber lanzando InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Contraseña no válida");
            ControlUsuarios.eliminarUsuario(u.getId());
        }
    }

    @Test
    public void modificarUsuarioRolNull() throws InputException {
        Usuario u = ControlUsuarios.crearUsuario("user1", "pass1", RolUsuario.Invitado, null);
        try {
            ControlUsuarios.modificarUsuario(u.getId(), "user2", "pass2", null);
            fail("Debería haber lanzando InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Rol no válido");
            ControlUsuarios.eliminarUsuario(u.getId());
        }
    }

    @Test
    public void modificarUsuarioRolUltimoAdmin() throws InputException {
        try {
            ControlUsuarios.modificarUsuario(usuarioJpa.findUsuariobyNick("admin").getId(), "user2", "pass2", RolUsuario.Invitado);
            fail("Debería haber lanzando InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "No se puede eliminar el último administrador del sistema");
        }
    }

    @Test
    public void modificarUsuario() throws InputException {
        Usuario u = ControlUsuarios.crearUsuario("user1", "pass1", RolUsuario.Gestor, null);
        Usuario u2 = ControlUsuarios.modificarUsuario(u.getId(), "user2", "pass2", RolUsuario.Invitado);
        assertNotNull(u2);
        assertEquals(u2.getNick(), "user2");
        assertEquals(u2.getPassword(), "pass2");
        assertEquals(u2.getRol().intValue(), RolUsuario.Invitado.ordinal());
        assertNull(usuarioJpa.findUsuariobyNick("user1"));
        ControlUsuarios.eliminarUsuario(u2.getId());
    }

     // PRUEBAS SOBRE ELIMINAR USUARIO
    @Test
    public void eliminarUsuarioNull() {
        try {
            ControlUsuarios.eliminarUsuario(null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Usuario no válido");
        }
    }

    @Test
    public void eliminarUsuarioNoExiste() {
        try {
            ControlUsuarios.eliminarUsuario(-1);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Usuario no encontrado");
        }
    }

    @Test
    public void eliminarUsuario() throws InputException {
        Usuario u = ControlUsuarios.crearUsuario("user1", "pass1", RolUsuario.Gestor, null);
        ControlUsuarios.eliminarUsuario(u.getId());
        assertNull(usuarioJpa.findUsuariobyNick("user1"));
    }

    @Test
    public void eliminarUltimoAdmin() {
        Usuario u = usuarioJpa.findUsuariobyNick("admin");
        try {
            ControlUsuarios.eliminarUsuario(u.getId());
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "No se puede eliminar el último administrador del sistema");
        }
    }

     //PRUEBAS SOBRE DAR PERMISO A UN USUARIO EN UNA COMPETICION
    @Test
    public void darAccesoCompeticionNull() {
        try {
            ControlUsuarios.darAccesoACompeticion(null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Usuario no válido");
        }
    }

    @Test
    public void darAccesoCompeticionCompNull() throws InputException {
        Usuario u = ControlUsuarios.crearUsuario("user1", "pass1", RolUsuario.Gestor, null);
        try {
            ControlUsuarios.darAccesoACompeticion(u.getId(), null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Competición no válida");
        }
        ControlUsuarios.eliminarUsuario(u.getId());
    }

    @Test
    public void darAccesoCompeticion() throws InputException {
        Usuario u = ControlUsuarios.crearUsuario("user1", "pass1", RolUsuario.Gestor, null);
        Competicion competicion = ControlCompeticiones.crearCompeticion("comp1", null, null, null, null, null);
        ControlUsuarios.darAccesoACompeticion(u.getId(), "comp1");
        u = usuarioJpa.findUsuariobyNick("user1");
        assertEquals(u.getAdministradoCollection().size(), 1);
        assertEquals(u.getAdministradoCollection().iterator().next().getCompeticionId().getId(), competicion.getId());
        ControlUsuarios.eliminarUsuario(u.getId());
        ControlCompeticiones.eliminarCompeticion(competicion.getNombre());
    }

    @Test
    public void darAccesoCompeticionNoExiste() throws InputException {
        Usuario u = ControlUsuarios.crearUsuario("user1", "pass1", RolUsuario.Gestor, null);
        try {
            ControlUsuarios.darAccesoACompeticion(u.getId(), "comp2");
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Competición no encontrada");
        }
        ControlUsuarios.eliminarUsuario(u.getId());
    }

    @Test
    public void darAccesoCompeticionUsuarioNoExiste() throws InputException {
        try {
            ControlUsuarios.darAccesoACompeticion(-1, "comp2");
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Usuario no encontrado");
        }
    }

    //PRUEBAS SOBRE QUITAR PERMISO A UN USUARIO EN UNA COMPETICION
    @Test
    public void quitarAccesoNull() {
        try {
            ControlUsuarios.quitarAccesoACompeticion(null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Usuario no válido");
        }
    }

    @Test
    public void quitarAccesoCompNull() throws InputException {
        Usuario u = ControlUsuarios.crearUsuario("user1", "pass1", RolUsuario.Gestor, null);
        try {
            ControlUsuarios.quitarAccesoACompeticion(u.getId(), null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Competición no válida");
            ControlUsuarios.eliminarUsuario(u.getId());
        }
    }

    @Test
    public void quitarAccesoCompNoExiste() throws InputException {
        try {
            ControlUsuarios.quitarAccesoACompeticion(-1, "adf");
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "El usuario no tiene permiso en esta competición");
        }
    }

    @Test
    public void quitarAcceso() throws InputException {
        Usuario u = ControlUsuarios.crearUsuario("user1", "pass1", RolUsuario.Gestor, null);
        Competicion competicion = ControlCompeticiones.crearCompeticion("comp2", null, null, null, null, null);
        ControlUsuarios.darAccesoACompeticion(u.getId(), competicion.getNombre());
        ControlUsuarios.quitarAccesoACompeticion(u.getId(), competicion.getNombre());
        assertEquals(u.getAdministradoCollection().size(), 0);
        ControlUsuarios.eliminarUsuario(u.getId());
        ControlCompeticiones.eliminarCompeticion(competicion.getNombre());
    }
}
