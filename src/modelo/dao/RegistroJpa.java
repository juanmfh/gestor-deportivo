package modelo.dao;

import controlador.InputException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import modelo.Prueba;
import modelo.Participante;
import modelo.Inscripcion;
import modelo.Equipo;
import modelo.Registro;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import modelo.Competicion;
import modelo.Grupo;
import modelo.TipoPrueba;
import modelo.TipoResultado;
import modelo.dao.exceptions.NonexistentEntityException;

/**
 *
 * @author JuanM
 */
public class RegistroJpa implements Serializable {

    public RegistroJpa() {
        this.emf = Persistence.createEntityManagerFactory("JavaAppDesktopPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Registro registro) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Prueba pruebaId = registro.getPruebaId();
            if (pruebaId != null) {
                pruebaId = em.getReference(pruebaId.getClass(), pruebaId.getId());
                registro.setPruebaId(pruebaId);
            }
            Participante participanteId = registro.getParticipanteId();
            if (participanteId != null) {
                participanteId = em.getReference(participanteId.getClass(), participanteId.getId());
                registro.setParticipanteId(participanteId);
            }
            Inscripcion inscripcionId = registro.getInscripcionId();
            if (inscripcionId != null) {
                inscripcionId = em.getReference(inscripcionId.getClass(), inscripcionId.getId());
                registro.setInscripcionId(inscripcionId);
            }
            Equipo equipoId = registro.getEquipoId();
            if (equipoId != null) {
                equipoId = em.getReference(equipoId.getClass(), equipoId.getId());
                registro.setEquipoId(equipoId);
            }
            em.persist(registro);
            if (pruebaId != null) {
                pruebaId.getRegistroCollection().add(registro);
                pruebaId = em.merge(pruebaId);
            }
            if (participanteId != null) {
                participanteId.getRegistroCollection().add(registro);
                participanteId = em.merge(participanteId);
            }
            if (inscripcionId != null) {
                inscripcionId.getRegistroCollection().add(registro);
                inscripcionId = em.merge(inscripcionId);
            }
            if (equipoId != null) {
                equipoId.getRegistroCollection().add(registro);
                equipoId = em.merge(equipoId);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Registro registro) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Registro persistentRegistro = em.find(Registro.class, registro.getId());
            Prueba pruebaIdOld = persistentRegistro.getPruebaId();
            Prueba pruebaIdNew = registro.getPruebaId();
            Participante participanteIdOld = persistentRegistro.getParticipanteId();
            Participante participanteIdNew = registro.getParticipanteId();
            Inscripcion inscripcionIdOld = persistentRegistro.getInscripcionId();
            Inscripcion inscripcionIdNew = registro.getInscripcionId();
            Equipo equipoIdOld = persistentRegistro.getEquipoId();
            Equipo equipoIdNew = registro.getEquipoId();
            if (pruebaIdNew != null) {
                pruebaIdNew = em.getReference(pruebaIdNew.getClass(), pruebaIdNew.getId());
                registro.setPruebaId(pruebaIdNew);
            }
            if (participanteIdNew != null) {
                participanteIdNew = em.getReference(participanteIdNew.getClass(), participanteIdNew.getId());
                registro.setParticipanteId(participanteIdNew);
            }
            if (inscripcionIdNew != null) {
                inscripcionIdNew = em.getReference(inscripcionIdNew.getClass(), inscripcionIdNew.getId());
                registro.setInscripcionId(inscripcionIdNew);
            }
            if (equipoIdNew != null) {
                equipoIdNew = em.getReference(equipoIdNew.getClass(), equipoIdNew.getId());
                registro.setEquipoId(equipoIdNew);
            }
            registro = em.merge(registro);
            if (pruebaIdOld != null && !pruebaIdOld.equals(pruebaIdNew)) {
                pruebaIdOld.getRegistroCollection().remove(registro);
                pruebaIdOld = em.merge(pruebaIdOld);
            }
            if (pruebaIdNew != null && !pruebaIdNew.equals(pruebaIdOld)) {
                pruebaIdNew.getRegistroCollection().add(registro);
                pruebaIdNew = em.merge(pruebaIdNew);
            }
            if (participanteIdOld != null && !participanteIdOld.equals(participanteIdNew)) {
                participanteIdOld.getRegistroCollection().remove(registro);
                participanteIdOld = em.merge(participanteIdOld);
            }
            if (participanteIdNew != null && !participanteIdNew.equals(participanteIdOld)) {
                participanteIdNew.getRegistroCollection().add(registro);
                participanteIdNew = em.merge(participanteIdNew);
            }
            if (inscripcionIdOld != null && !inscripcionIdOld.equals(inscripcionIdNew)) {
                inscripcionIdOld.getRegistroCollection().remove(registro);
                inscripcionIdOld = em.merge(inscripcionIdOld);
            }
            if (inscripcionIdNew != null && !inscripcionIdNew.equals(inscripcionIdOld)) {
                inscripcionIdNew.getRegistroCollection().add(registro);
                inscripcionIdNew = em.merge(inscripcionIdNew);
            }
            if (equipoIdOld != null && !equipoIdOld.equals(equipoIdNew)) {
                equipoIdOld.getRegistroCollection().remove(registro);
                equipoIdOld = em.merge(equipoIdOld);
            }
            if (equipoIdNew != null && !equipoIdNew.equals(equipoIdOld)) {
                equipoIdNew.getRegistroCollection().add(registro);
                equipoIdNew = em.merge(equipoIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = registro.getId();
                if (findRegistro(id) == null) {
                    throw new NonexistentEntityException("The registro with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Registro registro;
            try {
                registro = em.getReference(Registro.class, id);
                registro.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The registro with id " + id + " no longer exists.", enfe);
            }
            Prueba pruebaId = registro.getPruebaId();
            if (pruebaId != null) {
                pruebaId.getRegistroCollection().remove(registro);
                pruebaId = em.merge(pruebaId);
            }
            Participante participanteId = registro.getParticipanteId();
            if (participanteId != null) {
                participanteId.getRegistroCollection().remove(registro);
                participanteId = em.merge(participanteId);
            }
            Inscripcion inscripcionId = registro.getInscripcionId();
            if (inscripcionId != null) {
                inscripcionId.getRegistroCollection().remove(registro);
                inscripcionId = em.merge(inscripcionId);
            }
            Equipo equipoId = registro.getEquipoId();
            if (equipoId != null) {
                equipoId.getRegistroCollection().remove(registro);
                equipoId = em.merge(equipoId);
            }
            em.remove(registro);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Registro> findRegistroEntities() {
        return findRegistroEntities(true, -1, -1);
    }

    public List<Registro> findRegistroEntities(int maxResults, int firstResult) {
        return findRegistroEntities(false, maxResults, firstResult);
    }

    private List<Registro> findRegistroEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Registro.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Registro findRegistro(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Registro.class, id);
        } finally {
            em.close();
        }
    }

    public int getRegistroCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Registro> rt = cq.from(Registro.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    // author: Juan María Frías Hidalgo
    
    /** Retorna la lista de registros a partir de una inscripción
     * 
     * @param inscripcionid     Identificador de la inscripción.
     * @return List<Registro>
     */
    public List<Registro> findByInscripcion(Integer inscripcionid) {
        EntityManager em = getEntityManager();
        List<Registro> res;
        try {
            Query q = em.createNamedQuery("Registro.findByInscripcion");
            q.setParameter("inscripcionid", inscripcionid);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    /**Devuelve la lista completa de registros de una competición.
     * 
     * @param competicionid     Identificador de la competición.
     * @return List<Registro>
     */
    public List<Registro> findByCompeticion(Integer competicionid) {
        EntityManager em = getEntityManager();
        List<Registro> res;
        try {
            Query q = em.createNamedQuery("Registro.findByCompeticion");
            q.setParameter("competicionid", competicionid);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    /**Devuelve la lista de registros de pruebas de tipo individual
     * de una competición.
     * 
     * @param competicionid     Identificador de la competición.
     * @return List<Registro>
     */
    public List<Registro> findByCompeticionIndividual(Integer competicionid) {
        EntityManager em = getEntityManager();
        List<Registro> res;
        try {
            Query q = em.createNamedQuery("Registro.findByCompeticionIndividual");
            q.setParameter("competicionid", competicionid);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
     
    /**Devuelve la lista de registros de pruebas de tipo equipo
     * de una competición.
     * 
     * @param competicionid     Identificador de la competición.
     * @param mejoresMarcas     Valor booleano que indica si obtener solo las 
     * mejores marcas de cada equipo o todas las marcas
     * @return List<Registro>
     */
     public List<Registro> findByCompeticionEquipo(Integer competicionid, boolean mejoresMarcas) {
         EntityManager em = getEntityManager();
        List<Registro> res;
        try {
            Query q;
            if(mejoresMarcas){
                q = em.createNamedQuery("Registro.findByCompeticionEquipoMMTiempo");
            }else{
                q = em.createNamedQuery("Registro.findByCompeticionEquipo");
            }
            q.setParameter("competicionid", competicionid);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
     
     public List<Registro> temp(Integer competicionid, boolean mejoresMarcas) {
         EntityManager em = getEntityManager();
        List<Registro> res;
        try {
            Query q;
            q = em.createNamedQuery("Registro.findByCompeticionEquipoMMTiempo");
            q.setParameter("competicionid", competicionid);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
     /**Devuelve el último número de intento de un participante en una 
      * prueba determinada de una competición.
      * 
      * @param inscripcionid    Identificador de la inscripción.
      * @param pruebaid         Identificador del grupo.
      * @param participanteid   Identificador del participante
      * @return int
      */
    public int findMaxNumIntentoParticipante(Integer inscripcionid, Integer pruebaid, Integer participanteid){
        EntityManager em = getEntityManager();
        int res;
        Query q = em.createNamedQuery("Registro.findMinNumIntentoByInscripcionPruebaParticipante");
        q.setParameter("inscripcionid", inscripcionid);
        q.setParameter("pruebaid", pruebaid);
        q.setParameter("participanteid", participanteid);
        res = (Integer)q.getSingleResult();
        em.close();
        return res;
    }
    
    /**Devuelve el último número de intento de un equipo en una 
      * prueba determinada de una competición.
      * 
      * @param inscripcionid    Identificador de la inscripción.
      * @param pruebaid         Identificador del grupo.
      * @param equipoid   Identificador del equipo
      * @return int
      */
    public int findMaxNumIntentoEquipo(Integer inscripcionid, Integer pruebaid, Integer equipoid){
        EntityManager em = getEntityManager();
        int res;
        Query q = em.createNamedQuery("Registro.findMinNumIntentoByInscripcionPruebaEquipo");
        q.setParameter("inscripcionid", inscripcionid);
        q.setParameter("pruebaid", pruebaid);
        q.setParameter("equipoid", equipoid);
        res = (Integer)q.getSingleResult();
        em.close();
        return res;
    }
    
    /**Devuelve la lista de registros de un participante (de todas las pruebas)
     * 
     * @param participanteid    Identificador del participante
     * @return List<Registro>
     */
    public List<Registro> findByParticipante(Integer participanteid) {
        EntityManager em = getEntityManager();
        List<Registro> res;
        try {
            Query q = em.createNamedQuery("Registro.findByParticipante");
            q.setParameter("participanteid", participanteid);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    /**Devuelve la lista de registros de un equipo (de todas las pruebas)
     * 
     * @param equipoid    Identificador del equipo
     * @return List<Registro>
     */
    public List<Registro> findByEquipo(Integer equipoid) {
        EntityManager em = getEntityManager();
        List<Registro> res;
        try {
            Query q = em.createNamedQuery("Registro.findByEquipo");
            q.setParameter("equipoid", equipoid);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    /**Devuelve la lista de registros de equipos filtrada por competición,
     * grupo y prueba.
     * 
     * @param competicionid     Identificador de la competición
     * @param grupoid           Identificador del grupo
     * @param pruebaid          Identificador de la prueba
     * @return List<Registro>
     */
    public List<Registro> findByCompeticionGrupoPruebaEquipo(Integer competicionid, Integer grupoid, Integer pruebaid) {
        EntityManager em = getEntityManager();
        List<Registro> res;
        try {
            Query q = em.createNamedQuery("Registro.findByCompeticionGrupoPruebaEquipo");
            q.setParameter("grupoid", grupoid);
            q.setParameter("competicionid", competicionid);
            q.setParameter("pruebaid", pruebaid);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
     /**Devuelve la lista de registros de participantes individuales filtrada 
      * por competición, grupo y prueba.
     * 
     * @param competicionid     Identificador de la competición
     * @param grupoid           Identificador del grupo
     * @param pruebaid          Identificador de la prueba
     * @return List<Registro>
     */
    public List<Registro> findByCompeticionGrupoPruebaIndividual(Integer competicionid, Integer grupoid, Integer pruebaid) {
        EntityManager em = getEntityManager();
        List<Registro> res;
        try {
            Query q = em.createNamedQuery("Registro.findByCompeticionGrupoPruebaIndividual");
            q.setParameter("grupoid", grupoid);
            q.setParameter("competicionid", competicionid);
            q.setParameter("pruebaid", pruebaid);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    /**Devuelve la lista de registros de equipos y participantes filtrada por competición,
     * grupo y prueba.
     * 
     * @param competicionid     Identificador de la competición
     * @param grupoid           Identificador del grupo
     * @param pruebaid          Identificador de la prueba
     * @return List<Registro>
     */
    public List<Registro> findByCompeticionGrupoPrueba(Integer competicionid, Integer grupoid, Integer pruebaid) {
        EntityManager em = getEntityManager();
        List<Registro> res;
        try {
            Query q = em.createNamedQuery("Registro.findByCompeticionGrupoPrueba");
            q.setParameter("grupoid", grupoid);
            q.setParameter("competicionid", competicionid);
            q.setParameter("pruebaid", pruebaid);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    /**Devuelve la lista de registros de equipos y participantes filtrada por competición y
     * grupo
     * 
     * @param competicionid     Identificador de la competición
     * @param grupoid           Identificador del grupo
     * @return List<Registro>
     */
    public List<Registro> findByCompeticionGrupo(Integer competicionid, Integer grupoid) {
        EntityManager em = getEntityManager();
        List<Registro> res;
        try {
            Query q = em.createNamedQuery("Registro.findByCompeticionGrupo");
            q.setParameter("grupoid", grupoid);
            q.setParameter("competicionid", competicionid);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    /**Devuelve la lista de registros de participantes filtrada por competición y
     * grupo
     * 
     * @param competicionid     Identificador de la competición
     * @param grupoid           Identificador del grupo
     * @return List<Registro>
     */
    public List<Registro> findByCompeticionGrupoIndividual(Integer competicionid, Integer grupoid) {
        EntityManager em = getEntityManager();
        List<Registro> res;
        try {
            Query q = em.createNamedQuery("Registro.findByCompeticionGrupoIndividual");
            q.setParameter("grupoid", grupoid);
            q.setParameter("competicionid", competicionid);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    /**Devuelve la lista de registros de equipos filtrada por competición y
     * grupo
     * 
     * @param competicionid     Identificador de la competición
     * @param grupoid           Identificador del grupo
     * @return List<Registro>
     */
    public List<Registro> findByCompeticionGrupoEquipo(Integer competicionid, Integer grupoid) {
        EntityManager em = getEntityManager();
        List<Registro> res;
        try {
            Query q = em.createNamedQuery("Registro.findByCompeticionGrupoEquipo");
            q.setParameter("grupoid", grupoid);
            q.setParameter("competicionid", competicionid);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    /**Devuelve la lista de registros de equipos y participantes filtrada por competición y
     * prueba
     * 
     * @param competicionid     Identificador de la competición
     * @param pruebaid           Identificador de la prueba
     * @return List<Registro>
     */
    public List<Registro> findByCompeticionPrueba(Integer competicionid, Integer pruebaid) {
        EntityManager em = getEntityManager();
        List<Registro> res;
        try {
            Query q = em.createNamedQuery("Registro.findByCompeticionPrueba");
            q.setParameter("competicionid", competicionid);
            q.setParameter("pruebaid", pruebaid);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    /**Devuelve la lista de registros de participantes filtrada por competición y
     * prueba
     * 
     * @param competicionid     Identificador de la competición
     * @param pruebaid           Identificador de la prueba
     * @return List<Registro>
     */
    public List<Registro> findByCompeticionPruebaIndividual(Integer competicionid, Integer pruebaid) {
        EntityManager em = getEntityManager();
        List<Registro> res;
        try {
            Query q = em.createNamedQuery("Registro.findByCompeticionPruebaIndividual");
            q.setParameter("competicionid", competicionid);
            q.setParameter("pruebaid", pruebaid);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    /**Devuelve la lista de registros de equipos filtrada por competición y
     * prueba
     * 
     * @param competicionid     Identificador de la competición
     * @param pruebaid           Identificador de la prueba
     * @return List<Registro>
     */
    public List<Registro> findByCompeticionPruebaEquipo(Integer competicionid, Integer pruebaid) {
        EntityManager em = getEntityManager();
        List<Registro> res;
        try {
            Query q = em.createNamedQuery("Registro.findByCompeticionPruebaEquipo");
            q.setParameter("competicionid", competicionid);
            q.setParameter("pruebaid", pruebaid);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    /**Devuelve la lista de registros de un participante filtrada por competición y
     * prueba ordenada por número de intento
     * 
     * @param competicionid     Identificador de la competición
     * @param pruebaid          Identificador de la prueba
     * @param participanteid    Identificador del participante
     * @return List<Registro>
     */
    public List<Registro> findRegistroByParticipantePruebaCompeticionOrderByNumIntento(Integer competicionid, Integer pruebaid, Integer participanteid) {
        EntityManager em = getEntityManager();
        List<Registro> res;
        try {
            Query q = em.createNamedQuery("Registro.findRegistroByParticipantePruebaCompeticionOrderByNumIntento");
            q.setParameter("competicionid", competicionid);
            q.setParameter("pruebaid", pruebaid);
            q.setParameter("participanteid", participanteid);
            q.setMaxResults(2);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    /**Devuelve la lista de registros de un equipo filtrada por competición y
     * prueba ordenada por número de intento
     * 
     * @param competicionid     Identificador de la competición
     * @param pruebaid          Identificador de la prueba
     * @param equipoid    Identificador del participante
     * @return List<Registro>
     */
    public List<Registro> findRegistroByEquipoPruebaCompeticionOrderByNumIntento(Integer competicionid, Integer pruebaid, Integer equipoid) {
        EntityManager em = getEntityManager();
        List<Registro> res;
        try {
            Query q = em.createNamedQuery("Registro.findRegistroByEquipoPruebaCompeticionOrderByNumIntento");
            q.setParameter("competicionid", competicionid);
            q.setParameter("pruebaid", pruebaid);
            q.setParameter("equipoid", equipoid);
            q.setMaxResults(2);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    /**Devuelve la lista de registros de un equipo filtrada por competición y
     * prueba ordenada por marca de mayor a menor
     * 
     * @param competicionid     Identificador de la competición
     * @param pruebaid          Identificador de la prueba
     * @param equipoid    Identificador del equipo
     * @return List<Registro>
     */
    public List<Registro> findRegistroByEquipoPruebaCompeticionOrderByNum(Integer competicionid, Integer pruebaid, Integer equipoid) {
        EntityManager em = getEntityManager();
        List<Registro> res;
        try {
            Query q = em.createNamedQuery("Registro.findRegistroByEquipoPruebaCompeticionOrderByNum");
            q.setParameter("competicionid", competicionid);
            q.setParameter("pruebaid", pruebaid);
            q.setParameter("equipoid", equipoid);
            q.setMaxResults(2);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    /**Devuelve la lista de registros de un participante filtrada por competición y
     * prueba ordenada por marca de mayor a menor
     * 
     * @param competicionid     Identificador de la competición
     * @param pruebaid          Identificador de la prueba
     * @param participanteid    Identificador del participante
     * @return List<Registro>
     */
    public List<Registro> findRegistroByParticipantePruebaCompeticionOrderByNum(Integer competicionid, Integer pruebaid, Integer participanteid) {
        EntityManager em = getEntityManager();
        List<Registro> res;
        try {
            Query q = em.createNamedQuery("Registro.findRegistroByParticipantePruebaCompeticionOrderByNum");
            q.setParameter("competicionid", competicionid);
            q.setParameter("pruebaid", pruebaid);
            q.setParameter("participanteid", participanteid);
            q.setMaxResults(2);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    /**Devuelve la lista de registros de un equipo filtrada por competición y
     * prueba ordenada por tiempo
     * 
     * @param competicionid     Identificador de la competición
     * @param pruebaid          Identificador de la prueba
     * @param equipoid    Identificador del equipo
     * @return List<Registro>
     */
    public List<Registro> findRegistroByEquipoPruebaCompeticionOrderByTiempo(Integer competicionid, Integer pruebaid, Integer equipoid) {
        EntityManager em = getEntityManager();
        List<Registro> res;
        try {
            Query q = em.createNamedQuery("Registro.findRegistroByEquipoPruebaCompeticionOrderByTiempo");
            q.setParameter("competicionid", competicionid);
            q.setParameter("pruebaid", pruebaid);
            q.setParameter("equipoid", equipoid);
            q.setMaxResults(2);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    /**Devuelve la lista de registros de un participante filtrada por competición y
     * prueba ordenada por tiempo
     * 
     * @param competicionid     Identificador de la competición
     * @param pruebaid          Identificador de la prueba
     * @param participanteid    Identificador del participante
     * @return List<Registro>
     */
    public List<Registro> findRegistroByParticipantePruebaCompeticionOrderByTiempo(Integer competicionid, Integer pruebaid, Integer participanteid) {
        EntityManager em = getEntityManager();
        List<Registro> res;
        try {
            Query q = em.createNamedQuery("Registro.findRegistroByParticipantePruebaCompeticionOrderByTiempo");
            q.setParameter("competicionid", competicionid);
            q.setParameter("pruebaid", pruebaid);
            q.setParameter("participanteid", participanteid);
            q.setMaxResults(2);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    /**Devuelve la lista completa de registros de una prueba
     * 
     * @param pruebaid  Identificador de la prueba
     * @return List<Registro>
     */
    public List<Registro> findByPrueba(Integer pruebaid) {
        EntityManager em = getEntityManager();
        List<Registro> res;
        try {
            Query q = em.createNamedQuery("Registro.findByPrueba");
            q.setParameter("pruebaid", pruebaid);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
      
    /**Devuelve una lista de participantes que tienen registros en una prueba determinada
     * de una competición ordenada por marca (de mayor a menor).
     * 
     * @param competicionid     Identificador de la competición.
     * @param pruebaid          Identificador de la prueba.
     * @return List<Participante>
     */
    public List<Participante> findParticipantesConRegistrosNum(Integer competicionid, Integer pruebaid) {
        EntityManager em = getEntityManager();
        List<Participante> res;
        try {
            Query q = em.createNamedQuery("Registro.findParticipantesConRegistrosNum");
            q.setParameter("competicionid", competicionid);
            q.setParameter("pruebaid", pruebaid);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    /**Devuelve una lista de participantes que pertenecen a alguno de los grupos pasados y que tienen registros en una prueba determinada
     * de una competición ordenada por marca (de mayor a menor).
     * 
     * @param competicionid     Identificador de la competición.
     * @param pruebaid          Identificador de la prueba.
     * @param nombreGrupos      Lista con los nombre de los grupos
     * @return List<Participante>
     */
    public List<Participante> findParticipantesConRegistrosNumByGrupo(Integer competicionid, Integer pruebaid, List<String> nombreGrupos) {
        EntityManager em = getEntityManager();
        List<Participante> res;
        try {
            Query q = em.createNamedQuery("Registro.findParticipantesConRegistrosNumByGrupo");
            q.setParameter("competicionid", competicionid);
            q.setParameter("pruebaid", pruebaid);
            q.setParameter("grupos", nombreGrupos);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    /**Devuelve una lista de equipos que tienen registros en una prueba determinada
     * de una competición ordenada por marca (de mayor a menor).
     * 
     * @param competicionid     Identificador de la competición.
     * @param pruebaid          Identificador de la prueba.
     * @return List<Participante>
     */
    public List<Equipo> findEquiposConRegistrosNum(Integer competicionid, Integer pruebaid) {
        EntityManager em = getEntityManager();
        List<Equipo> res;
        try {
            Query q = em.createNamedQuery("Registro.findEquiposConRegistrosNum");
            q.setParameter("competicionid", competicionid);
            q.setParameter("pruebaid", pruebaid);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    /**Devuelve una lista de equipos que pertenecen a alguno de los grupos y que tienen registros en una prueba determinada
     * de una competición ordenada por marca (de mayor a menor).
     * 
     * @param competicionid     Identificador de la competición.
     * @param pruebaid          Identificador de la prueba.
     * @param nombreGrupos      Lista con los nombres de los grupos
     * @return List<Participante>
     */
    public List<Equipo> findEquiposConRegistrosNumByGrupo(Integer competicionid, Integer pruebaid,List<String> nombreGrupos) {
        EntityManager em = getEntityManager();
        List<Equipo> res;
        try {
            Query q = em.createNamedQuery("Registro.findEquiposConRegistrosNumByGrupo");
            q.setParameter("competicionid", competicionid);
            q.setParameter("pruebaid", pruebaid);
            q.setParameter("grupos", nombreGrupos);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    /**Devuelve una lista de participantes que tienen registros en una prueba determinada
     * de una competición ordenada por tiempo (de menor a mayor).
     * 
     * @param competicionid     Identificador de la competición.
     * @param pruebaid          Identificador de la prueba.
     * @return List<Participante>
     */
    public List<Participante> findParticipantesConRegistrosTiempo(Integer competicionid, Integer pruebaid) {
        EntityManager em = getEntityManager();
        List<Participante> res;
        try {
            Query q = em.createNamedQuery("Registro.findParticipantesConRegistrosTiempo");
            q.setParameter("competicionid", competicionid);
            q.setParameter("pruebaid", pruebaid);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    /**Devuelve una lista de participantes que pertenecen a alguno de los grupos que tienen registros en una prueba determinada
     * de una competición ordenada por tiempo (de menor a mayor).
     * 
     * @param competicionid     Identificador de la competición.
     * @param pruebaid          Identificador de la prueba.
     * @param nombreGrupos      Lista de nombres de los grupos
     * @return List<Participante>
     */
    public List<Participante> findParticipantesConRegistrosTiempoByGrupos(Integer competicionid, Integer pruebaid, List<String> nombreGrupos) {
        EntityManager em = getEntityManager();
        List<Participante> res;
        try {
            Query q = em.createNamedQuery("Registro.findParticipantesConRegistrosTiempoByGrupo");
            q.setParameter("competicionid", competicionid);
            q.setParameter("pruebaid", pruebaid);
            q.setParameter("grupos", nombreGrupos);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
     /**Devuelve una lista de equipos que tienen registros en una prueba determinada
     * de una competición ordenada por tiempo (de menor a mayor).
     * 
     * @param competicionid     Identificador de la competición.
     * @param pruebaid          Identificador de la prueba.
     * @return List<Participante>
     */
    public List<Equipo> findEquiposConRegistrosTiempo(Integer competicionid, Integer pruebaid) {
        EntityManager em = getEntityManager();
        List<Equipo> res;
        try {
            Query q = em.createNamedQuery("Registro.findEquiposConRegistrosTiempo");
            q.setParameter("competicionid", competicionid);
            q.setParameter("pruebaid", pruebaid);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    /**Devuelve una lista de equipos que pertenecen a alguno de los grupos y que tienen registros en una prueba determinada
     * de una competición ordenada por tiempo (de menor a mayor).
     * 
     * @param competicionid     Identificador de la competición.
     * @param pruebaid          Identificador de la prueba.
     * @param nombreGrupos      Lista con los nombres de los grupos
     * @return List<Participante>
     */
    public List<Equipo> findEquiposConRegistrosTiempoByGrupo(Integer competicionid, Integer pruebaid,List<String> nombreGrupos) {
        EntityManager em = getEntityManager();
        List<Equipo> res;
        try {
            Query q = em.createNamedQuery("Registro.findEquiposConRegistrosTiempoByGrupo");
            q.setParameter("competicionid", competicionid);
            q.setParameter("pruebaid", pruebaid);
            q.setParameter("grupos",nombreGrupos);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    /**Devuelve la mejor marca de un participante en una prueba determinada de 
     * una competición.
     * 
     * @param participanteid    Identificador del participante
     * @param competicionid     Identificador de la competición
     * @param pruebaid          Identificador de la prueba
     * @return Double
     */
    public Double findMaxRegistroByParticipantePruebaCompeticion(Integer participanteid,Integer competicionid, Integer pruebaid) {
        EntityManager em = getEntityManager();
        Double res;
        try {
            Query q = em.createNamedQuery("Registro.findMaxRegistroByParticipantePruebaCompeticion");
            q.setParameter("competicionid", competicionid);
            q.setParameter("pruebaid", pruebaid);
            q.setParameter("participanteid", participanteid);
            res = (Double)q.getSingleResult();
            
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    /**Devuelve la mejor marca de un equipo en una prueba determinada de 
     * una competición.
     * 
     * @param equipoid          Identificador del equipo
     * @param competicionid     Identificador de la competición
     * @param pruebaid          Identificador de la prueba
     * @return Double
     */
    public Double findMaxRegistroByEquipoPruebaCompeticion(Integer equipoid,Integer competicionid, Integer pruebaid) {
        EntityManager em = getEntityManager();
        Double res;
        try {
            Query q = em.createNamedQuery("Registro.findMaxRegistroByEquipoPruebaCompeticion");
            q.setParameter("competicionid", competicionid);
            q.setParameter("pruebaid", pruebaid);
            q.setParameter("equipoid", equipoid);
            res = (Double)q.getSingleResult();
            
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    /**Devuelve el mejor tiempo de un participante en una prueba determinada de 
     * una competición.
     * 
     * @param participanteid    Identificador del participante
     * @param competicionid     Identificador de la competición
     * @param pruebaid          Identificador de la prueba
     * @return Double
     */
    public Date findMinRegistroByParticipantePruebaCompeticion(Integer participanteid,Integer competicionid, Integer pruebaid) {
        EntityManager em = getEntityManager();
        Date res;
        try {
            Query q = em.createNamedQuery("Registro.findMinRegistroByParticipantePruebaCompeticion");
            q.setParameter("competicionid", competicionid);
            q.setParameter("pruebaid", pruebaid);
            q.setParameter("participanteid", participanteid);
            res = (Date)q.getSingleResult();
            
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    /**Devuelve el mejor tiempo de un equipo en una prueba determinada de 
     * una competición.
     * 
     * @param equipoid          Identificador del equipo
     * @param competicionid     Identificador de la competición
     * @param pruebaid          Identificador de la prueba
     * @return Double
     */
    public Date findMinRegistroByEquipoPruebaCompeticion(Integer equipoid,Integer competicionid, Integer pruebaid) {
        EntityManager em = getEntityManager();
        Date res;
        try {
            Query q = em.createNamedQuery("Registro.findMinRegistroByEquipoPruebaCompeticion");
            q.setParameter("competicionid", competicionid);
            q.setParameter("pruebaid", pruebaid);
            q.setParameter("equipoid", equipoid);
            res = (Date)q.getSingleResult();
            
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    /**
     * Crea un registro nuevo
     *
     * @param competicion
     * @param dorsal dorsal del participante, si es un equipo null
     * @param nombrePrueba nombre de la prueba
     * @param nombreEquipo Nombre del equipo si el registro es de un equipo,
     * null en otro caso
     * @param sorteo Indica si la prueba ha sido seleccionada por sorteo
     * @param segundos Marca en segundos o la distancia o número si la prueba no
     * es de tiempo
     * @param minutos Marca en minutos, si la prueba no es de tiempo null
     * @param horas Marca en horas, si la prueba no es de tiempo null
     * @return Registro si ha sido creado correctamente
     * @throws controlador.InputException
     */
    public static Registro crearRegistro(Competicion competicion, Integer dorsal,
            String nombrePrueba, String nombreEquipo, Boolean sorteo,
            Double segundos, Integer minutos, Integer horas) throws InputException {

        if (competicion != null) {
            if (nombrePrueba != null) {
                Registro registro = null;
                PruebaJpa pruebajpa = new PruebaJpa();
                // Obtenemos la prueba a partir del nombre
                Prueba prueba = pruebajpa.findPruebaByNombreCompeticion(nombrePrueba, competicion.getId());
                // Comprobamos que los parámetros son correctos
                if (prueba != null) {
                    if (!(dorsal == null && prueba.getTipo().equals(TipoPrueba.Individual.toString()))) {
                        if (!(nombreEquipo == null && prueba.getTipo().equals(TipoPrueba.Equipo.toString()))) {

                            Participante participante;
                            Grupo g;
                            registro = new Registro();
                            Inscripcion i;
                            GrupoJpa grupojpa = new GrupoJpa();
                            InscripcionJpa inscripcionjpa = new InscripcionJpa();
                            RegistroJpa registrojpa = new RegistroJpa();
                            // Si se ha seleccionado un participante individual
                            if (prueba.getTipo().equals(TipoPrueba.Individual.toString())) {
                                // Obtenemos el participante
                                ParticipanteJpa participantejpa = new ParticipanteJpa();
                                participante = participantejpa.findByDorsalAndCompeticion(dorsal, competicion.getId());
                                g = grupojpa.findByParticipanteCompeticion(competicion.getId(), participante.getId());
                                registro.setParticipanteId(participante);
                                i = inscripcionjpa.findInscripcionByCompeticionByGrupo(
                                        competicion.getId(), g.getId());
                                registro.setNumIntento(registrojpa.findMaxNumIntentoParticipante(i.getId(),
                                        prueba.getId(), participante.getId()) + 1);
                            } else {
                                // Obtenemos el equipo
                                EquipoJpa equipojpa = new EquipoJpa();
                                Equipo equipo = equipojpa.findByNombreAndCompeticion(nombreEquipo,
                                        competicion.getId());
                                g = grupojpa.findByEquipoCompeticion(competicion.getId(), equipo.getId());
                                registro.setEquipoId(equipo);
                                i = inscripcionjpa.findInscripcionByCompeticionByGrupo(competicion.getId(), g.getId());
                                registro.setNumIntento(registrojpa.findMaxNumIntentoEquipo(i.getId(),
                                        prueba.getId(), equipo.getId()) + 1);
                            }

                            // Establecemos los datos del registro comunes
                            registro.setInscripcionId(i);
                            registro.setPruebaId(prueba);
                            if (sorteo != null) {
                                registro.setSorteo(sorteo ? 1 : 0);
                            }
                            // Comprueba que la prueba no es de tipo tiempo
                            if (prueba.getTiporesultado().equals(TipoResultado.Distancia.toString())
                                    || prueba.getTiporesultado().equals(TipoResultado.Numerica.toString())) {
                                if (segundos == null) {
                                    throw new InputException("Formato del registro no válido");
                                }
                                registro.setNum(segundos);
                                // Si es de tipo Tiempo formateamos la hora, minutos y segundos    
                            } else if (prueba.getTiporesultado().equals(TipoResultado.Tiempo.toString())) {
                                // Obtenemos un objeto Date a partir de los parámetros
                                Date date = getTiempo(segundos, minutos, horas);
                                if (date == null) {
                                    throw new InputException("Formato de tiempo no válido");
                                }
                                registro.setTiempo(date);
                            }
                            // Creamos el registro en la base de datos
                            registrojpa.create(registro);
                        } else {
                            throw new InputException("Equipo no válido");
                        }
                    } else {
                        throw new InputException("Participante no válido");
                    }
                } else {
                    throw new InputException("Prueba no encontrada");
                }
                return registro;
            } else {
                throw new InputException("Nombre de prueba no válido");
            }
        } else {
            throw new InputException("Competición no válida");
        }
    }
    
    public static Registro crearRegistroIndividualNum(Competicion competicion, Integer dorsal, String nombrePrueba, Boolean sorteo, Double marca) throws InputException {
        return RegistroJpa.crearRegistro(competicion, dorsal, nombrePrueba, null, sorteo, marca, null, null);
    }

    public static Registro crearRegistroIndividualTiempo(Competicion competicion, Integer dorsal, String nombrePrueba, Boolean sorteo, Double segundos, Integer minutos, Integer horas) throws InputException {
        return RegistroJpa.crearRegistro(competicion, dorsal, nombrePrueba, null, sorteo, segundos, minutos, horas);
    }

    public static Registro crearRegistroEquipoNum(Competicion competicion, String nombreEquipo, String nombrePrueba, Boolean sorteo, Double marca) throws InputException {
        return RegistroJpa.crearRegistro(competicion, null, nombrePrueba, nombreEquipo, sorteo, marca, null, null);
    }

    public static Registro crearRegistroEquipoTiempo(Competicion competicion, String nombreEquipo, String nombrePrueba, Boolean sorteo, Double segundos, Integer minutos, Integer horas) throws InputException {
        return RegistroJpa.crearRegistro(competicion, null, nombrePrueba, nombreEquipo, sorteo, segundos, minutos, horas);
    }
    
    public static Integer getHoras(String tiempo) {
        try {
            String aux1, aux2;
            aux1 = tiempo.substring(0, tiempo.indexOf(":"));
            aux2 = tiempo.substring(tiempo.indexOf(":") + 1);
            if (aux2.contains(":")) {
                Integer res = Integer.parseInt(aux1);
                if (res >= 0) {
                    return res;
                } else {
                    return null;
                }
            } else {
                return 0;
            }
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            return null;
        }
    }

    public static Integer getMinutos(String tiempo) {
        try {
            if (tiempo.contains(":")) {
                String aux1, aux2;
                aux1 = tiempo.substring(0, tiempo.indexOf(":"));
                aux2 = tiempo.substring(tiempo.indexOf(":") + 1);
                Integer res = null;
                if (aux2.contains(":")) {
                    aux1 = aux2.substring(0, aux2.indexOf(":"));
                    res = Integer.parseInt(aux1);
                } else {
                    res = Integer.parseInt(aux1);

                }
                if (res >= 0 && res < 60) {
                    return res;
                } else {
                    return null;
                }
            } else if (Double.parseDouble(tiempo) > 0) {
                return 0;
            } else {
                return null;
            }
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            return null;
        }
    }

    public static Double getSegundos(String tiempo) {
        try {
            if (tiempo.contains(":")) {
                String aux1, aux2;
                aux2 = tiempo.substring(tiempo.indexOf(":") + 1);
                Double res = null;
                if (aux2.contains(":")) {
                    aux1 = aux2.substring(aux2.indexOf(":") + 1);
                    res = Double.parseDouble(aux1);
                } else {
                    res = Double.parseDouble(aux2);
                }
                if (res >= 0 && res < 60) {
                    return res;
                } else {
                    return null;
                }
            } else if (Double.parseDouble(tiempo) > 0) {
                return Double.parseDouble(tiempo);
            } else {
                return null;
            }
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            return null;
        }
    }

    public static Date getTiempo(Double segundos, Integer minutos, Integer horas) {
        Date res = null;
        if (segundos == null && minutos == null & horas == null) {
            return res;
        } else {
            try {
                String formatDate = "HH:mm:ss.S";
                String horasSt;
                if (horas == null) {
                    horasSt = "00";
                } else {
                    horasSt = Integer.toString(horas);
                    if (horasSt.length() == 0) {
                        horasSt = "00";
                    } else if (horasSt.length() == 1) {
                        horasSt = "0" + horasSt;
                    }
                }
                String minutosSt;
                if (minutos == null) {
                    minutosSt = "00";
                } else {
                    minutosSt = Integer.toString(minutos);
                    if (minutosSt.length() == 0) {
                        minutosSt = "00";
                    } else if (minutosSt.length() == 1) {
                        minutosSt = "0" + minutosSt;
                    }
                }
                String segundosSt;
                if (segundos == null) {
                    segundosSt = "00.0";
                } else {
                    segundosSt = Double.toString(segundos);
                    if (segundosSt.length() == 0) {
                        segundosSt = "00.0";
                    } else {
                        segundosSt = Double.toString(Double.parseDouble(segundosSt));
                        if (Double.parseDouble(segundosSt) > 59.999) {
                            return null;
                        }
                        if (segundosSt.charAt(2) == '.') {
                            String decimales = segundosSt.substring(2);
                            for (int j = 2; j < decimales.length(); j++) {
                                formatDate += "S";
                            }
                        }
                    }
                }
                res = new SimpleDateFormat(formatDate).parse(horasSt + ":" + minutosSt + ":" + segundosSt);
            } catch (ParseException | NumberFormatException ex) {
                return res;
            }
            return res;
        }
    }

    /**
     * Elimina un registro cuyo id es "registroid"
     *
     * @param registroid Id del registro a eliminar
     * @throws controlador.InputException
     */
    public static void eliminarRegistro(Integer registroid) throws InputException {

        if (registroid != null) {
            RegistroJpa registrojpa = new RegistroJpa();
            try {
                registrojpa.destroy(registroid);
            } catch (NonexistentEntityException ex) {
                throw new InputException("Registro no encontrado");
            }
        } else {
            throw new InputException("Registro no válido");
        }
    }

    /**
     * Modifica la marca del registro cuyo id es "registroid"
     *
     * @param registroid Id del registro que se va a modificar
     * @param segundos
     * @param minutos
     * @param horas
     *
     * @return el Registro modificado
     * @throws controlador.InputException
     */
    public static Registro modificarRegistro(Integer registroid, Double segundos, Integer minutos, Integer horas) throws InputException {

        if (registroid != null) {

            RegistroJpa registrojpa = new RegistroJpa();
            // Obtenemos el objeto Registro a partir de su Id
            Registro registro = registrojpa.findRegistro(registroid);

            if (registro != null) {
                // Si el registro es de Tipo Numerica o Distancia
                if (registro.getPruebaId().getTiporesultado().equals(TipoResultado.Distancia.toString())
                        || registro.getPruebaId().getTiporesultado().equals(TipoResultado.Numerica.toString())) {
                    if (segundos == null) {
                        throw new InputException("Formato del registro no válido");
                    }
                    // Establecemos el nuevo registro
                    registro.setNum(segundos);

                } else if (registro.getPruebaId().getTiporesultado().equals(TipoResultado.Tiempo.toString())) { // Si el registro es de Tipo Tiempo
                    Date date = getTiempo(segundos, minutos, horas);
                    if (date != null) {
                        // Establecemos el tiempo formateado
                        registro.setTiempo(date);
                    } else {
                        throw new InputException("Formato de tiempo no válido");
                    }
                }
                try {
                    // Guardamos los cambios en la base de datos
                    registrojpa.edit(registro);
                } catch (Exception ex) {
                    throw new InputException(ex.getMessage());
                }
                return registro;
            }
            throw new InputException("Registro no encontrado");

        } else {
            throw new InputException("Registro no válido");
        }
    }
}
