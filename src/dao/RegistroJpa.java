package dao;

import java.io.Serializable;
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
import dao.exceptions.NonexistentEntityException;

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
     * @return List<Registro>
     */
     public List<Registro> findByCompeticionEquipo(Integer competicionid) {
         EntityManager em = getEntityManager();
        List<Registro> res;
        try {
            Query q = em.createNamedQuery("Registro.findByCompeticionEquipo");
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
    public int findMaxNumIntento(Integer inscripcionid, Integer pruebaid, Integer participanteid){
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
}
