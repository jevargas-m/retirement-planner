import java.io.*;
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
	
	/**
	 * Analyze MonteCarlo simulation results
	 * @param simulationData FutureProjection[]
	 */
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
		
	/**
	 * Initialize analysis datasets, used in constructor
	 */
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
	
	/**
	 * Cummulative probability of being broke up to certain age
	 * @param age int
	 * @return double
	 * @throws IllegalArgumentException, when age is out of bounds
	 */
	public double getProbBrokeAtAge(int age) throws IllegalArgumentException {
		if (age < currentAge || age > maxAge) {
			throw new IllegalArgumentException("Age out of bounds");
		}
		return ageBrokeDistribution.getCumPct(age);
	}
	
	/**
	 * Write CSV file with key analysis result from Monte Carlo simulation
	 * @param filename string
	 * @throws IOException
	 */
	public void writeOutputCSV(String filename) throws IOException {
		FileWriter fw = new FileWriter(filename);
		PrintWriter pw = new PrintWriter(fw);
		pw.println("age,mean_principal,min_principal,max_principal,iterations_broke,cumm_prob_broke");
		for (int i = 0; i < ageCardinality; i++ ) {
			int age = i + currentAge;
			String line = age + "," + Math.round(principals[i].getAverage());
			line += "," + Math.round(principals[i].getMinConfInterval());
			line += "," + Math.round(principals[i].getMaxConfInterval());
			line += "," + ageBrokeDistribution.getCount(age) + "," + ageBrokeDistribution.getCumPct(age);
			pw.println(line);
		}
		pw.close();
	}
	
	// for testing
	public static void main(String[] args) {
		// Test Case
		UserInputs ui = UserInputs.getDefaultInputs();  // Use default constructor parameters
		InvestmentPortfolio ip = new InvestmentPortfolio(ui.getEquityPercentage());
		FutureProjection fp = new FutureProjection(ui.getPrincipal(), ui.getYearlyDeposits(), ui.getTargetRetirement(),
				ui.getCurrentAge(), ui.getMaxAge(), ui.getTargetRetirementAge(), ui.getInflation(), ip);
		SimulationAnalyzer sa = new SimulationAnalyzer(fp.monteCarloSimulation(50000));
		
		System.out.println("Prob of broke at 90 is : " + sa.getProbBrokeAtAge(90));
		
		try {
			// Test principal confidence intervals
			String filename = "output.csv";
			sa.writeOutputCSV(filename);
			System.out.println("DONE!: check " + filename);
			
		} catch (IOException e) {
			System.out.println("ERROR: Close the output file first!");
		}
	}	
	
}
