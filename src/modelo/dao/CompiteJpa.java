package modelo.dao;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import modelo.Compite;
import modelo.Prueba;
import modelo.Inscripcion;
import modelo.dao.exceptions.NonexistentEntityException;

/**
 *
 * @author JuanM
 */
public class CompiteJpa implements Serializable {

    public CompiteJpa() {
        this.emf = Persistence.createEntityManagerFactory("JavaAppDesktopPU");
    }
    
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Compite compite) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Prueba pruebaId = compite.getPruebaId();
            if (pruebaId != null) {
                pruebaId = em.getReference(pruebaId.getClass(), pruebaId.getId());
                compite.setPruebaId(pruebaId);
            }
            Inscripcion inscripcionId = compite.getInscripcionId();
            if (inscripcionId != null) {
                inscripcionId = em.getReference(inscripcionId.getClass(), inscripcionId.getId());
                compite.setInscripcionId(inscripcionId);
            }
            em.persist(compite);
            if (pruebaId != null) {
                pruebaId.getCompiteCollection().add(compite);
                pruebaId = em.merge(pruebaId);
            }
            if (inscripcionId != null) {
                inscripcionId.getCompiteCollection().add(compite);
                inscripcionId = em.merge(inscripcionId);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Compite compite) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Compite persistentCompite = em.find(Compite.class, compite.getId());
            Prueba pruebaIdOld = persistentCompite.getPruebaId();
            Prueba pruebaIdNew = compite.getPruebaId();
            Inscripcion inscripcionIdOld = persistentCompite.getInscripcionId();
            Inscripcion inscripcionIdNew = compite.getInscripcionId();
            if (pruebaIdNew != null) {
                pruebaIdNew = em.getReference(pruebaIdNew.getClass(), pruebaIdNew.getId());
                compite.setPruebaId(pruebaIdNew);
            }
            if (inscripcionIdNew != null) {
                inscripcionIdNew = em.getReference(inscripcionIdNew.getClass(), inscripcionIdNew.getId());
                compite.setInscripcionId(inscripcionIdNew);
            }
            compite = em.merge(compite);
            if (pruebaIdOld != null && !pruebaIdOld.equals(pruebaIdNew)) {
                pruebaIdOld.getCompiteCollection().remove(compite);
                pruebaIdOld = em.merge(pruebaIdOld);
            }
            if (pruebaIdNew != null && !pruebaIdNew.equals(pruebaIdOld)) {
                pruebaIdNew.getCompiteCollection().add(compite);
                pruebaIdNew = em.merge(pruebaIdNew);
            }
            if (inscripcionIdOld != null && !inscripcionIdOld.equals(inscripcionIdNew)) {
                inscripcionIdOld.getCompiteCollection().remove(compite);
                inscripcionIdOld = em.merge(inscripcionIdOld);
            }
            if (inscripcionIdNew != null && !inscripcionIdNew.equals(inscripcionIdOld)) {
                inscripcionIdNew.getCompiteCollection().add(compite);
                inscripcionIdNew = em.merge(inscripcionIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = compite.getId();
                if (findCompite(id) == null) {
                    throw new NonexistentEntityException("The compite with id " + id + " no longer exists.");
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
            Compite compite;
            try {
                compite = em.getReference(Compite.class, id);
                compite.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The compite with id " + id + " no longer exists.", enfe);
            }
            Prueba pruebaId = compite.getPruebaId();
            if (pruebaId != null) {
                pruebaId.getCompiteCollection().remove(compite);
                pruebaId = em.merge(pruebaId);
            }
            Inscripcion inscripcionId = compite.getInscripcionId();
            if (inscripcionId != null) {
                inscripcionId.getCompiteCollection().remove(compite);
                inscripcionId = em.merge(inscripcionId);
            }
            em.remove(compite);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Compite> findCompiteEntities() {
        return findCompiteEntities(true, -1, -1);
    }

    public List<Compite> findCompiteEntities(int maxResults, int firstResult) {
        return findCompiteEntities(false, maxResults, firstResult);
    }

    private List<Compite> findCompiteEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Compite.class));
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

    public Compite findCompite(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Compite.class, id);
        } finally {
            em.close();
        }
    }

    public int getCompiteCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Compite> rt = cq.from(Compite.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
