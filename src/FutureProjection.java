import java.io.*;
import java.util.*;
import org.apache.commons.math3.distribution.*;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.*;

/**
 * Build a simulated retirement projection randomizing returns as per supplied
 * portfolio variability.  Generates a random sampling assuming returns follow
 * a NormalDistribution.  Object is inmutable.  A new projecton is generated upon every clone or new instance.
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
	
	private final int NUM_INT_ITERATIONS = 2000;  // Max internal iterations in MonteCarlo and Solver
	private final int UPPERBOUND_WITHDRAWAL = 20; // Number of times non-volatile result considered upper bound for solver

	/**
	 * Build a new simulated retirement projection using portfolio volatility
	 * @param principal Initial money at currentAge
	 * @param deposits Yearly deposits to make to the account at the end of 
	 * every year before retirement age
	 * @param withdrawals Yearly withdrawals to make to the account at the end of 
	 * every year before retirement age.  This will always be considered in present
	 * day money (i.e. real)  If assumeReal is false, this will be adjusted by inflation
	 * @param age User age at present day
	 * @param maxAge Max age to consider in projections.  Could be considered user
	 * safety margin.
	 * @param retirementAge Age at which user will start withdrawing money every year
	 * @param inflation Constant annualized inflation rate to assume. 
	 * @param portfolio Investment portfolio.
	 * @param assumeReal True if calculation is in real terms, i.e. Today's money, 
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
	 * Build a new randomized amortization table
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
			if (age < retirementAge) {
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
	 * Simple annuity calculation, how much money to retire every year in real terms to 
	 * get broke at a given age in real terms
	 * @param ageBroke 
	 * @return
	 */
	private double getNoVolatileMaxWithdrawal(int ageBroke) {
		double interest = realRate(portfolio.getAverageReturns());
		double comp1 = Math.pow((1 + interest), (ageBroke - currentAge));  // (1+i)^n
		return initialPrincipal * (interest * comp1) / (comp1 - 1);
	}
	
	/**
	 * Probability of being broke a certain age, calculated with MonteCarlo simulation
	 * @param age At which user is brokeat which user is broke
	 * @return Cummulative Probability [0-1] of being broke at the supplied age
	 * @throws IllegalArgumentException If supplied age is less than currentAge
	 */
	public double getProbBrokeAtAge(int age) throws IllegalArgumentException {
		if (age < currentAge) throw new IllegalArgumentException("Age out of bounds");
		
		// Only need age + 1 values for SimulationAnalizer to build Frequency distribution
		FutureProjection fp = new FutureProjection(initialPrincipal, deposits, withdrawals, currentAge, 
				age + 1, retirementAge, inflation, portfolio, assumeReal);

		SimulationAnalyzer sa = new SimulationAnalyzer(fp.monteCarloSimulation(NUM_INT_ITERATIONS));
		return sa.getProbBrokeAtAge(age);
	}
	
	/**
	 * Probability of being broke a certain age given a withdrawal amount constant in Today's
	 * money (real) and assuming retirement starts at present age, calculated with MonteCarlo simulation
	 * @param withdrawal Yearly withdrawals in real money
	 * @param age Age at which to estimte probability
	 * @return Cummulative Probability [0-1] of being broke at the supplied age
	 * @throws IllegalArgumentException If supplied age is less than currentAge
	 */
	public double getProbBrokeAtAge(double withdrawal, int age) throws IllegalArgumentException {
		if (age < currentAge) throw new IllegalArgumentException("Age out of bounds");
		// change withdrawal and retirementAge = currentAge
		FutureProjection fp = new FutureProjection(initialPrincipal, deposits, withdrawal, currentAge, 
				age, currentAge, inflation, portfolio, assumeReal);

		return fp.getProbBrokeAtAge(age); 
	}
		
	/**
	 * Max withdrawal if retiring today achieving cumm probability of being broke
	 * uses Illinois Secant method iterating over Montecarlo Simulations.
	 * <br>
	 *  <a href="http://commons.apache.org/proper/commons-math/apidocs/org/apache/commons/math4/analysis/solvers/IllinoisSolver.html">IllinoisSolver Reference</a><br>
	 * @param age Age to which to calculate probability
	 * @param probability Cummulative probability of being broke at given age
	 * @return Maximum yearly withdrawal.  If not able to find solution returns -1
	 */
	public double getMaxSafeWithdrawal(int age, double probability) {
		// http://commons.apache.org/proper/commons-math/apidocs/org/apache/commons/math4/analysis/UnivariateFunction.html
		class WithdrawalFunction implements UnivariateFunction {

			@Override
			public double value(double withdrawal) {
				return getProbBrokeAtAge(withdrawal, age) - probability; // function to search for root
			} 
		}

		double estimate = getNoVolatileMaxWithdrawal(age);  // Simple annuity is start value for solver
		WithdrawalFunction f = new WithdrawalFunction();
		IllinoisSolver solver = new IllinoisSolver();

		double result = 0;
		try {
			result = solver.solve(NUM_INT_ITERATIONS, f, 0, UPPERBOUND_WITHDRAWAL * estimate, estimate);
		} catch (Exception e) {
			result = -1;
		}
		
		return result;
	}
	
	/**
	 * Deep copy of object, parameters used on instance creation are the same
	 * but a new projection is randomly generated
	 */
	public FutureProjection clone() {
		return new FutureProjection(initialPrincipal, deposits, withdrawals, 
				currentAge, maxAge, retirementAge, inflation, portfolio, assumeReal);
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
			FutureProjection projection = this.clone();
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
	 * Return Row in Amortizatization built randomly upon instanciation
	 * @param age
	 * @return Row in the amortization table
	 * @throws IllegalArgumentException If age is less than currentAge or greater than supplied maxAge
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
	 * @return Current scenario amortization table
	 */
	public FutureProjectionData[] getData() {
		return data;
	}

	public int getCurrentAge() {
		return currentAge;
	}

	/**
	 * Age in which there is not enough money to cover retirement expenses, at scenario
	 * built on instanciation
	 * if projection never gets broke, returns maxAge
	 * @return int Age at which user is broke 
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
		FutureProjection fp = new FutureProjection(100000, ui.getYearlyDeposits(), ui.getTargetRetirement(),
				ui.getCurrentAge(), ui.getMaxAge(),ui.getTargetRetirementAge(), ui.getInflation(), ip, true);
		
//		fp.printAmortizationTable();
//		System.out.println("Broke at age = " + fp.getAgeBroke());
//		
//		System.out.println("Prob broke at 90 is " + fp.getProbBrokeAtAge(90, 10000));
//		System.out.println("Prob broke at 120 is " + fp.getProbBrokeAtAge(120, 10000));
//		
//		
//		System.out.println("Prob broke at 100 @ 25000 /yr is " + fp.getProbBrokeAtAge(12000,80, 10000));
		
		double safe = fp.getMaxSafeWithdrawal(95, 0.1);
		
		FutureProjection fp2 = new FutureProjection(100000, ui.getYearlyDeposits(), safe,
				ui.getCurrentAge(), ui.getMaxAge(),ui.getCurrentAge(), ui.getInflation(), ip, true);
		
		
		
		System.out.println("Safe withdrawal is " + safe );
		System.out.println("Prob broke at 95 is " + fp2.getProbBrokeAtAge(95));

		fp2.printAmortizationTable();
		
	}

}
