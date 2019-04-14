
public class InvestmentPortfolio {
	private double equity;
	private double mean;
	private double stdev;
	
	public InvestmentPortfolio (double equityPercentage) {
		this.equity = equityPercentage / 100.0;
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
