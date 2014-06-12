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
import controlador.TipoPrueba;
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
import jpa.GrupoJpa;
import jpa.PruebaJpa;
import jpa.RegistroJpa;
import modelo.Equipo;

/**
 *
 * @author JuanM
 */
public class PDFHelper {

    public static boolean imprimirResultadosPDF() {
        Competicion c = Coordinador.getInstance().getControladorPrincipal().getSeleccionada();
        if (c != null) {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setDialogTitle("Guardar en");
            int res = fc.showSaveDialog(null);
            if (res == JFileChooser.APPROVE_OPTION) {
                try {
                    PDFHelper.crearPdf(fc.getSelectedFile().getPath(), c);
                } catch (FileNotFoundException | DocumentException ex) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public static void crearPdf(String path, Competicion c) throws FileNotFoundException, DocumentException {
        //System.out.println(patch);
        FileOutputStream archivo = new FileOutputStream(path
                + "/" + c.getNombre() + "_resultados.pdf");
        Document documento = new Document(PageSize.LETTER, 80, 80, 75, 75);
        PdfWriter writer = PdfWriter.getInstance(documento, archivo);

        documento.open();
        documento.addTitle("Resultados_" + c.getNombre());

        if (c.getImagen() != null && c.getImagen().length() > 0) {
            try {
                Image logo = Image.getInstance(System.getProperty("user.dir")
                        + "/resources/img/" + c.getImagen());
                logo.scaleAbsolute(100, 100);
                documento.add(logo);
            } catch (BadElementException | IOException ex) {
            }
        }

        Paragraph titulo = new Paragraph(c.getNombre().toUpperCase()
                + " - RESULTADOS");
        titulo.setAlignment(Paragraph.ALIGN_CENTER);
        documento.add(titulo);

        if (c.getCiudad() != null && c.getCiudad().length() > 0) {
            Paragraph lugar = new Paragraph("Lugar: " + c.getCiudad());
            documento.add(lugar);
        }

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
        if (c.getOrganizador() != null && c.getOrganizador().length() > 0) {
            Paragraph organizador = new Paragraph("Organizador: "
                    + c.getOrganizador());
            documento.add(organizador);
        }

        PruebaJpa pruebajpa = new PruebaJpa();
        List<Prueba> pruebas = pruebajpa.findPruebasByCompeticon(c);
        RegistroJpa registrojpa = new RegistroJpa();
        GrupoJpa grupojpa = new GrupoJpa();
        SimpleDateFormat dt = new SimpleDateFormat("HH:mm:ss.S");
        for (Prueba p : pruebas) {
            List<Participante> participantes = null;
            List<Equipo> equipos = null;
            if (p.getTiporesultado().equals("Tiempo")) {
                if (p.getTipo().equals(TipoPrueba.Individual.toString())) {
                    participantes = registrojpa.findParticipantesConRegistrosTiempo(c.getId(), p.getId());
                } else {
                    equipos = registrojpa.findEquiposConRegistrosTiempo(c.getId(), p.getId());
                }
            } else {
                if (p.getTipo().equals(TipoPrueba.Individual.toString())) {
                    participantes = registrojpa.findParticipantesConRegistrosNum(c.getId(), p.getId());
                } else {
                    equipos = registrojpa.findEquiposConRegistrosNum(c.getId(), p.getId());
                }

            }

            List<Registro> registros;

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

                for (Participante part : participantes) {

                    Grupo g;
                    g = grupojpa.findByParticipanteCompeticion(c.getId(), part.getId());
                    tabla.addCell(new Phrase(String.valueOf(part.getDorsal()), normal));
                    tabla.addCell(new Phrase(part.getApellidos() + ", " + part.getNombre(), normal));

                    tabla.addCell(new Paragraph(g.getNombre(), normal));

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
            } else {
                for (Equipo equipo : equipos) {

                    Grupo g;

                    g = grupojpa.findByEquipoCompeticion(c.getId(), equipo.getId());
                    tabla.addCell(new Phrase("#", normal));
                    tabla.addCell(new Phrase(equipo.getNombre(), normal));

                    tabla.addCell(new Paragraph(g.getNombre(), normal));

                    registros = registrojpa.findRegistroByParticipantePruebaCompeticionOrderByNumIntento(c.getId(), p.getId(), equipo.getId());
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

            documento.add(tabla);

        }

        documento.close();
        writer.close();

    }

}
