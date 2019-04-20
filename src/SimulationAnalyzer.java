import java.io.*;
import java.util.ArrayList;
import org.apache.commons.math3.distribution.*;
import org.apache.commons.math3.stat.Frequency;

/**
 * Analyze a simulation results providing meaningful information
 * such as confidence intervals of having money at any given age
 * @author Enrique Vargas
 *
 */
public class SimulationAnalyzer {
	private FutureProjection[] data;
	private ConfidenceInterval[] principals;
	private Frequency ageBrokeDistribution;
	private int maxAge;
	private int currentAge;
	private int ageCardinality;
	private int iterations;
		
	public SimulationAnalyzer(FutureProjection[] simulationData) {
		this.data = simulationData;	
		this.maxAge = simulationData[0].getMaxAge();
		this.currentAge = simulationData[0].getCurrentAge();
		this.ageCardinality = maxAge - currentAge + 1;
		this.iterations = simulationData.length;
		this.principals = new ConfidenceInterval[ageCardinality];
		this.ageBrokeDistribution = new Frequency();
		buildDataSets();
	}
		
	public ConfidenceInterval[] getPrincipals() {
		return principals;
	}

	
	
	public Frequency getAgeBrokeDistribution() {
		return ageBrokeDistribution;
	}

	private void buildDataSets() {
		// Initialize principal confidence intervals
		for (int i = 0; i < ageCardinality; i++) {
			principals[i] = new ConfidenceInterval();
		}
		// Build dataSets
		for (int iter = 0; iter < iterations; iter++) {
			// Build Principal confidence intervals
			for (int indexAge = 0; indexAge < ageCardinality; indexAge++) {
				FutureProjectionData[] projection = data[iter].getData();
				principals[indexAge].addValue(projection[indexAge].getPrincipal());
			}
			
			// ageBroke Frequency distribution
			ageBrokeDistribution.addValue(data[iter].getAgeBroke());
		}
	}
	
	// for testing
	public static void main(String[] args) {
		// Test Case
		UserInputs ui = new UserInputs();  // Use default constructor parameters
		InvestmentPortfolio ip = new InvestmentPortfolio(ui.getEquityPercentage());
		FutureProjection fp = new FutureProjection(ui.getPrincipal(), ui.getYearlyDeposits(), ui.getTargetRetirement(),
				ui.getCurrentAge(), ui.getMaxAge(), ui.getTargetRetirementAge(), ui.getInflation(), ip);
		SimulationAnalyzer sa = new SimulationAnalyzer(fp.monteCarloSimulation(50000));

		try {
			// Test principal confidence intervals
			FileWriter fw1 = new FileWriter("confgraph.csv");
			PrintWriter pw1 = new PrintWriter(fw1);
			ConfidenceInterval[] principals = sa.getPrincipals();
			for (int i = 0; i < sa.ageCardinality; i++ ) {
				String line = i + ui.getCurrentAge() + "," + Math.round(principals[i].getMinConfInterval()) + ","
						+ Math.round(principals[i].getMaxConfInterval()) ;
				System.out.println(line);
				pw1.println(line);
			}
			pw1.close();
			System.out.println();
			
			// Test ageBroke distribution
			FileWriter fw2 = new FileWriter("ageBrokeDistribution.csv");
			PrintWriter pw2 = new PrintWriter(fw2);
			Frequency f = sa.getAgeBrokeDistribution();
						
			for (int i = 0; i < sa.ageCardinality - 1; i++ ) {
				int age = i + ui.getCurrentAge();
				String line = age  + "," + f.getCount(age) + "," + f.getCumPct(age);
				System.out.println(line);
				pw2.println(line);
			}
			pw2.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}



	
	
}
