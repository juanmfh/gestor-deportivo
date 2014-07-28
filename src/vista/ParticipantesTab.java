
package vista;

import controlador.Coordinador;
import modelo.RolUsuario;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author JuanM
 */
public class ParticipantesTab extends javax.swing.JPanel implements VistaParticipantes {

    private JLabel nombreLabel;
    private JTextField nombreTextField;
    private JLabel edadLabel;
    private JTextField edadTextField;
    private JLabel apellidosLabel;
    private JTextField apellidosTextField;
    private JLabel sexoLabel;
    private JRadioButton masculinoRadioButton;
    private JRadioButton femeninoRadioButton;
    private JLabel dorsalLabel;
    private JTextField dorsalTextField;
    private ButtonGroup sexoButtonGroup;
    private JPanel sexoRadioButtonPanel;
    private JLabel grupoLabel;
    private JComboBox grupoComboBox;
    private JLabel equipoLabel;
    private JComboBox equipoComboBox;
    private DefaultComboBoxModel modelGrupoComboBox;
    private final DefaultComboBoxModel modelEquipoComboBox;
    private final JButton crearParticipanteButton;
    private final JButton modificarParticipanteButton;
    private final JButton eliminarParticipanteButton;
    private final JTable participantesTable;
    private final DefaultTableModel modeloParticipantesTable;
    private final JScrollPane participantesScrollPane;
    private final JButton limpiarButton;
    private final JPanel formularioParticipantePanel;
    private final JLabel listaParticipantesLabel;
    private JButton importarButton;
    private final JLabel pruebaAsignadaLabel;
    private final JComboBox pruebaAsignadaComboBox;
    private final DefaultComboBoxModel pruebaAsignadaModel;

    public ParticipantesTab() {
        initComponents();

        this.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        importarButton = new JButton("Importar desde Excel");
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(10, 20, 0, 0);
        constraints.anchor = GridBagConstraints.WEST;
        this.add(importarButton, constraints);

        formularioParticipantePanel = new JPanel(new GridBagLayout());
        TitledBorder bordeFormularioParticipantePanel = BorderFactory.createTitledBorder("Formulario Participante");
        formularioParticipantePanel.setBorder(bordeFormularioParticipantePanel);

        nombreLabel = new JLabel("* Nombre:");
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(0, 5, 0, 0);
        constraints.anchor = GridBagConstraints.WEST;
        formularioParticipantePanel.add(nombreLabel, constraints);

        apellidosLabel = new JLabel("* Apellidos:");
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        formularioParticipantePanel.add(apellidosLabel, constraints);

        nombreTextField = new JTextField(20);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 5, 5, 5);
        formularioParticipantePanel.add(nombreTextField, constraints);

        apellidosTextField = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        formularioParticipantePanel.add(apellidosTextField, constraints);

