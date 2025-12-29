package tds.vista;

import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import tds.Configuracion;

public class MenuController {
    
    // Elementos del menú
    @FXML private MenuItem menuCrearCuenta;
    @FXML private Menu menuEliminarCuenta;
    @FXML private MenuItem menuTotalCuenta;
    @FXML private MenuItem menuFiltrarGastos;
    @FXML private MenuItem menuImportarGastos;
    @FXML private MenuItem menuCrearCategoria;
    @FXML private Menu menuEliminarCategoria;
    @FXML private Menu menuAlertas;
    @FXML private MenuItem menuCrearAlerta;
    @FXML private MenuItem menuVerAlertas;
    @FXML private Menu menuEliminarAlerta;
    @FXML private MenuItem menuHistorialNotificaciones;
    @FXML private Menu menuNotificaciones;
    @FXML private MenuItem menuSalir;

    private String textoBaseNotificaciones = "Notificaciones";
    
    // Referencia al controlador principal
    private VentanaPrincipalController controladorPrincipal;
    
    @FXML
    public void initialize() {
        if (menuNotificaciones != null) {
            textoBaseNotificaciones = menuNotificaciones.getText();
        }
    }
    
    public void setControladorPrincipal(VentanaPrincipalController controlador) {
        this.controladorPrincipal = controlador;
        configurarEventos();
        actualizarIndicadorNotificaciones();
    }


    public void actualizarIndicadorNotificaciones() {
        if (menuNotificaciones == null) return;

        int sinLeer = 0;
        try {
            sinLeer = Configuracion.getInstancia().getGestorGastos().getNumeroNotificacionesNoLeidas();
        } catch (Exception ignored) {

        }

        if (sinLeer > 0) {
            menuNotificaciones.setText(textoBaseNotificaciones + " (" + sinLeer + ")");
        } else {
            menuNotificaciones.setText(textoBaseNotificaciones);
        }
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
        	menuEliminarCuenta.setOnShowing(e -> controladorPrincipal.eliminarCuenta());
        }
        
        if (menuTotalCuenta != null) {
            menuTotalCuenta.setOnAction(e -> controladorPrincipal.totalCuenta());
        }
        
        if (menuFiltrarGastos != null) {
            menuFiltrarGastos.setOnAction(e -> controladorPrincipal.filtrarGastos());
        }

        if (menuImportarGastos != null) {
            menuImportarGastos.setOnAction(e -> controladorPrincipal.importarGastosDesdeMenu());
        }
        
        if (menuCrearAlerta != null) {
            menuCrearAlerta.setOnAction(e -> controladorPrincipal.crearAlerta());
        }

        if (menuVerAlertas != null) {
            menuVerAlertas.setOnAction(e -> controladorPrincipal.mostrarAlertas());
        }

        if (menuAlertas != null) {
            menuAlertas.setOnShowing(e -> controladorPrincipal.eliminarAlerta());
        }
        
        if (menuEliminarAlerta != null) {

            menuEliminarAlerta.setOnShowing(e -> controladorPrincipal.eliminarAlerta());

            menuEliminarAlerta.setOnMenuValidation(e -> controladorPrincipal.eliminarAlerta());
        }
        
        if (menuCrearCategoria != null) {
            menuCrearCategoria.setOnAction(e -> controladorPrincipal.crearCategoria());
        }
        
        if (menuEliminarCategoria != null) {
            menuEliminarCategoria.setOnShowing(e -> controladorPrincipal.eliminarCategoria());
        }
        
        if (menuHistorialNotificaciones != null) {
            menuHistorialNotificaciones.setOnAction(e -> controladorPrincipal.mostrarHistorial());
        }

        if (menuNotificaciones != null) {
            menuNotificaciones.setOnShowing(e -> actualizarIndicadorNotificaciones());
        }
    }
}