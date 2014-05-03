package modelo.dao;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import modelo.Compuesta;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import modelo.Competicion;
import modelo.Registro;
import modelo.Compite;
import modelo.Prueba;
import modelo.dao.exceptions.IllegalOrphanException;
import modelo.dao.exceptions.NonexistentEntityException;

/**
 *
 * @author JuanM
 */
public class PruebaJpa implements Serializable {

    public PruebaJpa() {
        this.emf = Persistence.createEntityManagerFactory("JavaAppDesktopPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Prueba prueba) {
        if (prueba.getCompuestaCollection() == null) {
            prueba.setCompuestaCollection(new ArrayList<Compuesta>());
        }
        if (prueba.getRegistroCollection() == null) {
            prueba.setRegistroCollection(new ArrayList<Registro>());
        }
        if (prueba.getCompiteCollection() == null) {
            prueba.setCompiteCollection(new ArrayList<Compite>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Compuesta> attachedCompuestaCollection = new ArrayList<Compuesta>();
            for (Compuesta compuestaCollectionCompuestaToAttach : prueba.getCompuestaCollection()) {
                compuestaCollectionCompuestaToAttach = em.getReference(compuestaCollectionCompuestaToAttach.getClass(), compuestaCollectionCompuestaToAttach.getId());
                attachedCompuestaCollection.add(compuestaCollectionCompuestaToAttach);
            }
            prueba.setCompuestaCollection(attachedCompuestaCollection);
            Collection<Registro> attachedRegistroCollection = new ArrayList<Registro>();
            for (Registro registroCollectionRegistroToAttach : prueba.getRegistroCollection()) {
                registroCollectionRegistroToAttach = em.getReference(registroCollectionRegistroToAttach.getClass(), registroCollectionRegistroToAttach.getId());
                attachedRegistroCollection.add(registroCollectionRegistroToAttach);
            }
            prueba.setRegistroCollection(attachedRegistroCollection);
            Collection<Compite> attachedCompiteCollection = new ArrayList<Compite>();
            for (Compite compiteCollectionCompiteToAttach : prueba.getCompiteCollection()) {
                compiteCollectionCompiteToAttach = em.getReference(compiteCollectionCompiteToAttach.getClass(), compiteCollectionCompiteToAttach.getId());
                attachedCompiteCollection.add(compiteCollectionCompiteToAttach);
            }
            prueba.setCompiteCollection(attachedCompiteCollection);
            em.persist(prueba);
            for (Compuesta compuestaCollectionCompuesta : prueba.getCompuestaCollection()) {
                Prueba oldPruebaIdOfCompuestaCollectionCompuesta = compuestaCollectionCompuesta.getPruebaId();
                compuestaCollectionCompuesta.setPruebaId(prueba);
                compuestaCollectionCompuesta = em.merge(compuestaCollectionCompuesta);
                if (oldPruebaIdOfCompuestaCollectionCompuesta != null) {
                    oldPruebaIdOfCompuestaCollectionCompuesta.getCompuestaCollection().remove(compuestaCollectionCompuesta);
                    oldPruebaIdOfCompuestaCollectionCompuesta = em.merge(oldPruebaIdOfCompuestaCollectionCompuesta);
                }
            }
            for (Registro registroCollectionRegistro : prueba.getRegistroCollection()) {
                Prueba oldPruebaIdOfRegistroCollectionRegistro = registroCollectionRegistro.getPruebaId();
                registroCollectionRegistro.setPruebaId(prueba);
                registroCollectionRegistro = em.merge(registroCollectionRegistro);
                if (oldPruebaIdOfRegistroCollectionRegistro != null) {
                    oldPruebaIdOfRegistroCollectionRegistro.getRegistroCollection().remove(registroCollectionRegistro);
                    oldPruebaIdOfRegistroCollectionRegistro = em.merge(oldPruebaIdOfRegistroCollectionRegistro);
                }
            }
            for (Compite compiteCollectionCompite : prueba.getCompiteCollection()) {
                Prueba oldPruebaIdOfCompiteCollectionCompite = compiteCollectionCompite.getPruebaId();
                compiteCollectionCompite.setPruebaId(prueba);
                compiteCollectionCompite = em.merge(compiteCollectionCompite);
                if (oldPruebaIdOfCompiteCollectionCompite != null) {
                    oldPruebaIdOfCompiteCollectionCompite.getCompiteCollection().remove(compiteCollectionCompite);
                    oldPruebaIdOfCompiteCollectionCompite = em.merge(oldPruebaIdOfCompiteCollectionCompite);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Prueba prueba) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Prueba persistentPrueba = em.find(Prueba.class, prueba.getId());
            Collection<Compuesta> compuestaCollectionOld = persistentPrueba.getCompuestaCollection();
            Collection<Compuesta> compuestaCollectionNew = prueba.getCompuestaCollection();
            Collection<Registro> registroCollectionOld = persistentPrueba.getRegistroCollection();
            Collection<Registro> registroCollectionNew = prueba.getRegistroCollection();
            Collection<Compite> compiteCollectionOld = persistentPrueba.getCompiteCollection();
            Collection<Compite> compiteCollectionNew = prueba.getCompiteCollection();
            List<String> illegalOrphanMessages = null;
            for (Compuesta compuestaCollectionOldCompuesta : compuestaCollectionOld) {
                if (!compuestaCollectionNew.contains(compuestaCollectionOldCompuesta)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Compuesta " + compuestaCollectionOldCompuesta + " since its pruebaId field is not nullable.");
                }
            }
            for (Registro registroCollectionOldRegistro : registroCollectionOld) {
                if (!registroCollectionNew.contains(registroCollectionOldRegistro)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Registro " + registroCollectionOldRegistro + " since its pruebaId field is not nullable.");
                }
            }
            for (Compite compiteCollectionOldCompite : compiteCollectionOld) {
                if (!compiteCollectionNew.contains(compiteCollectionOldCompite)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Compite " + compiteCollectionOldCompite + " since its pruebaId field is not nullable.");
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
            prueba.setCompuestaCollection(compuestaCollectionNew);
            Collection<Registro> attachedRegistroCollectionNew = new ArrayList<Registro>();
            for (Registro registroCollectionNewRegistroToAttach : registroCollectionNew) {
                registroCollectionNewRegistroToAttach = em.getReference(registroCollectionNewRegistroToAttach.getClass(), registroCollectionNewRegistroToAttach.getId());
                attachedRegistroCollectionNew.add(registroCollectionNewRegistroToAttach);
            }
            registroCollectionNew = attachedRegistroCollectionNew;
            prueba.setRegistroCollection(registroCollectionNew);
            Collection<Compite> attachedCompiteCollectionNew = new ArrayList<Compite>();
            for (Compite compiteCollectionNewCompiteToAttach : compiteCollectionNew) {
                compiteCollectionNewCompiteToAttach = em.getReference(compiteCollectionNewCompiteToAttach.getClass(), compiteCollectionNewCompiteToAttach.getId());
                attachedCompiteCollectionNew.add(compiteCollectionNewCompiteToAttach);
            }
            compiteCollectionNew = attachedCompiteCollectionNew;
            prueba.setCompiteCollection(compiteCollectionNew);
            prueba = em.merge(prueba);
            for (Compuesta compuestaCollectionNewCompuesta : compuestaCollectionNew) {
                if (!compuestaCollectionOld.contains(compuestaCollectionNewCompuesta)) {
                    Prueba oldPruebaIdOfCompuestaCollectionNewCompuesta = compuestaCollectionNewCompuesta.getPruebaId();
                    compuestaCollectionNewCompuesta.setPruebaId(prueba);
                    compuestaCollectionNewCompuesta = em.merge(compuestaCollectionNewCompuesta);
                    if (oldPruebaIdOfCompuestaCollectionNewCompuesta != null && !oldPruebaIdOfCompuestaCollectionNewCompuesta.equals(prueba)) {
                        oldPruebaIdOfCompuestaCollectionNewCompuesta.getCompuestaCollection().remove(compuestaCollectionNewCompuesta);
                        oldPruebaIdOfCompuestaCollectionNewCompuesta = em.merge(oldPruebaIdOfCompuestaCollectionNewCompuesta);
                    }
                }
            }
            for (Registro registroCollectionNewRegistro : registroCollectionNew) {
                if (!registroCollectionOld.contains(registroCollectionNewRegistro)) {
                    Prueba oldPruebaIdOfRegistroCollectionNewRegistro = registroCollectionNewRegistro.getPruebaId();
                    registroCollectionNewRegistro.setPruebaId(prueba);
                    registroCollectionNewRegistro = em.merge(registroCollectionNewRegistro);
                    if (oldPruebaIdOfRegistroCollectionNewRegistro != null && !oldPruebaIdOfRegistroCollectionNewRegistro.equals(prueba)) {
                        oldPruebaIdOfRegistroCollectionNewRegistro.getRegistroCollection().remove(registroCollectionNewRegistro);
                        oldPruebaIdOfRegistroCollectionNewRegistro = em.merge(oldPruebaIdOfRegistroCollectionNewRegistro);
                    }
                }
            }
            for (Compite compiteCollectionNewCompite : compiteCollectionNew) {
                if (!compiteCollectionOld.contains(compiteCollectionNewCompite)) {
                    Prueba oldPruebaIdOfCompiteCollectionNewCompite = compiteCollectionNewCompite.getPruebaId();
                    compiteCollectionNewCompite.setPruebaId(prueba);
                    compiteCollectionNewCompite = em.merge(compiteCollectionNewCompite);
                    if (oldPruebaIdOfCompiteCollectionNewCompite != null && !oldPruebaIdOfCompiteCollectionNewCompite.equals(prueba)) {
                        oldPruebaIdOfCompiteCollectionNewCompite.getCompiteCollection().remove(compiteCollectionNewCompite);
                        oldPruebaIdOfCompiteCollectionNewCompite = em.merge(oldPruebaIdOfCompiteCollectionNewCompite);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = prueba.getId();
                if (findPrueba(id) == null) {
                    throw new NonexistentEntityException("The prueba with id " + id + " no longer exists.");
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
            Prueba prueba;
            try {
                prueba = em.getReference(Prueba.class, id);
                prueba.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The prueba with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Compuesta> compuestaCollectionOrphanCheck = prueba.getCompuestaCollection();
            for (Compuesta compuestaCollectionOrphanCheckCompuesta : compuestaCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Prueba (" + prueba + ") cannot be destroyed since the Compuesta " + compuestaCollectionOrphanCheckCompuesta + " in its compuestaCollection field has a non-nullable pruebaId field.");
            }
            Collection<Registro> registroCollectionOrphanCheck = prueba.getRegistroCollection();
            for (Registro registroCollectionOrphanCheckRegistro : registroCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Prueba (" + prueba + ") cannot be destroyed since the Registro " + registroCollectionOrphanCheckRegistro + " in its registroCollection field has a non-nullable pruebaId field.");
            }
            Collection<Compite> compiteCollectionOrphanCheck = prueba.getCompiteCollection();
            for (Compite compiteCollectionOrphanCheckCompite : compiteCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Prueba (" + prueba + ") cannot be destroyed since the Compite " + compiteCollectionOrphanCheckCompite + " in its compiteCollection field has a non-nullable pruebaId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(prueba);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Prueba> findPruebaEntities() {
        return findPruebaEntities(true, -1, -1);
    }

    public List<Prueba> findPruebaEntities(int maxResults, int firstResult) {
        return findPruebaEntities(false, maxResults, firstResult);
    }

    private List<Prueba> findPruebaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Prueba.class));
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

    public Prueba findPrueba(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Prueba.class, id);
        } finally {
            em.close();
        }
    }

    public int getPruebaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Prueba> rt = cq.from(Prueba.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    public List<Prueba> findPruebasByCompeticon(Competicion c) {
        EntityManager em = getEntityManager();
        List<Prueba> res;
        if(c == null){
            return null;
        } else{
        try {
            Query q = em.createNamedQuery("Prueba.findByCompeticionId");
            q.setParameter("id", c.getId());
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
        }
    }
    
    public List<Prueba> findPruebasByNotCompeticon(Competicion c) {
        EntityManager em = getEntityManager();
        List<Prueba> res;
        if(c == null){
            return null;
        } else{
        try {
            Query q = em.createNamedQuery("Prueba.findByNotCompeticionId");
            q.setParameter("id", c.getId());
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
        }
    }
    
    public Prueba findPruebaByNombreCompeticion(String nombre, Integer competicionid) {
        EntityManager em = getEntityManager();
        Prueba res;
        try {
            Query q = em.createNamedQuery("Prueba.findByNombreCompeticion");
            q.setParameter("nombre", nombre);
            q.setParameter("competicionid", competicionid);
            res = (Prueba)q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
}