        edadLabel = new JLabel("Edad:");
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0, 5, 0, 0);
        formularioParticipantePanel.add(edadLabel, constraints);

        sexoLabel = new JLabel("Sexo:");
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        formularioParticipantePanel.add(sexoLabel, constraints);

        edadTextField = new JTextField(20);
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 5, 5, 5);
        formularioParticipantePanel.add(edadTextField, constraints);

        sexoRadioButtonPanel = new JPanel(new FlowLayout());
        sexoButtonGroup = new ButtonGroup();
        masculinoRadioButton = new JRadioButton("masculino");
        femeninoRadioButton = new JRadioButton("femenino");
        sexoButtonGroup.add(masculinoRadioButton);
        sexoButtonGroup.add(femeninoRadioButton);
        sexoRadioButtonPanel.add(masculinoRadioButton);
        sexoRadioButtonPanel.add(femeninoRadioButton);
        constraints.gridx = 1;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        formularioParticipantePanel.add(sexoRadioButtonPanel, constraints);

        grupoLabel = new JLabel("* Grupo:");
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0, 5, 0, 0);
        formularioParticipantePanel.add(grupoLabel, constraints);

        grupoComboBox = new JComboBox();
        modelGrupoComboBox = new DefaultComboBoxModel();
        grupoComboBox.setModel(modelGrupoComboBox);
        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 5, 5, 5);
        formularioParticipantePanel.add(grupoComboBox, constraints);

        equipoLabel = new JLabel("Equipo:");
        constraints.gridx = 1;
        constraints.gridy = 4;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0, 5, 0, 0);
        formularioParticipantePanel.add(equipoLabel, constraints);

        equipoComboBox = new JComboBox();
        modelEquipoComboBox = new DefaultComboBoxModel();
        equipoComboBox.setModel(modelEquipoComboBox);
        constraints.gridx = 1;
        constraints.gridy = 5;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 5, 5, 5);
        formularioParticipantePanel.add(equipoComboBox, constraints);

        dorsalLabel = new JLabel("* Dorsal:");
        constraints.gridx = 1;
        constraints.gridy = 6;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0, 5, 0, 0);
        formularioParticipantePanel.add(dorsalLabel, constraints);

        dorsalTextField = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 7;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 5, 5, 5);
        formularioParticipantePanel.add(dorsalTextField, constraints);
        
        pruebaAsignadaLabel = new JLabel("Prueba asignada: ");
        constraints.gridx = 0;
        constraints.gridy = 6;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0, 5, 0, 0);
        formularioParticipantePanel.add(pruebaAsignadaLabel, constraints);
        
        pruebaAsignadaComboBox = new JComboBox();
        pruebaAsignadaModel = new DefaultComboBoxModel();
        pruebaAsignadaComboBox.setModel(pruebaAsignadaModel);
        constraints.gridx = 0;
        constraints.gridy = 7;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 5, 5, 5);
        formularioParticipantePanel.add(pruebaAsignadaComboBox, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 4;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(10, 20, 0, 0);
        this.add(formularioParticipantePanel, constraints);
        constraints.insets = new Insets(0, 0, 0, 0);
        
        //BOTONERA
        
        JPanel botoneraPanel = new JPanel(new FlowLayout()); 
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 4;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0, 20, 0, 0);
        this.add(botoneraPanel, constraints);
        
        crearParticipanteButton = new JButton("Crear");
        botoneraPanel.add(crearParticipanteButton);

        modificarParticipanteButton = new JButton("Modificar");
        botoneraPanel.add(modificarParticipanteButton);

        eliminarParticipanteButton = new JButton("Eliminar");
        botoneraPanel.add(eliminarParticipanteButton);

        limpiarButton = new JButton("Limpiar");
        botoneraPanel.add(limpiarButton);

        listaParticipantesLabel = new JLabel("Lista de Participantes");
        listaParticipantesLabel.setFont(new Font("TimesRoman", Font.BOLD, 14));
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 4;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(10, 0, 0, 0);
        this.add(listaParticipantesLabel, constraints);
        constraints.insets = new Insets(0, 0, 0, 0);

        participantesScrollPane = new JScrollPane();
        modeloParticipantesTable = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        participantesTable = new JTable(modeloParticipantesTable);
        participantesTable.setAutoCreateRowSorter(true);
        participantesTable.setSelectionMode(SINGLE_SELECTION);
        participantesTable.setFillsViewportHeight(false);
        participantesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if (!lse.getValueIsAdjusting()) {
                    if (getParticipanteSeleccionado() != -1) {
                        Coordinador.getInstance().cargarFormularioParticipante(getParticipanteSeleccionado());
                    }
                }
            }
        });

        participantesScrollPane.setViewportView(participantesTable);
        participantesScrollPane.setPreferredSize(new Dimension(350, 200));
        modeloParticipantesTable.setColumnIdentifiers(new Object[]{"ID", "DORSAL", "APELLIDOS", "NOMBRE", "GRUPO", "EQUIPO","PRUEBA ASIG."});
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 4;
        constraints.gridheight = 1;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(0, 20, 10, 20);
        this.add(participantesScrollPane, constraints);

        grupoComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (getGrupoParticipante() != null) {
                    Coordinador.getInstance().getControladorPrincipal().cargarEquipoEnParticipantes(getGrupoParticipante());

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
    public JComboBox getGrupoComboBox() {
        return grupoComboBox;
    }

    @Override
    public JComboBox getEquipoComboBox() {
        return equipoComboBox;
    }
    
    @Override
    public JComboBox getPruebaComboBox(){
        return pruebaAsignadaComboBox;
    }

    @Override
    public void controlador(ActionListener al) {
        crearParticipanteButton.addActionListener(al);
        modificarParticipanteButton.addActionListener(al);
        eliminarParticipanteButton.addActionListener(al);
        limpiarButton.addActionListener(al);
        importarButton.addActionListener(al);
        crearParticipanteButton.setActionCommand(CREARPARTICIPANTE);
        modificarParticipanteButton.setActionCommand(MODIFICARPARTICIPANTE);
        eliminarParticipanteButton.setActionCommand(ELIMINARPARTICIPANTE);
        limpiarButton.setActionCommand(LIMPIARPARTICIPANTE);
        importarButton.setActionCommand(IMPORTAR);
    }

    @Override
    public String getNombreParticipante() {
        return nombreTextField.getText().toString();
    }

    @Override
    public String getApellidosParticipante() {
        return apellidosTextField.getText().toString();
    }

    @Override
    public Integer getEdadParticipante() {
        try {

            return Integer.parseInt(edadTextField.getText().toString());
        } catch (NumberFormatException e) {
            return null;
        }

    }

    @Override
    public Integer getSexoParticipante() {
        return masculinoRadioButton.isSelected() ? 0 : (femeninoRadioButton.isSelected() ? 1 : -1);
    }

    @Override
    public String getGrupoParticipante() {
        try {
            return grupoComboBox.getSelectedItem().toString();
        } catch (NullPointerException e) {
            return null;
        }

    }

    @Override
    public String getEquipoParticipante() {
        try {
            return equipoComboBox.getSelectedItem().toString();
        } catch (NullPointerException e) {
            return null;
        }
    }
    
    @Override
    public String getPruebaAsignadaParticipante() {
        try {
            return pruebaAsignadaComboBox.getSelectedItem().toString();
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public Integer getDorsalParticipante() {
        Integer res;
        try {
            res = Integer.parseInt(dorsalTextField.getText().toString());
        } catch (java.lang.NumberFormatException e) {
            return null;
        }
        return res;
    }

    @Override
    public DefaultTableModel getModeloParticipantesTable() {
        return modeloParticipantesTable;
    }

    @Override
    public void añadirParticipanteATabla(Object[] o) {
        this.modeloParticipantesTable.addRow(o);
    }

    @Override
    public Integer getParticipanteSeleccionado() {
        try {
            //System.out.println("Fila seleccionada: " + (Integer) modeloParticipantesTable.getValueAt(participantesTable.getSelectedRow(), 0));
            return (Integer) modeloParticipantesTable.getValueAt(participantesTable.convertRowIndexToModel(participantesTable.getSelectedRow()), 0);
            //return (Integer) modeloParticipantesTable.getValueAt(participantesTable.getSelectedRow(), 0);
        } catch (ArrayIndexOutOfBoundsException e) {
            return -1;
        } catch (IndexOutOfBoundsException e) {
            return -1;
        }
    }

    @Override
    public void clearSelectionParticipante() {

        participantesTable.clearSelection();
    }

    @Override
    public void eliminarParticipanteDeTabla() {
        modeloParticipantesTable.removeRow(participantesTable.convertRowIndexToModel(participantesTable.getSelectedRow()));
    }

    @Override
    public void limpiarFormularioParticipante() {
        this.nombreTextField.setText("");
        this.apellidosTextField.setText("");
        this.edadTextField.setText("");
        this.dorsalTextField.setText("");
        sexoButtonGroup.clearSelection();
        this.equipoComboBox.setSelectedIndex(0);
        participantesTable.clearSelection();
        pruebaAsignadaComboBox.setSelectedIndex(0);
    }

    @Override
    public void setNombreParticipante(String nombre) {
        this.nombreTextField.setText(nombre);
    }

    @Override
    public void setApellidosParticipante(String apellidos) {
        this.apellidosTextField.setText(apellidos);
    }

    @Override
    public void setEdadParticipante(Integer edad) {
        if (edad != null) {
            this.edadTextField.setText(edad.toString());
        } else {
            this.edadTextField.setText("");
        }
    }

    @Override
    public void setDorsalParticipante(Integer dorsal) {
        this.dorsalTextField.setText(dorsal.toString());
    }

    @Override
    public void setGrupoParticipante(String grupo) {
        this.grupoComboBox.setSelectedItem(grupo);
    }

    @Override
    public void setEquipoParticipante(String equipo) {
        this.equipoComboBox.setSelectedItem(equipo);
    }
        
    @Override
    public void setPruebaAsignadaParticipante(String prueba){
        this.pruebaAsignadaComboBox.setSelectedItem(prueba);
    }

    @Override
    public void setSexoParticipante(Integer sexo) {
        if (sexo != null) {
            if (sexo == 0) {
                this.masculinoRadioButton.setSelected(true);
            } else if (sexo == 1) {
                this.femeninoRadioButton.setSelected(true);
            }
        } else {
            sexoButtonGroup.clearSelection();
        }

    }
    
     private void habilitarBotones(RolUsuario rol){
        
        if(rol.equals(RolUsuario.Invitado)){
            crearParticipanteButton.setEnabled(false);
            modificarParticipanteButton.setEnabled(false);
            eliminarParticipanteButton.setEnabled(false);
            limpiarButton.setEnabled(false);
        }
        
    }

}
