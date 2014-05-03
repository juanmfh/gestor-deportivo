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
import modelo.Miembro;
import modelo.Persona;
import modelo.Registro;
import modelo.dao.exceptions.IllegalOrphanException;
import modelo.dao.exceptions.NonexistentEntityException;

/**
 *
 * @author JuanM
 */
public class PersonaJpa implements Serializable {

    public PersonaJpa() {
        this.emf = Persistence.createEntityManagerFactory("JavaAppDesktopPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Persona persona) {
        if (persona.getParticipanteCollection() == null) {
            persona.setParticipanteCollection(new ArrayList<Participante>());
        }
        if (persona.getMiembroCollection() == null) {
            persona.setMiembroCollection(new ArrayList<Miembro>());
        }
        if (persona.getRegistroCollection() == null) {
            persona.setRegistroCollection(new ArrayList<Registro>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Participante> attachedParticipanteCollection = new ArrayList<Participante>();
            for (Participante participanteCollectionParticipanteToAttach : persona.getParticipanteCollection()) {
                participanteCollectionParticipanteToAttach = em.getReference(participanteCollectionParticipanteToAttach.getClass(), participanteCollectionParticipanteToAttach.getId());
                attachedParticipanteCollection.add(participanteCollectionParticipanteToAttach);
            }
            persona.setParticipanteCollection(attachedParticipanteCollection);
            Collection<Miembro> attachedMiembroCollection = new ArrayList<Miembro>();
            for (Miembro miembroCollectionMiembroToAttach : persona.getMiembroCollection()) {
                miembroCollectionMiembroToAttach = em.getReference(miembroCollectionMiembroToAttach.getClass(), miembroCollectionMiembroToAttach.getId());
                attachedMiembroCollection.add(miembroCollectionMiembroToAttach);
            }
            persona.setMiembroCollection(attachedMiembroCollection);
            Collection<Registro> attachedRegistroCollection = new ArrayList<Registro>();
            for (Registro registroCollectionRegistroToAttach : persona.getRegistroCollection()) {
                registroCollectionRegistroToAttach = em.getReference(registroCollectionRegistroToAttach.getClass(), registroCollectionRegistroToAttach.getId());
                attachedRegistroCollection.add(registroCollectionRegistroToAttach);
            }
            persona.setRegistroCollection(attachedRegistroCollection);
            em.persist(persona);
            for (Participante participanteCollectionParticipante : persona.getParticipanteCollection()) {
                Persona oldPersonaIdOfParticipanteCollectionParticipante = participanteCollectionParticipante.getPersonaId();
                participanteCollectionParticipante.setPersonaId(persona);
                participanteCollectionParticipante = em.merge(participanteCollectionParticipante);
                if (oldPersonaIdOfParticipanteCollectionParticipante != null) {
                    oldPersonaIdOfParticipanteCollectionParticipante.getParticipanteCollection().remove(participanteCollectionParticipante);
                    oldPersonaIdOfParticipanteCollectionParticipante = em.merge(oldPersonaIdOfParticipanteCollectionParticipante);
                }
            }
            for (Miembro miembroCollectionMiembro : persona.getMiembroCollection()) {
                Persona oldPersonaIdOfMiembroCollectionMiembro = miembroCollectionMiembro.getPersonaId();
                miembroCollectionMiembro.setPersonaId(persona);
                miembroCollectionMiembro = em.merge(miembroCollectionMiembro);
                if (oldPersonaIdOfMiembroCollectionMiembro != null) {
                    oldPersonaIdOfMiembroCollectionMiembro.getMiembroCollection().remove(miembroCollectionMiembro);
                    oldPersonaIdOfMiembroCollectionMiembro = em.merge(oldPersonaIdOfMiembroCollectionMiembro);
                }
            }
            for (Registro registroCollectionRegistro : persona.getRegistroCollection()) {
                Persona oldPersonaIdOfRegistroCollectionRegistro = registroCollectionRegistro.getPersonaId();
                registroCollectionRegistro.setPersonaId(persona);
                registroCollectionRegistro = em.merge(registroCollectionRegistro);
                if (oldPersonaIdOfRegistroCollectionRegistro != null) {
                    oldPersonaIdOfRegistroCollectionRegistro.getRegistroCollection().remove(registroCollectionRegistro);
                    oldPersonaIdOfRegistroCollectionRegistro = em.merge(oldPersonaIdOfRegistroCollectionRegistro);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Persona persona) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Persona persistentPersona = em.find(Persona.class, persona.getId());
            Collection<Participante> participanteCollectionOld = persistentPersona.getParticipanteCollection();
            Collection<Participante> participanteCollectionNew = persona.getParticipanteCollection();
            Collection<Miembro> miembroCollectionOld = persistentPersona.getMiembroCollection();
            Collection<Miembro> miembroCollectionNew = persona.getMiembroCollection();
            Collection<Registro> registroCollectionOld = persistentPersona.getRegistroCollection();
            Collection<Registro> registroCollectionNew = persona.getRegistroCollection();
            List<String> illegalOrphanMessages = null;
            for (Miembro miembroCollectionOldMiembro : miembroCollectionOld) {
                if (!miembroCollectionNew.contains(miembroCollectionOldMiembro)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Miembro " + miembroCollectionOldMiembro + " since its personaId field is not nullable.");
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
            persona.setParticipanteCollection(participanteCollectionNew);
            Collection<Miembro> attachedMiembroCollectionNew = new ArrayList<Miembro>();
            for (Miembro miembroCollectionNewMiembroToAttach : miembroCollectionNew) {
                miembroCollectionNewMiembroToAttach = em.getReference(miembroCollectionNewMiembroToAttach.getClass(), miembroCollectionNewMiembroToAttach.getId());
                attachedMiembroCollectionNew.add(miembroCollectionNewMiembroToAttach);
            }
            miembroCollectionNew = attachedMiembroCollectionNew;
            persona.setMiembroCollection(miembroCollectionNew);
            Collection<Registro> attachedRegistroCollectionNew = new ArrayList<Registro>();
            for (Registro registroCollectionNewRegistroToAttach : registroCollectionNew) {
                registroCollectionNewRegistroToAttach = em.getReference(registroCollectionNewRegistroToAttach.getClass(), registroCollectionNewRegistroToAttach.getId());
                attachedRegistroCollectionNew.add(registroCollectionNewRegistroToAttach);
            }
            registroCollectionNew = attachedRegistroCollectionNew;
            persona.setRegistroCollection(registroCollectionNew);
            persona = em.merge(persona);
            for (Participante participanteCollectionOldParticipante : participanteCollectionOld) {
                if (!participanteCollectionNew.contains(participanteCollectionOldParticipante)) {
                    participanteCollectionOldParticipante.setPersonaId(null);
                    participanteCollectionOldParticipante = em.merge(participanteCollectionOldParticipante);
                }
            }
            for (Participante participanteCollectionNewParticipante : participanteCollectionNew) {
                if (!participanteCollectionOld.contains(participanteCollectionNewParticipante)) {
                    Persona oldPersonaIdOfParticipanteCollectionNewParticipante = participanteCollectionNewParticipante.getPersonaId();
                    participanteCollectionNewParticipante.setPersonaId(persona);
                    participanteCollectionNewParticipante = em.merge(participanteCollectionNewParticipante);
                    if (oldPersonaIdOfParticipanteCollectionNewParticipante != null && !oldPersonaIdOfParticipanteCollectionNewParticipante.equals(persona)) {
                        oldPersonaIdOfParticipanteCollectionNewParticipante.getParticipanteCollection().remove(participanteCollectionNewParticipante);
                        oldPersonaIdOfParticipanteCollectionNewParticipante = em.merge(oldPersonaIdOfParticipanteCollectionNewParticipante);
                    }
                }
            }
            for (Miembro miembroCollectionNewMiembro : miembroCollectionNew) {
                if (!miembroCollectionOld.contains(miembroCollectionNewMiembro)) {
                    Persona oldPersonaIdOfMiembroCollectionNewMiembro = miembroCollectionNewMiembro.getPersonaId();
                    miembroCollectionNewMiembro.setPersonaId(persona);
                    miembroCollectionNewMiembro = em.merge(miembroCollectionNewMiembro);
                    if (oldPersonaIdOfMiembroCollectionNewMiembro != null && !oldPersonaIdOfMiembroCollectionNewMiembro.equals(persona)) {
                        oldPersonaIdOfMiembroCollectionNewMiembro.getMiembroCollection().remove(miembroCollectionNewMiembro);
                        oldPersonaIdOfMiembroCollectionNewMiembro = em.merge(oldPersonaIdOfMiembroCollectionNewMiembro);
                    }
                }
            }
            for (Registro registroCollectionOldRegistro : registroCollectionOld) {
                if (!registroCollectionNew.contains(registroCollectionOldRegistro)) {
                    registroCollectionOldRegistro.setPersonaId(null);
                    registroCollectionOldRegistro = em.merge(registroCollectionOldRegistro);
                }
            }
            for (Registro registroCollectionNewRegistro : registroCollectionNew) {
                if (!registroCollectionOld.contains(registroCollectionNewRegistro)) {
                    Persona oldPersonaIdOfRegistroCollectionNewRegistro = registroCollectionNewRegistro.getPersonaId();
                    registroCollectionNewRegistro.setPersonaId(persona);
                    registroCollectionNewRegistro = em.merge(registroCollectionNewRegistro);
                    if (oldPersonaIdOfRegistroCollectionNewRegistro != null && !oldPersonaIdOfRegistroCollectionNewRegistro.equals(persona)) {
                        oldPersonaIdOfRegistroCollectionNewRegistro.getRegistroCollection().remove(registroCollectionNewRegistro);
                        oldPersonaIdOfRegistroCollectionNewRegistro = em.merge(oldPersonaIdOfRegistroCollectionNewRegistro);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = persona.getId();
                if (findPersona(id) == null) {
                    throw new NonexistentEntityException("The persona with id " + id + " no longer exists.");
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
            Persona persona;
            try {
                persona = em.getReference(Persona.class, id);
                persona.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The persona with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Miembro> miembroCollectionOrphanCheck = persona.getMiembroCollection();
            for (Miembro miembroCollectionOrphanCheckMiembro : miembroCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Persona (" + persona + ") cannot be destroyed since the Miembro " + miembroCollectionOrphanCheckMiembro + " in its miembroCollection field has a non-nullable personaId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Participante> participanteCollection = persona.getParticipanteCollection();
            for (Participante participanteCollectionParticipante : participanteCollection) {
                participanteCollectionParticipante.setPersonaId(null);
                participanteCollectionParticipante = em.merge(participanteCollectionParticipante);
            }
            Collection<Registro> registroCollection = persona.getRegistroCollection();
            for (Registro registroCollectionRegistro : registroCollection) {
                registroCollectionRegistro.setPersonaId(null);
                registroCollectionRegistro = em.merge(registroCollectionRegistro);
            }
            em.remove(persona);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Persona> findPersonaEntities() {
        return findPersonaEntities(true, -1, -1);
    }

    public List<Persona> findPersonaEntities(int maxResults, int firstResult) {
        return findPersonaEntities(false, maxResults, firstResult);
    }

    private List<Persona> findPersonaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Persona.class));
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

    public Persona findPersona(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Persona.class, id);
        } finally {
            em.close();
        }
    }

    public int getPersonaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Persona> rt = cq.from(Persona.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    public Persona findByDorsalAndCompeticion(Integer dorsal, Integer competicionid) {
        EntityManager em = getEntityManager();
        Persona res;
        try {
            Query q = em.createNamedQuery("Persona.findByDorsalAndCompeticion");
            q.setParameter("competicionid", competicionid);
            q.setParameter("dorsal", dorsal);
            res = (Persona)q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
}
