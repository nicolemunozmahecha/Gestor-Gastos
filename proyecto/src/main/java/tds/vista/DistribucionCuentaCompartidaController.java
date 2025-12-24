
package tds.vista;

import java.util.List;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import tds.Configuracion;
import tds.controlador.GestorGastos;
import tds.modelo.Persona;

public class DistribucionCuentaCompartidaController {
	private GestorGastos gestor;
	@FXML private Label persona1;
	@FXML private Label persona2;
	@FXML private Label persona3;
	@FXML private Label persona4;
	@FXML private Label persona5;
	@FXML private Label persona6;
    @FXML private TableView<Persona> tablaDistribucion;
	@FXML private TableColumn<Persona, String> colNombre;
    @FXML private TableColumn<Persona, Double> colDistribucion;
    
    
    private List<Persona> personas;
	private CuentaCompartidaController cuentaCompartidaController;

	@FXML
    public void initialize() {
        gestor = Configuracion.getInstancia().getGestorGastos();
        configurarTabla();
    }
	
	public void setCuentaCompartidaController(CuentaCompartidaController controller) {
        this.cuentaCompartidaController = controller;
        this.personas = cuentaCompartidaController.getPersonasCuenta();
        cargarPorcentajesIguales();
        cargarPersonas();
    }

	private void cargarPorcentajesIguales() {
        int totalPersonas = personas.size(); 
        
		System.out.println(totalPersonas);
		for (Persona p: personas) {
			double porcentaje = 100.0/totalPersonas;
			p.setPorcentaje(porcentaje);
		}
	}
	private void configurarTabla() {
    	// Configurar qué propiedad mostrar en cada columna
    	colNombre.setCellValueFactory(cellData -> 
        	new SimpleStringProperty(cellData.getValue().getNombre())); 
    
    	colDistribucion.setCellValueFactory(cellData -> 
	        new SimpleDoubleProperty(cellData.getValue().getPorcentaje()).asObject());
    }
    

	// QUEREMOS QUE PARA CADA CUENTA APAREZCAN EN LA TABLA LA DISTRIBUCION DE CADA MIEMBRO Y ADEMAS 
	// QUE APAREZCAN LOS NOMNRES EN LAS LABELS
	// PRIMERO CARGAMOS PERSONAS EN TABLA CON DISTRIBUCIONES EQUITATIVAS
	private void cargarPersonas() {
		tablaDistribucion.getItems().clear();
		gestor = Configuracion.getInstancia().getGestorGastos();
        tablaDistribucion.getItems().clear();
        tablaDistribucion.getItems().addAll(personas);
	}
    
    @FXML
    private void personalizarDistribucion() {
    	Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }
    @FXML
    private void atras() {
    	Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }
   
}

