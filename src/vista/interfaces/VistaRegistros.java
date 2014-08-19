
package vista.interfaces;

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
    
    String CREARPLANTILLA = "crearplantilla";
    
    /**Asocia un listener controlador a elementos de la vista
     * 
     * @param al ActionListener 
     */
    public void controlador(ActionListener al);
    
    /**Devuelve el combobox de grupos
     * 
     * @return JComboBox 
     */
    public JComboBox getGruposComboBox();
 
    /**Devuelve una cadena con el grupo seleccionado
     * 
     * @return String
     */
    public String getGrupoParticipante();
    
    /**Establece el nombre del grupo en el formulario
     * 
     * @param grupo Nombre del grupo
     */
    public void setGrupoParticipante(String grupo);
    
    /**Devuelve el nombre de la prueba seleccionada
     * 
     * @return String 
     */
    public String getPrueba();
    
    /**Establece el nombre de la prueba en el formulario
     * 
     * @param prueba Nombre de la prueba
     */
    public void setPrueba(String prueba);
    
    /**Devuelve el dato horas del formulario en una cadena
     * 
     * @return String
     */
    public String getHoras();
    
    /**Establece en el formulario el campo horas
     * 
     * @param horas 
     */
    public void setHoras(String horas);
    
    /**Devuelve el dato minutos del formulario en una cadena
     * 
     * @return String
     */
    public String getMinutos();
    
    /**Establece en el formulario el campo minutos
     * 
     * @param minutos 
     */
    public void setMinutos(String minutos);
    
    /**Devuelve el dato segundos del formulario en una cadena, en caso de ser
     * una prueba de tipo distancia o numérica, este será el dato recogido.
     * 
     * @return String
     */
    public String getSegundos();
    
    /**Establece en el formulario el campo segundos
     * 
     * @param segundos 
     */
    public void setSegundos(String segundos);
    
    /**Devuelve el dorsal del participante seleccionado
     * 
     * @return Integer
     */
    public Integer getDorsalParticipante();
    
    /**Devuelve el modelo de la tabla de registros
     * 
     * @return DefaultTableModel
     */
    public DefaultTableModel getModeloRegistrosTable();
    
    /**Devuelve el combobox de equipos
     * 
     * @return JComboBox
     */
    public JComboBox getEquiposComboBox();
    
    /**Devuelve el combobox de participantes
     * 
     * @return JComboBox
     */
    public JComboBox getParticipantesComboBox();
    
    /**
     * Limpia el formulario de participantes
     */
    public void limpiarFormulario();
    
    /**Devuelve el combobox de pruebas
     * 
     * @return JComboBox
     */
    public JComboBox getPruebasComboBox();
    
    /**Devuelve 1 en caso de que se haya seleccionado que la prueba es por 
     * sorteo, 0 en caso contrario.
     * 
     * @return Integer
     */
    public Integer getSorteo();
    
    /**Devuelve el nombre del equipo seleccionado
     * 
     * @return String
     */
    public String getEquipo();
    
    /**Establece el participante seleccionado en el combobox
     * 
     * @param participante Nombre y apellidos del participante
     */
    public void setParticipante(String participante);
    
    /**Añade una nueva fila a la tabla de registros
     * 
     * @param o Nueva fila
     */
    public void añadirRegistroATabla(Object[] o);
    
    /**Devuelve el identificador del registro seleccionado
     * 
     * @return Integer
     */
    public Integer getRegistroSeleccionado();
    
    /**
     * Elimina de la tabla de registros la fila seleccionada
     */
    public void eliminarRegistroDeTabla();
    
    /**Devuelve el combobox del filtro de grupos
     * 
     * @return JComboBox
     */
    public JComboBox getFiltroGrupoComboBox();
    
    /**Devuelve el combobox del filtro de pruebas
     * 
     * @return JComboBox
     */
    public JComboBox getFiltroPruebasComboBox();
    
    /**Devuelve "Individual" o "Equipo" según lo seleccionado en el combobox
     * 
     * @return String
     */
    //public String getFiltroParticipante();
    
    
    /**Devuelve true si el checkbox está seleccionado y false en otro caso
     * 
     * @return boolean 
     */
    public boolean participantesAsignadosCheckBoxIsSelected();
    
    /**Devuelve true si el checkbox está seleccionado y false en otro caso
     * 
     * @return boolean
     */
    public boolean mejoresMarcasCheckBoxIsSelected();
    
    
}
