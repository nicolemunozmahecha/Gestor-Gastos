package tds.vista;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import tds.Configuracion;

public class VentanaPrincipalController {

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

    @FXML private MenuButton btnVisualizacion;
    @FXML private MenuItem menuVerTabla;
    @FXML private MenuItem menuVerGrafica;

    @FXML
    public void initialize() {
    }

    // ========== HANDLERS ==========

    @FXML
    private void crearCuenta() {
        try {
            Configuracion.getInstancia().getSceneManager().showCrearCuenta();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML private void eliminarCuenta() { System.out.println("Eliminar Cuenta"); }
    @FXML private void mostrarMisCuentas() { System.out.println("Mis Cuentas"); }

    @FXML private void crearCategoria() { System.out.println("Crear Categoria"); }
    @FXML private void eliminarCategoria() { System.out.println("Eliminar Categoria"); }

    @FXML private void crearAlerta() { System.out.println("Crear Alerta"); }
    @FXML private void eliminarAlerta() { System.out.println("Eliminar Alerta"); }

    @FXML private void mostrarHistorial() { System.out.println("Historial Notificaciones"); }

    @FXML private void crearGastoNuevo() { System.out.println("Crear Gasto Nuevo"); }
    @FXML private void importarGasto() { System.out.println("Importar Gasto"); }

    @FXML private void mostrarTabla() { System.out.println("Mostrar Tabla"); }
    @FXML private void mostrarGrafica() { System.out.println("Mostrar Gráfica"); }

    @FXML
    private void salirAplicacion() {
        Alert a = new Alert(AlertType.CONFIRMATION, "¿Salir?");
        a.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) System.exit(0);
        });
    }
}
