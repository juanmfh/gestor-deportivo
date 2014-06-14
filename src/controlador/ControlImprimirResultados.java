package controlador;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import main.PDFHelper;
import vista.VistaImprimirResultados;

/**
 *
 * @author JuanM
 */
public class ControlImprimirResultados implements ActionListener {

    private final VistaImprimirResultados vista;

    /**
     * Constructor que asocia la vista al controlador
     *
     * @param vista Vista del controlador (Interfaz)
     */
    public ControlImprimirResultados(VistaImprimirResultados vista) {
        this.vista = vista;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();
        switch (command) {
            case VistaImprimirResultados.OK:

                imprimirResultados();
                break;
            case VistaImprimirResultados.CANCELAR:
                vista.cerrar();
                break;
        }
    }

    /**
     * LLama a la clase PDFHelper según las opciones marcadas en la vista
     */
    private void imprimirResultados() {

        // Se ha seleccionado imprimir todo
        if (vista.getgruposCheckBox() && vista.getpruebasCheckBox()) {
            if (PDFHelper.imprimirResultadosPDF(null, null)) {
                Coordinador.getInstance().setEstadoLabel(
                        "Resultados imprimidos correctamente", Color.BLUE);
                vista.cerrar();
            } else {
                Coordinador.getInstance().setEstadoLabel(
                        "Impresión no finalizada", Color.RED);
            }
            //Se ha seleccionado imprimir algunas pruebas de todos los grupos
        } else if (vista.getgruposCheckBox() && !vista.getpruebasCheckBox()) {
            if (PDFHelper.imprimirResultadosPDF(vista.getpruebasList(), null)) {
                Coordinador.getInstance().setEstadoLabel(
                        "Resultados imprimidos correctamente", Color.BLUE);
                vista.cerrar();
            } else {
                Coordinador.getInstance().setEstadoLabel(
                        "Impresión no finalizada", Color.RED);
            }
            // Se ha seleccionado imprimir todas las pruebas pero solo de algunos grupos
        } else if (!vista.getgruposCheckBox() && vista.getpruebasCheckBox()) {
            if (PDFHelper.imprimirResultadosPDF(null, vista.getgruposList())) {
                Coordinador.getInstance().setEstadoLabel(
                        "Resultados imprimidos correctamente", Color.BLUE);
                vista.cerrar();
            } else {
                Coordinador.getInstance().setEstadoLabel(
                        "Impresión no finalizada", Color.RED);
            }
        } else if(vista.getpruebasList().isEmpty() || vista.getgruposList().isEmpty()){
            // Mensaje de error: selección no válida
        }else { // Se ha seleccionado algunos grupos y algunas pruebas
            if (PDFHelper.imprimirResultadosPDF(vista.getpruebasList(), vista.getgruposList())) {
                Coordinador.getInstance().setEstadoLabel(
                        "Resultados imprimidos correctamente", Color.BLUE);
                vista.cerrar();
            } else {
                Coordinador.getInstance().setEstadoLabel(
                        "Impresión no finalizada", Color.RED);
            }
        }
    }

}
