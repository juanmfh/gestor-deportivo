package modelo.dao;

import controlador.ControlUsuarios;
import controlador.InputException;
import modelo.RolUsuario;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import modelo.Competicion;
import modelo.dao.exceptions.IllegalOrphanException;
import modelo.dao.exceptions.NonexistentEntityException;

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
    

    /**Devuelve un usuario buscado a partir de su nick (nombre de usuario)
     * 
     * @param nick Nombre de usuario
     * @return Usuario o null en otro caso
     */
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
    
    public List<Usuario> findByRol(RolUsuario rol) {
        EntityManager em = getEntityManager();
        List<Usuario> res;
        try {
            Query q = em.createNamedQuery("Usuario.findByRol");
            q.setParameter("rol", rol.ordinal());
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    /**
     * Crea un nuevo usuario en la aplicación
     *
     * @param nick String con el nombre del usuario
     * @param password String con la contraseña del usuario
     * @param rol Enumerado que indica el rol del usuario
     * @param competicionesConAcceso Lista de competiciones en las que el
     * usuario tiene acceso
     * @return Usuario
     * @throws InputException
     */
    public static Usuario crearUsuario(String nick, String password, RolUsuario rol, List<Object> competicionesConAcceso) throws InputException {
        Usuario usuario = null;

        if (nick != null && nick.length() > 0) {
            if (password != null && password.length() > 0) {
                if (rol != null) {

                    UsuarioJpa usuariojpa = new UsuarioJpa();

                    // Buscamos que el nick no este ya en uso
                    usuario = usuariojpa.findUsuariobyNick(nick);

                    // Si no existe un usuario con ese nick se puede crear
                    if (usuario == null) {
                        usuario = new Usuario();
                        usuario.setNick(nick);
                        usuario.setPassword(password);
                        usuario.setRol(rol.ordinal());
                        usuariojpa.create(usuario);

                        //Le damos permiso al usuario en las competiciones
                        if (competicionesConAcceso != null) {

                            AdministradoJpa administradoJpa = new AdministradoJpa();
                            CompeticionJpa competicionJpa = new CompeticionJpa();
                            for (Object competicionString : competicionesConAcceso) {
                                Administrado administrado = new Administrado();
                                administrado.setCompeticionId(competicionJpa.findCompeticionByName(competicionString.toString()));
                                administrado.setUsuarioId(usuario);
                                administradoJpa.create(administrado);
                            }
                        }
                    } else {
                        throw new InputException("Nombre de usuario ocupado");
                    }
                    return usuario;
                } else {
                    throw new InputException("Rol no válido");
                }
            } else {
                throw new InputException("Contraseña no válida");
            }
        } else {
            throw new InputException("Nombre de usuario no válido");
        }

    }

    public static void eliminarUsuario(Integer usuarioId) throws InputException {

        if (usuarioId != null) {
            UsuarioJpa usuarioJpa = new UsuarioJpa();
            Usuario usuario = usuarioJpa.findUsuario(usuarioId);

            if (usuario != null) {

                //Miramos a ver si es el único usuario con rol de admin
                List<Usuario> usuarios = usuarioJpa.findByRol(RolUsuario.Administrador);
                if (RolUsuario.values()[usuario.getRol()].toString().equals(RolUsuario.Administrador.toString()) && (usuarios == null || usuarios.size() <= 1)) {
                    throw new InputException("No se puede eliminar el último administrador del sistema");
                } else {
                    //Buscamos todas las competiciones que administra el usuario seleccionado
                    AdministradoJpa administradoJpa = new AdministradoJpa();
                    List<Administrado> administrados = administradoJpa.findByUsuario(usuarioId);
                    if (administrados != null) {
                        for (Administrado a : administrados) {
                            try {
                                administradoJpa.destroy(a.getId());
                            } catch (NonexistentEntityException ex) {
                                Logger.getLogger(ControlUsuarios.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
                try {
                    usuarioJpa.destroy(usuarioId);
                } catch (IllegalOrphanException | NonexistentEntityException ex) {
                    throw new InputException("No se pudo eliminar el usuario seleccionado");
                }
            } else {
                throw new InputException("Usuario no encontrado");
            }
        } else {
            throw new InputException("Usuario no válido");
        }
    }

    public static Usuario modificarUsuario(Integer usuarioId, String nuevoNick, String nuevaPassword, RolUsuario nuevoRol) throws InputException {

        if (usuarioId != null) {

            UsuarioJpa usuariojpa = new UsuarioJpa();
            Usuario usuario = usuariojpa.findUsuario(usuarioId);

            if (usuario != null) {
                //Comprobamos que el nick y la contraseña son válidos
                if (nuevoNick != null && nuevoNick.length() > 0) {
                    if (nuevaPassword != null && nuevaPassword.length() > 0) {

                        Usuario temp = usuariojpa.findUsuariobyNick(nuevoNick);
                        if (temp != null && temp.getId() != usuario.getId()) {
                            throw new InputException("Nombre de usuario ocupado");
                        } else {
                            usuario.setNick(nuevoNick);
                            usuario.setPassword(nuevaPassword);

                            if (nuevoRol != null) {
                                //Miramos a ver si es el único usuario con rol de admin
                                List<Usuario> usuarios = usuariojpa.findByRol(RolUsuario.Administrador);
                                if ((RolUsuario.values()[usuario.getRol()].toString().equals(RolUsuario.Administrador.toString()) && (usuarios == null || usuarios.size() <= 1) && !nuevoRol.equals(RolUsuario.Administrador))) {
                                    throw new InputException("No se puede eliminar el último administrador del sistema");
                                } else {
                                    usuario.setRol(nuevoRol.ordinal());
                                }
                                try {
                                    usuariojpa.edit(usuario);
                                    return usuario;
                                } catch (Exception ex) {
                                    throw new InputException("No se pudo modificar el usuario seleccionado");
                                }
                            } else {
                                throw new InputException("Rol no válido");
                            }
                        }
                    } else {
                        throw new InputException("Contraseña no válida");
                    }
                } else {
                    throw new InputException("Nombre de usuario no válido");
                }
            } else {
                throw new InputException("Usuario no encontrado");
            }
        } else {
            throw new InputException("Usuario no válido");
        }
    }

    /**
     * Da permiso para administrar/ver una competición determinada a un usuario
     *
     * @param usuarioId Identificador del usuario
     * @param nombreCompeticion Nombre de la competicion
     * @throws controlador.InputException
     */
    public static void darAccesoACompeticion(Integer usuarioId, String nombreCompeticion) throws InputException {

        if (usuarioId != null) {
            if (nombreCompeticion != null) {
                AdministradoJpa administradoJpa = new AdministradoJpa();
                Administrado administrado = new Administrado();

                UsuarioJpa usuarioJpa = new UsuarioJpa();
                Usuario usuario = usuarioJpa.findUsuario(usuarioId);

                CompeticionJpa competicionJpa = new CompeticionJpa();
                Competicion competicion = competicionJpa.findCompeticionByName(nombreCompeticion);

                if (usuario != null) {
                    if (competicion != null) {

                        administrado.setUsuarioId(usuario);
                        administrado.setCompeticionId(competicion);
                        administradoJpa.create(administrado);
                    } else {
                        throw new InputException("Competición no encontrada");
                    }
                } else {
                    throw new InputException("Usuario no encontrado");
                }
            } else {
                throw new InputException("Competición no válida");
            }
        } else {
            throw new InputException("Usuario no válido");
        }
    }

    /**
     * Elimina el permiso para administrar/ver una competición determinada a un
     * usuario
     *
     * @param usuarioId
     * @param nombreCompeticion Nombre de la competicion
     * @throws controlador.InputException
     */
    public static void quitarAccesoACompeticion(Integer usuarioId, String nombreCompeticion) throws InputException {

        if (usuarioId != null) {
            if (nombreCompeticion != null) {
                AdministradoJpa administradoJpa = new AdministradoJpa();
                Administrado administrado = administradoJpa.findByCompeticionAndUsuario(usuarioId, nombreCompeticion);

                if (administrado != null) {
                    try {
                        administradoJpa.destroy(administrado.getId());
                    } catch (NonexistentEntityException ex) {
                        throw new InputException("No se puede quitar el acceso a esa competición");
                    }
                }else{
                    throw new InputException("El usuario no tiene permiso en esta competición");
                }
            } else {
                throw new InputException("Competición no válida");
            }
        } else {
            throw new InputException("Usuario no válido");
        }
    }
}
