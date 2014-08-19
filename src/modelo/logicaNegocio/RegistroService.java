package modelo.logicaNegocio;

import controlador.Coordinador;
import controlador.InputException;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author JuanM
 */
public class RegistroService {

    /**
     * Crea un registro nuevo
     *
     * @param competicion
     * @param dorsal dorsal del participante, si es un equipo null
     * @param nombrePrueba nombre de la prueba
     * @param nombreEquipo Nombre del equipo si el registro es de un equipo,
     * null en otro caso
     * @param sorteo Indica si la prueba ha sido seleccionada por sorteo
     * @param segundos Marca en segundos o la distancia o número si la prueba no
     * es de tiempo
     * @param minutos Marca en minutos, si la prueba no es de tiempo null
     * @param horas Marca en horas, si la prueba no es de tiempo null
     * @return Registro si ha sido creado correctamente
     * @throws controlador.InputException
     */
    public static Registro crearRegistro(Competicion competicion, Integer dorsal,
            String nombrePrueba, String nombreEquipo, Boolean sorteo,
            Double segundos, Integer minutos, Integer horas) throws InputException {

        if (competicion != null) {
            if (nombrePrueba != null) {
                Registro registro = null;
                PruebaJpa pruebajpa = new PruebaJpa();
                // Obtenemos la prueba a partir del nombre
                Prueba prueba = pruebajpa.findPruebaByNombreCompeticion(nombrePrueba, competicion.getId());
                // Comprobamos que los parámetros son correctos
                if (prueba != null) {
                    if (!(dorsal == null && prueba.getTipo().equals(TipoPrueba.Individual.toString()))) {
                        if (!(nombreEquipo == null && prueba.getTipo().equals(TipoPrueba.Equipo.toString()))) {

                            Participante participante;
                            Grupo g;
                            registro = new Registro();
                            Inscripcion i;
                            GrupoJpa grupojpa = new GrupoJpa();
                            InscripcionJpa inscripcionjpa = new InscripcionJpa();
                            RegistroJpa registrojpa = new RegistroJpa();
                            // Si se ha seleccionado un participante individual
                            if (prueba.getTipo().equals(TipoPrueba.Individual.toString())) {
                                // Obtenemos el participante
                                ParticipanteJpa participantejpa = new ParticipanteJpa();
                                participante = participantejpa.findByDorsalAndCompeticion(dorsal, competicion.getId());
                                g = grupojpa.findByParticipanteCompeticion(competicion.getId(), participante.getId());
                                registro.setParticipanteId(participante);
                                i = inscripcionjpa.findInscripcionByCompeticionByGrupo(
                                        competicion.getId(), g.getId());
                                registro.setNumIntento(registrojpa.findMaxNumIntentoParticipante(i.getId(),
                                        prueba.getId(), participante.getId()) + 1);
                            } else {
                                // Obtenemos el equipo
                                EquipoJpa equipojpa = new EquipoJpa();
                                Equipo equipo = equipojpa.findByNombreAndCompeticion(nombreEquipo,
                                        competicion.getId());
                                g = grupojpa.findByEquipoCompeticion(competicion.getId(), equipo.getId());
                                registro.setEquipoId(equipo);
                                i = inscripcionjpa.findInscripcionByCompeticionByGrupo(competicion.getId(), g.getId());
                                registro.setNumIntento(registrojpa.findMaxNumIntentoEquipo(i.getId(),
                                        prueba.getId(), equipo.getId()) + 1);
                            }

                            // Establecemos los datos del registro comunes
                            registro.setInscripcionId(i);
                            registro.setPruebaId(prueba);
                            if (sorteo != null) {
                                registro.setSorteo(sorteo ? 1 : 0);
                            }
                            // Comprueba que la prueba no es de tipo tiempo
                            if (prueba.getTiporesultado().equals(TipoResultado.Distancia.toString())
                                    || prueba.getTiporesultado().equals(TipoResultado.Numerica.toString())) {
                                if (segundos == null) {
                                    throw new InputException("Formato del registro no válido");
                                }
                                registro.setNum(segundos);
                                // Si es de tipo Tiempo formateamos la hora, minutos y segundos    
                            } else if (prueba.getTiporesultado().equals(TipoResultado.Tiempo.toString())) {
                                // Obtenemos un objeto Date a partir de los parámetros
                                Date date = getTiempo(segundos, minutos, horas);
                                if (date == null) {
                                    throw new InputException("Formato de tiempo no válido");
                                }
                                registro.setTiempo(date);
                            }
                            // Creamos el registro en la base de datos
                            registrojpa.create(registro);
                        } else {
                            throw new InputException("Equipo no válido");
                        }
                    } else {
                        throw new InputException("Participante no válido");
                    }
                } else {
                    throw new InputException("Prueba no encontrada");
                }
                return registro;
            } else {
                throw new InputException("Nombre de prueba no válido");
            }
        } else {
            throw new InputException("Competición no válida");
        }
    }

