package vista;

import vista.interfaces.VistaRegistros;
import controlador.Coordinador;
import modelo.entities.RolUsuario;
import modelo.entities.TipoResultado;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
public class RegistrosTab extends javax.swing.JPanel implements VistaRegistros {

    private JPanel formularioRegistroPanel;
    private JLabel pruebaLabel;
    private JComboBox pruebaComboBox;
    private DefaultComboBoxModel modeloPruebaComboBox;
    private JLabel grupoLabel;
    private DefaultComboBoxModel modeloGrupoComboBox;
    private JComboBox grupoComboBox;
    private JComboBox equipoComboBox;
    private JLabel participanteLabel;
    private JComboBox participanteComboBox;
    private DefaultComboBoxModel modeloParticipanteComboBox;
    private JLabel sorteoLabel;
    private ButtonGroup sorteoButtonGroup;
    private JRadioButton siSorteoRadioButton;
    private JRadioButton noSorteoRadioButton;
    private JLabel marcaLabel;
    private JButton crearRegistroButton;
    private JButton modificarRegistroButton;
    private JButton eliminarRegistroButton;
    private JButton limpiarFormularioRegistroButton;
    private JPanel sorteoPanel;
    private JTable registrosTable;
    private DefaultTableModel modeloRegistrosTable;
    private JScrollPane registrosScrollPane;
    private JPanel marcaTiempoPanel;
    private JLabel horaLabel;
    private JTextField horaTextField;
    private JLabel minutosLabel;
    private JTextField minutosTextField;
    private JLabel segundosLabel;
    private JTextField segundosTextField;
    private JButton importarRegistrosButton; 
    private JButton crearPlantillaButton;
    private JCheckBox soloParticipantesAsignadosCheckBox;

    private JPanel filtrosPanel;
    private JLabel filtrarLabel;
    private JLabel filtrarGrupoLabel;
    private JComboBox filtroGrupoComboBox;
    private DefaultComboBoxModel modeloFiltroGrupos;
    private JLabel filtrarPruebaLabel;
    private DefaultComboBoxModel modeloFiltroPruebas;
    private JComboBox filtroPruebaComboBox;
    private JButton aplicarFiltroButton;
    private JLabel listaRegistrosLabel;
    private JCheckBox soloMejoresMarcasCheckBox;

