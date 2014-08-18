package modelo.dao;

import controlador.Coordinador;
import controlador.InputException;
import modelo.Competicion;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import modelo.Compuesta;
import java.util.ArrayList;
import java.util.Collection;
import modelo.Participante;
import modelo.Prueba;
import modelo.Registro;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import modelo.Grupo;
import modelo.TipoPrueba;
import modelo.TipoResultado;
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
        if (prueba.getParticipanteCollection() == null) {
            prueba.setParticipanteCollection(new ArrayList<Participante>());
        }
        if (prueba.getRegistroCollection() == null) {
            prueba.setRegistroCollection(new ArrayList<Registro>());
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
            Collection<Participante> attachedParticipanteCollection = new ArrayList<Participante>();
            for (Participante participanteCollectionParticipanteToAttach : prueba.getParticipanteCollection()) {
                participanteCollectionParticipanteToAttach = em.getReference(participanteCollectionParticipanteToAttach.getClass(), participanteCollectionParticipanteToAttach.getId());
                attachedParticipanteCollection.add(participanteCollectionParticipanteToAttach);
            }
            prueba.setParticipanteCollection(attachedParticipanteCollection);
            Collection<Registro> attachedRegistroCollection = new ArrayList<Registro>();
            for (Registro registroCollectionRegistroToAttach : prueba.getRegistroCollection()) {
                registroCollectionRegistroToAttach = em.getReference(registroCollectionRegistroToAttach.getClass(), registroCollectionRegistroToAttach.getId());
                attachedRegistroCollection.add(registroCollectionRegistroToAttach);
            }
            prueba.setRegistroCollection(attachedRegistroCollection);
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
            for (Participante participanteCollectionParticipante : prueba.getParticipanteCollection()) {
                Prueba oldPruebaasignadaOfParticipanteCollectionParticipante = participanteCollectionParticipante.getPruebaasignada();
                participanteCollectionParticipante.setPruebaasignada(prueba);
                participanteCollectionParticipante = em.merge(participanteCollectionParticipante);
                if (oldPruebaasignadaOfParticipanteCollectionParticipante != null) {
                    oldPruebaasignadaOfParticipanteCollectionParticipante.getParticipanteCollection().remove(participanteCollectionParticipante);
                    oldPruebaasignadaOfParticipanteCollectionParticipante = em.merge(oldPruebaasignadaOfParticipanteCollectionParticipante);
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
            Collection<Participante> participanteCollectionOld = persistentPrueba.getParticipanteCollection();
            Collection<Participante> participanteCollectionNew = prueba.getParticipanteCollection();
            Collection<Registro> registroCollectionOld = persistentPrueba.getRegistroCollection();
            Collection<Registro> registroCollectionNew = prueba.getRegistroCollection();
            List<String> illegalOrphanMessages = null;
            for (Compuesta compuestaCollectionOldCompuesta : compuestaCollectionOld) {
                if (!compuestaCollectionNew.contains(compuestaCollectionOldCompuesta)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Compuesta " + compuestaCollectionOldCompuesta + " since its pruebaId field is not nullable.");
                }
            }
            for (Participante participanteCollectionOldParticipante : participanteCollectionOld) {
                if (!participanteCollectionNew.contains(participanteCollectionOldParticipante)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Participante " + participanteCollectionOldParticipante + " since its pruebaasignada field is not nullable.");
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
            Collection<Participante> attachedParticipanteCollectionNew = new ArrayList<Participante>();
            for (Participante participanteCollectionNewParticipanteToAttach : participanteCollectionNew) {
                participanteCollectionNewParticipanteToAttach = em.getReference(participanteCollectionNewParticipanteToAttach.getClass(), participanteCollectionNewParticipanteToAttach.getId());
                attachedParticipanteCollectionNew.add(participanteCollectionNewParticipanteToAttach);
            }
            participanteCollectionNew = attachedParticipanteCollectionNew;
            prueba.setParticipanteCollection(participanteCollectionNew);
            Collection<Registro> attachedRegistroCollectionNew = new ArrayList<Registro>();
            for (Registro registroCollectionNewRegistroToAttach : registroCollectionNew) {
                registroCollectionNewRegistroToAttach = em.getReference(registroCollectionNewRegistroToAttach.getClass(), registroCollectionNewRegistroToAttach.getId());
                attachedRegistroCollectionNew.add(registroCollectionNewRegistroToAttach);
            }
            registroCollectionNew = attachedRegistroCollectionNew;
            prueba.setRegistroCollection(registroCollectionNew);
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
            for (Participante participanteCollectionNewParticipante : participanteCollectionNew) {
                if (!participanteCollectionOld.contains(participanteCollectionNewParticipante)) {
                    Prueba oldPruebaasignadaOfParticipanteCollectionNewParticipante = participanteCollectionNewParticipante.getPruebaasignada();
                    participanteCollectionNewParticipante.setPruebaasignada(prueba);
                    participanteCollectionNewParticipante = em.merge(participanteCollectionNewParticipante);
                    if (oldPruebaasignadaOfParticipanteCollectionNewParticipante != null && !oldPruebaasignadaOfParticipanteCollectionNewParticipante.equals(prueba)) {
                        oldPruebaasignadaOfParticipanteCollectionNewParticipante.getParticipanteCollection().remove(participanteCollectionNewParticipante);
                        oldPruebaasignadaOfParticipanteCollectionNewParticipante = em.merge(oldPruebaasignadaOfParticipanteCollectionNewParticipante);
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
            Collection<Participante> participanteCollectionOrphanCheck = prueba.getParticipanteCollection();
            for (Participante participanteCollectionOrphanCheckParticipante : participanteCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Prueba (" + prueba + ") cannot be destroyed since the Participante " + participanteCollectionOrphanCheckParticipante + " in its participanteCollection field has a non-nullable pruebaasignada field.");
            }
            Collection<Registro> registroCollectionOrphanCheck = prueba.getRegistroCollection();
            for (Registro registroCollectionOrphanCheckRegistro : registroCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Prueba (" + prueba + ") cannot be destroyed since the Registro " + registroCollectionOrphanCheckRegistro + " in its registroCollection field has a non-nullable pruebaId field.");
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
    
    // author: Juan María Frías Hidalgo
    
    /**Obtiene la lista de pruebas de una competición
     * 
     * @param c Objeto Competición
     * @return List<Prueba>
     */
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
    
    /**Obtiene la lista de pruebas existentes que no hay en una competición.
     * 
     * @param c     Objeto Competición
     * @return      List<Prueba>
     */
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
    
    /**Obtiene un objeto Prueba a partir del nombre de esta y de la competición
     * 
     * @param nombre            Nombre de la prueba
     * @param competicionid     Identificador de la competición
     * @return Prueba
     */
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
    
    /**Devuelve el número de registros que hay asociados a una prueba.
     * 
     * @param pruebaid      Identificador de la prueba.
     * @return int
     */
    public int countRegistrosByPrueba(Integer pruebaid){
        EntityManager em = getEntityManager();
        int res;
        try {
            Query q = em.createNamedQuery("Prueba.countRegistrosByPrueba");
            q.setParameter("pruebaid", pruebaid);
            res =  ((Number)q.getSingleResult()).intValue();
        } catch (NoResultException e) {
            return 0;
        } finally{
            em.close();
        }
        return res;
    }
    
    /**Obtiene una lista con los nombres de las pruebas de una competición
     * 
     * @param c Objeto Competición
     * @return List<String>
     */
    public List<String> findNombresPruebasByCompeticon(Competicion c) {
        EntityManager em = getEntityManager();
        List<String> res;
        if(c == null){
            return null;
        } else{
        try {
            Query q = em.createNamedQuery("Prueba.findNombresByCompeticionId");
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
    
    /**
     * Crea una prueba con los datos de la vista
     *
     * @param competicion Competicion donde se creará la prueba
     * @param nombre Nombre de la prueba
     * @param tipoResultado Tipo de resultado (Distancia, Tiempo, Numerica)
     * @param tipoPrueba Individual o Equipo
     * @return la Prueba creada
     * @throws controlador.InputException
     */
    public static Prueba crearPrueba(Competicion competicion, String nombre, String tipoPrueba,
            String tipoResultado) throws InputException {

        Prueba p = null;

        // Comprueba que el nombre de la prueba es no vacío
        if (nombre != null && nombre.length() > 0) {

            CompuestaJpa compujpa = new CompuestaJpa();
            PruebaJpa prujpa = new PruebaJpa();

            // Comprueba que el nombre de la prueba esté disponible
            // en la competicion seleccionada
            if (!existePrueba(nombre, competicion)) {

                if (tipoPrueba != null) {

                    if (tipoResultado != null) {

                        // Creamos una prueba con sus datos correspondientes
                        p = new Prueba();
                        p.setNombre(nombre);

                        TipoResultado tresultado;
                        TipoPrueba tprueba;
                        try {
                            tresultado = TipoResultado.valueOf(tipoResultado);
                        } catch (IllegalArgumentException ie) {
                            throw new InputException("Tipo de resultado no válido");
                        }
                        try {
                            tprueba = TipoPrueba.valueOf(tipoPrueba);
                        } catch (IllegalArgumentException ie) {
                            throw new InputException("Tipo de prueba no válido");
                        }
                        p.setTipo(tprueba.toString());
                        p.setTiporesultado(tresultado.toString());
                        prujpa.create(p);

                        // Asociamos la prueba con la competición
                        Compuesta compuesta = new Compuesta();
                        compuesta.setCompeticionId(competicion);
                        // El orden las pruebas no se utiliza actualmente
                        compuesta.setOrden(1);
                        compuesta.setPruebaId(p);
                        compujpa.create(compuesta);

                    } else {
                        throw new InputException("Tipo de resultado no válido");
                    }
                } else {
                    throw new InputException("Tipo de prueba no válido");
                }
            } else {
                throw new InputException("Nombre de prueba ocupado");
            }
        } else {
            throw new InputException("Nombre de prueba no válido");
        }
        return p;
    }

    public static Prueba modificarPrueba(Integer pruebaid, Integer competicionid, String nombrePrueba, TipoResultado tipoResultado, TipoPrueba tipoPrueba) throws InputException {

        if (pruebaid != null) {

            // Comprobamos que se ha seleccionado una prueba válida
            PruebaJpa pruebajpa = new PruebaJpa();
            Prueba prueba = pruebajpa.findPrueba(pruebaid);
            if (prueba != null) {

                if (competicionid != null) {

                    prueba = pruebajpa.findPruebaByNombreCompeticion(prueba.getNombre(), competicionid);

                    if (prueba != null) {

                        //Comprobamos que el nombre de la prueba es válido
                        if (nombrePrueba.length() > 0) {

                            // Comprobamos que el nombre de la prueba no esta cogido
                            Prueba pruebaMod = pruebajpa.findPruebaByNombreCompeticion(
                                    nombrePrueba.toString(),
                                    competicionid);
                            if (pruebaMod == null || pruebaMod.getId() == prueba.getId()) {

                                // Establecemos los atributos a partir de los datos de la vista
                                prueba.setNombre(nombrePrueba.toString());

                                if (tipoPrueba != null) {

                                    if (tipoResultado != null) {

                                        // Comprobamos que la prueba no tiene registros asocidados
                                        // En caso de tener registros no se podrá modificar 
                                        if (pruebajpa.countRegistrosByPrueba(pruebaid) <= 0) {
                                            prueba.setTiporesultado(tipoResultado.toString());
                                            prueba.setTipo(tipoPrueba.toString());

                                        } else {
                                            throw new InputException("No se puede modificar una prueba con registros asociados");
                                        }
                                        try {
                                            pruebajpa.edit(prueba);
                                        } catch (NonexistentEntityException ex) {
                                            throw new InputException("Prueba no encontrada");
                                        } catch (Exception ex) {
                                            throw new InputException(ex.getMessage());
                                        }
                                    } else {
                                        throw new InputException("Tipo de resultado no válido");
                                    }
                                } else {
                                    throw new InputException("Tipo de prueba no válido");
                                }
                            } else {
                                throw new InputException("Nombre de prueba ocupado");
                            }
                        } else {
                            throw new InputException("Nombre de prueba no válido");
                        }
                    } else {
                        throw new InputException("Prueba no registrada en la competición");
                    }
                } else {
                    throw new InputException("Competición no válida");
                }
            } else {
                throw new InputException("Prueba no encontrada");
            }
            return prueba;
        } else {
            throw new InputException("Prueba no válida");
        }

    }

    /**
     * Elimina una prueba de la competición pasada como parámetro
     *
     * @param pruebaid Identificador de la prueba a eliminar
     * @param competicionid Identificador de la competicion
     * @throws controlador.InputException
     */
    public static void eliminarPrueba(Integer pruebaid, Integer competicionid) throws InputException {

        CompuestaJpa compuestajpa = new CompuestaJpa();
        PruebaJpa pruebajpa = new PruebaJpa();
        RegistroJpa registrojpa = new RegistroJpa();

        if (pruebaid != null) {

            Prueba prueba = pruebajpa.findPrueba(pruebaid);

            if (competicionid != null) {

                if (prueba != null) {

                    // Eliminamos la prueba de la competición
                    Compuesta c = compuestajpa.findCompuestaByPrueba_Competicion(
                            pruebaid, competicionid);

                    if (c != null) {
                        try {
                            compuestajpa.destroy(c.getId());
                            // Eliminamos todos los registros de esa prueba
                            List<Registro> registros = registrojpa.findByPrueba(pruebaid);
                            for (Registro r : registros) {
                                registrojpa.destroy(r.getId());
                            }
                        } catch (NonexistentEntityException ex) {

                        }

                        // Modificamos los participantes que tienen asignado dicha prueba
                        ParticipanteJpa participanteJpa = new ParticipanteJpa();
                        GrupoJpa grupoJpa = new GrupoJpa();
                        List<Grupo> grupos = grupoJpa.findGruposByCompeticion(Coordinador.getInstance().getSeleccionada());
                        if (grupos != null) {
                            for (Grupo g : grupos) {
                                List<Participante> participantes = participanteJpa.findParticipantesByGrupoPruebaAsignada(g.getId(), prueba);
                                for (Participante participante : participantes) {
                                    participante.setPruebaasignada(null);
                                    try {
                                        participanteJpa.edit(participante);
                                    } catch (Exception ex) {

                                    }
                                }
                            }
                        }

                        try {
                            // Eliminamos la prueba
                            pruebajpa.destroy(pruebaid);
                        } catch (                modelo.dao.exceptions.NonexistentEntityException ex) {
                            throw new InputException(ex.getMessage());
                        } catch (                modelo.dao.exceptions.IllegalOrphanException ex) {
                            throw new InputException("Prueba no encontrada");
                        }

                    } else {
                        throw new InputException("Prueba no registrada en Competición");
                    }
                } else {
                    throw new InputException("Prueba no encontrada");
                }
            } else {
                throw new InputException("Identificador de competición no válido");
            }
        } else {
            throw new InputException("Identificador de prueba no válido");
        }
    }

    /**
     * Devuelve true si ya existe una prueba con ese nombre en la competicion
     *
     * @param nombre
     * @return true si el nombre de la prueba ya existe en la competicion
     */
    private static boolean existePrueba(String nombre, Competicion competicion) {
        PruebaJpa prujpa = new PruebaJpa();
        return prujpa.findPruebaByNombreCompeticion(
                nombre,
                competicion.getId()) != null;
    }
}
