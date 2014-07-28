package testcases;

import controlador.ControlCompeticiones;
import controlador.ControlEquipos;
import controlador.ControlGrupos;
import controlador.ControlParticipantes;
import controlador.ControlPruebas;
import controlador.ControlRegistros;
import controlador.InputException;
import dao.ParticipanteJpa;
import dao.RegistroJpa;
import java.util.List;
import modelo.Competicion;
import modelo.Equipo;
import modelo.Participante;
import modelo.Prueba;
import modelo.Registro;
import modelo.TipoPrueba;
import modelo.TipoResultado;
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
        competicion = ControlCompeticiones.crearCompeticion("comp1", null, null, null, null, null);
    }

    @After
    public void destroy() throws InputException {
        ControlCompeticiones.eliminarCompeticion("comp1");
    }
    
     // PRUEBAS SOBRE CREAR PARTICIPANTE
     @Test
     public void crearParticipanteNull() {
     Participante p = null;
     try {
     p = ControlParticipantes.crearParticipante(null, null, null, null, null, null, null, null, null);
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
     p = ControlParticipantes.crearParticipante(competicion, null, null, null, null, null, null, null, null);
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
     p = ControlParticipantes.crearParticipante(competicion, "nombre1", null, null, null, null, null, null, null);
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
     p = ControlParticipantes.crearParticipante(competicion, "nombre1", "apellidos", null, null, null, null, null, null);
     fail("Debería haber lanzado InputException");
     } catch (InputException ex) {
     assertNull(p);
     assertEquals(ex.getMessage(), "Nombre de grupo no válido");
     }
     }

     @Test
     public void crearParticipanteDorsalNull() throws InputException {
     Participante p = null;
     ControlGrupos.crearGrupo(competicion, "grupo1", null);
     try {
     p = ControlParticipantes.crearParticipante(competicion, "nombre1", "apellidos", null, "grupo1", null, null, null, null);
     fail("Debería haber lanzado InputException");
     } catch (InputException ex) {
     assertNull(p);
     assertEquals(ex.getMessage(), "Dorsal no válido");
     }
     }

     @Test
     public void crearParticipanteDorsalOcupado() throws InputException {
     Participante p = null;
     ControlGrupos.crearGrupo(competicion, "grupo1", null);
     ControlParticipantes.crearParticipante(competicion, "nombre1", "apellidos", 1, "grupo1", null, null, null, null);
     try {
     p = ControlParticipantes.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null);
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
     p = ControlParticipantes.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null);
     fail("Debería haber lanzado InputException");
     } catch (InputException ex) {
     assertNull(p);
     assertEquals(ex.getMessage(), "Grupo no encontrado");
     }
     }

     @Test
     public void crearParticipantePruebaNoExiste() throws InputException {
     Participante p = null;
     ControlGrupos.crearGrupo(competicion, "grupo1", null);
     try {
     p = ControlParticipantes.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, "prueba1");
     fail("Debería haber lanzado InputException");
     } catch (InputException ex) {
     assertNull(p);
     assertEquals(ex.getMessage(), "Prueba no encontrada");
     }
     }

     @Test
     public void crearParticipanteEquipoNoExiste() throws InputException {
     Participante p = null;
     ControlGrupos.crearGrupo(competicion, "grupo1", null);
     try {
     p = ControlParticipantes.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, "equipo1", null);
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
     ControlGrupos.crearGrupo(competicion, "grupo1", null);
     p = ControlParticipantes.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null);
     assertNotNull(p);
     assertEquals(p.getNombre(), "nombre2");
     assertEquals(p.getApellidos(), "apellidos2");
     assertEquals(p.getGrupoId().getNombre(), "grupo1");
     assertNotNull(participantejpa.findByDorsalAndCompeticion(1, competicion.getId()));
     }

     @Test
     public void crearParticipanteEnEquipo() throws InputException {
     Participante p = null;
     ControlGrupos.crearGrupo(competicion, "grupo1", null);
     Equipo e = ControlEquipos.crearEquipo(competicion, "equipo1", "grupo1");
     p = ControlParticipantes.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, "equipo1", null);
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
     ControlGrupos.crearGrupo(competicion, "grupo1", null);
     ControlEquipos.crearEquipo(competicion, "equipo1", "grupo1");
     Prueba prueba = ControlPruebas.crearPrueba(competicion, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
     p = ControlParticipantes.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, "prueba1");
     assertNotNull(p);
     assertEquals(p.getPruebaasignada(), prueba);
     }
    
     // Crear dos participantes que tienen el mismo dorsal en competiciones distintas
     @Test
     public void crearParticipanteMismoDorsalCompeticiones() throws InputException{
     Competicion comp2 = ControlCompeticiones.crearCompeticion("comp2", null, null, null, null, null);
     ControlGrupos.crearGrupo(comp2, "grupo1", null);
     ControlGrupos.crearGrupo(competicion, "grupo1", null);
     ControlParticipantes.crearParticipante(competicion, "nombre1", "apellidos1", 1, "grupo1", null, null, null, null);
     ControlParticipantes.crearParticipante(comp2, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null);
     Participante p1 = participantejpa.findByDorsalAndCompeticion(1,competicion.getId());
     assertNotNull(p1);
     assertEquals(p1.getNombre(), "nombre1");
     Participante p2 = participantejpa.findByDorsalAndCompeticion(1,comp2.getId());
     assertNotNull(p2);
     assertEquals(p2.getNombre(), "nombre2");
     ControlCompeticiones.eliminarCompeticion("comp2");
     }

     // PRUEBAS SOBRE ELIMINAR PARTICIPANTE
     @Test
     public void eliminarParticipanteNull() {
     try {
     ControlParticipantes.eliminarParticipante(null);
     fail("Debería haber lanzado InputException");
     } catch (InputException ex) {
     assertEquals(ex.getMessage(), "Participante no válido");
     }
     }

     @Test
     public void eliminarParticipanteNoExiste() {
     try {
     ControlParticipantes.eliminarParticipante(-1);
     fail("Debería haber lanzado InputException");
     } catch (InputException ex) {
     assertEquals(ex.getMessage(), "Participante no encontrado");
     }
     }

     @Test
     public void eliminarParticipante() throws InputException {
     ControlGrupos.crearGrupo(competicion, "grupo1", null);
     Participante p = ControlParticipantes.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null);
     ControlParticipantes.eliminarParticipante(p.getId());
     assertNull(participantejpa.findByDorsalAndCompeticion(1, competicion.getId()));
     }

     // Elimina un participante que es miembro de un equipo
     @Test
     public void eliminarParticipanteEquipo() throws InputException {
     ControlGrupos.crearGrupo(competicion, "grupo1", null);
     Equipo e = ControlEquipos.crearEquipo(competicion, "equipo1", "grupo1");
     Participante p = ControlParticipantes.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, "equipo1", null);
     ControlParticipantes.eliminarParticipante(p.getId());
     assertNull(participantejpa.findByDorsalAndCompeticion(1, competicion.getId()));
     List<Participante> miembrosEquipo = participantejpa.findByEquipo(e.getId());
     assertEquals(miembrosEquipo.size(), 0);
     }

     // Elimina un participante que tiene una prueba asignada
     @Test
     public void eliminarParticipantePrueba() throws InputException {
     ControlGrupos.crearGrupo(competicion, "grupo1", null);
     ControlPruebas.crearPrueba(competicion, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
     Participante p = ControlParticipantes.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, "prueba1");
     ControlParticipantes.eliminarParticipante(p.getId());
     assertNull(participantejpa.findByDorsalAndCompeticion(1, competicion.getId()));
     }

     // Elimina un participante que tiene registros asociados
     @Test
     public void eliminarParticipanteConRegistros() throws InputException {
     ControlGrupos.crearGrupo(competicion, "grupo1", null);
     ControlPruebas.crearPrueba(competicion, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
     Participante p = ControlParticipantes.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, "prueba1");
     ControlRegistros.crearRegistro(competicion, 1, "prueba1", null, false, 12.0, null, null);
     ControlParticipantes.eliminarParticipante(p.getId());
     assertNull(participantejpa.findByDorsalAndCompeticion(1, competicion.getId()));
     RegistroJpa registroJpa = new RegistroJpa();
     List<Registro> registros = registroJpa.findByParticipante(p.getId());
     assertEquals(registros.size(), 0);
     }

    // PRUEBAS SOBRE MODIFICAR PARTICIPANTES
    @Test
    public void modificarParticipanteCompeticionNull() {
        try {
            ControlParticipantes.modificarParticipante(null, null, null, null, null, null, null, null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Competición no válida");
        }
    }

    @Test
    public void modificarParticipanteNull() {
        try {
            ControlParticipantes.modificarParticipante(competicion, null, null, null, null, null, null, null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Participante no válido");
        }
    }

    @Test
    public void modificarParticipanteNombreNull() throws InputException {
        ControlGrupos.crearGrupo(competicion, "grupo1", null);
        Participante p = ControlParticipantes.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null);
        try {
            ControlParticipantes.modificarParticipante(competicion, p.getId(), null, null, null, null, null, null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Nombre no válido");
        }
    }

    @Test
    public void modificarParticipanteApellidosNull() throws InputException {
        ControlGrupos.crearGrupo(competicion, "grupo1", null);
        Participante p = ControlParticipantes.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null);
        try {
            ControlParticipantes.modificarParticipante(competicion, p.getId(), "nombre1", null, null, null, null, null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Apellidos no válido");
        }
    }

    @Test
    public void modificarParticipanteDorsalNull() throws InputException {
        ControlGrupos.crearGrupo(competicion, "grupo1", null);
        Participante p = ControlParticipantes.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null);
        try {
            ControlParticipantes.modificarParticipante(competicion, p.getId(), "nombre1", "apellidos", null, "grupo1", null, null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Dorsal no válido");
        }
    }

    @Test
    public void modificarParticipanteDorsalOcupado() throws InputException {
        ControlGrupos.crearGrupo(competicion, "grupo1", null);
        Participante p = ControlParticipantes.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null);
        ControlParticipantes.crearParticipante(competicion, "nombre2", "apellidos2", 2, "grupo1", null, null, null, null);
        try {
            ControlParticipantes.modificarParticipante(competicion, p.getId(), "nombre1", "apellidos", 2, "grupo1", null, null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Dorsal ocupado");
        }
    }

    @Test
    public void modificarParticipanteGrupoNull() throws InputException {
        ControlGrupos.crearGrupo(competicion, "grupo1", null);
        Participante p = ControlParticipantes.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null);
        try {
            ControlParticipantes.modificarParticipante(competicion, p.getId(), "nombre1", "apellidos", 1, null, null, null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals(ex.getMessage(), "Nombre de grupo no válido");
        }
    }

    @Test
    public void modificarParticipanteMismoDorsal() throws InputException {
        ControlGrupos.crearGrupo(competicion, "grupo1", null);
        Participante p = ControlParticipantes.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null);
        Participante p2 = ControlParticipantes.modificarParticipante(competicion, p.getId(), "nombre1", "apellidos1", 1, "grupo1", null, null, null, null);
        assertNotNull(p2);
        assertEquals(p2.getNombre(), "nombre1");
        assertEquals(p2.getApellidos(), "apellidos1");
        assertEquals(p2.getDorsal(),1);
    }
    
    @Test
    public void modificarParticipanteDiferenteDorsal() throws InputException {
        ControlGrupos.crearGrupo(competicion, "grupo1", null);
        Participante p = ControlParticipantes.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null);
        Participante p2 = ControlParticipantes.modificarParticipante(competicion, p.getId(), "nombre1", "apellidos1", 2, "grupo1", null, null, null, null);
        assertNotNull(p2);
        assertEquals(p2.getNombre(), "nombre1");
        assertEquals(p2.getApellidos(), "apellidos1");
        assertEquals(p2.getDorsal(),2);
    }
    
    @Test
    public void modificarParticipanteDiferenteGrupo() throws InputException {
        ControlGrupos.crearGrupo(competicion, "grupo1", null);
        ControlGrupos.crearGrupo(competicion, "grupo2", null);
        Participante p = ControlParticipantes.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null);
        Participante p2 = ControlParticipantes.modificarParticipante(competicion, p.getId(), "nombre1", "apellidos1", 1, "grupo2", null, null, null, null);
        assertNotNull(p2);
        assertEquals(p2.getNombre(), "nombre1");
        assertEquals(p2.getApellidos(), "apellidos1");
        assertEquals(p2.getGrupoId().getNombre(),"grupo2");
        assertEquals(p2.getDorsal(),1);
    }
    
    @Test
    public void modificarParticipanteEquipo() throws InputException {
        ControlGrupos.crearGrupo(competicion, "grupo1", null);
        ControlEquipos.crearEquipo(competicion, "equipo1", "grupo1");
        Equipo e = ControlEquipos.crearEquipo(competicion, "equipo2", "grupo1");
        Participante p = ControlParticipantes.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, "equipo1", null);
        Participante p2 = ControlParticipantes.modificarParticipante(competicion, p.getId(), "nombre1", "apellidos1", 1, "grupo1", null, null, "equipo2", null);
        assertNotNull(p2);
        assertEquals(p2.getNombre(), "nombre1");
        assertEquals(p2.getApellidos(), "apellidos1");
        assertEquals(p2.getGrupoId().getNombre(),"grupo1");
        assertEquals(p2.getEquipoId(),e);
        assertEquals(p2.getDorsal(),1);
    }
    
    @Test
    public void modificarParticipantePruebaAsignada() throws InputException {
        ControlGrupos.crearGrupo(competicion, "grupo1", null);
        ControlPruebas.crearPrueba(competicion, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        Participante p = ControlParticipantes.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, "prueba1");
        Participante p2 = ControlParticipantes.modificarParticipante(competicion, p.getId(), "nombre1", "apellidos1", 1, "grupo1", null, null, null, null);
        assertNotNull(p2);
        assertEquals(p2.getPruebaasignada(),null);
    }
    
    @Test
    public void modificarParticipantePruebaAsignadaNoExiste()throws InputException  {
        ControlGrupos.crearGrupo(competicion, "grupo1", null);
        Participante p = ControlParticipantes.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null);
        Participante p2 =  null;
        try {
            p2 = ControlParticipantes.modificarParticipante(competicion, p.getId(), "nombre1", "apellidos1", 1, "grupo1", null, null, null, "prueba1");
        } catch (InputException ex) {
            assertNull(p2);
            assertEquals(ex.getMessage(),"Prueba no encontrada");
        }
    }
    
    @Test
    public void modificarParticipantePruebaAsignada2() throws InputException {
        ControlGrupos.crearGrupo(competicion, "grupo1", null);
        Prueba prueba = ControlPruebas.crearPrueba(competicion, "prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        Participante p = ControlParticipantes.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null);
        Participante p2 = ControlParticipantes.modificarParticipante(competicion, p.getId(), "nombre1", "apellidos1", 1, "grupo1", null, null, null, "prueba1");
        assertNotNull(p2);
        assertEquals(p2.getPruebaasignada(),prueba);
    }

    @Test
    public void modificarParticipanteEquipoNoExiste()throws InputException  {
        ControlGrupos.crearGrupo(competicion, "grupo1", null);
        Participante p = ControlParticipantes.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null);
        Participante p2 =  null;
        try {
            p2 = ControlParticipantes.modificarParticipante(competicion, p.getId(), "nombre1", "apellidos1", 1, "grupo1", null, null, "equipo1", null);
        } catch (InputException ex) {
            assertNull(p2);
            assertEquals(ex.getMessage(),"Equipo no encontrado");
        }
    }
}
