package modelo.dao;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import modelo.Participante;
import modelo.Grupo;
import modelo.Participa;
import modelo.dao.exceptions.NonexistentEntityException;

/**
 *
 * @author JuanM
 */
public class ParticipaJpa implements Serializable {

    public ParticipaJpa() {
        this.emf = Persistence.createEntityManagerFactory("JavaAppDesktopPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Participa participa) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Participante participanteId = participa.getParticipanteId();
            if (participanteId != null) {
                participanteId = em.getReference(participanteId.getClass(), participanteId.getId());
                participa.setParticipanteId(participanteId);
            }
            Grupo grupoId = participa.getGrupoId();
            if (grupoId != null) {
                grupoId = em.getReference(grupoId.getClass(), grupoId.getId());
                participa.setGrupoId(grupoId);
            }
            em.persist(participa);
            if (participanteId != null) {
                participanteId.getParticipaCollection().add(participa);
                participanteId = em.merge(participanteId);
            }
            if (grupoId != null) {
                grupoId.getParticipaCollection().add(participa);
                grupoId = em.merge(grupoId);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Participa participa) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Participa persistentParticipa = em.find(Participa.class, participa.getId());
            Participante participanteIdOld = persistentParticipa.getParticipanteId();
            Participante participanteIdNew = participa.getParticipanteId();
            Grupo grupoIdOld = persistentParticipa.getGrupoId();
            Grupo grupoIdNew = participa.getGrupoId();
            if (participanteIdNew != null) {
                participanteIdNew = em.getReference(participanteIdNew.getClass(), participanteIdNew.getId());
                participa.setParticipanteId(participanteIdNew);
            }
            if (grupoIdNew != null) {
                grupoIdNew = em.getReference(grupoIdNew.getClass(), grupoIdNew.getId());
                participa.setGrupoId(grupoIdNew);
            }
            participa = em.merge(participa);
            if (participanteIdOld != null && !participanteIdOld.equals(participanteIdNew)) {
                participanteIdOld.getParticipaCollection().remove(participa);
                participanteIdOld = em.merge(participanteIdOld);
            }
            if (participanteIdNew != null && !participanteIdNew.equals(participanteIdOld)) {
                participanteIdNew.getParticipaCollection().add(participa);
                participanteIdNew = em.merge(participanteIdNew);
            }
            if (grupoIdOld != null && !grupoIdOld.equals(grupoIdNew)) {
                grupoIdOld.getParticipaCollection().remove(participa);
                grupoIdOld = em.merge(grupoIdOld);
            }
            if (grupoIdNew != null && !grupoIdNew.equals(grupoIdOld)) {
                grupoIdNew.getParticipaCollection().add(participa);
                grupoIdNew = em.merge(grupoIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = participa.getId();
                if (findParticipa(id) == null) {
                    throw new NonexistentEntityException("The participa with id " + id + " no longer exists.");
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
            Participa participa;
            try {
                participa = em.getReference(Participa.class, id);
                participa.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The participa with id " + id + " no longer exists.", enfe);
            }
            Participante participanteId = participa.getParticipanteId();
            if (participanteId != null) {
                participanteId.getParticipaCollection().remove(participa);
                participanteId = em.merge(participanteId);
            }
            Grupo grupoId = participa.getGrupoId();
            if (grupoId != null) {
                grupoId.getParticipaCollection().remove(participa);
                grupoId = em.merge(grupoId);
            }
            em.remove(participa);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Participa> findParticipaEntities() {
        return findParticipaEntities(true, -1, -1);
    }

    public List<Participa> findParticipaEntities(int maxResults, int firstResult) {
        return findParticipaEntities(false, maxResults, firstResult);
    }

    private List<Participa> findParticipaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Participa.class));
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

    public Participa findParticipa(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Participa.class, id);
        } finally {
            em.close();
        }
    }

    public int getParticipaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Participa> rt = cq.from(Participa.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    public List<Participa> findParticipaByGrupo(Integer grupoid){
        EntityManager em = getEntityManager();
        List<Participa> res;
        try {
            Query q = em.createNamedQuery("Participa.findByGrupo");
            q.setParameter("grupoid", grupoid);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    public List<Participa> findEquiposByGrupo(Integer grupoid){
        EntityManager em = getEntityManager();
        List<Participa> res;
        try {
            Query q = em.createNamedQuery("Participa.findEquiposByGrupo");
            q.setParameter("grupoid", grupoid);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    public List<Participa> findPersonasByGrupo(Integer grupoid){
        EntityManager em = getEntityManager();
        List<Participa> res;
        try {
            Query q = em.createNamedQuery("Participa.findPersonasByGrupo");
            q.setParameter("grupoid", grupoid);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    
    
    public Participa findByCompeticionAndParticipante(Integer competicionid, Integer participanteid){
        EntityManager em = getEntityManager();
        Participa res;
        try {
            Query q = em.createNamedQuery("Participa.findByCompeticionAndParticipante");
            q.setParameter("competicionid", competicionid);
            q.setParameter("participanteid", participanteid);
            res = (Participa) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    public int countByParticipante(Integer participanteid) {
        EntityManager em = getEntityManager();
        int res;
        try {
            Query q = em.createNamedQuery("Participa.countByParticipante");
            q.setParameter("participanteid", participanteid);
            res =  ((Number)q.getSingleResult()).intValue();
            
        } catch (NoResultException e) {
            return 0;
        } finally {
            em.close();
        }
        return res;
    }
    
    public Participa findByEquipoCompeticion(Integer competicionid, Integer equipoid){
        EntityManager em = getEntityManager();
        Participa res;
        try {
            Query q = em.createNamedQuery("Participa.findByEquipoCompeticion");
            q.setParameter("competicionid", competicionid);
            q.setParameter("equipoid", equipoid);
            res = (Participa) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
}
