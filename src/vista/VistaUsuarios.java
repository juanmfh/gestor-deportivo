package vista;

import java.awt.event.ActionListener;

/**
 *
 * @author JuanM
 */
public interface VistaUsuarios {
    
    /**Asocia un listener controlador a elementos de la vista
     * 
     * @param al ActionListener 
     */
    public void controlador(ActionListener al);
    
    /**Devuelve el identificador del usuario de la fila seleccionada
     * 
     * @return Integer 
     */
    public Integer getUsuarioSeleccionado();
    
}
