package umu.tds;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EjemploProperties extends Application{

	TextField origenBindTF         = new TextField();
	TextField destinoBindTF        = new TextField();
	TextField bindBidireccional1TF = new TextField();
	TextField bindBidireccional2TF = new TextField();
	TextArea  salidaListenersTA    = new TextArea();
	ToggleButton bindBT            = new ToggleButton();
	Label unidireccionalL          = new Label();
	Label bidireccionalL           = new Label();
	
	@Override
    public void start(Stage primaryStage) throws Exception {
		
		VBox panelGeneral = new VBox(20);
		GridPane panelCampos = new GridPane();
		panelGeneral.setPadding(new Insets(10,10,10,10));
		panelCampos.setHgap(10);
		panelCampos.setVgap(10);
		
		unidireccionalL.setText("----- binding unidireccional ---->");
		bidireccionalL.setText("<---- binding bidireccional ----->");
		
		panelCampos.add(origenBindTF, 0, 0);
		panelCampos.add(unidireccionalL, 1, 0);
		panelCampos.add(destinoBindTF, 2, 0);
		
		panelCampos.add(bindBidireccional1TF, 0, 1);
		panelCampos.add(bidireccionalL, 1, 1);
		panelCampos.add(bindBidireccional2TF, 2, 1);
		
		bindBT.setText("Bind/Unbind");
		bindBT.setOnAction(e -> {cambiarBindings();});
		
		panelGeneral.getChildren().addAll(panelCampos, salidaListenersTA, bindBT);
				
		// Change listeners
		origenBindTF.textProperty().addListener(
				(prop, oldVal, newVal) -> {salidaListenersTA.appendText("origenBindTF cambiado de "+oldVal+" a "+newVal+"\n");}
		);
		destinoBindTF.textProperty().addListener(
				(prop, oldVal, newVal) -> {salidaListenersTA.appendText("destinoBindTF cambiado de "+oldVal+" a "+newVal+"\n");}
		);
		bindBidireccional1TF.textProperty().addListener(
				(prop, oldVal, newVal) -> {salidaListenersTA.appendText("bindBidireccional1TF cambiado de "+oldVal+" a "+newVal+"\n");}
		);
		bindBidireccional2TF.textProperty().addListener(
				(prop, oldVal, newVal) -> {salidaListenersTA.appendText("bindBidireccional2TF cambiado de "+oldVal+" a "+newVal+"\n");}
		);
		
		// Otro ejemplo de binding: 
		// Estado visible de las etiquetas enlazado con el estado del botón
		// De esta forma ahorramos implementar varios listeners
		unidireccionalL.visibleProperty().bind(bindBT.selectedProperty());
		bidireccionalL.visibleProperty().bind(bindBT.selectedProperty());

		// Crear la escena con el contenedor
        Scene scene = new Scene(panelGeneral, 550, 200);
        // Configurar la ventana principal (Stage)
        primaryStage.setTitle("Ejemplo sencillo de JavaFX properties");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
	
	private void cambiarBindings() {
		if (bindBT.isSelected()) {
			// Binding unidirectional
			destinoBindTF.textProperty().bind(origenBindTF.textProperty());
			
			// Binding bidirectional
			bindBidireccional1TF.textProperty().bindBidirectional(bindBidireccional2TF.textProperty());
			
			// Avisamos en la salida 
			salidaListenersTA.appendText("---- BINDINGS ACTIVOS ---\n");
		} else {
			// Quitamos los bindings
			destinoBindTF.textProperty().unbind();
			bindBidireccional1TF.textProperty().unbindBidirectional(bindBidireccional2TF.textProperty());

			// Avisamos en la salida 			
			salidaListenersTA.appendText("---- BINDINGS DESACTIVADOS ---\n");
		}		
	}
	
    public static void main(String[] args) {
        launch(args);
    }
}