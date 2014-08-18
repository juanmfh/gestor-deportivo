package testcases;

import controlador.ControlCompeticiones;
import controlador.ControlEquipos;
import controlador.ControlGrupos;
import modelo.dao.ParticipanteJpa;
import modelo.dao.PruebaJpa;
import controlador.InputException;
import modelo.dao.CompeticionJpa;
import modelo.dao.RegistroJpa;
import java.util.Date;
import java.util.List;
import modelo.Competicion;
import modelo.Equipo;
import modelo.Participante;
import modelo.Registro;
import modelo.TipoPrueba;
import modelo.TipoResultado;
import modelo.dao.EquipoJpa;
import modelo.dao.GrupoJpa;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author JuanM
 */
public class RegistrosTest {
    
    private static RegistroJpa registrosJpa;
    private static Competicion competicion;
    
    @BeforeClass
    public static void setUpClass() {
        registrosJpa = new RegistroJpa();
    }
    
    @Before
    public void ini() throws InputException {
        competicion = CompeticionJpa.crearCompeticion("comp1", null, null, null, null, null);
    }

    @After
    public void destroy() throws InputException {
        CompeticionJpa.eliminarCompeticion("comp1");
    }
    
    // PRUEBAS SOBRE CREAR REGISTRO
    
    @Test
    public void crearRegistroNull(){
        Registro r = null;
        try {
            r = RegistroJpa.crearRegistro(null, null, null, null, null, null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertNull(r);
            assertEquals(ex.getMessage(),"Competición no válida");
        }
    }
    
    @Test
    public void crearRegistroCompeticionNotNull(){
        Registro r = null;
        try {
            r = RegistroJpa.crearRegistro(competicion, null, null, null, null, null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertNull(r);
            assertEquals(ex.getMessage(),"Nombre de prueba no válido");
        }
    }
    
    @Test
    public void crearRegistroDorsalNull() throws InputException{
        Registro r = null;
        PruebaJpa.crearPrueba(competicion,"prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        try {
            r = RegistroJpa.crearRegistroIndividualNum(competicion, null,"prueba1", null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertNull(r);
            assertEquals(ex.getMessage(),"Participante no válido");
        }
    }
    
    @Test
    public void crearRegistroMarcaNumNull() throws InputException{
        Registro r = null;
        PruebaJpa.crearPrueba(competicion,"prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        GrupoJpa.crearGrupo(competicion, "grupo1", null);
        Participante participante = ParticipanteJpa.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null);
        try {
            r = RegistroJpa.crearRegistroIndividualNum(competicion, participante.getDorsal(),"prueba1", null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertNull(r);
            assertEquals(ex.getMessage(),"Formato del registro no válido");
        }
    }
    
    @Test
    public void crearRegistroTiempoNull() throws InputException{
        Registro r = null;
        PruebaJpa.crearPrueba(competicion,"prueba1", TipoPrueba.Individual.toString(), TipoResultado.Tiempo.toString());
        GrupoJpa.crearGrupo(competicion, "grupo1", null);
        Participante participante = ParticipanteJpa.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null);
        try {
            r = RegistroJpa.crearRegistroIndividualTiempo(competicion, participante.getDorsal(),"prueba1", null, null,null,null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertNull(r);
            assertEquals(ex.getMessage(),"Formato de tiempo no válido");
        }
    }
    
    @Test
    public void crearRegistroNombreEquipoNull() throws InputException{
        Registro r = null;
        PruebaJpa.crearPrueba(competicion,"prueba1", TipoPrueba.Equipo.toString(), TipoResultado.Distancia.toString());
        try {
            r = RegistroJpa.crearRegistroEquipoNum(competicion, null, "prueba1", null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertNull(r);
            assertEquals(ex.getMessage(),"Equipo no válido");
        }
    }
    
    @Test
    public void crearRegistroTiempoIndividual() throws InputException{
        Registro r = null;
        PruebaJpa.crearPrueba(competicion,"prueba1", TipoPrueba.Individual.toString(), TipoResultado.Tiempo.toString());
        GrupoJpa.crearGrupo(competicion, "grupo1", null);
        Participante participante = ParticipanteJpa.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null);
        r = RegistroJpa.crearRegistroIndividualTiempo(competicion, participante.getDorsal(), "prueba1", null, 12.0,null,null);
        assertNotNull(r);
        assertEquals(0,r.getTiempo().getHours());
        assertEquals(0,r.getTiempo().getMinutes());
        assertEquals(12.0,r.getTiempo().getSeconds(),r.getTiempo().getTime());
        assertEquals(r.getNumIntento(),1);
    }
    
    @Test
    public void crearRegistroDistanciaIndividual() throws InputException{
        Registro r = null;
        PruebaJpa.crearPrueba(competicion,"prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        GrupoJpa.crearGrupo(competicion, "grupo1", null);
        Participante participante = ParticipanteJpa.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null);
        r = RegistroJpa.crearRegistroIndividualNum(competicion, participante.getDorsal(), "prueba1", null, 15.0);
        assertNotNull(r);
        assertEquals(15.0, r.getNum().doubleValue(),0);
    }
    
    @Test
    public void getTiempoNull(){
        Date date = RegistroJpa.getTiempo(null, null, null);
        assertNull(date);
    }
    
    // PRUEBAS SOBRE MODIFICAR REGISTROS
    
    @Test
    public void modificarRegistroNull(){
        Registro r = null;
        try {
            r = RegistroJpa.modificarRegistro(null, null, null,null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertNull(r);
            assertEquals("Registro no válido", ex.getMessage());
        }
    }
    
    @Test
    public void modificarRegistroMarcaNull() throws InputException{
        PruebaJpa.crearPrueba(competicion,"prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        GrupoJpa.crearGrupo(competicion, "grupo1", null);
        Participante participante = ParticipanteJpa.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null); 
        Registro r = RegistroJpa.crearRegistroIndividualNum(competicion, participante.getDorsal(), "prueba1", null, 15.0);
        Registro r2 = null;
        try {
            r2 = RegistroJpa.modificarRegistro(r.getId(), null, null,null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertNull(r2);
            assertEquals("Formato del registro no válido", ex.getMessage());
        }
    }
    
    @Test
    public void modificarRegistroMarcaDistancia() throws InputException{
        PruebaJpa.crearPrueba(competicion,"prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        GrupoJpa.crearGrupo(competicion, "grupo1", null);
        Participante participante = ParticipanteJpa.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null); 
        Registro r = RegistroJpa.crearRegistroIndividualNum(competicion, participante.getDorsal(), "prueba1", null, 15.0);
        Registro r2 = RegistroJpa.modificarRegistro(r.getId(), 16.0, null,null);
        assertNotNull(r2);
        assertEquals(r2.getNum(),16,0);
    }
    
    @Test
    public void modificarRegistroMarcaNum() throws InputException{
        PruebaJpa.crearPrueba(competicion,"prueba1", TipoPrueba.Individual.toString(), TipoResultado.Numerica.toString());
        GrupoJpa.crearGrupo(competicion, "grupo1", null);
        Participante participante = ParticipanteJpa.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null); 
        Registro r = RegistroJpa.crearRegistroIndividualNum(competicion, participante.getDorsal(), "prueba1", null, 15.0);
        Registro r2 = RegistroJpa.modificarRegistro(r.getId(), 16.0, null,null);
        assertNotNull(r2);
        assertEquals(r2.getNum(),16,0);
    }
    
    @Test
    public void modificarRegistroMarcaTiempo() throws InputException{
        PruebaJpa.crearPrueba(competicion,"prueba1", TipoPrueba.Individual.toString(), TipoResultado.Tiempo.toString());
        GrupoJpa.crearGrupo(competicion, "grupo1", null);
        Participante participante = ParticipanteJpa.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null); 
        Registro r = RegistroJpa.crearRegistroIndividualTiempo(competicion, participante.getDorsal(), "prueba1", null, 15.0,20,1);
        Registro r2 = RegistroJpa.modificarRegistro(r.getId(), 16.0,35,1);
        assertNotNull(r2);
        assertEquals(1,r2.getTiempo().getHours());
        assertEquals(35,r2.getTiempo().getMinutes());
        assertEquals(16.0,r2.getTiempo().getSeconds(),r2.getTiempo().getTime());
        assertEquals(r2.getNumIntento(),1);
    }
    
    
    
    // PRUEBAS SOBRE ELIMINAR REGISTROS
    
    @Test
    public void eliminarRegistroNull(){
        try {
            RegistroJpa.eliminarRegistro(null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals("Registro no válido", ex.getMessage());
        }
    }
    
    @Test
    public void eliminarRegistroNoExiste(){
        try {
            RegistroJpa.eliminarRegistro(-1);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals("Registro no encontrado", ex.getMessage());
        }
    }
    
    @Test
    public void eliminarRegistroIndividual() throws InputException{
        PruebaJpa.crearPrueba(competicion,"prueba1", TipoPrueba.Individual.toString(), TipoResultado.Tiempo.toString());
        GrupoJpa.crearGrupo(competicion, "grupo1", null);
        Participante participante = ParticipanteJpa.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null); 
        Registro r = RegistroJpa.crearRegistroIndividualTiempo(competicion, participante.getDorsal(), "prueba1", null, 15.0,20,1);
        RegistroJpa.eliminarRegistro(r.getId());
        List<Registro> registros = registrosJpa.findByParticipante(participante.getId());
        assertEquals(registros.size(),0);
    }
    
    @Test
    public void eliminarRegistroEquipo() throws InputException{
        PruebaJpa.crearPrueba(competicion,"prueba1", TipoPrueba.Equipo.toString(), TipoResultado.Tiempo.toString());
        GrupoJpa.crearGrupo(competicion, "grupo1", null);
        Equipo e = EquipoJpa.crearEquipo(competicion, "a", "grupo1");
        Registro r = RegistroJpa.crearRegistroEquipoTiempo(competicion, e.getNombre(), "prueba1", null, 15.0,20,1);
        RegistroJpa.eliminarRegistro(r.getId());
        List<Registro> registros = registrosJpa.findByEquipo(e.getId());
        assertEquals(registros.size(),0);
    }
}
