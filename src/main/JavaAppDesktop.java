package main;

import controlador.ControlRegistros;
import controlador.Coordinador;
import dao.RegistroJpa;
import java.sql.SQLException;
import java.util.List;
import modelo.Registro;
/**
 *
 * @author JuanM
 */
public class JavaAppDesktop {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException {
        
        Coordinador c = Coordinador.getInstance();

        /*RegistroJpa registroJpa = new RegistroJpa();
        List<Registro> registros;
        List<Double> marcas;
        List<Object[]> objects = registroJpa.temp(1,true);
        System.out.println(objects.toString());*/
    }
    
}
