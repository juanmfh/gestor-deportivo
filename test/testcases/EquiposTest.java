package testcases;

import controlador.InputException;
import modelo.dao.EquipoJpa;
import modelo.logicaNegocio.EquipoService;
import modelo.dao.ParticipanteJpa;
import modelo.entities.Competicion;
import modelo.entities.Equipo;
import modelo.entities.Grupo;
import modelo.entities.Participante;
import modelo.logicaNegocio.GrupoService;
import modelo.logicaNegocio.CompeticionService;
import modelo.logicaNegocio.ParticipanteService;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author JuanM
 */
public class EquiposTest {

    private static EquipoJpa equipoJpa;
    private static Competicion competicion;

    @BeforeClass
    public static void setUp() {
        equipoJpa = new EquipoJpa();
    }

    @Before
    public void ini() throws InputException {
        competicion = CompeticionService.crearCompeticion("comp1", null, null, null, null, null);
    }

    @After
    public void destroy() throws InputException {
        CompeticionService.eliminarCompeticion("comp1");
    }

    // PRUEBAS SOBRE CREAR EQUIPO
    @Test
    public void crearEquipoTodoNull() {
        Equipo e = null;
        try {
            e = EquipoService.crearEquipo(null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Nombre de competición no válido");
            assertNull(e);
        }
    }

    @Test
    public void crearEquipoNombreNull() {
        Equipo e = null;
        try {
            e = EquipoService.crearEquipo(competicion, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Nombre de equipo no válido");
            assertNull(e);
        }
    }

    @Test
    public void crearEquipoNombreVacio() {
        Equipo e = null;
        try {
            e = EquipoService.crearEquipo(competicion, "", null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Nombre de equipo no válido");
            assertNull(e);
        }
    }

    @Test
    public void crearEquipoGrupoNull() {
        Equipo e = null;
        try {
            e = EquipoService.crearEquipo(competicion, "a", null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Grupo no válido");
            assertNull(e);
        }
    }

    @Test
    public void crearEquipoGrupoNoValido() {
        Equipo e = null;
        try {
            e = EquipoService.crearEquipo(competicion, "a", "b");
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Grupo no encontrado");
            assertNull(e);
        }
    }

    @Test
    public void crearEquipoValido() throws InputException {
        Equipo e = null;
        GrupoService.crearGrupo(competicion, "grupo1", null);
        e = EquipoService.crearEquipo(competicion, "a", "grupo1");
        assertNotNull(e);
        assertNotNull(equipoJpa.findByNombreAndCompeticion(e.getNombre(), competicion.getId()));
    }

    @Test
    public void crearEquipoNombreOcupado() throws InputException {
        Equipo e = null;
        GrupoService.crearGrupo(competicion, "grupo1", null);
        EquipoService.crearEquipo(competicion, "a", "grupo1");
        try {
            e = EquipoService.crearEquipo(competicion, "a", "grupo1");
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertNull(e);
            assertEquals(ex.getMessage(), "Nombre de equipo ocupado");
        }
    }
    
    @Test
    public void crearEquipoNombreOcupadoOtroGrupo() throws InputException {
        Equipo e = null;
        GrupoService.crearGrupo(competicion, "grupo1", null);
        GrupoService.crearGrupo(competicion, "grupo2", null);
        EquipoService.crearEquipo(competicion, "a", "grupo1");
        try {
            e = EquipoService.crearEquipo(competicion, "a", "grupo2");
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertNull(e);
            assertEquals(ex.getMessage(), "Nombre de equipo ocupado");
        }
    }
    
    @Test
    public void crearEquipoNombreOcupadoOtraCompeticion() throws InputException {
        Equipo e1,e2 = null;
        Competicion c2 = CompeticionService.crearCompeticion("comp2", null, null, null, null, null);
        GrupoService.crearGrupo(competicion, "grupo1", null);
        GrupoService.crearGrupo(c2, "grupo1", null);
        e1 =EquipoService.crearEquipo(competicion, "a", "grupo1");
        e2 = EquipoService.crearEquipo(c2, "a", "grupo1");
        assertNotNull(e1);
        assertNotNull(e2);
        assertNotNull(equipoJpa.findByNombreAndCompeticion(e1.getNombre(), competicion.getId()));
        assertNotNull(equipoJpa.findByNombreAndCompeticion(e2.getNombre(), c2.getId()));
        CompeticionService.eliminarCompeticion(c2.getNombre());
    }
    
    // PRUEBAS SOBRE ELIMINAR EQUIPO
    
    @Test
    public void eliminarEquipoNull(){
        try {
            EquipoService.eliminarEquipo(null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(),"Equipo no válido");
        }
    }
    
    @Test
    public void eliminarEquipoNoValido(){
        try {
            EquipoService.eliminarEquipo(-1);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(),"Equipo no encontrado");
        }
    }
    
    @Test
    public void eliminarEquipoValido() throws InputException{
        GrupoService.crearGrupo(competicion, "grupo1", null);
        Equipo e = EquipoService.crearEquipo(competicion, "a", "grupo1");
        EquipoService.eliminarEquipo(e.getId());
        e = equipoJpa.findByNombreAndCompeticion("a", competicion.getId());
        assertNull(e);
    }
    
    // Comprueba que no se eliminan los participantes del equipo
    @Test
    public void eliminarEquipoConParticipantes() throws InputException{
        Competicion c2 = CompeticionService.crearCompeticion("comp2", null, null, null, null, null);
        GrupoService.crearGrupo(c2, "grupo1", null);
        Grupo g =GrupoService.crearGrupo(competicion, "grupo1", null);
        Equipo e = EquipoService.crearEquipo(competicion, "a", "grupo1");
        Equipo e2 = EquipoService.crearEquipo(c2, "a", "grupo1");
        EquipoService.eliminarEquipo(e2.getId());
        e = equipoJpa.findByNombreAndCompeticion("a", competicion.getId());
        assertNotNull(e);
        e2 = equipoJpa.findByNombreAndCompeticion("a", c2.getId());
        assertNull(e2);
        CompeticionService.eliminarCompeticion(c2.getNombre());
    }
    
    // Comprueba que se elimina el equipo correcto
    @Test
    public void eliminarEquipoMismoNombre2Competiciones() throws InputException{
        Grupo g = GrupoService.crearGrupo(competicion, "grupo1", null);
        Equipo e = EquipoService.crearEquipo(competicion, "a", "grupo1");
        ParticipanteService.crearParticipante(competicion, "n", "a", 1, g.getNombre(), null, null, null, null);
        EquipoService.eliminarEquipo(e.getId());
        e = equipoJpa.findByNombreAndCompeticion("a", competicion.getId());
        assertNull(e);
        ParticipanteJpa participantejpa = new ParticipanteJpa();
        Participante p =participantejpa.findByDorsalAndCompeticion(1, competicion.getId());
        assertNotNull(p);
    }
    
    // PRUEBAS SOBRE MODIFICAR EQUIPO
    
    @Test
    public void modificarEquipoNull(){
        try {
            EquipoService.modificarEquipo(null,null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Competición no válida");
        }
    }
    
    @Test
    public void modificarEquipoIdNull(){
        try {
            EquipoService.modificarEquipo(competicion,null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Equipo no válido");
        }
    }
    
    @Test
    public void modificarEquipoNombreNull() throws InputException{
        GrupoService.crearGrupo(competicion, "grupo1", null);
        Equipo e = EquipoService.crearEquipo(competicion, "a", "grupo1");
        try {
            EquipoService.modificarEquipo(competicion,e.getId(), null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Nombre de equipo no válido");
        }
    }
    
    @Test
    public void modificarEquipoNombreVacio() throws InputException{
        GrupoService.crearGrupo(competicion, "grupo1", null);
        Equipo e = EquipoService.crearEquipo(competicion, "a", "grupo1");
        try {
            EquipoService.modificarEquipo(competicion,e.getId(), "", null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Nombre de equipo no válido");
        }
    }
    
    @Test
    public void modificarEquipoNombreOcupado() throws InputException{
        GrupoService.crearGrupo(competicion, "grupo1", null);
        Equipo e = EquipoService.crearEquipo(competicion, "a", "grupo1");
        Equipo e2 = EquipoService.crearEquipo(competicion, "b", "grupo1");
        try {
            EquipoService.modificarEquipo(competicion,e.getId(), "b", null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Nombre de equipo ocupado");
        }
    }
    
    @Test
    public void modificarEquipoNombreOcupadoOtraCompeticion() throws InputException{
        Competicion c2 = CompeticionService.crearCompeticion("comp2", null, null, null, null, null);
        GrupoService.crearGrupo(c2, "grupo1", null);
        GrupoService.crearGrupo(competicion, "grupo1", null);
        Equipo e = EquipoService.crearEquipo(competicion, "a", "grupo1");
        Equipo e2 = EquipoService.crearEquipo(c2, "b", "grupo1");
        e2 = EquipoService.modificarEquipo(c2,e2.getId(), "a", "grupo1");
        assertNotNull(equipoJpa.findByNombreAndCompeticion("a", competicion.getId()));
        assertNotNull(equipoJpa.findByNombreAndCompeticion("a", c2.getId()));
        assertNull(equipoJpa.findByNombreAndCompeticion("b", c2.getId()));
        
        CompeticionService.eliminarCompeticion(c2.getNombre());
    }
    
    @Test
    public void modificarNombreEquipo() throws InputException{
        GrupoService.crearGrupo(competicion, "grupo1", null);
        Equipo e = EquipoService.crearEquipo(competicion, "a", "grupo1");
        e = EquipoService.modificarEquipo(competicion,e.getId(), "b", "grupo1");
        assertNotNull(equipoJpa.findByNombreAndCompeticion("b", competicion.getId()));
        assertNull(equipoJpa.findByNombreAndCompeticion("a", competicion.getId()));
    }
    
    // Modifica el grupo al que pertenece el equipo
    @Test
    public void modificarEquipoGrupo() throws InputException{
        GrupoService.crearGrupo(competicion, "grupo1", null);
        GrupoService.crearGrupo(competicion, "grupo2", null);
        Equipo e = EquipoService.crearEquipo(competicion, "a", "grupo1");
        e = EquipoService.modificarEquipo(competicion,e.getId(), "a", "grupo2");
        e = equipoJpa.findByNombreAndCompeticion("a", competicion.getId());
        assertNotNull(e);
        assertEquals(e.getGrupoId().getNombre(), "grupo2");
    }
    
    
    @Test
    public void modificarEquipoGrupoNoValido() throws InputException{
        GrupoService.crearGrupo(competicion, "grupo1", null);
        Equipo e = EquipoService.crearEquipo(competicion, "a", "grupo1");
        Equipo e2 = null;
        try {
            e2 = EquipoService.modificarEquipo(competicion,e.getId(), "a", null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertNull(e2);
            assertEquals(ex.getMessage(), "Nombre de Grupo no válido");
        }
    }
    
    @Test
    public void modificarEquipoGrupoNoExiste() throws InputException{
        GrupoService.crearGrupo(competicion, "grupo1", null);
        Equipo e = EquipoService.crearEquipo(competicion, "a", "grupo1");
        Equipo e2 = null;
        try {
            e2 = EquipoService.modificarEquipo(competicion,e.getId(), "a", "asdf");
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertNull(e2);
            assertEquals(ex.getMessage(), "Grupo no encontrado");
        }
    }
    
    // Modifica el grupo al que pertenece un equipo al que pertenecen varios miembros
    @Test
    public void modificarEquipoConParticipantesGrupo() throws InputException{
        Grupo g = GrupoService.crearGrupo(competicion, "grupo1", null);
        GrupoService.crearGrupo(competicion, "grupo2", null);
        Equipo e = EquipoService.crearEquipo(competicion, "a", "grupo1");
        Equipo e2 = null;
        ParticipanteService.crearParticipante(competicion, "n", "a", 1, g.getNombre(), null, null, "a", null);
        try {
            e2 = EquipoService.modificarEquipo(competicion,e.getId(), "a", "grupo2");
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertNull(e2);
            assertEquals(ex.getMessage(), "No se puede modificar el grupo de un equipo con miembros asignados");
        }
    }

}
