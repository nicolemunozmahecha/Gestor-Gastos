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
    
    private String formatearNombre(String campo) {
        String[] palabras = campo.split(" ");
        StringBuilder resultado = new StringBuilder();

        for (String palabra : palabras) {
            if (palabra.length() > 0) {
                resultado.append(Character.toUpperCase(palabra.charAt(0)))
                         .append(palabra.substring(1).toLowerCase())
                         .append(" ");
            }
        }

        return resultado.toString().trim();
    }
    
    @FXML
    private void crearCategoria() {
    	String nombre = campoNombreCategoria.getText().trim().replaceAll("\\s+", " ");
        if (nombre.isEmpty()) {
        	System.out.println("La categoria debe tener nombre");
            return;
        }
        // Para que las categorias empiecen en Mayuscula
        nombre = formatearNombre(nombre);
        gestor.crearCategoria(nombre);
    	Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }
    @FXML
    private void atras() {
    	Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }
}