    public RegistrosTab() {

        this.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        
        crearPlantillaButton = new JButton("Crear plantilla...");
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(10, 20, 0, 0);
        constraints.anchor = GridBagConstraints.WEST;
        this.add(crearPlantillaButton, constraints);
        
        importarRegistrosButton = new JButton("Importar desde Excel");
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(10, 20, 0, 0);
        constraints.anchor = GridBagConstraints.WEST;
        this.add(importarRegistrosButton, constraints);

        GridBagConstraints constraintsFormulario = new GridBagConstraints();
        formularioRegistroPanel = new JPanel(new GridBagLayout());
        TitledBorder bordeDatosPanel = BorderFactory.createTitledBorder("Formulario Registro");
        formularioRegistroPanel.setBorder(bordeDatosPanel);

        grupoLabel = new JLabel("* Grupo:");
        constraintsFormulario.gridx = 0;
        constraintsFormulario.gridy = 0;
        constraintsFormulario.gridwidth = 1;
        constraintsFormulario.gridheight = 1;
        constraintsFormulario.fill = GridBagConstraints.NONE;
        constraintsFormulario.anchor = GridBagConstraints.WEST;
        constraintsFormulario.insets = new Insets(5, 5, 5, 5);
        formularioRegistroPanel.add(grupoLabel, constraintsFormulario);

        sorteoLabel = new JLabel("* Prueba por sorteo:");
        constraintsFormulario.gridx = 1;
        constraintsFormulario.gridy = 0;
        constraintsFormulario.gridwidth = 1;
        constraintsFormulario.gridheight = 1;
        constraintsFormulario.fill = GridBagConstraints.NONE;
        constraintsFormulario.anchor = GridBagConstraints.WEST;
        formularioRegistroPanel.add(sorteoLabel, constraintsFormulario);

        pruebaLabel = new JLabel("* Prueba:");
        constraintsFormulario.gridx = 2;
        constraintsFormulario.gridy = 0;
        constraintsFormulario.gridwidth = 1;
        constraintsFormulario.gridheight = 1;
        constraintsFormulario.fill = GridBagConstraints.NONE;
        constraintsFormulario.anchor = GridBagConstraints.WEST;
        formularioRegistroPanel.add(pruebaLabel, constraintsFormulario);

        participanteLabel = new JLabel("* Participante:");
        constraintsFormulario.gridx = 0;
        constraintsFormulario.gridy = 2;
        constraintsFormulario.gridwidth = 1;
        constraintsFormulario.gridheight = 1;
        constraintsFormulario.fill = GridBagConstraints.NONE;
        constraintsFormulario.anchor = GridBagConstraints.WEST;
        formularioRegistroPanel.add(participanteLabel, constraintsFormulario);

        modeloGrupoComboBox = new DefaultComboBoxModel();
        grupoComboBox = new JComboBox(modeloGrupoComboBox);
        constraintsFormulario.gridx = 0;
        constraintsFormulario.gridy = 1;
        constraintsFormulario.gridwidth = 1;
        constraintsFormulario.gridheight = 1;
        constraintsFormulario.fill = GridBagConstraints.HORIZONTAL;
        constraintsFormulario.anchor = GridBagConstraints.WEST;
        formularioRegistroPanel.add(grupoComboBox, constraintsFormulario);

        modeloPruebaComboBox = new DefaultComboBoxModel();
        pruebaComboBox = new JComboBox(modeloPruebaComboBox);
        constraintsFormulario.gridx = 2;
        constraintsFormulario.gridy = 1;
        constraintsFormulario.gridwidth = 1;
        constraintsFormulario.gridheight = 1;
        constraintsFormulario.fill = GridBagConstraints.HORIZONTAL;
        constraintsFormulario.anchor = GridBagConstraints.WEST;
        formularioRegistroPanel.add(pruebaComboBox, constraintsFormulario);
        constraints.weightx = 0;

        modeloParticipanteComboBox = new DefaultComboBoxModel();
        participanteComboBox = new JComboBox(modeloParticipanteComboBox);
        constraintsFormulario.gridx = 0;
        constraintsFormulario.gridy = 3;
        constraintsFormulario.gridwidth = 1;
        constraintsFormulario.gridheight = 1;
        constraintsFormulario.fill = GridBagConstraints.HORIZONTAL;
        constraintsFormulario.anchor = GridBagConstraints.WEST;
        formularioRegistroPanel.add(participanteComboBox, constraintsFormulario);

        sorteoPanel = new JPanel(new FlowLayout());
        sorteoButtonGroup = new ButtonGroup();
        siSorteoRadioButton = new JRadioButton("Si");
        noSorteoRadioButton = new JRadioButton("No");
        sorteoButtonGroup.add(siSorteoRadioButton);
        sorteoButtonGroup.add(noSorteoRadioButton);
        sorteoPanel.add(siSorteoRadioButton);
        sorteoPanel.add(noSorteoRadioButton);
        constraintsFormulario.gridx = 1;
        constraintsFormulario.gridy = 1;
        constraintsFormulario.gridwidth = 1;
        constraintsFormulario.gridheight = 1;
        constraintsFormulario.fill = GridBagConstraints.HORIZONTAL;
        constraintsFormulario.anchor = GridBagConstraints.WEST;
        formularioRegistroPanel.add(sorteoPanel, constraintsFormulario);

        marcaLabel = new JLabel("* Marca:");
        constraintsFormulario.gridx = 1;
        constraintsFormulario.gridy = 2;
        constraintsFormulario.gridwidth = 2;
        constraintsFormulario.gridheight = 1;
        constraintsFormulario.fill = GridBagConstraints.HORIZONTAL;
        constraintsFormulario.anchor = GridBagConstraints.WEST;
        formularioRegistroPanel.add(marcaLabel, constraintsFormulario);

        marcaTiempoPanel = new JPanel(new FlowLayout());
        horaLabel = new JLabel("HH");
        marcaTiempoPanel.add(horaLabel);
        horaTextField = new JTextField(2);
        marcaTiempoPanel.add(horaTextField);
        minutosLabel = new JLabel("MM");
        marcaTiempoPanel.add(minutosLabel);
        minutosTextField = new JTextField(2);
        marcaTiempoPanel.add(minutosTextField);
        segundosLabel = new JLabel("SS");
        marcaTiempoPanel.add(segundosLabel);
        segundosTextField = new JTextField(5);
        marcaTiempoPanel.add(segundosTextField);

        constraintsFormulario.gridx = 1;
        constraintsFormulario.gridy = 3;
        constraintsFormulario.gridwidth = 2;
        constraintsFormulario.gridheight = 1;
        constraintsFormulario.fill = GridBagConstraints.HORIZONTAL;
        constraintsFormulario.anchor = GridBagConstraints.WEST;
        formularioRegistroPanel.add(marcaTiempoPanel, constraintsFormulario);
        
        soloParticipantesAsignadosCheckBox = new JCheckBox("Solo participantes asignados");
        constraintsFormulario.gridx = 0;
        constraintsFormulario.gridy = 4;
        constraintsFormulario.gridwidth = 1;
        constraintsFormulario.gridheight = 1;
        constraintsFormulario.fill = GridBagConstraints.NONE;
        constraintsFormulario.anchor = GridBagConstraints.WEST;
        formularioRegistroPanel.add(soloParticipantesAsignadosCheckBox, constraintsFormulario);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 4;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(10, 20, 0, 20);
        this.add(formularioRegistroPanel, constraints);
        
        // BOTONERA
        JPanel botoneraPanel = new JPanel(new FlowLayout()); 
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 4;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0, 20, 0, 0);
        this.add(botoneraPanel, constraints);

