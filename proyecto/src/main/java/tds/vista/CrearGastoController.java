package tds.vista;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import tds.Configuracion;
import tds.controlador.GestorGastos;

public class CrearGastoController {
	
	@FXML private TextField nombreGasto;
	@FXML private TextField cantidad;
	@FXML private DatePicker fechaGasto;
	@FXML private TextField descripción;
	@FXML private CheckMenuItem categoria1;
	@FXML private CheckMenuItem categoria2;
	@FXML private CheckMenuItem categoria3;
	@FXML private CheckMenuItem persona1;
	@FXML private CheckMenuItem persona2;
	@FXML private CheckMenuItem persona3;
	@FXML private Button aceptar;

	private GestorGastos gestor;

	@FXML
    public void initialize() {
        gestor = Configuracion.getInstancia().getGestorGastos();
    }
    
    // FALTA FUNCIONALIDAD
    @FXML
    private void crearGasto() {
    	Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }
}
