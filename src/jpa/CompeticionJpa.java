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
import modelo.Compuesta;
import java.util.ArrayList;
import java.util.Collection;
import modelo.Inscripcion;
import modelo.Administrado;
import modelo.Competicion;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import jpa.exceptions.IllegalOrphanException;
import jpa.exceptions.NonexistentEntityException;

/**
 *
 * @author JuanM
 */
public class CompeticionJpa implements Serializable {

    public CompeticionJpa() {
        this.emf = Persistence.createEntityManagerFactory("JavaAppDesktopPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Competicion competicion) {
        if (competicion.getCompuestaCollection() == null) {
            competicion.setCompuestaCollection(new ArrayList<Compuesta>());
        }
        if (competicion.getInscripcionCollection() == null) {
            competicion.setInscripcionCollection(new ArrayList<Inscripcion>());
        }
        if (competicion.getAdministradoCollection() == null) {
            competicion.setAdministradoCollection(new ArrayList<Administrado>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Compuesta> attachedCompuestaCollection = new ArrayList<Compuesta>();
            for (Compuesta compuestaCollectionCompuestaToAttach : competicion.getCompuestaCollection()) {
                compuestaCollectionCompuestaToAttach = em.getReference(compuestaCollectionCompuestaToAttach.getClass(), compuestaCollectionCompuestaToAttach.getId());
                attachedCompuestaCollection.add(compuestaCollectionCompuestaToAttach);
            }
            competicion.setCompuestaCollection(attachedCompuestaCollection);
            Collection<Inscripcion> attachedInscripcionCollection = new ArrayList<Inscripcion>();
            for (Inscripcion inscripcionCollectionInscripcionToAttach : competicion.getInscripcionCollection()) {
                inscripcionCollectionInscripcionToAttach = em.getReference(inscripcionCollectionInscripcionToAttach.getClass(), inscripcionCollectionInscripcionToAttach.getId());
                attachedInscripcionCollection.add(inscripcionCollectionInscripcionToAttach);
            }
            competicion.setInscripcionCollection(attachedInscripcionCollection);
            Collection<Administrado> attachedAdministradoCollection = new ArrayList<Administrado>();
            for (Administrado administradoCollectionAdministradoToAttach : competicion.getAdministradoCollection()) {
                administradoCollectionAdministradoToAttach = em.getReference(administradoCollectionAdministradoToAttach.getClass(), administradoCollectionAdministradoToAttach.getId());
                attachedAdministradoCollection.add(administradoCollectionAdministradoToAttach);
            }
            competicion.setAdministradoCollection(attachedAdministradoCollection);
            em.persist(competicion);
            for (Compuesta compuestaCollectionCompuesta : competicion.getCompuestaCollection()) {
                Competicion oldCompeticionIdOfCompuestaCollectionCompuesta = compuestaCollectionCompuesta.getCompeticionId();
                compuestaCollectionCompuesta.setCompeticionId(competicion);
                compuestaCollectionCompuesta = em.merge(compuestaCollectionCompuesta);
                if (oldCompeticionIdOfCompuestaCollectionCompuesta != null) {
                    oldCompeticionIdOfCompuestaCollectionCompuesta.getCompuestaCollection().remove(compuestaCollectionCompuesta);
                    oldCompeticionIdOfCompuestaCollectionCompuesta = em.merge(oldCompeticionIdOfCompuestaCollectionCompuesta);
                }
            }
            for (Inscripcion inscripcionCollectionInscripcion : competicion.getInscripcionCollection()) {
                Competicion oldCompeticionIdOfInscripcionCollectionInscripcion = inscripcionCollectionInscripcion.getCompeticionId();
                inscripcionCollectionInscripcion.setCompeticionId(competicion);
                inscripcionCollectionInscripcion = em.merge(inscripcionCollectionInscripcion);
                if (oldCompeticionIdOfInscripcionCollectionInscripcion != null) {
                    oldCompeticionIdOfInscripcionCollectionInscripcion.getInscripcionCollection().remove(inscripcionCollectionInscripcion);
                    oldCompeticionIdOfInscripcionCollectionInscripcion = em.merge(oldCompeticionIdOfInscripcionCollectionInscripcion);
                }
            }
            for (Administrado administradoCollectionAdministrado : competicion.getAdministradoCollection()) {
                Competicion oldCompeticionIdOfAdministradoCollectionAdministrado = administradoCollectionAdministrado.getCompeticionId();
                administradoCollectionAdministrado.setCompeticionId(competicion);
                administradoCollectionAdministrado = em.merge(administradoCollectionAdministrado);
                if (oldCompeticionIdOfAdministradoCollectionAdministrado != null) {
                    oldCompeticionIdOfAdministradoCollectionAdministrado.getAdministradoCollection().remove(administradoCollectionAdministrado);
                    oldCompeticionIdOfAdministradoCollectionAdministrado = em.merge(oldCompeticionIdOfAdministradoCollectionAdministrado);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Competicion competicion) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Competicion persistentCompeticion = em.find(Competicion.class, competicion.getId());
            Collection<Compuesta> compuestaCollectionOld = persistentCompeticion.getCompuestaCollection();
            Collection<Compuesta> compuestaCollectionNew = competicion.getCompuestaCollection();
            Collection<Inscripcion> inscripcionCollectionOld = persistentCompeticion.getInscripcionCollection();
            Collection<Inscripcion> inscripcionCollectionNew = competicion.getInscripcionCollection();
            Collection<Administrado> administradoCollectionOld = persistentCompeticion.getAdministradoCollection();
            Collection<Administrado> administradoCollectionNew = competicion.getAdministradoCollection();
            List<String> illegalOrphanMessages = null;
            for (Compuesta compuestaCollectionOldCompuesta : compuestaCollectionOld) {
                if (!compuestaCollectionNew.contains(compuestaCollectionOldCompuesta)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Compuesta " + compuestaCollectionOldCompuesta + " since its competicionId field is not nullable.");
                }
            }
            for (Inscripcion inscripcionCollectionOldInscripcion : inscripcionCollectionOld) {
                if (!inscripcionCollectionNew.contains(inscripcionCollectionOldInscripcion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Inscripcion " + inscripcionCollectionOldInscripcion + " since its competicionId field is not nullable.");
                }
            }
            for (Administrado administradoCollectionOldAdministrado : administradoCollectionOld) {
                if (!administradoCollectionNew.contains(administradoCollectionOldAdministrado)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Administrado " + administradoCollectionOldAdministrado + " since its competicionId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Compuesta> attachedCompuestaCollectionNew = new ArrayList<Compuesta>();
            for (Compuesta compuestaCollectionNewCompuestaToAttach : compuestaCollectionNew) {
                compuestaCollectionNewCompuestaToAttach = em.getReference(compuestaCollectionNewCompuestaToAttach.getClass(), compuestaCollectionNewCompuestaToAttach.getId());
                attachedCompuestaCollectionNew.add(compuestaCollectionNewCompuestaToAttach);
            }
            compuestaCollectionNew = attachedCompuestaCollectionNew;
            competicion.setCompuestaCollection(compuestaCollectionNew);
            Collection<Inscripcion> attachedInscripcionCollectionNew = new ArrayList<Inscripcion>();
            for (Inscripcion inscripcionCollectionNewInscripcionToAttach : inscripcionCollectionNew) {
                inscripcionCollectionNewInscripcionToAttach = em.getReference(inscripcionCollectionNewInscripcionToAttach.getClass(), inscripcionCollectionNewInscripcionToAttach.getId());
                attachedInscripcionCollectionNew.add(inscripcionCollectionNewInscripcionToAttach);
            }
            inscripcionCollectionNew = attachedInscripcionCollectionNew;
            competicion.setInscripcionCollection(inscripcionCollectionNew);
            Collection<Administrado> attachedAdministradoCollectionNew = new ArrayList<Administrado>();
            for (Administrado administradoCollectionNewAdministradoToAttach : administradoCollectionNew) {
                administradoCollectionNewAdministradoToAttach = em.getReference(administradoCollectionNewAdministradoToAttach.getClass(), administradoCollectionNewAdministradoToAttach.getId());
                attachedAdministradoCollectionNew.add(administradoCollectionNewAdministradoToAttach);
            }
            administradoCollectionNew = attachedAdministradoCollectionNew;
            competicion.setAdministradoCollection(administradoCollectionNew);
            competicion = em.merge(competicion);
            for (Compuesta compuestaCollectionNewCompuesta : compuestaCollectionNew) {
                if (!compuestaCollectionOld.contains(compuestaCollectionNewCompuesta)) {
                    Competicion oldCompeticionIdOfCompuestaCollectionNewCompuesta = compuestaCollectionNewCompuesta.getCompeticionId();
                    compuestaCollectionNewCompuesta.setCompeticionId(competicion);
                    compuestaCollectionNewCompuesta = em.merge(compuestaCollectionNewCompuesta);
                    if (oldCompeticionIdOfCompuestaCollectionNewCompuesta != null && !oldCompeticionIdOfCompuestaCollectionNewCompuesta.equals(competicion)) {
                        oldCompeticionIdOfCompuestaCollectionNewCompuesta.getCompuestaCollection().remove(compuestaCollectionNewCompuesta);
                        oldCompeticionIdOfCompuestaCollectionNewCompuesta = em.merge(oldCompeticionIdOfCompuestaCollectionNewCompuesta);
                    }
                }
            }
            for (Inscripcion inscripcionCollectionNewInscripcion : inscripcionCollectionNew) {
                if (!inscripcionCollectionOld.contains(inscripcionCollectionNewInscripcion)) {
                    Competicion oldCompeticionIdOfInscripcionCollectionNewInscripcion = inscripcionCollectionNewInscripcion.getCompeticionId();
                    inscripcionCollectionNewInscripcion.setCompeticionId(competicion);
                    inscripcionCollectionNewInscripcion = em.merge(inscripcionCollectionNewInscripcion);
                    if (oldCompeticionIdOfInscripcionCollectionNewInscripcion != null && !oldCompeticionIdOfInscripcionCollectionNewInscripcion.equals(competicion)) {
                        oldCompeticionIdOfInscripcionCollectionNewInscripcion.getInscripcionCollection().remove(inscripcionCollectionNewInscripcion);
                        oldCompeticionIdOfInscripcionCollectionNewInscripcion = em.merge(oldCompeticionIdOfInscripcionCollectionNewInscripcion);
                    }
                }
            }
            for (Administrado administradoCollectionNewAdministrado : administradoCollectionNew) {
                if (!administradoCollectionOld.contains(administradoCollectionNewAdministrado)) {
                    Competicion oldCompeticionIdOfAdministradoCollectionNewAdministrado = administradoCollectionNewAdministrado.getCompeticionId();
                    administradoCollectionNewAdministrado.setCompeticionId(competicion);
                    administradoCollectionNewAdministrado = em.merge(administradoCollectionNewAdministrado);
                    if (oldCompeticionIdOfAdministradoCollectionNewAdministrado != null && !oldCompeticionIdOfAdministradoCollectionNewAdministrado.equals(competicion)) {
                        oldCompeticionIdOfAdministradoCollectionNewAdministrado.getAdministradoCollection().remove(administradoCollectionNewAdministrado);
                        oldCompeticionIdOfAdministradoCollectionNewAdministrado = em.merge(oldCompeticionIdOfAdministradoCollectionNewAdministrado);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = competicion.getId();
                if (findCompeticion(id) == null) {
                    throw new NonexistentEntityException("The competicion with id " + id + " no longer exists.");
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
            Competicion competicion;
            try {
                competicion = em.getReference(Competicion.class, id);
                competicion.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The competicion with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Compuesta> compuestaCollectionOrphanCheck = competicion.getCompuestaCollection();
            for (Compuesta compuestaCollectionOrphanCheckCompuesta : compuestaCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Competicion (" + competicion + ") cannot be destroyed since the Compuesta " + compuestaCollectionOrphanCheckCompuesta + " in its compuestaCollection field has a non-nullable competicionId field.");
            }
            Collection<Inscripcion> inscripcionCollectionOrphanCheck = competicion.getInscripcionCollection();
            for (Inscripcion inscripcionCollectionOrphanCheckInscripcion : inscripcionCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Competicion (" + competicion + ") cannot be destroyed since the Inscripcion " + inscripcionCollectionOrphanCheckInscripcion + " in its inscripcionCollection field has a non-nullable competicionId field.");
            }
            Collection<Administrado> administradoCollectionOrphanCheck = competicion.getAdministradoCollection();
            for (Administrado administradoCollectionOrphanCheckAdministrado : administradoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Competicion (" + competicion + ") cannot be destroyed since the Administrado " + administradoCollectionOrphanCheckAdministrado + " in its administradoCollection field has a non-nullable competicionId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(competicion);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Competicion> findCompeticionEntities() {
        return findCompeticionEntities(true, -1, -1);
    }

    public List<Competicion> findCompeticionEntities(int maxResults, int firstResult) {
        return findCompeticionEntities(false, maxResults, firstResult);
    }

    private List<Competicion> findCompeticionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Competicion.class));
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

    public Competicion findCompeticion(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Competicion.class, id);
        } finally {
            em.close();
        }
    }

    public int getCompeticionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Competicion> rt = cq.from(Competicion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    // Creados por mi
    
    public List<String> findAllCompeticionNames() {
        EntityManager em = getEntityManager();
        List<String> res;
        try {
            Query q = em.createNamedQuery("Competicion.findAllNames");
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return res;
    }

    public Competicion findCompeticionByName(String nombre) {
        EntityManager em = getEntityManager();
        Competicion res;
        try {
            Query q = em.createNamedQuery("Competicion.findByNombre");
            q.setParameter("nombre", nombre);
            res = (Competicion) q.getSingleResult();

        } catch (NoResultException e) {
            return null;
        } finally{
            em.close();
        }
        return res;
    }
    
}
