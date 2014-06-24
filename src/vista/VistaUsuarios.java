package vista;

import controlador.RolUsuario;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author JuanM
 */
public interface VistaUsuarios {
    
    String CREARUSUARIO = "crearusuario";
    
    String MODIFICARUSUARIO = "modificarusuario";
    
    String ELIMINARUSUARIO = "eliminarusuario";
    
    String LIMPIARFORMULARIO = "limpiarformulario";
    
    String INCLUIRCOMPETICION = "incluircompeticion";
    
    String EXCLUIRCOMPETICION = "excluircompeticion";
    
    
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
    
    /**Devuelve el modelo de la tabla de usuarios
     * 
     * @return DefaultTableModel
     */
    public DefaultTableModel getModeloUsuariosTable();
    
    /**
     * Limpia los datos del formulario de usuarios
     */
    public void limpiarFormularioUsuario();
    
    /**Devuelve la lista con el nombre de las competiciones seleccionadas de la primera lista
     * 
     * @return List<String>
     */
    public List<String> getCompeticionesSeleccionadas();
    
    /**Devuelve la lista con el nombre de las competiciones seleccionadas de la segunda lista
     * 
     * @return List<String>
     */
    public List<String> getCompeticionesConAccesoSeleccionadas();
    
    /**Añade a la lista de competiciones el nombre pasado como parámetro
     * 
     * @param nombreCompeticion 
     */
    public void añadirCompeticion(String nombreCompeticion);
    
    /**Elimina de la lista de competiciones el nombre pasado como parámetro
     * 
     * @param nombreCompeticion 
     */
    public void eliminarCompeticion(String nombreCompeticion);
    
    /**
     * Elimina todas las competiciones de la lista de competiciones
     */
    public void eliminarTodasCompeticiones();
    
    
    /**
     * Elimina todas las competiciones de la lista de competiciones con acceso
     */
    public void eliminarTodasCompeticionesConAcceso();
    
     /**Añade a la lista de competiciones con acceso el nombre pasado como parámetro
     * 
     * @param nombreCompeticion 
     */
    public void añadirCompeticionConAcceso(String nombreCompeticion);
    
    /**Elimina de la lista de competiciones con acceso el nombre pasado como parámetro
     * 
     * @param nombreCompeticion 
     */
    public void eliminarCompeticionConAcceso(String nombreCompeticion);
    
    
    /**Devuelve el nombre de usuario introducido en la vista
     * 
     * @return String
     */
    public String getNombreDeUsuario();
    
    /**Devuelve la contraseña introducida en la vista
     * 
     * @return char[] 
     */
    public char[] getContraseña();
    
    /**Devuelve un enumerado del tipo RolUsuario según lo seleccionado en la vista
     * 
     * @return RolUsuario
     */
    public RolUsuario getRol();
    
    /**Añade una nueva fila a la tabla de usuarios
     * 
     * @param o Nueva fila 
     */
    public void añadirUsuarioATabla(Object[] o);
    
    /**Establece en la vista el nombre del usuario
     *  
     * @param usuario 
     */
    public void setNombreUsuario(String usuario);
    
    /**Establece en la vista la contraseña del usuario
     * 
     * @param contraseña 
     */
    public void setContraseñaUsuario(String contraseña);
    
    /**Establece en la vista el rol de usuario
     * 
     * @param rol 
     */
    public void setRolUsuario(RolUsuario rol);
    
    /**
     * Elimina la fila seleccionada en la tabla
     */
    public void eliminarUsuarioSeleccionado();
}
