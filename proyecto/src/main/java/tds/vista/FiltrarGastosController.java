package tds.vista;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import tds.Configuracion;
import tds.controlador.GestorGastos;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import tds.Configuracion;
import tds.controlador.GestorGastos;

public class FiltrarGastosController {
	@FXML private RadioButton enero;
	@FXML private RadioButton febrero;
	@FXML private RadioButton marzo;
	@FXML private RadioButton abril;
	@FXML private RadioButton mayo;
	@FXML private RadioButton junio;
	@FXML private RadioButton julio;
	@FXML private RadioButton agosto;
	@FXML private RadioButton septiembre;
	@FXML private RadioButton octubre;
	@FXML private RadioButton noviembre;
	@FXML private RadioButton diciembre;

	
	@FXML private DatePicker fechaDesde;
	@FXML private DatePicker fechaHasta;
	// FALTA LISTADO DE CATEGORIAS PARA SELECCIONAR DE AHÍ

	@FXML private Button btAplicarFiltroa;

	private GestorGastos gestor;

    @FXML
    public void initialize() {
        gestor = Configuracion.getInstancia().getGestorGastos();
    }
    
    @FXML
    private void filtrarGastos() {
    	Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }
    @FXML
    private void atras() {
    	Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }

}
