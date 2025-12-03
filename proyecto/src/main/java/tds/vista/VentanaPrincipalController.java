package tds.vista;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import tds.Configuracion;

public class VentanaPrincipalController {

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
    

  //  @FXML private MenuButton btnVisualizacion;
  //  @FXML private MenuItem menuVerTabla;
  //  @FXML private MenuItem menuVerGrafica;

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
    	//System.out.println("creando Alerta");
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

    @FXML private void crearGasto() {
    	try {
            Configuracion.getInstancia().getSceneManager().showCrearGasto();
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
    
    @FXML private void personalizarDistribución() {
    	System.out.println("Personalizar Distribución");
    }
    
    // HASTA AQUI
    
    
    @FXML
    private void salirAplicacion() {
        Alert a = new Alert(AlertType.CONFIRMATION, "¿Salir?");
        a.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) System.exit(0);
        });
    }
}
