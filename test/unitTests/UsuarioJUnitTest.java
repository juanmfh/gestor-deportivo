
package unitTests;

import modelo.entities.RolUsuario;
import modelo.entities.Usuario;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author JuanM
 */
public class UsuarioJUnitTest {

    @Test
    public void crearUsuario(){
        Usuario usuario = new Usuario();
        usuario.setNick("nick1");
        usuario.setPassword("password1");
        usuario.setRol(RolUsuario.Administrador.ordinal());
        
        assertEquals(usuario.getNick(), "nick1");
        assertEquals(usuario.getPassword(), "password1");
        assertEquals(usuario.getRol(), RolUsuario.Administrador.ordinal(),0);
    }
    
    
    @Test
    public void usuarioEquals(){
        Usuario u1 = new Usuario();
        u1.setId(1);
        Usuario u2 = new Usuario();
        u2.setId(2);
        assertFalse(u1.equals(u2));
    }
    
    @Test
    public void registroEquals2(){
        Usuario u1 = new Usuario();
        u1.setId(1);
        Usuario u2 = new Usuario();
        u2.setId(1);
        assertTrue(u1.equals(u2));
    }
    
    @Test
    public void registroToString(){
        Usuario u = new Usuario();
        u.setId(1);
        assertEquals(u.toString(),"entities.Usuario[ id=1 ]");
    }
    
    
}
