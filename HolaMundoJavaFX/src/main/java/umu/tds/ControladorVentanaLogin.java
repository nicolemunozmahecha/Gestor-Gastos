package umu.tds;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ControladorVentanaLogin {

		@FXML
		TextField nombreUsuarioTF;
		
		@FXML
		public void hacerLogin() {
			
			System.out.println("Hacer algo");
			System.out.println(nombreUsuarioTF.getText());
		}
}
