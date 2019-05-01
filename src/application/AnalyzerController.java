package application;

import java.io.FileNotFoundException;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import modelPlanner.*;



public class AnalyzerController {
	
	UserInputs inputs;
	private final double DEFAULT_INFLATION = 0.03;
	private final int DEFAULT_MONTECARLO_ITERATIONS = 10000;
	
	@FXML private LineChart<Number, Number> brokeChart;
	@FXML private LineChart<Number, Number> principalChart;
	@FXML private Slider equitySlider;
	@FXML private Label minPrincipal;
	@FXML private Label maxPrincipal;
	@FXML private Label pBrokeAtMaxAge;
	@FXML private Label safeWithdrawal;
	@FXML private TextField fieldCurrentAge;
	@FXML private TextField fieldRetAge;
	@FXML private TextField fieldMaxAge;
	@FXML private TextField fieldPrincipal;
	@FXML private TextField fieldWithdrawal;
	@FXML private TextField fieldDeposits;
	@FXML private TextField fieldEquity;
	@FXML private Pane analyzerPane;
		
	@FXML
	public void doCalc(ActionEvent e) {
		clearGraphs();
		try {
			getInputs();
			InvestmentPortfolio portfolio = new InvestmentPortfolio(inputs.getEquityPercentage());
			// using +10 yrs in MaxAge to avoid graph jump
			RetirementAnalyzer ra = new RetirementAnalyzer(inputs.getPrincipal(), inputs.getYearlyDeposits(), 
					inputs.getTargetRetirement(), inputs.getCurrentAge(), inputs.getMaxAge() + 10, 
					inputs.getTargetRetirementAge(), inputs.getInflation(),	portfolio, inputs.isRealMoney());
			SummaryMonteCarlo smc = ra.getMonteCarloSummary();
			ra.buildMonteCarlo(DEFAULT_MONTECARLO_ITERATIONS);
			
			double minp = Math.round(ra.getPrincipalInterval(inputs.getTargetRetirementAge()).getMinConfInterval());
			minPrincipal.setText("$ " + Integer.toString((int)minp));
			
			double maxp = Math.round(ra.getPrincipalInterval(inputs.getTargetRetirementAge()).getMaxConfInterval());
			
			ra.getPrincipalInterval(inputs.getMaxAge()).getAverage();
			
			maxPrincipal.setText("$ " + Integer.toString((int)maxp));
			
			double pb = ra.getProbBrokeAtAge(inputs.getMaxAge());
			pBrokeAtMaxAge.setText(Double.toString(pb));
			
			double sw = Math.round(ra.getMaxSafeWithdrawal(inputs.getMaxAge(), 0.1));  
			safeWithdrawal.setText("$ " + Integer.toString((int)sw));
			
			generateBrokeChart(smc);
			generatePrincipalChart(smc);
		} catch (NumberFormatException exception) {
			Alert alert = new Alert (AlertType.ERROR);
			alert.setTitle("Input Error");
			alert.setHeaderText("Inputs error");
			alert.setContentText(exception.getMessage());
			alert.showAndWait();
		} catch (IllegalArgumentException exception) {
			Alert alert = new Alert (AlertType.ERROR);
			alert.setTitle("Input bounds error");
			alert.setHeaderText("Value error");
			alert.setContentText(exception.getMessage());
			alert.showAndWait();
		} catch (FileNotFoundException exception) {
			Alert alert = new Alert (AlertType.ERROR);
			alert.setTitle("Internal File Error");
			alert.setHeaderText("A required file is not present");
			alert.setContentText(exception.getMessage());
			alert.showAndWait();
		}
		
	}
	
	public void generateBrokeChart(SummaryMonteCarlo smc) {
		XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
		for (int i = 0; i < smc.getNumRows() - 1; i++) {
			series.getData().add(new XYChart.Data<Number, Number>(smc.getAge().get(i), 1 - smc.getProbBroke().get(i)));
		}
		
		NumberAxis xAxis = (NumberAxis)brokeChart.getXAxis();
		xAxis.setLowerBound(inputs.getTargetRetirementAge());
		xAxis.setUpperBound(inputs.getMaxAge());
		brokeChart.getData().add(series);
	}
	
	public void generatePrincipalChart(SummaryMonteCarlo smc) {
		XYChart.Series<Number, Number> seriesMax = new XYChart.Series<Number, Number>();
		XYChart.Series<Number, Number> seriesMin = new XYChart.Series<Number, Number>();
		XYChart.Series<Number, Number> seriesAvg = new XYChart.Series<Number, Number>();
		for (int i = 0; i < smc.getNumRows() - 1; i++) {
			seriesMax.getData().add(new XYChart.Data<Number, Number>(smc.getAge().get(i), smc.getMaxPrincipal().get(i)));
			seriesMin.getData().add(new XYChart.Data<Number, Number>(smc.getAge().get(i), smc.getMinPrincipal().get(i)));
			seriesAvg.getData().add(new XYChart.Data<Number, Number>(smc.getAge().get(i), smc.getMeanPrincipal().get(i)));
		}
		
		NumberAxis xAxis = (NumberAxis)principalChart.getXAxis();
		xAxis.setLowerBound(inputs.getCurrentAge());
		xAxis.setUpperBound(inputs.getMaxAge());
				
		principalChart.getData().add(seriesAvg);
		principalChart.getData().add(seriesMax);
		principalChart.getData().add(seriesMin);
	}
	
	
	private void getInputs() throws NumberFormatException {
		int age = Integer.parseInt(fieldCurrentAge.getText());
		double deposits = Double.parseDouble(fieldDeposits.getText());
		double withdrawals = Double.parseDouble(fieldWithdrawal.getText());
		double principal = Double.parseDouble(fieldPrincipal.getText());
		int retirementage = Integer.parseInt(fieldRetAge.getText());
		int maxAge = Integer.parseInt(fieldMaxAge.getText());
		double equity = equitySlider.getValue();
		
		inputs = new UserInputs(age, maxAge, DEFAULT_INFLATION, deposits, true, withdrawals, retirementage, equity, principal);;
	}
	
	@FXML
	public void loadDefaults(ActionEvent e) {
		UserInputs ui = UserInputs.getDefaultInputs();
		fieldCurrentAge.setText(Integer.toString(ui.getCurrentAge()));
		fieldDeposits.setText(Double.toString(ui.getYearlyDeposits()));
		fieldWithdrawal.setText(Double.toString(ui.getTargetRetirement()));
		fieldPrincipal.setText(Double.toString(ui.getPrincipal()));
		fieldRetAge.setText(Integer.toString(ui.getTargetRetirementAge()));
		fieldMaxAge.setText(Integer.toString(ui.getMaxAge()));
		equitySlider.setValue(ui.getEquityPercentage());
	}
	

	private void clearGraphs() {
		principalChart.getData().clear();
		brokeChart.getData().clear();
	}
	
	@FXML
	public void showWizard(ActionEvent e) throws IOException {
		Parent wizardParent = FXMLLoader.load(getClass().getResource("/application/Wizard.fxml"));
		Scene wizardScene = new Scene(wizardParent, 800, 600);
		Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();
		window.setScene(wizardScene);
		window.show();
	}
	
}
