package vista.interfaces;

import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author JuanM
 */
public interface VistaParticipantes {
    
    String CREARPARTICIPANTE = "crear";
    
    String MODIFICARPARTICIPANTE = "modificar";
    
    String ELIMINARPARTICIPANTE = "eliminar";
    
    String LIMPIARPARTICIPANTE = "limpiar";
    
    String IMPORTAR = "importar";
    
    /**Asocia un listener controlador a elementos de la vista
     * 
     * @param al ActionListener 
     */
    public void controlador(ActionListener al);
    
    /**Devuelve el nombre del participante
     * 
     * @return String
     */
    public String getNombreParticipante();
    
    /**Devuelve una cadena con los apellidos del participante
     * 
     * @return String 
     */
    public String getApellidosParticipante();
    
    /**Devuelve la edad del participante
     * 
     * @return Integer
     */
    public Integer getEdadParticipante();
    
    /**Devuelve 0 si es masculino o 1 si es femenino
     * 
     * @return Integer
     */
    public Integer getSexoParticipante();
    
    /**Devuelve el nombre del grupo seleccionado
     * 
     * @return String 
     */
    public String getGrupoParticipante();
    
    /**Devuelve el nombre del equipo seleccionado
     * 
     * @return String
     */
    public String getEquipoParticipante();
    
    /**Devuelve el dorsal del participante
     * 
     * @return Integer
     */
    public Integer getDorsalParticipante();
    
    /**Devuelve el modelo de la tabla de participantes
     * 
     * @return DefaultTableModel
     */
    public DefaultTableModel getModeloParticipantesTable();
    
    /**Añade una fila a la tabla de participantes
     * 
     * @param o 
     */
    public void añadirParticipanteATabla(Object[] o);
    
    /**Devuelve el identificador del participante seleccionado, -1 en otro caso
     * 
     * @return Integer
     */
    public Integer getParticipanteSeleccionado();
    
    /**Elimina la fila seleccionada de la tabla de participantes
     * 
     */
    public void eliminarParticipanteDeTabla();
    
    /**
     * Limpia el formulario de añadir nuevos participantes
     */
    public void limpiarFormularioParticipante();
    
    /**Pone en el textfield correspondiente del formulario el nombre pasado como parámetro
     * 
     * @param nombre Nombre del participante
     */
    public void setNombreParticipante(String nombre);
    
    /**Pone en el textfield correspondiente del formulario el apellido pasado como parámetro
     * 
     * @param apellidos Apellidos del participante
     */
    public void setApellidosParticipante(String apellidos);
    
    /**Pone la edad pasada como parámetro en el textfield del formulario
     * 
     * @param edad Edad del participante
     */
    public void setEdadParticipante(Integer edad);
    
    /**Pone en el textfield correspondiente del formulario el dorsal pasado como parámetro
     * 
     * @param dorsal Dorsal del participante
     */
    public void setDorsalParticipante(Integer dorsal);
    
    /**Pone en el textfield correspondiente del formulario el nombre del grupo pasado como parámetro
     * 
     * @param grupo Nombre del grupo
     */
    public void setGrupoParticipante(String grupo);
    
    /**Pone en el textfield correspondiente del formulario el nombre del equipo pasado como parámetro
     * 
     * @param equipo Nombre del equipo
     */
    public void setEquipoParticipante(String equipo);
    
    /**Marca en el radioComboBox el sexo del participante
     * 
     * @param sexo 0 si es masculino, 1 si es femenino.
     */
    public void setSexoParticipante(Integer sexo);
    
    /**Devuelve el combobox de equipos 
     * 
     * @return JComboBox
     */
    public JComboBox getEquipoComboBox();
    
    /**Devuelve el combobox de prueba asignada
     * 
     * @return JComboBox
     */
    public JComboBox getPruebaComboBox();
    
    /**Devuelve la prueba seleccionada 
     * 
     * @return String
     */
    public String getPruebaAsignadaParticipante();
    
    /**Establece la prueba seleccionada en el combobox
     * 
     * @param prueba Nombre de la prueba 
     */
    public void setPruebaAsignadaParticipante(String prueba);
    
    /**
     * Quita la seleccion de la tabla de participantes
     */
    public void clearSelectionParticipante();
    
    /**
     * Devuelve el comboBox de grupos
     * @return JComboBox
     */
    public JComboBox getGrupoComboBox();
}