        crearRegistroButton = new JButton("Crear");
        botoneraPanel.add(crearRegistroButton);

        modificarRegistroButton = new JButton("Modificar");
        botoneraPanel.add(modificarRegistroButton);

        eliminarRegistroButton = new JButton("Eliminar");
        botoneraPanel.add(eliminarRegistroButton);

        limpiarFormularioRegistroButton = new JButton("Limpiar");
        botoneraPanel.add(limpiarFormularioRegistroButton);

        listaRegistrosLabel = new JLabel("Lista de Registros");
        listaRegistrosLabel.setFont(new Font("TimesRoman", Font.BOLD, 14));
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 4;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(10, 0, 0, 0);
        this.add(listaRegistrosLabel, constraints);
        constraints.insets = new Insets(0, 0, 0, 0);

        // FILTROS
        filtrosPanel = new JPanel(new FlowLayout());
        filtrarLabel = new JLabel("Filtrar por: ");
        filtrosPanel.add(filtrarLabel);
        filtrarGrupoLabel = new JLabel("Grupo:");
        filtrosPanel.add(filtrarGrupoLabel);
        modeloFiltroGrupos = new DefaultComboBoxModel();
        filtroGrupoComboBox = new JComboBox(modeloFiltroGrupos);
        filtrosPanel.add(filtroGrupoComboBox);
        filtrarPruebaLabel = new JLabel("Prueba:");
        filtrosPanel.add(filtrarPruebaLabel);
        modeloFiltroPruebas = new DefaultComboBoxModel();
        filtroPruebaComboBox = new JComboBox(modeloFiltroPruebas);
        filtrosPanel.add(filtroPruebaComboBox);
        soloMejoresMarcasCheckBox = new JCheckBox("Solo mejores marcas por participante");
        filtrosPanel.add(soloMejoresMarcasCheckBox);
        aplicarFiltroButton = new JButton("Aplicar");
        filtrosPanel.add(aplicarFiltroButton);
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 4;
        constraints.gridheight = 1;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(10, 20, 0, 0);
        this.add(filtrosPanel, constraints);
        

