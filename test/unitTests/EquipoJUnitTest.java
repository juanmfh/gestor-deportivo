
package unitTests;

import modelo.entities.Equipo;
import modelo.entities.Grupo;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author JuanM
 */
public class EquipoJUnitTest {    
    
    @Test
    public void crearEquipo(){
        Equipo equipo = new Equipo();
        equipo.setNombre("equipo1");
        Grupo grupo = new Grupo();
        grupo.setId(1);
        equipo.setGrupoId(grupo);
        assertEquals(equipo.getNombre(), "equipo1");
        assertEquals(equipo.getGrupoId().getId(),1,0);
    }
    
    @Test
    public void equipoEquals(){
        Equipo e1 = new Equipo();
        e1.setId(1);
        Equipo e2 = new Equipo();
        e2.setId(2);
        assertFalse(e1.equals(e2));
    }
    
    @Test
    public void equipoEquals2(){
        Equipo e1 = new Equipo();
        e1.setId(1);
        Equipo e2 = new Equipo();
        e2.setId(1);
        assertTrue(e1.equals(e2));
    }
    
    @Test
    public void equipoToString(){
        Equipo e = new Equipo();
        e.setId(1);
        assertEquals(e.toString(),"entities.Equipo[ id=1 ]");
    }
    
}
