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
import dao.CompuestaJpa;
import dao.GrupoJpa;
import dao.InscripcionJpa;
import dao.PruebaJpa;
import java.util.List;
import modelo.Compuesta;
import modelo.Grupo;
import modelo.Inscripcion;
import vista.VistaCompeticion;

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
        Date fechaInicio = IOFile.formatearFecha(vista.getDiaInicio(), String.valueOf(vista.getMesInicio()), vista.getAñoInicio());
        Date fechaFin = IOFile.formatearFecha(vista.getDiaFin(), String.valueOf(vista.getMesFin()), vista.getAñoFin());
        String nombreImagen = IOFile.getNombreFichero(vista.getRutaImagen());

        // Creamos la competicion a partir de los datos recogidos de la vista
        competicion = crearCompeticion(vista.getNombre(),
                vista.getLugar(),
                fechaInicio,
                fechaFin,
                nombreImagen,
                vista.getOrganizador());

        // Se realiza una copia local de la imagen (logo) de la competición
        IOFile.copiarFichero(vista.getRutaImagen(),
                System.getProperty("user.dir") + "/resources/img/");

        // Se crea permisos al usuario para esta competición
        crearAdministrado(competicion, Coordinador.getInstance().getUsuario());

        return competicion;
    }

    /**
     * Crea una competición con los datos pasados como parámetros
     *
     * @param nombre nombre de la competición
     * @param lugar lugar de la competición.
     * @param fechaInicio fecha en la que comienza la competición.
     * @param fechaFin fecha en la que termina la competición.
     * @param nombreImagen nombre de la imagen que será su logo.
     * @param organizador organizador de la competición.
     * @return Competicion
     * @throws controlador.InputException
     */
    public Competicion crearCompeticion(String nombre,
            String lugar,
            Date fechaInicio,
            Date fechaFin,
            String nombreImagen,
            String organizador) throws InputException {

        CompeticionJpa competicionjpa = new CompeticionJpa();

        // Se comprueba que el nombre es no vacío y que no hay una competición ya creada
        // con dicho nombre
        if (nombre != null && nombre.length() > 0 && competicionjpa.findCompeticionByName(nombre) == null) {
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
            throw new InputException("Nombre de competición no válido");
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

        // Si la imagen ha cambiado, hacemos una copia del fichero y ponemos
        // la nueva imagen.
        if (!c.getImagen().equals(oldpath)) {
            IOFile.copiarFichero(vista.getRutaImagen(),
                    System.getProperty("user.dir") + "/resources/img/");
        }
        return c;

    }

    /**
     * Modifica y devuelve la competicion "competicion" con los datos pasados
     * como parámetros.
     *
     * @param competicion Objeto Competicion a modificar
     * @param nombre nombre de la competición
     * @param lugar lugar de la competición.
     * @param fechaInicio fecha en la que comienza la competición.
     * @param fechaFin fecha en la que termina la competición.
     * @param nombreImagen nombre de la imagen que será su logo.
     * @param organizador organizador de la competición.
     * @return Competicion
     * @throws controlador.InputException
     */
    public Competicion modificarCompeticion(Competicion competicion,
            String nombre,
            String lugar,
            Date fechaInicio,
            Date fechaFin,
            String nombreImagen,
            String organizador) throws InputException {

        CompeticionJpa competicionjpa = new CompeticionJpa();

        // Se comprueba que el nombre tenga una longitud > 0
        if (nombre != null && nombre.length() > 0) {

            Competicion c = competicionjpa.findCompeticionByName(nombre);
            if (c == null || c.equals(competicion)) {
                competicion.setNombre(nombre);
                competicion.setOrganizador(organizador);
                competicion.setCiudad(lugar);
                competicion.setFechainicio(fechaInicio);
                competicion.setFechafin(fechaFin);
                competicion.setImagen(nombreImagen);
                try {
                    // Cargamos la modificación en la base de datos
                    competicionjpa.edit(competicion);
                } catch (dao.exceptions.NonexistentEntityException ex) {
                    throw new InputException("Competición no encontrada");
                } catch (Exception ex) {
                    throw new InputException(ex.getMessage());
                }
            } else {
                throw new InputException("Nombre de competición ocupado");
            }
        } else {
            throw new InputException("Nombre de competición no válido");
        }
        return competicion;
    }

    /**
     * Elimina la Competicion y todo sus datos (grupos, participantes,
     * resultados) dado su nombre
     *
     * @param nombreCompeticion Nombre de la competicion
     * @throws controlador.InputException
     */
    public static void eliminarCompeticion(String nombreCompeticion) throws InputException {

        CompeticionJpa competicionjpa = new CompeticionJpa();
        // Obtenemos la competicion a partir de su nombre
        Competicion c = competicionjpa.findCompeticionByName(nombreCompeticion);

        // Se comprueba que la competición existe
        if (c != null) {
            CompuestaJpa compuestajpa = new CompuestaJpa();
            List<Compuesta> compuesta = compuestajpa.findCompuestaByCompeticion(c.getId());

            AdministradoJpa admjpa = new AdministradoJpa();
            List<Administrado> administra = admjpa.findAdministradoByCompeticion(c);

            try {
                for (Administrado temp : administra) {
                    admjpa.destroy(temp.getId());
                }

                // Eliminamos los grupos
                GrupoJpa grupojpa = new GrupoJpa();
                List<Grupo> grupos = grupojpa.findGruposRaizByCompeticion(c);
                for (Grupo grupo : grupos) {
                    ControlGrupos.eliminarGrupo(c,grupo.getId());
                }

                // Eliminamos las pruebas
                PruebaJpa pruebajpa = new PruebaJpa();
                for (Compuesta comp : compuesta) {
                    compuestajpa.destroy(comp.getId());
                    pruebajpa.destroy(comp.getPruebaId().getId());
                }

                // Eliminamos la competicion
                competicionjpa.destroy(c.getId());
            } catch (dao.exceptions.IllegalOrphanException | dao.exceptions.NonexistentEntityException e) {
                throw new InputException("No se pudo eliminar algún dato de la competición seleccionada");
            }
        } else {
            throw new InputException("Competición no encontrada");
        }
    }
}
