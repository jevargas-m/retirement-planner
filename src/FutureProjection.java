import java.util.*;
import org.apache.commons.math3.distribution.*;

public class FutureProjection {
	private InvestmentPortfolio portfolio;
	private int ageBroke;
	
	ArrayList<FutureProjectionData> data;

	private NormalDistribution nd;
	private Random r;
	private UserInputs ui;
		
	public FutureProjection(UserInputs ui) {
		this.ui = ui;
		this.r =new Random();

		data = new ArrayList<>();
		portfolio = new InvestmentPortfolio(ui.getEquityPercentage());
		this.nd = new NormalDistribution(portfolio.getAverageReturns(), portfolio.getStdDevReturns());
	
		buildProjectionData();
	}
	
	private void buildProjectionData() {
		double principal = ui.getPrincipal();
		boolean savingPeriod = true;
		
		// Saving period
		int age = ui.getCurrentAge();
		while ( savingPeriod || principal >= ui.getTargetRetirement() ) {
			double nominalRate = nd.inverseCumulativeProbability(r.nextDouble());
			double realRate = realRate(nominalRate);
			double inflation = ui.getInflation();
			double cashflow = 0;
		
			if (age <= ui.getTargetRetirementAge()) {
				cashflow = ui.getYearlyDeposits();
			} else {
				savingPeriod = false;
				cashflow = -1 * ui.getTargetRetirement();
			}
			
			FutureProjectionData fpd = new FutureProjectionData(age, realRate, inflation, principal, cashflow);
			data.add(fpd);
			
			principal += principal * realRate + cashflow;
			age++;
		}
		
		ageBroke = age;
	}
	
	private double realRate(double nominalRate) {
		return (nominalRate - ui.getInflation()) / (1 + ui.getInflation());
	}
	
	public FutureProjectionData getProjectedData(int age) throws IllegalArgumentException {
		int i = age - ui.getCurrentAge();
		
		if (i < ui.getCurrentAge()) {
			throw new IllegalArgumentException("Age before present day");
		}
		
		if (i < data.size() ) {
			// You are broke
			return null;
		}
		
		return data.get(i);
	}
	
	public int getAgeBroke() {
		return ageBroke;
	}
	
	public void printAmortizationTable () {
		System.out.println("Age     P          r         f         CashFlow");
		
		for (int i = 0; i < data.size(); i++) {
			FutureProjectionData fpd = data.get(i);
			System.out.printf("%3d  %8.0f   %8.4f   %8.4f   %8.0f", fpd.getAge(), fpd.getPrincipal(), 
					fpd.getRealRate(), fpd.getInflation(), fpd.getPmt());
			System.out.println();
		}
	}
	
	
	// For testing only
	public static void main(String[] args) {
		UserInputs ui = new UserInputs();
		FutureProjection fp = new FutureProjection(ui);
		fp.printAmortizationTable();
	}

}
