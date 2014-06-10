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
import modelo.Acceso;
import java.util.ArrayList;
import java.util.Collection;
import modelo.Administrado;
import modelo.Usuario;
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
public class UsuarioJpa implements Serializable {

    public UsuarioJpa() {
        this.emf = Persistence.createEntityManagerFactory("JavaAppDesktopPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Usuario usuario) {
        if (usuario.getAccesoCollection() == null) {
            usuario.setAccesoCollection(new ArrayList<Acceso>());
        }
        if (usuario.getAdministradoCollection() == null) {
            usuario.setAdministradoCollection(new ArrayList<Administrado>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Acceso> attachedAccesoCollection = new ArrayList<Acceso>();
            for (Acceso accesoCollectionAccesoToAttach : usuario.getAccesoCollection()) {
                accesoCollectionAccesoToAttach = em.getReference(accesoCollectionAccesoToAttach.getClass(), accesoCollectionAccesoToAttach.getId());
                attachedAccesoCollection.add(accesoCollectionAccesoToAttach);
            }
            usuario.setAccesoCollection(attachedAccesoCollection);
            Collection<Administrado> attachedAdministradoCollection = new ArrayList<Administrado>();
            for (Administrado administradoCollectionAdministradoToAttach : usuario.getAdministradoCollection()) {
                administradoCollectionAdministradoToAttach = em.getReference(administradoCollectionAdministradoToAttach.getClass(), administradoCollectionAdministradoToAttach.getId());
                attachedAdministradoCollection.add(administradoCollectionAdministradoToAttach);
            }
            usuario.setAdministradoCollection(attachedAdministradoCollection);
            em.persist(usuario);
            for (Acceso accesoCollectionAcceso : usuario.getAccesoCollection()) {
                Usuario oldUsuarioIdOfAccesoCollectionAcceso = accesoCollectionAcceso.getUsuarioId();
                accesoCollectionAcceso.setUsuarioId(usuario);
                accesoCollectionAcceso = em.merge(accesoCollectionAcceso);
                if (oldUsuarioIdOfAccesoCollectionAcceso != null) {
                    oldUsuarioIdOfAccesoCollectionAcceso.getAccesoCollection().remove(accesoCollectionAcceso);
                    oldUsuarioIdOfAccesoCollectionAcceso = em.merge(oldUsuarioIdOfAccesoCollectionAcceso);
                }
            }
            for (Administrado administradoCollectionAdministrado : usuario.getAdministradoCollection()) {
                Usuario oldUsuarioIdOfAdministradoCollectionAdministrado = administradoCollectionAdministrado.getUsuarioId();
                administradoCollectionAdministrado.setUsuarioId(usuario);
                administradoCollectionAdministrado = em.merge(administradoCollectionAdministrado);
                if (oldUsuarioIdOfAdministradoCollectionAdministrado != null) {
                    oldUsuarioIdOfAdministradoCollectionAdministrado.getAdministradoCollection().remove(administradoCollectionAdministrado);
                    oldUsuarioIdOfAdministradoCollectionAdministrado = em.merge(oldUsuarioIdOfAdministradoCollectionAdministrado);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Usuario usuario) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario persistentUsuario = em.find(Usuario.class, usuario.getId());
            Collection<Acceso> accesoCollectionOld = persistentUsuario.getAccesoCollection();
            Collection<Acceso> accesoCollectionNew = usuario.getAccesoCollection();
            Collection<Administrado> administradoCollectionOld = persistentUsuario.getAdministradoCollection();
            Collection<Administrado> administradoCollectionNew = usuario.getAdministradoCollection();
            List<String> illegalOrphanMessages = null;
            for (Acceso accesoCollectionOldAcceso : accesoCollectionOld) {
                if (!accesoCollectionNew.contains(accesoCollectionOldAcceso)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Acceso " + accesoCollectionOldAcceso + " since its usuarioId field is not nullable.");
                }
            }
            for (Administrado administradoCollectionOldAdministrado : administradoCollectionOld) {
                if (!administradoCollectionNew.contains(administradoCollectionOldAdministrado)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Administrado " + administradoCollectionOldAdministrado + " since its usuarioId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Acceso> attachedAccesoCollectionNew = new ArrayList<Acceso>();
            for (Acceso accesoCollectionNewAccesoToAttach : accesoCollectionNew) {
                accesoCollectionNewAccesoToAttach = em.getReference(accesoCollectionNewAccesoToAttach.getClass(), accesoCollectionNewAccesoToAttach.getId());
                attachedAccesoCollectionNew.add(accesoCollectionNewAccesoToAttach);
            }
            accesoCollectionNew = attachedAccesoCollectionNew;
            usuario.setAccesoCollection(accesoCollectionNew);
            Collection<Administrado> attachedAdministradoCollectionNew = new ArrayList<Administrado>();
            for (Administrado administradoCollectionNewAdministradoToAttach : administradoCollectionNew) {
                administradoCollectionNewAdministradoToAttach = em.getReference(administradoCollectionNewAdministradoToAttach.getClass(), administradoCollectionNewAdministradoToAttach.getId());
                attachedAdministradoCollectionNew.add(administradoCollectionNewAdministradoToAttach);
            }
            administradoCollectionNew = attachedAdministradoCollectionNew;
            usuario.setAdministradoCollection(administradoCollectionNew);
            usuario = em.merge(usuario);
            for (Acceso accesoCollectionNewAcceso : accesoCollectionNew) {
                if (!accesoCollectionOld.contains(accesoCollectionNewAcceso)) {
                    Usuario oldUsuarioIdOfAccesoCollectionNewAcceso = accesoCollectionNewAcceso.getUsuarioId();
                    accesoCollectionNewAcceso.setUsuarioId(usuario);
                    accesoCollectionNewAcceso = em.merge(accesoCollectionNewAcceso);
                    if (oldUsuarioIdOfAccesoCollectionNewAcceso != null && !oldUsuarioIdOfAccesoCollectionNewAcceso.equals(usuario)) {
                        oldUsuarioIdOfAccesoCollectionNewAcceso.getAccesoCollection().remove(accesoCollectionNewAcceso);
                        oldUsuarioIdOfAccesoCollectionNewAcceso = em.merge(oldUsuarioIdOfAccesoCollectionNewAcceso);
                    }
                }
            }
            for (Administrado administradoCollectionNewAdministrado : administradoCollectionNew) {
                if (!administradoCollectionOld.contains(administradoCollectionNewAdministrado)) {
                    Usuario oldUsuarioIdOfAdministradoCollectionNewAdministrado = administradoCollectionNewAdministrado.getUsuarioId();
                    administradoCollectionNewAdministrado.setUsuarioId(usuario);
                    administradoCollectionNewAdministrado = em.merge(administradoCollectionNewAdministrado);
                    if (oldUsuarioIdOfAdministradoCollectionNewAdministrado != null && !oldUsuarioIdOfAdministradoCollectionNewAdministrado.equals(usuario)) {
                        oldUsuarioIdOfAdministradoCollectionNewAdministrado.getAdministradoCollection().remove(administradoCollectionNewAdministrado);
                        oldUsuarioIdOfAdministradoCollectionNewAdministrado = em.merge(oldUsuarioIdOfAdministradoCollectionNewAdministrado);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = usuario.getId();
                if (findUsuario(id) == null) {
                    throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.");
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
            Usuario usuario;
            try {
                usuario = em.getReference(Usuario.class, id);
                usuario.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Acceso> accesoCollectionOrphanCheck = usuario.getAccesoCollection();
            for (Acceso accesoCollectionOrphanCheckAcceso : accesoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Acceso " + accesoCollectionOrphanCheckAcceso + " in its accesoCollection field has a non-nullable usuarioId field.");
            }
            Collection<Administrado> administradoCollectionOrphanCheck = usuario.getAdministradoCollection();
            for (Administrado administradoCollectionOrphanCheckAdministrado : administradoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Administrado " + administradoCollectionOrphanCheckAdministrado + " in its administradoCollection field has a non-nullable usuarioId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(usuario);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Usuario> findUsuarioEntities() {
        return findUsuarioEntities(true, -1, -1);
    }

    public List<Usuario> findUsuarioEntities(int maxResults, int firstResult) {
        return findUsuarioEntities(false, maxResults, firstResult);
    }

    private List<Usuario> findUsuarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuario.class));
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

    public Usuario findUsuario(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsuarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuario> rt = cq.from(Usuario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    // Creados por mi
    
    public Usuario findUsuariobyNick(String nick){
        EntityManager em = getEntityManager();
        Usuario res = null;
        try{
            if(nick != null){
            Query q = getEntityManager().createNamedQuery("Usuario.findByNick");
            q.setParameter("nick", nick);
            res = (Usuario) q.getSingleResult();
            }
        }catch(NoResultException e){
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
}
