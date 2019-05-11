package application;

import java.io.FileNotFoundException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import modelPlanner.*;


/**
 * Controller class for main window in the GUI
 * @author Team 11
 *
 */
public class AnalyzerController implements Initializable {
	
	// Parameters
	private final double DEFAULT_INFLATION = 0.03; 
	private final int DEFAULT_MONTECARLO_ITERATIONS = 100000;
	private final double DEFAULT_SAFETY_MARGIN_RETIREMENT_TODAY = 0.1;
		
	private UserInputs inputs = new UserInputs();
	private EquityPercent equityPercent = new EquityPercent(); 
	
	// Required by the View
	@FXML private LineChart<Number, Number> brokeChart;
	@FXML private LineChart<Number, Number> principalChart;
	@FXML private Slider equitySlider;
	@FXML private Label minPrincipalRetAge;
	@FXML private Label expectedPrincipalRetAge;
	@FXML private Label maxPrincipalRetAge;
	@FXML private Label minPrincipalMaxAge;
	@FXML private Label expectedPrincipalMaxAge;
	@FXML private Label maxPrincipalMaxAge;
	@FXML private Label pBrokeAtMaxAge;
	@FXML private Label safeWithdrawal;
	@FXML private Label labelIterations;
	@FXML private TextField fieldCurrentAge;
	@FXML private TextField fieldRetAge;
	@FXML private TextField fieldMaxAge;
	@FXML private TextField fieldPrincipal;
	@FXML private TextField fieldWithdrawal;
	@FXML private TextField fieldDeposits;
	@FXML private TextField fieldEquity;
	@FXML private Pane analyzerPane;
	@FXML private VBox outputInsights;
	@FXML private Button wizardBtn;
	
	// Cannot have an array for Scene Builder to work properly
	@FXML private ComboBox<String> answerEquity1;
	@FXML private ComboBox<String> answerEquity2;
	@FXML private ComboBox<String> answerEquity3;
	@FXML private ComboBox<String> answerEquity4;

	@Override
	public void initialize(URL location, ResourceBundle resources) {	
		wizardBtn.setDisable(true);
		ObservableList<String> list1 = FXCollections.observableArrayList(equityPercent.getAnswers(1));
		ObservableList<String> list2 = FXCollections.observableArrayList(equityPercent.getAnswers(2));
		ObservableList<String> list3 = FXCollections.observableArrayList(equityPercent.getAnswers(3));
		ObservableList<String> list4 = FXCollections.observableArrayList(equityPercent.getAnswers(4));
		
		// FXML SceneBuilder requires independent items, array not accepted
		answerEquity1.setItems(list1);
		answerEquity2.setItems(list2);
		answerEquity3.setItems(list3);
		answerEquity4.setItems(list4);
	}
	
	/**
	 * Main event for analyze user click, triggers simulation and results output 
	 * @param e Button click
	 */
	@FXML
	public void doAnalyze(ActionEvent e) {
		
		try {
			getInputs();
			
		// Do Monte Carlo Simulation
			InvestmentPortfolio portfolio = new InvestmentPortfolio(inputs.getEquityPercentage());
			// using +10 yrs in MaxAge to avoid graph jump
			RetirementAnalyzer ra = new RetirementAnalyzer(inputs.getPrincipal(), inputs.getYearlyDeposits(), 
					inputs.getTargetRetirement(), inputs.getCurrentAge(), inputs.getMaxAge() + 10, 
					inputs.getTargetRetirementAge(), inputs.getInflation(),	portfolio, inputs.isRealMoney());
			SummaryMonteCarlo smc = ra.getMonteCarloSummary();
			ra.buildMonteCarlo(DEFAULT_MONTECARLO_ITERATIONS);
			labelIterations.setText("Performed " + DEFAULT_MONTECARLO_ITERATIONS + " iterations");
			
		// Principals 
			minPrincipalRetAge.setText(moneyToLabel(ra.getPrincipalInterval(inputs.getTargetRetirementAge()).getMinConfInterval()));
			maxPrincipalRetAge.setText(moneyToLabel(ra.getPrincipalInterval(inputs.getTargetRetirementAge()).getMaxConfInterval()));
			expectedPrincipalRetAge.setText(moneyToLabel(ra.getPrincipalInterval(inputs.getTargetRetirementAge()).getAverage()));
			minPrincipalMaxAge.setText(moneyToLabel(ra.getPrincipalInterval(inputs.getMaxAge()).getMinConfInterval()));
			maxPrincipalMaxAge.setText(moneyToLabel(ra.getPrincipalInterval(inputs.getMaxAge()).getMaxConfInterval()));
			expectedPrincipalMaxAge.setText(moneyToLabel(ra.getPrincipalInterval(inputs.getMaxAge()).getAverage()));
		
		// Others
			double pbroke = ra.getProbBrokeAtAge(inputs.getMaxAge());
			NumberFormat nf = NumberFormat.getPercentInstance();
			nf.setMinimumFractionDigits(1);
			nf.setMaximumFractionDigits(1);
			pBrokeAtMaxAge.setText(nf.format(1.0 - pbroke));
			safeWithdrawal.setText(moneyToLabel(ra.getMaxSafeWithdrawal(inputs.getMaxAge(), DEFAULT_SAFETY_MARGIN_RETIREMENT_TODAY)));
			
		// Charts
			clearGraphs();
			generateBrokeChart(smc);
			generatePrincipalChart(smc);
		} 
		
		catch (NumberFormatException exception) {
			Alert alert = new Alert (AlertType.ERROR);
			alert.setTitle("Input Error");
			alert.setHeaderText("Inputs error");
			alert.setContentText(exception.getMessage());
			alert.showAndWait();
		} 
		
		catch (IllegalArgumentException exception) {
			Alert alert = new Alert (AlertType.ERROR);
			alert.setTitle("Input bounds error");
			alert.setHeaderText("Input bounds error");
			alert.setContentText(exception.getMessage());
			alert.showAndWait();
		} 
		
		catch (FileNotFoundException exception) {
			Alert alert = new Alert (AlertType.ERROR);
			alert.setTitle("Internal File Error");
			alert.setHeaderText("A required file is not present");
			alert.setContentText(exception.getMessage());
			alert.showAndWait();
		}
		
	}
	
