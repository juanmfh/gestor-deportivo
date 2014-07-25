package pruebasUnitarias;


import controlador.ControlCompeticiones;
import controlador.ControlGrupos;
import controlador.ControlPruebas;
import controlador.InputException;
import controlador.TipoPrueba;
import controlador.TipoResultado;
import dao.CompeticionJpa;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import static junit.framework.Assert.fail;
import modelo.Competicion;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author JuanM
 */
public class CompeticionTest {

    private static CompeticionJpa competicionJpa;

    // PRUEBAS SOBRE CREAR COMPETICION
    @BeforeClass
    public static void setUp() throws InputException {
        competicionJpa = new CompeticionJpa();

    }

    @AfterClass
    public static void tearDown() {
        // Elimina las competiciones que han sido creadas en las pruebas
        for (int i = 1; i <= 15; i++) {
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

    // Crea una competicion con todos los atributos null
    @Test(expected = InputException.class)
    public void crearCompeticionNombreNull() throws InputException {
        Competicion c = ControlCompeticiones.crearCompeticion(null, null, null, null, null, null);
    }

    // Crea una competicion con un nombre de longitud 0.
    @Test(expected = InputException.class)
    public void crearCompeticionNombreVacio() throws InputException {
        Competicion c = ControlCompeticiones.crearCompeticion("", null, null, null, null, null);
    }

    // Crea una competicion solo con el nombre
    @Test
    public void crearCompeticionNombre() throws InputException {
        String nombreCompeticion = "comp1";
        Competicion c = ControlCompeticiones.crearCompeticion(nombreCompeticion, null, null, null, null, null);
        Assert.assertNotNull(c);
        Assert.assertEquals(c.getNombre(), nombreCompeticion);
    }

    // Crea una competicion con todos los parámetros y se comprueban que se registran bien
    @Test
    public void crearCompeticion() throws InputException {
        String nombreCompeticion = "comp2";
        String lugar = "lugar1";
        Date fechaInicio = new Date(12 / 12 / 2010);
        Date fechaFin = new Date(12 / 07 / 2010);
        String nombreImagen = "logo1";
        String organizador = "organizador1";
        Competicion c = ControlCompeticiones.crearCompeticion(nombreCompeticion, lugar, fechaInicio, fechaFin, nombreImagen, organizador);

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
    public void crearCompeticionNombreOcupado() throws InputException {
        String nombreCompeticion = "comp3";
        ControlCompeticiones.crearCompeticion(nombreCompeticion, null, null, null, null, null);
        Competicion c = null;
        try {
            c = ControlCompeticiones.crearCompeticion(nombreCompeticion, null, null, null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            Assert.assertEquals("Nombre de competición ocupado", ex.getMessage());
        } finally {
            Assert.assertNull(c);
        }
    }

    // PRUEBAS SOBRE MODIFICAR COMPETICION
    
    // Modifica una competición con todos los parámetros a null, incluido la propia competición
    @Test
    public void modificarCompeticionTodoNull() throws InputException {
        try {
            Competicion c = ControlCompeticiones.modificarCompeticion(null, null, null, null, null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            Assert.assertEquals("Nombre de competición no válido", ex.getMessage());
        }
    }

    // Modifica una competición con todos los parámetros a null, salvo la competición
    @Test
    public void modificarCompeticionSinParametros() throws InputException {

        Competicion c = ControlCompeticiones.crearCompeticion("comp8", null, null, null, null, null);
        Competicion c2 = null;
        try {
            c2 = ControlCompeticiones.modificarCompeticion(c, null, null, null, null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            Assert.assertEquals("Nombre de competición no válido", ex.getMessage());
        } finally {
            Assert.assertNull(c2);
        }
    }

    // Modifica el nombre de una competición con un nombre que ya está ocupado por otra competición
    @Test
    public void modificarCompeticionNombreOcupado() throws InputException {

        Competicion c = ControlCompeticiones.crearCompeticion("comp4", null, null, null, null, null);
        ControlCompeticiones.crearCompeticion("comp5", null, null, null, null, null);
        Competicion c2 = null;
        try {
            c2 = ControlCompeticiones.modificarCompeticion(c, "comp5", null, null, null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            Assert.assertEquals("Nombre de competición ocupado", ex.getMessage());
        } finally {
            Assert.assertNull(c2);
        }
    }

    // Modifica todos los parámetros de una competición y se comprueban.
    @Test
    public void modificarCompeticion() throws InputException {
        Competicion c = ControlCompeticiones.crearCompeticion("comp6", null, null, null, null, null);
        String nombreCompeticion = "comp7";
        String lugar = "lugar1";
        Date fechaInicio = new Date(12 / 12 / 2010);
        Date fechaFin = new Date(12 / 07 / 2010);
        String nombreImagen = "logo1";
        String organizador = "organizador1";
        c = ControlCompeticiones.modificarCompeticion(c, nombreCompeticion, lugar, fechaInicio, fechaFin, nombreImagen, organizador);

        Assert.assertNotNull(c);
        Assert.assertEquals(c.getNombre(), nombreCompeticion);
        Assert.assertEquals(c.getCiudad(), lugar);
        Assert.assertEquals(c.getFechafin(), fechaFin);
        Assert.assertEquals(c.getFechainicio(), fechaInicio);
        Assert.assertEquals(c.getOrganizador(), organizador);
        Assert.assertEquals(c.getImagen(), nombreImagen);
    }

    // PRUEBAS SOBRE ELIMINAR COMPETICION
    
    // Elimina una competición que no tiene ni pruebas ni grupos
    @Test
    public void eliminarCompeticionVacia() throws InputException {
        Competicion c = ControlCompeticiones.crearCompeticion("comp9", null, null, null, null, null);
        ControlCompeticiones.eliminarCompeticion(c.getNombre());
        Assert.assertNull(competicionJpa.findCompeticionByName("comp9"));
    }

    // Elimina una competición que tiene una prueba
    @Test
    public void eliminarCompeticionConPrueba() throws InputException {
        Competicion c = ControlCompeticiones.crearCompeticion("comp10", null, null, null, null, null);
        ControlPruebas.crearPrueba(c, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Numerica.toString());
        ControlCompeticiones.eliminarCompeticion("comp10");
        Assert.assertNull(competicionJpa.findCompeticionByName("comp10"));
    }

    // Elimina una competición que tiene varias pruebas
    @Test
    public void eliminarCompeticionConVariasPrueba() throws InputException {
        Competicion c = ControlCompeticiones.crearCompeticion("comp11", null, null, null, null, null);
        ControlPruebas.crearPrueba(c, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Numerica.toString());
        ControlPruebas.crearPrueba(c, "prueba2", TipoPrueba.Individual.toString(), TipoResultado.Numerica.toString());
        ControlPruebas.crearPrueba(c, "prueba3", TipoPrueba.Individual.toString(), TipoResultado.Numerica.toString());
        ControlCompeticiones.eliminarCompeticion("comp11");
        Assert.assertNull(competicionJpa.findCompeticionByName("comp11"));
    }

    // Elimina una competición que tiene un grupo
    @Test
    public void eliminarCompeticionConGrupo() throws InputException {
        Competicion c = ControlCompeticiones.crearCompeticion("comp12", null, null, null, null, null);
        ControlGrupos.crearGrupo(c, "grupoA", null);
        ControlCompeticiones.eliminarCompeticion("comp12");
        Assert.assertNull(competicionJpa.findCompeticionByName("comp12"));
    }

    // Elimina una competición que tiene varios grupos
    @Test
    public void eliminarCompeticionConVariosGrupo() throws InputException {
        Competicion c = ControlCompeticiones.crearCompeticion("comp13", null, null, null, null, null);
        ControlGrupos.crearGrupo(c, "grupoA", null);
        ControlGrupos.crearGrupo(c, "grupoB", "grupoA");
        ControlGrupos.crearGrupo(c, "grupoC", null);
        ControlCompeticiones.eliminarCompeticion("comp13");
        Assert.assertNull(competicionJpa.findCompeticionByName("comp13"));
    }

    // Elimina una competición compuesta por varias pruebas y varios grupos
    @Test
    public void eliminarCompeticionConGruposYPruebas() throws InputException {
        Competicion c = ControlCompeticiones.crearCompeticion("comp14", null, null, null, null, null);
        ControlPruebas.crearPrueba(c, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Numerica.toString());
        ControlPruebas.crearPrueba(c, "prueba2", TipoPrueba.Individual.toString(), TipoResultado.Numerica.toString());
        ControlGrupos.crearGrupo(c, "grupoA", null);
        ControlGrupos.crearGrupo(c, "grupoB", "grupoA");
        ControlGrupos.crearGrupo(c, "grupoC", null);

        ControlCompeticiones.eliminarCompeticion("comp14");
        Assert.assertNull(competicionJpa.findCompeticionByName("comp14"));
    }

    // LLama al método de eliminar competición con Null
    @Test
    public void eliminarCompeticionNull() {
        try {
            ControlCompeticiones.eliminarCompeticion(null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            Assert.assertEquals("Competición no encontrada", ex.getMessage());
        }
    }

    // Llama al método de eliminar competición con una competición que no existe
    @Test
    public void eliminarCompeticionNoEncontrada() {
        try {
            ControlCompeticiones.eliminarCompeticion("comp15");
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            Assert.assertEquals("Competición no encontrada", ex.getMessage());
        }
    }
}
