package vista;

import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author JuanM
 */
public interface VistaGrupos {
    
    String CREARGRUPO = "crear";
    
    String MODIFICARGRUPO = "modificar";
    
    String ELIMINARGRUPO = "eliminar";
    
    String LIMPIAR = "limpiar";
    
    /**Asocia un listener controlador a elementos de la vista
     * 
     * @param al ActionListener 
     */
    public void controlador(ActionListener al);
    
    /**Devuelve el ComboBox de sub-grupos 
     * 
     * @return JComboBox
     */
    public JComboBox getSubgrupoDeComboBox();
    
    /**Devuelve el nombre del grupo
     * 
     * @return String
     */
    public String getNombreGrupo();
    
    /**Añade una nueva fila a la tabla de grupos
     * 
     * @param o Nueva fila 
     */
    public void añadirGrupoATabla(Object[] o);
    
    /**Devuelve el modelo de la tabla de grupos
     * 
     * @return DefaultTableModel
     */
    public DefaultTableModel getModeloGruposTable();
    
    /**Devuelve el identificador del grupo seleccionado
     * 
     * @return Integer 
     */
    public Integer getGrupoSelected();
    
    /**
     * Limpia el formulario de creación de grupos
     */
    public void limpiarFormularioGrupo();
    
    /**
     * Modifica el textfield con el nombre del grupo en la vista
     * @param nombre Nombre del grupo
     */
    public void setNombreGrupo(String nombre);

    /**Selecciona el grupo pasado como parámetro en la vista
     * 
     * @param nombre Nombre del grupo
     */
    public void setSubGrupoDe(String nombre);
}
