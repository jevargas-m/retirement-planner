package modelPlanner;
import java.io.FileNotFoundException;
import java.util.Random;
import org.apache.commons.math3.distribution.NormalDistribution;

/**
 * Build a user portfolio based on NormalDistribution
 * 
 * @author Team 11
 *
 */
public class InvestmentPortfolio implements SimulableRate {
	private NormalDistribution nd;
	private Random r = new Random();
	private double defaultReturn;
	
	/**
	 * Build a portfolio according to equitu percentage
	 * @param equityPercentage
	 * @throws FileNotFoundException 
	 */
	public InvestmentPortfolio(double equityPercentage) throws IllegalArgumentException, FileNotFoundException {
		ReturnCalc build = new ReturnCalc("portfoliodata.csv");
		defaultReturn = build.averageAnnualReturn(build.returnsPortfolio(build.monthlyReturnStocksBonds, equityPercentage));
		double stdev = build.annualStandardDeviation(build.returnsPortfolio(build.monthlyReturnStocksBonds, equityPercentage));
		if (stdev <= 0) throw new IllegalArgumentException("Invalid standatd deviation");
		this.nd = new NormalDistribution(defaultReturn, stdev);
	}
	
	/**
	 * Build portfolio according to supplied mean and stdev
	 * @param mean
	 * @param stdev
	 * @throws IllegalArgumentException
	 */
	public InvestmentPortfolio(double mean, double stdev) throws IllegalArgumentException {
		if (stdev <= 0) throw new IllegalArgumentException("Invalid standatd deviation");
		defaultReturn = mean;
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
