package controlador;

import modelo.TipoResultado;
import modelo.TipoPrueba;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JOptionPane;
import modelo.Competicion;
import modelo.Compuesta;
import modelo.Prueba;
import modelo.Registro;
import dao.CompuestaJpa;
import dao.GrupoJpa;
import dao.ParticipanteJpa;
import dao.PruebaJpa;
import dao.RegistroJpa;
import dao.exceptions.NonexistentEntityException;
import modelo.Grupo;
import modelo.Participante;
import vista.GeneralTab;
import vista.VistaPrincipal;

/**
 *
 * @author JuanM
 */
public class ControlPruebas implements ActionListener {

    private final GeneralTab vista;

    /**
     * Constructor que asocia la vista al controlador
     *
     * @param vista Vista del controlador (Interfaz)
     */
    public ControlPruebas(GeneralTab vista) {
        this.vista = vista;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();
        switch (command) {
            case VistaPrincipal.CREARPRUEBA:
                if (Coordinador.getInstance().getSeleccionada() != null) {
                    Prueba prueba;
                    try {
                        prueba = crearPrueba(Coordinador.getInstance().getSeleccionada(),
                                vista.getNombrePrueba(),
                                vista.getTipoPrueba(), vista.getTipoResultado());
                        // Actualizamos la vista
                        vista.añadirPruebaATabla(new Object[]{
                            prueba.getId(),
                            prueba.getNombre(),
                            prueba.getTipo(),
                            prueba.getTiporesultado()});
                        Coordinador.getInstance().setEstadoLabel(
                                "Prueba creada correctamente", Color.BLUE);
                        vista.limpiarFormularioPrueba();
                    } catch (InputException ex) {
                        Coordinador.getInstance().setEstadoLabel(
                                ex.getMessage(), Color.RED);
                    }
                }
                break;
            case VistaPrincipal.ELIMINARPRUEBA:
                if (vista.getPruebaSelected() != -1) {
                    int confirmDialogPrueba = JOptionPane.showConfirmDialog(
                            null,
                            "¿Está seguro de que desea eliminar la prueba seleccionada?",
                            "Aviso",
                            JOptionPane.YES_NO_OPTION);
                    if (confirmDialogPrueba == JOptionPane.YES_OPTION) {
                        try {
                            eliminarPrueba(vista.getPruebaSelected(),
                                    Coordinador.getInstance().getSeleccionada().getId());
                            vista.eliminarPrueba();
                            vista.limpiarFormularioPrueba();
                            Coordinador.getInstance().setEstadoLabel("Prueba eliminada correctamente", Color.BLUE);
                        } catch (InputException ex) {
                            Coordinador.getInstance().setEstadoLabel(ex.getMessage(), Color.RED);
                        }

                    }
                }
                break;
            case VistaPrincipal.MODIFICARPRUEBA:
                Prueba prueba;
                try {
                    prueba = modificarPrueba(vista.getPruebaSelected(),
                            Coordinador.getInstance().getSeleccionada().getId(),
                            vista.getNombrePrueba(),
                            TipoResultado.valueOf(vista.getTipoResultado()),
                            TipoPrueba.valueOf(vista.getTipoPrueba())
                    );
                    // Actualizamos la vista
                    vista.añadirPruebaATabla(new Object[]{
                        prueba.getId(),
                        prueba.getNombre(),
                        prueba.getTipo(),
                        prueba.getTiporesultado()});
                    vista.eliminarPrueba();
                    Coordinador.getInstance().setEstadoLabel("Prueba modificada correctamente", Color.BLUE);
                } catch (InputException ex) {
                    Coordinador.getInstance().setEstadoLabel(ex.getMessage(), Color.RED);
                }
                break;
            case VistaPrincipal.LIMPIARPRUEBA:
                vista.limpiarFormularioPrueba();
                break;

        }
    }

