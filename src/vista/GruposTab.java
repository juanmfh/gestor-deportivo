package vista;

import vista.interfaces.VistaGrupos;
import controlador.Coordinador;
import modelo.entities.RolUsuario;
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
public class GruposTab extends javax.swing.JPanel implements VistaGrupos {

    private JLabel nombreGrupoLabel;
    private JTextField nombreGrupoTextField;
    private JLabel subgrupoDeLabel;
    private JComboBox subgrupoDeComboBox;
    private JButton crearGrupoButton;
    private JButton modificarGrupoButton;
    private JButton eliminarGrupoButton;
    private ComboBoxModel subgrupoDeModel;
    private final JTable grupostable;
    private final DefaultTableModel modeloGruposTable;
    private final JScrollPane gruposTableScrollPane;
    private JPanel formularioGrupoPanel;
    private JLabel listaGruposLabel;
    private JButton limpiarButton;
    
    public GruposTab() {

        this.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        formularioGrupoPanel = new JPanel(new GridBagLayout());
        TitledBorder bordeFormularioGrupoPanel = BorderFactory.createTitledBorder("Formulario Grupo");
        formularioGrupoPanel.setBorder(bordeFormularioGrupoPanel);
        
        nombreGrupoLabel = new JLabel("* Nombre del grupo:");
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(10,10,10,10);
        formularioGrupoPanel.add(nombreGrupoLabel, constraints);

        nombreGrupoTextField = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        formularioGrupoPanel.add(nombreGrupoTextField, constraints);

        subgrupoDeLabel = new JLabel("* Subgrupo de: ");
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        formularioGrupoPanel.add(subgrupoDeLabel, constraints);

        subgrupoDeComboBox = new JComboBox();
        subgrupoDeModel = new DefaultComboBoxModel();
        subgrupoDeComboBox.setModel(subgrupoDeModel);
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        formularioGrupoPanel.add(subgrupoDeComboBox, constraints);
        
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 4;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(10,20,0,0);
        this.add(formularioGrupoPanel, constraints);
        constraints.insets = new Insets(0,0,0,0);
        
        crearGrupoButton = new JButton("Crear");
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0,20,0,0);
        this.add(crearGrupoButton, constraints);

        modificarGrupoButton = new JButton("Modificar");
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0,10,0,0);
        this.add(modificarGrupoButton, constraints);

        eliminarGrupoButton = new JButton("Eliminar");
        constraints.gridx = 2;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0,10,0,0);
        this.add(eliminarGrupoButton, constraints);
        
        limpiarButton = new JButton("Limpiar");
        constraints.gridx = 3;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0,10,0,0);
        this.add(limpiarButton, constraints);
        
        listaGruposLabel = new JLabel("Lista de Grupos");
        listaGruposLabel.setFont(new Font("TimesRoman", Font.BOLD, 14));
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 4;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(10,0,0,0);
        this.add(listaGruposLabel, constraints);
        constraints.insets = new Insets(0,0,0,0);

        gruposTableScrollPane = new JScrollPane();
        modeloGruposTable = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        grupostable = new JTable(modeloGruposTable);
        grupostable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        grupostable.setFillsViewportHeight(false);
        gruposTableScrollPane.setViewportView(grupostable);
        
        grupostable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if (!lse.getValueIsAdjusting()) {
                    if (getGrupoSelected()!= -1) {
                        Coordinador.getInstance().cargarFormularioGrupo(getGrupoSelected());
                    }
                }
            }
        });
        
        modeloGruposTable.setColumnIdentifiers(new Object[]{"ID", "NOMBRE", "SUBGRUPO DE"});
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 4;
        constraints.gridheight = 1;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(0,20,10,20);
        this.add(gruposTableScrollPane, constraints);
        gruposTableScrollPane.revalidate();

        this.habilitarBotones(RolUsuario.values()[Coordinador.getInstance().getUsuario().getRol()]);
    }

    @Override
    public void controlador(ActionListener al) {

        crearGrupoButton.addActionListener(al);
        crearGrupoButton.setActionCommand(CREARGRUPO);

        modificarGrupoButton.addActionListener(al);
        modificarGrupoButton.setActionCommand(MODIFICARGRUPO);

        eliminarGrupoButton.addActionListener(al);
        eliminarGrupoButton.setActionCommand(ELIMINARGRUPO);
        
        limpiarButton.addActionListener(al);
        limpiarButton.setActionCommand(LIMPIAR);
    }

    @Override
    public JComboBox getSubgrupoDeComboBox() {
        return subgrupoDeComboBox;
    }

    @Override
    public String getNombreGrupo() {
        return nombreGrupoTextField.getText().toString();
    }

    @Override
    public void añadirGrupoATabla(Object[] o) {
        this.modeloGruposTable.addRow(o);
    }

    @Override
    public DefaultTableModel getModeloGruposTable() {
        return modeloGruposTable;
    }

    @Override
    public Integer getGrupoSelected() {
        try {
            return (Integer) modeloGruposTable.getValueAt(grupostable.getSelectedRow(), 0);
        } catch (ArrayIndexOutOfBoundsException e) {
            return -1;
        }
    }
    
    @Override
    public void limpiarFormularioGrupo(){
        this.nombreGrupoTextField.setText("");
        this.subgrupoDeComboBox.setSelectedIndex(0);
    }
    @Override
    public void setNombreGrupo(String nombre){
        this.nombreGrupoTextField.setText(nombre);
    }
    
    @Override
    public void setSubGrupoDe(String nombre){
        this.subgrupoDeComboBox.setSelectedItem(nombre);
    }
    
    private void habilitarBotones(RolUsuario rol){
        
        if(rol.equals(RolUsuario.Invitado)){
            crearGrupoButton.setEnabled(false);
            modificarGrupoButton.setEnabled(false);
            eliminarGrupoButton.setEnabled(false);
            limpiarButton.setEnabled(false);
        }
        
    }
}
