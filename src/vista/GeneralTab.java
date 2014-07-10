
package vista;

import controlador.Coordinador;
import controlador.TipoPrueba;
import controlador.TipoResultado;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import net.coobird.thumbnailator.Thumbnails;
import static vista.VistaPrincipal.CREARPRUEBA;
import static vista.VistaPrincipal.ELIMINARPRUEBA;
import static vista.VistaPrincipal.LIMPIARPRUEBA;
import static vista.VistaPrincipal.MODIFICARPRUEBA;

/**
 *
 * @author JuanM
 */
public class GeneralTab extends javax.swing.JPanel {

    private JLabel nombreCompeticionLabel;
    private JLabel nombreCompeticion;
    private JLabel organizadorLabel;
    private JLabel organizador;
    private JLabel fechaInicioLabel;
    private JLabel fechaInicio;
    private JLabel fechaFinLabel;
    private JLabel fechaFin;
    private JPanel datosPanel;
    private JButton crearPruebaButton;
    private JButton modificarPruebaButton;
    private JButton eliminarPruebaButton;
    private final JTable pruebasTable;
    private DefaultTableModel modeloPruebasTable;
    private JScrollPane pruebasTableScrollPane;
    private JLabel nombrePruebaLabel;
    private JTextField nombrePruebaTextField;
    private JLabel tipoPruebaLabel;
    private JComboBox tipoPruebaComboBox;
    private JPanel formularioAñadirPruebaPanel;
    private JLabel tipoResultadoLabel;
    private JComboBox tipoResultadoComboBox;
    private JComboBox pruebasComboBox;
    private JButton limpiarPruebaButton;
    private JLabel listaPruebasLabel;
    private JLabel lugarLabel;
    private JLabel lugar;
    private JPanel imagePanel;
    private JPanel botonesPanel;

    public GeneralTab() {
        initComponents();

        this.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        datosPanel = new JPanel(new GridBagLayout());
        TitledBorder bordeDatosPanel = BorderFactory.createTitledBorder("Datos de la competición");
        datosPanel.setBorder(bordeDatosPanel);

        GridBagConstraints constraintsComp = new GridBagConstraints();
        nombreCompeticionLabel = new JLabel("Nombre de la competición: ");
        constraintsComp.insets = new Insets(10, 5, 5, 10);
        constraintsComp.gridx = 0;
        constraintsComp.gridy = 0;
        constraintsComp.gridwidth = 1;
        constraintsComp.fill = GridBagConstraints.NONE;
        constraintsComp.anchor = GridBagConstraints.WEST;
        datosPanel.add(nombreCompeticionLabel, constraintsComp);
        nombreCompeticion = new JLabel("");
        constraintsComp.gridx = 1;
        datosPanel.add(nombreCompeticion, constraintsComp);
        organizadorLabel = new JLabel("Organizador: ");
        constraintsComp.gridx = 0;
        constraintsComp.gridy = 1;

        datosPanel.add(organizadorLabel, constraintsComp);
        organizador = new JLabel("");
        constraintsComp.gridx = 1;
        datosPanel.add(organizador, constraintsComp);

        lugarLabel = new JLabel("Lugar: ");
        constraintsComp.gridx = 0;
        constraintsComp.gridy = 2;
        datosPanel.add(lugarLabel, constraintsComp);
        lugar = new JLabel("");
        constraintsComp.gridx = 1;
        datosPanel.add(lugar, constraintsComp);

        fechaInicioLabel = new JLabel("Fecha de Inicio: ");
        constraintsComp.gridx = 0;
        constraintsComp.gridy = 3;
        datosPanel.add(fechaInicioLabel, constraintsComp);
        fechaInicio = new JLabel("");
        constraintsComp.gridx = 1;
        datosPanel.add(fechaInicio, constraintsComp);
        fechaFinLabel = new JLabel("Fecha de Fin: ");
        constraintsComp.gridx = 0;
        constraintsComp.gridy = 4;
        datosPanel.add(fechaFinLabel, constraintsComp);
        fechaFin = new JLabel("");
        constraintsComp.gridx = 1;
        datosPanel.add(fechaFin, constraintsComp);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.gridheight = 2;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(10, 20, 0, 0);
        this.add(datosPanel, constraints);

        // FORMULARIO CREAR NUEVA PRUEBA
        nombrePruebaLabel = new JLabel("* Nombre:");
        nombrePruebaTextField = new JTextField(20);
        tipoPruebaLabel = new JLabel("* Tipo:");
        tipoPruebaComboBox = new JComboBox(TipoPrueba.values());
        tipoResultadoLabel = new JLabel("* Resultado:");
        tipoResultadoComboBox = new JComboBox(TipoResultado.values());
        
        
        formularioAñadirPruebaPanel = new JPanel(new FlowLayout());
        TitledBorder bordeFormularioPruebaPanel = BorderFactory.createTitledBorder("Formulario Prueba");
        formularioAñadirPruebaPanel.setBorder(bordeFormularioPruebaPanel);
        formularioAñadirPruebaPanel.add(nombrePruebaLabel);
        formularioAñadirPruebaPanel.add(nombrePruebaTextField);
        formularioAñadirPruebaPanel.add(tipoPruebaLabel);
        formularioAñadirPruebaPanel.add(tipoPruebaComboBox);
        formularioAñadirPruebaPanel.add(tipoResultadoLabel);
        formularioAñadirPruebaPanel.add(tipoResultadoComboBox);
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0, 20, 0, 0);
        this.add(formularioAñadirPruebaPanel, constraints);

