package tds.vista;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import tds.Configuracion;
import tds.controlador.GestorGastos;

public class CrearCategoriaController {
	
	@FXML private TextField nombreCategoria;
	@FXML private Button botonCreando;
	
	private GestorGastos gestor;

    @FXML
    public void initialize() {
        gestor = Configuracion.getInstancia().getGestorGastos();
    }
    
    // FALTA FUNCIONALIDAD
    @FXML
    private void crearCategoria() {
    	Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }
}
