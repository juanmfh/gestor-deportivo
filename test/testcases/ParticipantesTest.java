package testcases;

import modelo.logicaNegocio.GrupoService;
import modelo.dao.PruebaJpa;
import controlador.ControlRegistros;
import controlador.InputException;
import modelo.dao.CompeticionJpa;
import modelo.logicaNegocio.ParticipanteService;
import modelo.dao.RegistroJpa;
import java.util.List;
import modelo.entities.Competicion;
import modelo.entities.Equipo;
import modelo.entities.Participante;
import modelo.entities.Prueba;
import modelo.entities.Registro;
import modelo.entities.TipoPrueba;
import modelo.entities.TipoResultado;
import modelo.dao.EquipoJpa;
import modelo.dao.ParticipanteJpa;
import modelo.logicaNegocio.CompeticionService;
import modelo.logicaNegocio.EquipoService;
import modelo.logicaNegocio.PruebaService;
import modelo.logicaNegocio.RegistroService;
import org.junit.After;
import static org.junit.Assert.*;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author JuanM
 */
public class ParticipantesTest {

    private Competicion competicion;
    private static ParticipanteJpa participantejpa;

    @BeforeClass
    public static void setUp() {
        participantejpa = new ParticipanteJpa();
    }

    @Before
    public void ini() throws InputException {
        competicion = CompeticionService.crearCompeticion("comp1", null, null, null, null, null);
    }

    @After
    public void destroy() throws InputException {
        CompeticionService.eliminarCompeticion("comp1");
    }
    
     // PRUEBAS SOBRE CREAR PARTICIPANTE
     @Test
     public void crearParticipanteNull() {
     Participante p = null;
     try {
     p = ParticipanteService.crearParticipante(null, null, null, null, null, null, null, null, null);
     fail("Debería haber lanzado InputException");
     } catch (InputException ex) {
     assertNull(p);
     assertEquals(ex.getMessage(), "Competición no válida");
     }
     }

     @Test
     public void crearParticipanteNombreNull() {
     Participante p = null;
     try {
     p = ParticipanteService.crearParticipante(competicion, null, null, null, null, null, null, null, null);
     fail("Debería haber lanzado InputException");
     } catch (InputException ex) {
     assertNull(p);
     assertEquals(ex.getMessage(), "Nombre no válido");
     }
     }

     @Test
     public void crearParticipanteApellidosNull() {
     Participante p = null;
     try {
     p = ParticipanteService.crearParticipante(competicion, "nombre1", null, null, null, null, null, null, null);
     fail("Debería haber lanzado InputException");
     } catch (InputException ex) {
     assertNull(p);
     assertEquals(ex.getMessage(), "Apellidos no válido");
     }
     }

     @Test
     public void crearParticipanteGrupoNull() {
     Participante p = null;
     try {
     p = ParticipanteService.crearParticipante(competicion, "nombre1", "apellidos", null, null, null, null, null, null);
     fail("Debería haber lanzado InputException");
     } catch (InputException ex) {
     assertNull(p);
     assertEquals(ex.getMessage(), "Nombre de grupo no válido");
     }
     }

     @Test
     public void crearParticipanteDorsalNull() throws InputException {
     Participante p = null;
     GrupoService.crearGrupo(competicion, "grupo1", null);
     try {
     p = ParticipanteService.crearParticipante(competicion, "nombre1", "apellidos", null, "grupo1", null, null, null, null);
     fail("Debería haber lanzado InputException");
     } catch (InputException ex) {
     assertNull(p);
     assertEquals(ex.getMessage(), "Dorsal no válido");
     }
     }

