package modelo.dao;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import modelo.Persona;
import modelo.Equipo;
import modelo.Participa;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import modelo.Participante;
import modelo.Registro;
import modelo.dao.exceptions.IllegalOrphanException;
import modelo.dao.exceptions.NonexistentEntityException;

/**
 *
 * @author JuanM
 */
public class ParticipanteJpa implements Serializable {

    public ParticipanteJpa() {
        this.emf = Persistence.createEntityManagerFactory("JavaAppDesktopPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Participante participante) {
        if (participante.getParticipaCollection() == null) {
            participante.setParticipaCollection(new ArrayList<Participa>());
        }
        if (participante.getRegistroCollection() == null) {
            participante.setRegistroCollection(new ArrayList<Registro>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Persona personaId = participante.getPersonaId();
            if (personaId != null) {
                personaId = em.getReference(personaId.getClass(), personaId.getId());
                participante.setPersonaId(personaId);
            }
            Equipo equipoId = participante.getEquipoId();
            if (equipoId != null) {
                equipoId = em.getReference(equipoId.getClass(), equipoId.getId());
                participante.setEquipoId(equipoId);
            }
            Collection<Participa> attachedParticipaCollection = new ArrayList<Participa>();
            for (Participa participaCollectionParticipaToAttach : participante.getParticipaCollection()) {
                participaCollectionParticipaToAttach = em.getReference(participaCollectionParticipaToAttach.getClass(), participaCollectionParticipaToAttach.getId());
                attachedParticipaCollection.add(participaCollectionParticipaToAttach);
            }
            participante.setParticipaCollection(attachedParticipaCollection);
            Collection<Registro> attachedRegistroCollection = new ArrayList<Registro>();
            for (Registro registroCollectionRegistroToAttach : participante.getRegistroCollection()) {
                registroCollectionRegistroToAttach = em.getReference(registroCollectionRegistroToAttach.getClass(), registroCollectionRegistroToAttach.getId());
                attachedRegistroCollection.add(registroCollectionRegistroToAttach);
            }
            participante.setRegistroCollection(attachedRegistroCollection);
            em.persist(participante);
            if (personaId != null) {
                personaId.getParticipanteCollection().add(participante);
                personaId = em.merge(personaId);
            }
            if (equipoId != null) {
                equipoId.getParticipanteCollection().add(participante);
                equipoId = em.merge(equipoId);
            }
            for (Participa participaCollectionParticipa : participante.getParticipaCollection()) {
                Participante oldParticipanteIdOfParticipaCollectionParticipa = participaCollectionParticipa.getParticipanteId();
                participaCollectionParticipa.setParticipanteId(participante);
                participaCollectionParticipa = em.merge(participaCollectionParticipa);
                if (oldParticipanteIdOfParticipaCollectionParticipa != null) {
                    oldParticipanteIdOfParticipaCollectionParticipa.getParticipaCollection().remove(participaCollectionParticipa);
                    oldParticipanteIdOfParticipaCollectionParticipa = em.merge(oldParticipanteIdOfParticipaCollectionParticipa);
                }
            }
            for (Registro registroCollectionRegistro : participante.getRegistroCollection()) {
                Participante oldParticipanteIdOfRegistroCollectionRegistro = registroCollectionRegistro.getParticipanteId();
                registroCollectionRegistro.setParticipanteId(participante);
                registroCollectionRegistro = em.merge(registroCollectionRegistro);
                if (oldParticipanteIdOfRegistroCollectionRegistro != null) {
                    oldParticipanteIdOfRegistroCollectionRegistro.getRegistroCollection().remove(registroCollectionRegistro);
                    oldParticipanteIdOfRegistroCollectionRegistro = em.merge(oldParticipanteIdOfRegistroCollectionRegistro);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Participante participante) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Participante persistentParticipante = em.find(Participante.class, participante.getId());
            Persona personaIdOld = persistentParticipante.getPersonaId();
            Persona personaIdNew = participante.getPersonaId();
            Equipo equipoIdOld = persistentParticipante.getEquipoId();
            Equipo equipoIdNew = participante.getEquipoId();
            Collection<Participa> participaCollectionOld = persistentParticipante.getParticipaCollection();
            Collection<Participa> participaCollectionNew = participante.getParticipaCollection();
            Collection<Registro> registroCollectionOld = persistentParticipante.getRegistroCollection();
            Collection<Registro> registroCollectionNew = participante.getRegistroCollection();
            List<String> illegalOrphanMessages = null;
            for (Participa participaCollectionOldParticipa : participaCollectionOld) {
                if (!participaCollectionNew.contains(participaCollectionOldParticipa)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Participa " + participaCollectionOldParticipa + " since its participanteId field is not nullable.");
                }
            }
            for (Registro registroCollectionOldRegistro : registroCollectionOld) {
                if (!registroCollectionNew.contains(registroCollectionOldRegistro)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Registro " + registroCollectionOldRegistro + " since its participanteId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (personaIdNew != null) {
                personaIdNew = em.getReference(personaIdNew.getClass(), personaIdNew.getId());
                participante.setPersonaId(personaIdNew);
            }
            if (equipoIdNew != null) {
                equipoIdNew = em.getReference(equipoIdNew.getClass(), equipoIdNew.getId());
                participante.setEquipoId(equipoIdNew);
            }
            Collection<Participa> attachedParticipaCollectionNew = new ArrayList<Participa>();
            for (Participa participaCollectionNewParticipaToAttach : participaCollectionNew) {
                participaCollectionNewParticipaToAttach = em.getReference(participaCollectionNewParticipaToAttach.getClass(), participaCollectionNewParticipaToAttach.getId());
                attachedParticipaCollectionNew.add(participaCollectionNewParticipaToAttach);
            }
            participaCollectionNew = attachedParticipaCollectionNew;
            participante.setParticipaCollection(participaCollectionNew);
            Collection<Registro> attachedRegistroCollectionNew = new ArrayList<Registro>();
            for (Registro registroCollectionNewRegistroToAttach : registroCollectionNew) {
                registroCollectionNewRegistroToAttach = em.getReference(registroCollectionNewRegistroToAttach.getClass(), registroCollectionNewRegistroToAttach.getId());
                attachedRegistroCollectionNew.add(registroCollectionNewRegistroToAttach);
            }
            registroCollectionNew = attachedRegistroCollectionNew;
            participante.setRegistroCollection(registroCollectionNew);
            participante = em.merge(participante);
            if (personaIdOld != null && !personaIdOld.equals(personaIdNew)) {
                personaIdOld.getParticipanteCollection().remove(participante);
                personaIdOld = em.merge(personaIdOld);
            }
            if (personaIdNew != null && !personaIdNew.equals(personaIdOld)) {
                personaIdNew.getParticipanteCollection().add(participante);
                personaIdNew = em.merge(personaIdNew);
            }
            if (equipoIdOld != null && !equipoIdOld.equals(equipoIdNew)) {
                equipoIdOld.getParticipanteCollection().remove(participante);
                equipoIdOld = em.merge(equipoIdOld);
            }
            if (equipoIdNew != null && !equipoIdNew.equals(equipoIdOld)) {
                equipoIdNew.getParticipanteCollection().add(participante);
                equipoIdNew = em.merge(equipoIdNew);
            }
            for (Participa participaCollectionNewParticipa : participaCollectionNew) {
                if (!participaCollectionOld.contains(participaCollectionNewParticipa)) {
                    Participante oldParticipanteIdOfParticipaCollectionNewParticipa = participaCollectionNewParticipa.getParticipanteId();
                    participaCollectionNewParticipa.setParticipanteId(participante);
                    participaCollectionNewParticipa = em.merge(participaCollectionNewParticipa);
                    if (oldParticipanteIdOfParticipaCollectionNewParticipa != null && !oldParticipanteIdOfParticipaCollectionNewParticipa.equals(participante)) {
                        oldParticipanteIdOfParticipaCollectionNewParticipa.getParticipaCollection().remove(participaCollectionNewParticipa);
                        oldParticipanteIdOfParticipaCollectionNewParticipa = em.merge(oldParticipanteIdOfParticipaCollectionNewParticipa);
                    }
                }
            }
            for (Registro registroCollectionNewRegistro : registroCollectionNew) {
                if (!registroCollectionOld.contains(registroCollectionNewRegistro)) {
                    Participante oldParticipanteIdOfRegistroCollectionNewRegistro = registroCollectionNewRegistro.getParticipanteId();
                    registroCollectionNewRegistro.setParticipanteId(participante);
                    registroCollectionNewRegistro = em.merge(registroCollectionNewRegistro);
                    if (oldParticipanteIdOfRegistroCollectionNewRegistro != null && !oldParticipanteIdOfRegistroCollectionNewRegistro.equals(participante)) {
                        oldParticipanteIdOfRegistroCollectionNewRegistro.getRegistroCollection().remove(registroCollectionNewRegistro);
                        oldParticipanteIdOfRegistroCollectionNewRegistro = em.merge(oldParticipanteIdOfRegistroCollectionNewRegistro);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = participante.getId();
                if (findParticipante(id) == null) {
                    throw new NonexistentEntityException("The participante with id " + id + " no longer exists.");
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
            Participante participante;
            try {
                participante = em.getReference(Participante.class, id);
                participante.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The participante with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Participa> participaCollectionOrphanCheck = participante.getParticipaCollection();
            for (Participa participaCollectionOrphanCheckParticipa : participaCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Participante (" + participante + ") cannot be destroyed since the Participa " + participaCollectionOrphanCheckParticipa + " in its participaCollection field has a non-nullable participanteId field.");
            }
            Collection<Registro> registroCollectionOrphanCheck = participante.getRegistroCollection();
            for (Registro registroCollectionOrphanCheckRegistro : registroCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Participante (" + participante + ") cannot be destroyed since the Registro " + registroCollectionOrphanCheckRegistro + " in its registroCollection field has a non-nullable participanteId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Persona personaId = participante.getPersonaId();
            if (personaId != null) {
                personaId.getParticipanteCollection().remove(participante);
                personaId = em.merge(personaId);
            }
            Equipo equipoId = participante.getEquipoId();
            if (equipoId != null) {
                equipoId.getParticipanteCollection().remove(participante);
                equipoId = em.merge(equipoId);
            }
            em.remove(participante);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Participante> findParticipanteEntities() {
        return findParticipanteEntities(true, -1, -1);
    }

    public List<Participante> findParticipanteEntities(int maxResults, int firstResult) {
        return findParticipanteEntities(false, maxResults, firstResult);
    }

    private List<Participante> findParticipanteEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Participante.class));
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

    public Participante findParticipante(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Participante.class, id);
        } finally {
            em.close();
        }
    }

    public int getParticipanteCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Participante> rt = cq.from(Participante.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    public List<Persona> findPersonaByGrupo(Integer grupoid) {
        EntityManager em = getEntityManager();
        List<Persona> res;
        try {
            Query q = em.createNamedQuery("Participante.findPersonaByGrupo");
            q.setParameter("grupoid", grupoid);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    
    
    public Participante findByPersonaId(Integer id) {
        EntityManager em = getEntityManager();
        Participante res;
        try {
            Query q = em.createNamedQuery("Participante.findByPersonaId");
            q.setParameter("id", id);
            res = (Participante)q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    public Participante findByEquipoId(Integer id) {
        EntityManager em = getEntityManager();
        Participante res;
        try {
            Query q = em.createNamedQuery("Participante.findByEquipoId");
            q.setParameter("id", id);
            res = (Participante)q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
   
}
