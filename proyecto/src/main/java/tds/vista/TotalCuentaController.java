package tds.vista;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import tds.Configuracion;
import tds.controlador.GestorGastos;
import javafx.scene.control.Button;
import tds.Configuracion;
import tds.controlador.GestorGastos;

public class TotalCuentaController {
	
	@FXML private Label campoTotal;
	@FXML private Button btOK;

	private GestorGastos gestor;

    @FXML
    public void initialize() {
        gestor = Configuracion.getInstancia().getGestorGastos();
    }
    
    // FALTA FUNCIONALIDAD
    @FXML
    private void totalCuenta() {
    	
    	Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }

}
