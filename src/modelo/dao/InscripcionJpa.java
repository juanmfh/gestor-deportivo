package modelo.dao;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import modelo.entities.Grupo;
import modelo.entities.Competicion;
import modelo.entities.Inscripcion;
import modelo.entities.Registro;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import modelo.dao.exceptions.IllegalOrphanException;
import modelo.dao.exceptions.NonexistentEntityException;

/**
 *
 * @author JuanM
 */
public class InscripcionJpa implements Serializable {

    public InscripcionJpa() {
        this.emf = Persistence.createEntityManagerFactory("JavaAppDesktopPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Inscripcion inscripcion) {
        if (inscripcion.getRegistroCollection() == null) {
            inscripcion.setRegistroCollection(new ArrayList<Registro>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Grupo grupoId = inscripcion.getGrupoId();
            if (grupoId != null) {
                grupoId = em.getReference(grupoId.getClass(), grupoId.getId());
                inscripcion.setGrupoId(grupoId);
            }
            Competicion competicionId = inscripcion.getCompeticionId();
            if (competicionId != null) {
                competicionId = em.getReference(competicionId.getClass(), competicionId.getId());
                inscripcion.setCompeticionId(competicionId);
            }
            Collection<Registro> attachedRegistroCollection = new ArrayList<Registro>();
            for (Registro registroCollectionRegistroToAttach : inscripcion.getRegistroCollection()) {
                registroCollectionRegistroToAttach = em.getReference(registroCollectionRegistroToAttach.getClass(), registroCollectionRegistroToAttach.getId());
                attachedRegistroCollection.add(registroCollectionRegistroToAttach);
            }
            inscripcion.setRegistroCollection(attachedRegistroCollection);
            em.persist(inscripcion);
            if (grupoId != null) {
                grupoId.getInscripcionCollection().add(inscripcion);
                grupoId = em.merge(grupoId);
            }
            if (competicionId != null) {
                competicionId.getInscripcionCollection().add(inscripcion);
                competicionId = em.merge(competicionId);
            }
            for (Registro registroCollectionRegistro : inscripcion.getRegistroCollection()) {
                Inscripcion oldInscripcionIdOfRegistroCollectionRegistro = registroCollectionRegistro.getInscripcionId();
                registroCollectionRegistro.setInscripcionId(inscripcion);
                registroCollectionRegistro = em.merge(registroCollectionRegistro);
                if (oldInscripcionIdOfRegistroCollectionRegistro != null) {
                    oldInscripcionIdOfRegistroCollectionRegistro.getRegistroCollection().remove(registroCollectionRegistro);
                    oldInscripcionIdOfRegistroCollectionRegistro = em.merge(oldInscripcionIdOfRegistroCollectionRegistro);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Inscripcion inscripcion) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Inscripcion persistentInscripcion = em.find(Inscripcion.class, inscripcion.getId());
            Grupo grupoIdOld = persistentInscripcion.getGrupoId();
            Grupo grupoIdNew = inscripcion.getGrupoId();
            Competicion competicionIdOld = persistentInscripcion.getCompeticionId();
            Competicion competicionIdNew = inscripcion.getCompeticionId();
            Collection<Registro> registroCollectionOld = persistentInscripcion.getRegistroCollection();
            Collection<Registro> registroCollectionNew = inscripcion.getRegistroCollection();
            List<String> illegalOrphanMessages = null;
            for (Registro registroCollectionOldRegistro : registroCollectionOld) {
                if (!registroCollectionNew.contains(registroCollectionOldRegistro)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Registro " + registroCollectionOldRegistro + " since its inscripcionId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (grupoIdNew != null) {
                grupoIdNew = em.getReference(grupoIdNew.getClass(), grupoIdNew.getId());
                inscripcion.setGrupoId(grupoIdNew);
            }
            if (competicionIdNew != null) {
                competicionIdNew = em.getReference(competicionIdNew.getClass(), competicionIdNew.getId());
                inscripcion.setCompeticionId(competicionIdNew);
            }
            Collection<Registro> attachedRegistroCollectionNew = new ArrayList<Registro>();
            for (Registro registroCollectionNewRegistroToAttach : registroCollectionNew) {
                registroCollectionNewRegistroToAttach = em.getReference(registroCollectionNewRegistroToAttach.getClass(), registroCollectionNewRegistroToAttach.getId());
                attachedRegistroCollectionNew.add(registroCollectionNewRegistroToAttach);
            }
            registroCollectionNew = attachedRegistroCollectionNew;
            inscripcion.setRegistroCollection(registroCollectionNew);
            inscripcion = em.merge(inscripcion);
            if (grupoIdOld != null && !grupoIdOld.equals(grupoIdNew)) {
                grupoIdOld.getInscripcionCollection().remove(inscripcion);
                grupoIdOld = em.merge(grupoIdOld);
            }
            if (grupoIdNew != null && !grupoIdNew.equals(grupoIdOld)) {
                grupoIdNew.getInscripcionCollection().add(inscripcion);
                grupoIdNew = em.merge(grupoIdNew);
            }
            if (competicionIdOld != null && !competicionIdOld.equals(competicionIdNew)) {
                competicionIdOld.getInscripcionCollection().remove(inscripcion);
                competicionIdOld = em.merge(competicionIdOld);
            }
            if (competicionIdNew != null && !competicionIdNew.equals(competicionIdOld)) {
                competicionIdNew.getInscripcionCollection().add(inscripcion);
                competicionIdNew = em.merge(competicionIdNew);
            }
            for (Registro registroCollectionNewRegistro : registroCollectionNew) {
                if (!registroCollectionOld.contains(registroCollectionNewRegistro)) {
                    Inscripcion oldInscripcionIdOfRegistroCollectionNewRegistro = registroCollectionNewRegistro.getInscripcionId();
                    registroCollectionNewRegistro.setInscripcionId(inscripcion);
                    registroCollectionNewRegistro = em.merge(registroCollectionNewRegistro);
                    if (oldInscripcionIdOfRegistroCollectionNewRegistro != null && !oldInscripcionIdOfRegistroCollectionNewRegistro.equals(inscripcion)) {
                        oldInscripcionIdOfRegistroCollectionNewRegistro.getRegistroCollection().remove(registroCollectionNewRegistro);
                        oldInscripcionIdOfRegistroCollectionNewRegistro = em.merge(oldInscripcionIdOfRegistroCollectionNewRegistro);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = inscripcion.getId();
                if (findInscripcion(id) == null) {
                    throw new NonexistentEntityException("The inscripcion with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Inscripcion inscripcion;
            try {
                inscripcion = em.getReference(Inscripcion.class, id);
                inscripcion.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The inscripcion with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Registro> registroCollectionOrphanCheck = inscripcion.getRegistroCollection();
            for (Registro registroCollectionOrphanCheckRegistro : registroCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Inscripcion (" + inscripcion + ") cannot be destroyed since the Registro " + registroCollectionOrphanCheckRegistro + " in its registroCollection field has a non-nullable inscripcionId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Grupo grupoId = inscripcion.getGrupoId();
            if (grupoId != null) {
                grupoId.getInscripcionCollection().remove(inscripcion);
                grupoId = em.merge(grupoId);
            }
            Competicion competicionId = inscripcion.getCompeticionId();
            if (competicionId != null) {
                competicionId.getInscripcionCollection().remove(inscripcion);
                competicionId = em.merge(competicionId);
            }
            em.remove(inscripcion);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Inscripcion> findInscripcionEntities() {
        return findInscripcionEntities(true, -1, -1);
    }

    public List<Inscripcion> findInscripcionEntities(int maxResults, int firstResult) {
        return findInscripcionEntities(false, maxResults, firstResult);
    }

    private List<Inscripcion> findInscripcionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Inscripcion.class));
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

    public Inscripcion findInscripcion(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Inscripcion.class, id);
        } finally {
            em.close();
        }
    }

    public int getInscripcionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Inscripcion> rt = cq.from(Inscripcion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    // author: Juan María Frías Hidalgo
    
    
    /**Devuelve la lista de inscripciones de una competición
     * 
     * @param competicionid     Identificador de la competición.
     * @return List<Inscripciones>
     */
    public List<Inscripcion> findInscripcionByCompeticion(Integer competicionid) {
        EntityManager em = getEntityManager();
        List<Inscripcion> res;
        try {
            Query q = em.createNamedQuery("Inscripcion.findByCompeticion");
            q.setParameter("id", competicionid);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    /**Devuelve la inscripción de un grupo determinado en una competición
     * 
     * @param competicionid     Identificador de la competición
     * @param grupoid           Identificador del grupo
     * @return Inscripcion
     */
    public Inscripcion findInscripcionByCompeticionByGrupo(Integer competicionid, Integer grupoid) {
        EntityManager em = getEntityManager();
        Inscripcion res;
        try {
            Query q = em.createNamedQuery("Inscripcion.findByCompeticionByGrupo");
            q.setParameter("competicionid", competicionid);
            q.setParameter("grupoid", grupoid);
            res = (Inscripcion) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    /**Devuelve una inscripción de un grupo en una competición.
     * 
     * @param competicionid     Identificador de la competición.
     * @param nombregrupo       Nombre del grupo.
     * @return  Inscripcion
     */
    public Inscripcion findInscripcionByCompeticionByNombreGrupo(Integer competicionid, String nombregrupo) {
        EntityManager em = getEntityManager();
        Inscripcion res;
        try {
            Query q = em.createNamedQuery("Inscripcion.findByCompeticionByNombreGrupo");
            q.setParameter("competicionid", competicionid);
            q.setParameter("nombregrupo", nombregrupo);
            res = (Inscripcion) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
}
