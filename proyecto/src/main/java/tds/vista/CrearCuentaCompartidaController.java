package tds.vista;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import tds.Configuracion;
import tds.adapters.repository.exceptions.ElementoExistenteException;
import tds.adapters.repository.exceptions.ErrorPersistenciaException;
import tds.controlador.GestorGastos;
import tds.modelo.CuentaCompartida;

public class CrearCuentaController {

    @FXML private TextField campoNombreCuenta;
    @FXML private TextField propietario1;
    @FXML private TextField propietario2;
    @FXML private TextField propietario3;
    @FXML private TextField propietario4;
    @FXML private TextField propietario5;
    @FXML private TextField propietario6;

    private GestorGastos gestor;

    @FXML
    public void initialize() {
        gestor = Configuracion.getInstancia().getGestorGastos();
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    @FXML
    private void crearCuenta() {
        String nombreCuenta = campoNombreCuenta.getText().trim();
        // Recoger todos los nombres de propietarios introducidos        
        List<String> nombresPropietarios = Stream.of(propietario1, propietario2, propietario3, propietario4, propietario5, propietario6)
                .map(TextField::getText)      
                .map(String::trim)            
                .filter(s -> !s.isEmpty())    
                .collect(Collectors.toList()); 
        try {
        	if (nombreCuenta.isEmpty()) {
        		throw new IllegalArgumentException ("La cuenta debe tener un nombre");
            }
	        // Verificar que tengamos al menos 2 propietarios para una cuenta compartida
	        if (nombresPropietarios.size() < 2) {
	        	throw new IllegalArgumentException ("Una cuenta compartida ha de tener un mínimo de 2 propietarios y un máximo de 6 (hacer diálogo)");
	        }
	        // Llamar al gestor para crear la cuenta compartida
            CuentaCompartida nuevaCuenta = gestor.crearCuentaCompartidaConNombres(nombreCuenta, nombresPropietarios);

            // IMPORTANTE: Primero añadir la pestaña, luego cambiar de ventana
            VentanaPrincipalController controller = Configuracion.getInstancia().getSceneManager().getVentanaPrincipalController();
            if (controller != null) {
                controller.añadirPestañaCuentaCompartida(nuevaCuenta);
            }
            
            Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
            
        } catch (IllegalArgumentException e) {
            mostrarError("Datos inválidos", e.getMessage());
        } catch (ElementoExistenteException e) {
            mostrarError("Cuenta duplicada", "Ya existe una cuenta con ese nombre.");
        } catch (ErrorPersistenciaException e) {
            mostrarError("Error de guardado", "No se pudo guardar en base de datos.");
        } catch (Exception e) {
            mostrarError("Error inesperado", e.getMessage());
        }
    }
    
    @FXML
    private void atras() {
    	Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }
}