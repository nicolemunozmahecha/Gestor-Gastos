package tds.vista;

import java.time.LocalDate;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import tds.Configuracion;
import tds.controlador.GestorGastos;
import tds.modelo.Categoria;
import tds.modelo.CuentaPersonal;
import tds.modelo.impl.CuentaPersonalImpl;
import tds.modelo.impl.GastoImpl;

public class CrearGastoController {
	
	@FXML private TextField campoNombreGasto;
	@FXML private TextField campoCantidad;
	@FXML private DatePicker campoFechaGasto;
	@FXML private TextArea campoDescripcion;
	@FXML private MenuButton categorias;

	private final String TEXTO_DEFECTO = "Categoria";
	private GestorGastos gestor;
	private List<Categoria> categoriasDisp;
	private double cantidadFinal;
	private Categoria cat;

	@FXML
    public void initialize() {
        gestor = Configuracion.getInstancia().getGestorGastos();
        cargarCategorias();
    }
	
    
	private boolean ningunoSeleccionado() {
        return categorias.getItems().stream()
                .map(item -> (CheckMenuItem) item)               // Convertimos a CheckMenuItem
                .noneMatch(CheckMenuItem::isSelected);          // Ninguno está seleccionado
    }
	
    // PARA CARGAR LAS CATEGORIAS NUEVAS, Y QUE SOLO DEJE SELECCIONAR UNA
    public void cargarCategorias() {
    	categorias.getItems().clear();
        categoriasDisp = gestor.getCategorias();
        for (Categoria c : categoriasDisp) {
            CheckMenuItem item = new CheckMenuItem(c.getNombre());
            item.setUserData(c);

            item.setOnAction(e -> {
	            if (item.isSelected()) {
	                // deseleccionar las demás
	                for (MenuItem mi : categorias.getItems()) {
	                    if (mi != item && mi instanceof CheckMenuItem) {
	                        ((CheckMenuItem) mi).setSelected(false);
	                    }
	                }
	                cat = c;
	                categorias.setText(item.getText());

	            } else {
	                cat = null;
	                categorias.setText(TEXTO_DEFECTO);
	            }
	        });

            categorias.getItems().add(item);
        }
    }
    
    
    @FXML
    private void crearGasto() {
    	 String nombre = campoNombreGasto.getText().trim();
         String descripcion = campoDescripcion.getText().trim();
         LocalDate fecha = campoFechaGasto.getValue();
         String cantidad = campoCantidad.getText().trim();

         try {
         	if(nombre.isEmpty()) {
         		throw new IllegalArgumentException ("El gasto debe tener un nombre");
 	    	}   
 	    	if (cantidad.isEmpty())  {
 		        throw new IllegalArgumentException ("El gasto debe tener un importe");
 	    	}  
 	        // Verificar si es un número
 	        try {
 	            cantidadFinal = Double.parseDouble(cantidad);
 	        } catch (NumberFormatException e) {
 	        	new Alert(Alert.AlertType.ERROR, "Por favor, introduzca un número entero válido").showAndWait();
	            return;
 	        }
    	 	if (fecha == null)  {
	        throw new IllegalArgumentException ("El gasto debe tener  una fecha");
    	 	} 
     
    	 	if (ningunoSeleccionado())  {
	        throw new IllegalArgumentException ("El gasto debe tener una categoria");
    	 	}   
         
        	 	   
         }catch(IllegalArgumentException e){
        	 	new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
             return;
         }
        
        
        // Añadir gasto, luego cambiar de ventana
        VentanaPrincipalController controller = Configuracion.getInstancia().getSceneManager().getVentanaPrincipalController();
        
        if (descripcion.isEmpty()) {
            GastoImpl g = (GastoImpl) gestor.crearGasto(nombre, cantidadFinal, fecha, "", cat);
            if (controller != null) {
                // Añadir a la cuenta Y persistir el cambio
                boolean exito = gestor.agregarGastoACuenta(controller.getCuenta(), g);
                if (exito) {
                    System.out.println("DEBUG: Gasto añadido exitosamente");
                    // Actualizar la tabla visual
                    controller.añadirGastoTabla(g);
                    
                    // Recargar la cuenta desde el repositorio
                    try {
                        CuentaPersonal cuentaActualizada = (CuentaPersonal) gestor.getCuentaPorNombre(controller.getCuenta().getNombre());
                        controller.setCuenta(cuentaActualizada);
                        Configuracion.getInstancia().getSceneManager().setPrincipal((CuentaPersonalImpl) cuentaActualizada);
                        System.out.println("DEBUG: Cuenta recargada. Total gastos: " + cuentaActualizada.getNumeroGastos());
                    } catch (Exception e) {
                        System.err.println("Error al recargar cuenta: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("ERROR: No se pudo añadir el gasto");
                }
            } else {
                System.err.println("ERROR: Controller es null");
            }
        }else {
            GastoImpl g = (GastoImpl) gestor.crearGasto(nombre, cantidadFinal, fecha, descripcion, cat);
            if (controller != null) {
                // Añadir a la cuenta Y persistir el cambio
                boolean exito = gestor.agregarGastoACuenta(controller.getCuenta(), g);
                if (exito) {
                    System.out.println("DEBUG: Gasto añadido exitosamente");
                    // Actualizar la tabla visual
                    controller.añadirGastoTabla(g);
                    
                    // Recargar la cuenta desde el repositorio
                    try {
                        CuentaPersonal cuentaActualizada = (CuentaPersonal) gestor.getCuentaPorNombre(controller.getCuenta().getNombre());
                        controller.setCuenta(cuentaActualizada);
                        Configuracion.getInstancia().getSceneManager().setPrincipal((CuentaPersonalImpl) cuentaActualizada);
                        System.out.println("DEBUG: Cuenta recargada. Total gastos: " + cuentaActualizada.getNumeroGastos());
                    } catch (Exception e) {
                        System.err.println("Error al recargar cuenta: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("ERROR: No se pudo añadir el gasto");
                }
            } else {
                System.err.println("ERROR: Controller es null");
            }
        }
        Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }
    
    @FXML
    private void atras() {
    	Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }
}
