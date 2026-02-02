package tds.vista;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
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
import javafx.scene.text.Font;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import tds.Configuracion;
import tds.adapters.repository.exceptions.ElementoExistenteException;
import tds.adapters.repository.exceptions.ErrorPersistenciaException;
import tds.controlador.GestorGastos;
import tds.importacion.ImportacionException;
import tds.modelo.Cuenta;
import tds.modelo.CuentaCompartida;
import tds.modelo.Gasto;
import tds.modelo.Persona;

public class CuentaCompartidaController {
	@FXML private TabPane tabPane;

    // GASTOS
    @FXML private MenuButton btnCrearGasto;
    @FXML private MenuButton btnEliminarGasto;
   
    @FXML private MenuItem menuGastoNuevo;

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
    @FXML private MenuButton agrupacion;

    private enum AgrupacionGraficas { CATEGORIAS, PERSONAS }
    private AgrupacionGraficas agrupacionActual = AgrupacionGraficas.CATEGORIAS;
    
    private CuentaCompartidaController controller;
    private GestorGastos gestor;
    private CuentaCompartida cuenta;
    private final String[] PALETA_COLORES = {
            "#ffca28", "#ffa420", "#ca65e2", "e990c2", "#5c6bc0", "#ef5350", 
            "#66bb6a" };

