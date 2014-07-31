package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 *
 * @author JuanM
 */
public class IOFile {

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
            return false;
        } catch (IOException ioException) {
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
