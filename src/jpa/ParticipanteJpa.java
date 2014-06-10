/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jpa;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import modelo.Prueba;
import modelo.Grupo;
import modelo.Equipo;
import modelo.Participante;
import modelo.Registro;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import jpa.exceptions.NonexistentEntityException;

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
        if (participante.getRegistroCollection() == null) {
            participante.setRegistroCollection(new ArrayList<Registro>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Prueba pruebaasignada = participante.getPruebaasignada();
            if (pruebaasignada != null) {
                pruebaasignada = em.getReference(pruebaasignada.getClass(), pruebaasignada.getId());
                participante.setPruebaasignada(pruebaasignada);
            }
            Grupo grupoId = participante.getGrupoId();
            if (grupoId != null) {
                grupoId = em.getReference(grupoId.getClass(), grupoId.getId());
                participante.setGrupoId(grupoId);
            }
            Equipo equipoId = participante.getEquipoId();
            if (equipoId != null) {
                equipoId = em.getReference(equipoId.getClass(), equipoId.getId());
                participante.setEquipoId(equipoId);
            }
            Collection<Registro> attachedRegistroCollection = new ArrayList<Registro>();
            for (Registro registroCollectionRegistroToAttach : participante.getRegistroCollection()) {
                registroCollectionRegistroToAttach = em.getReference(registroCollectionRegistroToAttach.getClass(), registroCollectionRegistroToAttach.getId());
                attachedRegistroCollection.add(registroCollectionRegistroToAttach);
            }
            participante.setRegistroCollection(attachedRegistroCollection);
            em.persist(participante);
            if (pruebaasignada != null) {
                pruebaasignada.getParticipanteCollection().add(participante);
                pruebaasignada = em.merge(pruebaasignada);
            }
            if (grupoId != null) {
                grupoId.getParticipanteCollection().add(participante);
                grupoId = em.merge(grupoId);
            }
            if (equipoId != null) {
                equipoId.getParticipanteCollection().add(participante);
                equipoId = em.merge(equipoId);
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

    public void edit(Participante participante) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Participante persistentParticipante = em.find(Participante.class, participante.getId());
            Prueba pruebaasignadaOld = persistentParticipante.getPruebaasignada();
            Prueba pruebaasignadaNew = participante.getPruebaasignada();
            Grupo grupoIdOld = persistentParticipante.getGrupoId();
            Grupo grupoIdNew = participante.getGrupoId();
            Equipo equipoIdOld = persistentParticipante.getEquipoId();
            Equipo equipoIdNew = participante.getEquipoId();
            Collection<Registro> registroCollectionOld = persistentParticipante.getRegistroCollection();
            Collection<Registro> registroCollectionNew = participante.getRegistroCollection();
            if (pruebaasignadaNew != null) {
                pruebaasignadaNew = em.getReference(pruebaasignadaNew.getClass(), pruebaasignadaNew.getId());
                participante.setPruebaasignada(pruebaasignadaNew);
            }
            if (grupoIdNew != null) {
                grupoIdNew = em.getReference(grupoIdNew.getClass(), grupoIdNew.getId());
                participante.setGrupoId(grupoIdNew);
            }
            if (equipoIdNew != null) {
                equipoIdNew = em.getReference(equipoIdNew.getClass(), equipoIdNew.getId());
                participante.setEquipoId(equipoIdNew);
            }
            Collection<Registro> attachedRegistroCollectionNew = new ArrayList<Registro>();
            for (Registro registroCollectionNewRegistroToAttach : registroCollectionNew) {
                registroCollectionNewRegistroToAttach = em.getReference(registroCollectionNewRegistroToAttach.getClass(), registroCollectionNewRegistroToAttach.getId());
                attachedRegistroCollectionNew.add(registroCollectionNewRegistroToAttach);
            }
            registroCollectionNew = attachedRegistroCollectionNew;
            participante.setRegistroCollection(registroCollectionNew);
            participante = em.merge(participante);
            if (pruebaasignadaOld != null && !pruebaasignadaOld.equals(pruebaasignadaNew)) {
                pruebaasignadaOld.getParticipanteCollection().remove(participante);
                pruebaasignadaOld = em.merge(pruebaasignadaOld);
            }
            if (pruebaasignadaNew != null && !pruebaasignadaNew.equals(pruebaasignadaOld)) {
                pruebaasignadaNew.getParticipanteCollection().add(participante);
                pruebaasignadaNew = em.merge(pruebaasignadaNew);
            }
            if (grupoIdOld != null && !grupoIdOld.equals(grupoIdNew)) {
                grupoIdOld.getParticipanteCollection().remove(participante);
                grupoIdOld = em.merge(grupoIdOld);
            }
            if (grupoIdNew != null && !grupoIdNew.equals(grupoIdOld)) {
                grupoIdNew.getParticipanteCollection().add(participante);
                grupoIdNew = em.merge(grupoIdNew);
            }
            if (equipoIdOld != null && !equipoIdOld.equals(equipoIdNew)) {
                equipoIdOld.getParticipanteCollection().remove(participante);
                equipoIdOld = em.merge(equipoIdOld);
            }
            if (equipoIdNew != null && !equipoIdNew.equals(equipoIdOld)) {
                equipoIdNew.getParticipanteCollection().add(participante);
                equipoIdNew = em.merge(equipoIdNew);
            }
            for (Registro registroCollectionOldRegistro : registroCollectionOld) {
                if (!registroCollectionNew.contains(registroCollectionOldRegistro)) {
                    registroCollectionOldRegistro.setParticipanteId(null);
                    registroCollectionOldRegistro = em.merge(registroCollectionOldRegistro);
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

    public void destroy(Integer id) throws NonexistentEntityException {
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
            Prueba pruebaasignada = participante.getPruebaasignada();
            if (pruebaasignada != null) {
                pruebaasignada.getParticipanteCollection().remove(participante);
                pruebaasignada = em.merge(pruebaasignada);
            }
            Grupo grupoId = participante.getGrupoId();
            if (grupoId != null) {
                grupoId.getParticipanteCollection().remove(participante);
                grupoId = em.merge(grupoId);
            }
            Equipo equipoId = participante.getEquipoId();
            if (equipoId != null) {
                equipoId.getParticipanteCollection().remove(participante);
                equipoId = em.merge(equipoId);
            }
            Collection<Registro> registroCollection = participante.getRegistroCollection();
            for (Registro registroCollectionRegistro : registroCollection) {
                registroCollectionRegistro.setParticipanteId(null);
                registroCollectionRegistro = em.merge(registroCollectionRegistro);
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
    
    // Creado por mi
    

    
    public List<Participante> findPersonaByGrupo(Integer grupoid) {
        EntityManager em = getEntityManager();
        List<Participante> res;
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
    
    public List<Participante> findByEquipo(Integer equipoid) {
        EntityManager em = getEntityManager();
        List<Participante> res;
        try {
            Query q = em.createNamedQuery("Participante.findByEquipo");
            q.setParameter("equipoid", equipoid);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    public Participante findByDorsalAndCompeticion(Integer dorsal, Integer competicionid) {
        EntityManager em = getEntityManager();
        Participante res;
        try {
            Query q = em.createNamedQuery("Participante.findByDorsalAndCompeticion");
            q.setParameter("competicionid", competicionid);
            q.setParameter("dorsal", dorsal);
            res = (Participante)q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    
}
