import org.apache.commons.math3.stat.descriptive.*;

/**
 * Provide confidence interval for a given population
 * 2 sigma  confidence interval
 * @author Enrique Vargas;
 */
public class ConfidenceInterval {
	private DescriptiveStatistics population;

	public ConfidenceInterval() {
		population = new DescriptiveStatistics();
	}
	
	public double delta() {
		return 2 * population.getStandardDeviation() ;
	}
	
	public double getMinConfInterval() {
		double min = population.getMean() - delta();
		if (min == Double.NaN) {
			return 0;
		}
		return min;
	}
	
	public double getMaxConfInterval() {
		double max = population.getMean() + delta();
		if (max == Double.NaN) {
			return 0;
		}
		return max;
	}
	
	public double getAverage() {
		return population.getMean();
	}
	
	public void addValue(double v) {
		population.addValue(v);
	}
	
	public long getN() {
		return population.getN();
	}
	
}
