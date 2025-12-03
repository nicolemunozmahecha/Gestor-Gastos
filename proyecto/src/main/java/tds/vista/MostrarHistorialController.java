package tds.vista;

import javafx.fxml.FXML;
import tds.Configuracion;
import tds.controlador.GestorGastos;

public class MostrarHistorialController {
	private GestorGastos gestor;

    @FXML
    public void initialize() {
        gestor = Configuracion.getInstancia().getGestorGastos();
    }
    
    @FXML
    private void atras() {
    	Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }
}
