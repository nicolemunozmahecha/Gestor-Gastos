package tds.vista;

import java.io.IOException;
import java.io.File;
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
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import tds.Configuracion;
import tds.app.App;
import tds.controlador.GestorGastos;
import tds.modelo.Alerta;
import tds.modelo.Categoria;
import tds.modelo.Cuenta;
import tds.modelo.CuentaCompartida;
import tds.modelo.CuentaPersonal;
import tds.modelo.Gasto;
import tds.modelo.impl.GastoImpl;

public class VentanaPrincipalController {

    @FXML private TabPane tabPane;
    @FXML private MenuController menuController;

    // GASTOS
    @FXML private MenuButton btnCrearGasto;
    @FXML private MenuButton btnEliminarGasto;
    @FXML private MenuItem menuGastoNuevo;
    @FXML private MenuItem menuImportarGasto;
    @FXML private TableView<Gasto> tablaGastos;
    
   
    @FXML private TableColumn<Gasto, String> colDescripcion;
    @FXML private TableColumn<Gasto, String> colNombre;
    @FXML private TableColumn<Gasto, Double> colCantidad;
    @FXML private TableColumn<Gasto, LocalDate> colFecha;
    @FXML private TableColumn<Gasto, String> colCategoria;

    // GRÁFICAS (Principal)
    @FXML private BarChart<String, Number> barChart;
    @FXML private PieChart pieChart;
    
    private GestorGastos gestor;
    private CuentaPersonal principal;
    private List<Cuenta> cuentas;
    private List<Alerta> alertas;
    private List<Categoria> categorias;
    private final String[] PALETA_COLORES = {
        "#ffca28", "#ffa420", "#ca65e2", "#5c6bc0", "#ef5350", 
        "#66bb6a" };

    private Map<String, String> mapaColoresCategorias = new HashMap<>();
    
    @FXML
    public void initialize() {
    	 if (tablaGastos != null) {
    	        configurarTabla();
    	 }
    	 if (menuController != null) {
    	        menuController.setControladorPrincipal(this);
	    }
    	barChart.setLegendVisible(false);
    	pieChart.setLegendVisible(false);
    	barChart.getXAxis().setTickLabelFont(Font.font("Times New Roman", 12));
        barChart.getYAxis().setTickLabelFont(Font.font("Times New Roman", 12));
    }


    public void init(CuentaPersonal principal, List<CuentaCompartida> compartidas) {
        if (principal != null) {
            setCuenta(principal);
        }
        crearPestanasCompartidas(compartidas);

        // Al abrir la ventana principal, refrescar el indicador de notificaciones.
        actualizarIndicadorNotificaciones();
    }

    /**
     * Delegamos en el MenuController para que el indicador (texto del menú)
     * se mantenga actualizado.
     */
    public void actualizarIndicadorNotificaciones() {
        if (menuController != null) {
            menuController.actualizarIndicadorNotificaciones();
        }
    }
    
    public void setCuenta(CuentaPersonal p) {
    	this.principal = p;
        cargarGastos();
    }
    public CuentaPersonal getCuenta() {
    	return principal;
    	
    }

    private void configurarTabla() {
    	// Configurar qué propiedad mostrar en cada columna
    	colNombre.setCellValueFactory(cellData -> 
        	new SimpleStringProperty(cellData.getValue().getNombre())); // o el método que corresponda
    
	    colCantidad.setCellValueFactory(cellData -> 
	        new SimpleDoubleProperty(cellData.getValue().getCantidad()).asObject());
	    
	    colFecha.setCellValueFactory(cellData -> 
	        new SimpleObjectProperty<>(cellData.getValue().getFecha()));
	    
	    colDescripcion.setCellValueFactory(cellData -> 
	        new SimpleStringProperty(cellData.getValue().getDescripcion()));
	    
	    colCategoria.setCellValueFactory(cellData -> 
	        new SimpleStringProperty(cellData.getValue().getCategoria().getNombre()));
        
    }
    
