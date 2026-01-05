package tds.vista;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import tds.Configuracion;
import tds.adapters.repository.exceptions.ElementoExistenteException;
import tds.adapters.repository.exceptions.ErrorPersistenciaException;
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
    
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    @FXML
    private void crearCategoria() {
    	String nombre = campoNombreCategoria.getText().trim().replaceAll("\\s+", " ");
        try{
        	if (nombre.isEmpty()) {
	        	throw new IllegalArgumentException("La categoria debe tener nombre");
        	}
        	 nombre = formatearNombre(nombre);
             gestor.crearCategoria(nombre);
         	Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
         	
        } catch (IllegalArgumentException e) {
            mostrarError("Datos inválidos", e.getMessage());
        } catch (ElementoExistenteException e) {
            mostrarError("Categoria duplicada", "Ya existe una categoria con ese nombre.");
        } catch (ErrorPersistenciaException e) {
            mostrarError("Error de guardado", "No se pudo guardar en base de datos.");
        } catch (Exception e) {
            mostrarError("Error inesperado", e.getMessage());
        }
    }
    @FXML
    private void atras() {
    	Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }
}
