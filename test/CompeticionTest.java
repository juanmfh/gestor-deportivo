
import controlador.ControlCompeticiones;
import controlador.InputException;
import dao.CompeticionJpa;
import dao.exceptions.IllegalOrphanException;
import dao.exceptions.NonexistentEntityException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import modelo.Competicion;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import vista.VistaCompeticion;

/**
 *
 * @author JuanM
 */
public class CompeticionTest {
    
    private static CompeticionJpa competicionJpa;
    private static VistaCompeticion vista;     // Mock
    private static ControlCompeticiones ctrCrearcompeticion;

    // PRUEBAS SOBRE CREAR COMPETICION
    
    @BeforeClass
    public static void ini() {
        vista = Mockito.mock(VistaCompeticion.class);
        ctrCrearcompeticion = new ControlCompeticiones(vista);
        competicionJpa = new CompeticionJpa();
        
        // Elimina las competiciones creadas en las pruebas
        Competicion c = competicionJpa.findCompeticionByName("comp1");
        try {
            if (c != null) {
                competicionJpa.destroy(c.getId());
            }
            c = competicionJpa.findCompeticionByName("comp2");
            if (c != null) {
                competicionJpa.destroy(c.getId());
            }
        } catch (IllegalOrphanException | NonexistentEntityException ex) {
            Logger.getLogger(CompeticionTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Crea una competicion con todos los atributos null
    @Test(expected = InputException.class)
    public void crearCompeticionNombreNull() throws InputException {
        Competicion c = ctrCrearcompeticion.crearCompeticion(null, null, null, null, null, null);
    }

    // Crea una competicion con un nombre de longitud 0.
    @Test(expected = InputException.class)
    public void crearCompeticionNombreVacio() throws InputException {
        Competicion c = ctrCrearcompeticion.crearCompeticion("", null, null, null, null, null);
    }

    // Crea una competicion solo con el nombre
    @Test
    public void crearCompeticionNombre() throws InputException {
        String nombreCompeticion = "comp1";
        Competicion c = ctrCrearcompeticion.crearCompeticion(nombreCompeticion, null, null, null, null, null);
        Assert.assertNotNull(c);
        Assert.assertEquals(c.getNombre(), nombreCompeticion);
    }

    // Crea una competicion solo con el nombre 
    @Test
    public void crearCompeticion() throws InputException {
        String nombreCompeticion = "comp2";
        String lugar = "lugar1";
        Date fechaInicio = new Date(12 / 12 / 2010);
        Date fechaFin = new Date(12 / 07 / 2010);
        String nombreImagen = "logo1";
        String organizador = "organizador1";
        Competicion c = ctrCrearcompeticion.crearCompeticion(nombreCompeticion, lugar, fechaInicio, fechaFin, nombreImagen, organizador);

        Assert.assertNotNull(c);
        Assert.assertEquals(c.getNombre(), nombreCompeticion);
        Assert.assertEquals(c.getCiudad(), lugar);
        Assert.assertEquals(c.getFechafin(), fechaFin);
        Assert.assertEquals(c.getFechainicio(), fechaInicio);
        Assert.assertEquals(c.getOrganizador(), organizador);
        Assert.assertEquals(c.getImagen(), nombreImagen);
    }

    // Crea una competicion con un nombre ocupado
    @Test
    public void crearCompeticionNombreOcupado() {
        String nombreCompeticion = "comp1";
        Competicion c = null;
        try {
            c = ctrCrearcompeticion.crearCompeticion(nombreCompeticion, null, null, null, null, null);
        } catch (InputException ex) {
            Assert.assertEquals("Nombre de competición no válido", ex.getMessage());
        } finally {
            Assert.assertNull(c);
        }
    }
    
    // PRUEBAS SOBRE MODIFICAR COMPETICION
    
    @Test
    public void modificarCompeticion(){
        
    }
}
