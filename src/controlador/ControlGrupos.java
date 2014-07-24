package controlador;

import dao.EquipoJpa;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import modelo.Grupo;
import modelo.Inscripcion;
import dao.GrupoJpa;
import dao.InscripcionJpa;
import dao.ParticipanteJpa;
import dao.RegistroJpa;
import dao.exceptions.IllegalOrphanException;
import dao.exceptions.NonexistentEntityException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.Competicion;
import modelo.Equipo;
import modelo.Participante;
import modelo.Registro;
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
    public ControlGrupos(VistaGrupos vista) {
        this.vista = vista;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();
        switch (command) {
            case VistaGrupos.CREARGRUPO:
                Grupo g;
                try {
                    g = crearGrupo(Coordinador.getInstance().getSeleccionada(), vista.getNombreGrupo(),
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
                            eliminarGrupo(Coordinador.getInstance().getSeleccionada(), vista.getGrupoSelected());
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
     * @param competicion
     * @param nombre Nombre del grupo
     * @param subGrupoDe Grupo superior (null si no pertenece a ningún grupo o
     * cadena "Ninguno")
     * @return Grupo creado
     * @throws controlador.InputException
     */
    public static Grupo crearGrupo(Competicion competicion, String nombre, String subGrupoDe) throws InputException {

        if (competicion != null) {

            // Comprobamos el nombre del grupo 
            if (nombre != null && nombre.length() > 0) {

                GrupoJpa grupojpa = new GrupoJpa();
                InscripcionJpa inscripcionjpa = new InscripcionJpa();

                // Comprobamos que en la competición no haya ningún grupo con el mismo nombre            
                if (inscripcionjpa.findInscripcionByCompeticionByNombreGrupo(competicion.getId(), nombre) == null) {

                    // Establecemos el nombre del grupo
                    Grupo g = new Grupo();
                    g.setNombre(nombre);

                    // Establecemos el subgrupo al cual pertenece
                    if (subGrupoDe != null && !subGrupoDe.equals("Ninguno")) {
                        Grupo aux = grupojpa.findGrupoByNombreAndCompeticion(subGrupoDe, competicion.getId());
                        if (aux == null || aux.getNombre().equals(nombre)) {
                            throw new InputException("Subgrupo no válido");
                        } else {
                            g.setGrupoId(aux);
                        }

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
        } else {
            throw new InputException("Competición no válida");
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
     * @param c
     * @param grupoid Id del grupo a eliminar
     * @throws controlador.InputException
     */
    public static void eliminarGrupo(Competicion c, Integer grupoid) throws InputException {

        if (c != null) {
            if (grupoid != null) {

                GrupoJpa grupojpa = new GrupoJpa();
                Grupo g = grupojpa.findGrupo(grupoid);

                // Comprobamos que el grupo es válido
                if (g != null) {

                    // Obtenemos la lista de subgrupos y los eliminamos
                    List<Grupo> subgrupos = grupojpa.findGrupoByGrupoId(g.getId());
                    for (Grupo subgrupo : subgrupos) {
                        eliminarGrupo(c, subgrupo.getId());
                    }
                    try {
                        // Elimiina las inscripciones
                        eliminarInscripciones(c, g);
                        // Elimina los equipos
                        eliminarEquiposGrupo(g);
                        // Eliminamos los participantes
                        eliminarParticipantes(g);
                        // Eliminamos el grupo
                        grupojpa.destroy(g.getId());
                    } catch (IllegalOrphanException | NonexistentEntityException | InputException ex) {
                        throw new InputException(ex.getMessage());
                    }
                } else {
                    throw new InputException("Grupo no encontrado");
                }
            } else {
                throw new InputException("Identificador de grupo no válido");
            }
        } else {
            throw new InputException("Competición no válida");
        }
    }

    private static void eliminarInscripciones(Competicion c, Grupo g) throws InputException {
        try {
            InscripcionJpa inscrjpa = new InscripcionJpa();
            Inscripcion i = inscrjpa.findInscripcionByCompeticionByGrupo(c.getId(), g.getId());
            if (i != null) {
                // Eliminamos la inscripción en este grupo
                inscrjpa.destroy(i.getId());
            }
        } catch (NonexistentEntityException | IllegalOrphanException ex) {
            throw new InputException(ex.getMessage());
        }
    }

    /**
     * Elimina los participantes de un grupo g
     *
     * @param g Grupo al que pertenecen los participantes
     */
    private static void eliminarParticipantes(Grupo g) throws InputException {

        ParticipanteJpa participantejpa = new ParticipanteJpa();
        RegistroJpa registrosjpa = new RegistroJpa();

        List<Participante> participantes = participantejpa.findParticipantesByGrupo(g.getId());
        try {
            for (Participante participante : participantes) {
                // Eliminamos sus registros
                List<Registro> registros = registrosjpa.findByParticipante(participante.getId());
                for(Registro r: registros){
                    registrosjpa.destroy(r.getId());
                }
                // Eliminamos el participante
                participantejpa.destroy(participante.getId());
            }
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(ControlPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Elimina todos los equipos de un grupo g (No sus participantes) 
     *
     * @param g Grupo al que pertenecen los equipos
     * @throws controlador.InputException
     */
    private static void eliminarEquiposGrupo(Grupo g) throws InputException {

        EquipoJpa equipojpa = new EquipoJpa();
        RegistroJpa registrosjpa = new RegistroJpa();

        // Obtenemos la lista de equipos que participan en el grupo g
        List<Equipo> equipos = equipojpa.findByGrupo(g.getId());
        try {
            // Por cada equipo
            for (Equipo e : equipos) {
                // Eliminamos sus registros
                List<Registro> registros = registrosjpa.findByEquipo(e.getId());
                for(Registro r: registros){
                    registrosjpa.destroy(r.getId());
                }
                // Eliminamos al equipo
                equipojpa.destroy(e.getId());
            }
        } catch (NonexistentEntityException ex) {
            throw new InputException("Equipo no encontrado");
        }
    }

    public List<Grupo> getSubGrupos(Grupo grupo) {
        List<Grupo> res = new CopyOnWriteArrayList();
        GrupoJpa grupoJpa = new GrupoJpa();
        List<Grupo> aux = grupoJpa.findGrupoByGrupoId(grupo.getId());
        for (Grupo g1 : aux) {
            res.add(g1);
        }
        for (Grupo g : res) {
            List<Grupo> aux2 = getSubGrupos(g);
            for (Grupo g2 : aux2) {
                res.add(g2);
            }
        }
        return res;

    }
}
