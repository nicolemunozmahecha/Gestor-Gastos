package tds.vista;

import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
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

    // CATEGORÍAS
    @FXML private MenuItem menuCrearCategoria;

    // ALERTAS
    @FXML private MenuItem menuCrearAlerta;

    // NOTIFICACIONES
    @FXML private MenuItem menuHistorialNotificaciones;

    // SALIR
    @FXML private MenuItem menuSalir;

    // GASTOS
    @FXML private MenuButton btnCrearGasto;
    @FXML private MenuButton btnEliminarGasto;
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

    // GRÁFICAS (cuenta compartida: por categorías o por persona)
    @FXML private BarChart<String, Number> barChart;
    @FXML private PieChart pieChart;

    private enum AgrupacionGraficas { CATEGORIAS, PERSONAS }
    private AgrupacionGraficas agrupacionActual = AgrupacionGraficas.CATEGORIAS;
    
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
    	barChart.setLegendVisible(false);
        pieChart.setLegendVisible(false);
    }

    public void setCuenta(CuentaCompartida p) {
    	this.cuenta = p;
        cargarGastos();

    }
    public CuentaCompartida getCuenta() {
    	return cuenta;
    	
    }

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
        List<Gasto> gastos = gestor.getGastosPorCuenta(cuenta);
        tablaGastos.getItems().clear();
        tablaGastos.getItems().addAll(gastos);

        actualizarGraficas();
    }

    
    public void añadirGastoTabla(GastoImpl g) {
    	tablaGastos.getItems().add(g);            
		actualizarGraficas();
    }

    public void setCuentaCompartidaController(CuentaCompartidaController controller) {
        this.controller = controller;
    }
    
    public List<Persona> getPersonasCuenta(){
    	return cuenta.getPersonas();
    }
    
    @FXML 
    private void crearGastoCompartida() throws IOException {
    	try {
    		
            Configuracion.getInstancia().getSceneManager().showCrearGastoCompartida(this);
            
            
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
    
    @FXML
    private void cargarMenuEliminarGasto() {
        gestor = Configuracion.getInstancia().getGestorGastos();
        btnEliminarGasto.getItems().clear();

        List<Gasto> gastos = gestor.getGastosPorCuenta(cuenta);

        for (Gasto g : gastos) {
            String texto = String.format("%s - %.2f€ - %s - %s (%s)",
                    g.getNombre(), g.getCantidad(), g.getCategoria().getNombre(),
                    g.getFecha(), g.getPagador() != null ? g.getPagador().getNombre() : "-");
            MenuItem item = new MenuItem(texto);
            item.setUserData(g);
            item.setOnAction(ev -> {
                Gasto gastoAEliminar = (Gasto) item.getUserData();
                Alert a = new Alert(AlertType.CONFIRMATION, "¿Está seguro que quiere eliminar el gasto " + gastoAEliminar.getNombre() + " ?");
                a.showAndWait().ifPresent(r -> {
                	if (r == ButtonType.OK) {
			            boolean ok = gestor.eliminarGastoDeCuenta(cuenta, gastoAEliminar);
			            if (ok) {
			                cargarGastos();
			            } else {
			                new Alert(Alert.AlertType.ERROR, "No se pudo eliminar el gasto.").showAndWait();
			            }
                	}
                });
            });
            btnEliminarGasto.getItems().add(item);
        }
    }
    
    @FXML
    private void importarGasto() { System.out.println("Importar Gasto"); }


    @FXML 
    private void saldoPorPersona() {
		if (cuenta == null) return;
		StringBuilder sb = new StringBuilder();
		for (Persona p : cuenta.getPersonas()) {
			sb.append(p.getNombre())
			  .append(": ")
			  .append(String.format("%.2f€", cuenta.getSaldo(p)))
			  .append("\n");
		}

		Alert a = new Alert(AlertType.INFORMATION);
		a.setTitle("Saldo por persona");
		a.setHeaderText("Cuenta: " + cuenta.getNombre());
		TextArea area = new TextArea(sb.toString());
		area.setEditable(false);
		area.setWrapText(true);
		a.getDialogPane().setContent(area);
		a.showAndWait();
    }

    // Agrupa las gráficas por categorías (por defecto).
    @FXML
    private void agruparGraficasPorCategorias() {
        agrupacionActual = AgrupacionGraficas.CATEGORIAS;
        actualizarGraficas();
    }

    // Agrupa las gráficas por personas (por el pagador del gasto).
    @FXML
    private void agruparGraficasPorPersonas() {
        agrupacionActual = AgrupacionGraficas.PERSONAS;
        actualizarGraficas();
    }

    private void actualizarGraficas() {
        if (cuenta == null || barChart == null || pieChart == null) return;

        gestor = Configuracion.getInstancia().getGestorGastos();
        List<Gasto> gastos = gestor.getGastosPorCuenta(cuenta);

        Map<String, Double> totales = new LinkedHashMap<>();
        for (Gasto g : gastos) {
            String clave;
            if (agrupacionActual == AgrupacionGraficas.PERSONAS) {
                clave = (g.getPagador() != null) ? g.getPagador().getNombre() : "(Sin pagador)";
            } else {
                clave = (g.getCategoria() != null) ? g.getCategoria().getNombre() : "(Sin categoría)";
            }
            totales.merge(clave, g.getCantidad(), Double::sum);
        }

        // Barras
        barChart.getData().clear();
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        for (Map.Entry<String, Double> e : totales.entrySet()) {
            serie.getData().add(new XYChart.Data<>(e.getKey(), e.getValue()));
        }
        barChart.getData().add(serie);

        // Circular
        ObservableList<PieChart.Data> datosPie = FXCollections.observableArrayList();
        for (Map.Entry<String, Double> e : totales.entrySet()) {
            datosPie.add(new PieChart.Data(e.getKey(), e.getValue()));
        }
        pieChart.setData(datosPie);
    }
    
    @FXML 
    private void personalizarDistribucion() {
    	try {
            Configuracion.getInstancia().getSceneManager().showDistribucionCuentaCompartida(this);
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

	public CuentaCompartidaController getController() {
		return controller;
	}
}