    /**
     * Crea una prueba con los datos de la vista
     *
     * @param competicion Competicion donde se creará la prueba
     * @param nombre Nombre de la prueba
     * @param tipoResultado Tipo de resultado (Distancia, Tiempo, Numerica)
     * @param tipoPrueba Individual o Equipo
     * @return la Prueba creada
     * @throws controlador.InputException
     */
    public static Prueba crearPrueba(Competicion competicion, String nombre, String tipoPrueba,
            String tipoResultado) throws InputException {

        Prueba p = null;

        // Comprueba que el nombre de la prueba es no vacío
        if (nombre != null && nombre.length() > 0) {

            CompuestaJpa compujpa = new CompuestaJpa();
            PruebaJpa prujpa = new PruebaJpa();

            // Comprueba que el nombre de la prueba esté disponible
            // en la competicion seleccionada
            if (!existePrueba(nombre, competicion)) {

                if (tipoPrueba != null) {

                    if (tipoResultado != null) {

                        // Creamos una prueba con sus datos correspondientes
                        p = new Prueba();
                        p.setNombre(nombre);

                        TipoResultado tresultado;
                        TipoPrueba tprueba;
                        try {
                            tresultado = TipoResultado.valueOf(tipoResultado);
                        } catch (IllegalArgumentException ie) {
                            throw new InputException("Tipo de resultado no válido");
                        }
                        try {
                            tprueba = TipoPrueba.valueOf(tipoPrueba);
                        } catch (IllegalArgumentException ie) {
                            throw new InputException("Tipo de prueba no válido");
                        }
                        p.setTipo(tprueba.toString());
                        p.setTiporesultado(tresultado.toString());
                        prujpa.create(p);

                        // Asociamos la prueba con la competición
                        Compuesta compuesta = new Compuesta();
                        compuesta.setCompeticionId(competicion);
                        // El orden las pruebas no se utiliza actualmente
                        compuesta.setOrden(1);
                        compuesta.setPruebaId(p);
                        compujpa.create(compuesta);

                    } else {
                        throw new InputException("Tipo de resultado no válido");
                    }
                } else {
                    throw new InputException("Tipo de prueba no válido");
                }
            } else {
                throw new InputException("Nombre de prueba ocupado");
            }
        } else {
            throw new InputException("Nombre de prueba no válido");
        }
        return p;
    }

    public static Prueba modificarPrueba(Integer pruebaid, Integer competicionid, String nombrePrueba, TipoResultado tipoResultado, TipoPrueba tipoPrueba) throws InputException {

        if (pruebaid != null) {

            // Comprobamos que se ha seleccionado una prueba válida
            PruebaJpa pruebajpa = new PruebaJpa();
            Prueba prueba = pruebajpa.findPrueba(pruebaid);
            if (prueba != null) {

                if (competicionid != null) {

                    prueba = pruebajpa.findPruebaByNombreCompeticion(prueba.getNombre(), competicionid);

                    if (prueba != null) {

                        //Comprobamos que el nombre de la prueba es válido
                        if (nombrePrueba.length() > 0) {

                            // Comprobamos que el nombre de la prueba no esta cogido
                            Prueba pruebaMod = pruebajpa.findPruebaByNombreCompeticion(
                                    nombrePrueba.toString(),
                                    competicionid);
                            if (pruebaMod == null || pruebaMod.getId() == prueba.getId()) {

                                // Establecemos los atributos a partir de los datos de la vista
                                prueba.setNombre(nombrePrueba.toString());

                                if (tipoPrueba != null) {

                                    if (tipoResultado != null) {

                                        // Comprobamos que la prueba no tiene registros asocidados
                                        // En caso de tener registros no se podrá modificar 
                                        if (pruebajpa.countRegistrosByPrueba(pruebaid) <= 0) {
                                            prueba.setTiporesultado(tipoResultado.toString());
                                            prueba.setTipo(tipoPrueba.toString());

                                        } else {
                                            throw new InputException("No se puede modificar una prueba con registros asociados");
                                        }
                                        try {
                                            pruebajpa.edit(prueba);
                                        } catch (NonexistentEntityException ex) {
                                            throw new InputException("Prueba no encontrada");
                                        } catch (Exception ex) {
                                            throw new InputException(ex.getMessage());
                                        }
                                    } else {
                                        throw new InputException("Tipo de resultado no válido");
                                    }
                                } else {
                                    throw new InputException("Tipo de prueba no válido");
                                }
                            } else {
                                throw new InputException("Nombre de prueba ocupado");
                            }
                        } else {
                            throw new InputException("Nombre de prueba no válido");
                        }
                    } else {
                        throw new InputException("Prueba no registrada en la competición");
                    }
                } else {
                    throw new InputException("Competición no válida");
                }
            } else {
                throw new InputException("Prueba no encontrada");
            }
            return prueba;
        } else {
            throw new InputException("Prueba no válida");
        }

    }

