package vista;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import modelo.Competicion;
import javax.swing.filechooser.FileFilter;
/**
 *
 * @author JuanM
 */
public class DialogoCrearCompeticion extends JDialog implements VistaCrearCompeticion {

    private final JLabel nombreLabel;
    private final JTextField nombreTextField;
    private final JButton okButton;
    private final JButton cancelarButton;
    private final JLabel estadoLabel;
    private final JPanel formularioCompeticionPanel;
    private final JLabel organizadorLabel;
    private final JTextField organizadorTextField;
    private final JLabel logoLabel;
    private final JTextField logoTextField;
    private final JButton logoButton;
    private final JLabel lugarLabel;
    private final JTextField lugarTextField;
    private final JLabel fechaInicioLabel;
    private final JLabel fechaFinLabel;
    private final JLabel diaInicioLabel;
    private final JLabel diaFinLabel;
    private final JComboBox diaInicioComboBox;
    private final JComboBox diaFinComboBox;
    private final JLabel mesInicioLabel;
    private final JLabel mesFinLabel;
    private final JComboBox mesInicioComboBox;
    private final JComboBox mesFinComboBox;
    private final JLabel añoInicioLabel;
    private final JLabel añoFinLabel;
    private final JTextField añoInicioTextField;
    private final JTextField añoFinTextField;
    private final JPanel fechaInicioPanel;
    private final JPanel fechaFinPanel;

    public DialogoCrearCompeticion(String titulo, java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setTitle(titulo);
        this.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        formularioCompeticionPanel = new JPanel(new GridBagLayout());
        TitledBorder bordeFormularioCompeticion = BorderFactory.createTitledBorder("Datos de la competición");
        formularioCompeticionPanel.setBorder(bordeFormularioCompeticion);

        // NOMBRE
        nombreLabel = new JLabel("* Nombre:");
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(10, 10, 10, 10);
        formularioCompeticionPanel.add(nombreLabel, constraints);

        nombreTextField = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        formularioCompeticionPanel.add(nombreTextField, constraints);

        // ORGANIZADOR
        organizadorLabel = new JLabel("  Organizador: ");
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.NONE;
        formularioCompeticionPanel.add(organizadorLabel, constraints);

        organizadorTextField = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        formularioCompeticionPanel.add(organizadorTextField, constraints);

        // LOGO
        logoLabel = new JLabel("  Logo: ");
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.fill = GridBagConstraints.NONE;
        formularioCompeticionPanel.add(logoLabel, constraints);

        logoTextField = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        formularioCompeticionPanel.add(logoTextField, constraints);

        logoButton = new JButton("Buscar");
        logoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fc.setDialogTitle("Guardar en");
                FileFilter imageFilter = new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return file.isDirectory() || file.getName().endsWith(".jpg") || file.getName().endsWith(".png");
                    }

