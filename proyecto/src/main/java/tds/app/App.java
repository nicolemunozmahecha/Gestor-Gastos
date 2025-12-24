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
	    // Intentar cargar la cuenta Principal existente
	    CuentaPersonalImpl p = null;
	    try {
	        p = (CuentaPersonalImpl) configuracion.getGestorGastos().getCuentaPorNombre("Principal");
	        System.out.println("DEBUG App: Cuenta Principal cargada del repositorio con " + p.getNumeroGastos() + " gastos");
	    } catch (Exception e) {
	        // Si no existe, crear una nueva
	        System.out.println("DEBUG App: Cuenta Principal no existe, creando nueva");
	        p = new CuentaPersonalImpl("Principal", "User1");
	        configuracion.getGestorGastos().crearCuentaPersonal(p);
	    }
	    
	    configuracion.getSceneManager().setPrincipal(p);
	    configuracion.getSceneManager().showVentanaPrincipal();
	}
    
    public static void main(String[] args) {
        launch();
    }

}