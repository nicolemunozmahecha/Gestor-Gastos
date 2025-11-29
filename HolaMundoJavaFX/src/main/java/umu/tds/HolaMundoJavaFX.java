package umu.tds;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class HolaMundoJavaFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Crear un nodo (en este caso, una etiqueta)
        Label etiqueta = new Label("Hola Mundo con JavaFX");

        // Crear un contenedor y añadir la etiqueta
        FlowPane root = new FlowPane();
        root.getChildren().add(etiqueta);

        // Crear la escena con el contenedor
        Scene scene = new Scene(root, 300, 200);

        // Configurar la ventana principal (Stage)
        primaryStage.setTitle("Mi primera ventana en JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}