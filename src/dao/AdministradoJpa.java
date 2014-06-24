package dao;

import modelo.Administrado;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import modelo.Usuario;
import modelo.Competicion;
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
public class AdministradoJpa implements Serializable {

    public AdministradoJpa() {
        this.emf = Persistence.createEntityManagerFactory("JavaAppDesktopPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Administrado administrado) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario usuarioId = administrado.getUsuarioId();
            if (usuarioId != null) {
                usuarioId = em.getReference(usuarioId.getClass(), usuarioId.getId());
                administrado.setUsuarioId(usuarioId);
            }
            Competicion competicionId = administrado.getCompeticionId();
            if (competicionId != null) {
                competicionId = em.getReference(competicionId.getClass(), competicionId.getId());
                administrado.setCompeticionId(competicionId);
            }
            em.persist(administrado);
            if (usuarioId != null) {
                usuarioId.getAdministradoCollection().add(administrado);
                usuarioId = em.merge(usuarioId);
            }
            if (competicionId != null) {
                competicionId.getAdministradoCollection().add(administrado);
                competicionId = em.merge(competicionId);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Administrado administrado) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Administrado persistentAdministrado = em.find(Administrado.class, administrado.getId());
            Usuario usuarioIdOld = persistentAdministrado.getUsuarioId();
            Usuario usuarioIdNew = administrado.getUsuarioId();
            Competicion competicionIdOld = persistentAdministrado.getCompeticionId();
            Competicion competicionIdNew = administrado.getCompeticionId();
            if (usuarioIdNew != null) {
                usuarioIdNew = em.getReference(usuarioIdNew.getClass(), usuarioIdNew.getId());
                administrado.setUsuarioId(usuarioIdNew);
            }
            if (competicionIdNew != null) {
                competicionIdNew = em.getReference(competicionIdNew.getClass(), competicionIdNew.getId());
                administrado.setCompeticionId(competicionIdNew);
            }
            administrado = em.merge(administrado);
            if (usuarioIdOld != null && !usuarioIdOld.equals(usuarioIdNew)) {
                usuarioIdOld.getAdministradoCollection().remove(administrado);
                usuarioIdOld = em.merge(usuarioIdOld);
            }
            if (usuarioIdNew != null && !usuarioIdNew.equals(usuarioIdOld)) {
                usuarioIdNew.getAdministradoCollection().add(administrado);
                usuarioIdNew = em.merge(usuarioIdNew);
            }
            if (competicionIdOld != null && !competicionIdOld.equals(competicionIdNew)) {
                competicionIdOld.getAdministradoCollection().remove(administrado);
                competicionIdOld = em.merge(competicionIdOld);
            }
            if (competicionIdNew != null && !competicionIdNew.equals(competicionIdOld)) {
                competicionIdNew.getAdministradoCollection().add(administrado);
                competicionIdNew = em.merge(competicionIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = administrado.getId();
                if (findAdministrado(id) == null) {
                    throw new NonexistentEntityException("The administrado with id " + id + " no longer exists.");
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
            Administrado administrado;
            try {
                administrado = em.getReference(Administrado.class, id);
                administrado.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The administrado with id " + id + " no longer exists.", enfe);
            }
            Usuario usuarioId = administrado.getUsuarioId();
            if (usuarioId != null) {
                usuarioId.getAdministradoCollection().remove(administrado);
                usuarioId = em.merge(usuarioId);
            }
            Competicion competicionId = administrado.getCompeticionId();
            if (competicionId != null) {
                competicionId.getAdministradoCollection().remove(administrado);
                competicionId = em.merge(competicionId);
            }
            em.remove(administrado);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Administrado> findAdministradoEntities() {
        return findAdministradoEntities(true, -1, -1);
    }

    public List<Administrado> findAdministradoEntities(int maxResults, int firstResult) {
        return findAdministradoEntities(false, maxResults, firstResult);
    }

    private List<Administrado> findAdministradoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Administrado.class));
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

    public Administrado findAdministrado(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Administrado.class, id);
        } finally {
            em.close();
        }
    }

    public int getAdministradoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Administrado> rt = cq.from(Administrado.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    /** Devuelve la lista de objetos Administrado de una competición
     * 
     * @param comp Competicion
     * @return List<Administrado>
     */
    public List<Administrado> findAdministradoByCompeticion(Competicion comp) {
        EntityManager em = getEntityManager();
        List<Administrado> res;
        try {
            Query q = em.createNamedQuery("Administrado.findByCompeticion");
            q.setParameter("compid", comp);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally{
            em.close();
        }
        return res;
    }
    
    /** Devuelve los nombres de las competiciones administradas por un usuario
     * 
     * @param usuarioid     Identificador del usuario
     * @return List<Administrado>
     */
    public List<String> findCompeticionesByUser(Integer usuarioid) {
        EntityManager em = getEntityManager();
        List<String> res;
        try {
            Query q = em.createNamedQuery("Administrado.findCompeticionesByUser");
            q.setParameter("userid", usuarioid);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally{
            em.close();
        }
        return res;
    }
    
    /** Devuelve una lista de Administrados de un usuario
     * 
     * @param usuarioid     Identificador del usuario
     * @return List<Administrado>
     */
    public List<Administrado> findByUsuario(Integer usuarioid) {
        EntityManager em = getEntityManager();
        List<Administrado> res;
        try {
            Query q = em.createNamedQuery("Administrado.findByUsuario");
            q.setParameter("usuarioid", usuarioid);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally{
            em.close();
        }
        return res;
    }
    
}
