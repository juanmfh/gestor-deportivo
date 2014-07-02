
package vista;

import controlador.Coordinador;
import controlador.RolUsuario;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author JuanM
 */
public class EquiposTab extends javax.swing.JPanel implements VistaEquipos {

    private final JPanel formularioEquipoPanel;
    private final JLabel nombreEquipoLabel;
    private final JTextField nombreEquipoTextField;
    private final JButton crearEquipoButton;
    private final JScrollPane tablaEquiposScrollPane;
    private final DefaultTableModel modeloEquiposTable;
    private final JTable equiposTable;
    private final JButton eliminarEquipoButton;
    private final JLabel grupoLabel;
    private final JComboBox gruposComboBox;
    private ComboBoxModel gruposModel;
    private JButton modificarEquipoButton;
    private JButton limpiarButton;
    private JLabel listaEquiposLabel;

    public EquiposTab() {
        initComponents();

        this.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        formularioEquipoPanel = new JPanel(new GridBagLayout());
        TitledBorder bordeFormularioEquipo = BorderFactory.createTitledBorder("Nuevo Equipo");
        formularioEquipoPanel.setBorder(bordeFormularioEquipo);

        nombreEquipoLabel = new JLabel("* Nombre:");
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(10,10,10,10);
        formularioEquipoPanel.add(nombreEquipoLabel, constraints);
        
        nombreEquipoTextField = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        formularioEquipoPanel.add(nombreEquipoTextField, constraints);
        
        grupoLabel = new JLabel("* Grupo:");
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.NONE;
        formularioEquipoPanel.add(grupoLabel, constraints);
        
        gruposComboBox = new JComboBox();
        gruposModel = new DefaultComboBoxModel();
        gruposComboBox.setModel(gruposModel);
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        formularioEquipoPanel.add(gruposComboBox, constraints);     
        
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 4;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(10,20,0,0);
        this.add(formularioEquipoPanel, constraints);
        constraints.insets = new Insets(0,0,0,0);
        
        crearEquipoButton = new JButton("Crear");
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0,20,0,0);
        this.add(crearEquipoButton, constraints);
        
        modificarEquipoButton = new JButton("Modificar");
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0,10,0,0);
        this.add(modificarEquipoButton, constraints);
        
        eliminarEquipoButton = new JButton("Eliminar");
        constraints.gridx = 2;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0,10,0,0);
        this.add(eliminarEquipoButton, constraints);
        
        limpiarButton = new JButton("Limpiar");
        constraints.gridx = 3;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0,10,0,0);
        this.add(limpiarButton, constraints);
        
        listaEquiposLabel = new JLabel("Lista de Equipos");
        listaEquiposLabel.setFont(new Font("TimesRoman", Font.BOLD, 14));
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 4;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(10,0,0,0);
        this.add(listaEquiposLabel, constraints);
        constraints.insets = new Insets(0,0,0,0);

        tablaEquiposScrollPane = new JScrollPane();
        modeloEquiposTable = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        equiposTable = new JTable(modeloEquiposTable);
        equiposTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        equiposTable.setFillsViewportHeight(false);
        tablaEquiposScrollPane.setViewportView(equiposTable);
        modeloEquiposTable.setColumnIdentifiers(new Object[]{"ID", "NOMBRE", "GRUPO", "NUMERO DE PARTICIPANTES"});
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 4;
        constraints.gridheight = 1;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.insets = new Insets(0,20,10,20);
        constraints.fill = GridBagConstraints.BOTH;
        this.add(tablaEquiposScrollPane, constraints);
        equiposTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if (!lse.getValueIsAdjusting()) {
                    if (getEquipoSelected()!= -1) {
                        Coordinador.getInstance().cargarFormularioEquipo(getEquipoSelected());
                    }
                }
            }
        });
        
        this.habilitarBotones(RolUsuario.values()[Coordinador.getInstance().getUsuario().getRol()]);
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
    @Override
    public void controlador(ActionListener al) {
        crearEquipoButton.addActionListener(al);
        crearEquipoButton.setActionCommand(VistaEquipos.AÑADIREQUIPO);
        
        modificarEquipoButton.addActionListener(al);
        modificarEquipoButton.setActionCommand(VistaEquipos.MODIFICAREQUIPO);
        
        eliminarEquipoButton.addActionListener(al);
        eliminarEquipoButton.setActionCommand(VistaEquipos.ELIMINAREQUIPO);
        
        limpiarButton.addActionListener(al);
        limpiarButton.setActionCommand(VistaEquipos.LIMPIAR); 
    }
    
    
    
    @Override
    public String getNombreEquipo(){
        return nombreEquipoTextField.getText().toString();
    }
    
    @Override
    public void añadirEquipoATabla(Object[] o) {
        this.modeloEquiposTable.addRow(o);
    }
    
    @Override
    public DefaultTableModel getModeloEquiposTable(){
        return modeloEquiposTable;
    }

    @Override
    public Integer getEquipoSelected() {
        try {
            return (Integer) modeloEquiposTable.getValueAt(equiposTable.getSelectedRow(), 0);
        } catch (ArrayIndexOutOfBoundsException e) {
            return -1;
        }
    }

    @Override
    public JComboBox getGruposComboBox() {
        return gruposComboBox;
        
    }
    
    @Override
    public String getGrupo(){
        try {
            return gruposComboBox.getSelectedItem().toString(); 
        } catch (NullPointerException e) {
            return null;
        }
        
    
    }

    @Override
    public void eliminarEquipoSeleccionado() {
        modeloEquiposTable.removeRow(equiposTable.getSelectedRow());
    }
    
    @Override
    public void limpiarFormularioEquipo(){
        this.nombreEquipoTextField.setText("");
        this.gruposComboBox.setSelectedIndex(-1);
    }
    
    @Override
    public void setNombreEquipo(String nombre){
        this.nombreEquipoTextField.setText(nombre);
    }
    
    @Override
    public void setGrupoDelEquipo(String grupo){
        this.gruposComboBox.setSelectedItem(grupo);
    }
    
    @Override
    public void habilitarBotones(RolUsuario rol){
        if(rol.equals(RolUsuario.Invitado)){
            crearEquipoButton.setEnabled(false);
            modificarEquipoButton.setEnabled(false);
            eliminarEquipoButton.setEnabled(false);
            limpiarButton.setEnabled(false);
        }
    }
}
