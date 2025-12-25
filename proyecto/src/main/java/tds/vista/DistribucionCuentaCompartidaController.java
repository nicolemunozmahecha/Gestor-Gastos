package tds.vista;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import tds.Configuracion;
import tds.controlador.GestorGastos;
import tds.modelo.CuentaCompartida;
import tds.modelo.EstrategiaDistribucion;
import tds.modelo.Persona;
import tds.modelo.impl.DistribucionPersonalizadaImpl;


public class DistribucionCuentaCompartidaController {
    private GestorGastos gestor;

    @FXML private Label persona1;
    @FXML private Label persona2;
    @FXML private Label persona3;
    @FXML private Label persona4;
    @FXML private Label persona5;
    @FXML private Label persona6;

    // Estos TextField contienen los porcentajes
    @FXML private TextField propietario1;
    @FXML private TextField propietario2;
    @FXML private TextField propietario3;
    @FXML private TextField propietario4;
    @FXML private TextField propietario5;
    @FXML private TextField propietario6;

    @FXML private TableView<Persona> tablaDistribucion;
    @FXML private TableColumn<Persona, String> colNombre;
    @FXML private TableColumn<Persona, Integer> colDistribucion;

    private List<Persona> personas;
    private CuentaCompartidaController cuentaCompartidaController;

    @FXML
    public void initialize() {
        gestor = Configuracion.getInstancia().getGestorGastos();
        configurarTabla();
    }

    public void setCuentaCompartidaController(CuentaCompartidaController controller) {
        this.cuentaCompartidaController = controller;
        this.personas = cuentaCompartidaController.getPersonasCuenta();

        // Si nunca se abrió (o no hay porcentajes válidos), fijamos una distribución válida por defecto.
        int suma = 0;
        boolean hayAlguno = false;
        for (Persona p : personas) {
            suma += p.getPorcentaje();
            if (p.getPorcentaje() != 0) hayAlguno = true;
        }
        if (!hayAlguno || suma != 100) {
            cargarPorcentajesIguales();
        }

        cargarPersonas();
        cargarLabelsYCampos();
    }

    private void cargarPorcentajesIguales() {
        int n = personas.size();
        if (n == 0) return;
        int base = 100 / n;
        int resto = 100 % n;
        for (int i = 0; i < n; i++) {
            personas.get(i).setPorcentaje(base + (i < resto ? 1 : 0));
        }
    }

    private void configurarTabla() {
        colNombre.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNombre()));

        colDistribucion.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getPorcentaje()).asObject());
    }

    private void cargarPersonas() {
        tablaDistribucion.getItems().clear();
        tablaDistribucion.getItems().addAll(personas);
    }

    private void cargarLabelsYCampos() {
        List<Label> etiquetas = Arrays.asList(persona1, persona2, persona3, persona4, persona5, persona6);
        List<TextField> campos = Arrays.asList(propietario1, propietario2, propietario3, propietario4, propietario5, propietario6);

        for (int i = 0; i < etiquetas.size(); i++) {
            Label label = etiquetas.get(i);
            TextField campo = campos.get(i);

            if (i < personas.size()) {
                label.setText(personas.get(i).getNombre());
                label.setVisible(true);
                campo.setVisible(true);
                campo.setText(String.valueOf(personas.get(i).getPorcentaje()));
            } else {
                label.setText("");
                label.setVisible(false);
                campo.setVisible(false);
                campo.setText("");
            }
        }
    }

    @FXML
    private void personalizarDistribucion() {
        if (cuentaCompartidaController == null || personas == null) {
            return;
        }

        List<TextField> campos = Arrays.asList(propietario1, propietario2, propietario3, propietario4, propietario5, propietario6);

        int sumaTotal = 0;
        try {
            for (int i = 0; i < personas.size(); i++) {
                String raw = campos.get(i).getText().trim();
                if (raw.isEmpty()) {
                    throw new IllegalArgumentException("Faltan porcentajes por completar");
                }
                int valor = Integer.parseInt(raw);
                if (valor < 0) {
                    throw new IllegalArgumentException("Los porcentajes no pueden ser negativos");
                }
                personas.get(i).setPorcentaje(valor);
                sumaTotal += valor;
            }
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Por favor, introduce porcentajes enteros válidos.").showAndWait();
            return;
        } catch (IllegalArgumentException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
            return;
        }

        if (sumaTotal != 100) {
            new Alert(Alert.AlertType.ERROR,
                    "Los porcentajes deben sumar 100%. Suma actual: " + sumaTotal + "%").showAndWait();
            return;
        }

        try {
          
            EstrategiaDistribucion estrategia = gestor.crearEstrategiaDistribucion(
                    DistribucionPersonalizadaImpl.ID,
                    new ArrayList<>(personas)
            );

            CuentaCompartida cuenta = cuentaCompartidaController.getCuenta();
            boolean ok = gestor.actualizarEstrategiaDistribucion(cuenta, estrategia);
            if (!ok) {
                new Alert(Alert.AlertType.ERROR, "No se pudo guardar la distribución.").showAndWait();
                return;
            }

            Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error al aplicar la distribución: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void atras() {
        Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }
}
