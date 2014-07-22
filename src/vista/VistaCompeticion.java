package vista;

import java.awt.Color;
import java.awt.event.ActionListener;
import modelo.Competicion;

/**
 *
 * @author JuanM
 */
public interface VistaCompeticion {
       
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
    
    /** Devuelve el nombre de la competición
     * 
     * @return String
     */
    public String getNombre();
    
    /** Modifica un mensaje que se muestra en la vista con el texto estado con 
     *  un color determinado
     * 
     * @param estado Texto del mensaje
     * @param color  Color del mensaje
     */
    public void estado(String estado, Color color);
    
    /** Carga en la vista la información de una competición
     * 
     * @param c Competición
     */
    public void cargarDatosCompeticion(Competicion c);
    
    /**Devuelve una cadena que indica el organizador de la competición
     * 
     * @return String
     */
    public String getOrganizador();
    
    /**Devuelve una cadena que indica el lugar de la competición
     * 
     * @return String
     */
    public String getLugar();
    
    /**Devuelve una cadena que indica la ruta de la imagen seleccionada como
     * logo
     * 
     * @return String
     */
    public String getRutaImagen();
    
    /**Devuelve una cadena que indica el año de inicio de la competición
     * 
     * @return String
     */
    public String getAñoInicio();
    
    /**Devuelve una cadena que indica el mes de inicio de la competición
     * 
     * @return String
     */
    public Integer getMesInicio();
    
    /**Devuelve una cadena que indica el dia de inicio de la competición
     * 
     * @return String
     */
    public String getDiaInicio();
    
    /**Devuelve una cadena que indica el año de fin de la competición
     * 
     * @return String
     */
    public String getAñoFin();
    
    /**Devuelve una cadena que indica el mes de fin de la competición
     * 
     * @return String
     */
    public Integer getMesFin();
    
    /**Devuelve una cadena que indica el dia de fin de la competición
     * 
     * @return String
     */
    public String getDiaFin();
    
    
    public boolean getVistaModificarCompeticion();
}
