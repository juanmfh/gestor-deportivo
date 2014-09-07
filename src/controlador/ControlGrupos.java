package controlador;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import modelo.entities.Grupo;
import modelo.logicaNegocio.GrupoService;
import vista.interfaces.VistaGrupos;

/**
 *
 * @author JuanM
 */
public class ControlGrupos implements ActionListener {

    private final VistaGrupos vista;

    /**
     * Constructor que asocia la vista al controlador
     *
     * @param vista Vista del controlador (Interfaz)
     */
    public ControlGrupos(VistaGrupos vista) {
        this.vista = vista;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();
        switch (command) {
            case VistaGrupos.CREARGRUPO:
                Grupo g;
                try {
                    g = GrupoService.crearGrupo(Coordinador.getInstance().getSeleccionada(), vista.getNombreGrupo(),
                            vista.getSubgrupoDeComboBox().getSelectedItem().toString());
                    //Actualizamos la vista
                    vista.añadirGrupoATabla(new Object[]{
                        g.getId(),
                        g.getNombre(),
                        g.getGrupoId() != null
                        ? g.getGrupoId().getNombre()
                        : null});
                    Coordinador.getInstance().getControladorPrincipal().cargarSubGruposComboBox();
                    vista.limpiarFormularioGrupo();
                    Coordinador.getInstance().setEstadoLabel(
                            "Grupo creado correctamente", Color.BLUE);
                } catch (InputException ex) {
                    Coordinador.getInstance().setEstadoLabel(
                            ex.getMessage(), Color.RED);
                }
                break;
            case VistaGrupos.MODIFICARGRUPO:
                try {
                    g = GrupoService.modificarGrupo(Coordinador.getInstance().getSeleccionada(),
                            vista.getGrupoSelected(), vista.getNombreGrupo(),
                            vista.getSubgrupoDeComboBox().getSelectedItem().toString());
                    //Actualizamos la vista
                    vista.añadirGrupoATabla(new Object[]{
                        g.getId(),
                        g.getNombre(),
                        g.getGrupoId() != null ? g.getGrupoId().getNombre() : ""
                    });
                    Coordinador.getInstance().getControladorPrincipal().cargarTablaGrupos();
                    Coordinador.getInstance().getControladorPrincipal().cargarSubGruposComboBox();
                    Coordinador.getInstance().setEstadoLabel(
                            "Grupo modificado correctamente", Color.BLUE);
                } catch (InputException ex) {
                    Coordinador.getInstance().setEstadoLabel(
                            ex.getMessage(), Color.RED);
                }

                break;
            case VistaGrupos.ELIMINARGRUPO:
                if (vista.getGrupoSelected() != -1) {
                    int confirmDialog = JOptionPane.showConfirmDialog(null,
                            "¿Está seguro de que desea eliminar el grupo seleccionado?",
                            "Aviso",
                            JOptionPane.YES_NO_OPTION);
                    if (confirmDialog == JOptionPane.YES_OPTION) {
                        try {
                            GrupoService.eliminarGrupo(Coordinador.getInstance().getSeleccionada(), vista.getGrupoSelected());
                            //Actualizamos la vista
                            vista.limpiarFormularioGrupo();
                            Coordinador.getInstance().getControladorPrincipal().cargarSubGruposComboBox();
                            Coordinador.getInstance().getControladorPrincipal().cargarTablaGrupos();
                            Coordinador.getInstance().setEstadoLabel("Grupo eliminado correctamente", Color.BLUE);
                        } catch (InputException ex) {
                            Coordinador.getInstance().setEstadoLabel(ex.getMessage(), Color.RED);
                        }

                    }
                }
                break;
            case VistaGrupos.LIMPIAR:
                vista.limpiarFormularioGrupo();
                break;
        }
    }

}
