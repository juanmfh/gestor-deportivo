
package vista;

import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author JuanM
 */
public interface VistaRegistros {
    
    String CREARREGISTRO = "crearregistro";
    
    String MODIFICARREGISTRO = "modificarregistro";
    
    String ELIMINARREGISTRO = "eliminarregistro";
    
    String LIMPIAR = "limpiar";
    
    String FILTRAR = "filtrar";
    
    String IMPORTARREGISTROS = "importar";
    
    public void controlador(ActionListener al);
    
    public JComboBox getGruposComboBox();
 
    public String getGrupoParticipante();
    
    public void setGrupoParticipante(String grupo);
    
    public String getPrueba();
    
    public void setPrueba(String prueba);
    
    public String getHoras();
    
    public void setHoras(String horas);
    
    public String getMinutos();
    
    public void setMinutos(String minutos);
    
    public String getSegundos();
    
    public void setSegundos(String segundos);
    
    public Integer getDorsalParticipante();
    
    public DefaultTableModel getModeloRegistrosTable();
    
    public JComboBox getEquiposComboBox();
    
    public JComboBox getParticipantesComboBox();
    
    public void limpiarFormulario();
    
    public JComboBox getPruebasComboBox();
    
    public Integer getSorteo();
    
    public String getEquipo();
    
    public void setParticipante(String participante);
    
    public void añadirRegistroATabla(Object[] o);
    
    public Integer getRegistroSeleccionado();
    
    public void eliminarRegistroDeTabla();
    
    public JComboBox getFiltroGrupoComboBox();
    
    public JComboBox getFiltroPruebasComboBox();
    
    public String getFiltroParticipante();
}
