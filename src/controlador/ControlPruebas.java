package controlador;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JOptionPane;
import modelo.Competicion;
import modelo.Compuesta;
import modelo.Prueba;
import modelo.Registro;
import jpa.CompuestaJpa;
import jpa.PruebaJpa;
import jpa.RegistroJpa;
import jpa.exceptions.NonexistentEntityException;
import vista.GeneralTab;
import vista.VistaPrincipal;

/**
 *
 * @author JuanM
 */
public class ControlPruebas implements ActionListener {

    private GeneralTab vista;

    public ControlPruebas(GeneralTab vista) {
        this.vista = vista;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();
        switch (command) {
            case VistaPrincipal.CREARPRUEBA:
                Prueba prueba = crearPrueba(vista.getNombrePrueba(),
                        vista.getTipoPrueba(), vista.getTipoResultado());
                if (prueba != null) {
                    // Actualizamos la vista
                    vista.añadirPruebaATabla(new Object[]{
                    prueba.getId(),
                    prueba.getNombre(),
                    prueba.getTipo(),
                    prueba.getTiporesultado()});
                    Coordinador.getInstance().setEstadoLabel(
                            "Prueba creada correctamente", Color.BLUE);
                    vista.limpiarFormularioPrueba();
                } else {
                    Coordinador.getInstance().setEstadoLabel(
                            "Nombre de prueba incorrecto", Color.RED);
                }
                break;
            case VistaPrincipal.ELIMINARPRUEBA:
                if (vista.getPruebaSelected() != -1) {
                    int confirmDialogPrueba = JOptionPane.showConfirmDialog(
                            null,
                            "¿Está seguro de que desea eliminar la prueba seleccionada?",
                            "Aviso",
                            JOptionPane.YES_NO_OPTION);
                    if (confirmDialogPrueba == JOptionPane.YES_OPTION) {
                        if (eliminarPrueba(
                                vista.getPruebaSelected(),
                                Coordinador.getInstance().getSeleccionada().getId())) {
                            vista.eliminarPrueba();
                            vista.limpiarFormularioPrueba();
                            Coordinador.getInstance().setEstadoLabel(
                                    "Prueba eliminada correctamente", 
                                    Color.BLUE);
                        } else {
                            Coordinador.getInstance().setEstadoLabel(
                                    "No se pudo eliminar la prueba seleccionada",
                                    Color.RED);
                        }
                    }
                }
                break;
            case VistaPrincipal.MODIFICARPRUEBA:
                if (modificarPrueba(
                        vista.getPruebaSelected(),
                        Coordinador.getInstance().getSeleccionada().getId())) {
                    vista.eliminarPrueba();
                    Coordinador.getInstance().setEstadoLabel(
                            "Prueba modificada correctamente", Color.BLUE);
                } else {
                    Coordinador.getInstance().setEstadoLabel(
                            "No se pudo modificar la prueba seleccionada",
                            Color.RED);
                }
                break;
            case VistaPrincipal.LIMPIARPRUEBA:
                vista.limpiarFormularioPrueba();
                break;

        }
    }

    
    /**Crea una prueba con los datos de la vista
     * @param nombre        Nombre de la prueba
     * @param tipoResultado Tipo de resultado (Distancia, Tiempo, Numerica)
     * @param tipoPrueba    Individual o Equipo
     * @return true si la prueba ha sido creada correctamente
     */
    public static Prueba crearPrueba(String nombre, String tipoPrueba,
            String tipoResultado) {

        // Comprueba que el nombre de la prueba es no vacío y que hay
        // una competición seleccionada
        if (nombre.length() > 0 &&
                Coordinador.getInstance().getSeleccionada() != null) {
            
            CompuestaJpa compujpa = new CompuestaJpa();
            PruebaJpa prujpa = new PruebaJpa();

            // Comprueba que el nombre de la prueba esté disponible
            // en la competicion seleccionada
            if (!existePrueba(nombre,
                    Coordinador.getInstance().getSeleccionada())) {
                
                // Creamos una prueba con sus datos correspondientes
                Prueba p = new Prueba();
                p.setNombre(nombre);
                
                TipoResultado tresultado;
                TipoPrueba tprueba;
                try{
                    tresultado = TipoResultado.valueOf(tipoResultado);
                    tprueba = TipoPrueba.valueOf(tipoPrueba);
                }catch(IllegalArgumentException ie){
                    return null;
                }
                p.setTipo(tprueba.toString());
                p.setTiporesultado(tresultado.toString());
                prujpa.create(p);

                // Asociamos la prueba con la competición
                Compuesta compuesta = new Compuesta();
                compuesta.setCompeticionId(
                        Coordinador.getInstance().getSeleccionada());
                // El orden las pruebas no se utiliza actualmente
                compuesta.setOrden(1);
                compuesta.setPruebaId(p);
                compujpa.create(compuesta);

                return p;
            }
        }
        return null;
    }