    private Map<String, String> mapaColoresCategorias = new HashMap<>();

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
        barChart.getXAxis().setTickLabelFont(Font.font("Segoe UI", 12));
        barChart.getYAxis().setTickLabelFont(Font.font("Segoe UI", 12));
    }

    public void setCuenta(Cuenta cuentaActualizada) {
    	this.cuenta = (CuentaCompartida) cuentaActualizada;
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

    private void mostrarExito(String mensaje) {
        new Alert(AlertType.INFORMATION, mensaje).showAndWait();
    }

    private void mostrarAdvertencia(String mensaje) {
        new Alert(AlertType.WARNING, mensaje).showAndWait();
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    public void añadirGastoTabla(Gasto g) {
    	tablaGastos.getItems().add(g);            
		actualizarGraficas();
    }
    
	public CuentaCompartidaController getController() {
			return controller;
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
            item.getStyleClass().add("boton-peligro2");

            item.setOnAction(ev -> {
                Gasto gastoAEliminar = (Gasto) item.getUserData();
                Alert a = new Alert(AlertType.CONFIRMATION, "¿Está seguro que quiere eliminar el gasto " + gastoAEliminar.getNombre() + " ?");
                a.showAndWait().ifPresent(r -> {
                	if (r == ButtonType.OK) {
                		 try {
                             boolean ok = gestor.eliminarGastoDeCuenta(cuenta, gastoAEliminar);
                             
                             if (ok) {
                                 cargarGastos();
                                 mostrarExito("Gasto eliminado correctamente.");
                             } 
                         } catch (ErrorPersistenciaException e) {
                             mostrarError("Error al eliminar gasto", e.getMessage());
                         }
                	}
                });
            });
            btnEliminarGasto.getItems().add(item);
        }
        
        // SI QUEREMOS BORRAR TODOS LOS GASTOS DE LA CUENTA PRINCIPAL
	    MenuItem eliminar = new MenuItem("Eliminar todos los gastos");
	    eliminar.getStyleClass().add("boton-peligro2");
	    eliminar.setOnAction(e ->{
		    		Alert a = new Alert(AlertType.CONFIRMATION, "¿Está seguro que quiere eliminar todos los gastos?");
	                a.showAndWait().ifPresent(r -> {
	                	try {
	                        for (Gasto g : gastos) {
	                            gestor.eliminarGastoDeCuenta(cuenta, g);
	                        }

	                        cargarGastos();
	                        mostrarExito("Todos los gastos han sido eliminados correctamente.");

	                    } catch (ErrorPersistenciaException p) {
	                        mostrarError("Error al eliminar los gastos", p.getMessage());
	                    }
	                });
	    });
	    btnEliminarGasto.getItems().add(eliminar);
    }
    
    @FXML
    private void importarGasto() {
        Window w = (tabPane != null && tabPane.getScene() != null) ? tabPane.getScene().getWindow() : null;

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Importar gastos");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv"),
                new FileChooser.ExtensionFilter("Todos los ficheros", "*.*")
        );

        File fichero = chooser.showOpenDialog(w);
        if (fichero == null) {
            return;
        }

        try {
            gestor = Configuracion.getInstancia().getGestorGastos();
            boolean ok = gestor.importarGastos(fichero);
            
            if (ok) {
                cargarGastos();
                mostrarExito("Gastos importados correctamente.");
            } else {
                mostrarAdvertencia("No se importó ningún gasto del fichero.");
            }
            
        } catch (ImportacionException e) {
            mostrarError("Error al importar gastos", e.getMessage());
        } catch (ElementoExistenteException e) {
            mostrarError("Elemento duplicado", e.getMessage());
        } catch (ErrorPersistenciaException e) {
            mostrarError("Error de base de datos", e.getMessage());
        } catch (Exception e) {
            mostrarError("Error inesperado", e.getMessage());
            e.printStackTrace();
        }	   
    }


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

    // Agrupa las gráficas por categorías (por defecto)
    @FXML
    private void agruparGraficasPorCategorias() {
        agrupacionActual = AgrupacionGraficas.CATEGORIAS;
        String porCat = new String("Por categorias");
        agrupacion.setText(porCat);
        actualizarGraficas();
    }

    // Agrupa las gráficas por personas
    @FXML
    private void agruparGraficasPorPersonas() {
        agrupacionActual = AgrupacionGraficas.PERSONAS;
        String porPersonas = new String("Por personas");
        agrupacion.setText(porPersonas);
        actualizarGraficas();
    }

    private String obtenerColor(String nombreCategoria) {
        // Si ya tiene color asignado, devuélvelo
        if (mapaColoresCategorias.containsKey(nombreCategoria)) {
            return mapaColoresCategorias.get(nombreCategoria);
        }
        
        // Si es nuevo, asigna el siguiente color disponible (usando módulo para rotar si se acaban)
        int indice = mapaColoresCategorias.size() % PALETA_COLORES.length;
        String nuevoColor = PALETA_COLORES[indice];
        
        mapaColoresCategorias.put(nombreCategoria, nuevoColor);
        return nuevoColor;
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
        barChart.layout();
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        for (Map.Entry<String, Double> e : totales.entrySet()) {
            serie.getData().add(new XYChart.Data<>(e.getKey(), e.getValue()));
        }
        barChart.getData().add(serie);
        // APLICAR COLOR A LAS BARRAS
        for (XYChart.Data<String, Number> data : serie.getData()) {
            Node nodo = data.getNode();
            if (nodo != null) {
                String categoria = data.getXValue();
                String colorHex = obtenerColor(categoria);
                // Propiedad CSS para barras
                nodo.setStyle("-fx-background-color: " + colorHex + ";");
            }
        }

        // Circular
        ObservableList<PieChart.Data> datosPie = FXCollections.observableArrayList();
        for (Map.Entry<String, Double> e : totales.entrySet()) {
            datosPie.add(new PieChart.Data(e.getKey(), e.getValue()));
        }
        pieChart.setData(datosPie);
        for (PieChart.Data data : datosPie) {
            Node nodo = data.getNode();
            if (nodo != null) {
                String categoria = data.getName();
                String colorHex = obtenerColor(categoria);
                // Propiedad CSS para PieChart
                nodo.setStyle("-fx-pie-color: " + colorHex + ";");
            }
        }
    }
    
    @FXML 
    private void personalizarDistribucion() {
    	try {
            Configuracion.getInstancia().getSceneManager().showDistribucionCuentaCompartida(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        
	
}
