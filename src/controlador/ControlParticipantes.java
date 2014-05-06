package controlador;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import main.IOFile;
import main.ImportarParticipantes;
import modelo.Competicion;
import modelo.Equipo;
import modelo.Grupo;
import modelo.Miembro;
import modelo.Participa;
import modelo.Participante;
import modelo.Persona;
import modelo.Registro;
import modelo.dao.EquipoJpa;
import modelo.dao.GrupoJpa;
import modelo.dao.MiembroJpa;
import modelo.dao.ParticipaJpa;
import modelo.dao.ParticipanteJpa;
import modelo.dao.PersonaJpa;
import modelo.dao.RegistroJpa;
import modelo.dao.exceptions.IllegalOrphanException;
import modelo.dao.exceptions.NonexistentEntityException;
import vista.VistaParticipantes;
import main.*;

/**
 *
 * @author JuanM
 */
public class ControlParticipantes implements ActionListener {

    private VistaParticipantes vista;

    public ControlParticipantes(VistaParticipantes vista) {
        this.vista = vista;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();
        switch (command) {
            case VistaParticipantes.CREARPARTICIPANTE:
                Participante p = crearParticipante(
                        vista.getNombreParticipante(),
                        vista.getApellidosParticipante(),
                        vista.getDorsalParticipante(),
                        vista.getGrupoParticipante(),
                        vista.getEdadParticipante(),
                        vista.getSexoParticipante(),
                        vista.getEquipoParticipante());
                if (p != null) {
                    // Actualizamos la vista
                    vista.añadirParticipanteATabla(new Object[]{
                        p.getPersonaId().getId(),
                        p.getPersonaId().getDorsal(),
                        p.getPersonaId().getApellidos(),
                        p.getPersonaId().getNombre(),
                        vista.getGrupoParticipante(),
                        vista.getEquipoParticipante()});
                    vista.limpiarFormularioParticipante();
                    Coordinador.getInstance().setEstadoLabel(
                            "Participante creado correctamente", Color.BLUE);
                } else {
                    Coordinador.getInstance().setEstadoLabel(
                            "Datos del participante incorrectos", Color.RED);
                }
                break;
            case VistaParticipantes.MODIFICARPARTICIPANTE:
                if (modificarParticipante(vista.getParticipanteSeleccionado())) {
                    Coordinador.getInstance().setEstadoLabel(
                            "Participante modificado correctamente", Color.BLUE);
                    vista.limpiarFormularioParticipante();

                }
                break;
            case VistaParticipantes.ELIMINARPARTICIPANTE:
                if (vista.getParticipanteSeleccionado() != -1) {
                    int confirmDialog = JOptionPane.showConfirmDialog(null,
                            "¿Está seguro de que desea eliminar el participante seleccionado?",
                            "Aviso",
                            JOptionPane.YES_NO_OPTION);
                    if (confirmDialog == JOptionPane.YES_OPTION) {
                        if (eliminarParticipante(vista.getParticipanteSeleccionado())) {
                            vista.limpiarFormularioParticipante();
                            Coordinador.getInstance().setEstadoLabel(
                                    "Participante eliminado correctamente", Color.BLUE);
                        }
                    }
                }
                break;
            case VistaParticipantes.LIMPIARPARTICIPANTE:
                vista.limpiarFormularioParticipante();

            case VistaParticipantes.IMPORTAR:
                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fc.setDialogTitle("Abrir");
                int res = fc.showOpenDialog(null);
                if (res == JFileChooser.APPROVE_OPTION) {
                    
                    Coordinador.getInstance().setEstadoLabel("Importando participantes ...", Color.BLACK);
                    Coordinador.getInstance().mostrarBarraProgreso(true);
                    
                    ImportarParticipantes inPart;
                    (inPart = new ImportarParticipantes(fc.getSelectedFile().getPath())).execute();
                      
                }

        }
    }

