package tds.app;

import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;
import tds.Configuracion;
import tds.ConfiguracionImpl;
import tds.adapters.repository.exceptions.ElementoExistenteException;
import tds.adapters.repository.exceptions.ErrorPersistenciaException;
import tds.modelo.impl.CuentaPersonalImpl;

public class App extends Application {

	@Override
	public void start(Stage stage) throws IOException, ElementoExistenteException, ErrorPersistenciaException {
	    // Inicialización
	    Configuracion configuracion = new ConfiguracionImpl();
	    Configuracion.setInstancia(configuracion);
	    configuracion.getSceneManager().inicializar(stage);
	    
	    // Inicio de la aplicación
	    // Intentar cargar la cuenta Principal existente
	    CuentaPersonalImpl p = null;

	    try {
	        p = (CuentaPersonalImpl) configuracion.getGestorGastos().getCuentaPorNombre("Principal");

	    } catch (Exception e) {
	        // Si no existe, crear una nueva
	    	p =new CuentaPersonalImpl("Principal"); 
	    	configuracion.getGestorGastos().crearCuentaPersonal("Principal");
	    }
	    
	    configuracion.getSceneManager().setPrincipal(p);
	    configuracion.getSceneManager().showVentanaPrincipal();
	}
    
    public static void main(String[] args) throws ElementoExistenteException, ErrorPersistenciaException {
        // Si se pasa -cli como argumento (ejecutar como pone en el README)
        if (args.length > 0 && args[0].equals("-cli")) {
            // Modo línea de comandos
            Configuracion configuracion = new ConfiguracionImpl();
            Configuracion.setInstancia(configuracion);
            
            CLI cli = new CLI(configuracion);
            cli.ejecutar();
        } else {
            // Modo interfaz gráfica (por defecto)
            launch();
        }
    }

}