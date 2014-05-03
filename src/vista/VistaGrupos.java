/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package vista;

import java.awt.event.ActionListener;

/**
 *
 * @author JuanM
 */
public interface VistaGrupos {
    
    String CREARGRUPO = "crear";
    
    String MODIFICARGRUPO = "modificar";
    
    String ELIMINARGRUPO = "eliminar";
    
    String LIMPIAR = "limpiar";
    
    public void controlador(ActionListener al);
    
}
