package tds.vista;

import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.property.SimpleStringProperty;

import tds.Configuracion;
import tds.adapters.repository.exceptions.ErrorPersistenciaException;
import tds.controlador.GestorGastos;
import tds.modelo.Notificacion;

public class MostrarHistorialController {
	private GestorGastos gestor;

    @FXML private TableView<Notificacion> tablaNotificaciones;
    @FXML private TableColumn<Notificacion, String> colId;
    @FXML private TableColumn<Notificacion, String> colMensaje;
    @FXML private TableColumn<Notificacion, String> colFecha;
    @FXML private TableColumn<Notificacion, String> colAlerta;
    @FXML private TableColumn<Notificacion, String> colLeida;

    private ObservableList<Notificacion> datosTabla;

    @FXML
    public void initialize() {
        gestor = Configuracion.getInstancia().getGestorGastos();
        configurarTabla();
        datosTabla = FXCollections.observableArrayList();
        tablaNotificaciones.setItems(datosTabla);
        cargarNotificaciones();
    }

    private void configurarTabla() {
        if (colId != null) {
            colId.setCellValueFactory(c -> {
                String id = c.getValue().getId();
                String corto = (id == null) ? "" : (id.length() > 8 ? id.substring(0, 8) : id);
                return new SimpleStringProperty(corto);
            });
        }
        if (colMensaje != null) {
            colMensaje.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMensaje()));
        }
        if (colFecha != null) {
            colFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechaHoraFormateada()));
        }
        if (colAlerta != null) {
            colAlerta.setCellValueFactory(c -> {
                String nombre = (c.getValue().getAlertaOrigen() == null) ? "" : c.getValue().getAlertaOrigen().getNombre();
                return new SimpleStringProperty(nombre);
            });
        }
        if (colLeida != null) {
            colLeida.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isLeida() ? "Sí" : "No"));
        }
    }

    private void cargarNotificaciones() {
        datosTabla.setAll(gestor.getNotificaciones());
        // Más recientes primero, si no podemos hacer leídas/no leídas
        datosTabla.sort((a, b) -> b.getFechaHora().compareTo(a.getFechaHora()));
        tablaNotificaciones.refresh();
    }

    @FXML
    private void marcarSeleccionada() {
        Notificacion seleccionada = tablaNotificaciones.getSelectionModel().getSelectedItem();
        if (seleccionada == null) return;
        gestor.marcarNotificacionComoLeida(seleccionada);
        // Recargamos y forzamos refresh para que la tabla se actualice al instante
        // (sin salir/entrar), incluso aunque el objeto no exponga JavaFX properties.
        cargarNotificaciones();
    }

    @FXML
    private void marcarTodas() {
        gestor.marcarTodasNotificacionesComoLeidas();
        cargarNotificaciones();
    }

    @FXML
    private void eliminarSeleccionada() throws ErrorPersistenciaException {
        Notificacion seleccionada = tablaNotificaciones.getSelectionModel().getSelectedItem();
        if (seleccionada == null) return;

        gestor.eliminarNotificacion(seleccionada);
        cargarNotificaciones();
    }
    
    @FXML
    private void atras() {
    	Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }
}
