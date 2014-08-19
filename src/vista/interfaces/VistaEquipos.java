package vista.interfaces;

import modelo.entities.RolUsuario;
import java.awt.event.ActionListener;
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
    
    /**Asocia un listener controlador a elementos de la vista
     * 
     * @param al ActionListener 
     */
    public void controlador(ActionListener al);
    
    /**Devuelve un string con el nombre del equipo
     * 
     * @return String
     */
    public String getNombreEquipo();
    
    /**Añade a la tabla de equipos una nueva fila
     * 
     * @param o Fila nueva
     */
    public void añadirEquipoATabla(Object[] o);
    
    /**Devuelve el modelo de la tabla de equipos
     * 
     * @return DefaultTableModel
     */
    public DefaultTableModel getModeloEquiposTable();
    
    /**Devuelve el identificador del equipo seleccionado
     * 
     * @return Integer
     */
    public Integer getEquipoSelected();

    /**Devuelve el ComboBox de grupos 
     * 
     * @return JComboBox
     */
    public JComboBox getGruposComboBox();
    
    /**Devuelve una cadena con el nombre del grupo seleccionado
     * 
     * @return String
     */
    public String getGrupo();
    
    /**
     * Elimina de la tabla de equipos la fila seleccionada
     */
    public void eliminarEquipoSeleccionado();
    
    /**
     * Limpia el formulario
     */
    public void limpiarFormularioEquipo();
    
    /**Establece en el campo nombre el nombre del equipo pasado como parámetro
     * 
     * @param nombre Nombre del equipo
     */
    public void setNombreEquipo(String nombre);
    
    /**Selecciona en el comboBox el grupo pasado como parámetro
     * 
     * @param grupo 
     */
    public void setGrupoDelEquipo(String grupo);
    
    /**Dependiendo del rol del Usuario habilita ciertos botones
     * 
     * @param rol RolUsuario
     */
    public void habilitarBotones(RolUsuario rol);
}
