package tds.vista;

import java.util.List;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import tds.Configuracion;
import tds.controlador.GestorGastos;
import tds.modelo.Categoria;
import tds.modelo.PeriodoAlerta;
import tds.modelo.impl.CategoriaImpl;


public class CrearAlertaController {

	@FXML private TextField campoNombreAlerta;
	@FXML private TextField campoLimiteGasto;
	@FXML private RadioButton mensual;
	@FXML private RadioButton semanal;
	@FXML private MenuButton categorias;
	@FXML private CheckMenuItem categoriaAlimentacion;
	@FXML private CheckMenuItem categoriaTransporte;
	@FXML private CheckMenuItem categoriaEntretenimiento;
	@FXML private RadioButton activar;
	@FXML private RadioButton noActivar;

	private List<Categoria> categoriasDisp;
	private GestorGastos gestor;
	double valor;
	
    @FXML
    public void initialize() {
        gestor = Configuracion.getInstancia().getGestorGastos();
        cargarCategorias();
    }
    
    // PARA CARGAR LAS CATEGORIAS NUEVAS, Y QUE SOLO DEJE SELECCIONAR UNA
    private void cargarCategorias() {
        categoriasDisp = gestor.getCategoriasPersonalizadas();
        for (Categoria c : categoriasDisp) {
            final CheckMenuItem item = new CheckMenuItem(c.getNombre());
            item.setUserData(c);
            categorias.getItems().add(item);
        }
        for (MenuItem m : categorias.getItems()) {
            m.setOnAction(e -> {
			                if (((CheckMenuItem) m).isSelected()) {
			                    for (MenuItem mi : categorias.getItems()) {
			                        if (mi != m && mi instanceof CheckMenuItem) {
			                            ((CheckMenuItem) mi).setSelected(false);
			                        }
			                    }
			                }
			});
        }
    }
    
    private boolean ningunoSeleccionado() {
        return categorias.getItems().stream()
                .map(item -> (CheckMenuItem) item)               // Convertimos a CheckMenuItem
                .noneMatch(CheckMenuItem::isSelected);          // Ninguno está seleccionado
    }
    
    
    @FXML
    private void crearAlerta() {
    	// tienen que estar todos los campos llenos
    	String nombreAlerta = campoNombreAlerta.getText().trim();
        if (nombreAlerta.isEmpty()) {
        	System.out.println("La alerta debe tener un nombre");
            return;
        }
        String limiteGasto = campoLimiteGasto.getText().trim();
        if (limiteGasto.isEmpty()) {
        	System.out.println("La alerta debe tener un limite de gasto");
            return;
        }
        
        // Verificar si es un número
        try {
            valor = Double.parseDouble(limiteGasto);
        } catch (NumberFormatException e) {
            System.out.println("Por favor ingrese un número válido");
            return;
        }
       
        // seleccionamos periodo de alerta:
        PeriodoAlerta p;
        if(mensual.isSelected()) {
        	p = PeriodoAlerta.MENSUAL;
        }else if(semanal.isSelected()){
        	p = PeriodoAlerta.SEMANAL;
        }else {
        	System.out.println("La alerta debe tener un tipo de periodo");
        	return;
        }
        
        if(ningunoSeleccionado()) {
        	gestor.crearAlerta(nombreAlerta, valor, p);
        }else {
        	// Obtenemos lista de categorias, y solo queremos una, SOLO SE ELIGE 1 CATEGORIA
        	Optional<CheckMenuItem> c = categorias.getItems().stream()
        			.filter(item -> item instanceof CheckMenuItem)
        	        .map(item -> (CheckMenuItem) item)
        	        .filter(CheckMenuItem::isSelected)
        	        .findFirst();
        	// creamos categoria nueva para la alerta, pero YA EXISTE en la lista de categorias
        	CheckMenuItem item = c.get();
            Categoria cat = new CategoriaImpl(item.getText()); 
        	gestor.crearAlerta(nombreAlerta, valor, p, cat);
        }
        
        if(!activar.isSelected() && !noActivar.isSelected()) {
        	System.out.println("Debes elegir si activar o no la alerta");
        	return;
        }
    	Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }
    
    @FXML
    private void atras() {
    	Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }
}
