package vista;

import java.awt.event.ActionListener;
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
    
    public void controlador(ActionListener al);
    
    public String getNombreParticipante();
    
    public String getApellidosParticipante();
    
    public Integer getEdadParticipante();
    
    public Integer getSexoParticipante();
    
    public String getGrupoParticipante();
    
    public String getEquipoParticipante();
    
    public Integer getDorsalParticipante();
    
    public DefaultTableModel getModeloParticipantesTable();
    
    public void añadirParticipanteATabla(Object[] o);
    
    public Integer getParticipanteSeleccionado();
    
    public void eliminarParticipanteDeTabla();
    
    public void limpiarFormularioParticipante();
    
    public void setNombreParticipante(String nombre);
    
    public void setApellidosParticipante(String apellidos);
    
    public void setEdadParticipante(Integer edad);
    
    public void setDorsalParticipante(Integer dorsal);
    
    public void setGrupoParticipante(String grupo);
    
    public void setEquipoParticipante(String equipo);
    
    public void setSexoParticipante(Integer sexo);
}
