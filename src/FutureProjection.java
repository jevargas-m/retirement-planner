import java.util.ArrayList;
import java.util.Random;
import org.apache.commons.math3.distribution.*;

public class FutureProjection {
	private InvestmentPortfolio portfolio;
	private int depositYears;
	private int retirementYears;
	private double[] yearlyDeposits;
	private ArrayList<Double> principal;
	private NormalDistribution nd;
	private Random r;
	private UserInputs ui;
		
	public FutureProjection(UserInputs ui) {
		this.ui = ui;
		portfolio = new InvestmentPortfolio(ui.getEquityPercentage());
		this.nd = new NormalDistribution(portfolio.getAverageReturns(), portfolio.getStdDevReturns());
		this.r =new Random();
		
		this.depositYears = this.ui.getTargetRetirementAge() - this.ui.getCurrentAge();
		
		yearlyDeposits = new double[depositYears];
		principal = new ArrayList<>();
				
		calcYearlyDeposits();
		calcPrincipal();
	}
	
	private void calcYearlyDeposits() {
		for (int i = 0; i < depositYears; i++) {
			yearlyDeposits[i] = ui.getMonthlyDeposits() * 12;
		}
	}
	
	private double realRate(double nominalRate) {
		return (nominalRate - ui.getInflation()) / (1 + ui.getInflation());
	}
	
	
	private void calcPrincipal() {
		// Saving period
		principal.add(ui.getPrincipal());
		for (int i = 1; i < depositYears; i++) {
			double nominalInterestRate = nd.inverseCumulativeProbability(r.nextDouble());
			double interestEarned = principal.get(i - 1) * realRate(nominalInterestRate);
			principal.add(principal.get(i - 1) + interestEarned + yearlyDeposits[i]);			
		}
		
		// Retirement period
		int retirementYear = depositYears;
		while (principal.get(retirementYear - 1) > 0) {
			double nominalInterestRate = nd.inverseCumulativeProbability(r.nextDouble());
			double realRate = (nominalInterestRate - ui.getInflation()) / (1 + ui.getInflation());
			principal.add(principal.get(retirementYear - 1) * (1 + realRate) - ui.getTargetRetirement());
			retirementYear++;
		}
		
		retirementYears = retirementYear - ui.getTargetRetirementAge();
	}
	
	public double getProjectedPrincipal(int yearAfterStart) {
		return principal.get(yearAfterStart);
	}
	
	public int getTotalYears() {
		return principal.size();
	}
	
	// For testing only
	public static void main(String[] args) {
		UserInputs ui = new UserInputs();
		FutureProjection fp = new FutureProjection(ui);
		System.out.println("total years = " + fp.getTotalYears());
	
		for (int i = 0; i < fp.getTotalYears(); i++) {
			System.out.println(i + "  $" + Math.round(fp.getProjectedPrincipal(i)));
		}
	}
}
