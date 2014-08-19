package testcases;

import modelo.logicaNegocio.ParticipanteService;
import modelo.logicaNegocio.PruebaService;
import controlador.InputException;
import modelo.logicaNegocio.RegistroService;
import java.util.Date;
import java.util.List;
import modelo.dao.RegistroJpa;
import modelo.entities.Competicion;
import modelo.entities.Equipo;
import modelo.entities.Participante;
import modelo.entities.Registro;
import modelo.entities.TipoPrueba;
import modelo.entities.TipoResultado;
import modelo.logicaNegocio.GrupoService;
import modelo.logicaNegocio.CompeticionService;
import modelo.logicaNegocio.EquipoService;
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
        competicion = CompeticionService.crearCompeticion("comp1", null, null, null, null, null);
    }

    @After
    public void destroy() throws InputException {
        CompeticionService.eliminarCompeticion("comp1");
    }
    
    // PRUEBAS SOBRE CREAR REGISTRO
    
    @Test
    public void crearRegistroNull(){
        Registro r = null;
        try {
            r = RegistroService.crearRegistro(null, null, null, null, null, null, null, null);
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
            r = RegistroService.crearRegistro(competicion, null, null, null, null, null, null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertNull(r);
            assertEquals(ex.getMessage(),"Nombre de prueba no válido");
        }
    }
    
    @Test
    public void crearRegistroDorsalNull() throws InputException{
        Registro r = null;
        PruebaService.crearPrueba(competicion,"prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        try {
            r = RegistroService.crearRegistroIndividualNum(competicion, null,"prueba1", null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertNull(r);
            assertEquals(ex.getMessage(),"Participante no válido");
        }
    }
    
    @Test
    public void crearRegistroMarcaNumNull() throws InputException{
        Registro r = null;
        PruebaService.crearPrueba(competicion,"prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        GrupoService.crearGrupo(competicion, "grupo1", null);
        Participante participante = ParticipanteService.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null);
        try {
            r = RegistroService.crearRegistroIndividualNum(competicion, participante.getDorsal(),"prueba1", null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertNull(r);
            assertEquals(ex.getMessage(),"Formato del registro no válido");
        }
    }
    
    @Test
    public void crearRegistroTiempoNull() throws InputException{
        Registro r = null;
        PruebaService.crearPrueba(competicion,"prueba1", TipoPrueba.Individual.toString(), TipoResultado.Tiempo.toString());
        GrupoService.crearGrupo(competicion, "grupo1", null);
        Participante participante = ParticipanteService.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null);
        try {
            r = RegistroService.crearRegistroIndividualTiempo(competicion, participante.getDorsal(),"prueba1", null, null,null,null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertNull(r);
            assertEquals(ex.getMessage(),"Formato de tiempo no válido");
        }
    }
    
    @Test
    public void crearRegistroNombreEquipoNull() throws InputException{
        Registro r = null;
        PruebaService.crearPrueba(competicion,"prueba1", TipoPrueba.Equipo.toString(), TipoResultado.Distancia.toString());
        try {
            r = RegistroService.crearRegistroEquipoNum(competicion, null, "prueba1", null, null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertNull(r);
            assertEquals(ex.getMessage(),"Equipo no válido");
        }
    }
    
    @Test
    public void crearRegistroTiempoIndividual() throws InputException{
        Registro r = null;
        PruebaService.crearPrueba(competicion,"prueba1", TipoPrueba.Individual.toString(), TipoResultado.Tiempo.toString());
        GrupoService.crearGrupo(competicion, "grupo1", null);
        Participante participante = ParticipanteService.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null);
        r = RegistroService.crearRegistroIndividualTiempo(competicion, participante.getDorsal(), "prueba1", null, 12.0,null,null);
        assertNotNull(r);
        assertEquals(0,r.getTiempo().getHours());
        assertEquals(0,r.getTiempo().getMinutes());
        assertEquals(12.0,r.getTiempo().getSeconds(),r.getTiempo().getTime());
        assertEquals(r.getNumIntento(),1);
    }
    
    @Test
    public void crearRegistroDistanciaIndividual() throws InputException{
        Registro r = null;
        PruebaService.crearPrueba(competicion,"prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        GrupoService.crearGrupo(competicion, "grupo1", null);
        Participante participante = ParticipanteService.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null);
        r = RegistroService.crearRegistroIndividualNum(competicion, participante.getDorsal(), "prueba1", null, 15.0);
        assertNotNull(r);
        assertEquals(15.0, r.getNum().doubleValue(),0);
    }
    
    @Test
    public void getTiempoNull(){
        Date date = RegistroService.getTiempo(null, null, null);
        assertNull(date);
    }
    
    // PRUEBAS SOBRE MODIFICAR REGISTROS
    
    @Test
    public void modificarRegistroNull(){
        Registro r = null;
        try {
            r = RegistroService.modificarRegistro(null, null, null,null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertNull(r);
            assertEquals("Registro no válido", ex.getMessage());
        }
    }
    
    @Test
    public void modificarRegistroMarcaNull() throws InputException{
        PruebaService.crearPrueba(competicion,"prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        GrupoService.crearGrupo(competicion, "grupo1", null);
        Participante participante = ParticipanteService.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null); 
        Registro r = RegistroService.crearRegistroIndividualNum(competicion, participante.getDorsal(), "prueba1", null, 15.0);
        Registro r2 = null;
        try {
            r2 = RegistroService.modificarRegistro(r.getId(), null, null,null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertNull(r2);
            assertEquals("Formato del registro no válido", ex.getMessage());
        }
    }
    
    @Test
    public void modificarRegistroMarcaDistancia() throws InputException{
        PruebaService.crearPrueba(competicion,"prueba1", TipoPrueba.Individual.toString(), TipoResultado.Distancia.toString());
        GrupoService.crearGrupo(competicion, "grupo1", null);
        Participante participante = ParticipanteService.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null); 
        Registro r = RegistroService.crearRegistroIndividualNum(competicion, participante.getDorsal(), "prueba1", null, 15.0);
        Registro r2 = RegistroService.modificarRegistro(r.getId(), 16.0, null,null);
        assertNotNull(r2);
        assertEquals(r2.getNum(),16,0);
    }
    
    @Test
    public void modificarRegistroMarcaNum() throws InputException{
        PruebaService.crearPrueba(competicion,"prueba1", TipoPrueba.Individual.toString(), TipoResultado.Numerica.toString());
        GrupoService.crearGrupo(competicion, "grupo1", null);
        Participante participante = ParticipanteService.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null); 
        Registro r = RegistroService.crearRegistroIndividualNum(competicion, participante.getDorsal(), "prueba1", null, 15.0);
        Registro r2 = RegistroService.modificarRegistro(r.getId(), 16.0, null,null);
        assertNotNull(r2);
        assertEquals(r2.getNum(),16,0);
    }
    
    @Test
    public void modificarRegistroMarcaTiempo() throws InputException{
        PruebaService.crearPrueba(competicion,"prueba1", TipoPrueba.Individual.toString(), TipoResultado.Tiempo.toString());
        GrupoService.crearGrupo(competicion, "grupo1", null);
        Participante participante = ParticipanteService.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null); 
        Registro r = RegistroService.crearRegistroIndividualTiempo(competicion, participante.getDorsal(), "prueba1", null, 15.0,20,1);
        Registro r2 = RegistroService.modificarRegistro(r.getId(), 16.0,35,1);
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
            RegistroService.eliminarRegistro(null);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals("Registro no válido", ex.getMessage());
        }
    }
    
    @Test
    public void eliminarRegistroNoExiste(){
        try {
            RegistroService.eliminarRegistro(-1);
            fail("Debería haber lanzado InputException");
        } catch (InputException ex) {
            assertEquals("Registro no encontrado", ex.getMessage());
        }
    }
    
    @Test
    public void eliminarRegistroIndividual() throws InputException{
        PruebaService.crearPrueba(competicion,"prueba1", TipoPrueba.Individual.toString(), TipoResultado.Tiempo.toString());
        GrupoService.crearGrupo(competicion, "grupo1", null);
        Participante participante = ParticipanteService.crearParticipante(competicion, "nombre2", "apellidos2", 1, "grupo1", null, null, null, null); 
        Registro r = RegistroService.crearRegistroIndividualTiempo(competicion, participante.getDorsal(), "prueba1", null, 15.0,20,1);
        RegistroService.eliminarRegistro(r.getId());
        List<Registro> registros = registrosJpa.findByParticipante(participante.getId());
        assertEquals(registros.size(),0);
    }
    
    @Test
    public void eliminarRegistroEquipo() throws InputException{
        PruebaService.crearPrueba(competicion,"prueba1", TipoPrueba.Equipo.toString(), TipoResultado.Tiempo.toString());
        GrupoService.crearGrupo(competicion, "grupo1", null);
        Equipo e = EquipoService.crearEquipo(competicion, "a", "grupo1");
        Registro r = RegistroService.crearRegistroEquipoTiempo(competicion, e.getNombre(), "prueba1", null, 15.0,20,1);
        RegistroService.eliminarRegistro(r.getId());
        List<Registro> registros = registrosJpa.findByEquipo(e.getId());
        assertEquals(registros.size(),0);
    }
}
