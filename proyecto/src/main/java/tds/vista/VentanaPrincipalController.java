package tds.vista;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import tds.Configuracion;
import tds.app.App;
import tds.controlador.GestorGastos;
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
    
    private GestorGastos gestor;
    private CuentaPersonal principal;
    private List<Cuenta> cuentas;
    private List<Categoria> categorias;


    @FXML
    public void initialize() {
    	 if (tablaGastos != null) {
    	        configurarTabla();
    	 }
    	 if (menuController != null) {
    	        menuController.setControladorPrincipal(this);
	    }
    }

    /**
     * Inicialización "limpia" de la vista: recibe el modelo ya cargado
     * (principal + cuentas compartidas) y crea las pestañas correspondientes.
     *
     * Esto evita que el controller tenga que consultar al gestor para averiguar
     * qué cuentas hay al arrancar (mejor separación de responsabilidades).
     */
    public void init(CuentaPersonal principal, List<CuentaCompartida> compartidas) {
        if (principal != null) {
            setCuenta(principal);
        }
        crearPestanasCompartidas(compartidas);
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
    // NUEVO: Método público para añadir pestañas desde fuera
    public void añadirPestañaCuentaCompartida(CuentaCompartida cuenta) {
        añadirPestañaCuentaCompartida(cuenta, true);
    }

    /**
     * Añade una pestaña para una cuenta compartida.
     *
     * @param cuenta Cuenta compartida a mostrar
     * @param seleccionar Si true, selecciona la pestaña recién creada
     */
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

    /**
     * Crea (o recrea) las pestañas de todas las cuentas compartidas que haya
     * actualmente en el gestor (lo que viene de la persistencia al arrancar).
     *
     * Lógica:
     * - Deja la pestaña "Principal" tal cual.
     * - Elimina cualquier otra pestaña existente (para evitar duplicados).
     * - Añade una pestaña por cada CuentaCompartida cargada.
     */
    public void cargarPestañasCuentasCompartidasDesdePersistencia() {
        // Mantengo este método por compatibilidad, pero la lógica real está en
        // crearPestanasCompartidas(...) y se alimenta desde el gestor.
        gestor = Configuracion.getInstancia().getGestorGastos();
        crearPestanasCompartidas(gestor.getCuentasCompartidas());
    }

    private void crearPestanasCompartidas(List<CuentaCompartida> compartidas) {
        if (tabPane == null) return;

        // 1) Limpiar pestañas (dejando "Principal")
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
        tabPane.getSelectionModel().select(0);
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
            
            item.setOnAction(e -> {
                Cuenta cuentaAEliminar = (Cuenta) item.getUserData();
                // NUEVO: ELIMINAR PESTAÑA DE LA CUENTA
                if (tabPane != null) {
                    tabPane.getTabs().removeIf(t -> 
                        t.getText().equals(cuentaAEliminar.getNombre())
                    );
                } else {
                    System.err.println("ERROR: tabPane es null");
                }
                
                gestor.eliminarCuenta(cuentaAEliminar);
            });
            
            menu.getItems().add(item);
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
	        
	        item.setOnAction(e -> {
	        	Categoria categoriaAEliminar = (Categoria) item.getUserData();    
	            gestor.eliminarCategoria(categoriaAEliminar);
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
    	//System.out.println("creando Alerta");
    }
    @FXML public void eliminarAlerta() { System.out.println("Eliminar Alerta"); }

    @FXML 
    public void mostrarHistorial() {
    	try {
            Configuracion.getInstancia().getSceneManager().showMostrarHistorial();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public void añadirGastoTabla(GastoImpl g) {
    	tablaGastos.getItems().add(g);            
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
            item.setOnAction(ev -> {
                Gasto gastoAEliminar = (Gasto) item.getUserData();
                boolean ok = gestor.eliminarGastoDeCuenta(principal, gastoAEliminar);
                if (ok) {
                    cargarGastos();
                } else {
                    new Alert(Alert.AlertType.ERROR, "No se pudo eliminar el gasto.").showAndWait();
                }
            });
            btnEliminarGasto.getItems().add(item);
        }
    }
    
    @FXML private void importarGasto() { System.out.println("Importar Gasto"); }

    //@FXML private void mostrarTabla() { System.out.println("Mostrar Tabla"); }
    //@FXML private void mostrarGrafica() { System.out.println("Mostrar Gráfica"); }

    
    @FXML
    public void salirAplicacion() {
        Alert a = new Alert(AlertType.CONFIRMATION, "¿Salir?");
        a.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) System.exit(0);
        });
    }
}
