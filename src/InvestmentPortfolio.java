/**
 * Build a user portfolio
 * TODO: This class could be merged with ReturnCalc for cohesion
 * @author Enrique Vargas
 *
 */
public class InvestmentPortfolio {
	private double equity;
	private double mean;
	private double stdev;
	
	/**
	 * Build a portfolio according to 
	 * @param equityPercentage
	 */
	public InvestmentPortfolio (double equityPercentage) {
		this.equity = equityPercentage;
		ReturnCalc build = new ReturnCalc("portfoliodata.csv");
		mean = build.averageAnnualReturn(build.returnsPortfolio(build.monthlyReturnStocksBonds, this.equity));
		stdev = build.annualStandardDeviation(build.returnsPortfolio(build.monthlyReturnStocksBonds, this.equity));
	}
	
	public InvestmentPortfolio (double mean, double stdev) {
		this.mean = mean;
		this.stdev = stdev;
		this.equity = 0.0;
	}

	public double getAverageReturns() {
		return mean; 
	}
	
	public double getStdDevReturns() {
		return stdev;
	}
	
}