     @Test
     public void crearParticipanteDorsalOcupado() throws InputException {
     Participante p = null;
     GrupoService.crearGrupo(competicion, "grupo1", null);
     ParticipanteService.crearParticipante(competicion, "nombre1", "apellidos", 1, "grupo1", null, null, null, null);
     try {
     p = ParticipanteService.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null);
     fail("Debería haber lanzado InputException");
     } catch (InputException ex) {
     assertNull(p);
     assertEquals(ex.getMessage(), "Dorsal ocupado");
     }
     }

     @Test
     public void crearParticipanteGrupoNoExiste() throws InputException {
     Participante p = null;
     try {
     p = ParticipanteService.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null);
     fail("Debería haber lanzado InputException");
     } catch (InputException ex) {
     assertNull(p);
     assertEquals(ex.getMessage(), "Grupo no encontrado");
     }
     }

     @Test
     public void crearParticipantePruebaNoExiste() throws InputException {
     Participante p = null;
     GrupoService.crearGrupo(competicion, "grupo1", null);
     try {
     p = ParticipanteService.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, "prueba1");
     fail("Debería haber lanzado InputException");
     } catch (InputException ex) {
     assertNull(p);
     assertEquals(ex.getMessage(), "Prueba no encontrada");
     }
     }

     @Test
     public void crearParticipanteEquipoNoExiste() throws InputException {
     Participante p = null;
     GrupoService.crearGrupo(competicion, "grupo1", null);
     try {
     p = ParticipanteService.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, "equipo1", null);
     fail("Debería haber lanzado InputException");
     } catch (InputException ex) {
     assertNull(p);
     assertEquals(ex.getMessage(), "Equipo no encontrado");
     }
     }

     // Crea un participante con los datos obligatorios.
     @Test
     public void crearParticipanteCorrecto() throws InputException {
     Participante p = null;
     GrupoService.crearGrupo(competicion, "grupo1", null);
     p = ParticipanteService.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null);
     assertNotNull(p);
     assertEquals(p.getNombre(), "nombre2");
     assertEquals(p.getApellidos(), "apellidos2");
     assertEquals(p.getGrupoId().getNombre(), "grupo1");
     assertNotNull(participantejpa.findByDorsalAndCompeticion(1, competicion.getId()));
     }

     @Test
     public void crearParticipanteEnEquipo() throws InputException {
     Participante p = null;
     GrupoService.crearGrupo(competicion, "grupo1", null);
     Equipo e = EquipoService.crearEquipo(competicion, "equipo1", "grupo1");
     p = ParticipanteService.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, "equipo1", null);
     assertNotNull(p);
     assertEquals(p.getNombre(), "nombre2");
     assertEquals(p.getApellidos(), "apellidos2");
     assertEquals(p.getGrupoId().getNombre(), "grupo1");
     assertNotNull(participantejpa.findByDorsalAndCompeticion(1, competicion.getId()));
     List<Participante> participantes = participantejpa.findByEquipo(e.getId());
     assertNotNull(participantes);
     assertEquals(participantes.size(), 1);
     assertEquals(participantes.get(0).getId(), p.getId());
     }

     @Test
     public void crearParticipanteConPruebaAsignada() throws InputException {
     Participante p = null;
     GrupoService.crearGrupo(competicion, "grupo1", null);
     EquipoService.crearEquipo(competicion, "equipo1", "grupo1");
     Prueba prueba = PruebaService.crearPrueba(competicion, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
     p = ParticipanteService.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, "prueba1");
     assertNotNull(p);
     assertEquals(p.getPruebaasignada(), prueba);
     }
    
     // Crear dos participantes que tienen el mismo dorsal en competiciones distintas
     @Test
     public void crearParticipanteMismoDorsalCompeticiones() throws InputException{
     Competicion comp2 = CompeticionService.crearCompeticion("comp2", null, null, null, null, null);
     GrupoService.crearGrupo(comp2, "grupo1", null);
     GrupoService.crearGrupo(competicion, "grupo1", null);
     ParticipanteService.crearParticipante(competicion, "nombre1", "apellidos1", 1, "grupo1", null, null, null, null);
     ParticipanteService.crearParticipante(comp2, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null);
     Participante p1 = participantejpa.findByDorsalAndCompeticion(1,competicion.getId());
     assertNotNull(p1);
     assertEquals(p1.getNombre(), "nombre1");
     Participante p2 = participantejpa.findByDorsalAndCompeticion(1,comp2.getId());
     assertNotNull(p2);
     assertEquals(p2.getNombre(), "nombre2");
     CompeticionService.eliminarCompeticion("comp2");
     }

     // PRUEBAS SOBRE ELIMINAR PARTICIPANTE
     @Test
     public void eliminarParticipanteNull() {
     try {
     ParticipanteService.eliminarParticipante(null);
     fail("Debería haber lanzado InputException");
     } catch (InputException ex) {
     assertEquals(ex.getMessage(), "Participante no válido");
     }
     }

     @Test
     public void eliminarParticipanteNoExiste() {
     try {
     ParticipanteService.eliminarParticipante(-1);
     fail("Debería haber lanzado InputException");
     } catch (InputException ex) {
     assertEquals(ex.getMessage(), "Participante no encontrado");
     }
     }

     @Test
     public void eliminarParticipante() throws InputException {
     GrupoService.crearGrupo(competicion, "grupo1", null);
     Participante p = ParticipanteService.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null);
     ParticipanteService.eliminarParticipante(p.getId());
     assertNull(participantejpa.findByDorsalAndCompeticion(1, competicion.getId()));
     }

     // Elimina un participante que es miembro de un equipo
     @Test
     public void eliminarParticipanteEquipo() throws InputException {
     GrupoService.crearGrupo(competicion, "grupo1", null);
     Equipo e = EquipoService.crearEquipo(competicion, "equipo1", "grupo1");
     Participante p = ParticipanteService.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, "equipo1", null);
     ParticipanteService.eliminarParticipante(p.getId());
     assertNull(participantejpa.findByDorsalAndCompeticion(1, competicion.getId()));
     List<Participante> miembrosEquipo = participantejpa.findByEquipo(e.getId());
     assertEquals(miembrosEquipo.size(), 0);
     }

     // Elimina un participante que tiene una prueba asignada
     @Test
     public void eliminarParticipantePrueba() throws InputException {
     GrupoService.crearGrupo(competicion, "grupo1", null);
     PruebaService.crearPrueba(competicion, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
     Participante p = ParticipanteService.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, "prueba1");
     ParticipanteService.eliminarParticipante(p.getId());
     assertNull(participantejpa.findByDorsalAndCompeticion(1, competicion.getId()));
     }

     // Elimina un participante que tiene registros asociados
     @Test
     public void eliminarParticipanteConRegistros() throws InputException {
     GrupoService.crearGrupo(competicion, "grupo1", null);
     PruebaService.crearPrueba(competicion, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
     Participante p = ParticipanteService.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, "prueba1");
     RegistroService.crearRegistro(competicion, 1, "prueba1", null, false, 12.0, null, null);
     ParticipanteService.eliminarParticipante(p.getId());
     assertNull(participantejpa.findByDorsalAndCompeticion(1, competicion.getId()));
     RegistroJpa registroJpa = new RegistroJpa();
     List<Registro> registros = registroJpa.findByParticipante(p.getId());
     assertEquals(registros.size(), 0);
     }

    // PRUEBAS SOBRE MODIFICAR PARTICIPANTES
    @Test
    public void modificarParticipanteCompeticionNull() {
        try {
            ParticipanteService.modificarParticipante(null, null, null, null, null, null, null, null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Competición no válida");
        }
    }

    @Test
    public void modificarParticipanteNull() {
        try {
            ParticipanteService.modificarParticipante(competicion, null, null, null, null, null, null, null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Participante no válido");
        }
    }

    @Test
    public void modificarParticipanteNombreNull() throws InputException {
        GrupoService.crearGrupo(competicion, "grupo1", null);
        Participante p = ParticipanteService.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null);
        try {
            ParticipanteService.modificarParticipante(competicion, p.getId(), null, null, null, null, null, null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Nombre no válido");
        }
    }

    @Test
    public void modificarParticipanteApellidosNull() throws InputException {
        GrupoService.crearGrupo(competicion, "grupo1", null);
        Participante p = ParticipanteService.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null);
        try {
            ParticipanteService.modificarParticipante(competicion, p.getId(), "nombre1", null, null, null, null, null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Apellidos no válido");
        }
    }

    @Test
    public void modificarParticipanteDorsalNull() throws InputException {
        GrupoService.crearGrupo(competicion, "grupo1", null);
        Participante p = ParticipanteService.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null);
        try {
            ParticipanteService.modificarParticipante(competicion, p.getId(), "nombre1", "apellidos", null, "grupo1", null, null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Dorsal no válido");
        }
    }

    @Test
    public void modificarParticipanteDorsalOcupado() throws InputException {
        GrupoService.crearGrupo(competicion, "grupo1", null);
        Participante p = ParticipanteService.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null);
        ParticipanteService.crearParticipante(competicion, "nombre2", "apellidos2", 2, "grupo1", null, null, null, null);
        try {
            ParticipanteService.modificarParticipante(competicion, p.getId(), "nombre1", "apellidos", 2, "grupo1", null, null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Dorsal ocupado");
        }
    }

    @Test
    public void modificarParticipanteGrupoNull() throws InputException {
        GrupoService.crearGrupo(competicion, "grupo1", null);
        Participante p = ParticipanteService.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null);
        try {
            ParticipanteService.modificarParticipante(competicion, p.getId(), "nombre1", "apellidos", 1, null, null, null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Nombre de grupo no válido");
        }
    }

    @Test
    public void modificarParticipanteMismoDorsal() throws InputException {
        GrupoService.crearGrupo(competicion, "grupo1", null);
        Participante p = ParticipanteService.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null);
        Participante p2 = ParticipanteService.modificarParticipante(competicion, p.getId(), "nombre1", "apellidos1", 1, "grupo1", null, null, null, null);
        assertNotNull(p2);
        assertEquals(p2.getNombre(), "nombre1");
        assertEquals(p2.getApellidos(), "apellidos1");
        assertEquals(p2.getDorsal(),1);
    }
    
    @Test
    public void modificarParticipanteDiferenteDorsal() throws InputException {
        GrupoService.crearGrupo(competicion, "grupo1", null);
        Participante p = ParticipanteService.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null);
        Participante p2 = ParticipanteService.modificarParticipante(competicion, p.getId(), "nombre1", "apellidos1", 2, "grupo1", null, null, null, null);
        assertNotNull(p2);
        assertEquals(p2.getNombre(), "nombre1");
        assertEquals(p2.getApellidos(), "apellidos1");
        assertEquals(p2.getDorsal(),2);
    }
    
    @Test
    public void modificarParticipanteDiferenteGrupo() throws InputException {
        GrupoService.crearGrupo(competicion, "grupo1", null);
        GrupoService.crearGrupo(competicion, "grupo2", null);
        Participante p = ParticipanteService.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null);
        Participante p2 = ParticipanteService.modificarParticipante(competicion, p.getId(), "nombre1", "apellidos1", 1, "grupo2", null, null, null, null);
        assertNotNull(p2);
        assertEquals(p2.getNombre(), "nombre1");
        assertEquals(p2.getApellidos(), "apellidos1");
        assertEquals(p2.getGrupoId().getNombre(),"grupo2");
        assertEquals(p2.getDorsal(),1);
    }
    
    @Test
    public void modificarParticipanteEquipo() throws InputException {
        GrupoService.crearGrupo(competicion, "grupo1", null);
        EquipoService.crearEquipo(competicion, "equipo1", "grupo1");
        Equipo e = EquipoService.crearEquipo(competicion, "equipo2", "grupo1");
        Participante p = ParticipanteService.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, "equipo1", null);
        Participante p2 = ParticipanteService.modificarParticipante(competicion, p.getId(), "nombre1", "apellidos1", 1, "grupo1", null, null, "equipo2", null);
        assertNotNull(p2);
        assertEquals(p2.getNombre(), "nombre1");
        assertEquals(p2.getApellidos(), "apellidos1");
        assertEquals(p2.getGrupoId().getNombre(),"grupo1");
        assertEquals(p2.getEquipoId(),e);
        assertEquals(p2.getDorsal(),1);
    }
    
    @Test
    public void modificarParticipantePruebaAsignada() throws InputException {
        GrupoService.crearGrupo(competicion, "grupo1", null);
        PruebaService.crearPrueba(competicion, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        Participante p = ParticipanteService.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, "prueba1");
        Participante p2 = ParticipanteService.modificarParticipante(competicion, p.getId(), "nombre1", "apellidos1", 1, "grupo1", null, null, null, null);
        assertNotNull(p2);
        assertEquals(p2.getPruebaasignada(),null);
    }
    
    @Test
    public void modificarParticipantePruebaAsignadaNoExiste()throws InputException  {
        GrupoService.crearGrupo(competicion, "grupo1", null);
        Participante p = ParticipanteService.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null);
        Participante p2 =  null;
        try {
            p2 = ParticipanteService.modificarParticipante(competicion, p.getId(), "nombre1", "apellidos1", 1, "grupo1", null, null, null, "prueba1");
        } catch (InputException ex) {
            assertNull(p2);
            assertEquals(ex.getMessage(),"Prueba no encontrada");
        }
    }
    
    @Test
    public void modificarParticipantePruebaAsignada2() throws InputException {
        GrupoService.crearGrupo(competicion, "grupo1", null);
        Prueba prueba = PruebaService.crearPrueba(competicion, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        Participante p = ParticipanteService.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null);
        Participante p2 = ParticipanteService.modificarParticipante(competicion, p.getId(), "nombre1", "apellidos1", 1, "grupo1", null, null, null, "prueba1");
        assertNotNull(p2);
        assertEquals(p2.getPruebaasignada(),prueba);
    }

    @Test
    public void modificarParticipanteEquipoNoExiste()throws InputException  {
        GrupoService.crearGrupo(competicion, "grupo1", null);
        Participante p = ParticipanteService.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null);
        Participante p2 =  null;
        try {
            p2 = ParticipanteService.modificarParticipante(competicion, p.getId(), "nombre1", "apellidos1", 1, "grupo1", null, null, "equipo1", null);
        } catch (InputException ex) {
            assertNull(p2);
            assertEquals(ex.getMessage(),"Equipo no encontrado");
        }
    }
}
