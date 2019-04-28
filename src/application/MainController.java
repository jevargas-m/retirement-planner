package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import modelPlanner.InvestmentPortfolio;
import modelPlanner.RetirementAnalyzer;

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
		ra.buildMonteCarlo();
		
		double minp = Math.round(ra.getPrincipalInterval(retirementAge).getMinConfInterval());
		minPrincipal.setText(Double.toString(minp));
		
		double maxp = Math.round(ra.getPrincipalInterval(retirementAge).getMaxConfInterval());
		maxPrincipal.setText(Double.toString(maxp));
		
		double pb = ra.getProbBrokeAtAge(maxAge);
		pBrokeAtMaxAge.setText(Double.toString(pb));
		
		double sw = Math.round(ra.getMaxSafeWithdrawal(maxAge, 0.1));
		safeWithdrawal.setText(Double.toString(sw));
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
