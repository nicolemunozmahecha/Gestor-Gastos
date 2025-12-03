package tds.vista;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.Alert.AlertType;
import tds.Configuracion;

public class CuentaCompartidaController {
	@FXML private TabPane tabPane;

    // CUENTAS
    @FXML private MenuItem menuCrearCuenta;
    @FXML private MenuItem menuEliminarCuenta1;
    @FXML private MenuItem menuEliminarCuenta2;

    // CATEGORÍAS
    @FXML private MenuItem menuCrearCategoria;
    @FXML private MenuItem menuEliminarCategoria1;
    @FXML private MenuItem menuEliminarCategoria2;

    // ALERTAS
    @FXML private MenuItem menuCrearAlerta;
    @FXML private MenuItem menuEliminarAlerta1;
    @FXML private MenuItem menuEliminarAlerta2;

    // NOTIFICACIONES
    @FXML private MenuItem menuHistorialNotificaciones;

    // SALIR
    @FXML private MenuItem menuSalir;

    // GASTOS
    @FXML private MenuButton btnCrearGasto;
    @FXML private MenuItem menuGastoNuevo;
    @FXML private MenuItem menuImportarGasto;

    // DISTRIBUCION
    @FXML private MenuButton btnDistribucion;
    @FXML private MenuItem personalizarDistribución;
    
    // SALDO
    @FXML private MenuButton btnSaldo;
    @FXML private MenuItem saldoPorPersona;

    
    
   // @FXML private MenuButton btnVisualizacion;
   // @FXML private MenuItem menuVerTabla;
   // @FXML private MenuItem menuVerGrafica;

    @FXML
    public void initialize() {
    }

    // ========== HANDLERS ==========

    @FXML
    private void crearCuenta() {
        try {
            Configuracion.getInstancia().getSceneManager().showCrearCuenta();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML private void eliminarCuenta() { System.out.println("Eliminar Cuenta"); }
    @FXML 
    private void totalCuenta() {
    	try {
            Configuracion.getInstancia().getSceneManager().showTotalCuenta();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML 
    private void filtrarGastos() { 
    	try {
            Configuracion.getInstancia().getSceneManager().showFiltrarGastos();
        } catch (Exception e) {
            e.printStackTrace();
        }
   }
    
    @FXML 
    private void crearCategoria() { 
    	try {
            Configuracion.getInstancia().getSceneManager().showCrearCategoria();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML private void eliminarCategoria() { System.out.println("Eliminar Categoria"); }

    
    // VER POR QUE NO VA ESTA
    @FXML 
    private void crearAlerta() { 
    	try {
            Configuracion.getInstancia().getSceneManager().showCrearAlerta();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML private void eliminarAlerta() { System.out.println("Eliminar Alerta"); }

    @FXML 
    private void mostrarHistorial() {
    	try {
            Configuracion.getInstancia().getSceneManager().showMostrarHistorial();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML private void crearGastoCompartida() {
    	try {
            Configuracion.getInstancia().getSceneManager().showCrearGastoCompartida();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML private void importarGasto() { System.out.println("Importar Gasto"); }

    //@FXML private void mostrarTabla() { System.out.println("Mostrar Tabla"); }
    //@FXML private void mostrarGrafica() { System.out.println("Mostrar Gráfica"); }

    @FXML private void saldoPorPersona() {
    	System.out.println("Saldo por persona");
    }
    
    @FXML private void personalizarDistribucion() {
    	try {
            Configuracion.getInstancia().getSceneManager().showDistribucionCuentaCompartida();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        
    
    @FXML
    private void salirAplicacion() {
        Alert a = new Alert(AlertType.CONFIRMATION, "¿Salir?");
        a.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) System.exit(0);
        });
    }
}
