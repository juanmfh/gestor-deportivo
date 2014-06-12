package controlador;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import jpa.CompeticionJpa;
import main.IOFile;
import modelo.Competicion;
import vista.VistaCrearCompeticion;

/**
 *
 * @author JuanM
 */
public class ControlModificarCompeticion implements ActionListener {

    private final VistaCrearCompeticion vista;

    /**Constructor que asocia la vista al controlador
     * 
     * @param vista Vista del controlador (Interfaz)
     */
    public ControlModificarCompeticion(VistaCrearCompeticion vista) {
        this.vista = vista;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();
        switch (command) {
            case VistaCrearCompeticion.OK:
                Competicion competicion = modificarCompeticion();
                // Si la competicion ha sido modificada correctamente
                if (competicion != null) {
                    // Actualizamos la vista y cerramos el diálogo
                    Coordinador.getInstance().actualizarVistaCompeticionModificada(competicion);
                    vista.cerrar();
                    
                }else{
                    vista.estado("Nombre de la competición incorrecta", Color.RED);
                }
                break;
            case VistaCrearCompeticion.CANCELAR:
                vista.cerrar();
                break;
        }
    }

    
    /**
     * Modifica una competición a partir de los datos de la vista.
     *
     * @return la Competicion modificada o null si ha habido algún error
     */
    private Competicion modificarCompeticion() {

        // Obtenemos la competicion que vamos a modificar
        Competicion c = Coordinador.getInstance().getControladorPrincipal().getSeleccionada();
        
        // Obtenemos los datos necesarios para modificar la competicion
        Date fechaInicio = IOFile.formatearFecha(vista.getDiaInicio(), String.valueOf(vista.getMesInicio()), vista.getAñoInicio());
        Date fechaFin = IOFile.formatearFecha(vista.getDiaFin(), String.valueOf(vista.getMesFin()), vista.getAñoFin());
        String nombreImagen = IOFile.getNombreFichero(vista.getRutaImagen());
        
        // Modificamos la competicion "c" con los datos de la vista
        c = modificarCompeticion(c, vista.getNombre(),
                vista.getLugar(),
                fechaInicio,
                fechaFin,
                nombreImagen,
                vista.getOrganizador());
        
        return c;

    }

    /**
     * Modifica y devuelve la competicion "competicion" con los datos pasados
     * como parámetros.
     *
     * @param competicion   Objeto Competicion a modificar
     * @param nombre        nombre de la competición
     * @param lugar         lugar de la competición.
     * @param fechaInicio   fecha en la que comienza la competición.
     * @param fechaFin      fecha en la que termina la competición.
     * @param nombreImagen  nombre de la imagen que será su logo.
     * @param organizador   organizador de la competición.
     * @return Competicion
     */
    private Competicion modificarCompeticion(Competicion competicion,
            String nombre,
            String lugar,
            Date fechaInicio,
            Date fechaFin,
            String nombreImagen,
            String organizador) {

        CompeticionJpa competicionjpa = new CompeticionJpa();
        
        // Si el nombre es vacio el resultado es null
        if (vista.getNombre().length() > 0) {
            competicion.setNombre(nombre);
            competicion.setOrganizador(organizador);
            competicion.setCiudad(lugar);
            competicion.setFechainicio(fechaInicio);
            competicion.setFechafin(fechaFin);
            
            // Si la imagen ha cambiado, hacemos una copia del fichero y ponemos
            // la nueva imagen.
            if (!competicion.getImagen().equals(vista.getRutaImagen())) {
                competicion.setImagen(nombreImagen);
                IOFile.copiarFichero(vista.getRutaImagen(),
                        System.getProperty("user.dir") + "/resources/img/");
            }
            try {
                // Cargamos la modificación en la base de datos
                competicionjpa.edit(competicion);
            } catch (jpa.exceptions.NonexistentEntityException ex) {
                return null;
            } catch (Exception ex) {
                return null;
            }
            return competicion;
        }
        return null;
    }
}
