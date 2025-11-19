package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
    	
        Parent root = FXMLLoader.load(getClass().getResource("/app/ventanaPrincipal.fxml"));
        primaryStage.setTitle("Gestión de Gastos");
        primaryStage.setScene(new Scene(root, 667, 474));
        primaryStage.show();
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            
        	// TODO: Hacer la línea de comandos (al final)
        } else {

        	launch(args);
        }
    }

}