    public static Registro crearRegistroIndividualNum(Competicion competicion, Integer dorsal, String nombrePrueba, Boolean sorteo, Double marca) throws InputException {
        return crearRegistro(competicion, dorsal, nombrePrueba, null, sorteo, marca, null, null);
    }

    public static Registro crearRegistroIndividualTiempo(Competicion competicion, Integer dorsal, String nombrePrueba, Boolean sorteo, Double segundos, Integer minutos, Integer horas) throws InputException {
        return crearRegistro(competicion, dorsal, nombrePrueba, null, sorteo, segundos, minutos, horas);
    }

    public static Registro crearRegistroEquipoNum(Competicion competicion, String nombreEquipo, String nombrePrueba, Boolean sorteo, Double marca) throws InputException {
        return crearRegistro(competicion, null, nombrePrueba, nombreEquipo, sorteo, marca, null, null);
    }

    public static Registro crearRegistroEquipoTiempo(Competicion competicion, String nombreEquipo, String nombrePrueba, Boolean sorteo, Double segundos, Integer minutos, Integer horas) throws InputException {
        return crearRegistro(competicion, null, nombrePrueba, nombreEquipo, sorteo, segundos, minutos, horas);
    }

    public static Integer getHoras(String tiempo) {
        try {
            String aux1, aux2;
            aux1 = tiempo.substring(0, tiempo.indexOf(":"));
            aux2 = tiempo.substring(tiempo.indexOf(":") + 1);
            if (aux2.contains(":")) {
                Integer res = Integer.parseInt(aux1);
                if (res >= 0) {
                    return res;
                } else {
                    return null;
                }
            } else {
                return 0;
            }
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            return null;
        }
    }

    public static Integer getMinutos(String tiempo) {
        try {
            if (tiempo.contains(":")) {
                String aux1, aux2;
                aux1 = tiempo.substring(0, tiempo.indexOf(":"));
                aux2 = tiempo.substring(tiempo.indexOf(":") + 1);
                Integer res = null;
                if (aux2.contains(":")) {
                    aux1 = aux2.substring(0, aux2.indexOf(":"));
                    res = Integer.parseInt(aux1);
                } else {
                    res = Integer.parseInt(aux1);

                }
                if (res >= 0 && res < 60) {
                    return res;
                } else {
                    return null;
                }
            } else if (Double.parseDouble(tiempo) > 0) {
                return 0;
            } else {
                return null;
            }
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            return null;
        }
    }

    public static Double getSegundos(String tiempo) {
        try {
            if (tiempo.contains(":")) {
                String aux1, aux2;
                aux2 = tiempo.substring(tiempo.indexOf(":") + 1);
                Double res = null;
                if (aux2.contains(":")) {
                    aux1 = aux2.substring(aux2.indexOf(":") + 1);
                    res = Double.parseDouble(aux1);
                } else {
                    res = Double.parseDouble(aux2);
                }
                if (res >= 0 && res < 60) {
                    return res;
                } else {
                    return null;
                }
            } else if (Double.parseDouble(tiempo) > 0) {
                return Double.parseDouble(tiempo);
            } else {
                return null;
            }
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            return null;
        }
    }