        registrosScrollPane = new JScrollPane();
        modeloRegistrosTable = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        registrosTable = new JTable(modeloRegistrosTable);
        registrosTable.setAutoCreateRowSorter(true);
        registrosTable.setSelectionMode(SINGLE_SELECTION);
        registrosTable.setFillsViewportHeight(false);
        registrosTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if (!lse.getValueIsAdjusting()) {
                    if (getRegistroSeleccionado() != -1) {
                        Coordinador.getInstance().cargarFormularioRegistro(getRegistroSeleccionado());
                    }
                }
            }
        });
        registrosScrollPane.setViewportView(registrosTable);
        modeloRegistrosTable.setColumnIdentifiers(new Object[]{"ID", "DORSAL/ID_EQUIPO", "PARTICIPANTE/EQUIPO", "PRUEBA", "MARCA", "NUM. INTENTO"});
        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = 4;
        constraints.gridheight = 1;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(0, 20, 10, 20);
        this.add(registrosScrollPane, constraints);

        grupoComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (getGrupoParticipante() != null
                        && pruebaComboBox.getSelectedIndex() != -1) {
                    Coordinador.getInstance().getControladorPrincipal().cargarParticipantesEnRegistros(getGrupoParticipante(),getPrueba(),participantesAsignadosCheckBoxIsSelected());
                }
            }
        });

        siSorteoRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                Random r = new Random();
                if (pruebaComboBox.getItemCount() >= 1) {
                    pruebaComboBox.setSelectedIndex(r.nextInt(pruebaComboBox.getItemCount()));
                    pruebaComboBox.setEnabled(false);
                }
            }
        });

        noSorteoRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                pruebaComboBox.setEnabled(true);
            }
        });

        pruebaComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (getPrueba() != null) {
                    TipoResultado tr = Coordinador.getInstance().tipoResultado(getPrueba());
                    
                    switch (tr) {
                        case Tiempo:
                            horaLabel.setVisible(true);
                            horaTextField.setVisible(true);
                            minutosLabel.setVisible(true);
                            minutosTextField.setVisible(true);
                            minutosLabel.setVisible(true);
                            segundosLabel.setText("SS:");
                            break;
                        case Distancia:
                            horaLabel.setVisible(false);
                            horaTextField.setVisible(false);
                            minutosLabel.setVisible(false);
                            minutosTextField.setVisible(false);
                            minutosLabel.setVisible(false);
                            segundosLabel.setText("Distancia:");
                            break;
                        case Numerica:
                            horaLabel.setVisible(false);
                            horaTextField.setVisible(false);
                            minutosLabel.setVisible(false);
                            minutosTextField.setVisible(false);
                            minutosLabel.setVisible(false);
                            segundosLabel.setText("Calificación:");
                            break;
                    }
                    if (grupoComboBox.getSelectedIndex() != -1) {
                        Coordinador.getInstance().getControladorPrincipal().cargarParticipantesEnRegistros(getGrupoParticipante(),getPrueba(),participantesAsignadosCheckBoxIsSelected());
                    }
                }
            }
        });
        
        soloParticipantesAsignadosCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (getGrupoParticipante() != null
                        && pruebaComboBox.getSelectedIndex() != -1) {
                    Coordinador.getInstance().getControladorPrincipal().cargarParticipantesEnRegistros(getGrupoParticipante(),getPrueba(),participantesAsignadosCheckBoxIsSelected());
                }
            }
        });
        
        this.habilitarBotones(RolUsuario.values()[Coordinador.getInstance().getUsuario().getRol()]);
    }

    @Override
    public void controlador(ActionListener al) {
        crearRegistroButton.addActionListener(al);
        crearRegistroButton.setActionCommand(VistaRegistros.CREARREGISTRO);

        modificarRegistroButton.addActionListener(al);
        modificarRegistroButton.setActionCommand(VistaRegistros.MODIFICARREGISTRO);

        eliminarRegistroButton.addActionListener(al);
        eliminarRegistroButton.setActionCommand(VistaRegistros.ELIMINARREGISTRO);

        limpiarFormularioRegistroButton.addActionListener(al);
        limpiarFormularioRegistroButton.setActionCommand(VistaRegistros.LIMPIAR);

        aplicarFiltroButton.addActionListener(al);
        aplicarFiltroButton.setActionCommand(VistaRegistros.FILTRAR);
        
        importarRegistrosButton.addActionListener(al);
        importarRegistrosButton.setActionCommand(VistaRegistros.IMPORTARREGISTROS);
        
        crearPlantillaButton.addActionListener(al);
        crearPlantillaButton.setActionCommand(VistaRegistros.CREARPLANTILLA);
    }

    @Override
    public JComboBox getGruposComboBox() {
        return grupoComboBox;
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
    public JComboBox getEquiposComboBox() {
        return equipoComboBox;
    }

    @Override
    public JComboBox getParticipantesComboBox() {
        return participanteComboBox;
    }

    @Override
    public void limpiarFormulario() {

        grupoComboBox.setSelectedIndex(-1);
        participanteComboBox.setSelectedIndex(-1);
        sorteoButtonGroup.clearSelection();
        pruebaComboBox.setSelectedIndex(-1);
        registrosTable.clearSelection();
        limpiarMarca();
    }

    private void limpiarMarca() {
        horaTextField.setText("");
        minutosTextField.setText("");
        segundosTextField.setText("");
    }

    @Override
    public JComboBox getPruebasComboBox() {
        return pruebaComboBox;
    }

    @Override
    public JComboBox getFiltroGrupoComboBox() {
        return filtroGrupoComboBox;
    }

    @Override
    public Integer getDorsalParticipante() {
        try {
            String cadena = participanteComboBox.getSelectedItem().toString();
            return Integer.parseInt(cadena.substring(0, cadena.indexOf(":")));
        } catch (NullPointerException | StringIndexOutOfBoundsException e) {
            return null;
        }
    }
    
    @Override
    public String getEquipo(){
        System.out.println(participanteComboBox.getSelectedItem().toString());
        return participanteComboBox.getSelectedItem().toString();
        
    }

    @Override
    public String getPrueba() {
        try {
            return pruebaComboBox.getSelectedItem().toString();
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public Integer getSorteo() {
        return siSorteoRadioButton.isSelected() ? 1 : 0;
    }

    @Override
    public DefaultTableModel getModeloRegistrosTable() {
        return modeloRegistrosTable;
    }

    @Override
    public void añadirRegistroATabla(Object[] o) {
        this.modeloRegistrosTable.addRow(o);
    }

    @Override
    public Integer getRegistroSeleccionado() {
        try {
            return (Integer) modeloRegistrosTable.getValueAt(registrosTable.convertRowIndexToModel(registrosTable.getSelectedRow()), 0);
        } catch (ArrayIndexOutOfBoundsException e) {
            return -1;
        } catch (IndexOutOfBoundsException e) {
            return -1;
        }
    }

    @Override
    public void eliminarRegistroDeTabla() {
        modeloRegistrosTable.removeRow(registrosTable.convertRowIndexToModel(registrosTable.getSelectedRow()));
    }

    @Override
    public String getHoras() {
        if (horaTextField.getText().toString().length() == 0) {
            return null;
        } else {
            return horaTextField.getText().toString();
        }

    }

    @Override
    public String getMinutos() {
        if (minutosTextField.getText().toString().length() == 0) {
            return null;
        } else {
            return minutosTextField.getText().toString();
        }
    }

    @Override
    public String getSegundos() {
        if (segundosTextField.getText().toString().length() == 0) {
            return null;
        } else {
            return segundosTextField.getText().toString();
        }

    }

    @Override
    public JComboBox getFiltroPruebasComboBox() {
        return filtroPruebaComboBox;
    }

    @Override
    public void setGrupoParticipante(String grupo) {
        grupoComboBox.setSelectedItem(grupo);
    }

    @Override
    public void setPrueba(String prueba) {
        pruebaComboBox.setSelectedItem(prueba);
    }

    @Override
    public void setHoras(String horas) {
        horaTextField.setText(horas);
    }

    @Override
    public void setMinutos(String minutos) {
        minutosTextField.setText(minutos);
    }

    @Override
    public void setSegundos(String segundos) {
        segundosTextField.setText(segundos);
    }


    @Override
    public void setParticipante(String participante) {
        participanteComboBox.setSelectedItem(participante);
    }
    
    @Override
    public boolean participantesAsignadosCheckBoxIsSelected(){
        return soloParticipantesAsignadosCheckBox.isSelected();
    }
    
    @Override
    public boolean mejoresMarcasCheckBoxIsSelected(){
        return soloMejoresMarcasCheckBox.isSelected();
    }

    private void habilitarBotones(RolUsuario rol){
        
        if(rol.equals(RolUsuario.Invitado)){
            crearRegistroButton.setEnabled(false);
            modificarRegistroButton.setEnabled(false);
            eliminarRegistroButton.setEnabled(false);
            limpiarFormularioRegistroButton.setEnabled(false);
            importarRegistrosButton.setEnabled(false);
            crearPlantillaButton.setEnabled(false);
        }
        
    }
}
