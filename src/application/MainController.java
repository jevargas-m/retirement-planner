package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import modelPlanner.InvestmentPortfolio;
import modelPlanner.RetirementAnalyzer;
import modelPlanner.SummaryMonteCarlo;

public class MainController {
	
	private double equity = 0.3;
	private int maxAge = 100;
	private int age = 30;
	private int retirementAge = 65;
	private double deposits = 12000;
	private double withdrawals = 24000;
	private double principal = 100000;
	
	private final double inflation = 0.03;
	
	@FXML 
	private LineChart<Number, Number> brokeChart;
	
	@FXML 
	private LineChart<Number, Number> principalChart;
	
	@FXML 
	private Label minPrincipal;
	
	@FXML
	private Label maxPrincipal;
	
	@FXML
	private Label pBrokeAtMaxAge;
	
	@FXML
	private Label safeWithdrawal;
	
	@FXML
	private TextField fieldCurrentAge;
	
	@FXML
	private TextField fieldRetAge;
	
	@FXML
	private TextField fieldMaxAge;
	
	@FXML
	private TextField fieldPrincipal;
	
	@FXML
	private TextField fieldWithdrawal;
	
	@FXML
	private TextField fieldDeposits;
	
	@FXML
	private TextField fieldEquity;
		
	@FXML
	public void doCalc(ActionEvent e) {
		getInputs();
		
		InvestmentPortfolio portfolio = new InvestmentPortfolio(equity);
		RetirementAnalyzer ra = new RetirementAnalyzer(principal, deposits, withdrawals, age, maxAge + 10, retirementAge, inflation, portfolio, true);
		SummaryMonteCarlo smc = ra.getMonteCarloSummary();
		
		ra.buildMonteCarlo();
		
		double minp = Math.round(ra.getPrincipalInterval(retirementAge).getMinConfInterval());
		minPrincipal.setText(Double.toString(minp));
		
		double maxp = Math.round(ra.getPrincipalInterval(retirementAge).getMaxConfInterval());
		
		ra.getPrincipalInterval(maxAge).getAverage();
		
		maxPrincipal.setText(Double.toString(maxp));
		
		double pb = ra.getProbBrokeAtAge(maxAge);
		pBrokeAtMaxAge.setText(Double.toString(pb));
		
		double sw = Math.round(ra.getMaxSafeWithdrawal(maxAge, 0.1));
		safeWithdrawal.setText(Double.toString(sw));
		
		generateBrokeChart(smc);
		generatePrincipalChart(smc);
	}
	
	public void generateBrokeChart(SummaryMonteCarlo smc) {
		XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
		for (int i = 0; i < smc.getNumRows() - 1; i++) {
			series.getData().add(new XYChart.Data<Number, Number>(smc.getAge().get(i), 1 - smc.getProbBroke().get(i)));
		}
		
		NumberAxis xAxis = (NumberAxis)brokeChart.getXAxis();
		xAxis.setLowerBound(retirementAge);
		xAxis.setUpperBound(maxAge);
		brokeChart.getData().add(series);
	}
	
	public void generatePrincipalChart(SummaryMonteCarlo smc) {
		XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
		for (int i = 0; i < smc.getNumRows() - 1; i++) {
			series.getData().add(new XYChart.Data<Number, Number>(smc.getAge().get(i), smc.getMeanPrincipal().get(i)));
		}
		
		NumberAxis xAxis = (NumberAxis)principalChart.getXAxis();
		xAxis.setLowerBound(age);
		xAxis.setUpperBound(maxAge);
		principalChart.getData().add(series);
	}
	
	
	private void getInputs() {
		age = Integer.parseInt(fieldCurrentAge.getText());
		deposits = Double.parseDouble(fieldDeposits.getText());
		withdrawals = Double.parseDouble(fieldWithdrawal.getText());
		principal = Double.parseDouble(fieldPrincipal.getText());
		retirementAge = Integer.parseInt(fieldRetAge.getText());
		maxAge = Integer.parseInt(fieldMaxAge.getText());
		equity = Double.parseDouble(fieldEquity.getText());
	}
	
	@FXML
	public void loadDefaults(ActionEvent e) {
		fieldCurrentAge.setText(Integer.toString(age));
		fieldDeposits.setText(Double.toString(deposits));
		fieldWithdrawal.setText(Double.toString(withdrawals));
		fieldPrincipal.setText(Double.toString(principal));
		fieldRetAge.setText(Integer.toString(retirementAge));
		fieldMaxAge.setText(Integer.toString(maxAge));
		fieldEquity.setText(Double.toString(equity));
	}
}
