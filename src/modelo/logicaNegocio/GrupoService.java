
package modelo.logicaNegocio;

import controlador.ControlPrincipal;
import controlador.InputException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.dao.EquipoJpa;
import modelo.dao.GrupoJpa;
import modelo.dao.InscripcionJpa;
import modelo.dao.ParticipanteJpa;
import modelo.dao.RegistroJpa;
import modelo.dao.exceptions.IllegalOrphanException;
import modelo.dao.exceptions.NonexistentEntityException;
import modelo.entities.Competicion;
import modelo.entities.Equipo;
import modelo.entities.Grupo;
import modelo.entities.Inscripcion;
import modelo.entities.Participante;
import modelo.entities.Registro;

/**
 *
 * @author JuanM
 */
public class GrupoService {
    
    
    
    /**
     * Crea un grupo cuyo nombre debe de ser único en la competición
     *
     * @param competicion
     * @param nombre Nombre del grupo
     * @param subGrupoDe Grupo superior (null si no pertenece a ningún grupo o
     * cadena "Ninguno")
     * @return Grupo creado
     * @throws controlador.InputException
     */
    public static Grupo crearGrupo(Competicion competicion, String nombre, String subGrupoDe) throws InputException {

        if (competicion != null) {

            // Comprobamos el nombre del grupo 
            if (nombre != null && nombre.length() > 0) {

                GrupoJpa grupojpa = new GrupoJpa();
                InscripcionJpa inscripcionjpa = new InscripcionJpa();

                // Comprobamos que en la competición no haya ningún grupo con el mismo nombre            
                if (inscripcionjpa.findInscripcionByCompeticionByNombreGrupo(competicion.getId(), nombre) == null) {

                    // Establecemos el nombre del grupo
                    Grupo g = new Grupo();
                    g.setNombre(nombre);

                    // Establecemos el subgrupo al cual pertenece
                    if (subGrupoDe != null && !subGrupoDe.equals("Ninguno")) {
                        Grupo aux = grupojpa.findGrupoByNombreAndCompeticion(subGrupoDe, competicion.getId());
                        if (aux == null || aux.getNombre().equals(nombre)) {
                            throw new InputException("Campo Subgrupo De no válido");
                        } else {
                            g.setGrupoId(aux);
                        }
                    }

                    // Creamos el grupo
                    grupojpa.create(g);

                    // Inscribimos el grupo en la competición
                    Inscripcion i = new Inscripcion();
                    i.setCompeticionId(competicion);
                    i.setGrupoId(g);
                    inscripcionjpa.create(i);

                    return g;
                } else {
                    throw new InputException("Nombre de Grupo ocupado");
                }
            } else {
                throw new InputException("Nombre de Grupo no válido");
            }
        } else {
            throw new InputException("Competición no válida");
        }
    }

    /**
     * Modifica el grupo cuyo id es grupoid
     *
     * @param competicion
     * @param grupoid Identificador del grupo a modificar
     * @param nombreGrupo
     * @param nombreSubGrupoDe
     * @return Grupo modificado
     * @throws controlador.InputException
     */
    public static Grupo modificarGrupo(Competicion competicion, Integer grupoid, String nombreGrupo, String nombreSubGrupoDe) throws InputException {

        if (competicion != null) {
            if (grupoid != null) {
                GrupoJpa grupojpa = new GrupoJpa();
                Grupo g = grupojpa.findGrupo(grupoid);

                // Comprobamos que el grupo existe
                if (g != null) {
                    if (nombreGrupo!= null && nombreGrupo.length() > 0) {
                        Grupo antiguo = grupojpa.findGrupoByNombreAndCompeticion(nombreGrupo, competicion.getId());

                        // Compbrobamos que el nombre no está ocupado por otro grupo
                        // en la misma competición
                        if (antiguo == null || antiguo.getId() == g.getId()) {

                            // Modificamos el nombre
                            g.setNombre(nombreGrupo);

                            //Comprobamos que se puede modificar el campo subGrupo
                            if (nombreSubGrupoDe != null && !nombreSubGrupoDe.equals("Ninguno")) {
                                Grupo grupoSuperior = grupojpa.findGrupoByNombreAndCompeticion(nombreSubGrupoDe, competicion.getId());
                                if (grupoSuperior != null) {
                                        if(!esHijoDe(grupoSuperior,g)){
                                            g.setGrupoId(grupoSuperior);
                                        }else{
                                            throw new InputException("Un grupo no puede ser subGrupo de un subGrupo suyo");
                                        }
                                }else{
                                    throw new InputException("Campo Subgrupo De no válido");
                                }
                            } else {
                                g.setGrupoId(null);
                            }
                            //Editamos el subgrupo

                            try {
                                //Editamos el grupo
                                grupojpa.edit(g);
                            } catch (NonexistentEntityException ex) {
                                throw new InputException("No se ha encontrado el grupo");
                            } catch (Exception ex) {
                                throw new InputException(ex.getMessage());
                            }
                        } else {
                            throw new InputException("Nombre de grupo ocupado");
                        }
                    } else {
                        throw new InputException("Nombre de grupo no válido");
                    }
                } else {
                    throw new InputException("Grupo no encontrado");
                }
                return g;
            } else {
                throw new InputException("Identificador de grupo no válido");
            }
        } else {
            throw new InputException("Competición no válida");
        }
    }

