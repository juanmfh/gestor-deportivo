package modelo.dao;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import modelo.Prueba;
import modelo.Persona;
import modelo.Participante;
import modelo.Inscripcion;
import modelo.Registro;
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
            Persona personaId = registro.getPersonaId();
            if (personaId != null) {
                personaId = em.getReference(personaId.getClass(), personaId.getId());
                registro.setPersonaId(personaId);
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
            em.persist(registro);
            if (pruebaId != null) {
                pruebaId.getRegistroCollection().add(registro);
                pruebaId = em.merge(pruebaId);
            }
            if (personaId != null) {
                personaId.getRegistroCollection().add(registro);
                personaId = em.merge(personaId);
            }
            if (participanteId != null) {
                participanteId.getRegistroCollection().add(registro);
                participanteId = em.merge(participanteId);
            }
            if (inscripcionId != null) {
                inscripcionId.getRegistroCollection().add(registro);
                inscripcionId = em.merge(inscripcionId);
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
            Persona personaIdOld = persistentRegistro.getPersonaId();
            Persona personaIdNew = registro.getPersonaId();
            Participante participanteIdOld = persistentRegistro.getParticipanteId();
            Participante participanteIdNew = registro.getParticipanteId();
            Inscripcion inscripcionIdOld = persistentRegistro.getInscripcionId();
            Inscripcion inscripcionIdNew = registro.getInscripcionId();
            if (pruebaIdNew != null) {
                pruebaIdNew = em.getReference(pruebaIdNew.getClass(), pruebaIdNew.getId());
                registro.setPruebaId(pruebaIdNew);
            }
            if (personaIdNew != null) {
                personaIdNew = em.getReference(personaIdNew.getClass(), personaIdNew.getId());
                registro.setPersonaId(personaIdNew);
            }
            if (participanteIdNew != null) {
                participanteIdNew = em.getReference(participanteIdNew.getClass(), participanteIdNew.getId());
                registro.setParticipanteId(participanteIdNew);
            }
            if (inscripcionIdNew != null) {
                inscripcionIdNew = em.getReference(inscripcionIdNew.getClass(), inscripcionIdNew.getId());
                registro.setInscripcionId(inscripcionIdNew);
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
            if (personaIdOld != null && !personaIdOld.equals(personaIdNew)) {
                personaIdOld.getRegistroCollection().remove(registro);
                personaIdOld = em.merge(personaIdOld);
            }
            if (personaIdNew != null && !personaIdNew.equals(personaIdOld)) {
                personaIdNew.getRegistroCollection().add(registro);
                personaIdNew = em.merge(personaIdNew);
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
            Persona personaId = registro.getPersonaId();
            if (personaId != null) {
                personaId.getRegistroCollection().remove(registro);
                personaId = em.merge(personaId);
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
    
}
