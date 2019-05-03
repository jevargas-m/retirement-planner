package application;

import java.io.FileNotFoundException;
import java.net.URL;
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
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import modelPlanner.*;



public class AnalyzerController implements Initializable {
	
	private UserInputs inputs = new UserInputs();
	private EquityPercent equityPercent = new EquityPercent(); 
	private final double DEFAULT_INFLATION = 0.03;
	private final int DEFAULT_MONTECARLO_ITERATIONS = 50000;
	private final double DEFAULT_SAFETY_MARGIN_RETIREMENT_TODAY = 0.1;
	
	@FXML private LineChart<Number, Number> brokeChart;
	@FXML private LineChart<Number, Number> principalChart;
	@FXML private Slider equitySlider;
	@FXML private Label minPrincipal;
	@FXML private Label maxPrincipal;
	@FXML private Label pBrokeAtMaxAge;
	@FXML private Label safeWithdrawal;
	@FXML private Label labelOutputTitle;
	@FXML private TextField fieldCurrentAge;
	@FXML private TextField fieldRetAge;
	@FXML private TextField fieldMaxAge;
	@FXML private TextField fieldPrincipal;
	@FXML private TextField fieldWithdrawal;
	@FXML private TextField fieldDeposits;
	@FXML private TextField fieldEquity;
	@FXML private Pane analyzerPane;
	@FXML private VBox outputInsights;
	
	@FXML private ComboBox<String> answerEquity1;
	@FXML private ComboBox<String> answerEquity2;
	@FXML private ComboBox<String> answerEquity3;
	@FXML private ComboBox<String> answerEquity4;
	
		
	@FXML
	public void doCalc(ActionEvent e) {
		
		try {
			getInputs();
			
			InvestmentPortfolio portfolio = new InvestmentPortfolio(inputs.getEquityPercentage());
			// using +10 yrs in MaxAge to avoid graph jump
			RetirementAnalyzer ra = new RetirementAnalyzer(inputs.getPrincipal(), inputs.getYearlyDeposits(), 
					inputs.getTargetRetirement(), inputs.getCurrentAge(), inputs.getMaxAge() + 10, 
					inputs.getTargetRetirementAge(), inputs.getInflation(),	portfolio, inputs.isRealMoney());
			SummaryMonteCarlo smc = ra.getMonteCarloSummary();
			ra.buildMonteCarlo(DEFAULT_MONTECARLO_ITERATIONS);
			
			// Outputs to display
			double minp = Math.round(ra.getPrincipalInterval(inputs.getTargetRetirementAge()).getMinConfInterval());
			minPrincipal.setText("$ " + Integer.toString((int)minp));
			
			double maxp = Math.round(ra.getPrincipalInterval(inputs.getTargetRetirementAge()).getMaxConfInterval());
			maxPrincipal.setText("$ " + Integer.toString((int)maxp));
			//ra.getPrincipalInterval(inputs.getMaxAge()).getAverage();
						
			double pb = ra.getProbBrokeAtAge(inputs.getMaxAge());
			pBrokeAtMaxAge.setText(Double.toString(pb));
			
			double sw = Math.round(ra.getMaxSafeWithdrawal(inputs.getMaxAge(), DEFAULT_SAFETY_MARGIN_RETIREMENT_TODAY));  
			safeWithdrawal.setText("$ " + Integer.toString((int)sw));
			
			labelOutputTitle.setText("Key insights after " + DEFAULT_MONTECARLO_ITERATIONS + " simulations");
			outputInsights.setVisible(true);
			
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
	
	private void generateBrokeChart(SummaryMonteCarlo smc) {
		XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
		for (int i = 0; i < smc.getNumRows() - 1; i++) {
			series.getData().add(new XYChart.Data<Number, Number>(smc.getAge().get(i), 1 - smc.getProbBroke().get(i)));
		}
		
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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		outputInsights.setVisible(false);
		ObservableList<String> list1 = FXCollections.observableArrayList("Nonexistent", "I sometimes watch CNBC.",
				"I read the WSJ.", "I'm the Wolf of Wall St.");
		ObservableList<String> list2 = FXCollections.observableArrayList("Save my money! I don't like the volatility.", "Get some income, with low volatility.",
				"Some income, some growth; Some volatility is ok.", "I want to make it rain $$$; I can handle the risk.");
		ObservableList<String> list3 = FXCollections.observableArrayList("Panic and sell!!!",
				"Cancel my vacation, sell a little, and cry.", "Have two shots of Tequila and buy a little.",
				"Bring it on market!!! I'd wave it in!");
		ObservableList<String> list4 = FXCollections.observableArrayList("No way! Keep me safe and snug!",
				"Gulp, maybe a little.", "I can take some risk, not too crazy.", "Volatility is my middle name!!!");
		
		answerEquity1.setItems(list1);
		answerEquity2.setItems(list2);
		answerEquity3.setItems(list3);
		answerEquity4.setItems(list4);
	}
	
	
	public int getRiskScore() {
		int scoreList1 = 0; 
		int scoreList2 = 0; 
		int scoreList3 = 0; 
		int scoreList4 = 0; 
		
		if (answerEquity1.getValue().equals("Nonexistent")) {
			scoreList1 = 1; 
		} else if (answerEquity1.getValue().equals("I sometimes watch CNBC.")) {
			scoreList1 = 2; 
		} else if (answerEquity1.getValue().equals("I read the WSJ.")) {
			scoreList1 = 3; 
		} else {
			scoreList1 = 4;
		}
		
		if (answerEquity2.getValue().equals("Save my money! I don't like the volatility.")) {
			scoreList2 = 1; 
		} else if (answerEquity2.getValue().equals("Get some income, with low volatility.")) {
			scoreList2 = 2; 
		} else if (answerEquity2.getValue().equals("Some income, some growth; Some volatility is ok.")) {
			scoreList2 = 3; 
		} else {
			scoreList2 = 4;
		}
		
		if (answerEquity3.getValue().equals("Panic and sell!!!")) {
			scoreList3 = 1; 
		} else if (answerEquity3.getValue().equals("Cancel my vacation, sell a little, and cry.")) {
			scoreList3 = 2; 
		} else if (answerEquity3.getValue().equals("Have two shots of Tequila and buy a little.")) {
			scoreList3 = 3; 
		} else {
			scoreList3 = 4;
		}
		
		if (answerEquity4.getValue().equals("No way! Keep me safe and snug!")) {
			scoreList4 = 1; 
		} else if (answerEquity4.getValue().equals("Gulp, maybe a little.")) {
			scoreList4 = 2; 
		} else if (answerEquity4.getValue().equals("I can take some risk, not too crazy.")) {
			scoreList4 = 3; 
		} else {
			scoreList4 = 4;
		}
		System.out.println("score:" + (scoreList1 + scoreList2 + scoreList3 + scoreList4));
		return scoreList1 + scoreList2 + scoreList3 + scoreList4; 	
	}
	
	@FXML
	public void updateEquity(ActionEvent e) {
		double equity = equityPercent.getEquityPercent(getRiskScore());
		equitySlider.setValue(equity);
	}
	
	
//	@FXML
//	public void showWizard(ActionEvent e) throws IOException {
//		Parent wizardParent = FXMLLoader.load(getClass().getResource("/application/Wizard.fxml"));
//		Scene wizardScene = new Scene(wizardParent, 800, 600);
//		Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();
//		window.setScene(wizardScene);
//		window.show();
//	}
	
}
