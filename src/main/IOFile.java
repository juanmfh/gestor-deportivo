package main;

import controlador.ControlGrupos;
import controlador.ControlParticipantes;
import controlador.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import modelo.Equipo;
import modelo.Grupo;
import modelo.Persona;
import modelo.Prueba;
import modelo.dao.EquipoJpa;
import modelo.dao.GrupoJpa;
import modelo.dao.PersonaJpa;
import modelo.dao.PruebaJpa;

/**
 *
 * @author JuanM
 */
public class IOFile {

    /**
     * Devuelve en un objeto Date la fecha pasada como parámetros en String o
     * null si los parámetros no son correctos.
     *
     * @param dia numero del dia en String.
     * @param mes numero del mes en String.
     * @param año numero del año en String.
     * @return Date
     */
    public static Date formatearFecha(String dia, String mes, String año) {
        String fecha = null;
        try {
            Date res = null;
            if (año.length() == 4) {
                fecha = String.valueOf(Integer.parseInt(año)) + "-";
                if (mes.length() == 1) {
                    fecha += "0" + mes + "-";
                } else {
                    fecha += mes + "-";
                }
                if (dia.length() == 1) {
                    fecha += "0" + dia;
                } else {
                    fecha += dia;
                }
                SimpleDateFormat textFormat = new SimpleDateFormat("yyyy-MM-dd");
                res = textFormat.parse(fecha);
            }
            return res;
        } catch (NumberFormatException | ParseException exception) {
            return null;
        }
    }

    /**
     * Copia el archivo que se encuentra en la ruta pasada como parámetro
     * "rutaFichero" en la ruta "rutaCopia"
     *
     * @param rutaFichero ruta absoluta donde se encuentra el fichero original.
     * @param rutaCopia ruta absoluta donde se copiará el fichero.
     * @return true si se ha copiado correctamente el fichero
     */
    public static boolean copiarFichero(String rutaFichero, String rutaCopia) {

        try {
            FileInputStream fis;
            fis = new FileInputStream(rutaFichero);
            String nombreFichero = getNombreFichero(rutaFichero);
            File copia = new File(rutaCopia);
            copia.mkdirs();
            FileOutputStream fos = new FileOutputStream(copia
                    + "/" + nombreFichero);
            FileChannel inChannel = fis.getChannel();
            FileChannel outChannel = fos.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            fis.close();
            fos.close();
            return true;
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println(fileNotFoundException.toString());
            return false;
        } catch (IOException ioException) {
            System.out.println(ioException.toString());
            return false;
        }
    }

    /**
     * Devuelve el nombre del fichero de la ruta absoluta pasada como parametro
     * "rutaAbsoluta".
     *
     * @param rutaAbsoluta ruta absoluta donde se encuentra el fichero.
     * @return nombre del fichero
     */
    public static String getNombreFichero(String rutaAbsoluta) {
        return rutaAbsoluta.substring(rutaAbsoluta.lastIndexOf("\\") + 1,
                rutaAbsoluta.length());
    }

