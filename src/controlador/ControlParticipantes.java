package controlador;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import main.ImportarParticipantes;
import modelo.Competicion;
import modelo.Equipo;
import modelo.Grupo;
import modelo.Participante;
import modelo.Registro;
import dao.EquipoJpa;
import dao.GrupoJpa;
import dao.InscripcionJpa;
import dao.ParticipanteJpa;
import dao.PruebaJpa;
import dao.RegistroJpa;
import dao.exceptions.NonexistentEntityException;
import modelo.Inscripcion;
import modelo.Prueba;
import vista.VistaParticipantes;

/**
 *
 * @author JuanM
 */
public class ControlParticipantes implements ActionListener {

    private VistaParticipantes vista;

    /**Constructor que asocia la vista al controlador
     * 
     * @param vista Vista del controlador (Interfaz)
     */
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
                        vista.getEquipoParticipante(),
                        vista.getPruebaAsignadaParticipante());
                if (p != null) {
                    // Actualizamos la vista
                    vista.añadirParticipanteATabla(new Object[]{
                        p.getId(),
                        p.getDorsal(),
                        p.getApellidos(),
                        p.getNombre(),
                        p.getGrupoId().getNombre(),
                        p.getEquipoId()!=null?p.getEquipoId().getNombre():""});
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
                break;
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
     * @param pruebaAsignada Nombre de la prueba asignada o "Ninguno"
     * @return Participante si ha sido correctamente creado correctamente, null
     * en otro caso
     */
    public static Participante crearParticipante(String nombre, String apellidos,
            Integer dorsal, String nombreGrupo, Integer edad, Integer sexo,
            String nombreEquipo, String pruebaAsignada) {
        

        // Comprobamos que el nombre, apellidos, dorsal y grupo del participante
        // son válidos
        if (nombre != null && apellidos != null && dorsal != null
                && nombreGrupo != null && dorsalLibre(dorsal,
                        Coordinador.getInstance().getSeleccionada())) {

            ParticipanteJpa participantejpa = new ParticipanteJpa();
            GrupoJpa grupojpa = new GrupoJpa();

            // Creamos un objeto Persona y establecemos sus atributos
            // a partir de los datos de la vista
            Participante participante = new Participante();
            
            // Buscamos el grupo por el nombre
            Grupo g = grupojpa.findGrupoByNombreAndCompeticion(nombreGrupo,
                          Coordinador.getInstance().getSeleccionada().getId());
            
            participante.setNombre(nombre);
            participante.setApellidos(apellidos);
            participante.setDorsal(dorsal);
            participante.setEdad(edad);
            participante.setSexo(sexo);
            participante.setGrupoId(g);
            
            // Si se ha seleccionado alguna prueba
            if (!pruebaAsignada.equals("Ninguna")){
                PruebaJpa pruebajpa = new PruebaJpa();
                
                participante.setPruebaasignada(pruebajpa.findPruebaByNombreCompeticion(pruebaAsignada, 
                        Coordinador.getInstance().getSeleccionada().getId()));
            }

            // Si se ha seleccionado un equipo
            if (!nombreEquipo.equals("Ninguno")) {
                EquipoJpa equipojpa = new EquipoJpa();
                
                participante.setEquipoId(equipojpa.findByNombreAndCompeticion(
                        nombreEquipo.toString(),
                        Coordinador.getInstance().getSeleccionada().getId()));
            }
            participantejpa.create(participante);

            return participante;
        } else {
            return null;
        }
    }

    /**
     * Elimina al participante cuyo id es "participanteid"
     *
     * @param participanteid Id del participante
     * @return true si se ha eliminado el participante correctamente
     */
    public boolean eliminarParticipante(Integer participanteid) {
        
        // Comprobamos que el id es válido
        if (participanteid != -1) {

            ParticipanteJpa participantejpa = new ParticipanteJpa();

            //Buscamos el participante a partid del ID
            Participante participante = participantejpa.findParticipante(participanteid);
            eliminarRegistros(participante);
            try {
                participantejpa.destroy(participante.getId());
            } catch (NonexistentEntityException ex) {
                Logger.getLogger(ControlParticipantes.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            vista.eliminarParticipanteDeTabla();
            return true;
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

            ParticipanteJpa participantejpa = new ParticipanteJpa();
            GrupoJpa grupojpa = new GrupoJpa();

            try {
                // Busca a la persona identificada por el participante
                // y modifica sus atributos con los datos de la vista
                Participante participante = participantejpa.findParticipante(participanteid);
                participante.setNombre(vista.getNombreParticipante());
                participante.setApellidos(vista.getApellidosParticipante());
                participante.setDorsal(vista.getDorsalParticipante());
                participante.setEdad(vista.getEdadParticipante());
                participante.setSexo(vista.getSexoParticipante());
                participantejpa.edit(participante);

                // Cambia el grupo al que pertenece el participante
                Grupo g = grupojpa.findGrupoByNombreAndCompeticion(vista.getGrupoParticipante(),
                                Coordinador.getInstance().getSeleccionada().getId());
                participante.setGrupoId(g);
                // Si el participante tiene registros, se cambia la inscripción del registro
                RegistroJpa registrojpa = new RegistroJpa();
                List<Registro> registros = registrojpa.findByParticipante(participanteid);
                if(registros!=null){
                    InscripcionJpa inscripcionjpa = new InscripcionJpa();
                    Inscripcion inscripcion = inscripcionjpa.findInscripcionByCompeticionByGrupo(Coordinador.getInstance().getSeleccionada().getId(),
                            g.getId());
                    for(Registro r : registros){
                        r.setInscripcionId(inscripcion);
                        registrojpa.edit(r);
                    }
                }
                
                // Si se ha seleccionado una prueba
                if(!vista.getPruebaAsignadaParticipante().equals("Ninguna")){
                    PruebaJpa pruebajpa = new PruebaJpa();
                    Prueba p = pruebajpa.findPruebaByNombreCompeticion(vista.getPruebaAsignadaParticipante(),
                            Coordinador.getInstance().getSeleccionada().getId());
                    if(p!=null){
                        participante.setPruebaasignada(p);
                    }
                }else{
                    participante.setPruebaasignada(null);
                }

                // Si se ha seleccionado un equipo
                if (!vista.getEquipoParticipante().equals("Ninguno")) {
                    // Buscamos el equipo y ponemos al participante como miembro
                    EquipoJpa equipojpa = new EquipoJpa();
                    Equipo equiponuevo = equipojpa.findByNombreAndCompeticion(
                            vista.getEquipoParticipante(),
                            Coordinador.getInstance().getSeleccionada().getId());
                    participante.setEquipoId(equiponuevo);
                } else {
                    participante.setEquipoId(null);
                }
                participantejpa.edit(participante);

                // Actualizamos la vista
                vista.eliminarParticipanteDeTabla();
                vista.añadirParticipanteATabla(new Object[]{participante.getId(),
                    participante.getDorsal(),
                    participante.getApellidos(),
                    participante.getNombre(),
                    g.getNombre(),
                    participante.getEquipoId() == null
                    ? "Ninguno"
                    : participante.getEquipoId().getNombre()});
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
     * Elimina los registros de un participante
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
        ParticipanteJpa participantejpa = new ParticipanteJpa();
        return (participantejpa.findByDorsalAndCompeticion(dorsal,
                competicion.getId())) == null;
    }

    /**
     * Comprueba que el dorsal esté disponible o que lo tenga
     * el propio participante que lo solicita
     *
     * @param dorsal Dorsal del participante
     * @param participanteid identificador del participante
     * @return true si el dorsal está libre o es el del participante cuyo id es
     * participanteid
     */
    private boolean dorsalLibreOMio(Integer dorsal, Integer participanteid) {
        ParticipanteJpa participantejpa = new ParticipanteJpa();
        Participante participante = participantejpa.findByDorsalAndCompeticion(dorsal,
                Coordinador.getInstance().getSeleccionada().getId());
        return (participante == null || participante.getId() == participanteid);
    }
}
