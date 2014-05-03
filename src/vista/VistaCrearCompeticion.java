package vista;

import java.awt.Color;
import java.awt.event.ActionListener;
import modelo.Competicion;

/**
 *
 * @author JuanM
 */
public interface VistaCrearCompeticion {
       
    String OK = "ok";
   
    String CANCELAR = "cancelar";
    
    public void controlador(ActionListener al);
    
    public void cerrar();
    
    public String getNombre();
    
    public void estado(String estado, Color color);
    
    public void cargarDatosCompeticion(Competicion c);
    
    public String getOrganizador();
    
    public String getLugar();
    
    public String getRutaImagen();
    
    public String getRutaLogo();
    
    public String getAñoInicio();
    
    public Integer getMesInicio();
    
    public String getDiaInicio();
    
    public String getAñoFin();
    
    public Integer getMesFin();
    
    public String getDiaFin();
    
}
