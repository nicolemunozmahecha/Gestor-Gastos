package tds.vista;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import tds.Configuracion;
import tds.controlador.GestorGastos;
import tds.modelo.Alerta;


public class MostrarAlertasController {

    private GestorGastos gestor;

    @FXML private TableView<Alerta> tablaAlertas;
    @FXML private TableColumn<Alerta, String> colNombre;
    @FXML private TableColumn<Alerta, String> colLimite;
    @FXML private TableColumn<Alerta, String> colPeriodo;
    @FXML private TableColumn<Alerta, String> colCategoria;

    private ObservableList<Alerta> datosTabla;

    @FXML
    public void initialize() {
        gestor = Configuracion.getInstancia().getGestorGastos();
        configurarTabla();
        cargarAlertas();
    }

    private void configurarTabla() {
        if (colNombre != null) {
            colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        }
        if (colLimite != null) {
            colLimite.setCellValueFactory(c -> new SimpleStringProperty(String.format("%.2f€", c.getValue().getLimite())));
        }
        if (colPeriodo != null) {
            colPeriodo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstrategia().getNombrePeriodo()));
        }
        if (colCategoria != null) {
            colCategoria.setCellValueFactory(c -> {
                String txt = c.getValue().esGeneral() ? "General" : c.getValue().getCategoria().getNombre();
                return new SimpleStringProperty(txt);
            });
        }
    }

    private void cargarAlertas() {
        datosTabla = FXCollections.observableArrayList(gestor.getAlertas());
        tablaAlertas.setItems(datosTabla);
    }

    @FXML
    private void eliminarSeleccionada() {
        Alerta seleccionada = tablaAlertas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) return;

        gestor.eliminarAlerta(seleccionada);
        // Recarga para que la tabla se actualice automáticamente
        cargarAlertas();
    }

    @FXML
    private void atras() {
        Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }
}
