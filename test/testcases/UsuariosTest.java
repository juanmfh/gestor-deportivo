package testcases;

import controlador.ControlCompeticiones;
import modelo.dao.UsuarioJpa;
import controlador.InputException;
import modelo.dao.CompeticionJpa;
import modelo.dao.UsuarioJpa;
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
            UsuarioJpa.crearUsuario(null, null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Nombre de usuario no válido");
        }
    }

    @Test
    public void crearUsuarioContraseñaNull() {
        try {
            UsuarioJpa.crearUsuario("user1", null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Contraseña no válida");
        }
    }

    @Test
    public void crearUsuarioNickVacio() {
        try {
            UsuarioJpa.crearUsuario("", null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Nombre de usuario no válido");
        }
    }

    @Test
    public void crearUsuarioContraseñaVacia() {
        try {
            UsuarioJpa.crearUsuario("user1", "", null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Contraseña no válida");
        }
    }

    @Test
    public void crearUsuarioRolNull() {
        try {
            UsuarioJpa.crearUsuario("user1", "pass1", null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Rol no válido");
        }
    }

    @Test
    public void crearUsuarioCompeticionesNull() throws InputException {
        UsuarioJpa.crearUsuario("user1", "pass1", RolUsuario.Invitado, null);
        Usuario u = usuarioJpa.findUsuariobyNick("user1");
        assertEquals(u.getAdministradoCollection().size(), 0);
        assertEquals(u.getRol().intValue(), RolUsuario.Invitado.ordinal());
        UsuarioJpa.eliminarUsuario(u.getId());
    }

    @Test
    public void crearUsuarioConAcceso1Competicion() throws InputException {
        Competicion competicion = CompeticionJpa.crearCompeticion("comp1", null, null, null, null, null);
        List<Object> listaCompeticiones = new ArrayList();
        listaCompeticiones.add(competicion.getNombre());
        UsuarioJpa.crearUsuario("user1", "pass1", RolUsuario.Gestor, listaCompeticiones);
        Usuario u = usuarioJpa.findUsuariobyNick("user1");
        assertEquals(u.getAdministradoCollection().size(), 1);
        assertEquals(u.getAdministradoCollection().iterator().next().getCompeticionId().getId(), competicion.getId());
        assertEquals(u.getRol().intValue(), RolUsuario.Gestor.ordinal());
        UsuarioJpa.eliminarUsuario(u.getId());
        CompeticionJpa.eliminarCompeticion(competicion.getNombre());
    }

    @Test
    public void crearUsuarioConAccesoVariasCompeticiones() throws InputException {
        Competicion competicion = CompeticionJpa.crearCompeticion("comp1", null, null, null, null, null);
        Competicion competicion2 = CompeticionJpa.crearCompeticion("comp2", null, null, null, null, null);
        List<Object> listaCompeticiones = new ArrayList();
        listaCompeticiones.add(competicion.getNombre());
        listaCompeticiones.add(competicion2.getNombre());
        UsuarioJpa.crearUsuario("user1", "pass1", RolUsuario.Gestor, listaCompeticiones);
        Usuario u = usuarioJpa.findUsuariobyNick("user1");
        assertEquals(u.getAdministradoCollection().size(), 2);
        Iterator<Administrado> i = u.getAdministradoCollection().iterator();
        assertEquals(i.next().getCompeticionId().getId(), competicion.getId());
        assertEquals(i.next().getCompeticionId().getId(), competicion2.getId());
        assertEquals(u.getRol().intValue(), RolUsuario.Gestor.ordinal());
        UsuarioJpa.eliminarUsuario(u.getId());
        CompeticionJpa.eliminarCompeticion(competicion.getNombre());
        CompeticionJpa.eliminarCompeticion(competicion2.getNombre());
    }

     // PRUEBAS SOBRE MODIFICAR USUARIO
    @Test
    public void modificarUsuarioUsuarioNull() {
        try {
            UsuarioJpa.modificarUsuario(null, null, null, null);
            fail("Debería haber lanzando InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Usuario no válido");
        }
    }

    @Test
    public void modificarUsuarioNombreNull() throws InputException {
        Usuario u = UsuarioJpa.crearUsuario("user1", "pass1", RolUsuario.Invitado, null);
        try {
            UsuarioJpa.modificarUsuario(u.getId(), null, null, null);
            fail("Debería haber lanzando InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Nombre de usuario no válido");
            UsuarioJpa.eliminarUsuario(u.getId());
        }
    }

    @Test
    public void modificarUsuarioContraseñaNull() throws InputException {
        Usuario u = UsuarioJpa.crearUsuario("user1", "pass1", RolUsuario.Invitado, null);
        try {
            UsuarioJpa.modificarUsuario(u.getId(), "user2", null, null);
            fail("Debería haber lanzando InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Contraseña no válida");
            UsuarioJpa.eliminarUsuario(u.getId());
        }
    }

    @Test
    public void modificarUsuarioRolNull() throws InputException {
        Usuario u = UsuarioJpa.crearUsuario("user1", "pass1", RolUsuario.Invitado, null);
        try {
            UsuarioJpa.modificarUsuario(u.getId(), "user2", "pass2", null);
            fail("Debería haber lanzando InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Rol no válido");
            UsuarioJpa.eliminarUsuario(u.getId());
        }
    }

    @Test
    public void modificarUsuarioRolUltimoAdmin() throws InputException {
        try {
            UsuarioJpa.modificarUsuario(usuarioJpa.findUsuariobyNick("admin").getId(), "user2", "pass2", RolUsuario.Invitado);
            fail("Debería haber lanzando InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "No se puede eliminar el último administrador del sistema");
        }
    }

    @Test
    public void modificarUsuario() throws InputException {
        Usuario u = UsuarioJpa.crearUsuario("user1", "pass1", RolUsuario.Gestor, null);
        Usuario u2 = UsuarioJpa.modificarUsuario(u.getId(), "user2", "pass2", RolUsuario.Invitado);
        assertNotNull(u2);
        assertEquals(u2.getNick(), "user2");
        assertEquals(u2.getPassword(), "pass2");
        assertEquals(u2.getRol().intValue(), RolUsuario.Invitado.ordinal());
        assertNull(usuarioJpa.findUsuariobyNick("user1"));
        UsuarioJpa.eliminarUsuario(u2.getId());
    }

     // PRUEBAS SOBRE ELIMINAR USUARIO
    @Test
    public void eliminarUsuarioNull() {
        try {
            UsuarioJpa.eliminarUsuario(null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Usuario no válido");
        }
    }

    @Test
    public void eliminarUsuarioNoExiste() {
        try {
            UsuarioJpa.eliminarUsuario(-1);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Usuario no encontrado");
        }
    }

    @Test
    public void eliminarUsuario() throws InputException {
        Usuario u = UsuarioJpa.crearUsuario("user1", "pass1", RolUsuario.Gestor, null);
        UsuarioJpa.eliminarUsuario(u.getId());
        assertNull(usuarioJpa.findUsuariobyNick("user1"));
    }

    @Test
    public void eliminarUltimoAdmin() {
        Usuario u = usuarioJpa.findUsuariobyNick("admin");
        try {
            UsuarioJpa.eliminarUsuario(u.getId());
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "No se puede eliminar el último administrador del sistema");
        }
    }

     //PRUEBAS SOBRE DAR PERMISO A UN USUARIO EN UNA COMPETICION
    @Test
    public void darAccesoCompeticionNull() {
        try {
            UsuarioJpa.darAccesoACompeticion(null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Usuario no válido");
        }
    }

    @Test
    public void darAccesoCompeticionCompNull() throws InputException {
        Usuario u = UsuarioJpa.crearUsuario("user1", "pass1", RolUsuario.Gestor, null);
        try {
            UsuarioJpa.darAccesoACompeticion(u.getId(), null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Competición no válida");
        }
        UsuarioJpa.eliminarUsuario(u.getId());
    }

    @Test
    public void darAccesoCompeticion() throws InputException {
        Usuario u = UsuarioJpa.crearUsuario("user1", "pass1", RolUsuario.Gestor, null);
        Competicion competicion = CompeticionJpa.crearCompeticion("comp1", null, null, null, null, null);
        UsuarioJpa.darAccesoACompeticion(u.getId(), "comp1");
        u = usuarioJpa.findUsuariobyNick("user1");
        assertEquals(u.getAdministradoCollection().size(), 1);
        assertEquals(u.getAdministradoCollection().iterator().next().getCompeticionId().getId(), competicion.getId());
        UsuarioJpa.eliminarUsuario(u.getId());
        CompeticionJpa.eliminarCompeticion(competicion.getNombre());
    }

    @Test
    public void darAccesoCompeticionNoExiste() throws InputException {
        Usuario u = UsuarioJpa.crearUsuario("user1", "pass1", RolUsuario.Gestor, null);
        try {
            UsuarioJpa.darAccesoACompeticion(u.getId(), "comp2");
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Competición no encontrada");
        }
        UsuarioJpa.eliminarUsuario(u.getId());
    }

    @Test
    public void darAccesoCompeticionUsuarioNoExiste() throws InputException {
        try {
            UsuarioJpa.darAccesoACompeticion(-1, "comp2");
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Usuario no encontrado");
        }
    }

    //PRUEBAS SOBRE QUITAR PERMISO A UN USUARIO EN UNA COMPETICION
    @Test
    public void quitarAccesoNull() {
        try {
            UsuarioJpa.quitarAccesoACompeticion(null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Usuario no válido");
        }
    }

    @Test
    public void quitarAccesoCompNull() throws InputException {
        Usuario u = UsuarioJpa.crearUsuario("user1", "pass1", RolUsuario.Gestor, null);
        try {
            UsuarioJpa.quitarAccesoACompeticion(u.getId(), null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Competición no válida");
            UsuarioJpa.eliminarUsuario(u.getId());
        }
    }

    @Test
    public void quitarAccesoCompNoExiste() throws InputException {
        try {
            UsuarioJpa.quitarAccesoACompeticion(-1, "adf");
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "El usuario no tiene permiso en esta competición");
        }
    }

    @Test
    public void quitarAcceso() throws InputException {
        Usuario u = UsuarioJpa.crearUsuario("user1", "pass1", RolUsuario.Gestor, null);
        Competicion competicion = CompeticionJpa.crearCompeticion("comp2", null, null, null, null, null);
        UsuarioJpa.darAccesoACompeticion(u.getId(), competicion.getNombre());
        UsuarioJpa.quitarAccesoACompeticion(u.getId(), competicion.getNombre());
        assertEquals(u.getAdministradoCollection().size(), 0);
        UsuarioJpa.eliminarUsuario(u.getId());
        CompeticionJpa.eliminarCompeticion(competicion.getNombre());
    }
}
