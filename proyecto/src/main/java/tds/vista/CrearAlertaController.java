package tds.vista;

import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import tds.Configuracion;
import tds.controlador.GestorGastos;

public class CrearAlertaController {
	
	private GestorGastos gestor;
	private TextField nombreAlerta;
	private TextField limiteGasto;
	private RadioButton mensual;
	private RadioButton semanal;
	private CheckMenuItem categoriaEntretenimiento;
	private CheckMenuItem categoriaTransporte;
	private CheckMenuItem categoriaAlimentacion;


    @FXML
    public void initialize() {
        gestor = Configuracion.getInstancia().getGestorGastos();
    }
    
    @FXML
    private void crearAlerta() {
    	Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }
    @FXML
    private void atras() {
    	Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }
}
