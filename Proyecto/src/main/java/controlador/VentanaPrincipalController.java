package controlador;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

public class VentanaPrincipalController {
    
    @FXML private TabPane tabPane;
    @FXML private MenuItem menuCrearCuenta;
    @FXML private MenuItem menuEliminarCuenta;
    @FXML private MenuItem menuMisCuentas;
    @FXML private MenuItem menuCrearCategoria;
    @FXML private MenuItem menuEliminarCategoria;
    @FXML private MenuItem menuCrearAlerta;
    @FXML private MenuItem menuEliminarAlerta;
    @FXML private MenuItem menuHistorialNotificaciones;
    @FXML private MenuItem menuSalir;
    
    @FXML private MenuButton btnCrearGasto;
    @FXML private MenuItem menuGastoNuevo;
    @FXML private MenuItem menuImportarGasto;
    @FXML private MenuButton btnVisualizacion;
    @FXML private MenuItem menuVerTabla;
    @FXML private MenuItem menuVerGrafica;
    
    @FXML
    public void initialize() {
        System.out.println("Ventana principal inicializada desde el paquete controlador");
        configurarEventos();
    }
    
    private void configurarEventos() {

    	
        menuSalir.setOnAction(e -> salirAplicacion());
        menuCrearCuenta.setOnAction(e -> crearCuenta());
        menuEliminarCuenta.setOnAction(e -> eliminarCuenta());
        menuMisCuentas.setOnAction(e -> mostrarMisCuentas());
        menuCrearCategoria.setOnAction(e -> crearCategoria());
        menuEliminarCategoria.setOnAction(e -> eliminarCategoria());
        menuCrearAlerta.setOnAction(e -> crearAlerta());
        menuEliminarAlerta.setOnAction(e -> eliminarAlerta());
        menuHistorialNotificaciones.setOnAction(e -> mostrarHistorial());
        menuGastoNuevo.setOnAction(e -> crearGastoNuevo());
        menuImportarGasto.setOnAction(e -> importarGasto());
        menuVerTabla.setOnAction(e -> mostrarTabla());
        menuVerGrafica.setOnAction(e -> mostrarGrafica());
    }
    
    private void salirAplicacion() {
        Alert confirmacion = new Alert(AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar salida");
        confirmacion.setHeaderText("¿Está seguro que desea salir?");
        confirmacion.setContentText("Se guardarán todos los cambios.");
        
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                System.out.println("Guardando datos antes de salir...");
                javafx.application.Platform.exit();
            }
        });
    }
    
    private void crearCuenta() {
        System.out.println("TODO: Implementar crear cuenta");
    }
    
    private void eliminarCuenta() {
        System.out.println("TODO: Implementar eliminar cuenta");
    }
    
    private void mostrarMisCuentas() {
        System.out.println("TODO: Implementar mostrar mis cuentas");
    }
    
    private void crearCategoria() {
        System.out.println("TODO: Implementar crear categoría");
    }
    
    private void eliminarCategoria() {
        System.out.println("TODO: Implementar eliminar categoría");
    }
    
    private void crearAlerta() {
        System.out.println("TODO: Implementar crear alerta");
    }
    
    private void eliminarAlerta() {
        System.out.println("TODO: Implementar eliminar alerta");
    }
    
    private void mostrarHistorial() {
        System.out.println("TODO: Implementar mostrar historial");
    }
    
    private void crearGastoNuevo() {
        System.out.println("TODO: Implementar crear gasto nuevo");
    }
    
    private void importarGasto() {
        System.out.println("TODO: Implementar importar gasto");
    }
    
    private void mostrarTabla() {
        System.out.println("TODO: Implementar mostrar tabla");
    }
    
    private void mostrarGrafica() {
        System.out.println("TODO: Implementar mostrar gráfica");
    }
}