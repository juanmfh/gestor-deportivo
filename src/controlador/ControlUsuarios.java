package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import vista.VistaUsuarios;

/**
 *
 * @author JuanM
 */
public class ControlUsuarios implements ActionListener {
    
    private VistaUsuarios vista;

    /**Constructor que asocia la vista al controlador
     * 
     * @param vista Vista del controlador (Interfaz)
     */
    public ControlUsuarios(VistaUsuarios vista) {
        this.vista = vista;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        
    }
    
}
