package testcases;

import controlador.ControlCompeticiones;
import controlador.ControlUsuarios;
import controlador.InputException;
import dao.ParticipanteJpa;
import dao.UsuarioJpa;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.Administrado;
import modelo.Competicion;
import modelo.RolUsuario;
import modelo.Usuario;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
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
    

    /*@Before
    public void ini() throws InputException {
        competicion = ControlCompeticiones.crearCompeticion("comp1", null, null, null, null, null);
    }
    
    @After
    public void destroy() throws InputException {
        ControlCompeticiones.eliminarCompeticion("comp1");
    }*/
    
    // PRUEBAS SOBRE CREAR USUARIO
    
    @Test
    public void crearUsuarioNickNull(){
        try {
            ControlUsuarios.crearUsuario(null, null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(),"Nombre de usuario no válido");
        }
    }
    
    @Test
    public void crearUsuarioContraseñaNull(){
        try {
            ControlUsuarios.crearUsuario("user1", null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(),"Contraseña no válida");
        }
    }
    
    @Test
    public void crearUsuarioNickVacio(){
        try {
            ControlUsuarios.crearUsuario("", null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(),"Nombre de usuario no válido");
        }
    }
    
    @Test
    public void crearUsuarioContraseñaVacia(){
        try {
            ControlUsuarios.crearUsuario("user1", "", null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(),"Contraseña no válida");
        }
    }
    
    @Test
    public void crearUsuarioRolNull(){
        try {
            ControlUsuarios.crearUsuario("user1", "pass1", null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(),"Rol no válido");
        }
    }
    
    @Test
    public void crearUsuarioCompeticionesNull() throws InputException{
        ControlUsuarios.crearUsuario("user1", "pass1", RolUsuario.Invitado, null);
        Usuario u = usuarioJpa.findUsuariobyNick("user1");
        assertEquals(u.getAdministradoCollection().size(),0);
        assertEquals(u.getRol().intValue(),RolUsuario.Invitado.ordinal());
        ControlUsuarios.eliminarUsuario(u.getId());
    }
    
    @Test
    public void crearUsuarioConAcceso1Competicion() throws InputException{
        Competicion competicion = ControlCompeticiones.crearCompeticion("comp1", null, null, null, null, null);
        List<Object> listaCompeticiones = new ArrayList();
        listaCompeticiones.add(competicion.getNombre());
        ControlUsuarios.crearUsuario("user1", "pass1", RolUsuario.Gestor, listaCompeticiones);
        Usuario u = usuarioJpa.findUsuariobyNick("user1");
        assertEquals(u.getAdministradoCollection().size(),1);
        assertEquals(u.getAdministradoCollection().iterator().next().getCompeticionId().getId(),competicion.getId());
        assertEquals(u.getRol().intValue(),RolUsuario.Gestor.ordinal());
        ControlUsuarios.eliminarUsuario(u.getId());
        ControlCompeticiones.eliminarCompeticion(competicion.getNombre());
    }
    
    @Test
    public void crearUsuarioConAccesoVariasCompeticiones() throws InputException{
        Competicion competicion = ControlCompeticiones.crearCompeticion("comp1", null, null, null, null, null);
        Competicion competicion2 = ControlCompeticiones.crearCompeticion("comp2", null, null, null, null, null);
        List<Object> listaCompeticiones = new ArrayList();
        listaCompeticiones.add(competicion.getNombre());
        listaCompeticiones.add(competicion2.getNombre());
        ControlUsuarios.crearUsuario("user1", "pass1", RolUsuario.Gestor, listaCompeticiones);
        Usuario u = usuarioJpa.findUsuariobyNick("user1");
        assertEquals(u.getAdministradoCollection().size(),2);
        Iterator<Administrado> i = u.getAdministradoCollection().iterator();
        assertEquals(i.next().getCompeticionId().getId(),competicion.getId());
        assertEquals(i.next().getCompeticionId().getId(),competicion2.getId()); 
        assertEquals(u.getRol().intValue(),RolUsuario.Gestor.ordinal());
        ControlUsuarios.eliminarUsuario(u.getId());
        ControlCompeticiones.eliminarCompeticion(competicion.getNombre());
        ControlCompeticiones.eliminarCompeticion(competicion2.getNombre());
    }
    
    // PRUEBAS SOBRE MODIFICAR USUARIO
    
    @Test
    public void modificarUsuarioUsuarioNull(){
        try {
            ControlUsuarios.modificarUsuario(null, null, null, null);
            fail("Debería haber lanzando InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(),"Usuario no válido");
        }
    }
    
    @Test
    public void modificarUsuarioNombreNull() throws InputException{
        Usuario u = ControlUsuarios.crearUsuario("user1", "pass1", RolUsuario.Invitado, null);
        try {
            ControlUsuarios.modificarUsuario(u.getId(), null, null, null);
            fail("Debería haber lanzando InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(),"Nombre de usuario no válido");
            ControlUsuarios.eliminarUsuario(u.getId());
        }
    }
    
    @Test
    public void modificarUsuarioContraseñaNull() throws InputException{
        Usuario u = ControlUsuarios.crearUsuario("user1", "pass1", RolUsuario.Invitado, null);
        try {
            ControlUsuarios.modificarUsuario(u.getId(), "user2", null, null);
            fail("Debería haber lanzando InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(),"Contraseña no válida");
            ControlUsuarios.eliminarUsuario(u.getId());
        }
    }
    
    @Test
    public void modificarUsuarioRolNull() throws InputException{
        Usuario u = ControlUsuarios.crearUsuario("user1", "pass1", RolUsuario.Invitado, null);
        try {
            ControlUsuarios.modificarUsuario(u.getId(), "user2", "pass2", null);
            fail("Debería haber lanzando InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(),"Rol no válido");
            ControlUsuarios.eliminarUsuario(u.getId());
        }
    }
    
    @Test
    public void modificarUsuarioRolUltimoAdmin() throws InputException{
        try {
            ControlUsuarios.modificarUsuario(usuarioJpa.findUsuariobyNick("admin").getId(), "user2", "pass2", RolUsuario.Invitado);
            fail("Debería haber lanzando InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(),"No se puede eliminar el último administrador del sistema");
        }
    }
    
    @Test
    public void modificarUsuario() throws InputException{
        Usuario u = ControlUsuarios.crearUsuario("user1", "pass1", RolUsuario.Gestor, null);
        Usuario u2 = ControlUsuarios.modificarUsuario(u.getId(), "user2", "pass2", RolUsuario.Invitado);
        assertNotNull(u2);
        assertEquals(u2.getNick(),"user2");
        assertEquals(u2.getPassword(),"pass2");
        assertEquals(u2.getRol().intValue(), RolUsuario.Invitado.ordinal());
        assertNull(usuarioJpa.findUsuariobyNick("user1"));
        ControlUsuarios.eliminarUsuario(u2.getId());
    }
}
