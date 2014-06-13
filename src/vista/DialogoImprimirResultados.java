package vista;

import java.awt.Frame;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
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
    private JLabel pruebasLabel;
    private JList pruebasList;
    private DefaultListModel pruebasListModel;
    private DefaultListModel gruposListModel;
    
    public DialogoImprimirResultados(Frame parent, boolean modal){
        super(parent, modal);
        initComponents();
    }
    
    private void initComponents(){
        
        this.setTitle("Opciones de impresión");
        
        pruebasLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        pruebasList = new javax.swing.JList();
        gruposLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        gruposList = new javax.swing.JList();
        pruebasCheckBox = new javax.swing.JCheckBox();
        gruposCheckBox = new javax.swing.JCheckBox();
        imprimirButton = new javax.swing.JButton();
        cancelarButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        pruebasLabel.setText("Pruebas");

        pruebasListModel = new DefaultListModel();
        pruebasList.setModel(pruebasListModel);
        jScrollPane1.setViewportView(pruebasList);

        gruposLabel.setText("Grupos");

        gruposListModel = new DefaultListModel();
        gruposList.setModel(gruposListModel);
        jScrollPane2.setViewportView(gruposList);

        pruebasCheckBox.setText("Todas las pruebas");
        pruebasCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                    if(pruebasCheckBox.isSelected()){
                        pruebasList.clearSelection();
                        pruebasList.setEnabled(false);
                    }else{
                        pruebasList.setEnabled(true);
                    }
                }
        });

        gruposCheckBox.setText("Todos los grupos");
        gruposCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                    if(gruposCheckBox.isSelected()){
                        gruposList.clearSelection();
                        gruposList.setEnabled(false);
                    }else{
                        gruposList.setEnabled(true);
                    }
                }
        });

        imprimirButton.setText("Imprimir en PDF");

        cancelarButton.setText("Cancelar");
        
        GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(gruposCheckBox)
                    .addComponent(pruebasCheckBox)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(imprimirButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cancelarButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pruebasLabel)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(gruposLabel)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pruebasLabel)
                    .addComponent(gruposLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pruebasCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(gruposCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(imprimirButton)
                    .addComponent(cancelarButton))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        pack();
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
    public void asignarListaPruebas(List<String> pruebas){
        for(String prueba: pruebas){
            añadirPrueba(prueba);
        }
    }
    
    @Override
    public void asignarListaGrupos(List<String> grupos){
        for(String grupo: grupos){
            añadirGrupo(grupo);
        }
    }
}
