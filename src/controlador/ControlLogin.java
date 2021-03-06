package controlador;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import modelo.entities.Usuario;
import modelo.dao.UsuarioJpa;
import java.util.List;
import javax.swing.SwingWorker;
import vista.interfaces.VistaLogin;

/**
 *
 * @author JuanM
 */
public class ControlLogin implements ActionListener {

    VistaLogin vista;

    /**
     * Constructor que asocia la vista al controlador
     *
     * @param vista Vista del controlador (Interfaz)
     */
    public ControlLogin(VistaLogin vista) {
        this.vista = vista;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();

        if (command.equals(VistaLogin.INICIARSESION)) {
            WorkerLogin workerLogin = new WorkerLogin();
            workerLogin.execute();
        }
    }

    public class WorkerLogin extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            publish((Void) null);
            
            // Busca al usuario cuyo nick es el introducido en la vista
            UsuarioJpa usuariojpa = new UsuarioJpa();
            Usuario user = usuariojpa.findUsuariobyNick(vista.getUsuario());

            // Comprueba que el usuario exista y su contraseña sea correcta
            if (user != null && Arrays.equals(user.getPassword().toCharArray(),
                    vista.getContraseña())) {
                vista.estado("Login Correcto. Inicializando...", Color.BLUE);
                //Establece como usuario logeado a user
                Coordinador.getInstance().setUsuario(user);

                // Comunica al coordinador que el login ha sido correcto
                Coordinador.getInstance().loginOK();
            } else {
                vista.estado("Usuario/Contraseña incorreta", Color.RED);
            }

            return null;
        }

        @Override
        protected void process(List chunks) {
            vista.estado("Cargando...", Color.BLACK);
        }
    }

}