    public static Date getTiempo(Double segundos, Integer minutos, Integer horas) {
        Date res = null;
        if (segundos == null && minutos == null & horas == null) {
            return res;
        } else {
            try {
                String formatDate = "HH:mm:ss.S";
                String horasSt;
                if (horas == null) {
                    horasSt = "00";
                } else {
                    horasSt = Integer.toString(horas);
                    if (horasSt.length() == 0) {
                        horasSt = "00";
                    } else if (horasSt.length() == 1) {
                        horasSt = "0" + horasSt;
                    }
                }
                String minutosSt;
                if (minutos == null) {
                    minutosSt = "00";
                } else {
                    minutosSt = Integer.toString(minutos);
                    if (minutosSt.length() == 0) {
                        minutosSt = "00";
                    } else if (minutosSt.length() == 1) {
                        minutosSt = "0" + minutosSt;
                    }
                }
                String segundosSt;
                if (segundos == null) {
                    segundosSt = "00.0";
                } else {
                    segundosSt = Double.toString(segundos);
                    if (segundosSt.length() == 0) {
                        segundosSt = "00.0";
                    } else {
                        segundosSt = Double.toString(Double.parseDouble(segundosSt));
                        if (Double.parseDouble(segundosSt) > 59.999) {
                            return null;
                        }
                        if (segundosSt.charAt(2) == '.') {
                            String decimales = segundosSt.substring(2);
                            for (int j = 2; j < decimales.length(); j++) {
                                formatDate += "S";
                            }
                        }
                    }
                }
                res = new SimpleDateFormat(formatDate).parse(horasSt + ":" + minutosSt + ":" + segundosSt);
            } catch (ParseException | NumberFormatException ex) {
                return res;
            }
            return res;
        }
    }

    /**
     * Elimina un registro cuyo id es "registroid"
     *
     * @param registroid Id del registro a eliminar
     * @throws controlador.InputException
     */
    public static void eliminarRegistro(Integer registroid) throws InputException {

        if (registroid != null) {
            RegistroJpa registrojpa = new RegistroJpa();
            try {
                registrojpa.destroy(registroid);
            } catch (NonexistentEntityException ex) {
                throw new InputException("Registro no encontrado");
            }
        } else {
            throw new InputException("Registro no válido");
        }
    }

    /**
     * Modifica la marca del registro cuyo id es "registroid"
     *
     * @param registroid Id del registro que se va a modificar
     * @param segundos
     * @param minutos
     * @param horas
     *
     * @return el Registro modificado
     * @throws controlador.InputException
     */
    public static Registro modificarRegistro(Integer registroid, Double segundos, Integer minutos, Integer horas) throws InputException {

        if (registroid != null) {

            RegistroJpa registrojpa = new RegistroJpa();
            // Obtenemos el objeto Registro a partir de su Id
            Registro registro = registrojpa.findRegistro(registroid);

            if (registro != null) {
                // Si el registro es de Tipo Numerica o Distancia
                if (registro.getPruebaId().getTiporesultado().equals(TipoResultado.Distancia.toString())
                        || registro.getPruebaId().getTiporesultado().equals(TipoResultado.Numerica.toString())) {
                    if (segundos == null) {
                        throw new InputException("Formato del registro no válido");
                    }
                    // Establecemos el nuevo registro
                    registro.setNum(segundos);

                } else if (registro.getPruebaId().getTiporesultado().equals(TipoResultado.Tiempo.toString())) { // Si el registro es de Tipo Tiempo
                    Date date = getTiempo(segundos, minutos, horas);
                    if (date != null) {
                        // Establecemos el tiempo formateado
                        registro.setTiempo(date);
                    } else {
                        throw new InputException("Formato de tiempo no válido");
                    }
                }
                try {
                    // Guardamos los cambios en la base de datos
                    registrojpa.edit(registro);
                } catch (Exception ex) {
                    throw new InputException(ex.getMessage());
                }
                return registro;
            }
            throw new InputException("Registro no encontrado");

        } else {
            throw new InputException("Registro no válido");
        }
    }

    public static class CrearPlantillaExcel extends SwingWorker<Void, Void> {

        private final String path;
        private final Competicion competicion;
        private final List<String> nombrePruebas;
        private final List<String> nombreGrupos;
        private final boolean participantesAsignados;

        private static final int PRIMERA_FILA = 2;
        private static final int PRIMERA_COLUMNA_PRUEBA = 2;
        private static final int PRIMERA_COLUMNA = 1;
        private static final int NUM_PARAMPRUEBA = 2;

        public CrearPlantillaExcel(String path, Competicion c, List<String> nombrePruebas, List<String> nombreGrupos, boolean participantesAsignados) {
            this.path = path;
            this.competicion = c;
            this.nombrePruebas = nombrePruebas;
            this.nombreGrupos = nombreGrupos;
            this.participantesAsignados = participantesAsignados;
        }

