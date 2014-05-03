/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package vista;

import java.awt.event.ActionListener;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author JuanM
 */
public interface VistaEquipos {
    
    String AÑADIREQUIPO = "añadirequipo";
    
    String ELIMINAREQUIPO = "eliminarequipo";
    
    String MODIFICAREQUIPO = "modificarequipo";
    
    String LIMPIAR = "limpiar";
    
    public void controlador(ActionListener al);
    
    public String getNombreEquipo();
    
    public void añadirEquipoATabla(Object[] o);
    
    public DefaultTableModel getModeloEquiposTable();
    
    public Integer getEquipoSelected();
    
    public ComboBoxModel getGruposModel();

    public JComboBox getGruposComboBox();
    
    public String getGrupo();
    
    public void eliminarEquipoSeleccionado();
    
    public void limpiarFormularioEquipo();
}
