package vista;

import java.awt.Color;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author JuanM
 */
public class DialogoImprimirResultados extends JDialog implements VistaImprimirResultados {

    private JButton cancelarButton;
    private JCheckBox gruposCheckBox;
    private JLabel gruposLabel;
    private JList gruposList;
    private JButton imprimirButton;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    private JCheckBox pruebasCheckBox;
    private JCheckBox listaSalidaCheckBox;
    private JLabel pruebasLabel;
    private JList pruebasList;
    private DefaultListModel pruebasListModel;
    private DefaultListModel gruposListModel;
    private JCheckBox participantesAsignadosCheckBox;
    private String formato;

    public DialogoImprimirResultados(Frame parent, boolean modal, String formato) {
        super(parent, modal);
        this.formato = formato;
        initComponents(formato);
    }

    private void initComponents(String formato) {

        this.setTitle("Opciones");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        GridBagLayout layout = new GridBagLayout();
        this.setLayout(layout);
        GridBagConstraints constraints = new GridBagConstraints();

        pruebasLabel = new javax.swing.JLabel("Pruebas");
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(10, 10, 10, 10);

        this.add(pruebasLabel, constraints);

        gruposLabel = new javax.swing.JLabel("Grupos");
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(10, 10, 10, 10);
        this.add(gruposLabel, constraints);

        jScrollPane1 = new javax.swing.JScrollPane();
        pruebasList = new javax.swing.JList();
        pruebasListModel = new DefaultListModel();
        pruebasList.setModel(pruebasListModel);
        pruebasList.setPrototypeCellValue("                                                            ");
        jScrollPane1.setViewportView(pruebasList);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0, 10, 5, 0);
        this.add(jScrollPane1, constraints);

        jScrollPane2 = new javax.swing.JScrollPane();
        gruposList = new javax.swing.JList();
        gruposListModel = new DefaultListModel();
        gruposList.setModel(gruposListModel);
        gruposList.setPrototypeCellValue("                                                            ");
        jScrollPane2.setViewportView(gruposList);
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0, 10, 5, 0);
        this.add(jScrollPane2, constraints);

        pruebasCheckBox = new javax.swing.JCheckBox("Todas las pruebas");
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0, 10, 0, 0);
        this.add(pruebasCheckBox, constraints);

        pruebasCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                if (pruebasCheckBox.isSelected()) {
                    pruebasList.clearSelection();
                    pruebasList.setEnabled(false);
                } else {
                    pruebasList.setEnabled(true);
                }
            }
        });

        gruposCheckBox = new javax.swing.JCheckBox("Todos los grupos");
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        //constraints.insets = new Insets(10,10,10,10);
        this.add(gruposCheckBox, constraints);

        gruposCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                if (gruposCheckBox.isSelected()) {
                    gruposList.clearSelection();
                    gruposList.setEnabled(false);
                } else {
                    gruposList.setEnabled(true);
                }
            }
        });

        listaSalidaCheckBox = new javax.swing.JCheckBox("Generar lista de salida");
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0, 10, 5, 0);
        this.add(listaSalidaCheckBox, constraints);

        participantesAsignadosCheckBox = new JCheckBox("Solo participantes asignados");
        constraints.gridx = 1;
        constraints.gridy = 4;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0, 10, 0, 0);
        this.add(participantesAsignadosCheckBox, constraints);

        listaSalidaCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                if (listaSalidaCheckBox.isSelected()) {

                    participantesAsignadosCheckBox.setEnabled(true);
                } else {
                    participantesAsignadosCheckBox.setSelected(false);
                    participantesAsignadosCheckBox.setEnabled(false);
                }
            }
        });

        imprimirButton = new javax.swing.JButton("Crear fichero " + formato);
        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0, 10, 0, 0);
        constraints.weighty = 1.0;
        this.add(imprimirButton, constraints);

        cancelarButton = new javax.swing.JButton("Cancelar");
        constraints.gridx = 1;
        constraints.gridy = 5;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        //constraints.insets = new Insets(10,10,10,10);
        constraints.weightx = 1.0;
        this.add(cancelarButton, constraints);

        if (formato.equals("Excel")) {
            listaSalidaCheckBox.setVisible(false);

            constraints.gridx = 0;
            constraints.gridy = 4;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            constraints.fill = GridBagConstraints.NONE;
            constraints.anchor = GridBagConstraints.WEST;
            constraints.insets = new Insets(0, 10, 5, 0);
            this.add(participantesAsignadosCheckBox, constraints);
        }
    }

    @Override
    public void controlador(ActionListener al) {
        imprimirButton.addActionListener(al);
        imprimirButton.setActionCommand(VistaImprimirResultados.OK);

        cancelarButton.addActionListener(al);
        cancelarButton.setActionCommand(VistaImprimirResultados.CANCELAR);
    }

    @Override
    public void cerrar() {
        this.dispose();
    }

    @Override
    public boolean getpruebasCheckBox() {
        return pruebasCheckBox.isSelected();
    }

    @Override
    public boolean getgruposCheckBox() {
        return gruposCheckBox.isSelected();
    }

    @Override
    public boolean getparticipantesAsignadosCheckBox() {
        return participantesAsignadosCheckBox.isSelected();
    }

    @Override
    public boolean getgenerarListaSalidaCheckBox() {
        return listaSalidaCheckBox.isSelected();
    }

    @Override
    public List<String> getpruebasList() {
        return pruebasList.getSelectedValuesList();
    }

    @Override
    public List<String> getgruposList() {
        return gruposList.getSelectedValuesList();
    }

    @Override
    public void añadirPrueba(String nombrePrueba) {
        pruebasListModel.add(0, nombrePrueba);
    }

    @Override
    public void añadirGrupo(String nombreGrupo) {
        gruposListModel.add(0, nombreGrupo);
    }

    @Override
    public void asignarListaPruebas(List<String> pruebas) {
        for (String prueba : pruebas) {
            añadirPrueba(prueba);
        }
    }

    @Override
    public void asignarListaGrupos(List<String> grupos) {
        for (String grupo : grupos) {
            añadirGrupo(grupo);
        }
    }

    @Override
    public String getFormato() {
        return formato;
    }
}
