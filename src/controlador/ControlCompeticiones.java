package controlador;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import main.IOFile;
import modelo.entities.Competicion;
import modelo.dao.AdministradoJpa;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import modelo.logicaNegocio.CompeticionService;
import vista.interfaces.VistaCompeticion;

/**
 *
 * @author JuanM
 */
public class ControlCompeticiones implements ActionListener {

    private final VistaCompeticion vista;

    /**
     * Constructor que asocia la vista al controlador
     *
     * @param vista Vista del controlador (Interfaz)
     */
    public ControlCompeticiones(VistaCompeticion vista) {
        this.vista = vista;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();
        switch (command) {
            case VistaCompeticion.OK:
                Competicion competicion;
                try {
                    if (vista.getVistaModificarCompeticion()) {
                        competicion = modificarCompeticion();
                        // Actualizamos la vista y cerramos el diálogo
                        Coordinador.getInstance().actualizarVistaCompeticionModificada(competicion);
                    } else {
                        competicion = crearCompeticion();
                        Coordinador.getInstance().actualizarVistaCompeticionCreada(competicion);
                    }
                    vista.cerrar();
                } catch (InputException ex) {
                    vista.estado(ex.getMessage(), Color.RED);
                }
                break;
            case VistaCompeticion.CANCELAR:
                vista.cerrar();
                break;
        }
    }

    /**
     * Crea una competición a partir de los datos de la vista y le da permisos
     * de acceso al usuario que la crea
     *
     * @return la Competicion creada o null si ha habido algún error
     */
    private Competicion crearCompeticion() throws InputException {
        Competicion competicion;

        // Datos no obligatorios para crear la competicion
        Date fechaInicio = formatearFecha(vista.getDiaInicio(), String.valueOf(vista.getMesInicio()), vista.getAñoInicio());
        Date fechaFin = formatearFecha(vista.getDiaFin(), String.valueOf(vista.getMesFin()), vista.getAñoFin());
        String nombreImagen = IOFile.getNombreFichero(vista.getRutaImagen());

        // Creamos la competicion a partir de los datos recogidos de la vista
        competicion = CompeticionService.crearCompeticion(vista.getNombre(),
                vista.getLugar(),
                fechaInicio,
                fechaFin,
                nombreImagen,
                vista.getOrganizador());

        // Se realiza una copia local de la imagen (logo) de la competición
        IOFile.copiarFichero(vista.getRutaImagen(),
                System.getProperty("user.dir") + "/resources/img/");

        // Se crea permisos al usuario para esta competición
        AdministradoJpa.crearAdministrado(competicion, Coordinador.getInstance().getUsuario());

        return competicion;
    }


    /**
     * Modifica una competición a partir de los datos de la vista.
     *
     * @return la Competicion modificada
     * @throws controlador.InputException
     */
    private Competicion modificarCompeticion() throws InputException {

        // Obtenemos la competicion que vamos a modificar
        Competicion c = Coordinador.getInstance().getSeleccionada();
        String oldpath = c.getImagen();

        // Obtenemos los datos necesarios para modificar la competicion
        Date fechaInicio = formatearFecha(vista.getDiaInicio(), String.valueOf(vista.getMesInicio()), vista.getAñoInicio());
        Date fechaFin = formatearFecha(vista.getDiaFin(), String.valueOf(vista.getMesFin()), vista.getAñoFin());
        String nombreImagen = IOFile.getNombreFichero(vista.getRutaImagen());

        // Modificamos la competicion "c" con los datos de la vista
        c = CompeticionService.modificarCompeticion(c, vista.getNombre(),
                vista.getLugar(),
                fechaInicio,
                fechaFin,
                nombreImagen,
                vista.getOrganizador());

        // Si la imagen ha cambiado, hacemos una copia del fichero y ponemos
        // la nueva imagen.
        if (!c.getImagen().equals(oldpath)) {
            IOFile.copiarFichero(vista.getRutaImagen(),
                    System.getProperty("user.dir") + "/resources/img/");
        }
        return c;

    }
    
    /**
     * Devuelve en un objeto Date la fecha pasada como parámetros en String o
     * null si los parámetros no son correctos.
     *
     * @param dia numero del dia en String.
     * @param mes numero del mes en String.
     * @param año numero del año en String.
     * @return Date
     */
    public static Date formatearFecha(String dia, String mes, String año) {
        String fecha = null;
        try {
            Date res = null;
            if (año.length() == 4) {
                fecha = String.valueOf(Integer.parseInt(año)) + "-";
                if (mes.length() == 1) {
                    fecha += "0" + mes + "-";
                } else {
                    fecha += mes + "-";
                }
                if (dia.length() == 1) {
                    fecha += "0" + dia;
                } else {
                    fecha += dia;
                }
                SimpleDateFormat textFormat = new SimpleDateFormat("yyyy-MM-dd");
                res = textFormat.parse(fecha);
            }
            return res;
        } catch (NumberFormatException | ParseException exception) {
            return null;
        }
    }

}
