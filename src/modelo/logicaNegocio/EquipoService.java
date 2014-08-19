/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package modelo.logicaNegocio;

import controlador.InputException;
import modelo.dao.EquipoJpa;
import modelo.dao.GrupoJpa;
import modelo.dao.exceptions.NonexistentEntityException;
import modelo.entities.Competicion;
import modelo.entities.Equipo;
import modelo.entities.Grupo;

/**
 *
 * @author JuanM
 */
public class EquipoService {
    
    
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
