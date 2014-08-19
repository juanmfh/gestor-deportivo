package controlador;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import modelo.logicaNegocio.PDFHelper;
import vista.interfaces.VistaImprimirResultados;

/**
 *
 * @author JuanM
 */
public class ControlCrearFicheros implements ActionListener {

    private final VistaImprimirResultados vista;

    /**
     * Constructor que asocia la vista al controlador
     *
     * @param vista Vista del controlador (Interfaz)
     */
    public ControlCrearFicheros(VistaImprimirResultados vista) {
        this.vista = vista;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();
        switch (command) {
            case VistaImprimirResultados.OK:
                switch (vista.getFormato()) {
                    case "Excel":
                        crearPlantillaExcel();
                        vista.cerrar();
                        break;
                    case "PDF":
                        imprimirResultadosPDF();
                        vista.cerrar();
                        break;
                }
                break;
            case VistaImprimirResultados.CANCELAR:
                vista.cerrar();
                break;
        }
    }

    /**
     * LLama a la clase PDFHelper según las opciones marcadas en la vista
     */
    private void imprimirResultadosPDF() {

        try {

            if (vista.getgruposCheckBox() && vista.getpruebasCheckBox()) {          // Se ha seleccionado imprimir todo
                PDFHelper.imprimirResultadosPDF(null, null, vista.getgenerarListaSalidaCheckBox(), vista.getparticipantesAsignadosCheckBox());
            } else if (vista.getgruposCheckBox() && !vista.getpruebasCheckBox()) {  //Se ha seleccionado imprimir algunas pruebas de todos los grupos
                PDFHelper.imprimirResultadosPDF(vista.getpruebasList(), null, vista.getgenerarListaSalidaCheckBox(), vista.getparticipantesAsignadosCheckBox());
            } else if (!vista.getgruposCheckBox() && vista.getpruebasCheckBox()) {   // Se ha seleccionado imprimir todas las pruebas pero solo de algunos grupos
                PDFHelper.imprimirResultadosPDF(null, vista.getgruposList(), vista.getgenerarListaSalidaCheckBox(), vista.getparticipantesAsignadosCheckBox());
            } else if (vista.getpruebasList().isEmpty() || vista.getgruposList().isEmpty()) {   // No se ha seleccionado ningún grupo o ninguna prueba
                throw new InputException("Selección no válida");
            } else {                                                                            // Se ha seleccionado varios grupos y varias pruebas
                PDFHelper.imprimirResultadosPDF(vista.getpruebasList(), vista.getgruposList(), vista.getgenerarListaSalidaCheckBox(), vista.getparticipantesAsignadosCheckBox());
            }
        } catch (InputException ex) {
            Coordinador.getInstance().setEstadoLabel(ex.getMessage(), Color.RED);
        }
    }

    private void crearPlantillaExcel() {
        try {

            if (vista.getgruposCheckBox() && vista.getpruebasCheckBox()) {          // Se ha seleccionado imprimir todo
                ControlRegistros.crearPlantillaFileChooser(null, null, vista.getparticipantesAsignadosCheckBox());
            } else if (vista.getgruposCheckBox() && !vista.getpruebasCheckBox()) {  //Se ha seleccionado imprimir algunas pruebas de todos los grupos
                ControlRegistros.crearPlantillaFileChooser(vista.getpruebasList(), null, vista.getparticipantesAsignadosCheckBox());
            } else if (!vista.getgruposCheckBox() && vista.getpruebasCheckBox()) {   // Se ha seleccionado imprimir todas las pruebas pero solo de algunos grupos
                ControlRegistros.crearPlantillaFileChooser(null, vista.getgruposList(), vista.getparticipantesAsignadosCheckBox());
            } else if (vista.getpruebasList().isEmpty() || vista.getgruposList().isEmpty()) {   // No se ha seleccionado ningún grupo o ninguna prueba
                throw new InputException("Selección no válida");
            } else {                                                                            // Se ha seleccionado varios grupos y varias pruebas
                ControlRegistros.crearPlantillaFileChooser(vista.getpruebasList(), vista.getgruposList(), vista.getparticipantesAsignadosCheckBox());
            }
        } catch (InputException ex) {
            Coordinador.getInstance().setEstadoLabel(ex.getMessage(), Color.RED);
        }
    }

}