    private boolean modificarPrueba(Integer pruebaid, Integer competicionid) {
        
        // Comprobamos que se ha seleccionado una prueba, el nombre de la 
        // prueba es no vacío y la prueba seleccionada no tiene registros asociados
        if (pruebaid != -1 && vista.getNombrePrueba().length() > 0){
            PruebaJpa pruebajpa = new PruebaJpa();

            // Buscamos la prueba que vamos a modificar
            Prueba prueba = pruebajpa.findPrueba(pruebaid);

            // Comprobamos que el nombre de la prueba no esta cogido
            Prueba pruebaMod = pruebajpa.findPruebaByNombreCompeticion(
                    vista.getNombrePrueba().toString(),
                    competicionid);
            if (pruebaMod == null || pruebaMod.getId() == prueba.getId()) {
                
                // Establecemos los atributos a partir de los datos de la vista
                prueba.setNombre(vista.getNombrePrueba().toString());
                
                // Comprobamos que la prueba no tiene registros asocidados
                // En caso de tener registros no se podrá modificar el tipo de prueba
                // ni el tipo de resultado.
                if(pruebajpa.countRegistrosByPrueba(pruebaid) <= 0){
                    
                    prueba.setTiporesultado(vista.getTipoResultado());
                    prueba.setTipo(vista.getTipoPrueba());
                    
                }else{
                    // No se puede modificar una prueba con registros asociados (salvo el nombre)
                    return false;
                }
                
                
                try {
                    pruebajpa.edit(prueba);
                } catch (NonexistentEntityException ex) {
                    return false;
                } catch (Exception ex) {
                    return false;
                }
                // Actualizamos la vista
                vista.añadirPruebaATabla(new Object[]{
                    prueba.getId(),
                    prueba.getNombre(),
                    prueba.getTipo(),
                    prueba.getTiporesultado()});
                return true;
            }
        }
        return false;
    }

    /**Elimina una prueba de la competición pasada como parámetro
     * 
     * @param pruebaid Identificador de la prueba a eliminar
     * @param competicionid  Identificador de la competicion
     * @return true si la prueba ha sido eliminada correctamente
     */
    private boolean eliminarPrueba(Integer pruebaid, Integer competicionid) {
        
        // Comprobamos que el id de la prueba es válido
        if (pruebaid != -1) {
            
            CompuestaJpa compuestajpa = new CompuestaJpa();
            PruebaJpa pruebajpa = new PruebaJpa();
            RegistroJpa registrojpa = new RegistroJpa();
            
            try {
                // Eliminamos la prueba de la competición
                Compuesta c = compuestajpa.findCompuestaByPrueba_Competicion(
                        pruebaid, competicionid);
                compuestajpa.destroy(c.getId());

                // Eliminamos todos los registros de esa prueba
                List<Registro> registros = registrojpa.findByPrueba(pruebaid);
                for (Registro r : registros) {
                    registrojpa.destroy(r.getId());
                }
                // Eliminamos la prueba
                pruebajpa.destroy(pruebaid);
            } catch (jpa.exceptions.NonexistentEntityException ex) {
                return false;
            } catch (jpa.exceptions.IllegalOrphanException ex) {
                return false;
            }
            return true;
        } else {
            return false;
        }

    }

    /**Devuelve true si ya existe una prueba con ese nombre en la competicion
     *  
     * @param nombre
     * @return true si el nombre de la prueba ya existe en la competicion  
     */
    private static boolean existePrueba(String nombre, Competicion competicion) {
        PruebaJpa prujpa = new PruebaJpa();
        return prujpa.findPruebaByNombreCompeticion(
                nombre,
                competicion.getId()) != null;
    }
}
