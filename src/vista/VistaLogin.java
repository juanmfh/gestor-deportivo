package vista;

import java.awt.Color;
import java.awt.event.ActionListener;

/**
 *
 * @author JuanM
 */
public interface VistaLogin {
    
    String INICIARSESION = "iniciarsesion";
    
    /**Asocia un listener controlador a elementos de la vista
     * 
     * @param al ActionListener 
     */
    public void controlador(ActionListener al);
   
    /**Devuelve el nombre de usuario introducido en la vista
     * 
     * @return String
     */
    public String getUsuario();
    
    /**Devuelve la contraseña del usuario introducida en la vista
     * 
     * @return char[]
     */
    public char[] getContraseña();
    
    /**Pone un texto de estado en un determinado color en la vista
     * 
     * @param estado  Texto del mensaje
     * @param color   Color del mensaje
     */
    public void estado(String estado, Color color);
    
    
}
