package modelo.logicaNegocio;

import controlador.Coordinador;
import controlador.InputException;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import modelo.dao.EquipoJpa;
import modelo.dao.GrupoJpa;
import modelo.dao.InscripcionJpa;
import modelo.dao.ParticipanteJpa;
import modelo.dao.PruebaJpa;
import modelo.dao.RegistroJpa;
import modelo.dao.exceptions.NonexistentEntityException;
import modelo.entities.Competicion;
import modelo.entities.Equipo;
import modelo.entities.Grupo;
import modelo.entities.Inscripcion;
import modelo.entities.Participante;
import modelo.entities.Prueba;
import modelo.entities.Registro;
import modelo.entities.TipoPrueba;
import modelo.entities.TipoResultado;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 *
 * @author JuanM
 */
public class ParticipanteService {

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

    public static class ImportarParticipantes extends SwingWorker<Void, Void> {

        String ruta;

        private static final int PRIMERA_FILA = 2;
        private static final int PRIMERA_COLUMNA = 1;
        private static final int PRIMERA_COLUMNA_PRUEBAS = PRIMERA_COLUMNA + 5;

        public ImportarParticipantes(String rutaFichero) {
            ruta = rutaFichero;
        }

        @Override
        protected void done() {
            try {
                get();
                Coordinador.getInstance().setEstadoLabel("Participantes importados", Color.BLUE);

            } catch (InterruptedException | ExecutionException ex) {
                Coordinador.getInstance().setEstadoLabel(ex.getMessage(), Color.RED);
            } finally {
                Coordinador.getInstance().getControladorPrincipal().cargarTablaParticipantes();
                Coordinador.getInstance().mostrarBarraProgreso(false);
                Coordinador.getInstance().getControladorPrincipal().cargarGruposEnParticipantes();
                Coordinador.getInstance().getControladorPrincipal().cargarPruebasEnParticipantes();
            }

        }

        @Override
        protected void process(List chunks) {
            Coordinador.getInstance().mostrarBarraProgreso(true);
        }

