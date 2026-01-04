package tds.vista;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import tds.Configuracion;
import tds.controlador.GestorGastos;
import tds.modelo.*;
import tds.modelo.impl.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FiltrarGastosController {
    
    // CheckBoxes de meses
    @FXML private CheckBox checkEnero;
    @FXML private CheckBox checkFebrero;
    @FXML private CheckBox checkMarzo;
    @FXML private CheckBox checkAbril;
    @FXML private CheckBox checkMayo;
    @FXML private CheckBox checkJunio;
    @FXML private CheckBox checkJulio;
    @FXML private CheckBox checkAgosto;
    @FXML private CheckBox checkSeptiembre;
    @FXML private CheckBox checkOctubre;
    @FXML private CheckBox checkNoviembre;
    @FXML private CheckBox checkDiciembre;
    
    // DatePickers
    @FXML private DatePicker fechaDesde;
    @FXML private DatePicker fechaHasta;
    
    // ListView de categorías
    @FXML private ListView<Categoria> listaCategorias;
    
    // Tabla de gastos
    @FXML private TableView<Gasto> tablaGastos;
    @FXML private TableColumn<Gasto, String> colNombre;
    @FXML private TableColumn<Gasto, Double> colCantidad;
    @FXML private TableColumn<Gasto, LocalDate> colFecha;
    @FXML private TableColumn<Gasto, String> colDescripcion;
    @FXML private TableColumn<Gasto, String> colCategoria;
    @FXML private TableColumn<Gasto, String> colPagador;
    
    // Label de filtros activos
    @FXML private Label labelFiltrosActivos;
    
    // Botones
    @FXML private Button btnAplicarFiltros;
    @FXML private Button btnLimpiar;
    
    private GestorGastos gestor;
    private List<CheckBox> todosLosMeses;

    @FXML
    public void initialize() {
        gestor = Configuracion.getInstancia().getGestorGastos();
        configurarTabla();
        cargarCategorias();
        cargarGastos();
        todosLosMeses = List.of(checkEnero, checkFebrero, checkMarzo, checkAbril, 
                checkMayo, checkJunio, checkJulio, checkAgosto, 
                checkSeptiembre, checkOctubre, checkNoviembre, checkDiciembre);
    }
    
    
    private void configurarTabla() {
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
        
        colPagador.setCellValueFactory(cellData -> {
            // Si el gasto pertenece a una cuenta compartida, mostrar el pagador
            if (cellData.getValue() instanceof GastoImpl) {
                GastoImpl gasto = (GastoImpl) cellData.getValue();
                if (gasto.getPagador() != null) {
                    return new SimpleStringProperty(gasto.getPagador().getNombre());
                }
            }
            return new SimpleStringProperty("-");
        });
    }
    
    private void cargarCategorias() {
        // Permitir selección múltiple
        listaCategorias.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        // Cargar categorías desde el gestor
        List<Categoria> categorias = gestor.getCategorias();
        listaCategorias.getItems().addAll(categorias);
    }
    
    private void cargarGastos() {
        // Cargar todos los gastos del sistema desde el gestor
        List<Gasto> gastos = gestor.getGastos();
        tablaGastos.getItems().clear();
        tablaGastos.getItems().addAll(gastos);
    }
    

    @FXML
    private void aplicarFiltros() {
        // Recopila criterios de filtrado desde la vista
        List<String> mesesSeleccionados = obtenerMesesSeleccionados();
        LocalDate inicio = fechaDesde.getValue();
        LocalDate fin = fechaHasta.getValue();
        List<Categoria> categoriasSeleccionadas = 
            new ArrayList<>(listaCategorias.getSelectionModel().getSelectedItems());
        
        // Delega al controlador la creación del filtro compuesto
        Filtro filtro = gestor.crearFiltroCompuesto(
            mesesSeleccionados, 
            inicio, 
            fin, 
            categoriasSeleccionadas
        );
        
        List<Gasto> gastosFiltrados = gestor.getGastosFiltrados(filtro);
        
        // Actualizar la vista con los resultados
        tablaGastos.getItems().clear();
        tablaGastos.getItems().addAll(gastosFiltrados);
        
        actualizarLabelFiltrosActivos(filtro);
    }
    
    /**
     * Obtiene los meses seleccionados de los CheckBoxes.
     */
    private List<String> obtenerMesesSeleccionados() {
        List<String> meses = new ArrayList<>();
        
        for (int i = 0; i < todosLosMeses.size(); i++) {
            if (todosLosMeses.get(i).isSelected()) {
                meses.add(String.valueOf(i + 1));
            }
        }
        return meses;
    }
    
    /**
     * Actualiza el label que muestra los filtros activos.
     */
    private void actualizarLabelFiltrosActivos(Filtro filtro) {
        if (!filtro.hayFiltrosActivos()) {
            labelFiltrosActivos.setText("Ningún filtro aplicado");
            return;
        }
        
        List<String> descripciones = new ArrayList<>();
        
        // Mostrar meses si están activos
        List<String> meses = obtenerMesesSeleccionados();
        if (!meses.isEmpty()) {
            descripciones.add("Meses: " + meses.size() + " seleccionado(s)");
        }
        
        // Mostrar rango de fechas
        LocalDate inicio = fechaDesde.getValue();
        LocalDate fin = fechaHasta.getValue();
        if (inicio != null || fin != null) {
            String rango = "Fechas: ";
            if (inicio != null) rango += "desde " + inicio;
            if (inicio != null && fin != null) rango += " ";
            if (fin != null) rango += "hasta " + fin;
            descripciones.add(rango);
        }
        
        // Mostrar categorías
        List<Categoria> categorias = listaCategorias.getSelectionModel().getSelectedItems();
        if (!categorias.isEmpty()) {
            descripciones.add("Categorías: " + categorias.size() + " seleccionada(s)");
        }
        
        labelFiltrosActivos.setText(String.join(" | ", descripciones));
    }
    
    /**
     * Limpia todos los filtros y restaura la vista original.
     */
    @FXML
    private void limpiarFiltros() {
        // Limpiar CheckBoxes de meses
    	todosLosMeses.forEach(cb -> cb.setSelected(false));
        
        // Limpiar DatePickers
        fechaDesde.setValue(null);
        fechaHasta.setValue(null);
        
        // Limpiar selección de categorías
        listaCategorias.getSelectionModel().clearSelection();
        
        // Recargar gastos originales desde el gestor
        cargarGastos();
        
        // Actualizar label
        labelFiltrosActivos.setText("Ningún filtro aplicado");
    }
    
    @FXML
    private void atras() {
        Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }
}
