package dao;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import modelo.Prueba;
import modelo.Competicion;
import modelo.Compuesta;
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
public class CompuestaJpa implements Serializable {

    public CompuestaJpa() {
        this.emf = Persistence.createEntityManagerFactory("JavaAppDesktopPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Compuesta compuesta) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Prueba pruebaId = compuesta.getPruebaId();
            if (pruebaId != null) {
                pruebaId = em.getReference(pruebaId.getClass(), pruebaId.getId());
                compuesta.setPruebaId(pruebaId);
            }
            Competicion competicionId = compuesta.getCompeticionId();
            if (competicionId != null) {
                competicionId = em.getReference(competicionId.getClass(), competicionId.getId());
                compuesta.setCompeticionId(competicionId);
            }
            em.persist(compuesta);
            if (pruebaId != null) {
                pruebaId.getCompuestaCollection().add(compuesta);
                pruebaId = em.merge(pruebaId);
            }
            if (competicionId != null) {
                competicionId.getCompuestaCollection().add(compuesta);
                competicionId = em.merge(competicionId);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Compuesta compuesta) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Compuesta persistentCompuesta = em.find(Compuesta.class, compuesta.getId());
            Prueba pruebaIdOld = persistentCompuesta.getPruebaId();
            Prueba pruebaIdNew = compuesta.getPruebaId();
            Competicion competicionIdOld = persistentCompuesta.getCompeticionId();
            Competicion competicionIdNew = compuesta.getCompeticionId();
            if (pruebaIdNew != null) {
                pruebaIdNew = em.getReference(pruebaIdNew.getClass(), pruebaIdNew.getId());
                compuesta.setPruebaId(pruebaIdNew);
            }
            if (competicionIdNew != null) {
                competicionIdNew = em.getReference(competicionIdNew.getClass(), competicionIdNew.getId());
                compuesta.setCompeticionId(competicionIdNew);
            }
            compuesta = em.merge(compuesta);
            if (pruebaIdOld != null && !pruebaIdOld.equals(pruebaIdNew)) {
                pruebaIdOld.getCompuestaCollection().remove(compuesta);
                pruebaIdOld = em.merge(pruebaIdOld);
            }
            if (pruebaIdNew != null && !pruebaIdNew.equals(pruebaIdOld)) {
                pruebaIdNew.getCompuestaCollection().add(compuesta);
                pruebaIdNew = em.merge(pruebaIdNew);
            }
            if (competicionIdOld != null && !competicionIdOld.equals(competicionIdNew)) {
                competicionIdOld.getCompuestaCollection().remove(compuesta);
                competicionIdOld = em.merge(competicionIdOld);
            }
            if (competicionIdNew != null && !competicionIdNew.equals(competicionIdOld)) {
                competicionIdNew.getCompuestaCollection().add(compuesta);
                competicionIdNew = em.merge(competicionIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = compuesta.getId();
                if (findCompuesta(id) == null) {
                    throw new NonexistentEntityException("The compuesta with id " + id + " no longer exists.");
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
            Compuesta compuesta;
            try {
                compuesta = em.getReference(Compuesta.class, id);
                compuesta.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The compuesta with id " + id + " no longer exists.", enfe);
            }
            Prueba pruebaId = compuesta.getPruebaId();
            if (pruebaId != null) {
                pruebaId.getCompuestaCollection().remove(compuesta);
                pruebaId = em.merge(pruebaId);
            }
            Competicion competicionId = compuesta.getCompeticionId();
            if (competicionId != null) {
                competicionId.getCompuestaCollection().remove(compuesta);
                competicionId = em.merge(competicionId);
            }
            em.remove(compuesta);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Compuesta> findCompuestaEntities() {
        return findCompuestaEntities(true, -1, -1);
    }

    public List<Compuesta> findCompuestaEntities(int maxResults, int firstResult) {
        return findCompuestaEntities(false, maxResults, firstResult);
    }

    private List<Compuesta> findCompuestaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Compuesta.class));
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

    public Compuesta findCompuesta(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Compuesta.class, id);
        } finally {
            em.close();
        }
    }

    public int getCompuestaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Compuesta> rt = cq.from(Compuesta.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    // author: Juan María Frías Hidalgo

    /** Devuelve un objeto compuesta a partir del identificador de la prueba y
     * de la competición
     * 
     * @param pruebaid          Identificador de la prueba
     * @param competicionid     Identificador de la competición
     * @return Compuesta
     */
    public Compuesta findCompuestaByPrueba_Competicion(Integer pruebaid, Integer competicionid) {
        EntityManager em = getEntityManager();
        Compuesta res;
        try {
            Query q = em.createNamedQuery("Compuesta.findByPrueba_Competicion");
            q.setParameter("pruebaid", pruebaid);
            q.setParameter("competicionid", competicionid);
            res =  (Compuesta)q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally{
            em.close();
        }
        return res;
    }
    
    /**Devuelve una lista de objetos Compuesta a partir de una competición.
     * 
     * @param competicionid     Identificador de la competición
     * @return List<Compuesta>
     */
    public List<Compuesta> findCompuestaByCompeticion(Integer competicionid) {
        EntityManager em = getEntityManager();
        List<Compuesta> res;
        try {
            Query q = em.createNamedQuery("Compuesta.findByCompeticion");
            q.setParameter("competicionid", competicionid);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally{
            em.close();
        }
        return res;
    }
    
}
