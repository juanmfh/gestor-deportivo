package testcases;

import controlador.InputException;
import modelo.entities.TipoPrueba;
import modelo.entities.TipoResultado;
import modelo.logicaNegocio.CompeticionService;
import modelo.logicaNegocio.PruebaService;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import modelo.dao.CompeticionJpa;
import modelo.entities.Competicion;
import modelo.entities.Prueba;
import modelo.dao.GrupoJpa;
import modelo.dao.ParticipanteJpa;
import modelo.dao.PruebaJpa;
import modelo.dao.RegistroJpa;
import modelo.logicaNegocio.GrupoService;
import modelo.logicaNegocio.ParticipanteService;
import modelo.logicaNegocio.RegistroService;
import org.junit.AfterClass;
import static org.junit.Assert.fail;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 *
 * @author JuanM
 */
@RunWith(JUnit4.class)
public class PruebasTest {
    
    private static PruebaJpa pruebaJpa;
    private static CompeticionJpa competicionJpa;

    @BeforeClass
    public static void setUp() throws InputException {
        pruebaJpa = new PruebaJpa();
        competicionJpa = new CompeticionJpa();
    }

    @AfterClass
    public static void tearDown() {
        // Elimina las competiciones que han sido creadas en las pruebas
        for (int i = 1; i <= 25; i++) {
            Competicion c = competicionJpa.findCompeticionByName("comp" + i);
            if (c != null) {
                try {
                    CompeticionService.eliminarCompeticion(c.getNombre());
                } catch (InputException ex) {
                    Logger.getLogger(CompeticionTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    // PRUEBAS SOBRE CREAR PRUEBA
    // Crea una prueba en una competicion
    @Test
    public void crearPrueba() throws InputException {
        Competicion c = CompeticionService.crearCompeticion("comp1", null, null, null, null, null);
        PruebaService.crearPrueba(c, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Numerica.toString());
        Assert.assertNotNull(pruebaJpa.findPruebaByNombreCompeticion("prueba1", c.getId()));
    }

    // Crea una prueba con todos los valores a null
    @Test
    public void crearPruebaNull() throws InputException {

        Competicion c = CompeticionService.crearCompeticion("comp2", null, null, null, null, null);
        Prueba p = null;
        try {
            p = PruebaService.crearPrueba(c, null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            Assert.assertEquals("Nombre de prueba no válido", ex.getMessage());
        } finally {
            Assert.assertNull(p);
        }
    }

    // Crea una prueba con un tipo de Prueba no válido
    @Test
    public void crearPruebaTipoNoValido() throws InputException {

        Competicion c = CompeticionService.crearCompeticion("comp3", null, null, null, null, null);
        Prueba p = null;
        try {
            p = PruebaService.crearPrueba(c, "prueba1", null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            Assert.assertEquals("Tipo de prueba no válido", ex.getMessage());
        } finally {
            Assert.assertNull(p);
        }
    }

    // Crea una prueba con un tipo de Resultado no válido
    @Test
    public void crearPruebaTipoResultadoNoValido() throws InputException {

        Competicion c = CompeticionService.crearCompeticion("comp4", null, null, null, null, null);
        Prueba p = null;
        try {
            p = PruebaService.crearPrueba(c, "prueba1", TipoPrueba.Equipo.toString(), null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            Assert.assertEquals("Tipo de resultado no válido", ex.getMessage());
        } finally {
            Assert.assertNull(p);
        }
    }

    // Crea una prueba con un nombre no válido
    @Test
    public void crearPruebaNombreNoValido() throws InputException {

        Competicion c = CompeticionService.crearCompeticion("comp5", null, null, null, null, null);
        Prueba p = null;
        try {
            p = PruebaService.crearPrueba(c, "", TipoPrueba.Equipo.toString(), TipoResultado.Distancia.toString());
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            Assert.assertEquals("Nombre de prueba no válido", ex.getMessage());
        } finally {
            Assert.assertNull(p);
        }
    }

    // Crea una prueba con un nombre ocupado por otra prueba en la misma competición
    @Test
    public void crearPruebaNombreOcupado() throws InputException {

        Competicion c = CompeticionService.crearCompeticion("comp6", null, null, null, null, null);
        Prueba p = null;
        Prueba p2 = null;
        try {
            p = PruebaService.crearPrueba(c, "prueba1", TipoPrueba.Equipo.toString(), TipoResultado.Distancia.toString());
            p2 = PruebaService.crearPrueba(c, "prueba1", TipoPrueba.Equipo.toString(), TipoResultado.Distancia.toString());
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            Assert.assertEquals("Nombre de prueba ocupado", ex.getMessage());
        } finally {
            Assert.assertNotNull(p);
            Assert.assertNull(p2);
            List<Prueba> pruebas = pruebaJpa.findPruebasByCompeticon(c);
            Assert.assertEquals(1, pruebas.size());
        }
    }

    // Crea una prueba en una competicion y una prueba con el mismo nombre en otra competición
    @Test
    public void crearMismaPruebaDosCompeticiones() throws InputException {

        Competicion c = CompeticionService.crearCompeticion("comp7", null, null, null, null, null);
        Competicion c2 = CompeticionService.crearCompeticion("comp8", null, null, null, null, null);
        Prueba p = PruebaService.crearPrueba(c, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        Prueba p2 = PruebaService.crearPrueba(c2, "prueba1", TipoPrueba.Equipo.toString(), TipoResultado.Tiempo.toString());

        Assert.assertNotNull(p);
        Assert.assertNotNull(p2);
        List<Prueba> pruebas = pruebaJpa.findPruebasByCompeticon(c);
        Assert.assertEquals(1, pruebas.size());
        Assert.assertEquals("prueba1", pruebas.get(0).getNombre());
        Assert.assertEquals(TipoPrueba.Individual.toString(), pruebas.get(0).getTipo());
        Assert.assertEquals(TipoResultado.Distancia.toString(), pruebas.get(0).getTiporesultado());
        pruebas = pruebaJpa.findPruebasByCompeticon(c2);
        Assert.assertEquals(1, pruebas.size());
        Assert.assertEquals("prueba1", pruebas.get(0).getNombre());
        Assert.assertEquals(TipoPrueba.Equipo.toString(), pruebas.get(0).getTipo());
        Assert.assertEquals(TipoResultado.Tiempo.toString(), pruebas.get(0).getTiporesultado());
    }

    // PRUEBAS SOBRE MODIFICAR PRUEBAS
    // Intenta modificar una prueba que no existe
    @Test
    public void modificarPruebaNoExiste() throws InputException {
        Prueba p = null;
        Competicion c = CompeticionService.crearCompeticion("comp14", null, null, null, null, null);
        try {
            p = PruebaService.modificarPrueba(-1, c.getId(), null, TipoResultado.Tiempo, TipoPrueba.Equipo);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            Assert.assertEquals("Prueba no encontrada", ex.getMessage());
        } finally {
            Assert.assertNull(p);
        }
    }

    // Intenta modificar una prueba a null
    @Test
    public void modificarPruebaNull() throws InputException {
        Prueba p = null;
        try {
            p = PruebaService.modificarPrueba(null, null, null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            Assert.assertEquals("Prueba no válida", ex.getMessage());
        } finally {
            Assert.assertNull(p);
        }
    }

    @Test
    public void modificarPruebaNombre() throws InputException {
        Competicion c = CompeticionService.crearCompeticion("comp9", null, null, null, null, null);
        Prueba p = PruebaService.crearPrueba(c, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        Prueba p2 = PruebaService.modificarPrueba(p.getId(), c.getId(), "prueba2", TipoResultado.Distancia, TipoPrueba.Individual);

        Assert.assertEquals(p.getId(), p2.getId());
        p = pruebaJpa.findPruebaByNombreCompeticion("prueba2", c.getId());
        Assert.assertEquals("prueba2", p.getNombre());
        Assert.assertEquals(TipoResultado.Distancia.toString(), p.getTiporesultado());
        Assert.assertEquals(TipoPrueba.Individual.toString(), p.getTipo());
    }

    @Test
    public void modificarPruebaTipo() throws InputException {
        Competicion c = CompeticionService.crearCompeticion("comp10", null, null, null, null, null);
        Prueba p = PruebaService.crearPrueba(c, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        Prueba p2 = PruebaService.modificarPrueba(p.getId(), c.getId(), "prueba1", TipoResultado.Distancia, TipoPrueba.Equipo);
        Assert.assertEquals(p.getId(), p2.getId());
        p = pruebaJpa.findPruebaByNombreCompeticion("prueba1", c.getId());
        Assert.assertEquals("prueba1", p.getNombre());
        Assert.assertEquals(TipoResultado.Distancia.toString(), p.getTiporesultado());
        Assert.assertEquals(TipoPrueba.Equipo.toString(), p.getTipo());
    }

    @Test
    public void modificarPruebaTipoResultado() throws InputException {
        Competicion c = CompeticionService.crearCompeticion("comp11", null, null, null, null, null);
        Prueba p = PruebaService.crearPrueba(c, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        Prueba p2 = PruebaService.modificarPrueba(p.getId(), c.getId(), "prueba1", TipoResultado.Tiempo, TipoPrueba.Individual);
        Assert.assertEquals(p.getId(), p2.getId());
        p = pruebaJpa.findPruebaByNombreCompeticion("prueba1", c.getId());
        Assert.assertEquals("prueba1", p.getNombre());
        Assert.assertEquals(TipoResultado.Tiempo.toString(), p.getTiporesultado());
        Assert.assertEquals(TipoPrueba.Individual.toString(), p.getTipo());
    }

    @Test
    public void modificarPruebaNombreOcupado() throws InputException {
        Competicion c = CompeticionService.crearCompeticion("comp12", null, null, null, null, null);
        Prueba p = PruebaService.crearPrueba(c, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        PruebaService.crearPrueba(c, "prueba2", TipoPrueba.Equipo.toString(), TipoResultado.Distancia.toString());
        Prueba p2 = null;
        try {
            p2 = PruebaService.modificarPrueba(p.getId(), c.getId(), "prueba2", TipoResultado.Distancia, TipoPrueba.Individual);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            Assert.assertEquals("Nombre de prueba ocupado", ex.getMessage());
        } finally {
            Assert.assertNull(p2);
        }
    }

    @Test
    public void modificarPruebaCompeticionNoValida() throws InputException {
        Competicion c = CompeticionService.crearCompeticion("comp15", null, null, null, null, null);
        Prueba p = PruebaService.crearPrueba(c, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        Prueba p2 = null;
        try {
            p2 = PruebaService.modificarPrueba(p.getId(), null, "prueba1", TipoResultado.Tiempo, TipoPrueba.Equipo);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            Assert.assertEquals("Competición no válida", ex.getMessage());
        } finally {
            Assert.assertNull(p2);
        }
    }

    @Test
    public void modificarPruebaNoRegistradaCompeticion() throws InputException {
        Competicion c = CompeticionService.crearCompeticion("comp13", null, null, null, null, null);
        Prueba p = PruebaService.crearPrueba(c, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        Prueba p2 = null;
        try {
            p2 = PruebaService.modificarPrueba(p.getId(), -1, "prueba1", TipoResultado.Tiempo, TipoPrueba.Equipo);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            Assert.assertEquals("Prueba no registrada en la competición", ex.getMessage());
        } finally {
            Assert.assertNull(p2);
        }
    }

    @Test
    public void modificarPruebaTipoNoValido() throws InputException {
        Competicion c = CompeticionService.crearCompeticion("comp16", null, null, null, null, null);
        Prueba p = PruebaService.crearPrueba(c, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        Prueba p2 = null;
        try {
            p2 = PruebaService.modificarPrueba(p.getId(), c.getId(), "prueba1", TipoResultado.Tiempo, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            Assert.assertEquals("Tipo de prueba no válido", ex.getMessage());
        } finally {
            Assert.assertNull(p2);
        }
    }

    @Test
    public void modificarPruebaTipoResultadoNoValido() throws InputException {
        Competicion c = CompeticionService.crearCompeticion("comp17", null, null, null, null, null);
        Prueba p = PruebaService.crearPrueba(c, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        Prueba p2 = null;
        try {
            p2 = PruebaService.modificarPrueba(p.getId(), c.getId(), "prueba1", null, TipoPrueba.Individual);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            Assert.assertEquals("Tipo de resultado no válido", ex.getMessage());
        } finally {
            Assert.assertNull(p2);
        }
    }

    @Test
    public void modificarPruebaConRegistros() throws InputException {
        Competicion c = CompeticionService.crearCompeticion("comp18", null, null, null, null, null);
        Prueba p = PruebaService.crearPrueba(c, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        GrupoService.crearGrupo(c, "a", null);
        ParticipanteService.crearParticipante(c, "nombre", "apellidos", 1, "a", null, null, null, null);
        RegistroService.crearRegistro(c, 1, "prueba1", null, false, 12.0, null, null);
        Prueba p2 = null;
        try {
            p2 = PruebaService.modificarPrueba(p.getId(), c.getId(), "prueba1", TipoResultado.Numerica, TipoPrueba.Individual);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            Assert.assertEquals("No se puede modificar una prueba con registros asociados", ex.getMessage());
        } finally {
            Assert.assertNull(p2);
        }
    }

    // PRUEBAS SOBRE ELIMINAR PRUEBAS
    
    @Test
    public void eliminarPruebaNoExiste() throws InputException {
        
        Competicion c = CompeticionService.crearCompeticion("comp19", null, null, null, null, null);
        try {
            PruebaService.eliminarPrueba(-1, c.getId());
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            Assert.assertEquals("Prueba no encontrada", ex.getMessage());
        }
    }
    
    @Test
    public void eliminarPruebaNull() throws InputException{
        
        Competicion c = CompeticionService.crearCompeticion("comp20", null, null, null, null, null);
        try {
            PruebaService.eliminarPrueba(null, c.getId());
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            Assert.assertEquals("Identificador de prueba no válido", ex.getMessage());
        }
    }
    
    @Test
    public void eliminarPruebaCompeticionNull() throws InputException {
        
        Competicion c = CompeticionService.crearCompeticion("comp21", null, null, null, null, null);
        Prueba p = PruebaService.crearPrueba(c, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        try {
            PruebaService.eliminarPrueba(p.getId(), null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            Assert.assertEquals("Identificador de competición no válido", ex.getMessage());
        }
    }
    
    @Test
    public void eliminarPruebaCompeticionNoValida() throws InputException {
        
        Competicion c = CompeticionService.crearCompeticion("comp22", null, null, null, null, null);
        Prueba p = PruebaService.crearPrueba(c, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        try {
            PruebaService.eliminarPrueba(p.getId(), -1);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            Assert.assertEquals("Prueba no registrada en Competición", ex.getMessage());
        }
    }
    
    @Test
    public void eliminarPruebaExistente() throws InputException{
    
        Competicion c = CompeticionService.crearCompeticion("comp23", null, null, null, null, null);
        Prueba p = PruebaService.crearPrueba(c, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        PruebaService.eliminarPrueba(p.getId(), c.getId());
        
        p = pruebaJpa.findPruebaByNombreCompeticion("prueba1", c.getId());
        Assert.assertNull(p);
    }
    
    @Test
    public void eliminarPruebaExistenteMismoNombreDosCompeticiones() throws InputException{
    
        Competicion c = CompeticionService.crearCompeticion("comp24", null, null, null, null, null);
        Competicion c2 = CompeticionService.crearCompeticion("comp25", null, null, null, null, null);
        Prueba p = PruebaService.crearPrueba(c, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        PruebaService.crearPrueba(c2, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        PruebaService.eliminarPrueba(p.getId(), c.getId());
        
        p = pruebaJpa.findPruebaByNombreCompeticion("prueba1", c.getId());
        Assert.assertNull(p);
        Prueba p2 = pruebaJpa.findPruebaByNombreCompeticion("prueba1", c2.getId());
        Assert.assertNotNull(p2);
    }
    
}
