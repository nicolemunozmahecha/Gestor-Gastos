package tds.vista;

import java.io.IOException;
import java.io.File;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

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
import tds.adapters.repository.exceptions.ElementoExistenteException;
import tds.adapters.repository.exceptions.ErrorPersistenciaException;
import tds.app.App;
import tds.controlador.GestorGastos;
import tds.importacion.ImportacionException;
import tds.modelo.Alerta;
import tds.modelo.Categoria;
import tds.modelo.Cuenta;
import tds.modelo.CuentaCompartida;
import tds.modelo.CuentaPersonal;
import tds.modelo.Gasto;

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
        gestor = Configuracion.getInstancia().getGestorGastos();
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
    
    public void setCuenta(Cuenta cuentaActualizada) {
    	this.principal = (CuentaPersonal) cuentaActualizada;
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

        //gestor = Configuracion.getInstancia().getGestorGastos();
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

    /**
     * Método genérico para llenar menús de eliminación.
     
     * @param itemsMenu       Items del menu (menu.getItems()).
     * @param listaObjetos    Datos que queremos eliminar (List<Cuenta>, List<Gasto>, etc).
     * @param obtenerTexto    Función para sacar el nombre (ej: Cuenta::getNombre).
     * @param accionEliminar  Función que llama al gestor y devuelve true/false.
     * @param nombreEntidad   Nombre para los mensajes (ej: "cuenta", "gasto").
     * @param alTerminar      Acción para ejecutar tras borrar 
     * @param <T>             El tipo de objeto (Gasto, Cuenta, Alerta...).
     */
    private <T> void configurarMenuBorrado( ObservableList<MenuItem> itemsMenu, List<T> listaObjetos, Function<T, String> obtenerTexto,
            Predicate<T> accionEliminar, String nombreEntidad, Consumer<T> alTerminar) {
        itemsMenu.clear();

        if (listaObjetos == null || listaObjetos.isEmpty()) {
            MenuItem vacio = new MenuItem("No hay " + nombreEntidad + "s");
            vacio.setDisable(true);
            itemsMenu.add(vacio);
            return;
        }

        for (T objeto : listaObjetos) {
            // 1. Obtenemos items a mostrar
            String texto = obtenerTexto.apply(objeto);
            MenuItem item = new MenuItem(texto);
            item.getStyleClass().add("boton-peligro2");

            item.setOnAction(e -> {
                Alert alert = new Alert(AlertType.CONFIRMATION, "¿Está seguro que quiere eliminar la " + nombreEntidad + ": " + texto + "?");
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        try {
                            // 3. Llamamos al gestor
                            boolean eliminado = accionEliminar.test(objeto);
                            
                            if (eliminado) {
                                mostrarExito(nombreEntidad.substring(0, 1).toUpperCase() + nombreEntidad.substring(1) + " eliminada correctamente.");
                                // 4. Actualizar
                                if (alTerminar != null) {
                                    alTerminar.accept(objeto);
                                }
                                itemsMenu.remove(item); 
                            } else {
                                mostrarAdvertencia("No se pudo eliminar: " + texto);
                            }
                        } catch (Exception ex) {
                            mostrarError("Error al eliminar", ex.getMessage());
                        }
                    }
                });
            });
            itemsMenu.add(item);
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
            e.printStackTrace();
        }
    }


    public void cargarPestañasCuentasCompartidasDesdePersistencia() {
        // Mantengo este método por compatibilidad, pero la lógica real está en
        // crearPestanasCompartidas(...) y se alimenta desde el gestor.
        //gestor = Configuracion.getInstancia().getGestorGastos();
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
            return;
        }

        for (CuentaCompartida cc : compartidas) {
            añadirPestañaCuentaCompartida(cc, false);
        }

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
    
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    private void mostrarExito(String mensaje) {
        new Alert(AlertType.INFORMATION, mensaje).showAndWait();
    }
    
    private void mostrarAdvertencia(String mensaje) {
        new Alert(AlertType.WARNING, mensaje).showAndWait();
    }
    
    @FXML 
    public void eliminarCuenta() {
	    List<Cuenta> cuentas = gestor.getCuentas();
	    List<Cuenta> compartidas = cuentas.stream()
				.filter(c -> c instanceof CuentaCompartida)
				.collect(Collectors.toList());
	    
	    configurarMenuBorrado(
	        menuController.getMenuEliminarCuenta().getItems(), // Dónde poner los items
	        compartidas,                                       // lista a recorrer
	        Cuenta::getNombre,                                 // Cómo obtener el texto
	        c -> {                                             
	            try { 
	            	return gestor.eliminarCuenta(c); 
	            } 
	            catch (Exception e) { 
	            	throw new RuntimeException(e); 
	            }
	        }, "cuenta",                                        // Nombre para alertas
	        cuentaAEliminar -> {                                 
	            if (cuentaAEliminar instanceof CuentaCompartida) {
	                eliminarPestañaCuentaCompartida((CuentaCompartida) cuentaAEliminar);
	            }	            
	        }
	    );
	}
       
    private void eliminarPestañaCuentaCompartida(CuentaCompartida cuenta) {
        if (tabPane == null) {
            return;
        }
        
        // Buscar y eliminar la pestaña que corresponde a esta cuenta
        boolean eliminado = tabPane.getTabs().removeIf(tab -> {
        	return cuenta.getNombre().equals(tab.getText()) ||
                   (tab.getUserData() != null && tab.getUserData().equals(cuenta));
        });
        
        if (eliminado) {            
            // Seleccionar la pestaña "Principal" después de eliminar
            if (!tabPane.getTabs().isEmpty()) {
                tabPane.getSelectionModel().select(0);
            }
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
        configurarMenuBorrado(
            menuController.getMenuEliminarCategoria().getItems(),
            gestor.getCategoriasPersonalizadas(),
            Categoria::getNombre,
            cat -> {
                 try { 
                	 return gestor.eliminarCategoria(cat); 
                	} 
                 catch (Exception e) { 
 	            	throw new RuntimeException(e); 
                 }
            },
            "categoría",
            categoriaBorrada -> { 
                // Acciones extra si fueran necesarias, como recargar gráficas
                actualizarGraficas();
            }
        );
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
        configurarMenuBorrado(
            menuController.getMenuEliminarAlerta().getItems(),
            gestor.getAlertas(),
            Alerta::getNombre,
            alerta -> {
                try { 
                	return gestor.eliminarAlerta(alerta); 
                }
                catch (Exception e) { 
                	throw new RuntimeException(e); 
                }
            },
            "alerta",
            alertaBorrada -> {
                javafx.application.Platform.runLater(this::eliminarAlerta);
            }
        );
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

    
    public void añadirGastoTabla(Gasto g) {
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
    	List<Gasto> todosLosGastos = gestor.getGastosPorCuenta(principal);
        configurarMenuBorrado(
            btnEliminarGasto.getItems(),
            todosLosGastos,
            g -> String.format("%s - %.2f€ - %s", g.getNombre(), g.getCantidad(), g.getFecha()),
            g -> {
                try { 
                	return gestor.eliminarGastoDeCuenta(principal, g); 
                } 
                catch (Exception e) { 
                	throw new RuntimeException(e); 
                }
            },
            "gasto",
            gastoBorrado -> cargarGastos() 
        );
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
            boolean ok = gestor.importarGastos(fichero);

            if (ok) {
            	// Recargar pestañas cuentas compartidas (por si se han creado)
                // Recargar gastos de la principal
            	cargarPestañasCuentasCompartidasDesdePersistencia();
            	CuentaPersonal cuentaActualizada = (CuentaPersonal) gestor.getCuentaPorNombre(principal.getNombre());
                setCuenta(cuentaActualizada);
                Configuracion.getInstancia().getSceneManager().setPrincipal((tds.modelo.impl.CuentaPersonalImpl) cuentaActualizada);
            }else {
                mostrarAdvertencia("No se pudo importar el fichero: " + fichero.getName());
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
