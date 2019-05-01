package application;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class WizardController {
	
	
	@FXML TextField equityField;
	
	@FXML
	public void doneBtn(ActionEvent e) throws IOException {
		equityField.setText("0");
		double equity = Double.parseDouble(equityField.getText());
	
		
		Parent root = FXMLLoader.load(getClass().getResource("/application/AnalyzerScene.fxml"));
		
		
		
		Scene analizerScene = new Scene(root,800,600);
		Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();
		window.setScene(analizerScene);
		window.show();
	}
	
	
}