        // LOGO DE LA COMPETICION
        imagePanel = new JPanel();

        BufferedImage wPic;
        try {
            wPic = ImageIO.read(new File(System.getProperty("user.dir") + "/resources/icons/image_not_found.jpg"));

            wPic = Thumbnails.of(wPic).size(200, 200).asBufferedImage();

            JLabel wIcon = new JLabel(new ImageIcon(wPic));
            imagePanel.add(wIcon);
        } catch (IOException ex) {
            Logger.getLogger(GeneralTab.class.getName()).log(Level.SEVERE, null, ex);
        }

        constraintsComp.gridx = 2;
        constraintsComp.gridy = 0;
        constraintsComp.gridwidth = 2;
        constraintsComp.gridheight = 2;
        constraintsComp.insets = new Insets(20, 0, 0, 20);
        constraintsComp.fill = GridBagConstraints.NONE;
        constraintsComp.anchor = GridBagConstraints.NORTHEAST;

        this.add(imagePanel, constraintsComp);

        botonesPanel = new JPanel(new FlowLayout());

        crearPruebaButton = new JButton("Crear");
        botonesPanel.add(crearPruebaButton);

        modificarPruebaButton = new JButton("Modificar");
        botonesPanel.add(modificarPruebaButton);

        eliminarPruebaButton = new JButton("Eliminar");
        botonesPanel.add(eliminarPruebaButton);

