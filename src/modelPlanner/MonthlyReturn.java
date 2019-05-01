package modelPlanner;
/**
 * Stores returns for stocks and bonds for each month
 * 
 * @author Team 11
 *
 */
public class MonthlyReturn {
	private Double stocksReturn;
	private Double bondsReturn;
	
	
	//constructor for class that uses most of the data columns from the csv file
	public MonthlyReturn(Double stocksRet, Double bondsRet) {
		stocksReturn = stocksRet;
		bondsReturn = bondsRet;
		
	}


	public Double getStocksReturn() {
		return stocksReturn;
	}


	public Double getBondsReturn() {
		return bondsReturn;
	}

	
	
	
	
	
} // end of class MonthlyReturn
