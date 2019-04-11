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
	private UserInputs ui;
	
	/**
	 * Build a new projection based on userInputs and investment portfolio
	 * @param ui UserInputs
	 * @param portfolio InvestmentPortfolio
	 */
	public FutureProjection(UserInputs ui, InvestmentPortfolio portfolio) {
		this.ui = ui;
		this.r =new Random();
		data = new ArrayList<>();
		this.nd = new NormalDistribution(portfolio.getAverageReturns(), portfolio.getStdDevReturns());
		buildProjectionData();
	}
	
	/**
	 * Build amortization table, used on constructor
	 */
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
	
	/**
	 * Calculate real interest rate based on nominalRate and user provided inflation
	 * @param nominalRate
	 * @return double
	 */
	private double realRate(double nominalRate) {
		return (nominalRate - ui.getInflation()) / (1 + ui.getInflation());
	}
	
	/**
	 * Return Projected
	 * @param age
	 * @return
	 * @throws IllegalArgumentException
	 */
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
		FutureProjection fp = new FutureProjection(ui, ip);
		fp.printAmortizationTable();
	}

}
