package tds.vista;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import tds.Configuracion;
import tds.controlador.GestorGastos;

public class CrearCategoriaController {
	
	@FXML private TextField campoNombreCategoria;

	private GestorGastos gestor;

    @FXML
    public void initialize() {
        gestor = Configuracion.getInstancia().getGestorGastos();
    }
    
    @FXML
    private void crearCategoria() {
    	String nombre = campoNombreCategoria.getText().trim();
        if (nombre.isEmpty()) {
        	System.out.println("La categoria debe tener nombre");
            return;
        }
        gestor.crearCategoria(nombre);
    	Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }
    @FXML
    private void atras() {
    	Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }
}
