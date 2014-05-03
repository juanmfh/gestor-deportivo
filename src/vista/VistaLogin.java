package vista;

import java.awt.Color;
import java.awt.event.ActionListener;

/**
 *
 * @author JuanM
 */
public interface VistaLogin {
    
    String INICIARSESION = "iniciarsesion";
   
    public String getUsuario();
    
    public char[] getContraseña();
    
    public void estado(String estado, Color color);
    
    public void controlador(ActionListener al);
}
