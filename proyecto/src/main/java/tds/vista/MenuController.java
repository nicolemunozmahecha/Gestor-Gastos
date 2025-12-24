package tds.vista;

import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class MenuController {
    
    // Elementos del menú
    @FXML private MenuItem menuCrearCuenta;
    @FXML private Menu menuEliminarCuenta;
    @FXML private MenuItem menuTotalCuenta;
    @FXML private MenuItem menuFiltrarGastos;
    @FXML private MenuItem menuCrearCategoria;
    @FXML private Menu menuEliminarCategoria;
    @FXML private MenuItem menuCrearAlerta;
    @FXML private Menu menuEliminarAlerta;
    @FXML private MenuItem menuHistorialNotificaciones;
    @FXML private MenuItem menuSalir;
    
    // Referencia al controlador principal
    private VentanaPrincipalController controladorPrincipal;
    
    @FXML
    public void initialize() {
    }
    
    public void setControladorPrincipal(VentanaPrincipalController controlador) {
        this.controladorPrincipal = controlador;
        configurarEventos();
    }
    
    
    public Menu getMenuEliminarCuenta() {
		return menuEliminarCuenta;
	}

	public Menu getMenuEliminarCategoria() {
		return menuEliminarCategoria;
	}

	public Menu getMenuEliminarAlerta() {
		return menuEliminarAlerta;
	}

	private void configurarEventos() {
        if (menuSalir != null) {
            menuSalir.setOnAction(e -> controladorPrincipal.salirAplicacion());
        }
        
        if (menuCrearCuenta != null) {
            menuCrearCuenta.setOnAction(e -> controladorPrincipal.crearCuenta());
        }
        
        if (menuEliminarCuenta != null) {
        	menuEliminarCuenta.setOnShowing(e -> {
                System.out.println("¡¡¡ EVENTO setOnShowing EJECUTADO !!!");
                controladorPrincipal.eliminarCuenta();
            });
        }
        
        if (menuTotalCuenta != null) {
            menuTotalCuenta.setOnAction(e -> controladorPrincipal.totalCuenta());
        }
        
        if (menuFiltrarGastos != null) {
            menuFiltrarGastos.setOnAction(e -> controladorPrincipal.filtrarGastos());
        }
        
        if (menuCrearAlerta != null) {
            menuCrearAlerta.setOnAction(e -> controladorPrincipal.crearAlerta());
        }
        
        if (menuEliminarAlerta != null) {
            menuEliminarAlerta.setOnShowing(e -> controladorPrincipal.eliminarAlerta());
        }
        
        if (menuCrearCategoria != null) {
            menuCrearCategoria.setOnAction(e -> controladorPrincipal.crearCategoria());
        }
        
        if (menuEliminarCategoria != null) {
            menuEliminarCategoria.setOnAction(e -> controladorPrincipal.eliminarCategoria());
        }
        
        if (menuHistorialNotificaciones != null) {
            menuHistorialNotificaciones.setOnAction(e -> controladorPrincipal.mostrarHistorial());
        }
    }
}