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
import modelo.Persona;
import modelo.Equipo;
import modelo.Miembro;
import modelo.dao.exceptions.NonexistentEntityException;

/**
 *
 * @author JuanM
 */
public class MiembroJpa implements Serializable {

    public MiembroJpa() {
        this.emf = Persistence.createEntityManagerFactory("JavaAppDesktopPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Miembro miembro) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Persona personaId = miembro.getPersonaId();
            if (personaId != null) {
                personaId = em.getReference(personaId.getClass(), personaId.getId());
                miembro.setPersonaId(personaId);
            }
            Equipo equipoId = miembro.getEquipoId();
            if (equipoId != null) {
                equipoId = em.getReference(equipoId.getClass(), equipoId.getId());
                miembro.setEquipoId(equipoId);
            }
            em.persist(miembro);
            if (personaId != null) {
                personaId.getMiembroCollection().add(miembro);
                personaId = em.merge(personaId);
            }
            if (equipoId != null) {
                equipoId.getMiembroCollection().add(miembro);
                equipoId = em.merge(equipoId);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Miembro miembro) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Miembro persistentMiembro = em.find(Miembro.class, miembro.getId());
            Persona personaIdOld = persistentMiembro.getPersonaId();
            Persona personaIdNew = miembro.getPersonaId();
            Equipo equipoIdOld = persistentMiembro.getEquipoId();
            Equipo equipoIdNew = miembro.getEquipoId();
            if (personaIdNew != null) {
                personaIdNew = em.getReference(personaIdNew.getClass(), personaIdNew.getId());
                miembro.setPersonaId(personaIdNew);
            }
            if (equipoIdNew != null) {
                equipoIdNew = em.getReference(equipoIdNew.getClass(), equipoIdNew.getId());
                miembro.setEquipoId(equipoIdNew);
            }
            miembro = em.merge(miembro);
            if (personaIdOld != null && !personaIdOld.equals(personaIdNew)) {
                personaIdOld.getMiembroCollection().remove(miembro);
                personaIdOld = em.merge(personaIdOld);
            }
            if (personaIdNew != null && !personaIdNew.equals(personaIdOld)) {
                personaIdNew.getMiembroCollection().add(miembro);
                personaIdNew = em.merge(personaIdNew);
            }
            if (equipoIdOld != null && !equipoIdOld.equals(equipoIdNew)) {
                equipoIdOld.getMiembroCollection().remove(miembro);
                equipoIdOld = em.merge(equipoIdOld);
            }
            if (equipoIdNew != null && !equipoIdNew.equals(equipoIdOld)) {
                equipoIdNew.getMiembroCollection().add(miembro);
                equipoIdNew = em.merge(equipoIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = miembro.getId();
                if (findMiembro(id) == null) {
                    throw new NonexistentEntityException("The miembro with id " + id + " no longer exists.");
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
            Miembro miembro;
            try {
                miembro = em.getReference(Miembro.class, id);
                miembro.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The miembro with id " + id + " no longer exists.", enfe);
            }
            Persona personaId = miembro.getPersonaId();
            if (personaId != null) {
                personaId.getMiembroCollection().remove(miembro);
                personaId = em.merge(personaId);
            }
            Equipo equipoId = miembro.getEquipoId();
            if (equipoId != null) {
                equipoId.getMiembroCollection().remove(miembro);
                equipoId = em.merge(equipoId);
            }
            em.remove(miembro);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Miembro> findMiembroEntities() {
        return findMiembroEntities(true, -1, -1);
    }

    public List<Miembro> findMiembroEntities(int maxResults, int firstResult) {
        return findMiembroEntities(false, maxResults, firstResult);
    }

    private List<Miembro> findMiembroEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Miembro.class));
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

    public Miembro findMiembro(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Miembro.class, id);
        } finally {
            em.close();
        }
    }

    public int getMiembroCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Miembro> rt = cq.from(Miembro.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    public List<Miembro> findByEquipo(Integer id) {
        EntityManager em = getEntityManager();
        List<Miembro> res;
        //System.out.println(c.getId());
        try {
            Query q = em.createNamedQuery("Miembro.findByEquipo");
            q.setParameter("id", id);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    public List<Miembro> findByPersona(Integer personaid) {
        EntityManager em = getEntityManager();
        List<Miembro> res;
        try {
            Query q = em.createNamedQuery("Miembro.findByPersona");
            q.setParameter("personaid", personaid);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    public Miembro findByPersonaGrupo(Integer personaid, Integer grupoid) {
        EntityManager em = getEntityManager();
        Miembro res;
        try {
            Query q = em.createNamedQuery("Miembro.findByPersonaGrupo");
            q.setParameter("personaid", personaid);
            q.setParameter("grupoid", grupoid);
            res = (Miembro)q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
}
