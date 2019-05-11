package modelPlanner;
import java.io.*;

import org.apache.commons.math3.stat.Frequency;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.*;

/**
 * Build a simulated retirement projection randomizing returns as per supplied
 * portfolio variability.  Generates a random sampling assuming returns from 
 * class implementing SimulableRate interface.  Object is immutable.  
 * A new amortization table with a randomized projection is generated upon every 
 * clone or new instance.
 * @author Team 11
 *
 */
public class RetirementAnalyzer {
	private int ageBroke;
	private AmotizationTableRow[] data;
	private double initialPrincipal;
	private double deposits;
	private double withdrawals;
	private int currentAge;
	private int retirementAge;
	private int maxAge;
	private double inflation;
	private SimulableRate portfolio;
	private boolean assumeReal;
	private int ageCardinality;
	private ConfidenceInterval[] principalsIntervals;
	
	private Frequency ageBrokeDistribution;
	private boolean isMonteCarloBuilt;
	
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
	public RetirementAnalyzer(double principal, double deposits, double withdrawals, int age, 
			int maxAge, int retirementAge, double inflation, SimulableRate portfolio,
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
		this.isMonteCarloBuilt = false;
		
		this.ageCardinality = maxAge - currentAge + 1;
		this.data = new AmotizationTableRow[ageCardinality];
	
		buildProjectionData();
	}
	
