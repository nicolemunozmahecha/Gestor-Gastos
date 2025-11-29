package umu.tds;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ControlesAccionApp extends Application {

    @Override
    public void start(Stage stage) {
        // --- Barra de menú ---
        Menu menuArchivo = new Menu("Archivo");
        MenuItem nuevo = new MenuItem("Nuevo");
        MenuItem salir = new MenuItem("Salir");

        Menu guardarComo = new Menu("Guardar como");
        MenuItem pdf = new MenuItem("PDF");
        
        Menu menuAyuda = new Menu("Ayuda");
        MenuItem acercaDe = new MenuItem("Acerca de");

        guardarComo.getItems().add(pdf);
        menuArchivo.getItems().addAll(nuevo, guardarComo, salir);
        menuAyuda.getItems().add(acercaDe);

        MenuBar barraMenu = new MenuBar(menuArchivo, menuAyuda);

        // --- Zona central ---
        TextArea salida = new TextArea();
        salida.setPromptText("Aquí aparecerán los mensajes...");
        salida.setWrapText(true);

        Button botonAccion = new Button("Haz clic aquí");
        botonAccion.setOnAction(e -> salida.appendText("Botón pulsado.\n"));

        // --- Asignar acciones a los menús ---
        nuevo.setOnAction(e -> salida.appendText("Nuevo archivo creado.\n"));
        salir.setOnAction(e -> stage.close());
        acercaDe.setOnAction(e ->
                mostrarAlerta("Aplicación de ejemplo", "Versión 1.0\nCreada con JavaFX.")
        );

        // --- Layout principal ---
        BorderPane root = new BorderPane();
        root.setTop(barraMenu);
        root.setCenter(salida);
        root.setBottom(botonAccion);
        BorderPane.setAlignment(botonAccion, javafx.geometry.Pos.CENTER);
        root.setStyle("-fx-padding: 10;");

        // --- Escena ---
        Scene scene = new Scene(root, 400, 300);
        stage.setTitle("Ejemplo de controles de acción");
        stage.setScene(scene);
        stage.show();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}