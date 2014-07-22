package dao;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import modelo.Grupo;
import modelo.Equipo;
import java.util.ArrayList;
import java.util.Collection;
import modelo.Participante;
import modelo.Acceso;
import modelo.Competicion;
import modelo.Inscripcion;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import dao.exceptions.IllegalOrphanException;
import dao.exceptions.NonexistentEntityException;

/**
 *
 * @author JuanM
 */
public class GrupoJpa implements Serializable {

    public GrupoJpa() {
        this.emf = Persistence.createEntityManagerFactory("JavaAppDesktopPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Grupo grupo) {
        if (grupo.getEquipoCollection() == null) {
            grupo.setEquipoCollection(new ArrayList<Equipo>());
        }
        if (grupo.getParticipanteCollection() == null) {
            grupo.setParticipanteCollection(new ArrayList<Participante>());
        }
        if (grupo.getAccesoCollection() == null) {
            grupo.setAccesoCollection(new ArrayList<Acceso>());
        }
        if (grupo.getInscripcionCollection() == null) {
            grupo.setInscripcionCollection(new ArrayList<Inscripcion>());
        }
        if (grupo.getGrupoCollection() == null) {
            grupo.setGrupoCollection(new ArrayList<Grupo>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Grupo grupoId = grupo.getGrupoId();
            if (grupoId != null) {
                grupoId = em.getReference(grupoId.getClass(), grupoId.getId());
                grupo.setGrupoId(grupoId);
            }
            Collection<Equipo> attachedEquipoCollection = new ArrayList<Equipo>();
            for (Equipo equipoCollectionEquipoToAttach : grupo.getEquipoCollection()) {
                equipoCollectionEquipoToAttach = em.getReference(equipoCollectionEquipoToAttach.getClass(), equipoCollectionEquipoToAttach.getId());
                attachedEquipoCollection.add(equipoCollectionEquipoToAttach);
            }
            grupo.setEquipoCollection(attachedEquipoCollection);
            Collection<Participante> attachedParticipanteCollection = new ArrayList<Participante>();
            for (Participante participanteCollectionParticipanteToAttach : grupo.getParticipanteCollection()) {
                participanteCollectionParticipanteToAttach = em.getReference(participanteCollectionParticipanteToAttach.getClass(), participanteCollectionParticipanteToAttach.getId());
                attachedParticipanteCollection.add(participanteCollectionParticipanteToAttach);
            }
            grupo.setParticipanteCollection(attachedParticipanteCollection);
            Collection<Acceso> attachedAccesoCollection = new ArrayList<Acceso>();
            for (Acceso accesoCollectionAccesoToAttach : grupo.getAccesoCollection()) {
                accesoCollectionAccesoToAttach = em.getReference(accesoCollectionAccesoToAttach.getClass(), accesoCollectionAccesoToAttach.getId());
                attachedAccesoCollection.add(accesoCollectionAccesoToAttach);
            }
            grupo.setAccesoCollection(attachedAccesoCollection);
            Collection<Inscripcion> attachedInscripcionCollection = new ArrayList<Inscripcion>();
            for (Inscripcion inscripcionCollectionInscripcionToAttach : grupo.getInscripcionCollection()) {
                inscripcionCollectionInscripcionToAttach = em.getReference(inscripcionCollectionInscripcionToAttach.getClass(), inscripcionCollectionInscripcionToAttach.getId());
                attachedInscripcionCollection.add(inscripcionCollectionInscripcionToAttach);
            }
            grupo.setInscripcionCollection(attachedInscripcionCollection);
            Collection<Grupo> attachedGrupoCollection = new ArrayList<Grupo>();
            for (Grupo grupoCollectionGrupoToAttach : grupo.getGrupoCollection()) {
                grupoCollectionGrupoToAttach = em.getReference(grupoCollectionGrupoToAttach.getClass(), grupoCollectionGrupoToAttach.getId());
                attachedGrupoCollection.add(grupoCollectionGrupoToAttach);
            }
            grupo.setGrupoCollection(attachedGrupoCollection);
            em.persist(grupo);
            if (grupoId != null) {
                grupoId.getGrupoCollection().add(grupo);
                grupoId = em.merge(grupoId);
            }
            for (Equipo equipoCollectionEquipo : grupo.getEquipoCollection()) {
                Grupo oldGrupoIdOfEquipoCollectionEquipo = equipoCollectionEquipo.getGrupoId();
                equipoCollectionEquipo.setGrupoId(grupo);
                equipoCollectionEquipo = em.merge(equipoCollectionEquipo);
                if (oldGrupoIdOfEquipoCollectionEquipo != null) {
                    oldGrupoIdOfEquipoCollectionEquipo.getEquipoCollection().remove(equipoCollectionEquipo);
                    oldGrupoIdOfEquipoCollectionEquipo = em.merge(oldGrupoIdOfEquipoCollectionEquipo);
                }
            }
            for (Participante participanteCollectionParticipante : grupo.getParticipanteCollection()) {
                Grupo oldGrupoIdOfParticipanteCollectionParticipante = participanteCollectionParticipante.getGrupoId();
                participanteCollectionParticipante.setGrupoId(grupo);
                participanteCollectionParticipante = em.merge(participanteCollectionParticipante);
                if (oldGrupoIdOfParticipanteCollectionParticipante != null) {
                    oldGrupoIdOfParticipanteCollectionParticipante.getParticipanteCollection().remove(participanteCollectionParticipante);
                    oldGrupoIdOfParticipanteCollectionParticipante = em.merge(oldGrupoIdOfParticipanteCollectionParticipante);
                }
            }
            for (Acceso accesoCollectionAcceso : grupo.getAccesoCollection()) {
                Grupo oldGrupoIdOfAccesoCollectionAcceso = accesoCollectionAcceso.getGrupoId();
                accesoCollectionAcceso.setGrupoId(grupo);
                accesoCollectionAcceso = em.merge(accesoCollectionAcceso);
                if (oldGrupoIdOfAccesoCollectionAcceso != null) {
                    oldGrupoIdOfAccesoCollectionAcceso.getAccesoCollection().remove(accesoCollectionAcceso);
                    oldGrupoIdOfAccesoCollectionAcceso = em.merge(oldGrupoIdOfAccesoCollectionAcceso);
                }
            }
            for (Inscripcion inscripcionCollectionInscripcion : grupo.getInscripcionCollection()) {
                Grupo oldGrupoIdOfInscripcionCollectionInscripcion = inscripcionCollectionInscripcion.getGrupoId();
                inscripcionCollectionInscripcion.setGrupoId(grupo);
                inscripcionCollectionInscripcion = em.merge(inscripcionCollectionInscripcion);
                if (oldGrupoIdOfInscripcionCollectionInscripcion != null) {
                    oldGrupoIdOfInscripcionCollectionInscripcion.getInscripcionCollection().remove(inscripcionCollectionInscripcion);
                    oldGrupoIdOfInscripcionCollectionInscripcion = em.merge(oldGrupoIdOfInscripcionCollectionInscripcion);
                }
            }
            for (Grupo grupoCollectionGrupo : grupo.getGrupoCollection()) {
                Grupo oldGrupoIdOfGrupoCollectionGrupo = grupoCollectionGrupo.getGrupoId();
                grupoCollectionGrupo.setGrupoId(grupo);
                grupoCollectionGrupo = em.merge(grupoCollectionGrupo);
                if (oldGrupoIdOfGrupoCollectionGrupo != null) {
                    oldGrupoIdOfGrupoCollectionGrupo.getGrupoCollection().remove(grupoCollectionGrupo);
                    oldGrupoIdOfGrupoCollectionGrupo = em.merge(oldGrupoIdOfGrupoCollectionGrupo);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Grupo grupo) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Grupo persistentGrupo = em.find(Grupo.class, grupo.getId());
            Grupo grupoIdOld = persistentGrupo.getGrupoId();
            Grupo grupoIdNew = grupo.getGrupoId();
            Collection<Equipo> equipoCollectionOld = persistentGrupo.getEquipoCollection();
            Collection<Equipo> equipoCollectionNew = grupo.getEquipoCollection();
            Collection<Participante> participanteCollectionOld = persistentGrupo.getParticipanteCollection();
            Collection<Participante> participanteCollectionNew = grupo.getParticipanteCollection();
            Collection<Acceso> accesoCollectionOld = persistentGrupo.getAccesoCollection();
            Collection<Acceso> accesoCollectionNew = grupo.getAccesoCollection();
            Collection<Inscripcion> inscripcionCollectionOld = persistentGrupo.getInscripcionCollection();
            Collection<Inscripcion> inscripcionCollectionNew = grupo.getInscripcionCollection();
            Collection<Grupo> grupoCollectionOld = persistentGrupo.getGrupoCollection();
            Collection<Grupo> grupoCollectionNew = grupo.getGrupoCollection();
            List<String> illegalOrphanMessages = null;
            for (Equipo equipoCollectionOldEquipo : equipoCollectionOld) {
                if (!equipoCollectionNew.contains(equipoCollectionOldEquipo)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Equipo " + equipoCollectionOldEquipo + " since its grupoId field is not nullable.");
                }
            }
            for (Participante participanteCollectionOldParticipante : participanteCollectionOld) {
                if (!participanteCollectionNew.contains(participanteCollectionOldParticipante)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Participante " + participanteCollectionOldParticipante + " since its grupoId field is not nullable.");
                }
            }
            for (Acceso accesoCollectionOldAcceso : accesoCollectionOld) {
                if (!accesoCollectionNew.contains(accesoCollectionOldAcceso)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Acceso " + accesoCollectionOldAcceso + " since its grupoId field is not nullable.");
                }
            }
            for (Inscripcion inscripcionCollectionOldInscripcion : inscripcionCollectionOld) {
                if (!inscripcionCollectionNew.contains(inscripcionCollectionOldInscripcion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Inscripcion " + inscripcionCollectionOldInscripcion + " since its grupoId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (grupoIdNew != null) {
                grupoIdNew = em.getReference(grupoIdNew.getClass(), grupoIdNew.getId());
                grupo.setGrupoId(grupoIdNew);
            }
            Collection<Equipo> attachedEquipoCollectionNew = new ArrayList<Equipo>();
            for (Equipo equipoCollectionNewEquipoToAttach : equipoCollectionNew) {
                equipoCollectionNewEquipoToAttach = em.getReference(equipoCollectionNewEquipoToAttach.getClass(), equipoCollectionNewEquipoToAttach.getId());
                attachedEquipoCollectionNew.add(equipoCollectionNewEquipoToAttach);
            }
            equipoCollectionNew = attachedEquipoCollectionNew;
            grupo.setEquipoCollection(equipoCollectionNew);
            Collection<Participante> attachedParticipanteCollectionNew = new ArrayList<Participante>();
            for (Participante participanteCollectionNewParticipanteToAttach : participanteCollectionNew) {
                participanteCollectionNewParticipanteToAttach = em.getReference(participanteCollectionNewParticipanteToAttach.getClass(), participanteCollectionNewParticipanteToAttach.getId());
                attachedParticipanteCollectionNew.add(participanteCollectionNewParticipanteToAttach);
            }
            participanteCollectionNew = attachedParticipanteCollectionNew;
            grupo.setParticipanteCollection(participanteCollectionNew);
            Collection<Acceso> attachedAccesoCollectionNew = new ArrayList<Acceso>();
            for (Acceso accesoCollectionNewAccesoToAttach : accesoCollectionNew) {
                accesoCollectionNewAccesoToAttach = em.getReference(accesoCollectionNewAccesoToAttach.getClass(), accesoCollectionNewAccesoToAttach.getId());
                attachedAccesoCollectionNew.add(accesoCollectionNewAccesoToAttach);
            }
            accesoCollectionNew = attachedAccesoCollectionNew;
            grupo.setAccesoCollection(accesoCollectionNew);
            Collection<Inscripcion> attachedInscripcionCollectionNew = new ArrayList<Inscripcion>();
            for (Inscripcion inscripcionCollectionNewInscripcionToAttach : inscripcionCollectionNew) {
                inscripcionCollectionNewInscripcionToAttach = em.getReference(inscripcionCollectionNewInscripcionToAttach.getClass(), inscripcionCollectionNewInscripcionToAttach.getId());
                attachedInscripcionCollectionNew.add(inscripcionCollectionNewInscripcionToAttach);
            }
            inscripcionCollectionNew = attachedInscripcionCollectionNew;
            grupo.setInscripcionCollection(inscripcionCollectionNew);
            Collection<Grupo> attachedGrupoCollectionNew = new ArrayList<Grupo>();
            for (Grupo grupoCollectionNewGrupoToAttach : grupoCollectionNew) {
                grupoCollectionNewGrupoToAttach = em.getReference(grupoCollectionNewGrupoToAttach.getClass(), grupoCollectionNewGrupoToAttach.getId());
                attachedGrupoCollectionNew.add(grupoCollectionNewGrupoToAttach);
            }
            grupoCollectionNew = attachedGrupoCollectionNew;
            grupo.setGrupoCollection(grupoCollectionNew);
            grupo = em.merge(grupo);
            if (grupoIdOld != null && !grupoIdOld.equals(grupoIdNew)) {
                grupoIdOld.getGrupoCollection().remove(grupo);
                grupoIdOld = em.merge(grupoIdOld);
            }
            if (grupoIdNew != null && !grupoIdNew.equals(grupoIdOld)) {
                grupoIdNew.getGrupoCollection().add(grupo);
                grupoIdNew = em.merge(grupoIdNew);
            }
            for (Equipo equipoCollectionNewEquipo : equipoCollectionNew) {
                if (!equipoCollectionOld.contains(equipoCollectionNewEquipo)) {
                    Grupo oldGrupoIdOfEquipoCollectionNewEquipo = equipoCollectionNewEquipo.getGrupoId();
                    equipoCollectionNewEquipo.setGrupoId(grupo);
                    equipoCollectionNewEquipo = em.merge(equipoCollectionNewEquipo);
                    if (oldGrupoIdOfEquipoCollectionNewEquipo != null && !oldGrupoIdOfEquipoCollectionNewEquipo.equals(grupo)) {
                        oldGrupoIdOfEquipoCollectionNewEquipo.getEquipoCollection().remove(equipoCollectionNewEquipo);
                        oldGrupoIdOfEquipoCollectionNewEquipo = em.merge(oldGrupoIdOfEquipoCollectionNewEquipo);
                    }
                }
            }
            for (Participante participanteCollectionNewParticipante : participanteCollectionNew) {
                if (!participanteCollectionOld.contains(participanteCollectionNewParticipante)) {
                    Grupo oldGrupoIdOfParticipanteCollectionNewParticipante = participanteCollectionNewParticipante.getGrupoId();
                    participanteCollectionNewParticipante.setGrupoId(grupo);
                    participanteCollectionNewParticipante = em.merge(participanteCollectionNewParticipante);
                    if (oldGrupoIdOfParticipanteCollectionNewParticipante != null && !oldGrupoIdOfParticipanteCollectionNewParticipante.equals(grupo)) {
                        oldGrupoIdOfParticipanteCollectionNewParticipante.getParticipanteCollection().remove(participanteCollectionNewParticipante);
                        oldGrupoIdOfParticipanteCollectionNewParticipante = em.merge(oldGrupoIdOfParticipanteCollectionNewParticipante);
                    }
                }
            }
            for (Acceso accesoCollectionNewAcceso : accesoCollectionNew) {
                if (!accesoCollectionOld.contains(accesoCollectionNewAcceso)) {
                    Grupo oldGrupoIdOfAccesoCollectionNewAcceso = accesoCollectionNewAcceso.getGrupoId();
                    accesoCollectionNewAcceso.setGrupoId(grupo);
                    accesoCollectionNewAcceso = em.merge(accesoCollectionNewAcceso);
                    if (oldGrupoIdOfAccesoCollectionNewAcceso != null && !oldGrupoIdOfAccesoCollectionNewAcceso.equals(grupo)) {
                        oldGrupoIdOfAccesoCollectionNewAcceso.getAccesoCollection().remove(accesoCollectionNewAcceso);
                        oldGrupoIdOfAccesoCollectionNewAcceso = em.merge(oldGrupoIdOfAccesoCollectionNewAcceso);
                    }
                }
            }
            for (Inscripcion inscripcionCollectionNewInscripcion : inscripcionCollectionNew) {
                if (!inscripcionCollectionOld.contains(inscripcionCollectionNewInscripcion)) {
                    Grupo oldGrupoIdOfInscripcionCollectionNewInscripcion = inscripcionCollectionNewInscripcion.getGrupoId();
                    inscripcionCollectionNewInscripcion.setGrupoId(grupo);
                    inscripcionCollectionNewInscripcion = em.merge(inscripcionCollectionNewInscripcion);
                    if (oldGrupoIdOfInscripcionCollectionNewInscripcion != null && !oldGrupoIdOfInscripcionCollectionNewInscripcion.equals(grupo)) {
                        oldGrupoIdOfInscripcionCollectionNewInscripcion.getInscripcionCollection().remove(inscripcionCollectionNewInscripcion);
                        oldGrupoIdOfInscripcionCollectionNewInscripcion = em.merge(oldGrupoIdOfInscripcionCollectionNewInscripcion);
                    }
                }
            }
            for (Grupo grupoCollectionOldGrupo : grupoCollectionOld) {
                if (!grupoCollectionNew.contains(grupoCollectionOldGrupo)) {
                    grupoCollectionOldGrupo.setGrupoId(null);
                    grupoCollectionOldGrupo = em.merge(grupoCollectionOldGrupo);
                }
            }
            for (Grupo grupoCollectionNewGrupo : grupoCollectionNew) {
                if (!grupoCollectionOld.contains(grupoCollectionNewGrupo)) {
                    Grupo oldGrupoIdOfGrupoCollectionNewGrupo = grupoCollectionNewGrupo.getGrupoId();
                    grupoCollectionNewGrupo.setGrupoId(grupo);
                    grupoCollectionNewGrupo = em.merge(grupoCollectionNewGrupo);
                    if (oldGrupoIdOfGrupoCollectionNewGrupo != null && !oldGrupoIdOfGrupoCollectionNewGrupo.equals(grupo)) {
                        oldGrupoIdOfGrupoCollectionNewGrupo.getGrupoCollection().remove(grupoCollectionNewGrupo);
                        oldGrupoIdOfGrupoCollectionNewGrupo = em.merge(oldGrupoIdOfGrupoCollectionNewGrupo);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = grupo.getId();
                if (findGrupo(id) == null) {
                    throw new NonexistentEntityException("The grupo with id " + id + " no longer exists.");
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
            Grupo grupo;
            try {
                grupo = em.getReference(Grupo.class, id);
                grupo.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The grupo with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Equipo> equipoCollectionOrphanCheck = grupo.getEquipoCollection();
            for (Equipo equipoCollectionOrphanCheckEquipo : equipoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Grupo (" + grupo + ") cannot be destroyed since the Equipo " + equipoCollectionOrphanCheckEquipo + " in its equipoCollection field has a non-nullable grupoId field.");
            }
            Collection<Participante> participanteCollectionOrphanCheck = grupo.getParticipanteCollection();
            for (Participante participanteCollectionOrphanCheckParticipante : participanteCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Grupo (" + grupo + ") cannot be destroyed since the Participante " + participanteCollectionOrphanCheckParticipante + " in its participanteCollection field has a non-nullable grupoId field.");
            }
            Collection<Acceso> accesoCollectionOrphanCheck = grupo.getAccesoCollection();
            for (Acceso accesoCollectionOrphanCheckAcceso : accesoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Grupo (" + grupo + ") cannot be destroyed since the Acceso " + accesoCollectionOrphanCheckAcceso + " in its accesoCollection field has a non-nullable grupoId field.");
            }
            Collection<Inscripcion> inscripcionCollectionOrphanCheck = grupo.getInscripcionCollection();
            for (Inscripcion inscripcionCollectionOrphanCheckInscripcion : inscripcionCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Grupo (" + grupo + ") cannot be destroyed since the Inscripcion " + inscripcionCollectionOrphanCheckInscripcion + " in its inscripcionCollection field has a non-nullable grupoId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Grupo grupoId = grupo.getGrupoId();
            if (grupoId != null) {
                grupoId.getGrupoCollection().remove(grupo);
                grupoId = em.merge(grupoId);
            }
            Collection<Grupo> grupoCollection = grupo.getGrupoCollection();
            for (Grupo grupoCollectionGrupo : grupoCollection) {
                grupoCollectionGrupo.setGrupoId(null);
                grupoCollectionGrupo = em.merge(grupoCollectionGrupo);
            }
            em.remove(grupo);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Grupo> findGrupoEntities() {
        return findGrupoEntities(true, -1, -1);
    }

    public List<Grupo> findGrupoEntities(int maxResults, int firstResult) {
        return findGrupoEntities(false, maxResults, firstResult);
    }

    private List<Grupo> findGrupoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Grupo.class));
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

    public Grupo findGrupo(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Grupo.class, id);
        } finally {
            em.close();
        }
    }

    public int getGrupoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Grupo> rt = cq.from(Grupo.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    // author: Juan María Frías Hidalgo
    
    /** Devuelve una lista con los grupos que hay en una competición c.
     * 
     * @param c Objeto Competicion
     * @return List<Grupo>
     */
    public List<Grupo> findGruposByCompeticion(Competicion c) {
        EntityManager em = getEntityManager();
        List<Grupo> res;
        if (c == null) {
            return null;
        } else {
            try {
                Query q = em.createNamedQuery("Grupo.findByCompeticion");
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
    
    /** Devuelve una lista con los grupos que hay en una competición c.
     * 
     * @param c Objeto Competicion
     * @return List<Grupo>
     */
    public List<Grupo> findGruposRaizByCompeticion(Competicion c) {
        EntityManager em = getEntityManager();
        List<Grupo> res;
        if (c == null) {
            return null;
        } else {
            try {
                Query q = em.createNamedQuery("Grupo.findRaizByCompeticion");
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

    /**Devuelve un objeto Grupo a partir del nombre de este y la competición 
     * donde se encuentra (ya que puede haber grupos con igual nombre en distintas
     * competiciones)
     * 
     * @param nombre            Nombre del grupo
     * @param competicionid     Identificador de la competición
     * @return 
     */
    public Grupo findGrupoByNombreAndCompeticion(String nombre, Integer competicionid) {
        EntityManager em = getEntityManager();
        Grupo res;
        if (nombre == null) {
            return null;
        } else {
            try {
                Query q = em.createNamedQuery("Grupo.findByNombreAndCompeticion");
                q.setParameter("nombre", nombre);
                q.setParameter("competicionid", competicionid);
                res = (Grupo) q.getSingleResult();
            } catch (NoResultException e) {
                return null;
            } finally {
                em.close();
            }
            return res;
        }
    }

    /**Obtiene una lista de grupos que son subGrupos del grupo pasado como
     * parámetro
     * 
     * @param id    Identificador del Grupo padre
     * @return List<Grupo>
     */
    public List<Grupo> findGrupoByGrupoId(Integer id) {
        EntityManager em = getEntityManager();
        List<Grupo> res;
        try {
            Query q = em.createNamedQuery("Grupo.findByGrupoId");
            q.setParameter("id", id);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }


    /**Obtiene el grupo donde está un participante de una competición.
     * 
     * @param competicionid     Identificador de la competición
     * @param participanteid    Identificador del participante
     * @return Grupo
     */
    public Grupo findByParticipanteCompeticion(Integer competicionid, Integer participanteid) {
        EntityManager em = getEntityManager();
        Grupo res;
        try {
            Query q = em.createNamedQuery("Grupo.findByParticipanteCompeticion");
            q.setParameter("competicionid", competicionid);
            q.setParameter("participanteid", participanteid);
            res = (Grupo) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }

    /**Obtiene el grupo donde está un equipo de una competición
     * 
     * @param competicionid     Identificador de la competición.
     * @param equipoid          Identificador del equipo.
     * @return Grupo
     */
    public Grupo findByEquipoCompeticion(Integer competicionid, Integer equipoid) {
        EntityManager em = getEntityManager();
        Grupo res;
        try {
            Query q = em.createNamedQuery("Grupo.findByEquipoCompeticion");
            q.setParameter("competicionid", competicionid);
            q.setParameter("equipoid", equipoid);
            res = (Grupo) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    /** Devuelve una lista con los nombres de los grupos que hay en una competición c.
     * 
     * @param c Objeto Competicion
     * @return List<String>
     */
    public List<String> findNombresGruposByCompeticion(Competicion c) {
        EntityManager em = getEntityManager();
        List<String> res;
        if (c == null) {
            return null;
        } else {
            try {
                Query q = em.createNamedQuery("Grupo.findNombresByCompeticion");
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
}


