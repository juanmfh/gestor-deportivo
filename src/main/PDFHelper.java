package main;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import controlador.Coordinador;
import controlador.InputException;
import controlador.TipoPrueba;
import dao.EquipoJpa;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.JFileChooser;
import modelo.Competicion;
import modelo.Grupo;
import modelo.Participante;
import modelo.Prueba;
import modelo.Registro;
import dao.GrupoJpa;
import dao.ParticipanteJpa;
import dao.PruebaJpa;
import dao.RegistroJpa;
import java.util.ArrayList;
import modelo.Equipo;

/**
 *
 * @author JuanM
 */
public class PDFHelper {

    public static void imprimirResultadosPDF(List<String> nombrePruebas, List<String> nombreGrupos, Boolean listaSalida) throws InputException {
        Competicion c = Coordinador.getInstance().getControladorPrincipal().getSeleccionada();
        if (c != null) {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setDialogTitle("Guardar en");
            int res = fc.showSaveDialog(null);
            if (res == JFileChooser.APPROVE_OPTION) {
                try {
                    PDFHelper.crearPdf(fc.getSelectedFile().getPath(), c, nombrePruebas, nombreGrupos, listaSalida);
                } catch (FileNotFoundException | DocumentException ex) {
                    throw new InputException("No se pudo crear un archivo en esa ruta. Compruebe que el fichero destino no este abierto");
                }
            }
        } else {
            throw new InputException("Competición no seleccionada");
        }
    }