    private void cargarGastos() {
        gestor = Configuracion.getInstancia().getGestorGastos();
        // AQUI ES GASTOS POR CUENTA, LA CUENTA PRINCIPAL ES POR DEFECTO
        List<Gasto> gastos = gestor.getGastosPorCuenta(principal);
        tablaGastos.getItems().clear();
        tablaGastos.getItems().addAll(gastos);

        actualizarGraficas();

        // Por si se han generado nuevas notificaciones al añadir un gasto,
        // refrescamos el indicador del menú.
        actualizarIndicadorNotificaciones();
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

    // Actualiza las gráficas
    private void actualizarGraficas() {
        if (principal == null || barChart == null || pieChart == null) return;

        gestor = Configuracion.getInstancia().getGestorGastos();
        List<Gasto> gastos = gestor.getGastosPorCuenta(principal);

        Map<String, Double> totalPorCategoria = new LinkedHashMap<>();
        for (Gasto g : gastos) {
            String categoria = (g.getCategoria() != null) ? g.getCategoria().getNombre() : "(Sin categoría)";
            totalPorCategoria.merge(categoria, g.getCantidad(), Double::sum);
        }

        // Barras
        barChart.getData().clear();
        barChart.layout();
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        for (Map.Entry<String, Double> e : totalPorCategoria.entrySet()) {
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
        for (Map.Entry<String, Double> e : totalPorCategoria.entrySet()) {
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

   

    // ========== HANDLERS ==========
    @FXML
    public void crearCuenta() {
        try {
            Configuracion.getInstancia().getSceneManager().showCrearCuenta();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void añadirPestañaCuentaCompartida(CuentaCompartida cuenta) {
        añadirPestañaCuentaCompartida(cuenta, true);
    }


    public void añadirPestañaCuentaCompartida(CuentaCompartida cuenta, boolean seleccionar) {
    	// si la pestaña a crear es vacia
        if (tabPane == null) {
            System.err.println("ERROR: tabPane es null!");
            return;
        }

        // Evitar duplicados (por ejemplo, si se recarga la vista)
        for (Tab t : tabPane.getTabs()) {
            if (cuenta.getNombre().equals(t.getText())) {
                if (seleccionar) {
                    tabPane.getSelectionModel().select(t);
                }
                return;
            }
        }
        
        try {
            // Cargar el FXML usando App.class (como en SceneManager)
            FXMLLoader loader = new FXMLLoader(App.class.getResource("cuentaCompartida.fxml"));
            Parent contenido = loader.load();
            
            // Obtener el controlador de la cuenta compartida nueva y pasarle la cuenta
            CuentaCompartidaController controller = loader.getController();
            if (controller != null) {
                controller.inicializar(cuenta);
            }
            
            // Crear la pestaña
            Tab nuevaTab = new Tab(cuenta.getNombre());
            nuevaTab.setContent(contenido);
            nuevaTab.setClosable(true);
            nuevaTab.setUserData(cuenta);
            
            // Añadir al TabPane
            tabPane.getTabs().add(nuevaTab);
            if (seleccionar) {
                tabPane.getSelectionModel().select(nuevaTab);
            }
            
        } catch (IOException e) {
            //System.err.println("ERROR al cargar la vista de cuenta compartida:");
            e.printStackTrace();
        }
    }


    public void cargarPestañasCuentasCompartidasDesdePersistencia() {
        // Mantengo este método por compatibilidad, pero la lógica real está en
        // crearPestanasCompartidas(...) y se alimenta desde el gestor.
        gestor = Configuracion.getInstancia().getGestorGastos();
        crearPestanasCompartidas(gestor.getCuentasCompartidas());
    }

    private void crearPestanasCompartidas(List<CuentaCompartida> compartidas) {
        if (tabPane == null) return;

        // Guardar la pestaña actualmente seleccionada para mantener el contexto
        String pestañaSeleccionada = null;
        Tab tabActual = tabPane.getSelectionModel().getSelectedItem();
        if (tabActual != null) {
            pestañaSeleccionada = tabActual.getText();
        }

        // Limpiar pestañas (dejando "Principal")
        tabPane.getTabs().removeIf(t -> t.getText() != null && !t.getText().equals("Principal"));

        if (compartidas == null) {
            System.out.println("DEBUG UI: No hay cuentas compartidas para crear pestañas");
            return;
        }

        int num = 0;
        for (CuentaCompartida cc : compartidas) {
            añadirPestañaCuentaCompartida(cc, false);
            System.out.println("DEBUG UI: Pestaña creada para cuenta compartida '" + cc.getNombre() + "' con " + cc.getNumeroGastos() + " gastos");
            num++;
        }
        System.out.println("DEBUG UI: Total pestañas cuentas compartidas creadas: " + num);

        // Restaurar selección si existe, si no seleccionar "Principal"
        boolean restored = false;
        if (pestañaSeleccionada != null) {
            for (Tab t : tabPane.getTabs()) {
                if (pestañaSeleccionada.equals(t.getText())) {
                    tabPane.getSelectionModel().select(t);
                    restored = true;
                    break;
                }
            }
        }
        if (!restored) {
            tabPane.getSelectionModel().select(0);
        }
    }
    
    @FXML 
    public void eliminarCuenta() {
        gestor = Configuracion.getInstancia().getGestorGastos();
        cuentas = gestor.getCuentas();
        Menu menu = menuController.getMenuEliminarCuenta();
        menu.getItems().clear();
               
        for (Cuenta c : cuentas) {
            MenuItem item = new MenuItem(c.getNombre());
            item.setUserData(c);
            item.getStyleClass().add("boton-peligro2");
            
            item.setOnAction(e -> {
                Cuenta cuentaAEliminar = (Cuenta) item.getUserData();
                Alert a = new Alert(AlertType.CONFIRMATION, "¿Está seguro que quiere eliminar la cuenta " + cuentaAEliminar.getNombre() + " ?");
                a.showAndWait().ifPresent(r -> {
                    if (r == ButtonType.OK) {
                    	 // Eliminar del gestor
		                boolean eliminada = gestor.eliminarCuenta(cuentaAEliminar);
		                
		                if (eliminada) {
		                    System.out.println("Cuenta eliminada: " + cuentaAEliminar.getNombre());
		                    
		                    // NUEVO: Actualizar la interfaz inmediatamente
		                    if (cuentaAEliminar instanceof CuentaCompartida) {
		                        eliminarPestañaCuentaCompartida((CuentaCompartida) cuentaAEliminar);
		                    }
		                } else {
		                    // Mostrar error si no se pudo eliminar
		                    Alert alert = new Alert(Alert.AlertType.ERROR, "No se pudo eliminar la cuenta: " + cuentaAEliminar.getNombre());
		                    alert.showAndWait();
		                }
                    }
                });
            });
            
            menu.getItems().add(item);
        }
    }


    private void eliminarPestañaCuentaCompartida(CuentaCompartida cuenta) {
        if (tabPane == null) {
            System.err.println("ERROR: tabPane es null! No se puede eliminar la pestaña.");
            return;
        }
        
        // Buscar y eliminar la pestaña que corresponde a esta cuenta
        boolean eliminado = tabPane.getTabs().removeIf(tab -> {
        	return cuenta.getNombre().equals(tab.getText()) ||
                   (tab.getUserData() != null && tab.getUserData().equals(cuenta));
        });
        
        if (eliminado) {
            System.out.println("DEBUG UI: Pestaña eliminada para cuenta '" + cuenta.getNombre() + "'");
            
            // Seleccionar la pestaña "Principal" después de eliminar
            if (!tabPane.getTabs().isEmpty()) {
                tabPane.getSelectionModel().select(0);
            }
        } else {
            System.err.println("DEBUG UI: No se encontró pestaña para cuenta '" + cuenta.getNombre() + "'");
        }
    }
    
    @FXML 
    public void totalCuenta() {
    	try {
            Configuracion.getInstancia().getSceneManager().showTotalCuenta();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML 
    public void filtrarGastos() { 
    	try {
            Configuracion.getInstancia().getSceneManager().showFiltrarGastos();
        } catch (Exception e) {
            e.printStackTrace();
        }
   }
    
    @FXML 
    public void crearCategoria() { 
    	try {
            Configuracion.getInstancia().getSceneManager().showCrearCategoria();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML 
    public void eliminarCategoria() { 
    	gestor = Configuracion.getInstancia().getGestorGastos();
	    categorias = gestor.getCategoriasPersonalizadas();
	    Menu menu = menuController.getMenuEliminarCategoria();
	    menu.getItems().clear();
	           
	    for (Categoria c : categorias) {
	        MenuItem item = new MenuItem(c.getNombre());
	        item.setUserData(c);
            item.getStyleClass().add("boton-peligro2");

	        item.setOnAction(e -> {
	        	Categoria categoriaAEliminar = (Categoria) item.getUserData();
                Alert a = new Alert(AlertType.CONFIRMATION, "¿Está seguro que quiere eliminar la categoria " + categoriaAEliminar.getNombre() + " ?");
	        	a.showAndWait().ifPresent(r -> {
	                    if (r == ButtonType.OK) {
	                    	gestor.eliminarCategoria(categoriaAEliminar);
	                    }
	        	 });
	        });
	        
	        menu.getItems().add(item);
	    }
    }

    
    @FXML 
    public void crearAlerta() { 
    	try {
            Configuracion.getInstancia().getSceneManager().showCrearAlerta();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML 
    public void eliminarAlerta() {
    	gestor = Configuracion.getInstancia().getGestorGastos();
    	alertas = gestor.getAlertas();
        Menu menu = menuController.getMenuEliminarAlerta();
        menu.getItems().clear();

        if (alertas == null || alertas.isEmpty()) {
            MenuItem vacio = new MenuItem("No hay alertas");
            vacio.setDisable(true);
            menu.getItems().add(vacio);
            return;
        }
               
        for (Alerta a : alertas) {
            MenuItem item = new MenuItem(a.getNombre());
            item.setUserData(a);
            item.getStyleClass().add("boton-peligro2");
            
            item.setOnAction(e -> {
                Alerta alertaAEliminar = (Alerta) item.getUserData();
                Alert al = new Alert(AlertType.CONFIRMATION, "¿Está seguro que quiere eliminar la alerta " + alertaAEliminar.getNombre() + " ?");
                al.showAndWait().ifPresent(r -> {
                    if (r == ButtonType.OK) {
                    	 // Eliminar del gestor
		                boolean eliminada = gestor.eliminarAlerta(alertaAEliminar);
		                
		                if (eliminada) {
		                    System.out.println("Alerta eliminada: " + alertaAEliminar.getNombre());

                            // Refrescar el submenú la próxima vez que se abra (y, si sigue visible, también)
                            javafx.application.Platform.runLater(this::eliminarAlerta);
		                } else {
		                    // Mostrar error si no se pudo eliminar
		                    Alert alert = new Alert(Alert.AlertType.ERROR, "No se pudo eliminar la alerta: " + alertaAEliminar.getNombre());
		                    alert.showAndWait();
		                }
                    }
                });
            });
            
            menu.getItems().add(item);
        }
    }

    @FXML 
    public void mostrarHistorial() {
    	try {
            Configuracion.getInstancia().getSceneManager().showMostrarHistorial();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void mostrarAlertas() {
        try {
            Configuracion.getInstancia().getSceneManager().showMostrarAlertas();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public void añadirGastoTabla(GastoImpl g) {
    	tablaGastos.getItems().add(g);            
		actualizarGraficas();
    }
    
    @FXML 
    private void crearGasto() {
    	try {
            Configuracion.getInstancia().getSceneManager().showCrearGasto();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void cargarMenuEliminarGasto() {
        gestor = Configuracion.getInstancia().getGestorGastos();
        btnEliminarGasto.getItems().clear();

        List<Gasto> gastos = gestor.getGastosPorCuenta(principal);

        for (Gasto g : gastos) {
            String texto = String.format("%s - %.2f€ - %s - %s",
                    g.getNombre(), g.getCantidad(), g.getCategoria().getNombre(), g.getFecha());
            MenuItem item = new MenuItem(texto);
            item.setUserData(g);
            item.getStyleClass().add("boton-peligro2");

            item.setOnAction(ev -> {
                Gasto gastoAEliminar = (Gasto) item.getUserData();
                Alert a = new Alert(AlertType.CONFIRMATION, "¿Está seguro que quiere eliminar el gasto " + gastoAEliminar.getNombre() + " ?");
                a.showAndWait().ifPresent(r -> {
                	if (r == ButtonType.OK) {
		                boolean ok = gestor.eliminarGastoDeCuenta(principal, gastoAEliminar);
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
    private void importarGasto() {
        Window w = (tabPane != null && tabPane.getScene() != null) ? tabPane.getScene().getWindow() : null;

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Importar gastos");


        File fichero = chooser.showOpenDialog(w);
        if (fichero == null) {
            return;
        }

        try {
            gestor = Configuracion.getInstancia().getGestorGastos();
            boolean ok = gestor.importarGastos(fichero);

            if (!ok) {
                new Alert(AlertType.ERROR, "No se pudo importar el fichero.").showAndWait();
                return;
            }

            // Recargar pestañas cuentas compartidas (por si se han creado)
            cargarPestañasCuentasCompartidasDesdePersistencia();
            // RRecargar gastos de la principal
            try {
                CuentaPersonal cuentaActualizada = (CuentaPersonal) gestor.getCuentaPorNombre(principal.getNombre());
                setCuenta(cuentaActualizada);
                Configuracion.getInstancia().getSceneManager().setPrincipal((tds.modelo.impl.CuentaPersonalImpl) cuentaActualizada);
            } catch (Exception e) {
                // Si falla, recargar tabla con lo que haya en memoria
                cargarGastos();
            }

            new Alert(AlertType.INFORMATION, "Gastos importados correctamente.").showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(AlertType.ERROR, "No se pudo importar el fichero.").showAndWait();
        }
    }

    public void importarGastosDesdeMenu() {
        importarGasto();
    }

  
    @FXML
    public void salirAplicacion() {
        Alert a = new Alert(AlertType.CONFIRMATION, "¿Salir?");
        a.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) System.exit(0);
        });
    }
}
