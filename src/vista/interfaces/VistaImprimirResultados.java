package vista.interfaces;

import java.awt.event.ActionListener;
import java.util.List;

/**
 *
 * @author JuanM
 */
public interface VistaImprimirResultados {
    
    String OK = "ok";
   
    String CANCELAR = "cancelar";
    
    
    /**Asocia un listener controlador a elementos de la vista
     * 
     * @param al ActionListener 
     */
    public void controlador(ActionListener al);
    
    /** Cierra la vista
     * 
     */
    public void cerrar();
    
    /**Devuelve true si el checkbox de pruebas está marcado, false en otro caso
     * 
     * @return boolean
     */
    public boolean getpruebasCheckBox();
    
    /**Devuelve true si el checkbox de grupos está marcado, false en otro caso
     * 
     * @return boolean
     */
    public boolean getgruposCheckBox();
    
    /**Devuelve true si el checkbox de lista de salida está marcado, false en otro caso
     * 
     * @return boolean
     */
    public boolean getgenerarListaSalidaCheckBox();
    
    /**Devuelve true si el checkbox de participantes asignados está marcado, false en otro caso
     * 
     * @return boolean
     */
    public boolean getparticipantesAsignadosCheckBox();
    
    /**Devuelve la lista con los nombres de las pruebas seleccionadas
     * 
     * @return List<String>
     */
    public List<String> getpruebasList();
    
    /**Devuelve la lista con los nombres de los grupos seleccionados
     * 
     * @return List<String>
     */
    public List<String> getgruposList();
    
    /**Añade una nueva fila a la lista de pruebas
     * 
     * @param nombrePrueba Nombre de la prueba
     */
    public void añadirPrueba(String nombrePrueba);
    

    /**Añade una nueva fila a la lista de grupos
     * 
     * @param nombreGrupo Nombre del grupo
     */
    public void añadirGrupo(String nombreGrupo);
    
    /**Carga la lista de pruebas
     * 
     * @param pruebas Lista con los nombres de las pruebas
     */
    public void asignarListaPruebas(List<String> pruebas);
    
    /**Carga la lista de grupos
     * 
     * @param grupos Lista con los nombres de las grupos
     */
    public void asignarListaGrupos(List<String> grupos);
    
    /**Devuelve "Excel" o "PDF" para distinguir desde donde se ha llamado a la vista
     * 
     * @return 
     */
    public String getFormato();
    
}
