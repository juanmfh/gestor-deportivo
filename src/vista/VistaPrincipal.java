package vista;

import java.awt.Color;
import java.awt.event.ActionListener;

/**
 *
 * @author JuanM
 */
public interface VistaPrincipal {
    
    String CREARCOMPETICION = "crearcompeticion";
   
    String MODIFICARCOMPETICION = "modificarcompeticion";
    
    String ELIMINARCOMPETICION = "eliminarcompeticion";
    
    String CREARPRUEBA = "crearprueba";
    
    String MODIFICARPRUEBA = "modificarprueba";
    
    String ELIMINARPRUEBA = "eliminarprueba";
    
    String LIMPIARPRUEBA = "limpiarprueba";
    
    String ABRIRPARTICIPANTES = "abrirparticipantes";
    
    String ABRIRREGISTROS = "abrirregistros";
    
    String ABRIRGRUPOS = "abrirgrupos";
    
    String ABRIREQUIPOS = "abrirequipos";
    
    String ABRIRPRUEBAS = "abrirpruebas";
    
    String IMPRIMIRPDF = "imprimirpdf";
    
    /**Asocia un listener controlador a elementos de la vista
     * 
     * @param al ActionListener 
     */
    public void controlador(ActionListener al);

    /**Añade a la lista de competiciones una nueva fila con el nombre de la 
     * competición pasada.
     * 
     * @param nombre Nombre de la competición
     */
    public void añadirCompeticion(String nombre);
    
    /**Devuelve el nombre de la competición seleccionada
     * 
     * @return String
     */
    public String getCompeticionSelected();
    
    /**Modifica en la lista el nombre de la competición seleccionada cambiandole el nombre 
     * por el nuevo pasado por parámetro
     * 
     * @param competicion Nombre de la competicion
     */
    public void modificarCompeticionSeleccionada(String competicion);

    /**
     * Elimina de la lista de competiciones la que está seleccionada
     */
    public void eliminarCompeticionSeleccionada();
    
    /**
     * Carga los datos de la competición seleccionada en la lista en la pestaña
     * general
     */
    public void cargarCompeticionTabGeneral();

    /**
     * Limpia los datos de la competición seleccionada en la pestaña general
     */
    public void limpiarDatosCompeticion();
    
    /**Devuelve la vista de la pestaña general
     * 
     * @return GeneralTab 
     */
    public GeneralTab getGeneralTabPanel();
    
    /**Selecciona en la lista de competiciones el índice pasado como parámetro
     * 
     * @param i índice
     */
    public void setFocusList(int i);
    
    /**Pone un texto de estado en un determinado color en la vista
     * 
     * @param estado  Texto del mensaje
     * @param color   Color del mensaje
     */
    public void setEstadoLabel(String estado, Color color);
    
    /**Muestra o oculta la barra de progreso
     *  
     * @param mostrar Valor booleano para mostrar u ocultar 
     */
    public void mostrarBarraProgreso(Boolean mostrar);
    
}
