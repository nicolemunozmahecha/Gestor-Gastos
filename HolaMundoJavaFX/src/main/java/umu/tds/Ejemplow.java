package umu.tds;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Ejemplow extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Nodos hoja (controles)
        Label etiqueta = new Label("Nombre:");
        TextField campoTexto = new TextField();
        Button botonAceptar = new Button("Aceptar");

        // Contenedor horizontal para la etiqueta y el campo de texto
        HBox hbox = new HBox(10); 
        hbox.getChildren().addAll(etiqueta, campoTexto);

        // Contenedor vertical que contiene el HBox y el botón
        VBox vbox = new VBox(15); 
        vbox.getChildren().addAll(hbox, botonAceptar);

        // Crear la escena con el nodo raíz (vbox)
        Scene escena = new Scene(vbox, 300, 150);

        // Configurar y mostrar el Stage principal
        primaryStage.setTitle("Ejemplo de Scene Graph");
        primaryStage.setScene(escena);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}