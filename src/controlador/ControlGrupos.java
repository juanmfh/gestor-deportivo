package controlador;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import modelo.Grupo;
import modelo.Inscripcion;
import dao.GrupoJpa;
import dao.InscripcionJpa;
import dao.exceptions.NonexistentEntityException;
import modelo.Competicion;
import vista.GruposTab;
import vista.VistaGrupos;

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
    public ControlGrupos(GruposTab vista) {
        this.vista = vista;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();
        switch (command) {
            case VistaGrupos.CREARGRUPO:
                Grupo g;
                try {
                    g = crearGrupo(vista.getNombreGrupo(),
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
                    g = modificarGrupo(vista.getGrupoSelected(), vista.getNombreGrupo());
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
                            eliminarGrupo(vista.getGrupoSelected());
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

    /**
     * Crea un grupo cuyo nombre debe de ser único en la competición
     *
     * @param nombre Nombre del grupo
     * @param subGrupoDe Grupo superior (null si no pertenece a ningún grupo o
     * cadena "Ninguno")
     * @return Grupo creado
     */
    public static Grupo crearGrupo(String nombre, String subGrupoDe) throws InputException {

        // Comprobamos el nombre del grupo 
        if (nombre.length() > 0) {

            GrupoJpa grupojpa = new GrupoJpa();
            InscripcionJpa inscripcionjpa = new InscripcionJpa();
            Competicion competicion = Coordinador.getInstance().getSeleccionada();

            // Comprobamos que en la competición no haya ningún grupo con el mismo nombre            
            if (inscripcionjpa.findInscripcionByCompeticionByNombreGrupo(competicion.getId(), nombre) == null) {

                // Establecemos el nombre del grupo
                Grupo g = new Grupo();
                g.setNombre(nombre);

                // Establecemos el subgrupo al cual pertenece
                if (subGrupoDe != null && !subGrupoDe.equals("Ninguno")) {
                    g.setGrupoId(grupojpa.findGrupoByNombreAndCompeticion(subGrupoDe, competicion.getId()));
                }

                // Creamos el grupo
                grupojpa.create(g);

                // Inscribimos el grupo en la competición
                Inscripcion i = new Inscripcion();
                i.setCompeticionId(competicion);
                i.setGrupoId(g);
                inscripcionjpa.create(i);

                return g;
            } else {
                throw new InputException("Nombre de Grupo ocupado");
            }
        } else {
            throw new InputException("Nombre de Grupo no válido");
        }

    }

    /**
     * Modifica el grupo cuyo id es grupoid
     *
     * @param grupoid Identificador del grupo a modificar
     * @return true si el grupo ha sido modificado correctamente
     */
    private Grupo modificarGrupo(Integer grupoid, String nombreGrupo) throws InputException {

        GrupoJpa grupojpa = new GrupoJpa();
        Grupo g = grupojpa.findGrupo(grupoid);

        // Comprobamos que el id del grupo es válido y el nombre no es vacío.
        if (g != null) {
            if (nombreGrupo.length() > 0) {
                Grupo antiguo = grupojpa.findGrupoByNombreAndCompeticion(nombreGrupo, Coordinador.getInstance().getSeleccionada().getId());

                // Compbrobamos que el nombre no está ocupado por otro grupo
                // en la misma competición
                if (antiguo == null || antiguo.getId() == g.getId()) {

                    // Modificamos el nombre
                    g.setNombre(nombreGrupo);

                    //Falta modificar el campo subgrupode
                    //Editamos el grupo
                    try {
                        grupojpa.edit(g);
                    } catch (NonexistentEntityException ex) {
                        throw new InputException("No se ha encontrado el grupo");
                    } catch (Exception ex) {
                        throw new InputException(ex.getMessage());
                    }
                } else {
                    throw new InputException("Nombre de grupo ocupado");
                }
            } else {
                throw new InputException("Nombre de grupo no válido");
            }
        } else {
            throw new InputException("Grupo no encontrado");
        }
        return g;
    }

    /**
     * Elimina al grupo, participantes del grupo y registros de los
     * participantes cuyo id es grupoid
     *
     * @param grupoid Id del grupo a eliminar
     */
    private void eliminarGrupo(Integer grupoid) throws InputException {

        GrupoJpa grupojpa = new GrupoJpa();
        Grupo g = grupojpa.findGrupo(grupoid);

        // Comprobamos que el grupo es válido
        if (g != null) {
            Coordinador.getInstance().getControladorPrincipal().eliminarGrupo(g);
        } else {
            throw new InputException("Grupo no encontrado");
        }
    }
}