        @Override
        protected void done() {
            try {
                get();
                Coordinador.getInstance().setEstadoLabel("Plantilla creada correctamente", Color.BLUE);

            } catch (InterruptedException | ExecutionException ex) {
                Coordinador.getInstance().setEstadoLabel(ex.getMessage(), Color.RED);
            } finally {
                Coordinador.getInstance().mostrarBarraProgreso(false);
            }

        }

        @Override
        protected void process(List chunks) {
            Coordinador.getInstance().setEstadoLabel("Creando plantilla... ", Color.BLACK);
            Coordinador.getInstance().mostrarBarraProgreso(true);
        }

        @Override
        protected Void doInBackground() throws FileNotFoundException, IOException {
            // Actualiza la interfaz (muestra la barra de estado)
            publish((Void) null);

            Workbook wb = new XSSFWorkbook();
            FileOutputStream fileOut;
            fileOut = new FileOutputStream(path + "/" + competicion.getNombre() + "_registros.xlsx");

            // Se cargan las Pruebas de las que se van a generar resultados
            List<Prueba> pruebas = null;
            PruebaJpa pruebajpa = new PruebaJpa();
            if (nombrePruebas == null) {
                pruebas = pruebajpa.findPruebasByCompeticon(Coordinador.getInstance().getSeleccionada());
            } else {
                pruebas = new ArrayList();
                for (String nombrePrueba : nombrePruebas) {
                    pruebas.add(pruebajpa.findPruebaByNombreCompeticion(nombrePrueba, Coordinador.getInstance().getSeleccionada().getId()));
                }
            }

            // Estilo de las celdas
            XSSFCellStyle cs1 = (XSSFCellStyle) wb.createCellStyle();
            cs1.setFillForegroundColor(IndexedColors.GOLD.getIndex());
            cs1.setFillPattern(CellStyle.SOLID_FOREGROUND);
            cs1.setBorderBottom(BorderStyle.MEDIUM);
            cs1.setBorderLeft(BorderStyle.MEDIUM);
            cs1.setBorderRight(BorderStyle.MEDIUM);
            cs1.setBorderTop(BorderStyle.MEDIUM);
            XSSFFont f = (XSSFFont) wb.createFont();
            f.setBold(true);
            f.setColor(IndexedColors.BLACK.getIndex());
            cs1.setFont(f);

            XSSFCellStyle cs2 = (XSSFCellStyle) wb.createCellStyle();
            cs2.setFillForegroundColor(IndexedColors.TAN.getIndex());
            cs2.setFillPattern(CellStyle.SOLID_FOREGROUND);
            cs2.setBorderBottom(BorderStyle.THIN);
            cs2.setBorderLeft(BorderStyle.THIN);
            cs2.setBorderRight(BorderStyle.THIN);
            cs2.setBorderTop(BorderStyle.THIN);

            // Por cada prueba
            for (Prueba prueba : pruebas) {
                //Creamos una hoja nueva
                Sheet hoja = wb.createSheet(prueba.getNombre());

                // Fila de Cabeceras
                Row fila = hoja.createRow(PRIMERA_FILA - 1);
                Cell celda = fila.createCell(PRIMERA_COLUMNA_PRUEBA);
                celda.setCellValue("Prueba");
                celda.setCellStyle(cs1);
                celda = fila.createCell(PRIMERA_COLUMNA_PRUEBA + 1);
                celda.setCellValue("Tipo");
                celda.setCellStyle(cs1);
                celda = fila.createCell(PRIMERA_COLUMNA_PRUEBA + 2);
                celda.setCellValue("Resultado");
                celda.setCellStyle(cs1);

                //Datos de la prueba
                fila = hoja.createRow(PRIMERA_FILA);
                celda = fila.createCell(PRIMERA_COLUMNA_PRUEBA);
                celda.setCellValue(prueba.getNombre());
                celda.setCellStyle(cs2);
                celda = fila.createCell(PRIMERA_COLUMNA_PRUEBA + 1);
                celda.setCellValue(prueba.getTipo());
                celda.setCellStyle(cs2);
                celda = fila.createCell(PRIMERA_COLUMNA_PRUEBA + 2);
                celda.setCellValue(prueba.getTiporesultado());
                celda.setCellStyle(cs2);

                // Segunda fila de cabeceras
                fila = hoja.createRow(PRIMERA_FILA + 1);
                if (prueba.getTipo().equals(TipoPrueba.Individual.toString())) {
                    celda = fila.createCell(PRIMERA_COLUMNA - 1);
                    celda.setCellValue("Participante");
                    celda.setCellStyle(cs1);
                    celda = fila.createCell(PRIMERA_COLUMNA);
                    celda.setCellValue("Dorsal");
                    celda.setCellStyle(cs1);
                } else {
                    celda = fila.createCell(PRIMERA_COLUMNA);
                    celda.setCellValue("Equipo");
                    celda.setCellStyle(cs1);
                }
                celda = fila.createCell(PRIMERA_COLUMNA + 1);
                celda.setCellValue("Intento 1");
                celda.setCellStyle(cs1);
                celda = fila.createCell(PRIMERA_COLUMNA + 2);
                celda.setCellValue("Intento 2");
                celda.setCellStyle(cs1);
                celda = fila.createCell(PRIMERA_COLUMNA + 3);
                celda.setCellValue("Intento 3");
                celda.setCellStyle(cs1);

                int contadorFila = PRIMERA_FILA + 2;
                // Grupos
                ParticipanteJpa participanteJpa = new ParticipanteJpa();
                EquipoJpa equipoJpa = new EquipoJpa();
                GrupoJpa grupoJpa = new GrupoJpa();
                List<Grupo> grupos;

                if (prueba.getTipo().equals(TipoPrueba.Individual.toString())) {
                    if (nombreGrupos == null) {
                        grupos = grupoJpa.findGruposByCompeticion(competicion);
                    } else {
                        grupos = new ArrayList();
                        for (String nombreGrupo : nombreGrupos) {
                            Grupo g = grupoJpa.findGrupoByNombreAndCompeticion(nombreGrupo, competicion.getId());
                            grupos.add(g);
                        }
                    }
                    for (Grupo g : grupos) {
                        List<Participante> aux;
                        if (participantesAsignados) {
                            aux = participanteJpa.findParticipantesByGrupoPruebaAsignada(g.getId(), prueba);
                        } else {
                            aux = participanteJpa.findParticipantesByGrupo(g.getId());
                        }
                        for (Participante particip : aux) {
                            fila = hoja.createRow(contadorFila++);
                            celda = fila.createCell(PRIMERA_COLUMNA - 1);
                            celda.setCellValue(particip.getApellidos() + ", " + particip.getNombre());
                            celda.setCellStyle(cs2);
                            celda = fila.createCell(PRIMERA_COLUMNA);
                            celda.setCellValue(particip.getDorsal());
                            celda.setCellStyle(cs2);

                            for (int i = 1; i <= 3; i++) {
                                celda = fila.createCell(PRIMERA_COLUMNA + i);
                                celda.setCellStyle(cs2);
                            }
                        }
                    }
                } else if (prueba.getTipo().equals(TipoPrueba.Equipo.toString())) {
                    List<Equipo> equipos;
                    if (nombreGrupos == null) {
                        equipos = equipoJpa.findByCompeticion(competicion.getId());
                    } else {
                        equipos = new ArrayList();
                        for (String nombreGrupo : nombreGrupos) {
                            Grupo grupo = grupoJpa.findGrupoByNombreAndCompeticion(nombreGrupo, competicion.getId());
                            List<Equipo> aux = equipoJpa.findByGrupo(grupo.getId());
                            for (Equipo e : aux) {
                                equipos.add(e);
                            }
                        }
                    }
                    for (Equipo equipo : equipos) {
                        fila = hoja.createRow(contadorFila++);
                        celda = fila.createCell(PRIMERA_COLUMNA);
                        celda.setCellValue(equipo.getNombre());
                        celda.setCellStyle(cs2);

                        for (int i = 1; i <= 3; i++) {
                            celda = fila.createCell(PRIMERA_COLUMNA + i);
                            celda.setCellStyle(cs2);
                        }
                    }
                }
            }
            wb.write(fileOut);
            fileOut.close();
            return null;
        }

    }

