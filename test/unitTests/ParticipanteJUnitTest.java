
package unitTests;

import modelo.entities.Equipo;
import modelo.entities.Grupo;
import modelo.entities.Participante;
import modelo.entities.Prueba;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author JuanM
 */
public class ParticipanteJUnitTest {
    
    @Test
    public void crearParticipante() {
        Participante participante = new Participante();
        participante.setNombre("nombre1");
        participante.setApellidos("apellidos1");
        participante.setDorsal(1);
        participante.setEdad(30);
        participante.setSexo(0);
        Grupo g = new Grupo();
        g.setId(1);
        participante.setGrupoId(g);
        Equipo e = new Equipo();
        e.setId(1);
        participante.setEquipoId(e);
        Prueba p = new Prueba();
        p.setId(1);
        participante.setPruebaasignada(p);
        
        assertEquals(participante.getNombre(),"nombre1");
        assertEquals(participante.getApellidos(),"apellidos1");
        assertEquals(participante.getDorsal(),1);
        assertEquals(participante.getEdad(),30,0);
        assertEquals(participante.getSexo(),0,0);
        assertEquals(participante.getGrupoId().getId(),1,0);
        assertEquals(participante.getEquipoId().getId(),1,0);
        assertEquals(participante.getPruebaasignada().getId(),1,0);
    }
    
    @Test
    public void participanteEquals(){
        Participante p1 = new Participante();
        p1.setId(1);
        Participante p2 = new Participante();
        p2.setId(2);
        assertFalse(p1.equals(p2));
    }
    
    @Test
    public void participanteEquals2(){
        Participante p1 = new Participante();
        p1.setId(1);
        Participante p2 = new Participante();
        p2.setId(1);
        assertTrue(p1.equals(p2));
    }
    
    @Test
    public void participanteToString(){
        Participante p = new Participante();
        p.setId(1);
        assertEquals(p.toString(),"entities.Participante[ id=1 ]");
    }
    
}