        limpiarPruebaButton = new JButton("Limpiar");
        botonesPanel.add(limpiarPruebaButton);

        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 4;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0, 20, 0, 0);
        this.add(botonesPanel, constraints);
        constraints.insets = new Insets(0, 10, 0, 0);

        listaPruebasLabel = new JLabel("Lista de Pruebas");
        listaPruebasLabel.setFont(new Font("TimesRoman", Font.BOLD, 14));
        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = 4;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(10, 0, 0, 0);
        this.add(listaPruebasLabel, constraints);
        constraints.insets = new Insets(0, 0, 0, 0);

        pruebasTableScrollPane = new JScrollPane();
        modeloPruebasTable = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        pruebasTable = new JTable(modeloPruebasTable);
        pruebasTable.setSelectionMode(SINGLE_SELECTION);
        pruebasTable.setFillsViewportHeight(false);
        pruebasTableScrollPane.setViewportView(pruebasTable);
        pruebasTableScrollPane.setPreferredSize(new Dimension(350, 200));
        modeloPruebasTable.setColumnIdentifiers(new Object[]{"ID", "NOMBRE", "TIPO", "TIPO RESULTADO"});

        pruebasTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if (!lse.getValueIsAdjusting()) {
                    if (getPruebaSelected() != -1) {
                        Coordinador.getInstance().cargarFormularioPrueba(getPruebaSelected());
                    }
                }
            }
        });

        constraints.gridx = 0;
        constraints.gridy = 6;
        constraints.gridwidth = 4;
        constraints.gridheight = 1;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.insets = new Insets(0, 20, 10, 20);
        constraints.fill = GridBagConstraints.BOTH;
        this.add(pruebasTableScrollPane, constraints);
        pruebasTableScrollPane.revalidate();

    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    public void controlador(ActionListener al) {
        
        crearPruebaButton.addActionListener(al);
        crearPruebaButton.setActionCommand(CREARPRUEBA);

        modificarPruebaButton.addActionListener(al);
        modificarPruebaButton.setActionCommand(MODIFICARPRUEBA);
        
        eliminarPruebaButton.addActionListener(al);
        eliminarPruebaButton.setActionCommand(ELIMINARPRUEBA);

        limpiarPruebaButton.addActionListener(al);
        limpiarPruebaButton.setActionCommand(LIMPIARPRUEBA);
        
    }
    
    public JButton getCrearPruebaButton() {
        return crearPruebaButton;
    }

    public JButton getEliminarPruebaButton() {
        return eliminarPruebaButton;
    }

    public JTextField getNombrePruebaTextField() {
        return nombrePruebaTextField;
    }
    
    public String getNombrePrueba(){
        return nombrePruebaTextField.getText().toString();
    }
    
    
    public String getTipoPrueba() {
        return tipoPruebaComboBox.getSelectedItem().toString();
    }
    
    public String getTipoResultado(){
        return tipoResultadoComboBox.getSelectedItem().toString();
    }

    public void setNombrePruebaTextField(JTextField nombrePruebaTextField) {
        this.nombrePruebaTextField = nombrePruebaTextField;
    }
    
    public void añadirPruebaATabla(Object[] o) {
        modeloPruebasTable.addRow(o);
    }

    public void setTipoPruebaComboBox(JComboBox tipoPruebaComboBox) {
        this.tipoPruebaComboBox = tipoPruebaComboBox;
    }

    public JLabel getNombreCompeticion() {
        return nombreCompeticion;
    }

    public void setOrganizador(String organizador) {
        this.organizador.setText(organizador);
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio.setText(fechaInicio);
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin.setText(fechaFin);
    }

    public void setLugar(String lugar) {
        this.lugar.setText(lugar);
    }

    public void setNombreCompeticion(JLabel nombreCompeticion) {
        this.nombreCompeticion = nombreCompeticion;
    }

    public DefaultTableModel getModeloPruebasTable() {
        return modeloPruebasTable;
    }

    public void setModeloPruebasTable(DefaultTableModel modeloPruebasTable) {
        this.modeloPruebasTable = modeloPruebasTable;
    }

    public void cargarListaPruebasExistentes(String[] pruebas) {

        tipoPruebaComboBox.setModel(new DefaultComboBoxModel(pruebas));
    }

    public Integer getPruebaSelected() {
        if (pruebasTable.getSelectedRow() != -1) {
            return (Integer) modeloPruebasTable.getValueAt(pruebasTable.getSelectedRow(), 0);
        } else {
            return -1;
        }

    }
    
    public void limpiarDatosCompeticion(){
        nombreCompeticion.setText("");
        organizador.setText("");
        lugar.setText("");
        fechaFin.setText("");
        fechaInicio.setText("");
        setImagen(System.getProperty("user.dir")+"/resources/icons/image_not_found.jpg");
    }

    public void eliminarPrueba() {
        modeloPruebasTable.removeRow(pruebasTable.getSelectedRow());
    }

    public void setImagen(URL urlImagen) {

        JLabel wIcon = new JLabel(new ImageIcon(urlImagen));
        imagePanel.removeAll();
        imagePanel.add(wIcon);
    }

    public void setImagen(String rutaImagen) {
        BufferedImage wPic;
        try {
            wPic = ImageIO.read(new File(rutaImagen));

            wPic = Thumbnails.of(wPic).size(200, 200).asBufferedImage();

            JLabel wIcon = new JLabel(new ImageIcon(wPic));
            imagePanel.removeAll();
            imagePanel.add(wIcon);
        } catch (IOException ex) {
            setImagen(System.getProperty("user.dir")+"/resources/icons/image_not_found.jpg");
        }
    }

    public JComboBox getPruebasComboBox() {
        return pruebasComboBox;
    }

    public void setPruebasComboBox(JComboBox pruebasComboBox) {
        this.pruebasComboBox = pruebasComboBox;
    }

    public JButton getLimpiarPruebaButton() {
        return limpiarPruebaButton;
    }

    public JButton getModificarPruebaButton() {
        return modificarPruebaButton;
    }

    public void limpiarFormularioPrueba() {
        this.nombrePruebaTextField.setText("");
        this.pruebasTable.clearSelection();
    }

    public void setNombrePrueba(String nombre) {
        this.nombrePruebaTextField.setText(nombre);
    }

    public void setTipoPrueba(String tipo) {
        this.tipoPruebaComboBox.setSelectedItem(TipoPrueba.valueOf(tipo));
    }
    
    public void setTipoResultado(String resultado){
        this.tipoResultadoComboBox.setSelectedItem(TipoResultado.valueOf(resultado));
    }

}