    /**
     * Elimina una prueba de la competición pasada como parámetro
     *
     * @param pruebaid Identificador de la prueba a eliminar
     * @param competicionid Identificador de la competicion
     * @throws controlador.InputException
     */
    public static void eliminarPrueba(Integer pruebaid, Integer competicionid) throws InputException {

        CompuestaJpa compuestajpa = new CompuestaJpa();
        PruebaJpa pruebajpa = new PruebaJpa();
        RegistroJpa registrojpa = new RegistroJpa();

        if (pruebaid != null) {

            Prueba prueba = pruebajpa.findPrueba(pruebaid);

            if (competicionid != null) {

                if (prueba != null) {

                    // Eliminamos la prueba de la competición
                    Compuesta c = compuestajpa.findCompuestaByPrueba_Competicion(
                            pruebaid, competicionid);

                    if (c != null) {
                        try {
                            compuestajpa.destroy(c.getId());
                            // Eliminamos todos los registros de esa prueba
                            List<Registro> registros = registrojpa.findByPrueba(pruebaid);
                            for (Registro r : registros) {
                                registrojpa.destroy(r.getId());
                            }
                        } catch (NonexistentEntityException ex) {

                        }

                        // Modificamos los participantes que tienen asignado dicha prueba
                        ParticipanteJpa participanteJpa = new ParticipanteJpa();
                        GrupoJpa grupoJpa = new GrupoJpa();
                        List<Grupo> grupos = grupoJpa.findGruposByCompeticion(Coordinador.getInstance().getSeleccionada());
                        if (grupos != null) {
                            for (Grupo g : grupos) {
                                List<Participante> participantes = participanteJpa.findParticipantesByGrupoPruebaAsignada(g.getId(), prueba);
                                for (Participante participante : participantes) {
                                    participante.setPruebaasignada(null);
                                    try {
                                        participanteJpa.edit(participante);
                                    } catch (Exception ex) {

                                    }
                                }
                            }
                        }

                        try {
                            // Eliminamos la prueba
                            pruebajpa.destroy(pruebaid);
                        } catch (dao.exceptions.NonexistentEntityException ex) {
                            throw new InputException(ex.getMessage());
                        } catch (dao.exceptions.IllegalOrphanException ex) {
                            throw new InputException("Prueba no encontrada");
                        }

                    } else {
                        throw new InputException("Prueba no registrada en Competición");
                    }
                } else {
                    throw new InputException("Prueba no encontrada");
                }
            } else {
                throw new InputException("Identificador de competición no válido");
            }
        } else {
            throw new InputException("Identificador de prueba no válido");
        }
    }

    /**
     * Devuelve true si ya existe una prueba con ese nombre en la competicion
     *
     * @param nombre
     * @return true si el nombre de la prueba ya existe en la competicion
     */
    private static boolean existePrueba(String nombre, Competicion competicion) {
        PruebaJpa prujpa = new PruebaJpa();
        return prujpa.findPruebaByNombreCompeticion(
                nombre,
                competicion.getId()) != null;
    }
}
