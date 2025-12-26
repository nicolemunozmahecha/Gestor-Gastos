package tds.vista;

import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import tds.Configuracion;
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

    @FXML
    private void crearCuenta() {
        String nombreCuenta = campoNombreCuenta.getText().trim();
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

        try {
        	if (nombreCuenta.isEmpty()) {
        		throw new IllegalArgumentException ("La cuenta debe tener un nombre");
            }
	        // Verificar que tengamos al menos 2 propietarios para una cuenta compartida
	        if (nombresPropietarios.size() < 2 || nombresPropietarios.size() > 6) {
	        	throw new IllegalArgumentException ("Una cuenta compartida ha de tener un mínimo de 2 propietarios y un máximo de 6 (hacer diálogo)");
	        }
        }catch(IllegalArgumentException e){
       	 	new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
            return;
        }

        // Llamar al gestor para crear la cuenta compartida
        // Crear la cuenta
        CuentaCompartida nuevaCuenta = gestor.crearCuentaCompartidaConNombres(nombreCuenta, nombresPropietarios);
        
        // IMPORTANTE: Primero añadir la pestaña, luego cambiar de ventana
        VentanaPrincipalController controller = Configuracion.getInstancia().getSceneManager().getVentanaPrincipalController();
        
        if (controller != null) {
            controller.añadirPestañaCuentaCompartida(nuevaCuenta);
        } else {
            System.err.println("ERROR: Controller es null");
        }
        
        // Después volver a la ventana principal
        Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
        
    }
    @FXML
    private void atras() {
    	Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }
}