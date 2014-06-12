package dao;

import modelo.Acceso;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import modelo.Usuario;
import modelo.Grupo;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import dao.exceptions.NonexistentEntityException;

/**
 *
 * @author JuanM
 */
public class AccesoJpa implements Serializable {

    public AccesoJpa() {
        this.emf = Persistence.createEntityManagerFactory("JavaAppDesktopPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Acceso acceso) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario usuarioId = acceso.getUsuarioId();
            if (usuarioId != null) {
                usuarioId = em.getReference(usuarioId.getClass(), usuarioId.getId());
                acceso.setUsuarioId(usuarioId);
            }
            Grupo grupoId = acceso.getGrupoId();
            if (grupoId != null) {
                grupoId = em.getReference(grupoId.getClass(), grupoId.getId());
                acceso.setGrupoId(grupoId);
            }
            em.persist(acceso);
            if (usuarioId != null) {
                usuarioId.getAccesoCollection().add(acceso);
                usuarioId = em.merge(usuarioId);
            }
            if (grupoId != null) {
                grupoId.getAccesoCollection().add(acceso);
                grupoId = em.merge(grupoId);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Acceso acceso) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Acceso persistentAcceso = em.find(Acceso.class, acceso.getId());
            Usuario usuarioIdOld = persistentAcceso.getUsuarioId();
            Usuario usuarioIdNew = acceso.getUsuarioId();
            Grupo grupoIdOld = persistentAcceso.getGrupoId();
            Grupo grupoIdNew = acceso.getGrupoId();
            if (usuarioIdNew != null) {
                usuarioIdNew = em.getReference(usuarioIdNew.getClass(), usuarioIdNew.getId());
                acceso.setUsuarioId(usuarioIdNew);
            }
            if (grupoIdNew != null) {
                grupoIdNew = em.getReference(grupoIdNew.getClass(), grupoIdNew.getId());
                acceso.setGrupoId(grupoIdNew);
            }
            acceso = em.merge(acceso);
            if (usuarioIdOld != null && !usuarioIdOld.equals(usuarioIdNew)) {
                usuarioIdOld.getAccesoCollection().remove(acceso);
                usuarioIdOld = em.merge(usuarioIdOld);
            }
            if (usuarioIdNew != null && !usuarioIdNew.equals(usuarioIdOld)) {
                usuarioIdNew.getAccesoCollection().add(acceso);
                usuarioIdNew = em.merge(usuarioIdNew);
            }
            if (grupoIdOld != null && !grupoIdOld.equals(grupoIdNew)) {
                grupoIdOld.getAccesoCollection().remove(acceso);
                grupoIdOld = em.merge(grupoIdOld);
            }
            if (grupoIdNew != null && !grupoIdNew.equals(grupoIdOld)) {
                grupoIdNew.getAccesoCollection().add(acceso);
                grupoIdNew = em.merge(grupoIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = acceso.getId();
                if (findAcceso(id) == null) {
                    throw new NonexistentEntityException("The acceso with id " + id + " no longer exists.");
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
            Acceso acceso;
            try {
                acceso = em.getReference(Acceso.class, id);
                acceso.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The acceso with id " + id + " no longer exists.", enfe);
            }
            Usuario usuarioId = acceso.getUsuarioId();
            if (usuarioId != null) {
                usuarioId.getAccesoCollection().remove(acceso);
                usuarioId = em.merge(usuarioId);
            }
            Grupo grupoId = acceso.getGrupoId();
            if (grupoId != null) {
                grupoId.getAccesoCollection().remove(acceso);
                grupoId = em.merge(grupoId);
            }
            em.remove(acceso);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Acceso> findAccesoEntities() {
        return findAccesoEntities(true, -1, -1);
    }

    public List<Acceso> findAccesoEntities(int maxResults, int firstResult) {
        return findAccesoEntities(false, maxResults, firstResult);
    }

    private List<Acceso> findAccesoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Acceso.class));
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

    public Acceso findAcceso(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Acceso.class, id);
        } finally {
            em.close();
        }
    }

    public int getAccesoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Acceso> rt = cq.from(Acceso.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
