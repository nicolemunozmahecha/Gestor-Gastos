package tds.vista;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tds.app.App;

/**
 * Se encarga de coordinar la navegación entre ventanas.
 */
public class SceneManager {

    private Stage stage;
    private Scene escenaActual;

    /**
     * Inicializa el gestor de escenas con el stage principal.
     */
    public void inicializar(Stage stage) {
        this.stage = stage;
    }

    /**
     * Muestra la ventana principal de la aplicación.
     */
    public void showVentanaPrincipal() {
        cambiarEscena("ventanaPrincipal");
        stage.setTitle("Gestión de Gastos");
        stage.show();
    }
    public void showVentanaCompartida() {
        cambiarEscena("cuentaCompartida");
        stage.setTitle("Gestión de Gastos");
        stage.show();
    }
    
    public void showCrearCuenta() throws IOException {
        cambiarEscena("crearCuenta");
        stage.setTitle("Creando cuenta");
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
    
    public void showMostrarHistorial() throws IOException {
        cambiarEscena("historial");
        stage.setTitle("Historial");
        stage.show();
    }
    
    public void showCrearGasto() throws IOException {
        cambiarEscena("crearGasto");
        stage.setTitle("Creando gasto");
        stage.show();
    }
    public void showDistribucionCuentaCompartida() throws IOException {
        cambiarEscena("distribucionCuentaCompartida");
        stage.setTitle("Personalizar Distribución");
        stage.show();
    }
    
    /**
     * Muestra un diálogo modal a partir de un FXML.
     * (Por si en el futuro quieres diálogos de ayuda, etc.)
     */
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

    /**
     * Cambia la escena actual por la asociada al FXML indicado (sin extensión).
     */
    private void cambiarEscena(String fxml) {
        try {
            Parent root = loadFXML(fxml);

            if (escenaActual == null) {
                // tamaño por defecto similar al que ya usabas en App
                escenaActual = new Scene(root, 667, 474);
                stage.setScene(escenaActual);
            } else {
                escenaActual.setRoot(root);
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cambiar de escena a: " + fxml, e);
        }
    }

    private Parent loadFXML(String fxml) throws IOException {
        // Los FXML están en el mismo paquete que App (tds.app)
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
}
