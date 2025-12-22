package tds.vista;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import tds.Configuracion;
import tds.controlador.GestorGastos;
import tds.modelo.Categoria;
import tds.modelo.Persona;
import tds.modelo.impl.CategoriaImpl;
import tds.modelo.impl.GastoImpl;

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
    private void cargarCategorias() {
        categoriasDisp = gestor.getCategoriasPersonalizadas();
        for (Categoria c : categoriasDisp) {
            final CheckMenuItem item = new CheckMenuItem(c.getNombre());
            item.setUserData(c);
            categorias.getItems().add(item);
        }
        for (MenuItem m : categorias.getItems()) {
            m.setOnAction(e -> {
			                if (((CheckMenuItem) m).isSelected()) {
			                    for (MenuItem mi : categorias.getItems()) {
			                        if (mi != m && mi instanceof CheckMenuItem) {
			                            ((CheckMenuItem) mi).setSelected(false);
			                        }
			                    }
			                }
			});
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
	            } else {
	                p = null;
	            }
	        });

	        personas.getItems().add(item);
	    }
    }
    
    public void setCuentaCompartidaController(CuentaCompartidaController controller) {
        this.cuentaCompartidaController = controller;
        cargarPersonas();
    }
    
    @FXML
    private void crearGasto() {
    	String nombre = campoNombreGasto.getText().trim();
    	if(nombre.isEmpty()) {
    		System.out.println("El gasto debe tener un nombre");
            return;
    	}
    	
    	String cantidad = campoCantidad.getText().trim();
        if (cantidad.isEmpty()) {
        	System.out.println("El gasto debe tener un importe");
            return;
        }
        
        // Verificar si es un número
        try {
        	cantidadFinal = Double.parseDouble(cantidad);
        } catch (NumberFormatException e) {
            System.out.println("Por favor ingrese un número válido");
            return;
        }
        
        LocalDate fecha = campoFechaGasto.getValue();
        if (fecha == null) {
        	System.out.println("El gasto debe tener una fecha");
            return;
        }
        
        // FALTA VER COMO HACER LO DE LAS PERSONAS
        if(ningunaSeleccionada()) {
        	System.out.println("El gasto debe tener un pagador");
        	return;
        }
        
        if(ningunoSeleccionado()) {
        	System.out.println("El gasto debe tener una categoria");
        	return;
        }else {
        	// Obtenemos lista de categorias, y solo queremos una, SOLO SE ELIGE 1 CATEGORIA
        	Optional<CheckMenuItem> c = categorias.getItems().stream()
        			.filter(item -> item instanceof CheckMenuItem)
        	        .map(item -> (CheckMenuItem) item)
        	        .filter(CheckMenuItem::isSelected)
        	        .findFirst();
        	// creamos categoria nueva para la alerta, pero YA EXISTE en la lista de categorias
        	CheckMenuItem item = c.get();
            Categoria cat = new CategoriaImpl(item.getText()); 
            String descripcion = campoDescripcion.getText().trim();
            
        	if (descripcion.isEmpty()) {
        		GastoImpl g = (GastoImpl) gestor.crearGasto(nombre, cantidadFinal, fecha, "", cat, p);
                if (cuentaCompartidaController != null) {
                	cuentaCompartidaController.añadirGastoTabla(g);
                } else {
                    System.err.println("ERROR: Controller es null");
                }        	
            }else {
        		GastoImpl g = (GastoImpl) gestor.crearGasto(nombre, cantidadFinal, fecha, descripcion, cat, p);
                if (cuentaCompartidaController != null) {
                	cuentaCompartidaController.añadirGastoTabla(g);
                } else {
                    System.err.println("ERROR: Controller es null");
                }
        	}
        }
        
    	Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }
    @FXML
    private void atras() {
    	Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }
}

