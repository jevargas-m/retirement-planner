package modelPlanner;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;

/**
 * Unit tests.  As this is a random simulation.  If a test fails please run again, is likely due to variability
 *  in results (inherent to Monte Carlo)
 *  
 * Classes Tested:  UserInputs, InvestmentPortfolio, RetirementAnalyzer, ConfidenceInterval, SummaryMonteCarlo, ReturnCalc
 * 
 * @author Team 11
 *
 */
class SimulationUnitTests {

	@Test
	void testGetAgeBroke1() {
		// Test Scenario #1
		UserInputs ui = UserInputs.getDefaultInputs();
		InvestmentPortfolio ip = new InvestmentPortfolio(0.05, 0.0000001);
		
		RetirementAnalyzer fp = new RetirementAnalyzer(ui.getPrincipal(), ui.getYearlyDeposits(), ui.getTargetRetirement(),
				ui.getCurrentAge(), ui.getMaxAge(),ui.getTargetRetirementAge(), ui.getInflation(), ip, ui.isRealMoney());
		
		assertEquals(91, fp.getAgeBroke());
	}
	
	@Test
	void testGetAgeBroke2() {
		// Test Scenario #2
		UserInputs ui = new UserInputs();
		ui.setCurrentAge(30);
		ui.setRealMoney(true);
		ui.setMaxAge(105);
		ui.setInflation(0.03);
		ui.setPrincipal(200000);
		ui.setTargetRetirementAge(60);
		ui.setTargetRetirement(25000);
		ui.setYearlyDeposits(0);
		
		InvestmentPortfolio ip = new InvestmentPortfolio(0.02, 0.0000001);
		
		RetirementAnalyzer fp = new RetirementAnalyzer(ui.getPrincipal(), ui.getYearlyDeposits(), ui.getTargetRetirement(),
				ui.getCurrentAge(), ui.getMaxAge(),ui.getTargetRetirementAge(), ui.getInflation(), ip, ui.isRealMoney());
		
		assertEquals(65, fp.getAgeBroke());
	}
	
	@Test
	void testGetAgeBroke3() {
		// Test Scenario #3
		UserInputs ui = new UserInputs(30, 105, 0.0, 10000, true, 25000, 60, 0.3, 100000);
		InvestmentPortfolio ip = new InvestmentPortfolio(0.06, 0.0000001);
		
		RetirementAnalyzer fp = new RetirementAnalyzer(ui.getPrincipal(), ui.getYearlyDeposits(), ui.getTargetRetirement(),
				ui.getCurrentAge(), ui.getMaxAge(),ui.getTargetRetirementAge(), ui.getInflation(), ip, ui.isRealMoney());
		
		assertEquals(105, fp.getAgeBroke());
	}
	
	@Test
	void testProjectedData1() {
		UserInputs ui = UserInputs.getDefaultInputs();
		InvestmentPortfolio ip = new InvestmentPortfolio(0.05, 0.0000001);
		
		RetirementAnalyzer fp = new RetirementAnalyzer(ui.getPrincipal(), ui.getYearlyDeposits(), ui.getTargetRetirement(),
				ui.getCurrentAge(), ui.getMaxAge(),ui.getTargetRetirementAge(), ui.getInflation(), ip, ui.isRealMoney());
		
		assertEquals(430045.00, fp.getProjectedData(70).getPrincipal(), 1.0);
	}
	
	@Test
	void testProjectedData2() {
		UserInputs ui = UserInputs.getDefaultInputs();
		InvestmentPortfolio ip = new InvestmentPortfolio(0.05, 0.0000001);
		
		RetirementAnalyzer fp = new RetirementAnalyzer(ui.getPrincipal(), ui.getYearlyDeposits(), ui.getTargetRetirement(),
				ui.getCurrentAge(), ui.getMaxAge(),ui.getTargetRetirementAge(), ui.getInflation(), ip, ui.isRealMoney());

		assertEquals(-25000.00, fp.getProjectedData(70).getPmt(), 1.0);
	}
	
	@Test
	void testProjectedData3() {
		UserInputs ui = UserInputs.getDefaultInputs();
		InvestmentPortfolio ip = new InvestmentPortfolio(0.05, 0.0000001);
		
		RetirementAnalyzer fp = new RetirementAnalyzer(ui.getPrincipal(), ui.getYearlyDeposits(), ui.getTargetRetirement(),
				ui.getCurrentAge(), ui.getMaxAge(),ui.getTargetRetirementAge(), ui.getInflation(), ip, ui.isRealMoney());
		
		assertThrows(IllegalArgumentException.class, () -> {
			fp.getProjectedData(29);
	    });
		
		assertThrows(IllegalArgumentException.class, () -> {
			fp.getProjectedData(106);
	    });
	}
	
