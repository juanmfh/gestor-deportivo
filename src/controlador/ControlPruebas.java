package controlador;

import modelo.TipoResultado;
import modelo.TipoPrueba;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import modelo.Prueba;
import modelo.dao.PruebaJpa;
import vista.GeneralTab;
import vista.VistaPrincipal;

/**
 *
 * @author JuanM
 */
public class ControlPruebas implements ActionListener {

    private final GeneralTab vista;

    /**
     * Constructor que asocia la vista al controlador
     *
     * @param vista Vista del controlador (Interfaz)
     */
    public ControlPruebas(GeneralTab vista) {
        this.vista = vista;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();
        switch (command) {
            case VistaPrincipal.CREARPRUEBA:
                if (Coordinador.getInstance().getSeleccionada() != null) {
                    Prueba prueba;
                    try {
                        prueba = PruebaJpa.crearPrueba(Coordinador.getInstance().getSeleccionada(),
                                vista.getNombrePrueba(),
                                vista.getTipoPrueba(), vista.getTipoResultado());
                        // Actualizamos la vista
                        vista.añadirPruebaATabla(new Object[]{
                            prueba.getId(),
                            prueba.getNombre(),
                            prueba.getTipo(),
                            prueba.getTiporesultado()});
                        Coordinador.getInstance().setEstadoLabel(
                                "Prueba creada correctamente", Color.BLUE);
                        vista.limpiarFormularioPrueba();
                    } catch (InputException ex) {
                        Coordinador.getInstance().setEstadoLabel(
                                ex.getMessage(), Color.RED);
                    }
                }
                break;
            case VistaPrincipal.ELIMINARPRUEBA:
                if (vista.getPruebaSelected() != -1) {
                    int confirmDialogPrueba = JOptionPane.showConfirmDialog(
                            null,
                            "¿Está seguro de que desea eliminar la prueba seleccionada?",
                            "Aviso",
                            JOptionPane.YES_NO_OPTION);
                    if (confirmDialogPrueba == JOptionPane.YES_OPTION) {
                        try {
                            PruebaJpa.eliminarPrueba(vista.getPruebaSelected(),
                                    Coordinador.getInstance().getSeleccionada().getId());
                            vista.eliminarPrueba();
                            vista.limpiarFormularioPrueba();
                            Coordinador.getInstance().setEstadoLabel("Prueba eliminada correctamente", Color.BLUE);
                        } catch (InputException ex) {
                            Coordinador.getInstance().setEstadoLabel(ex.getMessage(), Color.RED);
                        }

                    }
                }
                break;
            case VistaPrincipal.MODIFICARPRUEBA:
                Prueba prueba;
                try {
                    prueba = PruebaJpa.modificarPrueba(vista.getPruebaSelected(),
                            Coordinador.getInstance().getSeleccionada().getId(),
                            vista.getNombrePrueba(),
                            TipoResultado.valueOf(vista.getTipoResultado()),
                            TipoPrueba.valueOf(vista.getTipoPrueba())
                    );
                    // Actualizamos la vista
                    vista.añadirPruebaATabla(new Object[]{
                        prueba.getId(),
                        prueba.getNombre(),
                        prueba.getTipo(),
                        prueba.getTiporesultado()});
                    vista.eliminarPrueba();
                    Coordinador.getInstance().setEstadoLabel("Prueba modificada correctamente", Color.BLUE);
                } catch (InputException ex) {
                    Coordinador.getInstance().setEstadoLabel(ex.getMessage(), Color.RED);
                }
                break;
            case VistaPrincipal.LIMPIARPRUEBA:
                vista.limpiarFormularioPrueba();
                break;

        }
    }

    
}
