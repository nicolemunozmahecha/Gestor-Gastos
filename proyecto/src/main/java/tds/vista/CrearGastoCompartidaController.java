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
import tds.adapters.repository.exceptions.ElementoExistenteException;
import tds.adapters.repository.exceptions.ErrorPersistenciaException;
import tds.controlador.GestorGastos;
import tds.modelo.Categoria;
import tds.modelo.Cuenta;
import tds.modelo.Gasto;
import tds.modelo.Persona;

public class CrearGastoCompartidaController {
	
	@FXML private TextField campoNombreGasto;
	@FXML private TextField campoCantidad;
	@FXML private DatePicker campoFechaGasto;
	@FXML private TextArea campoDescripcion;
	@FXML private MenuButton categorias;
	@FXML private MenuButton personas;

	private GestorGastos gestor;
	private List<Categoria> categoriasDisp;
	private double cantidadFinal;
	private Persona p;
	private CuentaCompartidaController cuentaCompartidaController;
	private final String TEXTO_DEFECTO = "Persona";
	private final String TEXTO_DEFECTO2 = "Categoria";
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
	
    // PARA CARGAR TODAS LAS CATEGORIAS
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
		                categorias.setText(TEXTO_DEFECTO2);
		            }
		        });

	            categorias.getItems().add(item);
	        }
	    }
    
    private boolean ningunaSeleccionada() {
        return personas.getItems().stream()
                .map(item -> (CheckMenuItem) item)               // Convertimos a CheckMenuItem
                .noneMatch(CheckMenuItem::isSelected);          // Ninguno está seleccionado
    }
	
    private void cargarPersonas() {
    	// necesitamos acceder a la cuenta y de ahi a las personas de dicha cuenta:
	    personas.getItems().clear();

	    List<Persona> lista = cuentaCompartidaController.getPersonasCuenta();

	    for (Persona persona : lista) {
	        CheckMenuItem item = new CheckMenuItem(persona.getNombre());
	        item.setUserData(persona);

	        item.setOnAction(e -> {
	            if (item.isSelected()) {
	                // deseleccionar las demás
	                for (MenuItem mi : personas.getItems()) {
	                    if (mi != item && mi instanceof CheckMenuItem) {
	                        ((CheckMenuItem) mi).setSelected(false);
	                    }
	                }
	                p = persona; // pagador seleccionado
	                personas.setText(item.getText());

	            } else {
	                p = null;
	                personas.setText(TEXTO_DEFECTO);
	            }
	        });

	        personas.getItems().add(item);
	    }
    }
    
    public void setCuentaCompartidaController(CuentaCompartidaController controller) {
        this.cuentaCompartidaController = controller;
        cargarPersonas();
    }
    
    private void mostrarError(String mensaje) {
        new Alert(Alert.AlertType.ERROR, mensaje).showAndWait();
    }
    
    @FXML
    private void crearGasto() throws ElementoExistenteException, ErrorPersistenciaException {
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
        
       	 	if (ningunaSeleccionada())  {
		        throw new IllegalArgumentException ("El gasto debe tener un pagador");
       	 	}   
        }catch(IllegalArgumentException e){
       	 	new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
            return;
        }
        String cuenta = cuentaCompartidaController.getCuenta().getNombre();
        Gasto g = gestor.crearGasto(nombre, cantidadFinal, fecha, descripcion, cat, p, cuenta);
        if (cuentaCompartidaController != null) {
            boolean exito = gestor.agregarGastoACuenta(cuentaCompartidaController.getCuenta(), g);
            if (exito) {
                cuentaCompartidaController.añadirGastoTabla(g);
                
                // CRÍTICO: Recargar la cuenta desde el repositorio
                try {
                    Cuenta cuentaActualizada = gestor.getCuentaPorNombre(cuentaCompartidaController.getCuenta().getNombre());
                    cuentaCompartidaController.setCuenta(cuentaActualizada);
                } catch (Exception e) {
                	mostrarError("ERROR: Error al cargar la cuenta");
                    e.printStackTrace();
                }
            } else {
                mostrarError("ERROR: No se pudo añadir el gasto a cuenta compartida");
            }
        } else {
            mostrarError("ERROR: No se pudo conectar con la pantalla anterior"); // controller null
        } 
        Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }

    @FXML
    private void atras() {
    	Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }
}

