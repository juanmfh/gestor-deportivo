package testcases;


import modelo.dao.GrupoJpa;
import controlador.ControlParticipantes;
import controlador.InputException;
import modelo.dao.CompeticionJpa;
import modelo.dao.GrupoJpa;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.Competicion;
import modelo.Grupo;
import modelo.dao.EquipoJpa;
import modelo.dao.ParticipanteJpa;
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
                    CompeticionJpa.eliminarCompeticion(c.getNombre());
                } catch (InputException ex) {
                    Logger.getLogger(CompeticionTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    // PRUEBAS SOBRE CREAR GRUPO
    @Test
    public void crearGrupo() throws InputException {
        Competicion c = CompeticionJpa.crearCompeticion("comp1", null, null, null, null, null);
        GrupoJpa.crearGrupo(c, "grupo1", null);
        Grupo g = grupoJpa.findGrupoByNombreAndCompeticion("grupo1", c.getId());
        assertNotNull(g);
        assertEquals(g.getNombre(), "grupo1");
    }

    @Test
    public void crearGrupoTodoNull() {
        Grupo g = null;
        try {
            g = GrupoJpa.crearGrupo(null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Competición no válida");
            assertNull(g);
        }
    }

    @Test
    public void crearGrupoNombreGrupoNull() throws InputException {
        Competicion c = CompeticionJpa.crearCompeticion("comp2", null, null, null, null, null);
        Grupo g = null;
        try {
            g = GrupoJpa.crearGrupo(c, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Nombre de Grupo no válido");
            assertNull(g);
        }
    }

    @Test
    public void crearGrupoNombreVacio() throws InputException {
        Competicion c = CompeticionJpa.crearCompeticion("comp3", null, null, null, null, null);
        Grupo g = null;
        try {
            g = GrupoJpa.crearGrupo(c, "", null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Nombre de Grupo no válido");
            assertNull(g);
        }
    }

    @Test
    public void crearGrupoNombreOcupado() throws InputException {
        Competicion c = CompeticionJpa.crearCompeticion("comp4", null, null, null, null, null);
        Grupo g2 = null;
        try {
            GrupoJpa.crearGrupo(c, "grupo1", null);
            g2 = GrupoJpa.crearGrupo(c, "grupo1", null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Nombre de Grupo ocupado");
            assertNull(g2);
        }
    }

    @Test
    public void crearSubGrupo() throws InputException {
        Competicion c = CompeticionJpa.crearCompeticion("comp5", null, null, null, null, null);
        Grupo g, g2 = null;
        g = GrupoJpa.crearGrupo(c, "grupo1", null);
        g2 = GrupoJpa.crearGrupo(c, "grupo2", "grupo1");
        assertEquals(g2.getGrupoId().getId(), g.getId());
    }

    @Test
    public void crearSubGrupoSiMismo() throws InputException {
        Competicion c = CompeticionJpa.crearCompeticion("comp12", null, null, null, null, null);
        try {
            GrupoJpa.crearGrupo(c, "grupo1", "grupo1");
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals("Campo Subgrupo De no válido", ex.getMessage());
        }
    }
    
    @Test
    public void crearGrupoSubGrupodeNoValido() throws InputException {
        Competicion c = CompeticionJpa.crearCompeticion("comp24", null, null, null, null, null);
        try {
            GrupoJpa.crearGrupo(c, "grupo1", "grupoA");
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals("Campo Subgrupo De no válido", ex.getMessage());
        }
    }
    
    

    // PRUEBAS SOBRE GETSUBGRUPOS
    @Test
    public void getSubgruposSinHijos() throws InputException {
        Competicion c = CompeticionJpa.crearCompeticion("comp6", null, null, null, null, null);
        Grupo g = null;
        g = GrupoJpa.crearGrupo(c, "grupo1", null);
        List<Grupo> hijos = GrupoJpa.getSubGrupos(g);
        assertEquals(hijos.size(), 0);
    }

    @Test
    public void getSubgrupos1Hijo() throws InputException {
        Competicion c = CompeticionJpa.crearCompeticion("comp7", null, null, null, null, null);
        Grupo g, g2 = null;
        g = GrupoJpa.crearGrupo(c, "grupo1", null);
        g2 = GrupoJpa.crearGrupo(c, "grupo2", "grupo1");
        List<Grupo> hijos = GrupoJpa.getSubGrupos(g);
        assertEquals(hijos.size(), 1);
        assertEquals(hijos.get(0).getNombre(), g2.getNombre());
    }

    @Test
    public void getSubgruposVariosHijos() throws InputException {
        Competicion c = CompeticionJpa.crearCompeticion("comp8", null, null, null, null, null);
        Grupo g, g2, g3, g4, g5 = null;
        g = GrupoJpa.crearGrupo(c, "grupo1", null);
        g2 = GrupoJpa.crearGrupo(c, "grupo2", "grupo1");
        g3 = GrupoJpa.crearGrupo(c, "grupo3", "grupo1");
        g4 = GrupoJpa.crearGrupo(c, "grupo4", "grupo2");
        g5 = GrupoJpa.crearGrupo(c, "grupo5", "grupo4");

        List<Grupo> hijos = GrupoJpa.getSubGrupos(g);
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
            GrupoJpa.eliminarGrupo(null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Competición no válida");
        }
    }

    @Test
    public void eliminarGrupoIdGrupoNull() {
        try {
            Competicion c = CompeticionJpa.crearCompeticion("comp9", null, null, null, null, null);
            GrupoJpa.eliminarGrupo(c, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals("Identificador de grupo no válido", ex.getMessage());
        }
    }

    @Test
    public void eliminarGrupoValido() throws InputException {
        Competicion c = CompeticionJpa.crearCompeticion("comp10", null, null, null, null, null);
        Grupo g = GrupoJpa.crearGrupo(c, "grupo1", null);
        assertNotNull(grupoJpa.findGrupoByNombreAndCompeticion("grupo1", c.getId()));
        GrupoJpa.eliminarGrupo(c, g.getId());
        assertNull(grupoJpa.findGrupoByNombreAndCompeticion("grupo1", c.getId()));
    }

    @Test
    public void eliminarGrupoConHijos() throws InputException {
        Competicion c = CompeticionJpa.crearCompeticion("comp11", null, null, null, null, null);
        Grupo g = GrupoJpa.crearGrupo(c, "grupo1", null);
        Grupo g2 = GrupoJpa.crearGrupo(c, "grupo2", "grupo1");
        GrupoJpa.eliminarGrupo(c, g.getId());
        assertNull(grupoJpa.findGrupoByNombreAndCompeticion("grupo2", c.getId()));
    }

    @Test
    public void eliminarGrupoConParticipantes() throws InputException {
        Competicion c = CompeticionJpa.crearCompeticion("comp13", null, null, null, null, null);
        Grupo g = GrupoJpa.crearGrupo(c, "grupo1", null);
        ParticipanteJpa.crearParticipante(c, "n", "a", 1, g.getNombre(), null, null, null, null);
        ParticipanteJpa.crearParticipante(c, "n2", "a2", 2, g.getNombre(), null, null, null, null);
        GrupoJpa.eliminarGrupo(c, g.getId());
        assertNull(grupoJpa.findGrupoByNombreAndCompeticion("grupo1", c.getId()));
    }

    @Test
    public void eliminarGrupoConEquipos() throws InputException {
        Competicion c = CompeticionJpa.crearCompeticion("comp14", null, null, null, null, null);
        Grupo g = GrupoJpa.crearGrupo(c, "grupo1", null);
        Grupo g2 = GrupoJpa.crearGrupo(c, "grupo2", "grupo1");
        EquipoJpa.crearEquipo(c, "e1", "grupo1");
        EquipoJpa.crearEquipo(c, "e2", "grupo1");
        GrupoJpa.eliminarGrupo(c, g.getId());
        assertNull(grupoJpa.findGrupoByNombreAndCompeticion("grupo1", c.getId()));
    }

    @Test
    public void eliminarGrupoConEquiposYParticipantes() throws InputException {
        Competicion c = CompeticionJpa.crearCompeticion("comp15", null, null, null, null, null);
        Grupo g = GrupoJpa.crearGrupo(c, "grupo1", null);
        Grupo g2 = GrupoJpa.crearGrupo(c, "grupo2", "grupo1");
        ParticipanteJpa.crearParticipante(c, "n", "a", 1, g.getNombre(), null, null, null, null);
        ParticipanteJpa.crearParticipante(c, "n2", "a2", 2, g2.getNombre(), null, null, null, null);
        EquipoJpa.crearEquipo(c, "e1", "grupo1");
        EquipoJpa.crearEquipo(c, "e2", "grupo2");
        GrupoJpa.eliminarGrupo(c, g.getId());
        assertNull(grupoJpa.findGrupoByNombreAndCompeticion("grupo1", c.getId()));
    }

    // PRUEBAS SOBRE MODIFICAR GRUPOS
    @Test
    public void modificarGrupoTodoNull() throws InputException {
        try {
            GrupoJpa.modificarGrupo(null, null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Competición no válida");
        }
    }

    @Test
    public void modificarGrupoNull() throws InputException {
        Competicion c = CompeticionJpa.crearCompeticion("comp16", null, null, null, null, null);
        try {
            GrupoJpa.modificarGrupo(c, null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Identificador de grupo no válido");
        }
    }

    @Test
    public void modificarGrupoNombreNull() throws InputException {
        Competicion c = CompeticionJpa.crearCompeticion("comp17", null, null, null, null, null);
        Grupo g = GrupoJpa.crearGrupo(c, "grupo1", null);
        try {
            GrupoJpa.modificarGrupo(c, g.getId(), null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Nombre de grupo no válido");
        }
    }

    @Test
    public void modificarGrupoNombre() throws InputException {
        Competicion c = CompeticionJpa.crearCompeticion("comp18", null, null, null, null, null);
        Grupo g = GrupoJpa.crearGrupo(c, "grupo1", null);
        GrupoJpa.modificarGrupo(c, g.getId(), "grupo2", null);
        assertNull(grupoJpa.findGrupoByNombreAndCompeticion("grupo1", c.getId()));
        assertNotNull(grupoJpa.findGrupoByNombreAndCompeticion("grupo2", c.getId()));
    }

    @Test
    public void modificarGrupoSubGrupoDeNoValido() throws InputException {
        Competicion c = CompeticionJpa.crearCompeticion("comp19", null, null, null, null, null);
        Grupo g = GrupoJpa.crearGrupo(c, "grupo1", null);
        try {
            GrupoJpa.modificarGrupo(c, g.getId(), "grupo2", "grupoA");
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Campo Subgrupo De no válido");
        }
    }

    @Test
    public void modificarGrupoNombreVacio() throws InputException {
        Competicion c = CompeticionJpa.crearCompeticion("comp20", null, null, null, null, null);
        Grupo g = GrupoJpa.crearGrupo(c, "grupo1", null);
        try {
            GrupoJpa.modificarGrupo(c, g.getId(), "", null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Nombre de grupo no válido");
        }
    }

    @Test
    public void modificarGrupoNombreOcupado() throws InputException {
        Competicion c = CompeticionJpa.crearCompeticion("comp21", null, null, null, null, null);
        Grupo g = GrupoJpa.crearGrupo(c, "grupo1", null);
        Grupo g2 = GrupoJpa.crearGrupo(c, "grupo2", null);
        try {
            GrupoJpa.modificarGrupo(c, g.getId(), "grupo2", null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Nombre de grupo ocupado");
        }
    }

    @Test
    public void modificarGrupoSubGrupoValido() throws InputException {
        Competicion c = CompeticionJpa.crearCompeticion("comp22", null, null, null, null, null);
        Grupo g = GrupoJpa.crearGrupo(c, "grupo1", null);
        Grupo g2 = GrupoJpa.crearGrupo(c, "grupo2", null);
        GrupoJpa.modificarGrupo(c, g.getId(), "grupo1", "grupo2");
        Grupo modificado = grupoJpa.findGrupoByNombreAndCompeticion("grupo1", c.getId());
        assertNotNull(modificado);
        assertEquals(modificado.getGrupoId().getId(), g2.getId());
    }

    @Test
    public void modificarGrupoSubGrupoValidoNull() throws InputException {
        Competicion c = CompeticionJpa.crearCompeticion("comp23", null, null, null, null, null);
        Grupo g = GrupoJpa.crearGrupo(c, "grupo1", null);
        Grupo g2 = GrupoJpa.crearGrupo(c, "grupo2", "grupo1");
        GrupoJpa.modificarGrupo(c, g2.getId(), "grupo2", null);
        Grupo modificado = grupoJpa.findGrupoByNombreAndCompeticion("grupo2", c.getId());
        assertNotNull(modificado);
        assertNull(modificado.getGrupoId());
    }
}
