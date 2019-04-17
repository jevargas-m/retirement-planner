import java.io.*;
import java.util.ArrayList;

/**
 * Analyze a simulation results providing meaningful information
 * such as confidence intervals of having money at any given age
 * @author Enrique Vargas
 *
 */
public class SimulationAnalyzer {
	private FutureProjection[] simulationData;
	private ConfidenceInterval[] confinterval;
	private int maxAgeToAnalyze = 110;
	private int initialAge;

	
	public SimulationAnalyzer(FutureProjection[] simulationData) {
		this.simulationData = simulationData;
		this.confinterval = new ConfidenceInterval[maxAgeToAnalyze];
		this.initialAge = simulationData[0].getCurrentAge();
		for (int i = 0; i < maxAgeToAnalyze; i++) {
			confinterval[i] = new ConfidenceInterval();
		}
		
		for (int iter = 0; iter< simulationData.length; iter++) {
			// Iterates over each simulation iteration
			for (int indexAge = 0; indexAge < maxAgeToAnalyze - initialAge; indexAge++) {
				// Iterates over all available age's
				ArrayList<FutureProjectionData> projection = simulationData[iter].getData();
				if (indexAge < projection.size()) {
					FutureProjectionData datapoint = projection.get(indexAge);
					double principal = datapoint.getPrincipal();
					confinterval[indexAge].addValue(principal);
				}
			}
		}
	}

	public int getMaxAgeToAnalyze() {
		return maxAgeToAnalyze;
	}

	public ConfidenceInterval[] getConfinterval() {
		return confinterval;
	}
	
	
	
	// for testing
	public static void main(String[] args) {

		try {
			FileWriter fw = new FileWriter("confgraph.csv");
			PrintWriter pw = new PrintWriter(fw);
			
			UserInputs ui = new UserInputs();
			InvestmentPortfolio ip = new InvestmentPortfolio(30);
			FutureProjection fp = new FutureProjection(ui.getPrincipal(), ui.getYearlyDeposits(), ui.getTargetRetirement(),
					ui.getCurrentAge(), ui.getTargetRetirementAge(), ui.getInflation(), ip);
			SimulationAnalyzer sa = new SimulationAnalyzer(fp.monteCarloSimulation(50000));
			ConfidenceInterval[] ci = sa.getConfinterval();
			
			
			for (int i=0; i <= sa.maxAgeToAnalyze - sa.initialAge; i++ ) {
				String line = i + ui.getCurrentAge() + "," + Math.round(ci[i].getMinConfInterval()) + ","
						+ Math.round(ci[i].getMaxConfInterval()) + "," + ci[i].getN();
				System.out.println(line);
				pw.println(line);
			}
			pw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}



	
	
}