	/**
	 * Convert a double number to a string displaying money without cents
	 * @param money
	 * @return
	 */
	private String moneyToLabel(double money) {
		NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
		nf.setMaximumFractionDigits(0);
		if (money >= 0) {
			return nf.format(money);
		} else {
			return "$  < 0";
		}
	}
	
	private void generateBrokeChart(SummaryMonteCarlo smc) {
		XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
		for (int i = 0; i < smc.getNumRows() - 1; i++) {
			series.getData().add(new XYChart.Data<Number, Number>(smc.getAge().get(i), 1 - smc.getProbBroke().get(i)));
		}
		
		// Displayed from retirement to max age
		NumberAxis xAxis = (NumberAxis)brokeChart.getXAxis();
		xAxis.setLowerBound(inputs.getTargetRetirementAge());
		xAxis.setUpperBound(inputs.getMaxAge());
		brokeChart.getData().add(series);
	}
	
	private void generatePrincipalChart(SummaryMonteCarlo smc) {
		XYChart.Series<Number, Number> seriesMax = new XYChart.Series<Number, Number>();
		XYChart.Series<Number, Number> seriesMin = new XYChart.Series<Number, Number>();
		XYChart.Series<Number, Number> seriesAvg = new XYChart.Series<Number, Number>();
		for (int i = 0; i < smc.getNumRows() - 1; i++) {
			seriesMax.getData().add(new XYChart.Data<Number, Number>(smc.getAge().get(i), smc.getMaxPrincipal().get(i)));
			seriesMin.getData().add(new XYChart.Data<Number, Number>(smc.getAge().get(i), smc.getMinPrincipal().get(i)));
			seriesAvg.getData().add(new XYChart.Data<Number, Number>(smc.getAge().get(i), smc.getMeanPrincipal().get(i)));
		}
		
		// Dsplayed from current age to MaxAge
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
		
		inputs = new UserInputs(age, maxAge, DEFAULT_INFLATION, deposits, true, withdrawals, 
				retirementage, equity, principal);;
	}
	
	/**
	 * Load predefined example in UserInputs class 
	 * @param e Event for Btn click
	 */
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
		clearAnswers();
	}
	

	private void clearGraphs() {
		principalChart.getData().clear();
		brokeChart.getData().clear();
	}


	/**
	 * Wizard Btn event, moves equity as per recommendation
	 * @param e Btn click
	 */
	@FXML
	public void updateEquity(ActionEvent e) {
		String[] userAnswers = new String[4];
		userAnswers[0] = answerEquity1.getValue();
		userAnswers[1] = answerEquity2.getValue();
		userAnswers[2] = answerEquity3.getValue();
		userAnswers[3] = answerEquity4.getValue();
		equitySlider.setValue(equityPercent.getEquityPercent(userAnswers));
	}
	
	/**
	 * Enable wizard btn, used when all the answers have been provided
	 * @param e
	 */
	@FXML 
	public void enableWizardBtn(ActionEvent e) {
		if (!answerEquity1.getSelectionModel().isEmpty() && 
				!answerEquity2.getSelectionModel().isEmpty() &&
				  !answerEquity3.getSelectionModel().isEmpty() &&
				     !answerEquity4.getSelectionModel().isEmpty() ) {
			wizardBtn.setDisable(false);
		};
	}
	
	/**
	 * Reset answers and disable wizard button
	 * @param e
	 */
	@FXML
	public void clearAnswers(ActionEvent e) {
		clearAnswers();
	}
	
	private void clearAnswers() {
		wizardBtn.setDisable(true);
		answerEquity1.getSelectionModel().clearSelection();
		answerEquity2.getSelectionModel().clearSelection();
		answerEquity3.getSelectionModel().clearSelection();
		answerEquity4.getSelectionModel().clearSelection();
	}

	
}
