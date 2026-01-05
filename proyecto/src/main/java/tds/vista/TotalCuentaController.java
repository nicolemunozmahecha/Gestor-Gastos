package tds.vista;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import tds.Configuracion;
import tds.controlador.GestorGastos;
import tds.modelo.Cuenta;
import javafx.scene.control.Button;

public class TotalCuentaController {
	
	@FXML private Label campoTotal;
    @FXML private Button btOK;
    @FXML private MenuButton menuCuentas;
    
	private Cuenta cuentaSeleccionada;
	private GestorGastos gestor;
	private final String TEXTO_DEFECTO = "Cuenta";

    @FXML
    public void initialize() {
        gestor = Configuracion.getInstancia().getGestorGastos();
        cargarCuentas();
    }
    
    private void cargarCuentas() {
    	// Borramos los items para no arriesgarnos a que haya repetidos.
    	//menuCuentas.getItems().clear();
    	
        for (Cuenta cuenta : gestor.getCuentas()) {
            MenuItem item = new MenuItem(cuenta.getNombre());
            item.setOnAction(e -> {
                cuentaSeleccionada = cuenta;
                mostrarTotal();
            });
            menuCuentas.getItems().add(item);
        }
    }
    
    
    // Mostramos en la label el total.
    private void mostrarTotal() {
        if (cuentaSeleccionada != null) {
            double total = gestor.getTotalCuenta(cuentaSeleccionada);
            campoTotal.setText(String.format("%.2f €", total));
            menuCuentas.setText(cuentaSeleccionada.getNombre());

        } else {
            campoTotal.setText("0.00 €");
            menuCuentas.setText(TEXTO_DEFECTO);

        }
    }
    
    @FXML
    private void btOK() {
    	Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }

}
