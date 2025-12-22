package tds.app;

import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;
import tds.Configuracion;
import tds.ConfiguracionImpl;
import tds.modelo.impl.CuentaImpl;
import tds.modelo.impl.CuentaPersonalImpl;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
    	// Inicialización
    	Configuracion configuracion = new ConfiguracionImpl();
        Configuracion.setInstancia(configuracion);
        configuracion.getSceneManager().inicializar(stage);
        
        // Inicio de la aplicación
        CuentaPersonalImpl p = new CuentaPersonalImpl("Principal", "User1");
        configuracion.getSceneManager().setPrincipal(p);
        configuracion.getGestorGastos().crearCuentaPersonal(p);
        configuracion.getSceneManager().showVentanaPrincipal();
    }
    
    public static void main(String[] args) {
        launch();
    }

}