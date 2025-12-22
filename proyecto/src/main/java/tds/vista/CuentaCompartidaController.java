package tds.vista;

import java.time.LocalDate;
import java.util.List;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import tds.Configuracion;
import tds.controlador.GestorGastos;
import tds.modelo.Cuenta;
import tds.modelo.CuentaCompartida;
import tds.modelo.Gasto;
import tds.modelo.Persona;
import tds.modelo.impl.GastoImpl;

// HACER QUE EL CONTROLADOR SEA UN TIPO DE LA VENTANA PRINCIPAL PARA AHORRAR CODIGO????
public class CuentaCompartidaController {
	@FXML private TabPane tabPane;

    // CUENTAS
    @FXML private MenuItem menuCrearCuenta;
    @FXML private MenuItem menuEliminarCuenta1;
    @FXML private MenuItem menuEliminarCuenta2;

    // CATEGORÍAS
    @FXML private MenuItem menuCrearCategoria;
    @FXML private MenuItem menuEliminarCategoria1;
    @FXML private MenuItem menuEliminarCategoria2;

    // ALERTAS
    @FXML private MenuItem menuCrearAlerta;
    @FXML private MenuItem menuEliminarAlerta1;
    @FXML private MenuItem menuEliminarAlerta2;

    // NOTIFICACIONES
    @FXML private MenuItem menuHistorialNotificaciones;

    // SALIR
    @FXML private MenuItem menuSalir;

    // GASTOS
    @FXML private MenuButton btnCrearGasto;
    @FXML private MenuItem menuGastoNuevo;
    @FXML private MenuItem menuImportarGasto;

    // DISTRIBUCION
    @FXML private MenuButton btnDistribucion;
    @FXML private MenuItem personalizarDistribución;
    
    // SALDO
    @FXML private MenuButton btnSaldo;
    @FXML private MenuItem saldoPorPersona;

    @FXML private TableView<Gasto> tablaGastos;
    
    
    @FXML private TableColumn<Gasto, String> colDescripcion;
    @FXML private TableColumn<Gasto, String> colNombre;
    @FXML private TableColumn<Gasto, Double> colCantidad;
    @FXML private TableColumn<Gasto, LocalDate> colFecha;
    @FXML private TableColumn<Gasto, String> colCategoria;
    @FXML private TableColumn<Gasto, String> colPagador;
    
    private CuentaCompartidaController controller;
    private GestorGastos gestor;
    private CuentaCompartida cuenta;

    // hemos añadido atributo cuenta al inicializar, para el nombre de la cuenta
    @FXML
    public void inicializar(Cuenta cuenta) {
    	this.cuenta = (CuentaCompartida) cuenta;
    	if (tablaGastos != null) {
	        configurarTabla();
	        cargarGastos();
    	}
    }


    /*
    @FXML
    public void initialize() {
    	 if (tablaGastos != null) {
    	        configurarTabla();
    	        cargarGastos();
    	 }
    }*/

    private void configurarTabla() {
    	// Configurar qué propiedad mostrar en cada columna
    	colNombre.setCellValueFactory(cellData -> 
        	new SimpleStringProperty(cellData.getValue().getNombre())); 
    
	    colCantidad.setCellValueFactory(cellData -> 
	        new SimpleDoubleProperty(cellData.getValue().getCantidad()).asObject());
	    
	    colFecha.setCellValueFactory(cellData -> 
	        new SimpleObjectProperty<>(cellData.getValue().getFecha()));
	    
	    colDescripcion.setCellValueFactory(cellData -> 
	        new SimpleStringProperty(cellData.getValue().getDescripcion()));
	    
	    colCategoria.setCellValueFactory(cellData -> 
	        new SimpleStringProperty(cellData.getValue().getCategoria().getNombre()));
	    colPagador.setCellValueFactory(cellData -> 
    		new SimpleStringProperty(cellData.getValue().getPagador().getNombre()));
        
    }
    
    private void cargarGastos() {
        gestor = Configuracion.getInstancia().getGestorGastos();
        List<Gasto> gastos = gestor.getGastosPorCuenta(this.cuenta);
        tablaGastos.getItems().clear();
        tablaGastos.getItems().addAll(gastos);
    }

    
    public void añadirGastoTabla(GastoImpl g) {
    	tablaGastos.getItems().add(g);            
    }

    public void setCuentaCompartidaController(CuentaCompartidaController controller) {
        this.controller = controller;
    }
    public List<Persona> getPersonasCuenta(){
    	return cuenta.getPersonas();
    }
    @FXML private void crearGastoCompartida() {
    	Configuracion.getInstancia().getSceneManager().showCrearGastoCompartida(this);
       
    }
    @FXML private void importarGasto() { System.out.println("Importar Gasto"); }


    @FXML private void saldoPorPersona() {
    	System.out.println("Saldo por persona");
    }
    
    @FXML private void personalizarDistribucion() {
    	try {
            Configuracion.getInstancia().getSceneManager().showDistribucionCuentaCompartida();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        
    
    @FXML
    private void salirAplicacion() {
        Alert a = new Alert(AlertType.CONFIRMATION, "¿Salir?");
        a.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) System.exit(0);
        });
    }
}
