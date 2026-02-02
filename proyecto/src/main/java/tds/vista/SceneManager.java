package tds.vista;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tds.Configuracion;
import tds.app.App;
import tds.modelo.impl.CuentaPersonalImpl;


public class SceneManager {

    private Stage stage;
    private Scene escenaActual;
    private VentanaPrincipalController ventanaPrincipalController; // NUEVO
    private Parent ventanaPrincipalRoot; 
    private CrearGastoCompartidaController crearGastoCompartidaController;
    private DistribucionCuentaCompartidaController distribucionCuentaCompartidaController;
    private CuentaPersonalImpl personal;


    public void inicializar(Stage stage) {
        this.stage = stage;
    }
    
    public void setPrincipal(CuentaPersonalImpl c) {
    	this.personal = c;
    }
    public CuentaPersonalImpl getPrincipal() {
    	return personal;
    }


    public void showVentanaPrincipal() {
    	try {
            // Si es la primera vez, cargar el FXML
            if (ventanaPrincipalRoot == null) {
                FXMLLoader loader = new FXMLLoader(App.class.getResource("ventanaPrincipal.fxml"));
                ventanaPrincipalRoot = loader.load();
                ventanaPrincipalController = loader.getController();

                // Inicializar la vista con los datos ya cargados (inyección).
                // Así el controller no tiene que "ir a buscar" cuentas al gestor,
                // y evitamos problemas de orden de inicialización.
                ventanaPrincipalController.init(
                        this.personal, Configuracion.getInstancia().getGestorGastos().getCuentasCompartidas()
                );
            }
            
            // Usar la misma instancia siempre
            if (escenaActual == null) {
                escenaActual = new Scene(ventanaPrincipalRoot, 1050, 650);
                escenaActual.getStylesheets().add(getClass().getResource("/tds/app/estilos.css").toExternalForm());
                stage.setScene(escenaActual);
            } else {
                escenaActual.setRoot(ventanaPrincipalRoot);
            }
            
            stage.setTitle("Gestión de Gastos");
            stage.show();

            // Refrescar el indicador de notificaciones (por si se han generado nuevas
            // notificaciones en otras pantallas, p.ej. al crear un gasto).
            if (ventanaPrincipalController != null) {
                ventanaPrincipalController.actualizarIndicadorNotificaciones();
            }
            
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar ventanaPrincipal", e);
        }
        stage.show();
    }
    
    public VentanaPrincipalController getVentanaPrincipalController() {
        return ventanaPrincipalController;
    }
    public void showVentanaCompartida() {
        cambiarEscena("cuentaCompartida");
        stage.setTitle("Gestión de Gastos");
        stage.show();
    }
    
    public void showCrearCuentaCompartida() throws IOException {
        cambiarEscena("crearCuentaCompartida");
        stage.setTitle("Creando cuenta compartida");
        stage.show();
    }
    
    
    public void showTotalCuenta() throws IOException {
        cambiarEscena("totalCuenta");
        stage.setTitle("Total cuenta");
        stage.show();
    }
    
    public void showFiltrarGastos() throws IOException {
        cambiarEscena("filtrarGastos");
        stage.setTitle("Filtrando gastos");
        stage.show();
    }
    
    public void showCalendario() throws IOException {
        cambiarEscena("calendario");
        stage.setTitle("Calendario Gastos");
        stage.show();
    }
    
    public void showCrearCategoria() throws IOException {
        cambiarEscena("crearCategoria");
        stage.setTitle("Creando categoria");
        stage.show();
    }
    
    public void showCrearAlerta() throws IOException {
        cambiarEscena("crearAlerta");
        stage.setTitle("Creando alerta");
        stage.show();
    }

    public void showMostrarAlertas() throws IOException {
        cambiarEscena("alertas");
        stage.setTitle("Alertas");
        stage.show();
    }
    
    public void showMostrarHistorial() throws IOException {
        cambiarEscena("historial");
        stage.setTitle("Historial");
        stage.show();
    }
    
    public void showCrearGasto() throws IOException {
    	try { 
    		FXMLLoader loader = new FXMLLoader(App.class.getResource("crearGasto.fxml")); 
	    	Parent root = loader.load(); 
	    	CrearGastoController controller = loader.getController(); 
	    	controller.cargarCategorias(); 
	    	cambiarEscenaConRoot("crearGasto", root); 
	    	stage.setTitle("Creando gasto");
    	} catch (IOException e) { 
    		e.printStackTrace(); }
    }
    
    public void showCrearGastoCompartida(CuentaCompartidaController parentController) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("crearGastoCompartida.fxml"));
            Parent root = loader.load();
            
            crearGastoCompartidaController = loader.getController();
            crearGastoCompartidaController.setCuentaCompartidaController(parentController);
            //crearGastoCompartidaController.cargarCategorias();
            
            cambiarEscenaConRoot("crearGastoCompartida", root);
            stage.setTitle("Creando gasto en cuenta compartida");
            
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar crear gasto compartida", e);
        }
    }
    public void showDistribucionCuentaCompartida(CuentaCompartidaController parentController) throws IOException {
       try {
             FXMLLoader loader = new FXMLLoader(App.class.getResource("distribucionCuentaCompartida.fxml"));
             Parent root = loader.load();
             
             distribucionCuentaCompartidaController = loader.getController();
             distribucionCuentaCompartidaController.setCuentaCompartidaController(parentController);
             
             cambiarEscenaConRoot("distribucionCuentaCompartida", root);
             stage.setTitle("Personalizar Distribución");

         } catch (IOException e) {
             throw new RuntimeException("No se pudo cargar crear gasto compartida", e);
         }
    }
    

    public void showDialog(String fxml) {
        try {
            DialogPane pane = FXMLLoader.load(App.class.getResource(fxml + ".fxml"));
            Dialog<Void> dialog = new Dialog<>();
            dialog.initOwner(stage);
            dialog.initStyle(StageStyle.UTILITY);
            dialog.setDialogPane(pane);
            dialog.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException("No se pudo abrir el diálogo: " + fxml, e);
        }
    }


    private void cambiarEscena(String fxml) {
        try {
            Parent root = loadFXML(fxml);

            if (escenaActual == null) {
                escenaActual = new Scene(root, 1050, 650);
                stage.setScene(escenaActual);
            } else {
                escenaActual.setRoot(root);
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cambiar de escena a: " + fxml, e);
        }
    }
    
    private void cambiarEscenaConRoot(String nombreEscena, Parent root) {
        stage.getScene().setRoot(root);
    }

    private Parent loadFXML(String fxml) throws IOException {
    	FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
}