        @Override
        protected Void doInBackground() throws InputException {

            // Actualiza la interfaz (muestra la barra de estado)
            publish((Void) null);

            try {
                FileInputStream file = new FileInputStream(new File(ruta));
                Workbook workbook = WorkbookFactory.create(file);

                String data;
                // Por cada hoja del archivo excel...
                for (int numHoja = 0; numHoja < workbook.getNumberOfSheets(); numHoja++) {

                    // Obtenemos el número de filas, columnas y la hoja.
                    Sheet hoja = workbook.getSheetAt(numHoja);
                    Iterator<Row> iteradorFila = hoja.iterator();

                    Row fila = iteradorFila.next(); //Cabecera, no contiene datos
                    fila = iteradorFila.next();     //Fila donde se encuentra los nombres de las pruebas

                    Iterator<Cell> iteradorCelda = fila.cellIterator();
                    List<String> nombresPruebas = new ArrayList();
                    PruebaJpa pruebajpa = new PruebaJpa();
                    // Se comprueba si hay pruebas o no
                    if (fila.getLastCellNum() < PRIMERA_COLUMNA_PRUEBAS) {
                        // No hay pruebas que añadir
                    } else {
                        int columna = PRIMERA_COLUMNA_PRUEBAS;

                        Prueba prueba;
                        // Obtenemos el nombre de las pruebas
                        while (columna < fila.getLastCellNum()) {
                            // Se obtiene el nombre de la prueba y se comprueba que no es vacío
                            Cell celda = fila.getCell(columna);
                            //System.out.println(celda.getStringCellValue());
                            if (celda.getStringCellValue().length() > 0) {
                                // Se añade a una lista de nombres de pruebas que será utilizada posteriormente
                                nombresPruebas.add(celda.getStringCellValue());
                                prueba = pruebajpa.findPruebaByNombreCompeticion(celda.getStringCellValue(), Coordinador.getInstance().getSeleccionada().getId());
                                if (prueba == null) {
                                    try {
                                    // Se crea una nueva prueba en caso de que no exista, por defecto se crea de tipo individual y con un resultado numérico.
                                        // Se podrá modificar luego manualmente en el programa o al importar registros
                                        PruebaService.crearPrueba(Coordinador.getInstance().getSeleccionada(), celda.getStringCellValue(), TipoPrueba.Individual.toString(), TipoResultado.Numerica.toString());
                                    } catch (InputException ex) {

                                    }
                                }
                            }
                            columna++;
                        }
                    }
                    while (iteradorFila.hasNext()) {
                        fila = iteradorFila.next();
                        int columna = PRIMERA_COLUMNA;
                        ParticipanteJpa participantejpa = new ParticipanteJpa();
                        Participante participante = new Participante();
                        //Leemos el dorsal
                        Cell celda = fila.getCell(columna);
                        if (celda != null) {
                            double dorsal = celda.getNumericCellValue();
                            if (ParticipanteService.dorsalLibre((int) dorsal, Coordinador.getInstance().getSeleccionada())) {
                                participante.setDorsal((int) dorsal);
                                columna++;
                                // Leemos los apellidos
                                celda = fila.getCell(columna);
                                if (celda != null) {
                                    data = celda.getStringCellValue();

                                    // Si este campo está vacío se pasa a la siguiente fila
                                    if (data.length() > 0) {
                                        participante.setApellidos(data);
                                        columna++;

                                        // Leemos el nombre
                                        data = fila.getCell(columna).getStringCellValue();
                                        // Si el nombre está vacío se pone un espacio ya que en la base de datos es un campo obligatorio
                                        if (data == null) {
                                            data = " ";
                                        }
                                        participante.setNombre(data);
                                        columna++;

                                        // Leemos el nombre del grupo
                                        GrupoJpa grupojpa = new GrupoJpa();
                                        Grupo grupo;
                                        data = fila.getCell(columna).getStringCellValue();
                        // Si este campo no esta vacío se busca el grupo y si no existe se crea
                                        // En caso de que este vacío se pasa al siguiente participante
                                        if (data != null) {
                                            grupo = grupojpa.findGrupoByNombreAndCompeticion(data, Coordinador.getInstance().getSeleccionada().getId());
                                            if (grupo == null) {
                                                grupo = GrupoService.crearGrupo(Coordinador.getInstance().getSeleccionada(), data, null);
                                            }
                                            participante.setGrupoId(grupo);
                                            columna++;

                                            // Leeemos el nombre del equipo
                                            data = fila.getCell(columna).getStringCellValue();
                                            if (data != null) {
                                                EquipoJpa equipojpa = new EquipoJpa();
                                                Equipo equipo;
                                                equipo = equipojpa.findByNombreAndCompeticion(data, Coordinador.getInstance().getSeleccionada().getId());
                                                if (equipo == null) {
                                                    equipo = EquipoService.crearEquipo(Coordinador.getInstance().getSeleccionada(), data, grupo.getNombre());
                                                }
                                                participante.setEquipoId(equipo);
                                            }
                                            columna++;

                                            // Leemos la prueba asignada al participante
                                            boolean pruebaAsignada = false;
                                            while (!pruebaAsignada && columna <= nombresPruebas.size() + PRIMERA_COLUMNA_PRUEBAS) {
                                                data = fila.getCell(columna).getStringCellValue();
                                                if (!data.equals("")) {
                                                    Prueba pr = pruebajpa.findPruebaByNombreCompeticion(nombresPruebas.get(columna - PRIMERA_COLUMNA_PRUEBAS),
                                                            Coordinador.getInstance().getSeleccionada().getId());
                                                    participante.setPruebaasignada(pr);
                                                    pruebaAsignada = true;
                                                }
                                                columna++;

                                            }

                                            participantejpa.create(participante);
                                            /*// Se pone un dorsal automáticamente (el id del participante)
                                             participante.setDorsal(participante.getId());
                                             participantejpa.edit(participante);*/
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                file.close();
            } catch (IOException ex) {
                throw new InputException("Archivo no válido");
            } catch (InvalidFormatException ex) {
                throw new InputException("Formato de archivo no válido");
            } catch (Exception ex) {
                throw new InputException(ex.getMessage());
            }

            return null;
        }
    }
}
