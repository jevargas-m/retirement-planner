import java.io.*;
import java.util.*;
import org.apache.commons.math3.distribution.*;

/**
 * Build a simulated retirement projection randomizing returns as per supplied
 * portfolio variability.  Generates a random sampling assuming returns follow
 * a NormalDistribution.  This object is inmutable, a new projecton is generated
 * upon every new instance.
 * @author Team 11
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
	boolean assumeReal;

	/**
	 * Build a new simulated retirement projection using portfolio volatility
	 * @param principal, Initial money at currentAge
	 * @param deposits, Yearly deposits to make to the account at the end of 
	 * every year before retirement age
	 * @param withdrawals, Yearly withdrawals to make to the account at the end of 
	 * every year before retirement age.  This will always be considered in present
	 * day money (i.e. real)  If assumeReal is false, this will be adjusted by inflation
	 * @param age, User age at present day
	 * @param maxAge,  Max age to consider in projections.  Could be considered user
	 * safety margin.
	 * @param retirementAge,  Age at which user will start withdrawing money every year
	 * @param inflation, Constant annualized inflation rate to assume. 
	 * @param portfolio, Investment portfolio.
	 * @param assumeReal, True if calculation is in real terms, i.e. Today's money, 
	 * this would mean every year deposits and withdrawals increase in nominal terms
	 */
	public FutureProjection(double principal, double deposits, double withdrawals, int age, 
			int maxAge, int retirementAge, double inflation, InvestmentPortfolio portfolio,
			boolean assumeReal) {
		this.initialPrincipal = principal;
		this.deposits = deposits;
		this.withdrawals = withdrawals;
		this.currentAge = age;
		this.retirementAge = retirementAge;
		this.maxAge = maxAge;
		this.inflation = inflation;
		this.portfolio = portfolio;
		this.assumeReal = assumeReal;
		
		this.r = new Random();
		this.data = new FutureProjectionData[maxAge - currentAge + 1];
		this.nd = new NormalDistribution(portfolio.getAverageReturns(), portfolio.getStdDevReturns());
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
			
			int year = age - currentAge; // consecutive year, starting at zero
			double cashflow = 0;					
			if (age <= retirementAge) {
				cashflow = deposits;
				// Deposits diminish every year in nominal terms
				if (!assumeReal) cashflow *= Math.pow(1.0 + inflation, (-1 * year));
			} else {
				cashflow = -1 * withdrawals;
				// Withdrawals increase every year in nominal terms
				if (!assumeReal) cashflow *= Math.pow(1.0 + inflation, year);
			}

			data[year] = new FutureProjectionData(age, realRate, inflation, principal, cashflow);
			
			if (assumeReal) {
				principal += principal * realRate + cashflow;
			} else {
				principal += principal * nominalRate + cashflow;
			}
			
			if (!flagBroke && age >= retirementAge && principal < 0) {
				ageBroke = age;
				flagBroke = true;
			}
		}
		if (!flagBroke) ageBroke = maxAge;  // if not broke ageBroke assumes maxAge
	}
	
	/**
	 * Perform MonteCarloSimulation building several random FutureProjections
	 * with parameters used when object was constructed
	 * Each iteration item in the array is a complete randomized projection 
	 * of cashflows until broke (i.e. each iteration is a complete amortization table)
	 * @param iterations, Number of iterations in the MonteCarlo
	 * @return FutureProjection[iterations]
	 */
	public FutureProjection[] monteCarloSimulation(int iterations) {
		FutureProjection[] results = new FutureProjection[iterations];
		for (int i = 0; i < iterations; i++) {
			FutureProjection projection = new FutureProjection(initialPrincipal, deposits, withdrawals, 
					currentAge, maxAge, retirementAge, inflation, portfolio, assumeReal);
			results[i] = projection;
		}
		return results;
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
	 * Return Amortizatization table row at a given age
	 * @param age
	 * @return
	 * @throws IllegalArgumentException
	 */
	public FutureProjectionData getProjectedData(int age) throws IllegalArgumentException {
		if (age < currentAge || age > maxAge) {
			throw new IllegalArgumentException("Age out of bounds");
		}
		return data[age - currentAge];
	}
	
	public int getMaxAge() {
		return maxAge;
	}

	/**
	 * Get a single randomized amortization table for simulated retirement
	 * @return ArrayList<FutureProjectionData>
	 */
	public FutureProjectionData[] getData() {
		return data;
	}

	public int getCurrentAge() {
		return currentAge;
	}

	/**
	 * Age in which there is not enough money to cover retirement expenses, if 
	 * projection never gets broke, returns maxAge
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
		UserInputs ui = UserInputs.getDefaultInputs();
		InvestmentPortfolio ip = new InvestmentPortfolio(0.3);
		FutureProjection fp = new FutureProjection(ui.getPrincipal(), ui.getYearlyDeposits(), ui.getTargetRetirement(),
				ui.getCurrentAge(), ui.getMaxAge(),ui.getTargetRetirementAge(), ui.getInflation(), ip, false);
		
		fp.printAmortizationTable();
		System.out.println("Broke at age = " + fp.getAgeBroke());
	}

}
