package testcases;

import controlador.ControlParticipantes;
import controlador.InputException;
import modelo.dao.CompeticionJpa;
import modelo.dao.EquipoJpa;
import modelo.dao.ParticipanteJpa;
import modelo.Competicion;
import modelo.Equipo;
import modelo.Grupo;
import modelo.Participante;
import modelo.dao.GrupoJpa;
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
        competicion = CompeticionJpa.crearCompeticion("comp1", null, null, null, null, null);
    }

    @After
    public void destroy() throws InputException {
        CompeticionJpa.eliminarCompeticion("comp1");
    }

    // PRUEBAS SOBRE CREAR EQUIPO
    @Test
    public void crearEquipoTodoNull() {
        Equipo e = null;
        try {
            e = EquipoJpa.crearEquipo(null, null, null);
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
            e = EquipoJpa.crearEquipo(competicion, null, null);
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
            e = EquipoJpa.crearEquipo(competicion, "", null);
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
            e = EquipoJpa.crearEquipo(competicion, "a", null);
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
            e = EquipoJpa.crearEquipo(competicion, "a", "b");
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Grupo no encontrado");
            assertNull(e);
        }
    }

    @Test
    public void crearEquipoValido() throws InputException {
        Equipo e = null;
        GrupoJpa.crearGrupo(competicion, "grupo1", null);
        e = EquipoJpa.crearEquipo(competicion, "a", "grupo1");
        assertNotNull(e);
        assertNotNull(equipoJpa.findByNombreAndCompeticion(e.getNombre(), competicion.getId()));
    }

    @Test
    public void crearEquipoNombreOcupado() throws InputException {
        Equipo e = null;
        GrupoJpa.crearGrupo(competicion, "grupo1", null);
        EquipoJpa.crearEquipo(competicion, "a", "grupo1");
        try {
            e = EquipoJpa.crearEquipo(competicion, "a", "grupo1");
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertNull(e);
            assertEquals(ex.getMessage(), "Nombre de equipo ocupado");
        }
    }
    
    @Test
    public void crearEquipoNombreOcupadoOtroGrupo() throws InputException {
        Equipo e = null;
        GrupoJpa.crearGrupo(competicion, "grupo1", null);
        GrupoJpa.crearGrupo(competicion, "grupo2", null);
        EquipoJpa.crearEquipo(competicion, "a", "grupo1");
        try {
            e = EquipoJpa.crearEquipo(competicion, "a", "grupo2");
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertNull(e);
            assertEquals(ex.getMessage(), "Nombre de equipo ocupado");
        }
    }
    
    @Test
    public void crearEquipoNombreOcupadoOtraCompeticion() throws InputException {
        Equipo e1,e2 = null;
        Competicion c2 = CompeticionJpa.crearCompeticion("comp2", null, null, null, null, null);
        GrupoJpa.crearGrupo(competicion, "grupo1", null);
        GrupoJpa.crearGrupo(c2, "grupo1", null);
        e1 =EquipoJpa.crearEquipo(competicion, "a", "grupo1");
        e2 = EquipoJpa.crearEquipo(c2, "a", "grupo1");
        assertNotNull(e1);
        assertNotNull(e2);
        assertNotNull(equipoJpa.findByNombreAndCompeticion(e1.getNombre(), competicion.getId()));
        assertNotNull(equipoJpa.findByNombreAndCompeticion(e2.getNombre(), c2.getId()));
        CompeticionJpa.eliminarCompeticion(c2.getNombre());
    }
    
    // PRUEBAS SOBRE ELIMINAR EQUIPO
    
    @Test
    public void eliminarEquipoNull(){
        try {
            EquipoJpa.eliminarEquipo(null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(),"Equipo no válido");
        }
    }
    
    @Test
    public void eliminarEquipoNoValido(){
        try {
            EquipoJpa.eliminarEquipo(-1);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(),"Equipo no encontrado");
        }
    }
    
    @Test
    public void eliminarEquipoValido() throws InputException{
        GrupoJpa.crearGrupo(competicion, "grupo1", null);
        Equipo e = EquipoJpa.crearEquipo(competicion, "a", "grupo1");
        EquipoJpa.eliminarEquipo(e.getId());
        e = equipoJpa.findByNombreAndCompeticion("a", competicion.getId());
        assertNull(e);
    }
    
    // Comprueba que no se eliminan los participantes del equipo
    @Test
    public void eliminarEquipoConParticipantes() throws InputException{
        Competicion c2 = CompeticionJpa.crearCompeticion("comp2", null, null, null, null, null);
        GrupoJpa.crearGrupo(c2, "grupo1", null);
        Grupo g =GrupoJpa.crearGrupo(competicion, "grupo1", null);
        Equipo e = EquipoJpa.crearEquipo(competicion, "a", "grupo1");
        Equipo e2 = EquipoJpa.crearEquipo(c2, "a", "grupo1");
        EquipoJpa.eliminarEquipo(e2.getId());
        e = equipoJpa.findByNombreAndCompeticion("a", competicion.getId());
        assertNotNull(e);
        e2 = equipoJpa.findByNombreAndCompeticion("a", c2.getId());
        assertNull(e2);
        CompeticionJpa.eliminarCompeticion(c2.getNombre());
    }
    
    // Comprueba que se elimina el equipo correcto
    @Test
    public void eliminarEquipoMismoNombre2Competiciones() throws InputException{
        Grupo g = GrupoJpa.crearGrupo(competicion, "grupo1", null);
        Equipo e = EquipoJpa.crearEquipo(competicion, "a", "grupo1");
        ParticipanteJpa.crearParticipante(competicion, "n", "a", 1, g.getNombre(), null, null, null, null);
        EquipoJpa.eliminarEquipo(e.getId());
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
            EquipoJpa.modificarEquipo(null,null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Competición no válida");
        }
    }
    
    @Test
    public void modificarEquipoIdNull(){
        try {
            EquipoJpa.modificarEquipo(competicion,null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Equipo no válido");
        }
    }
    
    @Test
    public void modificarEquipoNombreNull() throws InputException{
        GrupoJpa.crearGrupo(competicion, "grupo1", null);
        Equipo e = EquipoJpa.crearEquipo(competicion, "a", "grupo1");
        try {
            EquipoJpa.modificarEquipo(competicion,e.getId(), null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Nombre de equipo no válido");
        }
    }
    
    @Test
    public void modificarEquipoNombreVacio() throws InputException{
        GrupoJpa.crearGrupo(competicion, "grupo1", null);
        Equipo e = EquipoJpa.crearEquipo(competicion, "a", "grupo1");
        try {
            EquipoJpa.modificarEquipo(competicion,e.getId(), "", null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Nombre de equipo no válido");
        }
    }
    
    @Test
    public void modificarEquipoNombreOcupado() throws InputException{
        GrupoJpa.crearGrupo(competicion, "grupo1", null);
        Equipo e = EquipoJpa.crearEquipo(competicion, "a", "grupo1");
        Equipo e2 = EquipoJpa.crearEquipo(competicion, "b", "grupo1");
        try {
            EquipoJpa.modificarEquipo(competicion,e.getId(), "b", null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Nombre de equipo ocupado");
        }
    }
    
    @Test
    public void modificarEquipoNombreOcupadoOtraCompeticion() throws InputException{
        Competicion c2 = CompeticionJpa.crearCompeticion("comp2", null, null, null, null, null);
        GrupoJpa.crearGrupo(c2, "grupo1", null);
        GrupoJpa.crearGrupo(competicion, "grupo1", null);
        Equipo e = EquipoJpa.crearEquipo(competicion, "a", "grupo1");
        Equipo e2 = EquipoJpa.crearEquipo(c2, "b", "grupo1");
        e2 = EquipoJpa.modificarEquipo(c2,e2.getId(), "a", "grupo1");
        assertNotNull(equipoJpa.findByNombreAndCompeticion("a", competicion.getId()));
        assertNotNull(equipoJpa.findByNombreAndCompeticion("a", c2.getId()));
        assertNull(equipoJpa.findByNombreAndCompeticion("b", c2.getId()));
        
        CompeticionJpa.eliminarCompeticion(c2.getNombre());
    }
    
    @Test
    public void modificarNombreEquipo() throws InputException{
        GrupoJpa.crearGrupo(competicion, "grupo1", null);
        Equipo e = EquipoJpa.crearEquipo(competicion, "a", "grupo1");
        e = EquipoJpa.modificarEquipo(competicion,e.getId(), "b", "grupo1");
        assertNotNull(equipoJpa.findByNombreAndCompeticion("b", competicion.getId()));
        assertNull(equipoJpa.findByNombreAndCompeticion("a", competicion.getId()));
    }
    
    // Modifica el grupo al que pertenece el equipo
    @Test
    public void modificarEquipoGrupo() throws InputException{
        GrupoJpa.crearGrupo(competicion, "grupo1", null);
        GrupoJpa.crearGrupo(competicion, "grupo2", null);
        Equipo e = EquipoJpa.crearEquipo(competicion, "a", "grupo1");
        e = EquipoJpa.modificarEquipo(competicion,e.getId(), "a", "grupo2");
        e = equipoJpa.findByNombreAndCompeticion("a", competicion.getId());
        assertNotNull(e);
        assertEquals(e.getGrupoId().getNombre(), "grupo2");
    }
    
    
    @Test
    public void modificarEquipoGrupoNoValido() throws InputException{
        GrupoJpa.crearGrupo(competicion, "grupo1", null);
        Equipo e = EquipoJpa.crearEquipo(competicion, "a", "grupo1");
        Equipo e2 = null;
        try {
            e2 = EquipoJpa.modificarEquipo(competicion,e.getId(), "a", null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertNull(e2);
            assertEquals(ex.getMessage(), "Nombre de Grupo no válido");
        }
    }
    
    @Test
    public void modificarEquipoGrupoNoExiste() throws InputException{
        GrupoJpa.crearGrupo(competicion, "grupo1", null);
        Equipo e = EquipoJpa.crearEquipo(competicion, "a", "grupo1");
        Equipo e2 = null;
        try {
            e2 = EquipoJpa.modificarEquipo(competicion,e.getId(), "a", "asdf");
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertNull(e2);
            assertEquals(ex.getMessage(), "Grupo no encontrado");
        }
    }
    
    // Modifica el grupo al que pertenece un equipo al que pertenecen varios miembros
    @Test
    public void modificarEquipoConParticipantesGrupo() throws InputException{
        Grupo g = GrupoJpa.crearGrupo(competicion, "grupo1", null);
        GrupoJpa.crearGrupo(competicion, "grupo2", null);
        Equipo e = EquipoJpa.crearEquipo(competicion, "a", "grupo1");
        Equipo e2 = null;
        ParticipanteJpa.crearParticipante(competicion, "n", "a", 1, g.getNombre(), null, null, "a", null);
        try {
            e2 = EquipoJpa.modificarEquipo(competicion,e.getId(), "a", "grupo2");
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertNull(e2);
            assertEquals(ex.getMessage(), "No se puede modificar el grupo de un equipo con miembros asignados");
        }
    }

}
