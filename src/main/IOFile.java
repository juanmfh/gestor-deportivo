package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    
}
