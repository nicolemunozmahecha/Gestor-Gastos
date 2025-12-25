
package tds.vista;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
    @FXML private TextField propietario1;
    @FXML private TextField propietario2;
    @FXML private TextField propietario3;
    @FXML private TextField propietario4;
    @FXML private TextField propietario5;
    @FXML private TextField propietario6;

	
    @FXML private TableView<Persona> tablaDistribucion;
	@FXML private TableColumn<Persona, String> colNombre;
    @FXML private TableColumn<Persona, Integer> colDistribucion;
    
    
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
        double sumaActual = 0;
        for (Persona p : personas) {
            sumaActual += p.getPorcentaje();
        }

        // Solo si están a 0 (nunca se ha abierto antes) calculamos los iguales
        if (sumaActual == 0) {
            cargarPorcentajesIguales();
        }
        cargarPersonas();
        cargarLabels();
    }

	private void cargarPorcentajesIguales() {
        int totalPersonas = personas.size(); 
        
		System.out.println(totalPersonas);
		for (Persona p: personas) {
			int porcentaje = 100/totalPersonas;
			p.setPorcentaje(porcentaje);
		}
	}
	private void configurarTabla() {
    	// Configurar qué propiedad mostrar en cada columna
    	colNombre.setCellValueFactory(cellData -> 
        	new SimpleStringProperty(cellData.getValue().getNombre())); 
    
    	colDistribucion.setCellValueFactory(cellData -> 
	        new SimpleIntegerProperty(cellData.getValue().getPorcentaje()).asObject());
    }
	
	private void cargarPersonas() {
		tablaDistribucion.getItems().clear();
        tablaDistribucion.getItems().clear();
        tablaDistribucion.getItems().addAll(personas);
	}
	
	private void cargarLabels() {
		List<Label> etiquetas = new ArrayList<>();
		List<TextField> campos = new ArrayList<>();
		etiquetas.add(persona1);
		etiquetas.add(persona2);
		etiquetas.add(persona3);
		etiquetas.add(persona4);
		etiquetas.add(persona5);
		etiquetas.add(persona6);
		campos.add(propietario1);
		campos.add(propietario2);
		campos.add(propietario3);
		campos.add(propietario4);
		campos.add(propietario5);
		campos.add(propietario6);
		
		for (int i = 0; i < etiquetas.size(); i++) {
            Label label = etiquetas.get(i);
            TextField campo = campos.get(i);
            if (i < personas.size()) {
                // Si hay persona, ponemos el nombre
                label.setText(personas.get(i).getNombre());
                label.setVisible(true);
                campo.setVisible(true);
            } else {
                // Si no hay persona, ocultamos la label
                label.setText("");
                label.setVisible(false);
                campo.setVisible(false);
            }
        }
	}
    
    @FXML
    private void personalizarDistribucion() {
    	// comprobacion de campos
        // Recoger todos los nombres de propietarios introducidos
        List<String> nombresPropietarios = new ArrayList<>();
        
        if (!propietario1.getText().trim().isEmpty()) {
            nombresPropietarios.add(propietario1.getText().trim());
        }
        if (!propietario2.getText().trim().isEmpty()) {
            nombresPropietarios.add(propietario2.getText().trim());
        }
        if (!propietario3.getText().trim().isEmpty()) {
            nombresPropietarios.add(propietario3.getText().trim());
        }
        if (!propietario4.getText().trim().isEmpty()) {
            nombresPropietarios.add(propietario4.getText().trim());
        }
        if (!propietario5.getText().trim().isEmpty()) {
            nombresPropietarios.add(propietario5.getText().trim());
        }
        if (!propietario6.getText().trim().isEmpty()) {
            nombresPropietarios.add(propietario6.getText().trim());
        }

        if (nombresPropietarios.size() < personas.size() || nombresPropietarios.size() > personas.size()) {
        	System.out.println("La cuenta compartida tiene " + personas.size() + " propietarios");
            return;
        }
        // comprobar que la suma de porcentajes sume 100%
        double sumaTotal = 0.0;
        List<TextField> campos = Arrays.asList(propietario1, propietario2, propietario3, propietario4, propietario5, propietario6);

        for (int i = 0; i < personas.size(); i++) {
            TextField txt = campos.get(i);
            
            // Intentamos leer el número
            int valor = Integer.parseInt(txt.getText());
            
            if (valor < 0) {
            	System.out.println("Error Los porcentajes no pueden ser negativos.");
            }
            
            // Actualizamos el objeto Persona temporalmente
            personas.get(i).setPorcentaje(valor);
            sumaTotal += valor;
        }
        
        if (Math.abs(sumaTotal - 100.0) > 0.01) {
        	System.out.println("Error de Distribución. Los porcentajes deben sumar 100%. \nSuma actual: " + String.format("%.2f", sumaTotal) + "%");
            return;
        }

        // Si llegamos aquí, todo es correcto. Actualizamos tabla y salimos.
        configurarTabla();
        
    	Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }
  
        
    @FXML
    private void atras() {
    	Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }
   
}

