package controlador;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import main.ImportarParticipantes;
import modelo.Participante;
import modelo.dao.ParticipanteJpa;
import vista.VistaParticipantes;

/**
 *
 * @author JuanM
 */
public class ControlParticipantes implements ActionListener {

    private VistaParticipantes vista;

    /**
     * Constructor que asocia la vista al controlador
     *
     * @param vista Vista del controlador (Interfaz)
     */
    public ControlParticipantes(VistaParticipantes vista) {
        this.vista = vista;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();
        switch (command) {
            case VistaParticipantes.CREARPARTICIPANTE:
                Participante p;
                try {
                    p = ParticipanteJpa.crearParticipante(
                            Coordinador.getInstance().getSeleccionada(),
                            vista.getNombreParticipante(),
                            vista.getApellidosParticipante(),
                            vista.getDorsalParticipante(),
                            vista.getGrupoParticipante(),
                            vista.getEdadParticipante(),
                            vista.getSexoParticipante(),
                            vista.getEquipoParticipante(),
                            vista.getPruebaAsignadaParticipante());
                    // Actualizamos la vista
                    vista.añadirParticipanteATabla(new Object[]{
                        p.getId(),
                        p.getDorsal(),
                        p.getApellidos(),
                        p.getNombre(),
                        p.getGrupoId().getNombre(),
                        p.getEquipoId() != null ? p.getEquipoId().getNombre() : "",
                        p.getPruebaasignada() != null ? p.getPruebaasignada().getNombre() : ""});
                    vista.limpiarFormularioParticipante();
                    Coordinador.getInstance().setEstadoLabel("Participante creado correctamente", Color.BLUE);
                } catch (InputException ex) {
                    Coordinador.getInstance().setEstadoLabel(ex.getMessage(), Color.RED);
                }
                break;
            case VistaParticipantes.MODIFICARPARTICIPANTE:
                if (vista.getParticipanteSeleccionado() != -1) {
                    try {
                        p = ParticipanteJpa.modificarParticipante(
                                Coordinador.getInstance().getSeleccionada(),
                                vista.getParticipanteSeleccionado(),
                                vista.getNombreParticipante(),
                                vista.getApellidosParticipante(),
                                vista.getDorsalParticipante(),
                                vista.getGrupoParticipante(),
                                vista.getEdadParticipante(),
                                vista.getSexoParticipante(),
                                vista.getEquipoParticipante(),
                                vista.getPruebaAsignadaParticipante()
                        );
                        // Actualizamos la vista
                        vista.eliminarParticipanteDeTabla();
                        vista.añadirParticipanteATabla(new Object[]{
                            p.getId(),
                            p.getDorsal(),
                            p.getApellidos(),
                            p.getNombre(),
                            p.getGrupoId().getNombre(),
                            p.getEquipoId() == null
                            ? "Ninguno"
                            : p.getEquipoId().getNombre(),
                            p.getPruebaasignada() != null ? p.getPruebaasignada().getNombre() : ""});
                        Coordinador.getInstance().setEstadoLabel(
                                "Participante modificado correctamente", Color.BLUE);
                        vista.limpiarFormularioParticipante();
                    } catch (InputException ex) {
                        Coordinador.getInstance().setEstadoLabel(
                                ex.getMessage(), Color.RED);
                    }
                }
                break;
            case VistaParticipantes.ELIMINARPARTICIPANTE:
                if (vista.getParticipanteSeleccionado() != -1) {
                    int confirmDialog = JOptionPane.showConfirmDialog(null,
                            "¿Está seguro de que desea eliminar el participante seleccionado?",
                            "Aviso",
                            JOptionPane.YES_NO_OPTION);
                    if (confirmDialog == JOptionPane.YES_OPTION) {
                        try {
                            ParticipanteJpa.eliminarParticipante(vista.getParticipanteSeleccionado());
                            vista.eliminarParticipanteDeTabla();
                            vista.limpiarFormularioParticipante();
                            Coordinador.getInstance().setEstadoLabel(
                                    "Participante eliminado correctamente", Color.BLUE);
                        } catch (InputException ex) {
                            Coordinador.getInstance().setEstadoLabel(
                                    ex.getMessage(), Color.RED);
                        }
                    }
                }
                break;
            case VistaParticipantes.LIMPIARPARTICIPANTE:
                vista.limpiarFormularioParticipante();
                break;
            case VistaParticipantes.IMPORTAR:
                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fc.setDialogTitle("Abrir");
                int res = fc.showOpenDialog(null);
                if (res == JFileChooser.APPROVE_OPTION) {
                    Coordinador.getInstance().setEstadoLabel("Importando participantes ...", Color.BLACK);
                    ImportarParticipantes inPart;
                    (inPart = new ImportarParticipantes(fc.getSelectedFile().getPath())).execute();
                }

        }
    }

    
}
