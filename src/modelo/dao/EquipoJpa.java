package modelo.dao;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import modelo.Participante;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import modelo.Equipo;
import modelo.Miembro;
import modelo.dao.exceptions.IllegalOrphanException;
import modelo.dao.exceptions.NonexistentEntityException;

/**
 *
 * @author JuanM
 */
public class EquipoJpa implements Serializable {

    public EquipoJpa() {
        this.emf = Persistence.createEntityManagerFactory("JavaAppDesktopPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Equipo equipo) {
        if (equipo.getParticipanteCollection() == null) {
            equipo.setParticipanteCollection(new ArrayList<Participante>());
        }
        if (equipo.getMiembroCollection() == null) {
            equipo.setMiembroCollection(new ArrayList<Miembro>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Participante> attachedParticipanteCollection = new ArrayList<Participante>();
            for (Participante participanteCollectionParticipanteToAttach : equipo.getParticipanteCollection()) {
                participanteCollectionParticipanteToAttach = em.getReference(participanteCollectionParticipanteToAttach.getClass(), participanteCollectionParticipanteToAttach.getId());
                attachedParticipanteCollection.add(participanteCollectionParticipanteToAttach);
            }
            equipo.setParticipanteCollection(attachedParticipanteCollection);
            Collection<Miembro> attachedMiembroCollection = new ArrayList<Miembro>();
            for (Miembro miembroCollectionMiembroToAttach : equipo.getMiembroCollection()) {
                miembroCollectionMiembroToAttach = em.getReference(miembroCollectionMiembroToAttach.getClass(), miembroCollectionMiembroToAttach.getId());
                attachedMiembroCollection.add(miembroCollectionMiembroToAttach);
            }
            equipo.setMiembroCollection(attachedMiembroCollection);
            em.persist(equipo);
            for (Participante participanteCollectionParticipante : equipo.getParticipanteCollection()) {
                Equipo oldEquipoIdOfParticipanteCollectionParticipante = participanteCollectionParticipante.getEquipoId();
                participanteCollectionParticipante.setEquipoId(equipo);
                participanteCollectionParticipante = em.merge(participanteCollectionParticipante);
                if (oldEquipoIdOfParticipanteCollectionParticipante != null) {
                    oldEquipoIdOfParticipanteCollectionParticipante.getParticipanteCollection().remove(participanteCollectionParticipante);
                    oldEquipoIdOfParticipanteCollectionParticipante = em.merge(oldEquipoIdOfParticipanteCollectionParticipante);
                }
            }
            for (Miembro miembroCollectionMiembro : equipo.getMiembroCollection()) {
                Equipo oldEquipoIdOfMiembroCollectionMiembro = miembroCollectionMiembro.getEquipoId();
                miembroCollectionMiembro.setEquipoId(equipo);
                miembroCollectionMiembro = em.merge(miembroCollectionMiembro);
                if (oldEquipoIdOfMiembroCollectionMiembro != null) {
                    oldEquipoIdOfMiembroCollectionMiembro.getMiembroCollection().remove(miembroCollectionMiembro);
                    oldEquipoIdOfMiembroCollectionMiembro = em.merge(oldEquipoIdOfMiembroCollectionMiembro);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Equipo equipo) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Equipo persistentEquipo = em.find(Equipo.class, equipo.getId());
            Collection<Participante> participanteCollectionOld = persistentEquipo.getParticipanteCollection();
            Collection<Participante> participanteCollectionNew = equipo.getParticipanteCollection();
            Collection<Miembro> miembroCollectionOld = persistentEquipo.getMiembroCollection();
            Collection<Miembro> miembroCollectionNew = equipo.getMiembroCollection();
            List<String> illegalOrphanMessages = null;
            for (Miembro miembroCollectionOldMiembro : miembroCollectionOld) {
                if (!miembroCollectionNew.contains(miembroCollectionOldMiembro)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Miembro " + miembroCollectionOldMiembro + " since its equipoId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Participante> attachedParticipanteCollectionNew = new ArrayList<Participante>();
            for (Participante participanteCollectionNewParticipanteToAttach : participanteCollectionNew) {
                participanteCollectionNewParticipanteToAttach = em.getReference(participanteCollectionNewParticipanteToAttach.getClass(), participanteCollectionNewParticipanteToAttach.getId());
                attachedParticipanteCollectionNew.add(participanteCollectionNewParticipanteToAttach);
            }
            participanteCollectionNew = attachedParticipanteCollectionNew;
            equipo.setParticipanteCollection(participanteCollectionNew);
            Collection<Miembro> attachedMiembroCollectionNew = new ArrayList<Miembro>();
            for (Miembro miembroCollectionNewMiembroToAttach : miembroCollectionNew) {
                miembroCollectionNewMiembroToAttach = em.getReference(miembroCollectionNewMiembroToAttach.getClass(), miembroCollectionNewMiembroToAttach.getId());
                attachedMiembroCollectionNew.add(miembroCollectionNewMiembroToAttach);
            }
            miembroCollectionNew = attachedMiembroCollectionNew;
            equipo.setMiembroCollection(miembroCollectionNew);
            equipo = em.merge(equipo);
            for (Participante participanteCollectionOldParticipante : participanteCollectionOld) {
                if (!participanteCollectionNew.contains(participanteCollectionOldParticipante)) {
                    participanteCollectionOldParticipante.setEquipoId(null);
                    participanteCollectionOldParticipante = em.merge(participanteCollectionOldParticipante);
                }
            }
            for (Participante participanteCollectionNewParticipante : participanteCollectionNew) {
                if (!participanteCollectionOld.contains(participanteCollectionNewParticipante)) {
                    Equipo oldEquipoIdOfParticipanteCollectionNewParticipante = participanteCollectionNewParticipante.getEquipoId();
                    participanteCollectionNewParticipante.setEquipoId(equipo);
                    participanteCollectionNewParticipante = em.merge(participanteCollectionNewParticipante);
                    if (oldEquipoIdOfParticipanteCollectionNewParticipante != null && !oldEquipoIdOfParticipanteCollectionNewParticipante.equals(equipo)) {
                        oldEquipoIdOfParticipanteCollectionNewParticipante.getParticipanteCollection().remove(participanteCollectionNewParticipante);
                        oldEquipoIdOfParticipanteCollectionNewParticipante = em.merge(oldEquipoIdOfParticipanteCollectionNewParticipante);
                    }
                }
            }
            for (Miembro miembroCollectionNewMiembro : miembroCollectionNew) {
                if (!miembroCollectionOld.contains(miembroCollectionNewMiembro)) {
                    Equipo oldEquipoIdOfMiembroCollectionNewMiembro = miembroCollectionNewMiembro.getEquipoId();
                    miembroCollectionNewMiembro.setEquipoId(equipo);
                    miembroCollectionNewMiembro = em.merge(miembroCollectionNewMiembro);
                    if (oldEquipoIdOfMiembroCollectionNewMiembro != null && !oldEquipoIdOfMiembroCollectionNewMiembro.equals(equipo)) {
                        oldEquipoIdOfMiembroCollectionNewMiembro.getMiembroCollection().remove(miembroCollectionNewMiembro);
                        oldEquipoIdOfMiembroCollectionNewMiembro = em.merge(oldEquipoIdOfMiembroCollectionNewMiembro);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = equipo.getId();
                if (findEquipo(id) == null) {
                    throw new NonexistentEntityException("The equipo with id " + id + " no longer exists.");
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
            Equipo equipo;
            try {
                equipo = em.getReference(Equipo.class, id);
                equipo.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The equipo with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Miembro> miembroCollectionOrphanCheck = equipo.getMiembroCollection();
            for (Miembro miembroCollectionOrphanCheckMiembro : miembroCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Equipo (" + equipo + ") cannot be destroyed since the Miembro " + miembroCollectionOrphanCheckMiembro + " in its miembroCollection field has a non-nullable equipoId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Participante> participanteCollection = equipo.getParticipanteCollection();
            for (Participante participanteCollectionParticipante : participanteCollection) {
                participanteCollectionParticipante.setEquipoId(null);
                participanteCollectionParticipante = em.merge(participanteCollectionParticipante);
            }
            em.remove(equipo);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Equipo> findEquipoEntities() {
        return findEquipoEntities(true, -1, -1);
    }

    public List<Equipo> findEquipoEntities(int maxResults, int firstResult) {
        return findEquipoEntities(false, maxResults, firstResult);
    }

    private List<Equipo> findEquipoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Equipo.class));
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

    public Equipo findEquipo(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Equipo.class, id);
        } finally {
            em.close();
        }
    }

    public int getEquipoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Equipo> rt = cq.from(Equipo.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    public Equipo findByNombreAndCompeticion(String nombre, Integer competicionid) {
        EntityManager em = getEntityManager();
        Equipo res;
        try {
            Query q = em.createNamedQuery("Equipo.findByNombreAndCompeticion");
            q.setParameter("competicionid", competicionid);
            q.setParameter("nombreEquipo", nombre);
            res = (Equipo)q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    public List<Equipo> findByCompeticion(Integer competicionid) {
        EntityManager em = getEntityManager();
        List<Equipo> res;
        try {
            Query q = em.createNamedQuery("Equipo.findByCompeticion");
            q.setParameter("competicionid", competicionid);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    public List<Equipo> findByGrupo(Integer grupoid) {
        EntityManager em = getEntityManager();
        List<Equipo> res;
        try {
            Query q = em.createNamedQuery("Equipo.findByGrupo");
            q.setParameter("grupoid", grupoid);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
}
