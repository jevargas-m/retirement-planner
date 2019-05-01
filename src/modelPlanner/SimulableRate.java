package modelPlanner;
/**
 * Interface used in RetirementAnalyzer to supply random source of interest rates
 * @author Team 11
 *
 */
public interface SimulableRate {
	/**
	 * Return a randomized annualized nominal return according to the defined probability
	 * distribution
	 * 
	 * Standard procedure for random sample according to probability distribution is
	 * 
	 * 	private NormalDistribution nd;
	 * 	private Random r;
	 *  double nextReturn() {
		  	return nd.inverseCumulativeProbability(r.nextDouble());
	 *  }
	 *  
	 */
	double nextRate();
	
	/**
	 * 
	 * Default expected value (mean)
	 * @return
	 */
	double getDefaultRate();
	
}
