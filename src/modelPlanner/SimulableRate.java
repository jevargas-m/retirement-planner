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
	 * @return  Randomized rate
	 */
	double nextRate();
	
	/**
	 * 
	 * Default expected value (mean)
	 * @return Expected rate
	 */
	double getDefaultRate();
	
}
