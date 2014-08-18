package modelo.dao;

import controlador.InputException;
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
import modelo.Competicion;
import modelo.Inscripcion;
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
    
    // author: Juan María Frías Hidalgo
    
    /**Devuelve la lista de participantes de una competicion
     * 
     * @param competicionid       Identificador de la competicion
     * @return List<Participante>
     */
    /*public List<Participante> findParticipantesByCompeticion(Integer competicionid) {
        EntityManager em = getEntityManager();
        List<Participante> res;
        try {
            Query q = em.createNamedQuery("Participante.findByCompeticion");
            q.setParameter("competicionid", competicionid);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }*/
    

    /**Devuelve la lista de participantes de un grupo determinado
     * 
     * @param grupoid       Identificador del grupo
     * @return List<Participante>
     */
    public List<Participante> findParticipantesByGrupo(Integer grupoid) {
        EntityManager em = getEntityManager();
        List<Participante> res;
        try {
            Query q = em.createNamedQuery("Participante.findByGrupo");
            q.setParameter("grupoid", grupoid);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    /**Devuelve la lista de participantes de un grupo determinado que tienen
     * asignados una prueba determinada
     * 
     * @param grupoid       Identificador del grupo
     * @param prueba        Prueba asignada
     * @return List<Participante>
     */
    public List<Participante> findParticipantesByGrupoPruebaAsignada(Integer grupoid,Prueba prueba) {
        EntityManager em = getEntityManager();
        List<Participante> res;
        try {
            Query q = em.createNamedQuery("Participante.findByGrupoPruebaAsignada");
            q.setParameter("grupoid", grupoid);
            q.setParameter("pruebaAsignada", prueba);
            res = q.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return res;
    }
    
    /**Devuelve la lista de participantes miembros de un equipo
     * 
     * @param equipoid  Identificador del equipo
     * @return List<Participante>
     */
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
    
    /**Devuelve al participante a partir de un dorsal y una competición, null
     * en caso de que no se encuentre.
     * 
     * @param dorsal            Dorsal del participante
     * @param competicionid     Identificador de la competición
     * @return Participante
     */
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
    
    /**
     * Crea un participante nuevo
     *
     * @param competicion
     * @param nombre Nombre del participante
     * @param apellidos Apellidos del participante
     * @param dorsal Dorsal del participante. Debe de ser único en la
     * competicion
     * @param nombreGrupo Nombre del grupo al que pertenece
     * @param edad Edad del participante
     * @param sexo Sexo del participante (1 == Hombre, 0 == Mujer)
     * @param nombreEquipo Nombre del equipo del que es miembro o "Ninguno"
     * @param pruebaAsignada Nombre de la prueba asignada o "Ninguno"
     * @return Participante si ha sido correctamente creado correctamente, null
     * en otro caso
     * @throws controlador.InputException
     */
    public static Participante crearParticipante(Competicion competicion, String nombre, String apellidos,
            Integer dorsal, String nombreGrupo, Integer edad, Integer sexo,
            String nombreEquipo, String pruebaAsignada) throws InputException {

        if (competicion != null) {
            if (nombre != null && nombre.length() > 0) {
                if (apellidos != null && apellidos.length() > 0) {
                    if (nombreGrupo != null && nombreGrupo.length() > 0) {
                        Participante participante = null;
                        if (dorsal != null) {
                            if (dorsalLibre(dorsal, competicion)) {
                                ParticipanteJpa participantejpa = new ParticipanteJpa();
                                GrupoJpa grupojpa = new GrupoJpa();

                                // Creamos un objeto Participante y establecemos sus atributos
                                participante = new Participante();

                                // Buscamos el grupo por el nombre
                                Grupo g = grupojpa.findGrupoByNombreAndCompeticion(nombreGrupo,
                                        competicion.getId());

                                if (g != null) {

                                    participante.setNombre(nombre);
                                    participante.setApellidos(apellidos);
                                    participante.setDorsal(dorsal);
                                    participante.setEdad(edad);
                                    participante.setSexo(sexo);
                                    participante.setGrupoId(g);

                                    // Si se ha seleccionado alguna prueba se la asignamos al participante
                                    if (pruebaAsignada != null && !pruebaAsignada.equals("Ninguna")) {
                                        PruebaJpa pruebajpa = new PruebaJpa();
                                        Prueba p = pruebajpa.findPruebaByNombreCompeticion(pruebaAsignada,
                                                competicion.getId());
                                        if (p != null) {
                                            participante.setPruebaasignada(p);
                                        } else {
                                            throw new InputException("Prueba no encontrada");
                                        }
                                    }

                                    // Si se ha seleccionado un equipo
                                    if (nombreEquipo != null && !nombreEquipo.equals("Ninguno")) {
                                        EquipoJpa equipojpa = new EquipoJpa();
                                        Equipo equipo = equipojpa.findByNombreAndCompeticion(
                                                nombreEquipo.toString(),
                                                competicion.getId());
                                        if (equipo != null) {
                                            participante.setEquipoId(equipo);
                                        } else {
                                            throw new InputException("Equipo no encontrado");
                                        }
                                    }
                                    participantejpa.create(participante);
                                } else {
                                    throw new InputException("Grupo no encontrado");
                                }
                            } else {
                                throw new InputException("Dorsal ocupado");
                            }
                        } else {
                            throw new InputException("Dorsal no válido");
                        }
                        return participante;
                    } else {
                        throw new InputException("Nombre de grupo no válido");
                    }
                } else {
                    throw new InputException("Apellidos no válido");
                }
            } else {
                throw new InputException("Nombre no válido");
            }
        } else {
            throw new InputException("Competición no válida");
        }
    }

    /**
     * Elimina al participante cuyo id es "participanteid"
     *
     * @param participanteid Id del participante
     * @throws controlador.InputException
     */
    public static void eliminarParticipante(Integer participanteid) throws InputException {

        // Comprobamos que el id es válido
        if (participanteid != null) {

            ParticipanteJpa participantejpa = new ParticipanteJpa();

            //Buscamos el participante a partid del ID
            Participante participante = participantejpa.findParticipante(participanteid);

            if (participante != null) {
                eliminarRegistros(participante);
                try {
                    participantejpa.destroy(participante.getId());
                } catch (NonexistentEntityException ex) {
                    throw new InputException("Participante no encontrado");
                }
            } else {
                throw new InputException("Participante no encontrado");
            }
        } else {
            throw new InputException("Participante no válido");
        }
    }

    /**
     * Modifica los datos del participante cuyo id es "participanteid"
     *
     * @param competicion
     * @param participanteid Identificador del participante a modificar
     * @param nombre Nombre del participante
     * @param apellidos Apellidos del participante
     * @param dorsal Dorsal del participante
     * @param nombreGrupo Nombre del grupo al que pertence el participante
     * @param edad Edad del participante
     * @param sexo Sexo del participante, 0=Hombre, 1=Mujer
     * @param nombreEquipo Nombre del equipo al que pertenece el participante
     * @param pruebaAsignada Nombre de la prueba asignada al participante
     * @return el Participante modificado
     * @throws controlador.InputException
     */
    public static Participante modificarParticipante(Competicion competicion, Integer participanteid, String nombre, String apellidos,
            Integer dorsal, String nombreGrupo, Integer edad, Integer sexo,
            String nombreEquipo, String pruebaAsignada) throws InputException {

        if (competicion != null) {
            if (participanteid != null) {
                if (nombre != null && nombre.length() > 0) {
                    if (apellidos != null && nombre.length() > 0) {
                        if (nombreGrupo != null && nombre.length() > 0) {
                            Participante participante = null;
                            if (dorsal != null) {
                                if (dorsalLibreOMio(competicion, dorsal, participanteid)) {
                                    ParticipanteJpa participantejpa = new ParticipanteJpa();
                                    GrupoJpa grupojpa = new GrupoJpa();

                                    try {
                                        participante = participantejpa.findParticipante(participanteid);

                                        if (participante != null) {
                                            participante.setNombre(nombre);
                                            participante.setApellidos(apellidos);
                                            participante.setDorsal(dorsal);
                                            participante.setEdad(edad);
                                            participante.setSexo(sexo);

                                            // Buscamos el grupo por el nombre
                                            Grupo g = grupojpa.findGrupoByNombreAndCompeticion(nombreGrupo, competicion.getId());

                                            if (g != null) {
                                                // Cambia el grupo al que pertenece el participante
                                                participante.setGrupoId(g);

                                                // Si el participante tiene registros, se cambia la inscripción del registro
                                                RegistroJpa registrojpa = new RegistroJpa();
                                                List<Registro> registros = registrojpa.findByParticipante(participanteid);
                                                if (registros != null) {
                                                    InscripcionJpa inscripcionjpa = new InscripcionJpa();
                                                    Inscripcion inscripcion = inscripcionjpa.findInscripcionByCompeticionByGrupo(competicion.getId(),
                                                            g.getId());
                                                    for (Registro r : registros) {
                                                        r.setInscripcionId(inscripcion);
                                                        registrojpa.edit(r);
                                                    }
                                                }

                                                // Si se ha seleccionado una prueba
                                                if (pruebaAsignada != null && !pruebaAsignada.equals("Ninguna")) {
                                                    PruebaJpa pruebajpa = new PruebaJpa();
                                                    Prueba p = pruebajpa.findPruebaByNombreCompeticion(pruebaAsignada,
                                                            competicion.getId());
                                                    if (p != null) {
                                                        participante.setPruebaasignada(p);
                                                    } else {
                                                        throw new InputException("Prueba no encontrada");
                                                    }
                                                } else {
                                                    participante.setPruebaasignada(null);
                                                }

                                                // Si se ha seleccionado un equipo
                                                if (nombreEquipo != null && !nombreEquipo.equals("Ninguno")) {
                                                    // Buscamos el equipo y ponemos al participante como miembro
                                                    EquipoJpa equipojpa = new EquipoJpa();
                                                    Equipo equiponuevo = equipojpa.findByNombreAndCompeticion(nombreEquipo, competicion.getId());
                                                    if (equiponuevo != null) {
                                                        participante.setEquipoId(equiponuevo);
                                                    } else {
                                                        throw new InputException("Equipo no encontrado");
                                                    }
                                                } else {
                                                    participante.setEquipoId(null);
                                                }
                                                participantejpa.edit(participante);
                                            } else {
                                                throw new InputException("Grupo no encontrado");
                                            }
                                        } else {
                                            throw new InputException("Participante no encontrado");
                                        }
                                    } catch (NonexistentEntityException ex) {
                                        throw new InputException("Participante no encontrado");
                                    } catch (Exception ex) {
                                        throw new InputException(ex.getMessage());
                                    }
                                    return participante;

                                } else {
                                    throw new InputException("Dorsal ocupado");
                                }
                            } else {
                                throw new InputException("Dorsal no válido");
                            }
                        } else {
                            throw new InputException("Nombre de grupo no válido");
                        }
                    } else {
                        throw new InputException("Apellidos no válido");
                    }
                } else {
                    throw new InputException("Nombre no válido");
                }
            } else {
                throw new InputException("Participante no válido");
            }
        } else {
            throw new InputException("Competición no válida");
        }
    }

    /**
     * Elimina los registros de un participante
     *
     * @param participante Participante asociado a esos registros
     */
    private static void eliminarRegistros(Participante participante) throws InputException {

        RegistroJpa registrosjpa = new RegistroJpa();
        // Buscamos todos los registros de "participante"
        List<Registro> registros = registrosjpa.findByParticipante(participante.getId());
        try {
            // Eliminamos cada registro 
            for (Registro r : registros) {
                registrosjpa.destroy(r.getId());
            }
        } catch (NonexistentEntityException ex) {
            throw new InputException("Registro no encontrado");
        }
    }

    /**
     * Comprueba que el dorsal "dorsal" no esta ocupado en la competicion
     * seleccionada
     *
     * @param competicion Objeto Competicion
     * @param dorsal Numero del dorsal
     * @return true si el dorsal no está ocupado
     */
    public static boolean dorsalLibre(Integer dorsal, Competicion competicion) {
        ParticipanteJpa participantejpa = new ParticipanteJpa();
        return (participantejpa.findByDorsalAndCompeticion(dorsal,
                competicion.getId())) == null;
    }

    /**
     * Comprueba que el dorsal esté disponible o que lo tenga el propio
     * participante que lo solicita
     *
     * @param dorsal Dorsal del participante
     * @param participanteid identificador del participante
     * @return true si el dorsal está libre o es el del participante cuyo id es
     * participanteid
     */
    private static boolean dorsalLibreOMio(Competicion c, Integer dorsal, Integer participanteid) {
        ParticipanteJpa participantejpa = new ParticipanteJpa();
        Participante participante = participantejpa.findByDorsalAndCompeticion(dorsal,
                c.getId());
        return (participante == null || participante.getId() == participanteid);
    }
    
}