    /**
     * Elimina al grupo, participantes del grupo y registros de los
     * participantes cuyo id es grupoid
     *
     * @param c
     * @param grupoid Id del grupo a eliminar
     * @throws controlador.InputException
     */
    public static void eliminarGrupo(Competicion c, Integer grupoid) throws InputException {

        if (c != null) {
            if (grupoid != null) {

                GrupoJpa grupojpa = new GrupoJpa();
                Grupo g = grupojpa.findGrupo(grupoid);

                // Comprobamos que el grupo es válido
                if (g != null) {

                    // Obtenemos la lista de subgrupos y los eliminamos
                    List<Grupo> subgrupos = grupojpa.findGrupoByGrupoId(g.getId());
                    for (Grupo subgrupo : subgrupos) {
                        eliminarGrupo(c, subgrupo.getId());
                    }
                    try {
                        // Elimiina las inscripciones
                        eliminarInscripciones(c, g);
                        // Elimina los equipos
                        eliminarEquiposGrupo(g);
                        // Eliminamos los participantes
                        eliminarParticipantes(g);
                        // Eliminamos el grupo
                        grupojpa.destroy(g.getId());
                    } catch (IllegalOrphanException | NonexistentEntityException | InputException ex) {
                        throw new InputException(ex.getMessage());
                    }
                } else {
                    throw new InputException("Grupo no encontrado");
                }
            } else {
                throw new InputException("Identificador de grupo no válido");
            }
        } else {
            throw new InputException("Competición no válida");
        }
    }

    private static void eliminarInscripciones(Competicion c, Grupo g) throws InputException {
        try {
            InscripcionJpa inscrjpa = new InscripcionJpa();
            RegistroJpa registrosjpa = new RegistroJpa();
            Inscripcion i = inscrjpa.findInscripcionByCompeticionByGrupo(c.getId(), g.getId());
            if (i != null) {
                // Obtenemos los registros de este grupo y los eliminamos
                List<Registro> registros = registrosjpa.findByInscripcion(i.getId());
                if (registros != null) {
                    for (Registro r : registros) {
                        registrosjpa.destroy(r.getId());
                    }
                }
                // Eliminamos la inscripción en este grupo
                inscrjpa.destroy(i.getId());
            }
        } catch (NonexistentEntityException | IllegalOrphanException ex) {
            throw new InputException(ex.getMessage());
        }
    }

    /**
     * Elimina los participantes de un grupo g
     *
     * @param g Grupo al que pertenecen los participantes
     */
    private static void eliminarParticipantes(Grupo g) throws InputException {

        ParticipanteJpa participantejpa = new ParticipanteJpa();
        RegistroJpa registrosjpa = new RegistroJpa();

        List<Participante> participantes = participantejpa.findParticipantesByGrupo(g.getId());
        try {
            for (Participante participante : participantes) {
                // Eliminamos sus registros
                List<Registro> registros = registrosjpa.findByParticipante(participante.getId());
                for (Registro r : registros) {
                    registrosjpa.destroy(r.getId());
                }
                // Eliminamos el participante
                participantejpa.destroy(participante.getId());
            }
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(ControlPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Elimina todos los equipos de un grupo g (No sus participantes)
     *
     * @param g Grupo al que pertenecen los equipos
     * @throws controlador.InputException
     */
    private static void eliminarEquiposGrupo(Grupo g) throws InputException {

        EquipoJpa equipojpa = new EquipoJpa();
        RegistroJpa registrosjpa = new RegistroJpa();

        // Obtenemos la lista de equipos que participan en el grupo g
        List<Equipo> equipos = equipojpa.findByGrupo(g.getId());
        try {
            // Por cada equipo
            for (Equipo e : equipos) {
                // Eliminamos sus registros
                List<Registro> registros = registrosjpa.findByEquipo(e.getId());
                for (Registro r : registros) {
                    registrosjpa.destroy(r.getId());
                }
                // Eliminamos al equipo
                equipojpa.destroy(e.getId());
            }
        } catch (NonexistentEntityException ex) {
            throw new InputException("Equipo no encontrado");
        }
    }

    public static List<Grupo> getSubGrupos(Grupo grupo) {
        List<Grupo> res = new CopyOnWriteArrayList();
        GrupoJpa grupoJpa = new GrupoJpa();
        List<Grupo> aux = grupoJpa.findGrupoByGrupoId(grupo.getId());
        for (Grupo g1 : aux) {
            res.add(g1);
        }
        for (Grupo g : res) {
            List<Grupo> aux2 = getSubGrupos(g);
            for (Grupo g2 : aux2) {
                res.add(g2);
            }
        }
        return res;

    }

    private static boolean esHijoDe(Grupo g, Grupo grupoPadre) {
        List<Grupo> hijos = getSubGrupos(grupoPadre);
        return hijos.contains(g) || g.equals(grupoPadre);
    }
}
