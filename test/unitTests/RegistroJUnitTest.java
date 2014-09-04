
package unitTests;

import modelo.entities.Participante;
import modelo.entities.Prueba;
import modelo.entities.Registro;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author JuanM
 */
public class RegistroJUnitTest {
    
    @Test
    public void crearRegistroParticipante() {
        
        Registro registro = new Registro();
        Participante participante = new Participante();
        participante.setId(1);
        registro.setParticipanteId(participante);
        Prueba prueba = new Prueba();
        prueba.setId(1);
        registro.setPruebaId(prueba);
        registro.setSorteo(0);
        registro.setNum(15.032);
        
        assertEquals(registro.getParticipanteId(), participante);
        assertEquals(registro.getPruebaId(),prueba);
        assertEquals(registro.getNum(), 15,032);
        assertEquals(registro.getSorteo(),0,0);
    }
    
    
    @Test
    public void registroEquals(){
        Registro r1 = new Registro();
        r1.setId(1);
        Registro r2 = new Registro();
        r2.setId(2);
        assertFalse(r1.equals(r2));
    }
    
    @Test
    public void registroEquals2(){
        Registro r1 = new Registro();
        r1.setId(1);
        Registro r2 = new Registro();
        r2.setId(1);
        assertTrue(r1.equals(r2));
    }
    
    @Test
    public void registroToString(){
        Registro p = new Registro();
        p.setId(1);
        assertEquals(p.toString(),"entities.Registro[ id=1 ]");
    }
    
    
    
}
