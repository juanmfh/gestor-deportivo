
package vista;

import controlador.ControlPrincipal;
import controlador.Coordinador;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import modelo.Competicion;

/**
 *
 * @author JuanM
 */
public class PanelPrincipal extends JPanel implements VistaPrincipal {

    private JScrollPane listaScrollPane;
    private JList listaCompeticiones;
    private JButton crearcompeticionButton;
    private JButton modificarcompeticionButton;
    private JButton eliminarcompeticionButton;
    private JPanel modElimButtonPanel;
    private JPanel barraHerramientasPanel;
    private JPanel barraInferiorPanel;
    private JTabbedPane tabbedPane;
    private DefaultListModel modeloListaCompeticiones;
    private GeneralTab generalTabPanel;
    private JPanel participantesTabPanel;
    private JPanel pruebasTabPanel;
    private JButton añadirUsuarioButton;
    private JButton participantesButton;
    private JButton gruposButton;
    private JButton equiposButton;
    private JButton registrosButton;
    private JLabel estadoEtiquetaLabel;
    private JLabel estadoLabel;
    private JButton pruebasButton;
    private JButton imprimirButton;
    private JLabel listaCompeticionesLabel;
    private JProgressBar progressBar;

    /**
     * Creates new form PanelPrincipal
     */
    public PanelPrincipal() {
        initComponents();

        this.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        // BARRA DE TAREAS
        barraHerramientasPanel = new JPanel();
        barraHerramientasPanel.setLayout(new FlowLayout());

        crearcompeticionButton = new JButton("Crear competición");
        crearcompeticionButton = new JButton("Crear competición");
        crearcompeticionButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        crearcompeticionButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        crearcompeticionButton.setIcon(new ImageIcon(getClass().getResource("/img/añadircompeticion.png")));
        barraHerramientasPanel.add(crearcompeticionButton);

        modificarcompeticionButton = new JButton("Modificar");
        modificarcompeticionButton = new JButton("Modificar");
        modificarcompeticionButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        modificarcompeticionButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        modificarcompeticionButton.setIcon(new ImageIcon(getClass().getResource("/img/modificarCompeticion.png")));
        barraHerramientasPanel.add(modificarcompeticionButton);

        eliminarcompeticionButton = new JButton("Eliminar");
        eliminarcompeticionButton = new JButton("Eliminar");
        eliminarcompeticionButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        eliminarcompeticionButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        eliminarcompeticionButton.setIcon(new ImageIcon(getClass().getResource("/img/borrarCompeticion.png")));
        barraHerramientasPanel.add(eliminarcompeticionButton);

        pruebasButton = new JButton("Pruebas");
        pruebasButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pruebasButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        pruebasButton.setIcon(new ImageIcon(getClass().getResource("/img/pruebas.png")));
        barraHerramientasPanel.add(pruebasButton);

        gruposButton = new JButton("Grupos");
        gruposButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gruposButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        gruposButton.setIcon(new ImageIcon(getClass().getResource("/img/groups.png")));
        barraHerramientasPanel.add(gruposButton);

        equiposButton = new JButton("Equipos");
        equiposButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        equiposButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        equiposButton.setIcon(new ImageIcon(getClass().getResource("/img/equipos.png")));
        barraHerramientasPanel.add(equiposButton);

        participantesButton = new JButton("Participantes");
        participantesButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        participantesButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        participantesButton.setIcon(new ImageIcon(getClass().getResource("/img/participante.png")));
        barraHerramientasPanel.add(participantesButton);

        registrosButton = new JButton("Registros");
        registrosButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        registrosButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        registrosButton.setIcon(new ImageIcon(getClass().getResource("/img/registro.png")));
        barraHerramientasPanel.add(registrosButton);

        imprimirButton = new JButton("Imprimir Resultados");
        imprimirButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        imprimirButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        imprimirButton.setIcon(new ImageIcon(getClass().getResource("/img/imprimirpdf.png")));
        barraHerramientasPanel.add(imprimirButton);

        añadirUsuarioButton = new JButton("Añadir Usuario");
        añadirUsuarioButton.setEnabled(false);
        añadirUsuarioButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        añadirUsuarioButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        añadirUsuarioButton.setIcon(new ImageIcon(getClass().getResource("/img/adduser.png")));
        barraHerramientasPanel.add(añadirUsuarioButton);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridheight = 3;
        constraints.weightx = 0;
        constraints.gridwidth = 3;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        this.add(barraHerramientasPanel, constraints);
        constraints.weightx = 0;

        // BOTON DE AÑADIR COMPETICION
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        //this.add(crearcompeticionButton, constraints);

        // BOTONES DE ELIMINAR Y MODIFCAR COMPETICION
        modElimButtonPanel = new JPanel(new GridLayout(1, 2));

        /*modElimButtonPanel.add(modificarcompeticionButton);
         modElimButtonPanel.add(eliminarcompeticionButton);

         constraints.gridx = 0;
         constraints.gridy = 4;
         constraints.gridwidth = 2;
         constraints.gridheight = 1;
         constraints.fill = GridBagConstraints.HORIZONTAL;
         constraints.anchor = GridBagConstraints.WEST;
         this.add(modElimButtonPanel, constraints);*/
        listaCompeticionesLabel = new JLabel("Lista de competiciones");
        //listaCompeticionesLabel.setFont(new Font("TimesRoman", Font.BOLD, 14));
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 2;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(5, 15, 5, 15);
        this.add(listaCompeticionesLabel, constraints);

        // LISTA DE COMPETICIONES
        listaScrollPane = new JScrollPane();
        listaCompeticiones = new JList();
        listaCompeticiones.setSelectionMode(SINGLE_SELECTION);
        modeloListaCompeticiones = new DefaultListModel();
        cargarListaCompeticiones();
        listaCompeticiones.setModel(modeloListaCompeticiones);
        listaCompeticiones.addListSelectionListener((new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if (!lse.getValueIsAdjusting()) {

                    Coordinador.getInstance().getControladorPrincipal().cargarCompeticionGeneral(getCompeticionSelected());

                    // Cargar Tab general
                    cargarTablaPruebas();
                    tabbedPane.setSelectedIndex(0);

                    if (getCompeticionSelected() != null) {
                        habilitarBotones(true);
                    } else {
                        habilitarBotones(false);
                    }

                }
            }
        }));
        listaScrollPane.setViewportView(listaCompeticiones);

        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = 2;
        constraints.gridheight = 6;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.weighty = 1.0;
        constraints.insets = new Insets(5, 5, 0, 5);
        this.add(listaScrollPane, constraints);
        constraints.weighty = 0.0;
        constraints.insets = new Insets(0, 0, 0, 0);

        // BARRA INFERIOR
        barraInferiorPanel = new JPanel(new FlowLayout());

        estadoEtiquetaLabel = new JLabel("Estado: ");
        estadoLabel = new JLabel();
        constraints.gridx = 0;
        constraints.gridy = 11;
        constraints.gridheight = 3;
        constraints.gridwidth = 8;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        barraInferiorPanel.add(estadoEtiquetaLabel);
        barraInferiorPanel.add(estadoLabel);

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        barraInferiorPanel.add(progressBar);
        progressBar.setVisible(false);
        //progressBar.setIndeterminate(true);

        this.add(barraInferiorPanel, constraints);
        constraints.weightx = 0;

        // CONTENIDO PRINCIPAL
        tabbedPane = new JTabbedPane();
        generalTabPanel = new GeneralTab();
        pruebasTabPanel = new JPanel(new GridBagLayout());
        participantesTabPanel = new JPanel(new GridBagLayout());
        tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int sele = tabbedPane.getSelectedIndex();

                if (sele == tabbedPane.indexOfTab("Participantes")) {
                    //System.out.println("Pestaña participantes seleccionada");
                    Coordinador.getInstance().getControladorPrincipal().getParticipantesTabPanel().clearSelectionParticipante();
                    Coordinador.getInstance().getControladorPrincipal().cargarGruposEnParticipantes();
                    Coordinador.getInstance().getControladorPrincipal().cargarTablaParticipantes();
                } else if (sele == tabbedPane.indexOfTab("Grupos")) {
                    //System.out.println("Pestaña grupos seleccionada");
                    Coordinador.getInstance().getControladorPrincipal().cargarSubGruposComboBox();
                    Coordinador.getInstance().getControladorPrincipal().cargarTablaGrupos();
                } else if (sele == tabbedPane.indexOfTab("Equipos")) {
                    //System.out.println("Pestaña equipos seleccionada");
                    Coordinador.getInstance().getControladorPrincipal().cargarTablaEquipos();
                    Coordinador.getInstance().getControladorPrincipal().cargarGruposEnEquipos();
                } else if (sele == tabbedPane.indexOfTab("Registros")) {
                    //System.out.println("Pestaña registros seleccionada");
                    Coordinador.getInstance().getControladorPrincipal().cargarGruposEnRegistros();
                    Coordinador.getInstance().getControladorPrincipal().cargarPruebasEnRegistros();
                    Coordinador.getInstance().limpiarTablaRegistros();
                } else if (sele == tabbedPane.indexOfTab("General")) {
                    Coordinador.getInstance().cargarTablaPruebasCompeticion(Coordinador.getInstance().getSeleccionada());
                }
            }
        });
        tabbedPane.addTab("General", generalTabPanel);

        constraints.gridx = 2;
        constraints.gridy = 3;
        constraints.gridwidth = 6;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1.0;
        this.add(tabbedPane, constraints);

        this.habilitarBotones(false);
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

        crearcompeticionButton.addActionListener(al);
        crearcompeticionButton.setActionCommand(CREARCOMPETICION);

        modificarcompeticionButton.addActionListener(al);
        modificarcompeticionButton.setActionCommand(MODIFICARCOMPETICION);

        eliminarcompeticionButton.addActionListener(al);
        eliminarcompeticionButton.setActionCommand(ELIMINARCOMPETICION);

        participantesButton.addActionListener(al);
        participantesButton.setActionCommand(ABRIRPARTICIPANTES);

        registrosButton.addActionListener(al);
        registrosButton.setActionCommand(ABRIRREGISTROS);

        gruposButton.addActionListener(al);
        gruposButton.setActionCommand(ABRIRGRUPOS);

        equiposButton.addActionListener(al);
        equiposButton.setActionCommand(ABRIREQUIPOS);

        pruebasButton.addActionListener(al);
        pruebasButton.setActionCommand(ABRIRPRUEBAS);

        imprimirButton.addActionListener(al);
        imprimirButton.setActionCommand(IMPRIMIRPDF);
    }

    private void cargarListaCompeticiones() {
        List<String> listaCompeticiones = ControlPrincipal.getCompeticiones();
        for (String temp : listaCompeticiones) {
            modeloListaCompeticiones.addElement(temp);
        }
    }

    @Override
    public void cargarCompeticionTabGeneral() {

        Competicion c = Coordinador.getInstance().getControladorPrincipal().getSeleccionada();
        if (c != null) {
            generalTabPanel.getNombreCompeticion().setText(c.getNombre());
            generalTabPanel.setLugar(c.getCiudad());
            generalTabPanel.setOrganizador(c.getOrganizador());
            SimpleDateFormat textFormat = new SimpleDateFormat("dd-MM-yyyy");
            if (c.getFechainicio() != null) {

                generalTabPanel.setFechaInicio(textFormat.format(c.getFechainicio()));
            } else {
                generalTabPanel.setFechaInicio("");
            }
            if (c.getFechafin() != null) {
                generalTabPanel.setFechaFin(textFormat.format(c.getFechafin()));
            } else {
                generalTabPanel.setFechaFin("");
            }
            if (c.getImagen() != null && c.getImagen().length() > 0) {
                //System.out.println("Ruta : " + System.getProperty("user.dir") + "/src/img/" + c.getImagen());
                generalTabPanel.setImagen(System.getProperty("user.dir") + "/src/img/" + c.getImagen());
            } else {
                //System.out.println(getClass().getResource("/img/image_not_found.jpg").toString());
                generalTabPanel.setImagen(getClass().getResource("/img/image_not_found.jpg"));
            }

        } else {
            generalTabPanel.getNombreCompeticion().setText("");
        }

    }

    public void cargarTablaPruebas() {

        if (Coordinador.getInstance().getSeleccionada() != null) {
            Coordinador.getInstance().cargarTablaPruebasCompeticion(Coordinador.getInstance().getSeleccionada());

        }
    }

    @Override
    public void añadirCompeticion(String nombre) {
        modeloListaCompeticiones.add(0, nombre);
    }

    @Override
    public String getCompeticionSelected() {
        return (String) listaCompeticiones.getSelectedValue();

    }

    @Override
    public void modificarCompeticionSeleccionada(String competicion) {
        modeloListaCompeticiones.remove(listaCompeticiones.getSelectedIndex());
        modeloListaCompeticiones.add(0, competicion);
        setFocusList(0);
    }

    @Override
    public void eliminarCompeticionSeleccionada() {
        modeloListaCompeticiones.remove(listaCompeticiones.getSelectedIndex());
    }

    @Override
    public void limpiarDatosCompeticion() {
        //generalTabPanel.getNombreCompeticion().setText("");
        generalTabPanel.limpiarDatosCompeticion();
        int count = generalTabPanel.getModeloPruebasTable().getRowCount();

        for (int i = 0; i < count; i++) {
            generalTabPanel.getModeloPruebasTable().removeRow(0);
        }
    }

    @Override
    public void setFocusList(int i) {
        listaCompeticiones.clearSelection();
        listaCompeticiones.setSelectedIndex(i);
    }
     
    public GeneralTab getGeneralTabPanel() {
        return generalTabPanel;
    }

    public void setGeneralTabPanel(GeneralTab generalTabPanel) {
        this.generalTabPanel = generalTabPanel;
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public void setTabbedPane(JTabbedPane tabbedPane) {
        this.tabbedPane = tabbedPane;
    }

    @Override
    public void setEstadoLabel(String estado, Color color) {
        this.estadoLabel.setText(estado);
        this.estadoLabel.setForeground(color);
    }

    private void habilitarBotones(Boolean bool) {
        this.pruebasButton.setEnabled(bool);
        this.gruposButton.setEnabled(bool);
        this.equiposButton.setEnabled(bool);
        this.registrosButton.setEnabled(bool);
        this.participantesButton.setEnabled(bool);
        this.imprimirButton.setEnabled(bool);
        this.modificarcompeticionButton.setEnabled(bool);
        this.eliminarcompeticionButton.setEnabled(bool);
        this.getGeneralTabPanel().getCrearPruebaButton().setEnabled(bool);
        this.getGeneralTabPanel().getModificarPruebaButton().setEnabled(bool);
        this.getGeneralTabPanel().getEliminarPruebaButton().setEnabled(bool);
        this.getGeneralTabPanel().getLimpiarPruebaButton().setEnabled(bool);
    }

    @Override
    public void mostrarBarraProgreso(Boolean mostrar) {
        progressBar.setVisible(mostrar);
        progressBar.setIndeterminate(mostrar);
    }

}
