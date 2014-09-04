package unitTests;

import modelo.entities.Grupo;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author JuanM
 */
public class GrupoJUnitTest {
    
    @Test
    public void crearGrupo(){
        Grupo g = new Grupo();
        g.setNombre("grupo1");
        assertEquals(g.getNombre(), "grupo1");
    }
    
    @Test
    public void crearSubGrupo(){
        Grupo g = new Grupo();
        g.setId(1);
        Grupo subgrupo = new Grupo();
        subgrupo.setGrupoId(g);
        assertEquals(subgrupo.getGrupoId().getId(),g.getId());
    }
    
    @Test
    public void grupoEquals(){
        Grupo g1 = new Grupo();
        g1.setId(1);
        Grupo g2 = new Grupo();
        g2.setId(2);
        assertFalse(g1.equals(g2));
    }
    
    @Test
    public void grupoEquals2(){
        Grupo g1 = new Grupo();
        g1.setId(1);
        Grupo g2 = new Grupo();
        g2.setId(1);
        assertTrue(g1.equals(g2));
    }
    
    @Test
    public void grupoToString(){
        Grupo g = new Grupo();
        g.setId(1);
        assertEquals(g.toString(),"entities.Grupo[ id=1 ]");
    }
    
}
