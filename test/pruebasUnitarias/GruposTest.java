package pruebasUnitarias;

import controlador.ControlCompeticiones;
import controlador.ControlEquipos;
import controlador.ControlGrupos;
import controlador.ControlParticipantes;
import controlador.InputException;
import dao.CompeticionJpa;
import dao.GrupoJpa;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.Competicion;
import modelo.Grupo;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author JuanM
 */
public class GruposTest {

    private static GrupoJpa grupoJpa;
    private static CompeticionJpa competicionJpa;

    @BeforeClass
    public static void setUp() {
        grupoJpa = new GrupoJpa();
        competicionJpa = new CompeticionJpa();
    }

    @AfterClass
    public static void tearDown() {
        // Elimina las competiciones que han sido creadas en las pruebas
        for (int i = 1; i <= 24; i++) {
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

    // PRUEBAS SOBRE CREAR GRUPO
    @Test
    public void crearGrupo() throws InputException {
        Competicion c = ControlCompeticiones.crearCompeticion("comp1", null, null, null, null, null);
        ControlGrupos.crearGrupo(c, "grupo1", null);
        Grupo g = grupoJpa.findGrupoByNombreAndCompeticion("grupo1", c.getId());
        assertNotNull(g);
        assertEquals(g.getNombre(), "grupo1");
    }

    @Test
    public void crearGrupoTodoNull() {
        Grupo g = null;
        try {
            g = ControlGrupos.crearGrupo(null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Competición no válida");
            assertNull(g);
        }
    }

    @Test
    public void crearGrupoNombreGrupoNull() throws InputException {
        Competicion c = ControlCompeticiones.crearCompeticion("comp2", null, null, null, null, null);
        Grupo g = null;
        try {
            g = ControlGrupos.crearGrupo(c, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Nombre de Grupo no válido");
            assertNull(g);
        }
    }

    @Test
    public void crearGrupoNombreVacio() throws InputException {
        Competicion c = ControlCompeticiones.crearCompeticion("comp3", null, null, null, null, null);
        Grupo g = null;
        try {
            g = ControlGrupos.crearGrupo(c, "", null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Nombre de Grupo no válido");
            assertNull(g);
        }
    }

    @Test
    public void crearGrupoNombreOcupado() throws InputException {
        Competicion c = ControlCompeticiones.crearCompeticion("comp4", null, null, null, null, null);
        Grupo g2 = null;
        try {
            ControlGrupos.crearGrupo(c, "grupo1", null);
            g2 = ControlGrupos.crearGrupo(c, "grupo1", null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Nombre de Grupo ocupado");
            assertNull(g2);
        }
    }

    @Test
    public void crearSubGrupo() throws InputException {
        Competicion c = ControlCompeticiones.crearCompeticion("comp5", null, null, null, null, null);
        Grupo g, g2 = null;
        g = ControlGrupos.crearGrupo(c, "grupo1", null);
        g2 = ControlGrupos.crearGrupo(c, "grupo2", "grupo1");
        assertEquals(g2.getGrupoId().getId(), g.getId());
    }

    @Test
    public void crearSubGrupoSiMismo() throws InputException {
        Competicion c = ControlCompeticiones.crearCompeticion("comp12", null, null, null, null, null);
        try {
            ControlGrupos.crearGrupo(c, "grupo1", "grupo1");
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals("Campo Subgrupo De no válido", ex.getMessage());
        }
    }
    
    @Test
    public void crearGrupoSubGrupodeNoValido() throws InputException {
        Competicion c = ControlCompeticiones.crearCompeticion("comp24", null, null, null, null, null);
        try {
            ControlGrupos.crearGrupo(c, "grupo1", "grupoA");
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals("Campo Subgrupo De no válido", ex.getMessage());
        }
    }
    
    

    // PRUEBAS SOBRE GETSUBGRUPOS
    @Test
    public void getSubgruposSinHijos() throws InputException {
        Competicion c = ControlCompeticiones.crearCompeticion("comp6", null, null, null, null, null);
        Grupo g = null;
        g = ControlGrupos.crearGrupo(c, "grupo1", null);
        List<Grupo> hijos = ControlGrupos.getSubGrupos(g);
        assertEquals(hijos.size(), 0);
    }

    @Test
    public void getSubgrupos1Hijo() throws InputException {
        Competicion c = ControlCompeticiones.crearCompeticion("comp7", null, null, null, null, null);
        Grupo g, g2 = null;
        g = ControlGrupos.crearGrupo(c, "grupo1", null);
        g2 = ControlGrupos.crearGrupo(c, "grupo2", "grupo1");
        List<Grupo> hijos = ControlGrupos.getSubGrupos(g);
        assertEquals(hijos.size(), 1);
        assertEquals(hijos.get(0).getNombre(), g2.getNombre());
    }

    @Test
    public void getSubgruposVariosHijos() throws InputException {
        Competicion c = ControlCompeticiones.crearCompeticion("comp8", null, null, null, null, null);
        Grupo g, g2, g3, g4, g5 = null;
        g = ControlGrupos.crearGrupo(c, "grupo1", null);
        g2 = ControlGrupos.crearGrupo(c, "grupo2", "grupo1");
        g3 = ControlGrupos.crearGrupo(c, "grupo3", "grupo1");
        g4 = ControlGrupos.crearGrupo(c, "grupo4", "grupo2");
        g5 = ControlGrupos.crearGrupo(c, "grupo5", "grupo4");

        List<Grupo> hijos = ControlGrupos.getSubGrupos(g);
        assertEquals(hijos.size(), 4);
        assertTrue(hijos.contains(g2));
        assertTrue(hijos.contains(g3));
        assertTrue(hijos.contains(g4));
        assertTrue(hijos.contains(g5));
    }

    // PRUEBAS SOBRE ELIMINAR GRUPO
    @Test
    public void eliminarGrupoTodoNull() {
        try {
            ControlGrupos.eliminarGrupo(null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Competición no válida");
        }
    }

    @Test
    public void eliminarGrupoIdGrupoNull() {
        try {
            Competicion c = ControlCompeticiones.crearCompeticion("comp9", null, null, null, null, null);
            ControlGrupos.eliminarGrupo(c, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals("Identificador de grupo no válido", ex.getMessage());
        }
    }

    @Test
    public void eliminarGrupoValido() throws InputException {
        Competicion c = ControlCompeticiones.crearCompeticion("comp10", null, null, null, null, null);
        Grupo g = ControlGrupos.crearGrupo(c, "grupo1", null);
        assertNotNull(grupoJpa.findGrupoByNombreAndCompeticion("grupo1", c.getId()));
        ControlGrupos.eliminarGrupo(c, g.getId());
        assertNull(grupoJpa.findGrupoByNombreAndCompeticion("grupo1", c.getId()));
    }

    @Test
    public void eliminarGrupoConHijos() throws InputException {
        Competicion c = ControlCompeticiones.crearCompeticion("comp11", null, null, null, null, null);
        Grupo g = ControlGrupos.crearGrupo(c, "grupo1", null);
        Grupo g2 = ControlGrupos.crearGrupo(c, "grupo2", "grupo1");
        ControlGrupos.eliminarGrupo(c, g.getId());
        assertNull(grupoJpa.findGrupoByNombreAndCompeticion("grupo2", c.getId()));
    }

    @Test
    public void eliminarGrupoConParticipantes() throws InputException {
        Competicion c = ControlCompeticiones.crearCompeticion("comp13", null, null, null, null, null);
        Grupo g = ControlGrupos.crearGrupo(c, "grupo1", null);
        ControlParticipantes.crearParticipante(c, "n", "a", 1, g.getNombre(), null, null, null, null);
        ControlParticipantes.crearParticipante(c, "n2", "a2", 2, g.getNombre(), null, null, null, null);
        ControlGrupos.eliminarGrupo(c, g.getId());
        assertNull(grupoJpa.findGrupoByNombreAndCompeticion("grupo1", c.getId()));
    }

    @Test
    public void eliminarGrupoConEquipos() throws InputException {
        Competicion c = ControlCompeticiones.crearCompeticion("comp14", null, null, null, null, null);
        Grupo g = ControlGrupos.crearGrupo(c, "grupo1", null);
        Grupo g2 = ControlGrupos.crearGrupo(c, "grupo2", "grupo1");
        ControlEquipos.crearEquipo(c, "e1", "grupo1");
        ControlEquipos.crearEquipo(c, "e2", "grupo1");
        ControlGrupos.eliminarGrupo(c, g.getId());
        assertNull(grupoJpa.findGrupoByNombreAndCompeticion("grupo1", c.getId()));
    }

    @Test
    public void eliminarGrupoConEquiposYParticipantes() throws InputException {
        Competicion c = ControlCompeticiones.crearCompeticion("comp15", null, null, null, null, null);
        Grupo g = ControlGrupos.crearGrupo(c, "grupo1", null);
        Grupo g2 = ControlGrupos.crearGrupo(c, "grupo2", "grupo1");
        ControlParticipantes.crearParticipante(c, "n", "a", 1, g.getNombre(), null, null, null, null);
        ControlParticipantes.crearParticipante(c, "n2", "a2", 2, g2.getNombre(), null, null, null, null);
        ControlEquipos.crearEquipo(c, "e1", "grupo1");
        ControlEquipos.crearEquipo(c, "e2", "grupo2");
        ControlGrupos.eliminarGrupo(c, g.getId());
        assertNull(grupoJpa.findGrupoByNombreAndCompeticion("grupo1", c.getId()));
    }

    // PRUEBAS SOBRE MODIFICAR GRUPOS
    @Test
    public void modificarGrupoTodoNull() throws InputException {
        try {
            ControlGrupos.modificarGrupo(null, null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Competición no válida");
        }
    }

    @Test
    public void modificarGrupoNull() throws InputException {
        Competicion c = ControlCompeticiones.crearCompeticion("comp16", null, null, null, null, null);
        try {
            ControlGrupos.modificarGrupo(c, null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Identificador de grupo no válido");
        }
    }

    @Test
    public void modificarGrupoNombreNull() throws InputException {
        Competicion c = ControlCompeticiones.crearCompeticion("comp17", null, null, null, null, null);
        Grupo g = ControlGrupos.crearGrupo(c, "grupo1", null);
        try {
            ControlGrupos.modificarGrupo(c, g.getId(), null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Nombre de grupo no válido");
        }
    }

    @Test
    public void modificarGrupoNombre() throws InputException {
        Competicion c = ControlCompeticiones.crearCompeticion("comp18", null, null, null, null, null);
        Grupo g = ControlGrupos.crearGrupo(c, "grupo1", null);
        ControlGrupos.modificarGrupo(c, g.getId(), "grupo2", null);
        assertNull(grupoJpa.findGrupoByNombreAndCompeticion("grupo1", c.getId()));
        assertNotNull(grupoJpa.findGrupoByNombreAndCompeticion("grupo2", c.getId()));
    }

    @Test
    public void modificarGrupoSubGrupoDeNoValido() throws InputException {
        Competicion c = ControlCompeticiones.crearCompeticion("comp19", null, null, null, null, null);
        Grupo g = ControlGrupos.crearGrupo(c, "grupo1", null);
        try {
            ControlGrupos.modificarGrupo(c, g.getId(), "grupo2", "grupoA");
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Campo Subgrupo De no válido");
        }
    }

    @Test
    public void modificarGrupoNombreVacio() throws InputException {
        Competicion c = ControlCompeticiones.crearCompeticion("comp20", null, null, null, null, null);
        Grupo g = ControlGrupos.crearGrupo(c, "grupo1", null);
        try {
            ControlGrupos.modificarGrupo(c, g.getId(), "", null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Nombre de grupo no válido");
        }
    }

    @Test
    public void modificarGrupoNombreOcupado() throws InputException {
        Competicion c = ControlCompeticiones.crearCompeticion("comp21", null, null, null, null, null);
        Grupo g = ControlGrupos.crearGrupo(c, "grupo1", null);
        Grupo g2 = ControlGrupos.crearGrupo(c, "grupo2", null);
        try {
            ControlGrupos.modificarGrupo(c, g.getId(), "grupo2", null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Nombre de grupo ocupado");
        }
    }

    @Test
    public void modificarGrupoSubGrupoValido() throws InputException {
        Competicion c = ControlCompeticiones.crearCompeticion("comp22", null, null, null, null, null);
        Grupo g = ControlGrupos.crearGrupo(c, "grupo1", null);
        Grupo g2 = ControlGrupos.crearGrupo(c, "grupo2", null);
        ControlGrupos.modificarGrupo(c, g.getId(), "grupo1", "grupo2");
        Grupo modificado = grupoJpa.findGrupoByNombreAndCompeticion("grupo1", c.getId());
        assertNotNull(modificado);
        assertEquals(modificado.getGrupoId().getId(), g2.getId());
    }

    @Test
    public void modificarGrupoSubGrupoValidoNull() throws InputException {
        Competicion c = ControlCompeticiones.crearCompeticion("comp23", null, null, null, null, null);
        Grupo g = ControlGrupos.crearGrupo(c, "grupo1", null);
        Grupo g2 = ControlGrupos.crearGrupo(c, "grupo2", "grupo1");
        ControlGrupos.modificarGrupo(c, g2.getId(), "grupo2", null);
        Grupo modificado = grupoJpa.findGrupoByNombreAndCompeticion("grupo2", c.getId());
        assertNotNull(modificado);
        assertNull(modificado.getGrupoId());
    }
}
