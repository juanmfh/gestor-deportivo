package modelo.logicaNegocio;

import controlador.InputException;
import modelo.dao.AdministradoJpa;
import modelo.dao.exceptions.NonexistentEntityException;
import modelo.entities.Administrado;
import modelo.entities.Competicion;
import modelo.entities.Usuario;

/**
 *
 * @author JuanM
 */
public class AdministradoService {
   
    
      /**
     * Da permisos a un usuario para poder administrar una competición.
     *
     * @param competicion
     * @param usuario
     * @return Administrado
     */
    public static Administrado crearAdministrado(Competicion competicion, Usuario usuario) {

        Administrado administrado = null;
        if (competicion != null && usuario != null) {
            administrado = new Administrado();
            administrado.setUsuarioId(usuario);
            administrado.setCompeticionId(competicion);
            AdministradoJpa admjpa = new AdministradoJpa();
            admjpa.create(administrado);
        }
        return administrado;
    }
    
    /** Elimina un objeto Administrado a través de su identificador
     * 
     * @param administradoId    Identificador del objeto
     * @throws InputException 
     */
    public static void eliminarAdministrado(Integer administradoId) throws InputException{
        AdministradoJpa admjpa = new AdministradoJpa();
        try {
            admjpa.destroy(administradoId);
        } catch (NonexistentEntityException ex) {
            throw new InputException("No se ha podido eliminar el objeto");
        }
    }
    
    
}