    /**
     * Crea un fichero pdf en la ruta pasada como parámetro (path)
     *
     * @param path Ruta donde se guardará el fichero (sin el nombre del fichero)
     * @param c Competición de la que se va a crear los resultados
     * @param nombrePruebas Nombre de las pruebas de las que se va a crear los
     * resultados
     * @param nombreGrupos Nombre de los grupos de los que se va a crear los
     * resultados
     * @throws FileNotFoundException
     * @throws DocumentException
     */
    public static void crearPdf(String path, Competicion c, List<String> nombrePruebas, List<String> nombreGrupos, boolean listaSalida) throws FileNotFoundException, DocumentException {

        // Se crea un fichero de salida en la ruta seleccionada
        FileOutputStream archivo = new FileOutputStream(path
                + "/" + c.getNombre() + "_resultados.pdf");
        Document documento = new Document(PageSize.LETTER, 80, 80, 75, 75);
        PdfWriter writer = PdfWriter.getInstance(documento, archivo);

        documento.open();
        documento.addTitle("Resultados_" + c.getNombre());

        // Se cargar el logo de la competición si existe
        if (c.getImagen() != null && c.getImagen().length() > 0) {
            try {
                Image logo = Image.getInstance(System.getProperty("user.dir")
                        + "/resources/img/" + c.getImagen());
                logo.scaleAbsolute(100, 100);
                documento.add(logo);
            } catch (BadElementException | IOException ex) {
                // Sin logo
            }
        }
        // DATOS DE LA COMPETICION

        // Titulo
        Paragraph titulo = new Paragraph(c.getNombre().toUpperCase()
                + " - RESULTADOS");
        titulo.setAlignment(Paragraph.ALIGN_CENTER);
        documento.add(titulo);

        // Ciudad
        if (c.getCiudad() != null && c.getCiudad().length() > 0) {
            Paragraph lugar = new Paragraph("Lugar: " + c.getCiudad());
            documento.add(lugar);
        }

        // Fechas
        SimpleDateFormat fechas = new SimpleDateFormat("dd-MM-yyyy");
        if (c.getFechainicio() != null) {
            Paragraph fechaInicio = new Paragraph("Fecha de Inicio: "
                    + fechas.format(c.getFechainicio()));
            documento.add(fechaInicio);
        }
        if (c.getFechafin() != null) {
            Paragraph fechaFin = new Paragraph("Fecha de Fin: "
                    + fechas.format(c.getFechafin()));
            documento.add(fechaFin);
        }

        // Organizador
        if (c.getOrganizador() != null && c.getOrganizador().length() > 0) {
            Paragraph organizador = new Paragraph("Organizador: "
                    + c.getOrganizador());
            documento.add(organizador);
        }

        PruebaJpa pruebajpa = new PruebaJpa();
        RegistroJpa registrojpa = new RegistroJpa();
        GrupoJpa grupojpa = new GrupoJpa();

        // Se cargan las Pruebas de las que se van a generar resultados
        List<Prueba> pruebas;
        if (nombrePruebas == null) {
            pruebas = pruebajpa.findPruebasByCompeticon(c);
        } else {
            pruebas = new ArrayList();
            for (String nombrePrueba : nombrePruebas) {
                pruebas.add(pruebajpa.findPruebaByNombreCompeticion(nombrePrueba, c.getId()));
            }
        }
        ParticipanteJpa participanteJpa = new ParticipanteJpa();
        EquipoJpa equipoJpa = new EquipoJpa();

        // Formato de tiempo
        SimpleDateFormat dt = new SimpleDateFormat("HH:mm:ss.S");

        // Por cada prueba se crea una tabla con los registros ordenados por clasificación
        for (Prueba p : pruebas) {
            List<Participante> participantes = null;
            List<Equipo> equipos = null;

            // Se comprueba el tipo de prueba y se realiza una búsqueda en la BD con los parámetros adecuados
            if (p.getTiporesultado().equals("Tiempo")) {
                if (p.getTipo().equals(TipoPrueba.Individual.toString())) {
                    if (nombreGrupos == null) {
                        if (listaSalida) {
                            // Buscar todos los participantes de una competicion
                            List<Grupo> grupos = grupojpa.findGruposByCompeticion(c);
                            participantes = new ArrayList();
                            for(Grupo g : grupos){
                                List<Participante> aux = participanteJpa.findParticipantesByGrupo(g.getId());
                                for(Participante particip: aux){
                                    participantes.add(particip);
                                }
                            }
                        } else {
                            participantes = registrojpa.findParticipantesConRegistrosTiempo(c.getId(), p.getId());
                        }
                    } else {
                        if (listaSalida) {
                            participantes = new ArrayList();
                            for (String nombreGrupo : nombreGrupos) {
                                Grupo g = grupojpa.findGrupoByNombreAndCompeticion(nombreGrupo, c.getId());
                                List<Participante> aux = participanteJpa.findParticipantesByGrupo(g.getId());
                                for (Participante particip : aux) {
                                    participantes.add(particip);
                                }
                            }
                        } else {
                            participantes = registrojpa.findParticipantesConRegistrosTiempoByGrupos(c.getId(), p.getId(), nombreGrupos);
                        }
                    }
                } else {
                    if (nombreGrupos == null) {
                        if (listaSalida) {
                            //Buscar todos los equipos de una competición
                            equipos = equipoJpa.findByCompeticion(c.getId());
                        } else {
                            equipos = registrojpa.findEquiposConRegistrosTiempo(c.getId(), p.getId());
                        }
                    } else {
                        if (listaSalida) {
                            equipos = new ArrayList();
                            for (String nombreGrupo : nombreGrupos) {
                                Grupo g = grupojpa.findGrupoByNombreAndCompeticion(nombreGrupo, c.getId());
                                List<Equipo> aux = equipoJpa.findByGrupo(g.getId());
                                for (Equipo equi : aux) {
                                    equipos.add(equi);
                                }
                            }
                        } else {
                            equipos = registrojpa.findEquiposConRegistrosTiempoByGrupo(c.getId(), p.getId(), nombreGrupos);
                        }
                    }
                }
            } else {
                if (p.getTipo().equals(TipoPrueba.Individual.toString())) {
                    if (nombreGrupos == null) {
                        if (listaSalida) {
                            // Buscar todos los participantes de una competicion
                            List<Grupo> grupos = grupojpa.findGruposByCompeticion(c);
                            participantes = new ArrayList();
                            for(Grupo g : grupos){
                                List<Participante> aux = participanteJpa.findParticipantesByGrupo(g.getId());
                                for(Participante particip: aux){
                                    participantes.add(particip);
                                }
                            }
                        } else {
                            participantes = registrojpa.findParticipantesConRegistrosNum(c.getId(), p.getId());
                        }
                    } else {
                        if (listaSalida) {
                            participantes = new ArrayList();
                            for (String nombreGrupo : nombreGrupos) {
                                Grupo g = grupojpa.findGrupoByNombreAndCompeticion(nombreGrupo, c.getId());
                                List<Participante> aux = participanteJpa.findParticipantesByGrupo(g.getId());
                                for (Participante particip : aux) {
                                    participantes.add(particip);
                                }
                            }
                        } else {
                            participantes = registrojpa.findParticipantesConRegistrosNumByGrupo(c.getId(), p.getId(), nombreGrupos);
                        }
                    }
                } else {
                    if (nombreGrupos == null) {
                        if (listaSalida) {
                            equipos = equipoJpa.findByCompeticion(c.getId());
                        } else {
                            equipos = registrojpa.findEquiposConRegistrosNum(c.getId(), p.getId());
                        }
                    } else {
                        if (listaSalida) {
                            equipos = new ArrayList();
                            for (String nombreGrupo : nombreGrupos) {
                                Grupo g = grupojpa.findGrupoByNombreAndCompeticion(nombreGrupo, c.getId());
                                List<Equipo> aux = equipoJpa.findByGrupo(g.getId());
                                for (Equipo equi : aux) {
                                    equipos.add(equi);
                                }
                            }
                        } else {
                            equipos = registrojpa.findEquiposConRegistrosNumByGrupo(c.getId(), p.getId(), nombreGrupos);
                        }

                    }

                }

            }

            List<Registro> registros;

            // DATOS DE LA TABLA
            PdfPTable tabla = new PdfPTable(8);
            float[] columnWidths = {1f, 2f, 1f, 1f, 1f, 1f, 1f, 1f};
            Font normal = new Font(FontFamily.TIMES_ROMAN, 9);
            tabla.setWidths(columnWidths);
            tabla.setWidthPercentage(100);
            tabla.setSpacingBefore(30f);
            tabla.setSpacingAfter(30f);
            Phrase header = new Phrase(p.getNombre().toUpperCase());
            header.setFont(new Font(FontFamily.TIMES_ROMAN, 10));
            PdfPCell celda = new PdfPCell(header);
            celda.setColspan(8);
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);

            tabla.addCell(celda);
            tabla.addCell(new Phrase("DORSAL", normal));
            tabla.addCell(new Phrase("NOMBRE", normal));
            tabla.addCell(new Phrase("GRUPO", normal));
            tabla.addCell(new Phrase("1º", normal));
            tabla.addCell(new Phrase("2º", normal));
            tabla.addCell(new Phrase("MMP", normal));
            tabla.addCell(new Phrase("PUESTO", normal));
            tabla.addCell(new Phrase("PUNTOS", normal));
            int puesto = 1;

            if (p.getTipo().equals(TipoPrueba.Individual.toString())) {

                // Por cada participante se cargan sus registros y se escribe en la tabla
                for (Participante part : participantes) {

                    Grupo g;
                    g = grupojpa.findByParticipanteCompeticion(c.getId(), part.getId());
                    tabla.addCell(new Phrase(String.valueOf(part.getDorsal()), normal));
                    tabla.addCell(new Phrase(part.getApellidos() + ", " + part.getNombre(), normal));

                    tabla.addCell(new Paragraph(g.getNombre(), normal));
                    if(listaSalida){
                        for(int i=0; i<5;i++){
                            tabla.addCell("");
                        }
                    }else{
                        registros = registrojpa.findRegistroByParticipantePruebaCompeticionOrderByNumIntento(c.getId(), p.getId(), part.getId());
                        if (registros != null) {
                            if (p.getTiporesultado().equals("Tiempo")) {
                                tabla.addCell(new Phrase(String.valueOf(dt.format(registros.get(0).getTiempo())), normal));
                            } else {
                                tabla.addCell(new Phrase(String.valueOf(registros.get(0).getNum()), normal));
                            }
                        } else {
                            tabla.addCell("");
                        }
                        if (registros != null && registros.size() == 2) {
                            if (p.getTiporesultado().equals("Tiempo")) {

                                tabla.addCell(new Phrase(String.valueOf(dt.format(registros.get(1).getTiempo())), normal));
                            } else {
                                tabla.addCell(new Phrase(String.valueOf(registros.get(1).getNum()), normal));
                            }
                        } else {
                            tabla.addCell("");
                        }

                        if (p.getTiporesultado().equals("Tiempo")) {
                            tabla.addCell(new Phrase(String.valueOf(dt.format(
                                    registrojpa.findMinRegistroByParticipantePruebaCompeticion(
                                            part.getId(),
                                            c.getId(),
                                            p.getId()))),
                                    normal));
                        } else {
                            tabla.addCell(new Phrase(
                                    String.valueOf(
                                            registrojpa.findMaxRegistroByParticipantePruebaCompeticion(
                                                    part.getId(),
                                                    c.getId(),
                                                    p.getId())),
                                    normal));
                        }
                        tabla.addCell(new Phrase(String.valueOf(participantes.size()
                                - (participantes.size() - puesto)), normal));
                        tabla.addCell(new Phrase(String.valueOf(participantes.size()
                                - (puesto - 1)), normal));
                        puesto++;
                    }
                }
            } else {
                // Por cada equipo se cargan sus registros en la prueba y se escriben en la tabla
                for (Equipo equipo : equipos) {

                    Grupo g;

                    g = grupojpa.findByEquipoCompeticion(c.getId(), equipo.getId());
                    tabla.addCell(new Phrase("#", normal));
                    tabla.addCell(new Phrase(equipo.getNombre(), normal));

                    tabla.addCell(new Paragraph(g.getNombre(), normal));

                    if(listaSalida){
                        for(int i=0; i<5;i++){
                            tabla.addCell("");
                        }
                    }else{
                        registros = registrojpa.findRegistroByEquipoPruebaCompeticionOrderByNumIntento(c.getId(), p.getId(), equipo.getId());
                        if (registros != null) {
                            if (p.getTiporesultado().equals("Tiempo")) {
                                tabla.addCell(new Phrase(String.valueOf(dt.format(registros.get(0).getTiempo())), normal));
                            } else {
                                tabla.addCell(new Phrase(String.valueOf(registros.get(0).getNum()), normal));
                            }
                        } else {
                            tabla.addCell("");
                        }
                        if (registros != null && registros.size() == 2) {
                            if (p.getTiporesultado().equals("Tiempo")) {

                                tabla.addCell(new Phrase(String.valueOf(dt.format(registros.get(1).getTiempo())), normal));
                            } else {
                                tabla.addCell(new Phrase(String.valueOf(registros.get(1).getNum()), normal));
                            }
                        } else {
                            tabla.addCell("");
                        }

                        if (p.getTiporesultado().equals("Tiempo")) {
                            tabla.addCell(new Phrase(String.valueOf(dt.format(
                                    registrojpa.findMinRegistroByEquipoPruebaCompeticion(
                                            equipo.getId(),
                                            c.getId(),
                                            p.getId()))),
                                    normal));
                        } else {
                            tabla.addCell(new Phrase(
                                    String.valueOf(
                                            registrojpa.findMaxRegistroByEquipoPruebaCompeticion(
                                                    equipo.getId(),
                                                    c.getId(),
                                                    p.getId())),
                                    normal));
                        }
                        tabla.addCell(new Phrase(String.valueOf(equipos.size()
                                - (equipos.size() - puesto)), normal));
                        tabla.addCell(new Phrase(String.valueOf(equipos.size()
                                - (puesto - 1)), normal));
                        puesto++;
                    }
                }

            }
            // Se añade la tabla al documento
            documento.add(tabla);

        }

        documento.close();
        writer.close();

    }

}
