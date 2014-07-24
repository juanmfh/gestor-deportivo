package pruebasUnitarias;

import controlador.ControlCompeticiones;
import controlador.ControlGrupos;
import controlador.ControlParticipantes;
import controlador.ControlPruebas;
import controlador.ControlRegistros;
import controlador.InputException;
import controlador.TipoPrueba;
import controlador.TipoResultado;
import dao.CompeticionJpa;
import dao.PruebaJpa;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import modelo.Competicion;
import modelo.Prueba;
import org.junit.AfterClass;
import static org.junit.Assert.fail;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import vista.GeneralTab;
import vista.VistaCompeticion;

/**
 *
 * @author JuanM
 */
@RunWith(JUnit4.class)
public class PruebasTest {
    
    private static ControlCompeticiones ctrCompeticiones;
    private static VistaCompeticion vistaCompeticiones;
    private static ControlPruebas ctrPruebas;
    private static GeneralTab vistaPruebas;
    private static PruebaJpa pruebaJpa;
    private static CompeticionJpa competicionJpa;

    @BeforeClass
    public static void setUp() throws InputException {
        vistaCompeticiones = Mockito.mock(VistaCompeticion.class);
        ctrCompeticiones = new ControlCompeticiones(vistaCompeticiones);
        vistaPruebas = Mockito.mock(GeneralTab.class);
        ctrPruebas = new ControlPruebas(vistaPruebas);
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
                    ControlCompeticiones.eliminarCompeticion(c.getNombre());
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
        Competicion c = ctrCompeticiones.crearCompeticion("comp1", null, null, null, null, null);
        ControlPruebas.crearPrueba(c, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Numerica.toString());
        Assert.assertNotNull(pruebaJpa.findPruebaByNombreCompeticion("prueba1", c.getId()));
    }