    public static void importarRegistrosDeExcel(String rutaFichero) {

        Workbook excel = null;
        try {
            excel = Workbook.getWorkbook(new File(rutaFichero));
            String data;
            Prueba p = null;
            for (int numHoja = 0; numHoja < excel.getNumberOfSheets(); numHoja++) {

                Sheet hoja = excel.getSheet(numHoja);
                int numFilas = hoja.getRows();
                int numColumnas = hoja.getColumns();

                int fila = 0;
                int columna = 1;

                // Obtenemos la prueba con sus parámetros y si no existe se crea
                if (columna + 2 <= numColumnas) {
                    try{
                    String nombrePrueba = hoja.getCell(columna, fila).getContents().toString();
                    TipoPrueba tipoPrueba = TipoPrueba.valueOf(hoja.getCell(columna + 1, fila).getContents().toString());
                    TipoResultado tipoResultado = TipoResultado.valueOf(hoja.getCell(columna + 2, fila).getContents().toString());
                    
                    p = ControlPruebas.crearPrueba(nombrePrueba, tipoPrueba.toString(), tipoResultado.toString());
                    if (p == null) {
                        PruebaJpa pruebajpa = new PruebaJpa();
                        p = pruebajpa.findPruebaByNombreCompeticion(
                                nombrePrueba,
                                Coordinador.getInstance().getSeleccionada().getId());
                    }
                    }catch(IllegalArgumentException iae){
                        
                    }
                }
                if (p != null) {
                    columna = 0;
                    fila = 1;
                    while (fila < numFilas) {
                        // Si es una prueba individual
                        if (p.getTipo().equals(TipoPrueba.Individual.toString())) {
                            try {
                                // Leemos el dorsal
                                data = hoja.getCell(columna, fila).getContents();
                                Integer dorsal = Integer.parseInt(data.toString());
                                PersonaJpa personaJpa = new PersonaJpa();
                                Persona persona = personaJpa.findByDorsalAndCompeticion(dorsal,
                                        Coordinador.getInstance().getSeleccionada().getId());
                                // Si existe una persona con ese dorsal
                                if (persona != null) {
                                    columna++;
                                    while (columna < numColumnas) {
                                        // Si la prueba es de tipo tiempo
                                        data = hoja.getCell(columna, fila).getContents();
                                        if (!data.isEmpty()) {
                                            if (p.getTiporesultado().equals(TipoResultado.Tiempo.toString())) {
                                                String tiempo = data.toString();
                                                ControlRegistros.crearRegistro(
                                                        dorsal,
                                                        p.getNombre(),
                                                        null,
                                                        false,
                                                        ControlRegistros.getSegundos(tiempo),
                                                        ControlRegistros.getMinutos(tiempo),
                                                        ControlRegistros.getHoras(tiempo));
                                            } else {
                                                String marca = data.toString();
                                                ControlRegistros.crearRegistro(
                                                        dorsal,
                                                        p.getNombre(),
                                                        null,
                                                        false,
                                                        Double.valueOf(marca),
                                                        null,
                                                        null);
                                            }
                                        }
                                        columna++;
                                    }
                                    columna = 0;
                                }
                            } catch (NumberFormatException nfe) {

                            } finally {
                                fila++;
                            }
                            // Si es una prueba de equipos    
                        } else {
                            try {
                                // Leemos el nombre del equipo
                                data = hoja.getCell(columna, fila).getContents();
                                String nombreEquipo = data.toString();
                                EquipoJpa equipoJpa = new EquipoJpa();
                                Equipo equipo = equipoJpa.findByNombreAndCompeticion(nombreEquipo,
                                        Coordinador.getInstance().getSeleccionada().getId());
                                // Si existe una persona con ese dorsal
                                if (equipo != null) {
                                    columna++;
                                    while (columna < numColumnas) {
                                        // Si la prueba es de tipo tiempo
                                        data = hoja.getCell(columna, fila).getContents();
                                        if (!data.isEmpty()) {
                                            if (p.getTiporesultado().equals(TipoResultado.Tiempo.toString())) {
                                                String tiempo = data.toString();
                                                ControlRegistros.crearRegistro(
                                                        null,
                                                        p.getNombre(),
                                                        nombreEquipo,
                                                        false,
                                                        ControlRegistros.getSegundos(tiempo),
                                                        ControlRegistros.getMinutos(tiempo),
                                                        ControlRegistros.getHoras(tiempo));
                                            } else {
                                                String marca = data.toString();
                                                ControlRegistros.crearRegistro(
                                                        null,
                                                        p.getNombre(),
                                                        nombreEquipo,
                                                        false,
                                                        Double.valueOf(marca),
                                                        null,
                                                        null);
                                            }
                                        }
                                        columna++;
                                    }
                                    columna = 0;
                                }
                            } catch (NumberFormatException nfe) {

                            } finally {
                                fila++;
                            }
                            
                        }
                    }
                }
            }
        } catch (IOException | BiffException ex) {
            Logger.getLogger(IOFile.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (excel != null) {
                excel.close();
            }

        }

    }

    public static void importarParticipantesDeExcel(String rutaFichero) {
        Workbook excel = null;
        try {
            excel = Workbook.getWorkbook(new File(rutaFichero));

            String data;
            for (int numHoja = 0; numHoja < excel.getNumberOfSheets(); numHoja++) {

                Sheet hoja = excel.getSheet(numHoja);
                int numFilas = hoja.getRows();
                int numColumnas = hoja.getColumns();

                String nombreSubGrupo;
                int fila = 0;
                int columna = 0;
                String grupoActual = null;
                while (fila < numFilas) {
                    try {
                        data = hoja.getCell(columna, fila).getContents();
                        if (data.equals("Grupo")) {
                            // Crear grupo
                            data = hoja.getCell(columna + 1, fila).getContents();
                            Grupo g = ControlGrupos.crearGrupo(data.toString(), null);
                            if (g == null) {
                                GrupoJpa grupojpa = new GrupoJpa();
                                g = grupojpa.findGrupoByNombre(data.toString());
                            }
                            if (g != null) {
                                grupoActual = g.getNombre();
                            }
                            //System.out.println("Crear grupo " + data.toString());
                        } else if (data.equals("SubGrupo")) {
                            // Crear subgrupo
                            data = hoja.getCell(columna + 1, fila).getContents();
                            nombreSubGrupo = data;
                            data = hoja.getCell(columna + 2, fila).getContents();
                            Grupo g = ControlGrupos.crearGrupo(nombreSubGrupo, data.toString());
                            if (g == null) {
                                GrupoJpa grupojpa = new GrupoJpa();
                                g = grupojpa.findGrupoByNombre(nombreSubGrupo);
                            }
                            if (g != null) {
                                grupoActual = g.getNombre();
                            }
                            //System.out.println("Crear subgrupo " + nombreSubGrupo);

                        } else {
                            // Crear participante
                            String nombreEquipo = "";
                            Integer edad = null;
                            Integer sexo = null;
                            if (numColumnas > 3) {
                                nombreEquipo = hoja.getCell(columna + 3, fila).getContents().toString();
                                // Si el equipo no existe se crea
                                ControlEquipos.crearEquipo(nombreEquipo, grupoActual);
                                if (numColumnas > 4) {
                                    try {
                                        edad = Integer.parseInt(hoja.getCell(columna + 4, fila).getContents().toString());
                                    } catch (NumberFormatException ex) {

                                    }
                                    if (numColumnas > 5) {
                                        try {
                                            sexo = hoja.getCell(columna + 5, fila).getContents().toString().equals("H") ? 0 : 1;
                                        } catch (NumberFormatException ex) {

                                        }
                                    }
                                }
                            }

                            ControlParticipantes.crearParticipante(
                                    // Nombre
                                    hoja.getCell(columna + 1, fila).getContents().toString(),
                                    // Apellidos
                                    hoja.getCell(columna + 2, fila).getContents().toString(),
                                    // Dorsal
                                    Integer.parseInt(hoja.getCell(columna, fila).getContents().toString()),
                                    grupoActual,
                                    edad,
                                    sexo,
                                    // Equipo
                                    nombreEquipo.length() == 0 ? "Ninguno" : nombreEquipo);
                            //System.out.println("Crear participante, Dorsal:["+ data + "]");
                        }
                    } catch (NumberFormatException  e) {
                        //Logger.getLogger(IOFile.class.getName()).log(Level.SEVERE, null, e);
                    } finally {
                        fila++;
                    }
                    //columna = 0;
                }
            }

        } catch (IOException | BiffException ex) {
            System.out.println("Error de lectura fichero excel");
        } finally {
            if (excel != null) {
                excel.close();
            }

        }
    }
}
