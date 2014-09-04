package modelo.logicaNegocio;

import modelo.dao.AdministradoJpa;
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
    
    
}
