package tds.vista;

import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

import tds.Configuracion;
import tds.controlador.GestorGastos;

public class CrearCuentaController {

    @FXML private TextField campoNombreCuenta;
    @FXML private TextField propietario1;
    @FXML private TextField propietario2;
    @FXML private TextField propietario3;
    @FXML private TextField propietario4;
    @FXML private TextField propietario5;
    @FXML private TextField propietario6;

    @FXML private Button btnCrearCuenta;

    private GestorGastos gestor;

    @FXML
    public void initialize() {
        gestor = Configuracion.getInstancia().getGestorGastos();
    }

    @FXML
    private void crearCuenta() {

        String nombreCuenta = campoNombreCuenta.getText().trim();
        if (nombreCuenta.isEmpty()) {
            return;
        }

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

        // Verificar que tengamos al menos 2 propietarios para una cuenta compartida
        if (nombresPropietarios.size() < 2 || nombresPropietarios.size() > 6) {
        	System.out.println("Una cuenta compartida ha de tener un mínimo de 2 propietarios y un máximo de 6 (hacer diálogo)");
            return;
        }


        // Llamar al gestor para crear la cuenta compartida
        gestor.crearCuentaCompartidaConNombres(nombreCuenta, nombresPropietarios);

        // Volver a la ventana principal
        Configuracion.getInstancia().getSceneManager().showVentanaCompartida();
    }
    @FXML
    private void atras() {
    	Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }
}