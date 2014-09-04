package unitTests;

import modelo.entities.Prueba;
import modelo.entities.TipoPrueba;
import modelo.entities.TipoResultado;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author JuanM
 */
public class PruebaJUnitTest {
    
    // Pruebas unitarias sobre la entidad Prueba
    
    @Test
    public void crearPrueba(){
        Prueba prueba = new Prueba();
        prueba.setNombre("prueba1");
        prueba.setTipo(TipoPrueba.Individual.toString());
        prueba.setTiporesultado(TipoResultado.Distancia.toString());
        assertEquals(prueba.getNombre(),"prueba1");
        assertEquals(prueba.getTipo(),TipoPrueba.Individual.toString());
        assertEquals(prueba.getTiporesultado(),TipoResultado.Distancia.toString());
    }
    
    @Test
    public void pruebaEquals(){
        Prueba p1 = new Prueba();
        p1.setId(1);
        Prueba p2 = new Prueba();
        p2.setId(2);
        assertFalse(p1.equals(p2));
    }
    
    @Test
    public void pruebaEquals2(){
        Prueba p1 = new Prueba();
        p1.setId(1);
        Prueba p2 = new Prueba();
        p2.setId(1);
        assertTrue(p1.equals(p2));
    }
    
    @Test
    public void pruebaToString(){
        Prueba p = new Prueba();
        p.setId(1);
        assertEquals(p.toString(),"entities.Prueba[ id=1 ]");
    }

}
