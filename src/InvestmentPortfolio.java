
public class InvestmentPortfolio {
	double equityPercentage;
	
	
	public InvestmentPortfolio (double equityPercentage) {
		this.equityPercentage = equityPercentage;
	}
	
	public double getAverageReturns() {
		return 0.05; //Dummy value for testing
	}
	
	public double getStdDevReturns() {
		return 0.03;
	}
	
}
