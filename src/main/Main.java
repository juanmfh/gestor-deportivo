package main;

import controlador.Coordinador;
/**
 *
 * @author JuanM
 */
public class Main {


    public static void main(String[] args) {

        Coordinador c = Coordinador.getInstance();
        c.inicializar();

    }
    
}