                    @Override
                    public String getDescription() {
                        return "Images Files";
                    }
                };
                
                fc.addChoosableFileFilter(imageFilter);
                fc.setAcceptAllFileFilterUsed(false);
                fc.setFileFilter(imageFilter);
                int res = fc.showOpenDialog(null);
                if (res == JFileChooser.APPROVE_OPTION) {
                    logoTextField.setText(fc.getSelectedFile().getPath());
                }
            }
        });
        constraints.gridx = 2;
        constraints.gridy = 2;
        constraints.fill = GridBagConstraints.NONE;
        formularioCompeticionPanel.add(logoButton, constraints);

        // LUGAR
        lugarLabel = new JLabel("  Lugar:");
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.fill = GridBagConstraints.NONE;
        formularioCompeticionPanel.add(lugarLabel, constraints);

        lugarTextField = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 3;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        formularioCompeticionPanel.add(lugarTextField, constraints);

        // FECHA DE INICIO
        fechaInicioLabel = new JLabel("  Fecha de Inicio: ");
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.fill = GridBagConstraints.NONE;
        formularioCompeticionPanel.add(fechaInicioLabel, constraints);

        fechaInicioPanel = new JPanel(new FlowLayout());

        diaInicioLabel = new JLabel("Día: ");
        fechaInicioPanel.add(diaInicioLabel);
        diaInicioComboBox = new JComboBox();
        diaInicioComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
            "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"}));
        fechaInicioPanel.add(diaInicioComboBox);

        mesInicioLabel = new JLabel("Mes: ");
        fechaInicioPanel.add(mesInicioLabel);
        mesInicioComboBox = new JComboBox();
        mesInicioComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio",
            "Agosto", "Octubre", "Noviembre", "Diciembre"}));
        fechaInicioPanel.add(mesInicioComboBox);

        añoInicioLabel = new JLabel("Año: ");
        fechaInicioPanel.add(añoInicioLabel);
        añoInicioTextField = new JTextField(20);
        añoInicioTextField.setColumns(4);
        fechaInicioPanel.add(añoInicioTextField);

        constraints.gridx = 1;
        constraints.gridy = 4;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.NONE;
        formularioCompeticionPanel.add(fechaInicioPanel, constraints);
        constraints.gridwidth = 1;

        // FECHA DE FIN
        fechaFinLabel = new JLabel("  Fecha de Fin: ");
        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.fill = GridBagConstraints.NONE;
        formularioCompeticionPanel.add(fechaFinLabel, constraints);

        fechaFinPanel = new JPanel(new FlowLayout());

        diaFinLabel = new JLabel("Día: ");
        fechaFinPanel.add(diaFinLabel);
        diaFinComboBox = new JComboBox();
        diaFinComboBox.setModel(new javax.swing.DefaultComboBoxModel( new String[]{
             "", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
            "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"}));
        fechaFinPanel.add(diaFinComboBox);

        mesFinLabel = new JLabel("Mes: ");
        fechaFinPanel.add(mesFinLabel);
        mesFinComboBox = new JComboBox();
        mesFinComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio",
            "Agosto", "Octubre", "Noviembre", "Diciembre"}));
        fechaFinPanel.add(mesFinComboBox);

        añoFinLabel = new JLabel("Año: ");
        fechaFinPanel.add(añoFinLabel);
        añoFinTextField = new JTextField(20);
        añoFinTextField.setColumns(4);
        fechaFinPanel.add(añoFinTextField);

        constraints.gridx = 1;
        constraints.gridy = 5;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.NONE;
        formularioCompeticionPanel.add(fechaFinPanel, constraints);
        constraints.gridwidth = 1;

        // GUARDAR Y CANCELAR
        okButton = new JButton("Guardar");
        constraints.gridx = 0;
        constraints.gridy = 6;
        constraints.fill = GridBagConstraints.NONE;
        formularioCompeticionPanel.add(okButton, constraints);

        cancelarButton = new JButton("Cancelar");
        constraints.gridx = 1;
        constraints.gridy = 6;
        formularioCompeticionPanel.add(cancelarButton, constraints);

        estadoLabel = new JLabel("");
        constraints.gridx = 2;
        constraints.gridy = 6;
        constraints.gridwidth = 1;
        formularioCompeticionPanel.add(estadoLabel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 3;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(10, 10, 10, 10);
        this.add(formularioCompeticionPanel, constraints);
        constraints.gridwidth = 1;

    }

 
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void controlador(ActionListener al) {
        okButton.addActionListener(al);
        okButton.setActionCommand(VistaCrearCompeticion.OK);

        cancelarButton.addActionListener(al);
        cancelarButton.setActionCommand(VistaCrearCompeticion.CANCELAR);
    }

    @Override
    public void cerrar() {
        this.dispose();
    }

    @Override
    public String getNombre() {
        return nombreTextField.getText();
    }

    @Override
    public void estado(String estado, Color color) {
        estadoLabel.setText(estado);
        estadoLabel.setForeground(color);
    }

    @Override
    public void cargarDatosCompeticion(Competicion c) {
        nombreTextField.setText(c.getNombre());
        organizadorTextField.setText(c.getOrganizador());
        lugarTextField.setText(c.getCiudad());
        logoTextField.setText(c.getImagen());
        if (c.getFechainicio() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
            diaInicioComboBox.setSelectedIndex(Integer.valueOf(dateFormat.format(c.getFechainicio())));
            dateFormat.applyLocalizedPattern("MM");
            mesInicioComboBox.setSelectedIndex(Integer.valueOf(dateFormat.format(c.getFechainicio())));
            dateFormat.applyLocalizedPattern("yyyy");
            añoInicioTextField.setText(dateFormat.format(c.getFechainicio()));
        }
        if (c.getFechafin() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
            diaFinComboBox.setSelectedIndex(Integer.valueOf(dateFormat.format(c.getFechafin())));
            dateFormat.applyLocalizedPattern("MM");
            mesFinComboBox.setSelectedIndex(Integer.valueOf(dateFormat.format(c.getFechafin())));
            dateFormat.applyLocalizedPattern("yyyy");
            añoFinTextField.setText(dateFormat.format(c.getFechafin()));
        }

    }

    @Override
    public String getOrganizador() {
        return organizadorTextField.getText().toString();
    }

    @Override
    public String getLugar() {
        return lugarTextField.getText().toString();
    }

    @Override
    public String getAñoInicio() {
        return añoInicioTextField.getText().toString();
    }

    @Override
    public Integer getMesInicio() {
        return mesInicioComboBox.getSelectedIndex();
    }

    @Override
    public String getDiaInicio() {
        return diaInicioComboBox.getSelectedItem().toString();
    }

    @Override
    public String getAñoFin() {
        return añoFinTextField.getText().toString();
    }

    @Override
    public Integer getMesFin() {
        return mesFinComboBox.getSelectedIndex();
    }

    @Override
    public String getDiaFin() {
        return diaFinComboBox.getSelectedItem().toString();
    }
    
    @Override
    public String getRutaImagen(){
        return logoTextField.getText().toString();
    }

}
