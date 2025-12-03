package tds.vista;

import javafx.fxml.FXML;
import tds.Configuracion;
import tds.controlador.GestorGastos;

public class DistribucionCuentaCompartidaController {
	private GestorGastos gestor;

	@FXML
    public void initialize() {
        gestor = Configuracion.getInstancia().getGestorGastos();
    }
    
    @FXML
    private void personalizarDistribucion() {
    	Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }
    @FXML
    private void atras() {
    	Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }
}