    // Crea una prueba con todos los valores a null
    @Test
    public void crearPruebaNull() throws InputException {

        Competicion c = ctrCompeticiones.crearCompeticion("comp2", null, null, null, null, null);
        Prueba p = null;
        try {
            p = ControlPruebas.crearPrueba(c, null, null, null);
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

        Competicion c = ctrCompeticiones.crearCompeticion("comp3", null, null, null, null, null);
        Prueba p = null;
        try {
            p = ControlPruebas.crearPrueba(c, "prueba1", null, null);
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

        Competicion c = ctrCompeticiones.crearCompeticion("comp4", null, null, null, null, null);
        Prueba p = null;
        try {
            p = ControlPruebas.crearPrueba(c, "prueba1", TipoPrueba.Equipo.toString(), null);
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

        Competicion c = ctrCompeticiones.crearCompeticion("comp5", null, null, null, null, null);
        Prueba p = null;
        try {
            p = ControlPruebas.crearPrueba(c, "", TipoPrueba.Equipo.toString(), TipoResultado.Distancia.toString());
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

        Competicion c = ctrCompeticiones.crearCompeticion("comp6", null, null, null, null, null);
        Prueba p = null;
        Prueba p2 = null;
        try {
            p = ControlPruebas.crearPrueba(c, "prueba1", TipoPrueba.Equipo.toString(), TipoResultado.Distancia.toString());
            p2 = ControlPruebas.crearPrueba(c, "prueba1", TipoPrueba.Equipo.toString(), TipoResultado.Distancia.toString());
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

        Competicion c = ctrCompeticiones.crearCompeticion("comp7", null, null, null, null, null);
        Competicion c2 = ctrCompeticiones.crearCompeticion("comp8", null, null, null, null, null);
        Prueba p = ControlPruebas.crearPrueba(c, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        Prueba p2 = ControlPruebas.crearPrueba(c2, "prueba1", TipoPrueba.Equipo.toString(), TipoResultado.Tiempo.toString());

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
        Competicion c = ctrCompeticiones.crearCompeticion("comp14", null, null, null, null, null);
        try {
            p = ctrPruebas.modificarPrueba(-1, c.getId(), null, TipoResultado.Tiempo, TipoPrueba.Equipo);
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
            p = ctrPruebas.modificarPrueba(null, null, null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            Assert.assertEquals("Prueba no válida", ex.getMessage());
        } finally {
            Assert.assertNull(p);
        }
    }

    @Test
    public void modificarPruebaNombre() throws InputException {
        Competicion c = ctrCompeticiones.crearCompeticion("comp9", null, null, null, null, null);
        Prueba p = ControlPruebas.crearPrueba(c, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        Prueba p2 = ctrPruebas.modificarPrueba(p.getId(), c.getId(), "prueba2", TipoResultado.Distancia, TipoPrueba.Individual);

        Assert.assertEquals(p.getId(), p2.getId());
        p = pruebaJpa.findPruebaByNombreCompeticion("prueba2", c.getId());
        Assert.assertEquals("prueba2", p.getNombre());
        Assert.assertEquals(TipoResultado.Distancia.toString(), p.getTiporesultado());
        Assert.assertEquals(TipoPrueba.Individual.toString(), p.getTipo());
    }

    @Test
    public void modificarPruebaTipo() throws InputException {
        Competicion c = ctrCompeticiones.crearCompeticion("comp10", null, null, null, null, null);
        Prueba p = ControlPruebas.crearPrueba(c, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        Prueba p2 = ctrPruebas.modificarPrueba(p.getId(), c.getId(), "prueba1", TipoResultado.Distancia, TipoPrueba.Equipo);
        Assert.assertEquals(p.getId(), p2.getId());
        p = pruebaJpa.findPruebaByNombreCompeticion("prueba1", c.getId());
        Assert.assertEquals("prueba1", p.getNombre());
        Assert.assertEquals(TipoResultado.Distancia.toString(), p.getTiporesultado());
        Assert.assertEquals(TipoPrueba.Equipo.toString(), p.getTipo());
    }

    @Test
    public void modificarPruebaTipoResultado() throws InputException {
        Competicion c = ctrCompeticiones.crearCompeticion("comp11", null, null, null, null, null);
        Prueba p = ControlPruebas.crearPrueba(c, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        Prueba p2 = ctrPruebas.modificarPrueba(p.getId(), c.getId(), "prueba1", TipoResultado.Tiempo, TipoPrueba.Individual);
        Assert.assertEquals(p.getId(), p2.getId());
        p = pruebaJpa.findPruebaByNombreCompeticion("prueba1", c.getId());
        Assert.assertEquals("prueba1", p.getNombre());
        Assert.assertEquals(TipoResultado.Tiempo.toString(), p.getTiporesultado());
        Assert.assertEquals(TipoPrueba.Individual.toString(), p.getTipo());
    }

    @Test
    public void modificarPruebaNombreOcupado() throws InputException {
        Competicion c = ctrCompeticiones.crearCompeticion("comp12", null, null, null, null, null);
        Prueba p = ControlPruebas.crearPrueba(c, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        ControlPruebas.crearPrueba(c, "prueba2", TipoPrueba.Equipo.toString(), TipoResultado.Distancia.toString());
        Prueba p2 = null;
        try {
            p2 = ctrPruebas.modificarPrueba(p.getId(), c.getId(), "prueba2", TipoResultado.Distancia, TipoPrueba.Individual);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            Assert.assertEquals("Nombre de prueba ocupado", ex.getMessage());
        } finally {
            Assert.assertNull(p2);
        }
    }

    @Test
    public void modificarPruebaCompeticionNoValida() throws InputException {
        Competicion c = ctrCompeticiones.crearCompeticion("comp15", null, null, null, null, null);
        Prueba p = ControlPruebas.crearPrueba(c, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        Prueba p2 = null;
        try {
            p2 = ctrPruebas.modificarPrueba(p.getId(), null, "prueba1", TipoResultado.Tiempo, TipoPrueba.Equipo);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            Assert.assertEquals("Competición no válida", ex.getMessage());
        } finally {
            Assert.assertNull(p2);
        }
    }

    @Test
    public void modificarPruebaNoRegistradaCompeticion() throws InputException {
        Competicion c = ctrCompeticiones.crearCompeticion("comp13", null, null, null, null, null);
        Prueba p = ControlPruebas.crearPrueba(c, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        Prueba p2 = null;
        try {
            p2 = ctrPruebas.modificarPrueba(p.getId(), -1, "prueba1", TipoResultado.Tiempo, TipoPrueba.Equipo);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            Assert.assertEquals("Prueba no registrada en la competición", ex.getMessage());
        } finally {
            Assert.assertNull(p2);
        }
    }

    @Test
    public void modificarPruebaTipoNoValido() throws InputException {
        Competicion c = ctrCompeticiones.crearCompeticion("comp16", null, null, null, null, null);
        Prueba p = ControlPruebas.crearPrueba(c, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        Prueba p2 = null;
        try {
            p2 = ctrPruebas.modificarPrueba(p.getId(), c.getId(), "prueba1", TipoResultado.Tiempo, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            Assert.assertEquals("Tipo de prueba no válido", ex.getMessage());
        } finally {
            Assert.assertNull(p2);
        }
    }

    @Test
    public void modificarPruebaTipoResultadoNoValido() throws InputException {
        Competicion c = ctrCompeticiones.crearCompeticion("comp17", null, null, null, null, null);
        Prueba p = ControlPruebas.crearPrueba(c, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        Prueba p2 = null;
        try {
            p2 = ctrPruebas.modificarPrueba(p.getId(), c.getId(), "prueba1", null, TipoPrueba.Individual);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            Assert.assertEquals("Tipo de resultado no válido", ex.getMessage());
        } finally {
            Assert.assertNull(p2);
        }
    }

    @Test
    public void modificarPruebaConRegistros() throws InputException {
        Competicion c = ctrCompeticiones.crearCompeticion("comp18", null, null, null, null, null);
        Prueba p = ControlPruebas.crearPrueba(c, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        ControlGrupos.crearGrupo(c, "a", null);
        ControlParticipantes.crearParticipante(c, "nombre", "apellidos", 1, "a", null, null, null, null);
        ControlRegistros.crearRegistro(c, 1, "prueba1", null, false, 12.0, null, null);
        Prueba p2 = null;
        try {
            p2 = ctrPruebas.modificarPrueba(p.getId(), c.getId(), "prueba1", TipoResultado.Numerica, TipoPrueba.Individual);
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
        
        Competicion c = ctrCompeticiones.crearCompeticion("comp19", null, null, null, null, null);
        try {
            ctrPruebas.eliminarPrueba(-1, c.getId());
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            Assert.assertEquals("Prueba no encontrada", ex.getMessage());
        }
    }
    
    @Test
    public void eliminarPruebaNull() throws InputException{
        
        Competicion c = ctrCompeticiones.crearCompeticion("comp20", null, null, null, null, null);
        try {
            ctrPruebas.eliminarPrueba(null, c.getId());
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            Assert.assertEquals("Identificador de prueba no válido", ex.getMessage());
        }
    }
    
    @Test
    public void eliminarPruebaCompeticionNull() throws InputException {
        
        Competicion c = ctrCompeticiones.crearCompeticion("comp21", null, null, null, null, null);
        Prueba p = ControlPruebas.crearPrueba(c, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        try {
            ctrPruebas.eliminarPrueba(p.getId(), null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            Assert.assertEquals("Identificador de competición no válido", ex.getMessage());
        }
    }
    
    @Test
    public void eliminarPruebaCompeticionNoValida() throws InputException {
        
        Competicion c = ctrCompeticiones.crearCompeticion("comp22", null, null, null, null, null);
        Prueba p = ControlPruebas.crearPrueba(c, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        try {
            ctrPruebas.eliminarPrueba(p.getId(), -1);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            Assert.assertEquals("Prueba no registrada en Competición", ex.getMessage());
        }
    }
    
    @Test
    public void eliminarPruebaExistente() throws InputException{
    
        Competicion c = ctrCompeticiones.crearCompeticion("comp23", null, null, null, null, null);
        Prueba p = ControlPruebas.crearPrueba(c, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        ctrPruebas.eliminarPrueba(p.getId(), c.getId());
        
        p = pruebaJpa.findPruebaByNombreCompeticion("prueba1", c.getId());
        Assert.assertNull(p);
    }
    
    @Test
    public void eliminarPruebaExistenteMismoNombreDosCompeticiones() throws InputException{
    
        Competicion c = ctrCompeticiones.crearCompeticion("comp24", null, null, null, null, null);
        Competicion c2 = ctrCompeticiones.crearCompeticion("comp25", null, null, null, null, null);
        Prueba p = ControlPruebas.crearPrueba(c, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        ControlPruebas.crearPrueba(c2, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        ctrPruebas.eliminarPrueba(p.getId(), c.getId());
        
        p = pruebaJpa.findPruebaByNombreCompeticion("prueba1", c.getId());
        Assert.assertNull(p);
        Prueba p2 = pruebaJpa.findPruebaByNombreCompeticion("prueba1", c2.getId());
        Assert.assertNotNull(p2);
    }
    
    
}
