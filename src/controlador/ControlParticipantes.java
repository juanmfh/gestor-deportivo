package controlador;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
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

    /**
     * Constructor que asocia la vista al controlador
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
                Participante p;
                try {
                    p = crearParticipante(
                            Coordinador.getInstance().getSeleccionada(),
                            vista.getNombreParticipante(),
                            vista.getApellidosParticipante(),
                            vista.getDorsalParticipante(),
                            vista.getGrupoParticipante(),
                            vista.getEdadParticipante(),
                            vista.getSexoParticipante(),
                            vista.getEquipoParticipante(),
                            vista.getPruebaAsignadaParticipante());
                    // Actualizamos la vista
                    vista.añadirParticipanteATabla(new Object[]{
                        p.getId(),
                        p.getDorsal(),
                        p.getApellidos(),
                        p.getNombre(),
                        p.getGrupoId().getNombre(),
                        p.getEquipoId() != null ? p.getEquipoId().getNombre() : "",
                        p.getPruebaasignada() != null ? p.getPruebaasignada().getNombre() : ""});
                    vista.limpiarFormularioParticipante();
                    Coordinador.getInstance().setEstadoLabel("Participante creado correctamente", Color.BLUE);
                } catch (InputException ex) {
                    Coordinador.getInstance().setEstadoLabel(ex.getMessage(), Color.RED);
                }
                break;
            case VistaParticipantes.MODIFICARPARTICIPANTE:
                if (vista.getParticipanteSeleccionado() != -1) {
                    try {
                        p = modificarParticipante(
                                Coordinador.getInstance().getSeleccionada(),
                                vista.getParticipanteSeleccionado(),
                                vista.getNombreParticipante(),
                                vista.getApellidosParticipante(),
                                vista.getDorsalParticipante(),
                                vista.getGrupoParticipante(),
                                vista.getEdadParticipante(),
                                vista.getSexoParticipante(),
                                vista.getEquipoParticipante(),
                                vista.getPruebaAsignadaParticipante()
                        );
                        // Actualizamos la vista
                        vista.eliminarParticipanteDeTabla();
                        vista.añadirParticipanteATabla(new Object[]{
                            p.getId(),
                            p.getDorsal(),
                            p.getApellidos(),
                            p.getNombre(),
                            p.getGrupoId().getNombre(),
                            p.getEquipoId() == null
                            ? "Ninguno"
                            : p.getEquipoId().getNombre(),
                            p.getPruebaasignada() != null ? p.getPruebaasignada().getNombre() : ""});
                        Coordinador.getInstance().setEstadoLabel(
                                "Participante modificado correctamente", Color.BLUE);
                        vista.limpiarFormularioParticipante();
                    } catch (InputException ex) {
                        Coordinador.getInstance().setEstadoLabel(
                                ex.getMessage(), Color.RED);
                    }
                }
                break;
            case VistaParticipantes.ELIMINARPARTICIPANTE:
                if (vista.getParticipanteSeleccionado() != -1) {
                    int confirmDialog = JOptionPane.showConfirmDialog(null,
                            "¿Está seguro de que desea eliminar el participante seleccionado?",
                            "Aviso",
                            JOptionPane.YES_NO_OPTION);
                    if (confirmDialog == JOptionPane.YES_OPTION) {
                        try {
                            eliminarParticipante(vista.getParticipanteSeleccionado());
                            vista.eliminarParticipanteDeTabla();
                            vista.limpiarFormularioParticipante();
                            Coordinador.getInstance().setEstadoLabel(
                                    "Participante eliminado correctamente", Color.BLUE);
                        } catch (InputException ex) {
                            Coordinador.getInstance().setEstadoLabel(
                                    ex.getMessage(), Color.RED);
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
                    ImportarParticipantes inPart;
                    (inPart = new ImportarParticipantes(fc.getSelectedFile().getPath())).execute();
                }

        }
    }

    /**
     * Crea un participante nuevo
     *
     * @param competicion
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
     * @throws controlador.InputException
     */
    public static Participante crearParticipante(Competicion competicion, String nombre, String apellidos,
            Integer dorsal, String nombreGrupo, Integer edad, Integer sexo,
            String nombreEquipo, String pruebaAsignada) throws InputException {

        if (competicion != null) {
            if (nombre != null && nombre.length() > 0) {
                if (apellidos != null && apellidos.length() > 0) {
                    if (nombreGrupo != null && nombreGrupo.length() > 0) {
                        Participante participante = null;
                        if (dorsal != null) {
                            if (dorsalLibre(dorsal, competicion)) {
                                ParticipanteJpa participantejpa = new ParticipanteJpa();
                                GrupoJpa grupojpa = new GrupoJpa();

                                // Creamos un objeto Participante y establecemos sus atributos
                                participante = new Participante();

                                // Buscamos el grupo por el nombre
                                Grupo g = grupojpa.findGrupoByNombreAndCompeticion(nombreGrupo,
                                        competicion.getId());

                                if (g != null) {

                                    participante.setNombre(nombre);
                                    participante.setApellidos(apellidos);
                                    participante.setDorsal(dorsal);
                                    participante.setEdad(edad);
                                    participante.setSexo(sexo);
                                    participante.setGrupoId(g);

                                    // Si se ha seleccionado alguna prueba se la asignamos al participante
                                    if (pruebaAsignada != null && !pruebaAsignada.equals("Ninguna")) {
                                        PruebaJpa pruebajpa = new PruebaJpa();
                                        Prueba p = pruebajpa.findPruebaByNombreCompeticion(pruebaAsignada,
                                                competicion.getId());
                                        if (p != null) {
                                            participante.setPruebaasignada(p);
                                        } else {
                                            throw new InputException("Prueba no encontrada");
                                        }
                                    }

                                    // Si se ha seleccionado un equipo
                                    if (nombreEquipo != null && !nombreEquipo.equals("Ninguno")) {
                                        EquipoJpa equipojpa = new EquipoJpa();
                                        Equipo equipo = equipojpa.findByNombreAndCompeticion(
                                                nombreEquipo.toString(),
                                                competicion.getId());
                                        if (equipo != null) {
                                            participante.setEquipoId(equipo);
                                        } else {
                                            throw new InputException("Equipo no encontrado");
                                        }
                                    }
                                    participantejpa.create(participante);
                                } else {
                                    throw new InputException("Grupo no encontrado");
                                }
                            } else {
                                throw new InputException("Dorsal ocupado");
                            }
                        } else {
                            throw new InputException("Dorsal no válido");
                        }
                        return participante;
                    } else {
                        throw new InputException("Nombre de grupo no válido");
                    }
                } else {
                    throw new InputException("Apellidos no válido");
                }
            } else {
                throw new InputException("Nombre no válido");
            }
        } else {
            throw new InputException("Competición no válida");
        }
    }

    /**
     * Elimina al participante cuyo id es "participanteid"
     *
     * @param participanteid Id del participante
     * @throws controlador.InputException
     */
    public static void eliminarParticipante(Integer participanteid) throws InputException {

        // Comprobamos que el id es válido
        if (participanteid != null) {

            ParticipanteJpa participantejpa = new ParticipanteJpa();

            //Buscamos el participante a partid del ID
            Participante participante = participantejpa.findParticipante(participanteid);

            if (participante != null) {
                eliminarRegistros(participante);
                try {
                    participantejpa.destroy(participante.getId());
                } catch (NonexistentEntityException ex) {
                    throw new InputException("Participante no encontrado");
                }
            } else {
                throw new InputException("Participante no encontrado");
            }
        } else {
            throw new InputException("Participante no válido");
        }
    }

    /**
     * Modifica los datos del participante cuyo id es "participanteid"
     *
     * @param competicion
     * @param participanteid Identificador del participante a modificar
     * @param nombre Nombre del participante
     * @param apellidos Apellidos del participante
     * @param dorsal Dorsal del participante
     * @param nombreGrupo Nombre del grupo al que pertence el participante
     * @param edad Edad del participante
     * @param sexo Sexo del participante, 0=Hombre, 1=Mujer
     * @param nombreEquipo Nombre del equipo al que pertenece el participante
     * @param pruebaAsignada Nombre de la prueba asignada al participante
     * @return el Participante modificado
     * @throws controlador.InputException
     */
    public static Participante modificarParticipante(Competicion competicion, Integer participanteid, String nombre, String apellidos,
            Integer dorsal, String nombreGrupo, Integer edad, Integer sexo,
            String nombreEquipo, String pruebaAsignada) throws InputException {

        if (competicion != null) {
            if (participanteid != null) {
                if (nombre != null && nombre.length() > 0) {
                    if (apellidos != null && nombre.length() > 0) {
                        if (nombreGrupo != null && nombre.length() > 0) {
                            Participante participante = null;
                            if (dorsal != null) {
                                if (dorsalLibreOMio(competicion, dorsal, participanteid)) {
                                    ParticipanteJpa participantejpa = new ParticipanteJpa();
                                    GrupoJpa grupojpa = new GrupoJpa();

                                    try {
                                        participante = participantejpa.findParticipante(participanteid);

                                        if (participante != null) {
                                            participante.setNombre(nombre);
                                            participante.setApellidos(apellidos);
                                            participante.setDorsal(dorsal);
                                            participante.setEdad(edad);
                                            participante.setSexo(sexo);

                                            // Buscamos el grupo por el nombre
                                            Grupo g = grupojpa.findGrupoByNombreAndCompeticion(nombreGrupo, competicion.getId());

                                            if (g != null) {
                                                // Cambia el grupo al que pertenece el participante
                                                participante.setGrupoId(g);

                                                // Si el participante tiene registros, se cambia la inscripción del registro
                                                RegistroJpa registrojpa = new RegistroJpa();
                                                List<Registro> registros = registrojpa.findByParticipante(participanteid);
                                                if (registros != null) {
                                                    InscripcionJpa inscripcionjpa = new InscripcionJpa();
                                                    Inscripcion inscripcion = inscripcionjpa.findInscripcionByCompeticionByGrupo(competicion.getId(),
                                                            g.getId());
                                                    for (Registro r : registros) {
                                                        r.setInscripcionId(inscripcion);
                                                        registrojpa.edit(r);
                                                    }
                                                }

                                                // Si se ha seleccionado una prueba
                                                if (pruebaAsignada != null && !pruebaAsignada.equals("Ninguna")) {
                                                    PruebaJpa pruebajpa = new PruebaJpa();
                                                    Prueba p = pruebajpa.findPruebaByNombreCompeticion(pruebaAsignada,
                                                            competicion.getId());
                                                    if (p != null) {
                                                        participante.setPruebaasignada(p);
                                                    } else {
                                                        throw new InputException("Prueba no encontrada");
                                                    }
                                                } else {
                                                    participante.setPruebaasignada(null);
                                                }

                                                // Si se ha seleccionado un equipo
                                                if (nombreEquipo != null && !nombreEquipo.equals("Ninguno")) {
                                                    // Buscamos el equipo y ponemos al participante como miembro
                                                    EquipoJpa equipojpa = new EquipoJpa();
                                                    Equipo equiponuevo = equipojpa.findByNombreAndCompeticion(nombreEquipo, competicion.getId());
                                                    if (equiponuevo != null) {
                                                        participante.setEquipoId(equiponuevo);
                                                    } else {
                                                        throw new InputException("Equipo no encontrado");
                                                    }
                                                } else {
                                                    participante.setEquipoId(null);
                                                }
                                                participantejpa.edit(participante);
                                            } else {
                                                throw new InputException("Grupo no encontrado");
                                            }
                                        } else {
                                            throw new InputException("Participante no encontrado");
                                        }
                                    } catch (NonexistentEntityException ex) {
                                        throw new InputException("Participante no encontrado");
                                    } catch (Exception ex) {
                                        throw new InputException(ex.getMessage());
                                    }
                                    return participante;

                                } else {
                                    throw new InputException("Dorsal ocupado");
                                }
                            } else {
                                throw new InputException("Dorsal no válido");
                            }
                        } else {
                            throw new InputException("Nombre de grupo no válido");
                        }
                    } else {
                        throw new InputException("Apellidos no válido");
                    }
                } else {
                    throw new InputException("Nombre no válido");
                }
            } else {
                throw new InputException("Participante no válido");
            }
        } else {
            throw new InputException("Competición no válida");
        }
    }

    /**
     * Elimina los registros de un participante
     *
     * @param participante Participante asociado a esos registros
     */
    private static void eliminarRegistros(Participante participante) throws InputException {

        RegistroJpa registrosjpa = new RegistroJpa();
        // Buscamos todos los registros de "participante"
        List<Registro> registros = registrosjpa.findByParticipante(participante.getId());
        try {
            // Eliminamos cada registro 
            for (Registro r : registros) {
                registrosjpa.destroy(r.getId());
            }
        } catch (NonexistentEntityException ex) {
            throw new InputException("Registro no encontrado");
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
    public static boolean dorsalLibre(Integer dorsal, Competicion competicion) {
        ParticipanteJpa participantejpa = new ParticipanteJpa();
        return (participantejpa.findByDorsalAndCompeticion(dorsal,
                competicion.getId())) == null;
    }

    /**
     * Comprueba que el dorsal esté disponible o que lo tenga el propio
     * participante que lo solicita
     *
     * @param dorsal Dorsal del participante
     * @param participanteid identificador del participante
     * @return true si el dorsal está libre o es el del participante cuyo id es
     * participanteid
     */
    private static boolean dorsalLibreOMio(Competicion c, Integer dorsal, Integer participanteid) {
        ParticipanteJpa participantejpa = new ParticipanteJpa();
        Participante participante = participantejpa.findByDorsalAndCompeticion(dorsal,
                c.getId());
        return (participante == null || participante.getId() == participanteid);
    }
}
