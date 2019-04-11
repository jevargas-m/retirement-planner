import org.apache.commons.math3.stat.descriptive.*;

/**
 * Provide confidence interval for a given population
 * 95% confidence interval
 * check calculation details in: https://en.wikipedia.org/wiki/Confidence_interval
 * @author Enrique Vargas;
 */
public class ConfidenceInterval {
	private final double ZETA = 1.96; // Value for 95% conf interval
	private DescriptiveStatistics population;
	

	public ConfidenceInterval() {
		population = new DescriptiveStatistics();
	}
	
	public double delta() {
		return ZETA * population.getStandardDeviation() / Math.sqrt(population.getN());
	}
	
	public double getMinConfInterval() {
		return population.getMean() - delta();
	}
	
	public double getMaxConfInterval() {
		return population.getMean() + delta();
	}
	
	public double getAverage() {
		return population.getMean();
	}
	
	public void addValue(double v) {
		population.addValue(v);
	}
	
}