	@Test
	void testGetProbBrokeAtAge1() {
		UserInputs ui = UserInputs.getDefaultInputs();
		
		try {
			InvestmentPortfolio ip = new InvestmentPortfolio(ui.getEquityPercentage());
			RetirementAnalyzer fp = new RetirementAnalyzer(ui.getPrincipal(), ui.getYearlyDeposits(), ui.getTargetRetirement(),
					ui.getCurrentAge(), ui.getMaxAge(),ui.getTargetRetirementAge(), ui.getInflation(), ip, ui.isRealMoney());
			
			fp.buildMonteCarlo(100000);

			assertEquals(0.23, fp.getProbBrokeAtAge(100), 0.05);
			
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	void testGetProbBrokeAtAge2() {
			UserInputs ui = new UserInputs(27, 95, 0.0, 10000, true, 22000, 60, 0.3, 100000);
			InvestmentPortfolio ip = new InvestmentPortfolio(0.05, 0.0000001);
			RetirementAnalyzer fp = new RetirementAnalyzer(ui.getPrincipal(), ui.getYearlyDeposits(), ui.getTargetRetirement(),
					ui.getCurrentAge(), ui.getMaxAge(),ui.getTargetRetirementAge(), ui.getInflation(), ip, ui.isRealMoney());

			assertEquals(0.0, fp.getProbBrokeAtAge(90), 0.02);
	}
	
	@Test
	void testConfidenceInterval1() {
		UserInputs ui = UserInputs.getDefaultInputs();
		try {
			InvestmentPortfolio ip = new InvestmentPortfolio(0);
			RetirementAnalyzer fp = new RetirementAnalyzer(ui.getPrincipal(), ui.getYearlyDeposits(), ui.getTargetRetirement(),
					ui.getCurrentAge(), ui.getMaxAge(),ui.getTargetRetirementAge(), ui.getInflation(), ip, ui.isRealMoney());
			ConfidenceInterval ci = fp.getPrincipalInterval(80);
			
			fp.buildMonteCarlo(100000);
			

			assertEquals(89700.0, ci.getMaxConfInterval(), 89700.0 * 0.05);  // Needs high tolerance due to variability
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	@Test
	void testConfidenceInterval2() {
		UserInputs ui = UserInputs.getDefaultInputs();
		try {
			InvestmentPortfolio ip = new InvestmentPortfolio(0);
			RetirementAnalyzer fp = new RetirementAnalyzer(ui.getPrincipal(), ui.getYearlyDeposits(), ui.getTargetRetirement(),
					ui.getCurrentAge(), ui.getMaxAge(),ui.getTargetRetirementAge(), ui.getInflation(), ip, ui.isRealMoney());
			ConfidenceInterval ci = fp.getPrincipalInterval(80);
			fp.buildMonteCarlo(100000);

			assertEquals(40280.0, ci.getMinConfInterval(), 40280.0 * 0.05); // Needs high tolerance due to variability
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
	@Test
	void testMaxWithdrawal1() {
		UserInputs ui = UserInputs.getDefaultInputs();
	
		try {
			InvestmentPortfolio ip = new InvestmentPortfolio(ui.getEquityPercentage());
			RetirementAnalyzer fp = new RetirementAnalyzer(250000, ui.getYearlyDeposits(), ui.getTargetRetirement(),
					ui.getCurrentAge(), ui.getMaxAge(),ui.getTargetRetirementAge(), ui.getInflation(), ip, ui.isRealMoney());
			
			double safe = fp.getMaxSafeWithdrawal(90, 0.1);
			
			RetirementAnalyzer fp2 = new RetirementAnalyzer(250000, ui.getYearlyDeposits(), safe,
					ui.getCurrentAge(), ui.getMaxAge(), ui.getCurrentAge(), ui.getInflation(), ip, true);
			
			assertEquals(0.1, fp2.getProbBrokeAtAge(90), 0.02);
			
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	void testMaxWithdrawal2() {
		UserInputs ui = new UserInputs(65, 106, 0.03, 0, true, 25000, 65, 0.5, 350000);

		try {
			InvestmentPortfolio ip = new InvestmentPortfolio(0.5);
			RetirementAnalyzer fp = new RetirementAnalyzer(100000, ui.getYearlyDeposits(), ui.getTargetRetirement(),
					ui.getCurrentAge(), 106, ui.getTargetRetirementAge(), ui.getInflation(), ip, ui.isRealMoney());
			
			double safe = fp.getMaxSafeWithdrawal(105, 0.2);
			
			RetirementAnalyzer fp2 = new RetirementAnalyzer(100000, ui.getYearlyDeposits(), safe,
					ui.getCurrentAge(), ui.getMaxAge(), ui.getCurrentAge(), ui.getInflation(), ip, true);
			
			assertEquals(0.2, fp2.getProbBrokeAtAge(105), 0.03);
			
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	void testSummary() {
		UserInputs ui = new UserInputs(65, 106, 0.03, 0, true, 25000, 65, 0.5, 350000);
		try {
			InvestmentPortfolio ip = new InvestmentPortfolio(1.0);
			RetirementAnalyzer fp = new RetirementAnalyzer(100000, ui.getYearlyDeposits(), ui.getTargetRetirement(),
					30, 100, ui.getTargetRetirementAge(), ui.getInflation(), ip, ui.isRealMoney());
			
			SummaryMonteCarlo s = fp.getMonteCarloSummary();
			
			assertEquals(100 - 30 + 1, s.getNumRows());  // Must match cardinality of years
			
			
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test 
	void testDataReading1() {
		try {
			ReturnCalc build = new ReturnCalc("portfoliodata.csv");
			double average = build.averageAnnualReturn(build.returnsPortfolio(build.monthlyReturnStocksBonds, 0.0));
			double stdev = build.annualStandardDeviation(build.returnsPortfolio(build.monthlyReturnStocksBonds, 0.0));
			
			assertEquals(0.042, average, 0.001);
			assertEquals(0.00452, stdev, 0.00001);
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@Test 
	void testDataReading2() {
		try {
			ReturnCalc build = new ReturnCalc("portfoliodata.csv");
			double average = build.averageAnnualReturn(build.returnsPortfolio(build.monthlyReturnStocksBonds, 1.0));
			double stdev = build.annualStandardDeviation(build.returnsPortfolio(build.monthlyReturnStocksBonds, 1.0));
			
			assertEquals(0.1050, average, 0.001);
			assertEquals(0.14357, stdev, 0.00001);
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	

}