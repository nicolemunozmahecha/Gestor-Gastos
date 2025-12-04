package tds.vista;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import tds.Configuracion;
import tds.app.App;
import tds.modelo.CuentaCompartida;

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
    // NUEVO: Método público para añadir pestañas desde fuera
    public void añadirPestañaCuentaCompartida(CuentaCompartida cuenta) {
    	// si la pestaña a crear es vacia
        if (tabPane == null) {
            System.err.println("ERROR: tabPane es null!");
            return;
        }
        
        try {
            // Cargar el FXML usando App.class (como en SceneManager)
            FXMLLoader loader = new FXMLLoader(App.class.getResource("cuentaCompartida.fxml"));
            Parent contenido = loader.load();
            
            // Obtener el controlador de la cuenta compartida nueva y pasarle la cuenta
            CuentaCompartidaController controller = loader.getController();
            if (controller != null) {
                controller.inicializar(cuenta);
            }
            
            // Crear la pestaña
            Tab nuevaTab = new Tab(cuenta.getNombre());
            nuevaTab.setContent(contenido);
            nuevaTab.setClosable(true);
            nuevaTab.setUserData(cuenta);
            
            // Añadir al TabPane
            tabPane.getTabs().add(nuevaTab);
            tabPane.getSelectionModel().select(nuevaTab);
            
        } catch (IOException e) {
            //System.err.println("ERROR al cargar la vista de cuenta compartida:");
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
