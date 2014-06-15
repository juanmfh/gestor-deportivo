package main;

import controlador.ControlEquipos;
import controlador.ControlGrupos;
import controlador.ControlPruebas;
import controlador.Coordinador;
import controlador.TipoPrueba;
import controlador.TipoResultado;
import dao.EquipoJpa;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import javax.swing.SwingWorker;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import modelo.Grupo;
import dao.GrupoJpa;
import dao.ParticipanteJpa;
import dao.PruebaJpa;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.WorkbookSettings;
import modelo.Equipo;
import modelo.Participante;
import modelo.Prueba;

/**
 *
 * @author JuanM
 */
public class ImportarParticipantes extends SwingWorker<Void, Void> {

    String ruta;

    public ImportarParticipantes(String rutaFichero) {
        ruta = rutaFichero;
    }

    protected void done() {
        Coordinador.getInstance().getControladorPrincipal().cargarTablaParticipantes();
        Coordinador.getInstance().setEstadoLabel("Participantes importados", Color.BLUE);
        Coordinador.getInstance().mostrarBarraProgreso(false);
    }

    @Override
    protected Void doInBackground() {
        Workbook excel = null;
        WorkbookSettings ws = new WorkbookSettings();
        ws.setEncoding("CP1250");
        try {
            int dorsal = 1;
            excel = Workbook.getWorkbook(new File(ruta), ws);

            String data;
            for (int numHoja = 0; numHoja < excel.getNumberOfSheets(); numHoja++) {

                Sheet hoja = excel.getSheet(numHoja);
                int numFilas = hoja.getRows();
                int numColumnas = hoja.getColumns();

                //System.out.println("Num de filas: " + numFilas + ", Num de colm: " + numColumnas);
                int fila = 2;
                int columna = 5;

                List<String> nombresPruebas = new ArrayList();
                PruebaJpa pruebajpa = new PruebaJpa();
                Prueba prueba;
                // Obtenemos el nombre de las pruebas
                while (columna < numColumnas) {
                    data = hoja.getCell(columna, fila).getContents();
                    nombresPruebas.add(data);
                    prueba = pruebajpa.findPruebaByNombreCompeticion(data, Coordinador.getInstance().getSeleccionada().getId());
                    // Se crea una nueva prueba, por defecto se crea de tipo individual y con un resultado numérico.
                    // Esto se podrá modificar luego manualmente en el programa
                    if (prueba == null) {
                        ControlPruebas.crearPrueba(data, TipoPrueba.Individual.toString(), TipoResultado.Numerica.toString());
                    }
                    columna++;
                }

                fila++;
                columna = 1;

                // Iteramos sobre los participantes
                while (fila < numFilas) {
                    try {
                        ParticipanteJpa participantejpa = new ParticipanteJpa();
                        Participante participante = new Participante();
                        // Leemos los apellidos
                        data = hoja.getCell(columna, fila).getContents();
                        System.out.println("APELLIDOS: " + data);
                        participante.setApellidos(data);
                        columna++;
                        // Leemos el nombre
                        data = hoja.getCell(columna, fila).getContents();
                        System.out.println("NOMBRE: " + data);
                        // QUITAR ESTO
                        if (data == null) {
                            data = " ";
                        }
                        participante.setNombre(data);
                        columna++;
                        // Leemos el nombre del grupo
                        GrupoJpa grupojpa = new GrupoJpa();
                        Grupo grupo;
                        data = hoja.getCell(columna, fila).getContents();
                        System.out.println("GRUPO: " + data);
                        grupo = grupojpa.findGrupoByNombreAndCompeticion(data, Coordinador.getInstance().getSeleccionada().getId());
                        if (grupo == null) {
                            grupo = ControlGrupos.crearGrupo(data, null);
                        }
                        participante.setGrupoId(grupo);
                        columna++;
                        // Leeemos el nombre del equipo
                        EquipoJpa equipojpa = new EquipoJpa();
                        Equipo equipo;
                        data = hoja.getCell(columna, fila).getContents();
                        System.out.println("EQUIPO: " + data);
                        if (!data.equals("")) {
                            equipo = equipojpa.findByNombreAndCompeticion(data, Coordinador.getInstance().getSeleccionada().getId());
                            if (equipo == null) {
                                equipo = ControlEquipos.crearEquipo(data, grupo.getNombre());
                            }
                            participante.setEquipoId(equipo);
                        }
                        columna++;
                        // Leemos la prueba asignada al participante
                        data = hoja.getCell(columna, fila).getContents();
                        System.out.println("PRUEBAASIGNADA: [" + data + "]");
                        boolean pruebaAsignada = false;
                        while (!pruebaAsignada && columna <= nombresPruebas.size()+5) {
                            if (!data.equals("")) {
                                Prueba pr = pruebajpa.findPruebaByNombreCompeticion(nombresPruebas.get(columna - 5),
                                        Coordinador.getInstance().getSeleccionada().getId());
                                System.out.println("PRUEBA: " + pr.getNombre());
                                participante.setPruebaasignada(pr);
                                pruebaAsignada = true;
                            }
                            columna++;
                            data = hoja.getCell(columna, fila).getContents();
                        }
                        participante.setDorsal(dorsal++);
                        participantejpa.create(participante);

                    } catch (RuntimeException ex) {
                        //Logger.getLogger(ImportarParticipantes.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        columna = 1;
                        fila++;
                    }

                }

                //System.out.println("LISTAAA: "+nombresPruebas.toString());
                /*                while (fila < numFilas) {
                 try {
                 data = hoja.getCell(columna, fila).getContents();
                 if (data.equals("Grupo")) {
                 // Crear grupo
                 data = hoja.getCell(columna + 1, fila).getContents();
                 Grupo g = ControlGrupos.crearGrupo(data.toString(), null);
                 if (g == null) {
                 GrupoJpa grupojpa = new GrupoJpa();
                 g = grupojpa.findGrupoByNombreAndCompeticion(data.toString(),
                 Coordinador.getInstance().getSeleccionada().getId());
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
                 g = grupojpa.findGrupoByNombreAndCompeticion(nombreSubGrupo,
                 Coordinador.getInstance().getSeleccionada().getId());
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
                 nombreEquipo.length() == 0 ? "Ninguno" : nombreEquipo,
                 "Ninguna");
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
                 }*/
            }

        } catch (IOException | BiffException ex) {
            Logger.getLogger(ImportarParticipantes.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
