package vista;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author JuanM
 */
public class PanelLogin extends JPanel implements VistaLogin {

    private JPanel jpcontenido;
    private JLabel usuarioJL;
    private JLabel contraseñaJL;
    private JTextField usuarioJTF;
    private JPasswordField contraseñaJPF;
    private JButton iniciarsesionJB;
    private JLabel estadoJL;

    public PanelLogin() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");  // Pone el estilo visual de Windows
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(PanelLogin.class.getName()).log(Level.SEVERE, null, ex);
        }
        initComponents();

        this.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        jpcontenido = new JPanel(new GridLayout(3, 1, 5, 5));
        jpcontenido.setBorder(new EmptyBorder(20, 20, 20, 20));
        jpcontenido.setBackground(Color.WHITE);
        usuarioJL = new JLabel("Nombre de usuario");
        contraseñaJL = new JLabel("Contraseña");
        usuarioJTF = new JTextField(20);
        contraseñaJPF = new JPasswordField(20);
        iniciarsesionJB = new JButton("Iniciar Sesión");
        estadoJL = new JLabel("");

        this.add(jpcontenido, constraints);
        jpcontenido.add(usuarioJL);
        jpcontenido.add(usuarioJTF);
        jpcontenido.add(contraseñaJL);
        jpcontenido.add(contraseñaJPF);
        jpcontenido.add(iniciarsesionJB);
        jpcontenido.add(estadoJL);

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        Color color1 = getBackground();
        Color color2 = color1.darker();
        int ancho = this.getWidth();
        int alto = this.getHeight();
        GradientPaint gp = new GradientPaint(
                0, 0, color1, 0, alto, color2);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, ancho, alto);
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
    public void addNotify() {
        super.addNotify();
        SwingUtilities.getRootPane(iniciarsesionJB).setDefaultButton(iniciarsesionJB);
    }

    @Override
    public void controlador(ActionListener al) {
        iniciarsesionJB.addActionListener(al);
        iniciarsesionJB.setActionCommand(INICIARSESION);
    }

    @Override
    public String getUsuario() {
        return usuarioJTF.getText();
    }

    @Override
    public char[] getContraseña() {
        return contraseñaJPF.getPassword();
    }

    @Override
    public void estado(String estado, Color color) {
        estadoJL.setText(estado);
        estadoJL.setForeground(color);
    }
}
