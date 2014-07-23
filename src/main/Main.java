package main;

import controlador.Coordinador;
/**
 *
 * @author JuanM
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Coordinador c = Coordinador.getInstance();
        c.inicializar();

    }
    
}
