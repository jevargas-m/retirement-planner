package modelPlanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Read a file with historical return data which will produce
 * an arraylist with all monthly returns of stocks and bonds separately
 * @author Team 11
 *
 */
public class ReturnCalc {

	ArrayList<MonthlyReturn> monthlyReturnStocksBonds;
	ArrayList<Double> portfolioReturns;
	
	/**
	 * constructor of class, produces an arraylist with all the monthly returns of stocks and bonds separately
	 * @param filename file to read
	 */
	public ReturnCalc(String filename) throws FileNotFoundException {
		File historicalData = new File(filename);
		monthlyReturnStocksBonds = new ArrayList<MonthlyReturn>();

			Double stockRet;
			Double bondRet;
			
			Scanner scanner = new Scanner(historicalData);
			for (int i=0; i<9;i++) {
			scanner.nextLine();
			}//skip 9 lines to get to data 
			
			while (scanner.hasNextLine()) {
				String returnRow = scanner.nextLine();
				String[] columnData = returnRow.split(",");
				stockRet = Double.parseDouble(columnData[2]);
				bondRet = Double.parseDouble(columnData[5]);
						
				MonthlyReturn returnStockBonds = new MonthlyReturn(stockRet, bondRet);
				monthlyReturnStocksBonds.add(returnStockBonds);
				
			}
			scanner.close();

	}// end of Class ReturnCalc
		
	
	/**
	 * method to calculate monthly portfolio return, given monthly returns of stocks/bonds and percent equity of portfolio
	 * @param returns
	 * @param percentEquity
	 * @return
	 */
	public ArrayList<Double> returnsPortfolio (ArrayList<MonthlyReturn> returns, Double percentEquity) {
		ArrayList<Double> portfolioReturns = new ArrayList<Double>();
		Double monthlyPortfolioReturn;
		for (int i=0; i< returns.size();i++) {
			monthlyPortfolioReturn = (1-percentEquity)*returns.get(i).getBondsReturn()+(percentEquity)*returns.get(i).getStocksReturn();
			portfolioReturns.add(monthlyPortfolioReturn);
			
		}
		
		
		return portfolioReturns;
		
	}// end of method Returns Portfolio
	
	/**
	 * Method that will take arraylist of monthly returns and will return annualized avg return
	 * @param portReturns
	 * @return
	 */
	public Double averageAnnualReturn(ArrayList<Double> portReturns) {
		Double annualizedReturn = null;
		Double sum = 0.00;
		for (Double item : portReturns) {
			
			sum = sum + item;
		}
		
		Double avgMonthlyReturn = sum/portReturns.size();
		annualizedReturn = Math.pow(1+avgMonthlyReturn, 12)-1;
		// System.out.println(annualizedReturn);
		return annualizedReturn;
	} // end of method averageAnnualReturn
	
	
	/**
	 * Method that will take arraylist of monthly returns and will return annualized std dev
	 * @param args
	 */
	public Double annualStandardDeviation(ArrayList<Double> portReturns) {
		Double sumOfX = 0.00;
		Double monthlyStanDev = 0.00;
		Double annualizedStDev = 0.00;
		Double sum = 0.00;
		for (Double item : portReturns) {
			
			sum = sum + item;
		}
		
		Double avgMonthlyReturn = sum/portReturns.size();
		
		for (Double line : portReturns) {
			double x = Math.pow(line - avgMonthlyReturn, 2);
			sumOfX = sumOfX + x;
			
		}
		monthlyStanDev = Math.sqrt(sumOfX/portReturns.size());
		annualizedStDev = monthlyStanDev * Math.sqrt(12);
		
		
		// System.out.println(annualizedStDev);
		return annualizedStDev;
	} // end of method averageAnnualStandardDeviation
	
	
	

	
}
