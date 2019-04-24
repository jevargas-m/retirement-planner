import java.util.Random;
import org.apache.commons.math3.distribution.NormalDistribution;

/**
 * Build a user portfolio
 * @author Team 11
 *
 */
public class InvestmentPortfolio implements SimulableRate {
	private NormalDistribution nd;
	private Random r;
	private double defaultReturn;
	
	/**
	 * Build a portfolio according to 
	 * @param equityPercentage
	 */
	public InvestmentPortfolio(double equityPercentage) throws IllegalArgumentException {
		ReturnCalc build = new ReturnCalc("portfoliodata.csv");
		defaultReturn = build.averageAnnualReturn(build.returnsPortfolio(build.monthlyReturnStocksBonds, equityPercentage));
		double stdev = build.annualStandardDeviation(build.returnsPortfolio(build.monthlyReturnStocksBonds, equityPercentage));
		if (stdev <= 0) throw new IllegalArgumentException("Invalid standatd deviation");
		buildParameters(defaultReturn, stdev);
	}
	
	public InvestmentPortfolio(double mean, double stdev) throws IllegalArgumentException {
		if (stdev <= 0) throw new IllegalArgumentException("Invalid standatd deviation");
		defaultReturn = mean;
		buildParameters(mean, stdev);
	}
	
	public void buildParameters(double mean, double stdev) {
		this.r = new Random();
		this.nd = new NormalDistribution(mean, stdev);
	}
	
	@Override
	public double nextRate() {
		return nd.inverseCumulativeProbability(r.nextDouble());
	}

	@Override
	public double getDefaultRate() {
		return defaultReturn;
	}	
}
