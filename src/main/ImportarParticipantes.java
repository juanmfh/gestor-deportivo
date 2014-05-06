/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import controlador.ControlEquipos;
import controlador.ControlGrupos;
import controlador.ControlParticipantes;
import controlador.Coordinador;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import javax.swing.SwingWorker;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import modelo.Grupo;
import modelo.dao.GrupoJpa;

/**
 *
 * @author JuanM
 */
public class ImportarParticipantes extends SwingWorker<Void, Void> {

    String ruta;

    public ImportarParticipantes(String rutaFichero) {
        ruta = rutaFichero;
    }
    
    protected void done(){
        Coordinador.getInstance().getControladorPrincipal().cargarTablaParticipantes();
        Coordinador.getInstance().setEstadoLabel("Participantes importados", Color.BLUE);
        Coordinador.getInstance().mostrarBarraProgreso(false);
    }
    
    @Override
    protected Void doInBackground() {
        Workbook excel = null;
        try {
            excel = Workbook.getWorkbook(new File(ruta));

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
                    } catch (NumberFormatException e) {
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
        return null;
    }

}
