package controlador;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import main.IOFile;
import modelo.Administrado;
import modelo.Competicion;
import modelo.Usuario;
import dao.AdministradoJpa;
import dao.CompeticionJpa;
import vista.VistaCrearCompeticion;

/**
 *
 * @author JuanM
 */
public class ControlCrearCompeticion implements ActionListener {

    private final VistaCrearCompeticion vista;

    /**Constructor que asocia la vista al controlador
     * 
     * @param vista Vista del controlador (Interfaz)
     */
    public ControlCrearCompeticion(VistaCrearCompeticion vista) {
        this.vista = vista;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();
        switch (command) {
            case VistaCrearCompeticion.OK:
                Competicion competicion = crearCompeticion();
                if (competicion != null) {
                    Coordinador.getInstance().actualizarVistaCompeticionCreada(competicion);
                    vista.cerrar();
                } else {
                    vista.estado("Nombre de la competición incorrecta", Color.RED);
                }
                break;
            case VistaCrearCompeticion.CANCELAR:
                vista.cerrar();
                break;
        }
    }

    
    /**
     * Crea una competición a partir de los datos de la vista y le da permisos 
     * al usuario logeado como administrador
     *
     * @return la Competicion creada o null si ha habido algún error
     */
    private Competicion crearCompeticion() {
        Competicion competicion = null;

        // Datos necesarios para crear la competicion
        Date fechaInicio = IOFile.formatearFecha(vista.getDiaInicio(), String.valueOf(vista.getMesInicio()), vista.getAñoInicio());
        Date fechaFin = IOFile.formatearFecha(vista.getDiaFin(), String.valueOf(vista.getMesFin()), vista.getAñoFin());
        String nombreImagen = IOFile.getNombreFichero(vista.getRutaImagen());

        // crea la competicion
        competicion = crearCompeticion(vista.getNombre(),
                vista.getLugar(),
                fechaInicio,
                fechaFin,
                nombreImagen,
                vista.getOrganizador());

        if (competicion != null) {
            // Copia local de la imagen logo
            IOFile.copiarFichero(vista.getRutaImagen(),
                    System.getProperty("user.dir") + "/resources/img/");

            // crea permisos al usuario en la competicion
            crearAdministrado(competicion, Coordinador.getInstance().getUsuario());
        }
        return competicion;
    }

    
    /**
     * Crea una competición con los datos pasados como parámetros
     *
     * @param nombre        nombre de la competición
     * @param lugar         lugar de la competición.
     * @param fechaInicio   fecha en la que comienza la competición.
     * @param fechaFin      fecha en la que termina la competición.
     * @param nombreImagen  nombre de la imagen que será su logo.
     * @param organizador   organizador de la competición.
     * @return Competicion
     */
    private Competicion crearCompeticion(String nombre,
            String lugar,
            Date fechaInicio,
            Date fechaFin,
            String nombreImagen,
            String organizador) {

        CompeticionJpa competicionjpa = new CompeticionJpa();

        if (nombre.length() > 0 && competicionjpa.findCompeticionByName(nombre) == null) {
            Competicion competicion = new Competicion();
            competicion.setNombre(nombre);
            competicion.setCiudad(lugar);
            competicion.setFechainicio(fechaInicio);
            competicion.setFechafin(fechaFin);
            competicion.setImagen(nombreImagen);
            competicion.setOrganizador(organizador);
            competicion.setPais(null);
            competicionjpa.create(competicion);
            return competicion;
        } else {
            return null;
        }
    }
    
    /**
     * Da permisos a un usuario para poder administrar una competición.
     * 
     * @param competicion 
     * @param usuario
     * @return Administrado
     */
    private Administrado crearAdministrado(Competicion competicion, Usuario usuario) {
        
        Administrado administrado = null;
        if (competicion != null && usuario != null) {
            administrado = new Administrado();
            administrado.setUsuarioId(usuario);
            administrado.setCompeticionId(competicion);
            AdministradoJpa admjpa = new AdministradoJpa();
            admjpa.create(administrado);
        }
        return administrado;
    }
}