    public static class ImportarRegistros extends SwingWorker<Void, Void> {

        String ruta;

        private static final int PRIMERA_FILA = 2;
        private static final int PRIMERA_COLUMNA_PRUEBA = 2;
        private static final int PRIMERA_COLUMNA = 1;
        private static final int NUM_PARAMPRUEBA = 2;

        public ImportarRegistros(String rutaFichero) {
            ruta = rutaFichero;
        }

        @Override
        protected void done() {
            try {
                get();
                Coordinador.getInstance().setEstadoLabel("Registros importardos", Color.BLUE);
            } catch (InterruptedException | ExecutionException ex) {
                Coordinador.getInstance().setEstadoLabel(ex.getMessage(), Color.RED);
            } finally {
                RegistroJpa registrojpa = new RegistroJpa();
                List<Registro> registros = registrojpa.findByCompeticion(Coordinador.getInstance().getSeleccionada().getId());
                Coordinador.getInstance().getControladorPrincipal().cargarTablaRegistros(registros);
                Coordinador.getInstance().mostrarBarraProgreso(false);
                Coordinador.getInstance().getControladorPrincipal().cargarGruposEnRegistros();
                Coordinador.getInstance().getControladorPrincipal().cargarPruebasEnRegistros();
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
                Prueba prueba = null;

                // Por cada hoja del archivo excel...
                for (int numHoja = 0; numHoja < workbook.getNumberOfSheets(); numHoja++) {
                    // Obtenemos el número de filas, columnas y la hoja.
                    Sheet hoja = workbook.getSheetAt(numHoja);
                    // Si la hoja es la de Tipos se descarta (hoja donde se encuentran las listas de tipos de prueba)
                    if (!hoja.getSheetName().equals("Tipos")) {
                        Iterator<Row> iteradorFila = hoja.iterator();

                        Row fila = null;/* = iteradorFila.next();
                         fila = iteradorFila.next(); //Cabecera, no contiene datos
                         fila = iteradorFila.next();*/     //Fila donde se encuentra los datos de las pruebas

                        for (int i = 0; i <= PRIMERA_FILA - hoja.getFirstRowNum(); i++) {
                            fila = iteradorFila.next();
                        }
                        if (fila.getLastCellNum() < PRIMERA_COLUMNA_PRUEBA + NUM_PARAMPRUEBA + 1) {
                            // Se pasa a la siguiente hoja, parametros no correctos
                        } else {
                            int columna = PRIMERA_COLUMNA_PRUEBA;
                            Cell celda = fila.getCell(columna);
                            try {
                                String nombrePrueba = celda.getStringCellValue();
                                columna++;
                                TipoPrueba tipoPrueba = TipoPrueba.valueOf(fila.getCell(columna).getStringCellValue());
                                columna++;
                                TipoResultado tipoResultado = TipoResultado.valueOf(fila.getCell(columna).getStringCellValue());

                                PruebaJpa pruebajpa = new PruebaJpa();
                                prueba = pruebajpa.findPruebaByNombreCompeticion(nombrePrueba, Coordinador.getInstance().getSeleccionada().getId());
                                if (prueba == null) {
                                    try {
                                        prueba = PruebaService.crearPrueba(Coordinador.getInstance().getSeleccionada(), nombrePrueba, tipoPrueba.toString(), tipoResultado.toString());
                                    } catch (InputException ex) {
                                        //throw new InputException(ex.getMessage());
                                    }
                                } else {
                                    // Si la prueba ya existe, se modifica
                                    prueba.setTipo(tipoPrueba.toString());
                                    prueba.setTiporesultado(tipoResultado.toString());
                                    try {
                                        pruebajpa.edit(prueba);
                                    } catch (NonexistentEntityException ex) {
                                        //throw new InputException("Prueba no encontrada");
                                    } catch (Exception ex) {
                                        //throw new InputException(ex.getMessage());
                                    }
                                }
                            } catch (IllegalArgumentException iae) {
                                //throw new InputException(iae.getMessage());
                            }
                            fila = iteradorFila.next(); // Cabecera
                            if (prueba != null) {
                                columna = PRIMERA_COLUMNA;

                                while (iteradorFila.hasNext()) {
                                    fila = iteradorFila.next();
                                    // Si es una prueba individual..
                                    if (prueba.getTipo().equals(TipoPrueba.Individual.toString())) {
                                        try {
                                            // Leemos el dorsal
                                            Double doubleData = fila.getCell(columna).getNumericCellValue();
                                            Integer dorsal = doubleData.intValue();
                                            ParticipanteJpa participanteJpa = new ParticipanteJpa();
                                            Participante participante = participanteJpa.findByDorsalAndCompeticion(dorsal,
                                                    Coordinador.getInstance().getSeleccionada().getId());
                                            // Si existe una persona con ese dorsal se crean los registros, sino se pasa al siguiente dorsal
                                            if (participante != null) {
                                                columna++;
                                                while (columna < fila.getLastCellNum()) {
                                                    //Se lee el registro(marca)
                                                    celda = fila.getCell(columna);
                                                    if (celda != null) {
                                                        // Se comprueba el tipo de Prueba y se crea un registro con los datos correspondientes
                                                        if (prueba.getTiporesultado().equals(TipoResultado.Tiempo.toString())) {
                                                            String tiempo = celda.toString();
                                                            if (tiempo.length() > 0) {
                                                                try {
                                                                    RegistroService.crearRegistro(
                                                                            Coordinador.getInstance().getSeleccionada(),
                                                                            dorsal,
                                                                            prueba.getNombre(),
                                                                            null,
                                                                            false,
                                                                            RegistroService.getSegundos(tiempo),
                                                                            RegistroService.getMinutos(tiempo),
                                                                            RegistroService.getHoras(tiempo));
                                                                } catch (InputException ex) {
                                                                    throw new InputException(ex.getMessage());
                                                                }
                                                            }
                                                        } else {
                                                            String marca = celda.toString();
                                                            try {
                                                                RegistroService.crearRegistro(
                                                                        Coordinador.getInstance().getSeleccionada(),
                                                                        dorsal,
                                                                        prueba.getNombre(),
                                                                        null,
                                                                        false,
                                                                        Double.valueOf(marca),
                                                                        null,
                                                                        null);
                                                            } catch (InputException ex) {
                                                                throw new InputException(ex.getMessage());
                                                            }
                                                        }
                                                    }
                                                    columna++;
                                                }
                                            } else {
                                                //throw new InputException("Dorsal no encontrado");
                                            }
                                        } catch (NumberFormatException nfe) {
                                            //throw new InputException("Formato numérico no válido");
                                        } finally {
                                            columna = PRIMERA_COLUMNA;
                                        }
                                    } else {
                                        try {
                                            // Leemos el nombre del equipo
                                            data = fila.getCell(columna).getStringCellValue();
                                            String nombreEquipo = data.toString();
                                            EquipoJpa equipoJpa = new EquipoJpa();
                                            Equipo equipo = equipoJpa.findByNombreAndCompeticion(nombreEquipo,
                                                    Coordinador.getInstance().getSeleccionada().getId());
                                            // Si existe un equipo con ese nombre se crean los registros 
                                            if (equipo != null) {
                                                columna++;
                                                while (columna < fila.getLastCellNum()) {
                                                    // Si la prueba es de tipo tiempo
                                                    celda = fila.getCell(columna);

                                                    if (celda != null) {
                                                        if (prueba.getTiporesultado().equals(TipoResultado.Tiempo.toString())) {
                                                            data = celda.getStringCellValue();
                                                            //System.out.println("Tiempo:" + data);
                                                            if (data.length() > 0) {
                                                                try {
                                                                    RegistroService.crearRegistro(
                                                                            Coordinador.getInstance().getSeleccionada(),
                                                                            null,
                                                                            prueba.getNombre(),
                                                                            nombreEquipo,
                                                                            false,
                                                                            RegistroService.getSegundos(String.valueOf(data)),
                                                                            RegistroService.getMinutos(String.valueOf(data)),
                                                                            RegistroService.getHoras(String.valueOf(data)));
                                                                } catch (InputException ex) {

                                                                }
                                                            }
                                                        } else {
                                                            String marca = celda.toString();
                                                            try {
                                                                RegistroService.crearRegistro(
                                                                        Coordinador.getInstance().getSeleccionada(),
                                                                        null,
                                                                        prueba.getNombre(),
                                                                        nombreEquipo,
                                                                        false,
                                                                        Double.valueOf(marca),
                                                                        null,
                                                                        null);
                                                            } catch (InputException ex) {

                                                            }
                                                        }
                                                    }
                                                    columna++;
                                                }
                                            } else {
                                                //throw new InputException("Equipo no encontrado");
                                            }
                                        } catch (NumberFormatException nfe) {
                                            //throw new InputException("Formato numérico no válido");
                                        } finally {
                                            columna = PRIMERA_COLUMNA;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                file.close();
            } catch (FileNotFoundException ex) {
                throw new InputException("Archivo no encontrado");
            } catch (IOException ex) {
                throw new InputException("Archivo no válido");
            } catch (InvalidFormatException ex) {
                throw new InputException("Formato de archivo no válido");
            }
            return null;
        }

    }
}
