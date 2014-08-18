package controlador;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import modelo.Equipo;
import modelo.dao.EquipoJpa;
import vista.VistaEquipos;

/**
 *
 * @author JuanM
 */
public class ControlEquipos implements ActionListener {

    private VistaEquipos vista;

    /**
     * Constructor que asocia la vista al controlador
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
                Equipo equipo;
                try {
                    equipo = EquipoJpa.crearEquipo(Coordinador.getInstance().getSeleccionada(), vista.getNombreEquipo(),
                            vista.getGrupo());
                    // Actualizamos la vista
                    vista.añadirEquipoATabla(new Object[]{
                        equipo.getId(),
                        equipo.getNombre(),
                        equipo.getGrupoId().getNombre(),
                        equipo.getParticipanteCollection().size()});
                    vista.limpiarFormularioEquipo();
                    Coordinador.getInstance().setEstadoLabel(
                            "Equipo creado correctamente", Color.BLUE);
                } catch (InputException ex) {
                    Coordinador.getInstance().setEstadoLabel(
                            ex.getMessage(), Color.RED);
                }
                break;
            case VistaEquipos.MODIFICAREQUIPO:
                try {
                    equipo = EquipoJpa.modificarEquipo(
                            Coordinador.getInstance().getSeleccionada(),
                            vista.getEquipoSelected(),
                            vista.getNombreEquipo(),
                            vista.getGruposComboBox().getSelectedItem().toString());
                    //Actualizamos la vista
                    vista.añadirEquipoATabla(new Object[]{
                        equipo.getId(),
                        equipo.getNombre(),
                        equipo.getGrupoId().getNombre(),
                        equipo.getParticipanteCollection().size()});
                    vista.eliminarEquipoSeleccionado();
                    Coordinador.getInstance().setEstadoLabel(
                            "Equipo modificado correctamente",
                            Color.BLUE);
                    vista.limpiarFormularioEquipo();
                } catch (InputException ex) {
                    Coordinador.getInstance().setEstadoLabel(
                            ex.getMessage(),
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
                        try {
                            EquipoJpa.eliminarEquipo(vista.getEquipoSelected());
                            vista.eliminarEquipoSeleccionado();
                            vista.limpiarFormularioEquipo();
                            Coordinador.getInstance().setEstadoLabel(
                                    "Equipo eliminado correctamente", Color.BLUE);
                        } catch (InputException ex) {
                            Coordinador.getInstance().setEstadoLabel(
                                    ex.getMessage(), Color.RED);
                        }
                    }
                }
                break;
            case VistaEquipos.LIMPIAR:
                vista.limpiarFormularioEquipo();
                break;
        }
    }

}
