import java.io.*;
import java.util.*;
import org.apache.commons.math3.distribution.*;

/**
 * Build a simulated retirement projection using portfolio variability using 
 * NormalDistribution for return estimation
 * @author Enrique Vargas
 *
 */
public class FutureProjection {
	private int ageBroke;
	ArrayList<FutureProjectionData> data;
	private NormalDistribution nd;
	private Random r;
	private double initialPrincipal;
	private double yrdeposits;
	private double withdrawals;
	private int currentAge;
	private int retirementAge;
	private double inflation;
	
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
	public FutureProjection(double principal, double yrdeposits, double withdrawals, int age, 
			int retirementAge, double inflation, InvestmentPortfolio portfolio) {
		this.initialPrincipal = principal;
		this.yrdeposits = yrdeposits;
		this.withdrawals = withdrawals;
		this.currentAge = age;
		this.retirementAge = retirementAge;
		this.inflation = inflation;
		
		this.r =new Random();
		this.data = new ArrayList<>();
		this.nd = new NormalDistribution(portfolio.getAverageReturns(), portfolio.getStdDevReturns());
		buildProjectionData();
	}
	
	/**
	 * Build amortization table, used on constructor
	 */
	private void buildProjectionData() {
		double principal = initialPrincipal;
		boolean savingPeriod = true;
		
		// Saving period
		int age = currentAge;
		while ( savingPeriod || principal >= retirementAge ) {
			double nominalRate = nd.inverseCumulativeProbability(r.nextDouble());
			double realRate = realRate(nominalRate);
			double cashflow = 0;
		
			if (age <= retirementAge) {
				cashflow = yrdeposits;
			} else {
				savingPeriod = false;
				cashflow = -1 * withdrawals;
			}
			
			FutureProjectionData fpd = new FutureProjectionData(age, realRate, inflation, principal, cashflow);
			data.add(fpd);
			
			principal += principal * realRate + cashflow;
			age++;
		}
		
		ageBroke = age;
	}
	
	/**
	 * Calculate real interest rate based on nominalRate and provided inflation
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
		
		if (i < currentAge) {
			throw new IllegalArgumentException("Age before present day");
		}
		
		if (i < data.size() ) {
			// You are broke
			return null;
		}
		
		return data.get(i);
	}
	
	/**
	 * Get amortization table for simulated retirement
	 * @return ArrayList<FutureProjectionData>
	 */
	public ArrayList<FutureProjectionData> getData() {
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
		FileWriter fw;
		try {
			fw = new FileWriter("data.csv");
			PrintWriter pw = new PrintWriter(fw);
			System.out.println("Age     P          r         f         CashFlow");
			pw.println("Age,P,r,f,CashFlow");
			for (int i = 0; i < data.size(); i++) {
				FutureProjectionData fpd = data.get(i);
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
		InvestmentPortfolio ip = new InvestmentPortfolio(100.0);
		FutureProjection fp = new FutureProjection(ui.getPrincipal(), ui.getYearlyDeposits(), ui.getTargetRetirement(),
				ui.getCurrentAge(), ui.getTargetRetirementAge(), ui.getInflation(), ip);
		
		fp.printAmortizationTable();
	}

}
