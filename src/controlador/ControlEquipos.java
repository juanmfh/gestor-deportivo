package controlador;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import modelo.Equipo;
import modelo.Grupo;
import dao.EquipoJpa;
import dao.GrupoJpa;
import dao.exceptions.NonexistentEntityException;
import modelo.Competicion;
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
                    equipo = crearEquipo(Coordinador.getInstance().getSeleccionada(),vista.getNombreEquipo(),
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
                    equipo = modificarEquipo(vista.getEquipoSelected(),
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
                            eliminarEquipo(vista.getEquipoSelected());
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

    /**
     * Crea un equipo nuevo
     *
     * @param competicion Competicion donde se creará el equipo
     * @param nombre Nombre del equipo
     * @param nombreGrupo Nombre del grupo al que pertenece el equipo
     * @return Equipo si se ha podido crear el equipo
     * @throws controlador.InputException
     */
    public static Equipo crearEquipo(Competicion competicion,String nombre, String nombreGrupo) throws InputException {

        EquipoJpa equipojpa = new EquipoJpa();
        GrupoJpa grupojpa = new GrupoJpa();
        Equipo equipo = null;

        // Se comprueba que el grupo es válido
        // y el nombre del equipo es único en la competición
        if (nombre != null && nombre.length() > 0
                && equipojpa.findByNombreAndCompeticion(nombre, competicion.getId()) == null) {

            // Obtenemos el grupo a partir del nombre de este
            Grupo grupo = grupojpa.findGrupoByNombreAndCompeticion(nombreGrupo, competicion.getId());
            if (grupo != null) {
                // Creamos el equipo
                equipo = new Equipo();
                equipo.setNombre(nombre);
                equipo.setGrupoId(grupo);
                equipojpa.create(equipo);
            } else {
                throw new InputException("Grupo no válido");
            }
        } else {
            throw new InputException("Nombre de equipo no válido u ocupado");
        }
        return equipo;
    }

    /**
     * Elimina el equipo cuyo id es equipoid. (No elimina a sus participantes)
     *
     * @param equipoid Identificador del equipo a modificar
     * @throws controlador.InputException
     */
    public void eliminarEquipo(Integer equipoid) throws InputException {

        // Comprobamos que se ha seleccionado un equipo 
        EquipoJpa equipojpa = new EquipoJpa();
        try {
            // Eliminamos el equipo
            equipojpa.destroy(equipoid);
        } catch (NonexistentEntityException ex) {
            throw new InputException("Equipo no encontrado");
        }
    }

    /**
     * Modifica el nombre y grupo (en caso de que el equipo no tenga miembros
     * asociados) del equipo cuyo id es equipoid
     *
     * @param equipoid Identificador del equipo a modificar
     * @return Equipo si se ha modificado correctamente
     * @throws controlador.InputException
     */
    private Equipo modificarEquipo(Integer equipoid,String nombreEquipo, String nombreGrupo) throws InputException {

        
        EquipoJpa equipojpa = new EquipoJpa();

        Equipo antiguo = equipojpa.findByNombreAndCompeticion(
                nombreEquipo,
                Coordinador.getInstance().getSeleccionada().getId());

        Equipo equipo = equipojpa.findEquipo(equipoid);

        // Se comprueba que el equipo a modificar es válido
        if (equipo != null) {
            // Comprobamos que el nombre del equipo no existe o es suyo
            if (antiguo == null || antiguo.getId() == equipoid) {
                equipo.setNombre(nombreEquipo);
                try {
                    GrupoJpa grupojpa = new GrupoJpa();

                    // Si el equipo no tiene miembros modificamos su grupo
                    if (equipo.getParticipanteCollection().isEmpty()) {

                        // Buscamos el grupo con el nombre obtenido en la vista
                        Grupo g = grupojpa.findGrupoByNombreAndCompeticion(nombreGrupo,
                                Coordinador.getInstance().getSeleccionada().getId());
                        // Comprobamos que el grupo existe
                        if (g != null) {
                            // Cambiamos el grupo
                            equipo.setGrupoId(g);
                        }
                    } else {
                        throw new InputException("No se puede modificar el grupo de un equipo con miembros asignados");
                    }
                    // Guardamos los cambios en la base de datos
                    equipojpa.edit(equipo);

                } catch (NonexistentEntityException ex) {
                    throw new InputException("Equipo no encontrado");
                } catch (Exception ex) {
                    throw new InputException(ex.getMessage());
                }
            }
        } else {
            throw new InputException("Equipo no encontrado");
        }
        return equipo;
    }
}
