/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package vista;

import java.awt.Color;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.event.ListSelectionListener;

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
    
    //String AÑADIRPRUEBA = "añadirprueba";
    
    String ABRIRPARTICIPANTES = "abrirparticipantes";
    
    String ABRIRREGISTROS = "abrirregistros";
    
    String ABRIRGRUPOS = "abrirgrupos";
    
    String ABRIREQUIPOS = "abrirequipos";
    
    String ABRIRPRUEBAS = "abrirpruebas";
    
    String IMPRIMIRPDF = "imprimirpdf";
    
    public void controlador(ActionListener al);

    public void añadirCompeticion(String nombre);
    
    public String getCompeticionSelected();
    
    public void modificarCompeticionSeleccionada(String competicion);

    public void eliminarCompeticionSeleccionada();
    
    public void cargarCompeticionTabGeneral();

    public void limpiarDatosCompeticion();
    
    public GeneralTab getGeneralTabPanel();
    
    public void setFocusList(int i);
    
    public void setEstadoLabel(String estado, Color color);
    
    public void mostrarBarraProgreso(Boolean mostrar);
    
}
