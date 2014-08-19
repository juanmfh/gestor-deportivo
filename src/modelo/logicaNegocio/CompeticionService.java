package modelo.logicaNegocio;

import controlador.InputException;
import java.util.Date;
import java.util.List;
import modelo.dao.AdministradoJpa;
import modelo.dao.CompeticionJpa;
import modelo.dao.CompuestaJpa;
import modelo.dao.GrupoJpa;
import modelo.dao.PruebaJpa;
import modelo.entities.Administrado;
import modelo.entities.Competicion;
import modelo.entities.Compuesta;
import modelo.entities.Grupo;

/**
 *
 * @author JuanM
 */
public class CompeticionService {
    
    
    /**
     * Crea una competición con los datos pasados como parámetros
     *
     * @param nombre nombre de la competición
     * @param lugar lugar de la competición.
     * @param fechaInicio fecha en la que comienza la competición.
     * @param fechaFin fecha en la que termina la competición.
     * @param nombreImagen nombre de la imagen que será su logo.
     * @param organizador organizador de la competición.
     * @return Competicion
     * @throws controlador.InputException
     */
    public static Competicion crearCompeticion(String nombre,
            String lugar,
            Date fechaInicio,
            Date fechaFin,
            String nombreImagen,
            String organizador) throws InputException {

        CompeticionJpa competicionjpa = new CompeticionJpa();

        // Se comprueba que el nombre es no vacío y que no hay una competición ya creada
        // con dicho nombre
        if (nombre != null && nombre.length() > 0) {
            if (competicionjpa.findCompeticionByName(nombre) == null) {
                Competicion competicion = new Competicion();
                competicion.setNombre(nombre);
                competicion.setCiudad(lugar);
                competicion.setFechainicio(fechaInicio);
                if(fechaInicio!=null && fechaFin!=null && fechaInicio.after(fechaFin)){
                    throw new InputException("La fecha de fin es posterior a la fecha de inicio");
                }
                competicion.setFechafin(fechaFin);
                competicion.setImagen(nombreImagen);
                competicion.setOrganizador(organizador);
                competicion.setPais(null);
                competicionjpa.create(competicion);
                return competicion;
            } else {
                throw new InputException("Nombre de competición ocupado");
            }
        } else {
            throw new InputException("Nombre de competición no válido");
        }
    }
    
    
    /**
     * Modifica y devuelve la competicion "competicion" con los datos pasados
     * como parámetros.
     *
     * @param competicion Objeto Competicion a modificar
     * @param nombre nombre de la competición
     * @param lugar lugar de la competición.
     * @param fechaInicio fecha en la que comienza la competición.
     * @param fechaFin fecha en la que termina la competición.
     * @param nombreImagen nombre de la imagen que será su logo.
     * @param organizador organizador de la competición.
     * @return Competicion
     * @throws controlador.InputException
     */
    public static Competicion modificarCompeticion(Competicion competicion,
            String nombre,
            String lugar,
            Date fechaInicio,
            Date fechaFin,
            String nombreImagen,
            String organizador) throws InputException {

        CompeticionJpa competicionjpa = new CompeticionJpa();

        // Se comprueba que el nombre tenga una longitud > 0
        if (nombre != null && nombre.length() > 0) {

            Competicion c = competicionjpa.findCompeticionByName(nombre);
            if (c == null || c.equals(competicion)) {
                competicion.setNombre(nombre);
                competicion.setOrganizador(organizador);
                competicion.setCiudad(lugar);
                if(fechaInicio!=null && fechaFin!=null && fechaInicio.after(fechaFin)){
                    throw new InputException("La fecha de fin es posterior a la fecha de inicio");
                }
                competicion.setFechainicio(fechaInicio);
                competicion.setFechafin(fechaFin);
                competicion.setImagen(nombreImagen);
                try {
                    // Cargamos la modificación en la base de datos
                    competicionjpa.edit(competicion);
                } catch (modelo.dao.exceptions.NonexistentEntityException ex) {
                    throw new InputException("Competición no encontrada");
                } catch (Exception ex) {
                    throw new InputException(ex.getMessage());
                }
            } else {
                throw new InputException("Nombre de competición ocupado");
            }
        } else {
            throw new InputException("Nombre de competición no válido");
        }
        return competicion;
    }

    /**
     * Elimina la Competicion y todo sus datos (grupos, participantes,
     * resultados) dado su nombre
     *
     * @param nombreCompeticion Nombre de la competicion
     * @throws controlador.InputException
     */
    public static void eliminarCompeticion(String nombreCompeticion) throws InputException {

        CompeticionJpa competicionjpa = new CompeticionJpa();
        // Obtenemos la competicion a partir de su nombre
        Competicion c = competicionjpa.findCompeticionByName(nombreCompeticion);

        // Se comprueba que la competición existe
        if (c != null) {
            CompuestaJpa compuestajpa = new CompuestaJpa();
            List<Compuesta> compuesta = compuestajpa.findCompuestaByCompeticion(c.getId());

            AdministradoJpa admjpa = new AdministradoJpa();
            List<Administrado> administra = admjpa.findAdministradoByCompeticion(c);

            try {
                // Eliminamos los grupos
                GrupoJpa grupojpa = new GrupoJpa();
                List<Grupo> grupos = grupojpa.findGruposRaizByCompeticion(c);
                for (Grupo grupo : grupos) {
                    GrupoService.eliminarGrupo(c, grupo.getId());
                }
                
                // Eliminamos los permisos de acceso
                for (Administrado temp : administra) {
                    admjpa.destroy(temp.getId());
                }

                // Eliminamos las pruebas
                PruebaJpa pruebajpa = new PruebaJpa();
                for (Compuesta comp : compuesta) {
                    compuestajpa.destroy(comp.getId());
                    pruebajpa.destroy(comp.getPruebaId().getId());
                }

                // Eliminamos la competicion
                competicionjpa.destroy(c.getId());
            } catch (modelo.dao.exceptions.IllegalOrphanException | modelo.dao.exceptions.NonexistentEntityException e) {
                throw new InputException("No se pudo eliminar algún dato de la competición seleccionada");
            }
        } else {
            throw new InputException("Competición no encontrada");
        }
    }
    
}
