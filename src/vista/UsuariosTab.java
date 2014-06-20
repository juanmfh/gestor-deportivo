package vista;

import controlador.Coordinador;
import controlador.RolUsuario;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
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
public class UsuariosTab extends JPanel implements VistaUsuarios {

    private final JPanel formularioUsuariosPanel;
    private final JLabel nombreUsuarioLabel;
    private final JTextField nombreUsuarioTextField;
    private final JLabel contraseñaLabel;
    private final JTextField contraseñaTextField;
    private final JLabel rolLabel;
    private final JComboBox rolComboBox;
    private final JButton crearUsuarioButton;
    private final JButton modificarUsuarioButton;
    private final JButton eliminarUsuarioButton;
    private final JButton limpiarFormularioUsuarioButton;
    private final JLabel listaUsuariosLabel;
    private final JScrollPane usuariosScrollPane;
    private final DefaultTableModel modeloUsuariosTable;
    private final JTable usuariosTable;
    private final JLabel competicionesListLabel;
    private final DefaultListModel modeloListaCompeticiones;
    private final JList competicionesList;
    private final JScrollPane listaCompeticionesScrollPane;
    private final JPanel botonesListaCompeticionsePanel;
    private final JButton incluirCompeticionButton;
    private final JButton excluirCompeticionButton;
    private final JLabel competicionesConAccesoListLabel;
    private final DefaultListModel modeloListaCompeticionesConAcceso;
    private final JList competicionesConAccesoList;
    private final JScrollPane listaCompeticionesConAccesoScrollPane;
    
    public UsuariosTab(){
        
        this.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        
        formularioUsuariosPanel = new JPanel(new GridBagLayout());
        TitledBorder bordeFormularioUsuariosPanel = BorderFactory.createTitledBorder("Formulario de Usuarios");
        formularioUsuariosPanel.setBorder(bordeFormularioUsuariosPanel);
        
        nombreUsuarioLabel = new JLabel("* Nombre de usuario:");
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(0, 5, 0, 0);
        constraints.anchor = GridBagConstraints.WEST;
        formularioUsuariosPanel.add(nombreUsuarioLabel, constraints);
        
        nombreUsuarioTextField = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 5, 5, 5);
        formularioUsuariosPanel.add(nombreUsuarioTextField, constraints);
        
        contraseñaLabel = new JLabel("* Contraseña:");
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(0, 5, 0, 0);
        constraints.anchor = GridBagConstraints.WEST;
        formularioUsuariosPanel.add(contraseñaLabel, constraints);
        
        contraseñaTextField = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 5, 5, 5);
        formularioUsuariosPanel.add(contraseñaTextField, constraints);
        
        rolLabel = new JLabel("* Rol:");
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0, 5, 0, 0);
        formularioUsuariosPanel.add(rolLabel, constraints);
           
        rolComboBox = new JComboBox(RolUsuario.values());        
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 5, 5, 5);
        formularioUsuariosPanel.add(rolComboBox, constraints);
        
        competicionesListLabel = new JLabel ("Competiciones sin acceso");
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 5, 5, 5);
        formularioUsuariosPanel.add(competicionesListLabel, constraints);
        
        modeloListaCompeticiones = new DefaultListModel();
        competicionesList = new JList();
        listaCompeticionesScrollPane = new JScrollPane();
        competicionesList.setModel(modeloListaCompeticiones);
        listaCompeticionesScrollPane.setViewportView(competicionesList);
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 5, 5, 5);
        formularioUsuariosPanel.add(listaCompeticionesScrollPane, constraints);
        
        botonesListaCompeticionsePanel = new JPanel(new BorderLayout(1,3));
        incluirCompeticionButton = new JButton(">>>");
        botonesListaCompeticionsePanel.add(incluirCompeticionButton,BorderLayout.NORTH);
        excluirCompeticionButton = new JButton("<<<");
        botonesListaCompeticionsePanel.add(excluirCompeticionButton,BorderLayout.SOUTH);
        
        constraints.gridx = 1;
        constraints.gridy = 4;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(5, 5, 5, 5);
        formularioUsuariosPanel.add(botonesListaCompeticionsePanel, constraints);
        
        
        competicionesConAccesoListLabel = new JLabel ("Competiciones con acceso");
        constraints.gridx = 2;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 5, 5, 5);
        formularioUsuariosPanel.add(competicionesConAccesoListLabel, constraints);
        
        modeloListaCompeticionesConAcceso = new DefaultListModel();
        competicionesConAccesoList = new JList();
        listaCompeticionesConAccesoScrollPane = new JScrollPane();
        competicionesConAccesoList.setModel(modeloListaCompeticionesConAcceso);
        listaCompeticionesConAccesoScrollPane.setViewportView(competicionesConAccesoList);
        constraints.gridx = 2;
        constraints.gridy = 4;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 5, 5, 5);
        formularioUsuariosPanel.add(listaCompeticionesConAccesoScrollPane, constraints);
        
        
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 4;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(10, 20, 0, 0);
        this.add(formularioUsuariosPanel, constraints);
        constraints.insets = new Insets(0, 0, 0, 0);
        
        crearUsuarioButton = new JButton("Crear");
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0, 20, 0, 0);
        this.add(crearUsuarioButton, constraints);
        
        modificarUsuarioButton = new JButton("Modificar");
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0, 10, 0, 0);
        this.add(modificarUsuarioButton, constraints);
        
        eliminarUsuarioButton = new JButton("Eliminar");
        constraints.gridx = 2;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        this.add(eliminarUsuarioButton, constraints);
        
        limpiarFormularioUsuarioButton = new JButton("Limpiar");
        constraints.gridx = 3;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        this.add(limpiarFormularioUsuarioButton, constraints);
        
        listaUsuariosLabel = new JLabel("Lista de Usuarios");
        listaUsuariosLabel.setFont(new Font("TimesRoman", Font.BOLD, 14));
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 4;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(10, 0, 0, 0);
        this.add(listaUsuariosLabel, constraints);
        constraints.insets = new Insets(0, 0, 0, 0);
        
        usuariosScrollPane = new JScrollPane();
        modeloUsuariosTable = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        
        usuariosTable = new JTable(modeloUsuariosTable);
        usuariosTable.setAutoCreateRowSorter(true);
        usuariosTable.setSelectionMode(SINGLE_SELECTION);
        usuariosTable.setFillsViewportHeight(false);
        usuariosTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if (!lse.getValueIsAdjusting()) {
                    if (getUsuarioSeleccionado() != -1) {
                        //Coordinador.getInstance().cargarFormularioParticipante(getParticipanteSeleccionado());
                    }
                }
            }
        });
        
        usuariosScrollPane.setViewportView(usuariosTable);
        usuariosScrollPane.setPreferredSize(new Dimension(350, 200));
        modeloUsuariosTable.setColumnIdentifiers(new Object[]{"ID", "NOMBRE DE USUARIO", "CONTRASEÑA", "ROL"});
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 4;
        constraints.gridheight = 1;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(0, 20, 10, 20);
        this.add(usuariosScrollPane, constraints);
    }
    
    @Override
    public void controlador(ActionListener al){
        
    }
    
    @Override
    public Integer getUsuarioSeleccionado() {
        try {
            return (Integer) modeloUsuariosTable.getValueAt(usuariosTable.convertRowIndexToModel(usuariosTable.getSelectedRow()), 0);
        } catch (ArrayIndexOutOfBoundsException e) {
            return -1;
        } catch (IndexOutOfBoundsException e) {
            return -1;
        }
    }
}