    /**
     * Crea un participante nuevo
     *
     * @param nombre Nombre del participante
     * @param apellidos Apellidos del participante
     * @param dorsal Dorsal del participante. Debe de ser único en la
     * competicion
     * @param nombreGrupo Nombre del grupo al que pertenece
     * @param edad Edad del participante
     * @param sexo Sexo del participante (1 == Hombre, 0 == Mujer)
     * @param nombreEquipo Nombre del equipo del que es miembro o "Ninguno"
     * @return Participante si ha sido correctamente creado correctamente, null
     * en otro caso
     */
    public static Participante crearParticipante(String nombre, String apellidos,
            Integer dorsal, String nombreGrupo, Integer edad, Integer sexo,
            String nombreEquipo) {

        // Comprobamos que el nombre, apellidos, dorsal y grupo del participante
        // son válidos
        if (nombre != null && apellidos != null && dorsal != null
                && nombreGrupo != null && dorsalLibre(dorsal,
                        Coordinador.getInstance().getSeleccionada())) {

            PersonaJpa personajpa = new PersonaJpa();
            ParticipanteJpa participantejpa = new ParticipanteJpa();
            ParticipaJpa participajpa = new ParticipaJpa();
            GrupoJpa grupojpa = new GrupoJpa();

            // Creamos un objeto Persona y establecemos sus atributos
            // a partir de los datos de la vista
            Persona p = new Persona();
            p.setNombre(nombre);
            p.setApellidos(apellidos);
            p.setDorsal(dorsal);
            p.setEdad(edad);
            p.setSexo(sexo);
            personajpa.create(p);

            // Creamos un participante que representa a la persona
            Participante participante = new Participante();
            participante.setPersonaId(p);
            participantejpa.create(participante);

            // Creamos una participación de la persona en el grupo seleccionado
            // en la vista
            Participa participa = new Participa();
            Grupo g = grupojpa.findGrupoByNombre(nombreGrupo);
            participa.setGrupoId(g);
            participa.setParticipanteId(participante);
            participajpa.create(participa);

            // Si se ha seleccionado un equipo
            Miembro miembro = null;
            if (!nombreEquipo.equals("Ninguno")) {

                MiembroJpa miembrojpa = new MiembroJpa();
                EquipoJpa equipojpa = new EquipoJpa();

                // Creamos un objeto miembro con el equipo seleccionado y la 
                // persona que hemos creado anteriormente
                miembro = new Miembro();
                miembro.setEquipoId(equipojpa.findByNombreAndCompeticion(
                        nombreEquipo.toString(),
                        Coordinador.getInstance().getSeleccionada().getId()));
                miembro.setPersonaId(p);
                miembrojpa.create(miembro);
            }

            return participante;
        } else {
            return null;
        }
    }

    /**
     * Elimina al participante cuyo id es "personaid"
     *
     * @param personaid Id de la persona
     * @return true si se ha eliminado el participante correctamente
     */
    public boolean eliminarParticipante(Integer personaid) {

        // Comprobamos que el id es válido
        if (personaid != -1) {

            PersonaJpa personajpa = new PersonaJpa();
            ParticipanteJpa participantejpa = new ParticipanteJpa();
            ParticipaJpa participajpa = new ParticipaJpa();

            //Buscamos el participante que representa a la persona
            //con id "personaid"
            Participante participante = participantejpa.findByPersonaId(personaid);
            try {
                // Eliminamos sus registros
                eliminarRegistros(participante);

                // Eliminamos su participación en la competición
                Participa participa
                        = participajpa.findByCompeticionAndParticipante(
                                Coordinador.getInstance().getSeleccionada().getId(),
                                participante.getId());
                participajpa.destroy(participa.getId());

                // Comprobamos que no participa en más competiciones
                if (participajpa.countByParticipante(participante.getId()) == 0) {

                    // Eliminamos al participante
                    participantejpa.destroy(participante.getId());

                    // Buscamos en que equipos es miembro
                    MiembroJpa miembrojpa = new MiembroJpa();
                    List<Miembro> miembro = miembrojpa.findByPersona(personaid);
                    // Eliminamos al participante de los equipos a los que pertenece
                    for (Miembro m : miembro) {
                        miembrojpa.destroy(m.getId());
                    }
                    // Eliminamos a la persona
                    personajpa.destroy(personaid);
                }
                // Actualizamos la vista
                vista.eliminarParticipanteDeTabla();
                return true;
            } catch (IllegalOrphanException | NonexistentEntityException ex) {
                return false;
            }
        }
        return false;
    }

