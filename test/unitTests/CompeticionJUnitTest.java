package unitTests;

import java.util.Date;
import modelo.entities.Competicion;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author JuanM
 */
public class CompeticionJUnitTest { 
    
    // Pruebas unitarias sobre la entidad Competicion
    
    @Test
    public void crearCompeticion(){
        Competicion c = new Competicion();
        c.setNombre("comp1");
        c.setCiudad("ciudad1");
        Date d = new Date("10/12/2010");
        Date d2 = new Date("08/01/2014");
        c.setFechainicio(d);
        c.setFechafin(d2);
        c.setId(1);
        c.setImagen("path");
        c.setOrganizador("org1");
        c.setPais("pais");
        assertNotNull(c);
        assertEquals(c.getNombre(),"comp1");
        assertEquals(c.getCiudad(),"ciudad1");
        assertEquals(c.getFechainicio(),d);
        assertEquals(c.getFechafin(),d2);
        assertEquals(c.getId(),1,0);
        assertEquals(c.getImagen(),"path");
        assertEquals(c.getOrganizador(), "org1");
        assertEquals(c.getPais(),"pais");
    }
    
    @Test
    public void competicionEquals(){
        Competicion c1 = new Competicion();
        c1.setId(1);
        Competicion c2 = new Competicion();
        c2.setId(2);
        assertFalse(c1.equals(c2));
    }
    
    @Test
    public void competicionEquals2(){
        Competicion c1 = new Competicion();
        c1.setId(1);
        Competicion c2 = new Competicion();
        assertFalse(c1.equals(c2));
    }
    
    @Test
    public void competicionEquals3(){
        Competicion c1 = new Competicion();
        Competicion c2 = new Competicion();
        c2.setId(1);
        assertFalse(c1.equals(c2));
    }
    
    @Test
    public void competicionEquals4(){
        Competicion c1 = new Competicion();
        c1.setId(1);
        Competicion c2 = new Competicion();
        c2.setId(1);
        assertTrue(c1.equals(c2));
    }
    
    @Test
    public void competicionToString(){
        Competicion c = new Competicion();
        assertEquals(c.toString(),"entities.Competicion[ id=null ]");
    }
    
    @Test
    public void competicionToString2(){
        Competicion c = new Competicion();
        c.setId(1);
        assertEquals(c.toString(),"entities.Competicion[ id=1 ]");
    }
}