	/**
	 * Build a new randomized amortization table used on new instance creation only
	 */
	private void buildProjectionData() {
		double principal = initialPrincipal;  // Money in your account
		boolean flagBroke = false;
		
		for ( int age = currentAge; age <= maxAge; age++) {
			//Get a randomly generated interest rate from probability distribution
			double nominalRate = portfolio.nextRate(); 
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

			data[year] = new AmotizationTableRow(age, realRate, inflation, principal, cashflow);
			
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
	 * Deep copy of object, parameters used on instance creation are the same
	 * but a new projection is randomly generated
	 */
	public RetirementAnalyzer clone() {
		return new RetirementAnalyzer(initialPrincipal, deposits, withdrawals, 
				currentAge, maxAge, retirementAge, inflation, portfolio, assumeReal);
	}
	
	/**
	 * Probability of being broke a certain age, calculated with previously built MonteCarlo simulation
	 * if Monte Carlo has not been built, one is built using default iterations
	 * @param age At which user is broke at which user is broke
	 * @return Cumulative Probability [0-1] of being broke at the supplied age
	 * @throws IllegalArgumentException If supplied age is less than currentAge or greater than maxAge
	 */
	public double getProbBrokeAtAge(int age) throws IllegalArgumentException {		
		validateAgeBounds(age, true);
		if (!isMonteCarloBuilt) buildMonteCarlo();
		return ageBrokeDistribution.getCumPct(age);
	}
	
	/**
	 * Probability of being broke a certain age given a withdrawal amount constant in Today's
	 * money (real) and assuming retirement starts at present age, calculated with MonteCarlo simulation
	 * @param withdrawal Yearly withdrawals in real money
	 * @param age Age at which to estimate probability
	 * @return Cumulative Probability [0-1] of being broke at the supplied age
	 * @throws IllegalArgumentException If supplied age is less than currentAge
	 */
	public double getProbBrokeAtAge(double withdrawal, int age) throws IllegalArgumentException {
		validateAgeBounds(age, false);
		// change withdrawal and retirementAge = currentAge.  MaxAge only needs to be age + 1 for build Frequency
		RetirementAnalyzer fp = new RetirementAnalyzer(initialPrincipal, deposits, withdrawal, currentAge, 
				age + 1, currentAge, inflation, portfolio, assumeReal);

		return fp.getProbBrokeAtAge(age); 
	}
		
	/**
	 * Max withdrawal if retiring today achieving cumm probability of being broke
	 * uses Illinois Secant method iterating over Montecarlo Simulations.
	 * <br>
	 *  <a href="http://commons.apache.org/proper/commons-math/apidocs/org/apache/commons/math4/analysis/solvers/IllinoisSolver.html">IllinoisSolver Reference</a><br>
	 * @param age Age to which to calculate probability
	 * @param probability Cumulative probability of being broke at given age
	 * @return Maximum yearly withdrawal.  If not able to find solution returns -1
	 */
	public double getMaxSafeWithdrawal(int age, double probability) {
		// http://commons.apache.org/proper/commons-math/apidocs/org/apache/commons/math4/analysis/UnivariateFunction.html
		validateAgeBounds(age, false);
		class WithdrawalFunction implements UnivariateFunction {

			@Override
			public double value(double withdrawal) {
				return getProbBrokeAtAge(withdrawal, age) - probability; // function to search for root
			} 
		}
		WithdrawalFunction f = new WithdrawalFunction();
		IllinoisSolver solver = new IllinoisSolver();

		double estimate = getNoVolatileMaxWithdrawal(age);  // Simple annuity is start value for solver
		double result = 0;
		try {
			result = solver.solve(NUM_INT_ITERATIONS, f, 0, UPPERBOUND_WITHDRAWAL * estimate, estimate);
		} catch (Exception e) {
			result = -1;
		}
		
		return result;
	}
	
	/**
	 * Simple annuity calculation, how much money to retire every year in real terms to 
	 * get broke at a given age in real terms
	 * @param ageBroke 
	 * @return
	 */
	private double getNoVolatileMaxWithdrawal(int ageBroke) {
		double interest = realRate(portfolio.getDefaultRate());
		double comp1 = Math.pow((1 + interest), (ageBroke - currentAge));  // (1+i)^n
		return initialPrincipal * (interest * comp1) / (comp1 - 1);
	}
	
	/**
	 * Perform MonteCarloSimulation building several random FutureProjections
	 * with parameters used when object was constructed
	 * Each iteration item in the array is a complete randomized projection 
	 * of cashflows until broke (i.e. each iteration is a complete amortization table)
	 * @param iterations, Number of iterations in the MonteCarlo
	 * @return FutureProjection[iterations]
	 */
	public RetirementAnalyzer[] getProjectionArray(int iterations) {
		RetirementAnalyzer[] results = new RetirementAnalyzer[iterations];
		for (int i = 0; i < iterations; i++) {
			RetirementAnalyzer projection = this.clone();
			results[i] = projection;
		}
		return results;
	}
	
	/**
	 * Build a MonteCarlo simulation using internal default iterations
	 * @param iterations Number of iterations
	 */
	public void buildMonteCarlo() {
		buildMonteCarlo(NUM_INT_ITERATIONS);
	}
	
	/**
	 * Build a MonteCarlo simulation as per supplied number of iterations
	 * @param iterations Number of iterations
	 */
	public void buildMonteCarlo(int iterations) {
		// Initialize principal confidence intervals		
		this.ageBrokeDistribution = new Frequency();
		this.principalsIntervals = new ConfidenceInterval[ageCardinality];	
		for (int i = 0; i < ageCardinality; i++) {
			principalsIntervals[i] = new ConfidenceInterval();
		}
		
		// Build dataSets
		for (int iter = 0; iter < iterations; iter++) {
			RetirementAnalyzer projection = this.clone();
			// Build ageBroke Frequency distribution
			ageBrokeDistribution.addValue(projection.getAgeBroke()); 
			
			// Build Principal confidence intervals
			for (int age = currentAge; age <= maxAge; age++) {
				principalsIntervals[age - currentAge].addValue(projection.getProjectedData(age).getPrincipal());
			}
		}
		isMonteCarloBuilt = true;
	}
	
	/**
	 * Write CSV file with key analysis result from previously built Monte Carlo simulation
	 * if a Monte Carlo has not been built, it builds one using default interations
	 * Has the form: "age,mean_principal,min_principal,max_principal,iterations_broke,cumm_prob_broke"
	 * @param filename string
	 * @throws IOException For errors writing the file, typically if its open by another program
	 */
	public void writeMontecarloOutputCSV(String filename) throws IOException {
		SummaryMonteCarlo summary = getMonteCarloSummary(); 
		FileWriter fw = new FileWriter(filename);
		PrintWriter pw = new PrintWriter(fw);
		pw.println("age,mean_principal,min_principal,max_principal,iterations_broke,cumm_prob_broke");
		for (int i = 0; i < ageCardinality; i++ ) {
			String line = summary.getAge().get(i) + "," + summary.getMeanPrincipal().get(i);
			line += "," + summary.getMinPrincipal().get(i);
			line += "," + summary.getMaxPrincipal().get(i);
			line += "," + summary.getNumBroke().get(i) + "," + summary.getProbBroke().get(i);
			pw.println(line);
		}
		pw.close();
	}
	
	/**
	 * Summary of results table
	 * @return
	 */
	public SummaryMonteCarlo getMonteCarloSummary() {
		if (!isMonteCarloBuilt) buildMonteCarlo(); 
		SummaryMonteCarlo output = new SummaryMonteCarlo(retirementAge);
		for (int i = 0; i < ageCardinality; i++ ) {
			int age = i + currentAge;
			double minPrincipal = Math.round(principalsIntervals[i].getMinConfInterval());
			double maxPrincipal = Math.round(principalsIntervals[i].getMaxConfInterval());
			double meanPrincipal = Math.round(principalsIntervals[i].getAverage());
			int numBroke = (int)ageBrokeDistribution.getCount(age);
			double probBroke = ageBrokeDistribution.getCumPct(age);
			output.addRow(age, minPrincipal, maxPrincipal, meanPrincipal, numBroke, probBroke);
		}
		return output;
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
	public AmotizationTableRow getProjectedData(int age) throws IllegalArgumentException {
		validateAgeBounds(age, true);
		return data[age - currentAge];
	}
	
	/**
	 * Get a single randomized amortization table for simulated retirement
	 * @return Current scenario amortization table
	 */
	public AmotizationTableRow[] getData() {
		return data;
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
	public void printAmortizationTable() {

		try {
			FileWriter fw = new FileWriter("data.csv");
			PrintWriter pw = new PrintWriter(fw);
			System.out.println("Age     P          r         f         CashFlow");
			pw.println("Age,P,r,f,CashFlow");
			for (int i = 0; i < data.length; i++) {
				AmotizationTableRow fpd = data[i];
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
	
	/**
	 * Validate that age is within object construction bounds
	 * @param age Age 
	 * @param upper If true validates age >= currentAge and age <= maxAge, if false
	 * only validates lower limit
	 */
	private void validateAgeBounds(int age, boolean upper) throws IllegalArgumentException {
		if (upper && age > maxAge) {
			throw new IllegalArgumentException("Age less than currentAge");
		} 
		if (age < currentAge) {
			throw new IllegalArgumentException("Age greater than maxAge");
		}
	}
	
	/**
	 * 2 sigma interval of principals at a given age, builds a default monte carlo
	 * if has not been previously built
	 * @param age
	 * @return
	 * @throws IllegalArgumentException
	 */
	public ConfidenceInterval getPrincipalInterval(int age) throws IllegalArgumentException {
		validateAgeBounds(age, true);
		if (!isMonteCarloBuilt) buildMonteCarlo();
		return principalsIntervals[age - currentAge];
	}
	
}
