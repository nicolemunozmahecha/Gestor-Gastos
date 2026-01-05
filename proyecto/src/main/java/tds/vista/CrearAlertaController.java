package tds.vista;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import tds.Configuracion;
import tds.adapters.repository.exceptions.ElementoExistenteException;
import tds.adapters.repository.exceptions.ErrorPersistenciaException;
import tds.controlador.GestorGastos;
import tds.modelo.Categoria;
import tds.modelo.PeriodoAlerta;


public class CrearAlertaController {

	@FXML private TextField campoNombreAlerta;
	@FXML private TextField campoLimiteGasto;
	@FXML private RadioButton mensual;
	@FXML private RadioButton semanal;
	@FXML private MenuButton categorias;
	@FXML private CheckMenuItem categoriaAlimentacion;
	@FXML private CheckMenuItem categoriaTransporte;
	@FXML private CheckMenuItem categoriaEntretenimiento;

	private List<Categoria> categoriasDisp;
	private GestorGastos gestor;
	private double valor;
	private final String TEXTO_DEFECTO2 = "Categoria";
	private Categoria cat;
	
    @FXML
    public void initialize() {
        gestor = Configuracion.getInstancia().getGestorGastos();
        cargarCategorias();
    }
    
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    // PARA CARGAR LAS CATEGORIAS NUEVAS, Y QUE SOLO DEJE SELECCIONAR UNA
    public void cargarCategorias() {
    	categorias.getItems().clear();
        categoriasDisp = gestor.getCategorias();
        for (Categoria c : categoriasDisp) {
            CheckMenuItem item = new CheckMenuItem(c.getNombre());
            item.setUserData(c);

            item.setOnAction(e -> {
	            if (item.isSelected()) {
	                // deseleccionar las demás
	                for (MenuItem mi : categorias.getItems()) {
	                    if (mi != item && mi instanceof CheckMenuItem) {
	                        ((CheckMenuItem) mi).setSelected(false);
	                    }
	                }
	                cat = c;
	                categorias.setText(item.getText());

	            } else {
	                cat = null;
	                categorias.setText(TEXTO_DEFECTO2);
	            }
	        });
            categorias.getItems().add(item);
        }
    }
    
    private boolean ningunoSeleccionado() {
        return categorias.getItems().stream()
                .map(item -> (CheckMenuItem) item)         
                .noneMatch(CheckMenuItem::isSelected);          
    }
    
    
    @FXML
    private void crearAlerta() {
    	// tienen que estar todos los campos llenos
    	String nombreAlerta = campoNombreAlerta.getText().trim();
        String limiteGasto = campoLimiteGasto.getText().trim();
        PeriodoAlerta p;

    	try{
        	if (nombreAlerta.isEmpty()) {
	        	throw new IllegalArgumentException("La alerta debe tener nombre");
        	}
        	if (limiteGasto.isEmpty()) {
        		throw new IllegalArgumentException("La alerta debe tener un limite de gasto");
        	}
        	// Verificar si es un número
	        try {
	            valor = Double.parseDouble(limiteGasto);
	        } catch (NumberFormatException e) {
	        	new Alert(Alert.AlertType.ERROR, "Por favor, introduzca un número entero válido").showAndWait();
	            return;
	        }
	        // seleccionamos periodo de alerta:
	        if(mensual.isSelected()) {
	        	p = PeriodoAlerta.MENSUAL;
	        }else if(semanal.isSelected()){
	        	p = PeriodoAlerta.SEMANAL;
	        }else {
	        	throw new IllegalArgumentException("La alerta debe tener un tipo de periodo");
	        }
	        
	        if(ningunoSeleccionado()) {
	        	gestor.crearAlerta(nombreAlerta, valor, p);
	        }else {
	        	gestor.crearAlerta(nombreAlerta, valor, p, cat);
	        }
	        
	    	Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();

    	}catch (IllegalArgumentException e) {
            mostrarError("Datos inválidos", e.getMessage());
        } catch (ElementoExistenteException e) {
            mostrarError("Alerta duplicada", "Ya existe una alerta con ese nombre.");
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
