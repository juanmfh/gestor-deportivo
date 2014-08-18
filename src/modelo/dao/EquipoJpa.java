package modelo.dao;

import controlador.InputException;
import modelo.Equipo;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import modelo.Grupo;
import modelo.Participante;
import java.util.ArrayList;
import java.util.Collection;
import modelo.Registro;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import modelo.Competicion;
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
        if (equipo.getRegistroCollection() == null) {
            equipo.setRegistroCollection(new ArrayList<Registro>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Grupo grupoId = equipo.getGrupoId();
            if (grupoId != null) {
                grupoId = em.getReference(grupoId.getClass(), grupoId.getId());
                equipo.setGrupoId(grupoId);
            }
            Collection<Participante> attachedParticipanteCollection = new ArrayList<Participante>();
            for (Participante participanteCollectionParticipanteToAttach : equipo.getParticipanteCollection()) {
                participanteCollectionParticipanteToAttach = em.getReference(participanteCollectionParticipanteToAttach.getClass(), participanteCollectionParticipanteToAttach.getId());
                attachedParticipanteCollection.add(participanteCollectionParticipanteToAttach);
            }
            equipo.setParticipanteCollection(attachedParticipanteCollection);
            Collection<Registro> attachedRegistroCollection = new ArrayList<Registro>();
            for (Registro registroCollectionRegistroToAttach : equipo.getRegistroCollection()) {
                registroCollectionRegistroToAttach = em.getReference(registroCollectionRegistroToAttach.getClass(), registroCollectionRegistroToAttach.getId());
                attachedRegistroCollection.add(registroCollectionRegistroToAttach);
            }
            equipo.setRegistroCollection(attachedRegistroCollection);
            em.persist(equipo);
            if (grupoId != null) {
                grupoId.getEquipoCollection().add(equipo);
                grupoId = em.merge(grupoId);
            }
            for (Participante participanteCollectionParticipante : equipo.getParticipanteCollection()) {
                Equipo oldEquipoIdOfParticipanteCollectionParticipante = participanteCollectionParticipante.getEquipoId();
                participanteCollectionParticipante.setEquipoId(equipo);
                participanteCollectionParticipante = em.merge(participanteCollectionParticipante);
                if (oldEquipoIdOfParticipanteCollectionParticipante != null) {
                    oldEquipoIdOfParticipanteCollectionParticipante.getParticipanteCollection().remove(participanteCollectionParticipante);
                    oldEquipoIdOfParticipanteCollectionParticipante = em.merge(oldEquipoIdOfParticipanteCollectionParticipante);
                }
            }
            for (Registro registroCollectionRegistro : equipo.getRegistroCollection()) {
                Equipo oldEquipoIdOfRegistroCollectionRegistro = registroCollectionRegistro.getEquipoId();
                registroCollectionRegistro.setEquipoId(equipo);
                registroCollectionRegistro = em.merge(registroCollectionRegistro);
                if (oldEquipoIdOfRegistroCollectionRegistro != null) {
                    oldEquipoIdOfRegistroCollectionRegistro.getRegistroCollection().remove(registroCollectionRegistro);
                    oldEquipoIdOfRegistroCollectionRegistro = em.merge(oldEquipoIdOfRegistroCollectionRegistro);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Equipo equipo) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Equipo persistentEquipo = em.find(Equipo.class, equipo.getId());
            Grupo grupoIdOld = persistentEquipo.getGrupoId();
            Grupo grupoIdNew = equipo.getGrupoId();
            Collection<Participante> participanteCollectionOld = persistentEquipo.getParticipanteCollection();
            Collection<Participante> participanteCollectionNew = equipo.getParticipanteCollection();
            Collection<Registro> registroCollectionOld = persistentEquipo.getRegistroCollection();
            Collection<Registro> registroCollectionNew = equipo.getRegistroCollection();
            if (grupoIdNew != null) {
                grupoIdNew = em.getReference(grupoIdNew.getClass(), grupoIdNew.getId());
                equipo.setGrupoId(grupoIdNew);
            }
            Collection<Participante> attachedParticipanteCollectionNew = new ArrayList<Participante>();
            for (Participante participanteCollectionNewParticipanteToAttach : participanteCollectionNew) {
                participanteCollectionNewParticipanteToAttach = em.getReference(participanteCollectionNewParticipanteToAttach.getClass(), participanteCollectionNewParticipanteToAttach.getId());
                attachedParticipanteCollectionNew.add(participanteCollectionNewParticipanteToAttach);
            }
            participanteCollectionNew = attachedParticipanteCollectionNew;
            equipo.setParticipanteCollection(participanteCollectionNew);
            Collection<Registro> attachedRegistroCollectionNew = new ArrayList<Registro>();
            for (Registro registroCollectionNewRegistroToAttach : registroCollectionNew) {
                registroCollectionNewRegistroToAttach = em.getReference(registroCollectionNewRegistroToAttach.getClass(), registroCollectionNewRegistroToAttach.getId());
                attachedRegistroCollectionNew.add(registroCollectionNewRegistroToAttach);
            }
            registroCollectionNew = attachedRegistroCollectionNew;
            equipo.setRegistroCollection(registroCollectionNew);
            equipo = em.merge(equipo);
            if (grupoIdOld != null && !grupoIdOld.equals(grupoIdNew)) {
                grupoIdOld.getEquipoCollection().remove(equipo);
                grupoIdOld = em.merge(grupoIdOld);
            }
            if (grupoIdNew != null && !grupoIdNew.equals(grupoIdOld)) {
                grupoIdNew.getEquipoCollection().add(equipo);
                grupoIdNew = em.merge(grupoIdNew);
            }
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
            for (Registro registroCollectionOldRegistro : registroCollectionOld) {
                if (!registroCollectionNew.contains(registroCollectionOldRegistro)) {
                    registroCollectionOldRegistro.setEquipoId(null);
                    registroCollectionOldRegistro = em.merge(registroCollectionOldRegistro);
                }
            }
            for (Registro registroCollectionNewRegistro : registroCollectionNew) {
                if (!registroCollectionOld.contains(registroCollectionNewRegistro)) {
                    Equipo oldEquipoIdOfRegistroCollectionNewRegistro = registroCollectionNewRegistro.getEquipoId();
                    registroCollectionNewRegistro.setEquipoId(equipo);
                    registroCollectionNewRegistro = em.merge(registroCollectionNewRegistro);
                    if (oldEquipoIdOfRegistroCollectionNewRegistro != null && !oldEquipoIdOfRegistroCollectionNewRegistro.equals(equipo)) {
                        oldEquipoIdOfRegistroCollectionNewRegistro.getRegistroCollection().remove(registroCollectionNewRegistro);
                        oldEquipoIdOfRegistroCollectionNewRegistro = em.merge(oldEquipoIdOfRegistroCollectionNewRegistro);
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

    public void destroy(Integer id) throws NonexistentEntityException {
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
            Grupo grupoId = equipo.getGrupoId();
            if (grupoId != null) {
                grupoId.getEquipoCollection().remove(equipo);
                grupoId = em.merge(grupoId);
            }
            Collection<Participante> participanteCollection = equipo.getParticipanteCollection();
            for (Participante participanteCollectionParticipante : participanteCollection) {
                participanteCollectionParticipante.setEquipoId(null);
                participanteCollectionParticipante = em.merge(participanteCollectionParticipante);
            }
            Collection<Registro> registroCollection = equipo.getRegistroCollection();
            for (Registro registroCollectionRegistro : registroCollection) {
                registroCollectionRegistro.setEquipoId(null);
                registroCollectionRegistro = em.merge(registroCollectionRegistro);
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
    
    // author: Juan María Frías Hidalgo
    
    /** Devuelve un objeto Equipo a partir del nombre de este, y de la 
     *  competición en la que se encuentra.
     * 
     * @param nombre            Nombre del equipo
     * @param competicionid     Identificador de la competición
     * @return Equipo
     */
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
    
    /**Devuelve una lista de Equipos que tiene una competición
     * 
     * @param competicionid     Identificador de la competición
     * @return List<Equipo>
     */
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
    
    /**Devuelve una lista de Equipos que hay en un grupo determinado.
     * Como es el identificador del grupo el que se pasa como parámetro no hace 
     * falta indicar la competición ya que es único.
     * 
     * @param grupoid   Identificador del grupo
     * @return List<Equipo>
     */
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
    
    
    /**
     * Crea un equipo nuevo
     *
     * @param competicion Competicion donde se creará el equipo
     * @param nombre Nombre del equipo
     * @param nombreGrupo Nombre del grupo al que pertenece el equipo
     * @return Equipo si se ha podido crear el equipo
     * @throws controlador.InputException
     */
    public static Equipo crearEquipo(Competicion competicion, String nombre, String nombreGrupo) throws InputException {

        if (competicion != null) {

            EquipoJpa equipojpa = new EquipoJpa();
            Equipo equipo = null;

            // Se comprueba que el grupo es válido   
            if (nombre != null && nombre.length() > 0) {
                // y el nombre del equipo es único en la competición
                if (equipojpa.findByNombreAndCompeticion(nombre, competicion.getId()) == null) {

                    if (nombreGrupo != null) {
                        GrupoJpa grupojpa = new GrupoJpa();
                        // Obtenemos el grupo a partir del nombre de este
                        Grupo grupo = grupojpa.findGrupoByNombreAndCompeticion(nombreGrupo, competicion.getId());
                        if (grupo != null) {
                            // Creamos el equipo
                            equipo = new Equipo();
                            equipo.setNombre(nombre);
                            equipo.setGrupoId(grupo);
                            equipojpa.create(equipo);
                        } else {
                            throw new InputException("Grupo no encontrado");
                        }
                    } else {
                        throw new InputException("Grupo no válido");
                    }
                } else {
                    throw new InputException("Nombre de equipo ocupado");
                }
            } else {
                throw new InputException("Nombre de equipo no válido");
            }
            return equipo;
        } else {
            throw new InputException("Nombre de competición no válido");
        }
    }

    /**
     * Elimina el equipo cuyo id es equipoid. (No elimina a sus participantes)
     *
     * @param equipoid Identificador del equipo a modificar
     * @throws controlador.InputException
     */
    public static void eliminarEquipo(Integer equipoid) throws InputException {

        if (equipoid != null) {
            EquipoJpa equipojpa = new EquipoJpa();
            try {
                // Eliminamos el equipo
                equipojpa.destroy(equipoid);
            } catch (NonexistentEntityException ex) {
                throw new InputException("Equipo no encontrado");
            }
        } else {
            throw new InputException("Equipo no válido");
        }
    }

    /**
     * Modifica el nombre y grupo (en caso de que el equipo no tenga miembros
     * asociados) del equipo cuyo id es equipoid
     *
     * @param competicion
     * @param equipoid Identificador del equipo a modificar
     * @param nombreEquipo
     * @param nombreGrupo
     * @return Equipo si se ha modificado correctamente
     * @throws controlador.InputException
     */
    public static Equipo modificarEquipo(Competicion competicion, Integer equipoid, String nombreEquipo, String nombreGrupo) throws InputException {

        if (competicion != null) {
            if (equipoid != null) {
                EquipoJpa equipojpa = new EquipoJpa();

                Equipo equipo = equipojpa.findEquipo(equipoid);

                // Se comprueba que el equipo a modificar es válido
                if (equipo != null) {

                    if (nombreEquipo != null && nombreEquipo.length() > 0) {
                        Equipo antiguo = equipojpa.findByNombreAndCompeticion(
                                nombreEquipo,
                                competicion.getId());
                        // Comprobamos que el nombre del equipo no existe o es suyo
                        if (antiguo == null || antiguo.getId() == equipoid) {
                            equipo.setNombre(nombreEquipo);
                            try {
                                GrupoJpa grupojpa = new GrupoJpa();

                                // Si el equipo no tiene miembros modificamos su grupo
                                if (equipo.getParticipanteCollection().isEmpty()) {

                                    if (nombreGrupo != null) {

                                        // Buscamos el grupo con el nombre obtenido en la vista
                                        Grupo g = grupojpa.findGrupoByNombreAndCompeticion(nombreGrupo,
                                                competicion.getId());
                                        // Comprobamos que el grupo existe
                                        if (g != null) {
                                            // Cambiamos el grupo
                                            equipo.setGrupoId(g);
                                        }else{
                                            throw new InputException("Grupo no encontrado");
                                        }
                                    }else{
                                        throw new InputException("Nombre de Grupo no válido");
                                    }
                                } else {
                                    throw new InputException("No se puede modificar el grupo de un equipo con miembros asignados");
                                }
                                // Guardamos los cambios en la base de datos
                                equipojpa.edit(equipo);

                            } catch (NonexistentEntityException ex) {
                                throw new InputException("Equipo no encontrado");
                            } catch (Exception ex) {
                                throw new InputException(ex.getMessage());
                            }
                        } else {
                            throw new InputException("Nombre de equipo ocupado");
                        }
                    } else {
                        throw new InputException("Nombre de equipo no válido");
                    }
                } else {
                    throw new InputException("Equipo no encontrado");
                }
                return equipo;
            } else {
                throw new InputException("Equipo no válido");
            }
        } else {
            throw new InputException("Competición no válida");
        }

    }
    
}
