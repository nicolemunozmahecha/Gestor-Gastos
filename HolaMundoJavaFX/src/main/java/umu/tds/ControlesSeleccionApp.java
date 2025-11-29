package umu.tds;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ControlesSeleccionApp extends Application {

    @Override
    public void start(Stage stage) {
        // --- ListView ---
        Label labelLista = new Label("Lenguajes preferidos:");
        ListView<String> listaLenguajes = new ListView<>();
        listaLenguajes.getItems().addAll("Java", "Python", "Kotlin", "C#", "C++");
        listaLenguajes.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listaLenguajes.setPrefHeight(100);
        //listaLenguajes.setMinHeight(100);

        // --- ComboBox ---
        Label labelCombo = new Label("País de residencia:");
        ComboBox<String> comboPais = new ComboBox<>();
        comboPais.getItems().addAll("España", "Francia", "Italia", "Alemania");
        comboPais.setPromptText("Selecciona un país");

        // --- CheckBox ---
        CheckBox checkSuscripcion = new CheckBox("Suscribirme a las noticias");

        // --- RadioButtons ---
        Label labelGenero = new Label("Género:");
        RadioButton hombre = new RadioButton("Hombre");
        RadioButton mujer = new RadioButton("Mujer");
        RadioButton otro = new RadioButton("Otro");

        ToggleGroup grupoGenero = new ToggleGroup();
        hombre.setToggleGroup(grupoGenero);
        mujer.setToggleGroup(grupoGenero);
        otro.setToggleGroup(grupoGenero);

        HBox boxGenero = new HBox(10, hombre, mujer, otro);

        // --- Botón de acción ---
        Button botonMostrar = new Button("Mostrar selección");
        TextArea resultado = new TextArea();
        resultado.setEditable(false);
        resultado.setWrapText(true);

        botonMostrar.setOnAction(e -> {
            StringBuilder sb = new StringBuilder();

            sb.append("Lenguajes seleccionados: ")
              .append(listaLenguajes.getSelectionModel().getSelectedItems())
              .append("\n");

            sb.append("País: ")
              .append(comboPais.getValue() != null ? comboPais.getValue() : "Ninguno")
              .append("\n");

            sb.append("Suscripción: ")
              .append(checkSuscripcion.isSelected() ? "Sí" : "No")
              .append("\n");

            Toggle seleccionado = grupoGenero.getSelectedToggle();
            sb.append("Género: ")
              .append(seleccionado != null ? ((RadioButton) seleccionado).getText() : "Sin especificar");

            resultado.setText(sb.toString());
        });

        // --- Layout principal ---
        VBox root = new VBox(10,
                labelLista, listaLenguajes,
                labelCombo, comboPais,
                labelGenero, boxGenero,
                checkSuscripcion,
                botonMostrar, resultado
        );
        root.setPadding(new Insets(15));
        root.setStyle("-fx-font-family: 'Segoe UI';");

        // --- Escena ---
        Scene scene = new Scene(root, 400, 500);
        stage.setTitle("Ejemplo de controles de selección en JavaFX");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
