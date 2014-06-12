package controlador;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import modelo.Equipo;
import modelo.Grupo;
import dao.EquipoJpa;
import dao.GrupoJpa;
import dao.exceptions.NonexistentEntityException;
import vista.VistaEquipos;

/**
 *
 * @author JuanM
 */
public class ControlEquipos implements ActionListener {

    private VistaEquipos vista;

    /**Constructor que asocia la vista al controlador
     * 
     * @param vista Vista del controlador (Interfaz)
     */
    public ControlEquipos(VistaEquipos vista) {
        this.vista = vista;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();
        switch (command) {
            case VistaEquipos.AÑADIREQUIPO:
                Equipo equipo = crearEquipo(vista.getNombreEquipo(),
                        vista.getGrupo());
                if (equipo != null) {
                    
                    // Actualizamos la vista
                    vista.añadirEquipoATabla(new Object[]{
                        equipo.getId(),
                        equipo.getNombre(),
                        vista.getGruposComboBox().getSelectedItem().toString(),
                        equipo.getParticipanteCollection().size()});
                    vista.limpiarFormularioEquipo();
                    Coordinador.getInstance().setEstadoLabel(
                            "Equipo creado correctamente", Color.BLUE);
                } else {
                    Coordinador.getInstance().setEstadoLabel(
                            "Nombre de equipo incorrecto/ocupado", Color.RED);
                }
                break;
            case VistaEquipos.MODIFICAREQUIPO:
                if (modificarEquipo(vista.getEquipoSelected())) {
                    vista.eliminarEquipoSeleccionado();
                    Coordinador.getInstance().setEstadoLabel(
                            "Equipo modificado correctamente",
                            Color.BLUE);
                    vista.limpiarFormularioEquipo();
                } else {
                    Coordinador.getInstance().setEstadoLabel(
                            "No se puedo modificar el equipo seleccionado",
                            Color.RED);
                }
                break;
            case VistaEquipos.ELIMINAREQUIPO:
                if (vista.getEquipoSelected() != -1) {
                    int confirmDialog = JOptionPane.showConfirmDialog(null,
                            "¿Está seguro de que desea eliminar el equipo seleccionado?",
                            "Aviso",
                            JOptionPane.YES_NO_OPTION);
                    if (confirmDialog == JOptionPane.YES_OPTION) {
                        if (eliminarEquipo(vista.getEquipoSelected())) {
                            vista.eliminarEquipoSeleccionado();
                            vista.limpiarFormularioEquipo();
                            Coordinador.getInstance().setEstadoLabel(
                                    "Equipo eliminado correctamente", Color.BLUE);
                        } else {
                            Coordinador.getInstance().setEstadoLabel(
                                    "Selecciona un equipo", Color.RED);
                        }
                    }
                }
                break;
            case VistaEquipos.LIMPIAR:
                vista.limpiarFormularioEquipo();
                break;
        }
    }

    /**
     * Crea un equipo nuevo
     *
     * @param nombre Nombre del equipo
     * @param nombreGrupo Nombre del grupo al que pertenece el equipo
     * @return Equipo si se ha podido crear el equipo o null en otro caso
     */
    public static Equipo crearEquipo(String nombre, String nombreGrupo) {
        EquipoJpa equipojpa = new EquipoJpa();
        GrupoJpa grupojpa = new GrupoJpa();

        // Comprobamos que se ha seleccionado un grupo
        // y el nombre del equipo es único en la competición
        if (nombreGrupo != null && nombre != null && nombre.length()>0
                && equipojpa.findByNombreAndCompeticion(nombre,Coordinador.getInstance().getSeleccionada().getId()) == null) {

            Grupo grupo = grupojpa.findGrupoByNombreAndCompeticion(nombreGrupo, Coordinador.getInstance().getSeleccionada().getId());
            
            // Creamos el equipo
            Equipo equipo = new Equipo();
            equipo.setNombre(nombre);
            equipo.setGrupoId(grupo);
            equipojpa.create(equipo);

            return equipo;
        }
        return null;
    }

    /**
     * Elimina el equipo cuyo id es equipoid. (No elimina a sus participantes)
     *
     * @param equipoid Id del equipo a modificar
     * @return true si el equipo a sido eliminado correctamente
     */
    public boolean eliminarEquipo(Integer equipoid) {
        
        // Comprobamos que se ha seleccionado un equipo 
        if (equipoid != -1) {

            EquipoJpa equipojpa = new EquipoJpa();
            try {        
                // Eliminamos el equipo
                equipojpa.destroy(equipoid);
            } catch (NonexistentEntityException ex) {
                Logger.getLogger(ControlEquipos.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Modifica el nombre y grupo (en caso de que esté vacio) del equipo cuyo id
     * es equipoid
     *
     * @param equipoid Id del equipo a modificar
     * @return true si ha sido posible modificar el equipo
     */
    private boolean modificarEquipo(Integer equipoid) {
        
        // Comprobamos que se ha seleccionado un equipo 
        if (equipoid != -1) {

            EquipoJpa equipojpa = new EquipoJpa();

            Equipo antiguo = equipojpa.findByNombreAndCompeticion(
                    vista.getNombreEquipo(),
                    Coordinador.getInstance().getSeleccionada().getId());
            
            Equipo equipo = equipojpa.findEquipo(equipoid);

            // Comprobamos que el nombre del equipo no existe o es suyo
            if (antiguo == null || antiguo.getId() == equipoid) {
                equipo.setNombre(vista.getNombreEquipo());
                try {
                    GrupoJpa grupojpa = new GrupoJpa();
                    Grupo g = grupojpa.findByEquipoCompeticion(
                            Coordinador.getInstance().getSeleccionada().getId(),
                            equipoid);

                    // Si el equipo no tiene miembros modificamos su grupo
                    if (equipo.getParticipanteCollection().isEmpty()) {

                        // Buscamos el grupo con el nombre obtenido en la vista
                        g = grupojpa.findGrupoByNombreAndCompeticion(vista.getGruposComboBox().getSelectedItem().toString(),
                                Coordinador.getInstance().getSeleccionada().getId());
                        // Comprobamos que el grupo existe
                        if (g != null) {
                            // Cambiamos el grupo
                            equipo.setGrupoId(g);
                        }
                    }
                    // Cargamos los cambios
                    equipojpa.edit(equipo);
                    
                    // Actualizamos la vista
                    vista.añadirEquipoATabla(new Object[]{
                        equipo.getId(),
                        equipo.getNombre(),
                        g != null ? g.getNombre() : "",
                        equipo.getParticipanteCollection().size()});
                    return true;
                } catch (NonexistentEntityException ex) {
                    return false;
                } catch (Exception ex) {
                    return false;
                }
            }
        }
        return false;
    }
}
