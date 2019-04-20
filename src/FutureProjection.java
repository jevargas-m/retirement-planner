import java.io.*;
import java.util.*;
import org.apache.commons.math3.distribution.*;

/**
 * Build a simulated retirement projection using portfolio variability using 
 * NormalDistribution for return estimation, all cashflows are assumed real
 * (real = Today's money)
 * @author Enrique Vargas
 *
 */
public class FutureProjection {
	private int ageBroke;
	FutureProjectionData[] data;
	private NormalDistribution nd;
	private Random r;
	private double initialPrincipal;
	private double deposits;
	private double withdrawals;
	private int currentAge;
	private int retirementAge;
	private int maxAge;
	private double inflation;
	private InvestmentPortfolio portfolio;
	
	/**
	 * Build a new simulated retirement projection using portfolio volatility
	 * @param principal 
	 * @param yrdeposits
	 * @param withdrawals
	 * @param age
	 * @param retirementAge
	 * @param inflation
	 * @param portfolio
	 */
	public FutureProjection(double principal, double deposits, double withdrawals, int age, 
			int maxAge, int retirementAge, double inflation, InvestmentPortfolio portfolio) {
		this.initialPrincipal = principal;
		this.deposits = deposits;
		this.withdrawals = withdrawals;
		this.currentAge = age;
		this.retirementAge = retirementAge;
		this.maxAge = maxAge;
		this.inflation = inflation;
		this.portfolio = portfolio;
		
		this.r =new Random();
		this.data = new FutureProjectionData[maxAge - currentAge + 1];
		this.nd = new NormalDistribution(this.portfolio.getAverageReturns(), this.portfolio.getStdDevReturns());
		buildProjectionData();
	}
	
	/**
	 * Build amortization table, used on constructor
	 */
	private void buildProjectionData() {
		double principal = initialPrincipal;  // Money in your account
		boolean flagBroke = false;
		
		for ( int age = currentAge; age <= maxAge; age++) {
			//Get a randomly generated interest rate from probability distribution
			double nominalRate = nd.inverseCumulativeProbability(r.nextDouble());
			double realRate = realRate(nominalRate);
			
			double cashflow = 0;					
			if (age <= retirementAge) {
				cashflow = deposits;
			} else {
				cashflow = -1 * withdrawals;
			}

			data[age - currentAge] = new FutureProjectionData(age, realRate, inflation, principal, cashflow);
			principal += principal * realRate + cashflow;
			
			if (!flagBroke && age >= retirementAge && principal < 0) {
				ageBroke = age;
				flagBroke = true;
			}
		}
		if (!flagBroke) ageBroke = maxAge;
	}
	
	/**
	 * Perform MonteCarloSimulation building several random FutureProjections
	 * with parameters used when object is instanciated
	 * Each iteration item in the array is a complete randomized projection 
	 * of cashflows until broke (i.e. each iteration is a complete amortization table)
	 * @param iterations int
	 * @return FutureProjection[iterations]
	 */
	public FutureProjection[] monteCarloSimulation(int iterations) {
		FutureProjection[] results = new FutureProjection[iterations];
		for (int i = 0; i < iterations; i++) {
			FutureProjection projection = new FutureProjection(initialPrincipal, deposits, withdrawals, 
					currentAge, maxAge, retirementAge, inflation, portfolio);
			results[i] = projection;
		}
		return results;
	}
	
	/**
	 * Your age Today
	 * @return int
	 */
	public int getCurrentAge() {
		return currentAge;
	}

	/**
	 * Calculate real interest rate based on nominalRate and provided inflation
	 * based on Fisher Equation
	 * https://en.wikipedia.org/wiki/Real_interest_rate
	 * @param nominalRate
	 * @return double
	 */
	private double realRate(double nominalRate) {
		return (nominalRate - inflation) / (1 + inflation);
	}
	
	/**
	 * Return Projected
	 * @param age
	 * @return
	 * @throws IllegalArgumentException
	 */
	public FutureProjectionData getProjectedData(int age) throws IllegalArgumentException {
		int i = age - currentAge;
		
		if (age < currentAge || age > maxAge) {
			throw new IllegalArgumentException("Age out of bounds");
		}
		
		if (getAgeBroke() < maxAge ) {
			// You are broke
			return null;
		}
		
		return data[i];
	}
	
	/**
	 * Get a single randomized amortization table for simulated retirement
	 * @return ArrayList<FutureProjectionData>
	 */
	public FutureProjectionData[] getData() {
		return data;
	}

	/**
	 * Age in which there is not enough money to cover retirement expenses
	 * @return int
	 */
	public int getAgeBroke() {
		return ageBroke;
	}
	
	/**
	 * Used for debugging, print amortization table in console and data.csv file
	 */
	public void printAmortizationTable () {

		try {
			FileWriter fw = new FileWriter("data.csv");
			PrintWriter pw = new PrintWriter(fw);
			System.out.println("Age     P          r         f         CashFlow");
			pw.println("Age,P,r,f,CashFlow");
			for (int i = 0; i < data.length; i++) {
				FutureProjectionData fpd = data[i];
				System.out.printf("%3d  %8.0f   %8.4f   %8.4f   %8.0f", fpd.getAge(), fpd.getPrincipal(), 
						fpd.getRealRate(), fpd.getInflation(), fpd.getPmt());
				System.out.println();
				
				pw.println(fpd.getAge() + "," + fpd.getPrincipal() + "," +  
						fpd.getRealRate() + "," + fpd.getInflation() + "," + fpd.getPmt());
			}
			pw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("File error: Make sure data.csv is closed");
			e.printStackTrace();
		}
	}
	
	
	// For testing only
	public static void main(String[] args) {
		UserInputs ui = new UserInputs();
		InvestmentPortfolio ip = new InvestmentPortfolio(30);
		FutureProjection fp = new FutureProjection(ui.getPrincipal(), ui.getYearlyDeposits(), ui.getTargetRetirement(),
				ui.getCurrentAge(), ui.getMaxAge(),ui.getTargetRetirementAge(), ui.getInflation(), ip);
		
		fp.printAmortizationTable();
		System.out.println("Broke at age = " + fp.getAgeBroke());
	}

}
