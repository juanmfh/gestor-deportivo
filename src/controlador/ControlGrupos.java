package controlador;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import modelo.Grupo;
import modelo.Inscripcion;
import modelo.dao.GrupoJpa;
import modelo.dao.InscripcionJpa;
import modelo.dao.exceptions.NonexistentEntityException;
import vista.GruposTab;
import vista.VistaGrupos;

/**
 *
 * @author JuanM
 */
public class ControlGrupos implements ActionListener {

    private GruposTab vista;

    public ControlGrupos(GruposTab vista) {
        this.vista = vista;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();
        switch (command) {
            case VistaGrupos.CREARGRUPO:
                Grupo g = crearGrupo(vista.getNombreGrupo(),
                        vista.getSubgrupoDeComboBox().getSelectedItem().toString());
                if (g != null) {
                    vista.añadirGrupoATabla(new Object[]{
                        g.getId(),
                        g.getNombre(),
                        g.getGrupoId() != null ?
                                g.getGrupoId().getNombre() :
                                null});
                    Coordinador.getInstance().getControladorPrincipal().cargarSubGruposComboBox();
                    vista.limpiarFormularioGrupo();
                    Coordinador.getInstance().setEstadoLabel(
                            "Grupo creado correctamente", Color.BLUE);
                } else {
                    Coordinador.getInstance().setEstadoLabel(
                            "Nombre de grupo incorrecto/ocupado", Color.RED);
                }
                break;
            case VistaGrupos.MODIFICARGRUPO:
                if (modificarGrupo(vista.getGrupoSelected())) {
                    Coordinador.getInstance().getControladorPrincipal().cargarTablaGrupos();
                    Coordinador.getInstance().getControladorPrincipal().cargarSubGruposComboBox();
                    Coordinador.getInstance().setEstadoLabel(
                            "Grupo modificado correctamente", Color.BLUE);
                } else {
                    Coordinador.getInstance().setEstadoLabel(
                            "No se pudo modificar el grupo seleccionado", Color.RED);
                }
                break;
            case VistaGrupos.ELIMINARGRUPO:
                if (vista.getGrupoSelected() != -1) {
                    int confirmDialog = JOptionPane.showConfirmDialog(null,
                            "¿Está seguro de que desea eliminar el grupo seleccionado?",
                            "Aviso",
                            JOptionPane.YES_NO_OPTION);
                    if (confirmDialog == JOptionPane.YES_OPTION) {
                        if (eliminarGrupo(vista.getGrupoSelected())) {
                            vista.limpiarFormularioGrupo();
                            Coordinador.getInstance().getControladorPrincipal().cargarSubGruposComboBox();
                            Coordinador.getInstance().getControladorPrincipal().cargarTablaGrupos();
                            Coordinador.getInstance().setEstadoLabel(
                                    "Grupo eliminado correctamente", Color.BLUE);
                        } else {
                            Coordinador.getInstance().setEstadoLabel(
                                    "No se pudo eliminar el grupo seleccionado",
                                    Color.RED);
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
     * @param nombre Nombre del grupo
     * @param subGrupoDe Grupo superior
     * @return Grupo creado o null si no se ha podido crear
     */
    public static Grupo crearGrupo(String nombre, String subGrupoDe) {

        // Comprobamos el nombre del grupo 
        if (nombre.length() > 0) {
            
            GrupoJpa grupojpa = new GrupoJpa();
            InscripcionJpa inscripcionjpa = new InscripcionJpa();
            

            // Comprobamos que en la competición no haya ningún grupo con el mismo nombre            
            if (inscripcionjpa.findInscripcionByCompeticionByNombreGrupo(Coordinador.getInstance().getControladorPrincipal().getSeleccionada().getId(), nombre) == null) {
                
                // Establecemos el nombre del grupo
                Grupo g = new Grupo();
                g.setNombre(nombre);
                
                // Establecemos el subgrupo al cual pertenece
                //String subgrupode = vista.getSubgrupoDeComboBox().getSelectedItem().toString();
                if (subGrupoDe != null && !subGrupoDe.equals("Ninguno")) {
                    g.setGrupoId(grupojpa.findGrupoByNombreAndCompeticion(subGrupoDe, Coordinador.getInstance().getControladorPrincipal().getSeleccionada().getId()));
                }
                
                // Creamos el grupo
                grupojpa.create(g);

                // Inscribimos el grupo en la competición
                Inscripcion i = new Inscripcion();
                i.setCompeticionId(Coordinador.getInstance().getControladorPrincipal().getSeleccionada());
                i.setGrupoId(g);
                inscripcionjpa.create(i);
                
                return g;
            } else {
                return null;
            }
        } else {
            return null;
        }

    }

    /**
     * Modifica el grupo cuyo id es grupoid
     * 
     * @param grupoid Id del grupo a modificar 
     * @return true si el grupo ha sido modificado correctamente
     */
    private boolean modificarGrupo(Integer grupoid) {
        
        // Comprobamos que el id del grupo es válido y el nombre no es vacío.
        if (grupoid != -1 && vista.getNombreGrupo().length() > 0) {
            
                // Compbrobamos que el nombre no está ocupado por otro grupo
                // en la misma competición
                GrupoJpa grupojpa = new GrupoJpa();
                Grupo antiguo = grupojpa.findGrupoByNombreAndCompeticion(vista.getNombreGrupo(), Coordinador.getInstance().getControladorPrincipal().getSeleccionada().getId());
                Grupo g = grupojpa.findGrupo(grupoid);
                if (antiguo == null || antiguo.getId() == g.getId()) {
                    
                    // Modificamos el nombre
                    g.setNombre(vista.getNombreGrupo());
                    try {
                        //Falta modificar el campo subgrupode
                        grupojpa.edit(g);
                    } catch (NonexistentEntityException ex) {
                        return false;
                    } catch (Exception ex) {
                        return false;
                    }
                    // Actualizamos la vista
                    vista.añadirGrupoATabla(new Object[]{g.getId(), g.getNombre(), g.getGrupoId() != null ? g.getGrupoId().getNombre() : null});
                    return true;
                }
        }
        return false;
    }

    /**Elimina al grupo, participantes del grupo y registros de los 
     * participantes cuyo id es grupoid
     * 
     * @param grupoid Id del grupo a eliminar
     * @return true si ha sido posible eliminar al grupo
     */
    private boolean eliminarGrupo(Integer grupoid) {
        
        // Comprobamos que el id del grupo es válido
        if (grupoid != -1) {
            
            GrupoJpa grupojpa = new GrupoJpa();
            Coordinador.getInstance().getControladorPrincipal().eliminarGrupo(grupojpa.findGrupo(grupoid));
            return true;
        }
        return false;
    }
}