    /**
     * Modifica los datos del participante cuyo id es "participanteid"
     *
     * @param participanteid Identificador del participante a modificar
     * @return true si se ha modificado correctamte los datos del participante
     */
    public boolean modificarParticipante(Integer participanteid) {

        // Comprueba que los datos de la vista sean válidos y el dorsal esté
        // disponible
        if (participanteid != null
                && vista.getNombreParticipante() != null
                && vista.getApellidosParticipante() != null
                && vista.getDorsalParticipante() != null
                && vista.getGrupoParticipante() != null
                && dorsalLibreOMio(vista.getDorsalParticipante(), participanteid)) {

            PersonaJpa personajpa = new PersonaJpa();
            ParticipanteJpa participantejpa = new ParticipanteJpa();
            ParticipaJpa participajpa = new ParticipaJpa();
            GrupoJpa grupojpa = new GrupoJpa();

            try {
                // Busca a la persona identificada por el participante
                // y modifica sus atributos con los datos de la vista
                Persona p = personajpa.findPersona(participanteid);
                p.setNombre(vista.getNombreParticipante());
                p.setApellidos(vista.getApellidosParticipante());
                p.setDorsal(vista.getDorsalParticipante());
                p.setEdad(vista.getEdadParticipante());
                p.setSexo(vista.getSexoParticipante());
                personajpa.edit(p);

                // Busca el grupo al que pertenece el participante
                MiembroJpa miembrojpa = new MiembroJpa();
                Grupo g = grupojpa.findByPersonaCompeticion(
                        Coordinador.getInstance().getSeleccionada().getId(),
                        p.getId());

                //Busca si el participante pertenece a algún equipo y lo elimina
                Miembro miembroviejo = miembrojpa.findByPersonaGrupo(p.getId(),
                        g.getId());
                if (miembroviejo != null) {
                    miembrojpa.destroy(miembroviejo.getId());
                }

                // Cambia el grupo al que pertenece el participante
                Participante part = participantejpa.findByPersonaId(p.getId());
                g = grupojpa.findGrupoByNombre(vista.getGrupoParticipante());
                Participa participa = participajpa.findByCompeticionAndParticipante(
                        Coordinador.getInstance().getSeleccionada().getId(),
                        part.getId());
                participa.setGrupoId(g);
                participajpa.edit(participa);

                Miembro miembro = null;
                // Si se ha seleccionado un equipo
                if (!vista.getEquipoParticipante().equals("Ninguno")) {
                    // Buscamos el equipo y ponemos al participante como miembro
                    EquipoJpa equipojpa = new EquipoJpa();
                    Equipo equiponuevo = equipojpa.findByNombreAndCompeticion(
                            vista.getEquipoParticipante(),
                            Coordinador.getInstance().getSeleccionada().getId());
                    miembro = new Miembro();
                    miembro.setEquipoId(equiponuevo);
                    miembro.setPersonaId(p);
                    miembrojpa.create(miembro);
                }

                // Actualizamos la vista
                vista.eliminarParticipanteDeTabla();
                vista.añadirParticipanteATabla(new Object[]{p.getId(),
                    p.getDorsal(),
                    p.getApellidos(),
                    p.getNombre(),
                    g.getNombre(),
                    miembro == null
                    ? "Ninguno"
                    : miembro.getEquipoId().getNombre()});
            } catch (NonexistentEntityException ex) {
                return false;
            } catch (Exception ex) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Elimina los registros del participante "participante"
     *
     * @param participante Participante asociado a esos registros
     */
    private void eliminarRegistros(Participante participante) {

        RegistroJpa registrosjpa = new RegistroJpa();
        // Buscamos todos los registros de "participante"
        List<Registro> registros = registrosjpa.findByParticipante(participante.getId());
        try {
            // Eliminamos cada registro 
            for (Registro r : registros) {
                registrosjpa.destroy(r.getId());
            }
        } catch (NonexistentEntityException ex) {
        }
    }

    /**
     * Comprueba que el dorsal "dorsal" no esta ocupado en la competicion
     * seleccionada
     *
     * @param competicion Objeto Competicion
     * @param dorsal Numero del dorsal
     * @return true si el dorsal no está ocupado
     */
    private static boolean dorsalLibre(Integer dorsal, Competicion competicion) {
        PersonaJpa personajpa = new PersonaJpa();
        return (personajpa.findByDorsalAndCompeticion(dorsal,
                competicion.getId())) == null;
    }

    /**
     * Comprueba que el dorsal solo esté ocupado
     *
     * @param dorsal Dorsal del participante
     * @param personaid identificador de la persona
     * @return true si el dorsal está libre o es el de la persona cuyo id es
     * personaid
     */
    private boolean dorsalLibreOMio(Integer dorsal, Integer personaid) {
        PersonaJpa personajpa = new PersonaJpa();
        Persona persona = personajpa.findByDorsalAndCompeticion(dorsal,
                Coordinador.getInstance().getSeleccionada().getId());
        return (persona == null || persona.getId() == personaid);
    }
}
