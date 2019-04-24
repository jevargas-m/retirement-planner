import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SimulationUnitTests {

	@Test
	void testGetAgeBroke1() {
		// Test Case #1
		UserInputs ui = UserInputs.getDefaultInputs();
		InvestmentPortfolio ip = new InvestmentPortfolio(0.05, 0.0000001);
		
		RetirementAnalyzer fp = new RetirementAnalyzer(ui.getPrincipal(), ui.getYearlyDeposits(), ui.getTargetRetirement(),
				ui.getCurrentAge(), ui.getMaxAge(),ui.getTargetRetirementAge(), ui.getInflation(), ip, ui.isRealMoney());
		
		assertEquals(79, fp.getAgeBroke());
	}
	
	@Test
	void testGetAgeBroke2() {
		// Test Case #2
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
		// Test Case #3
		UserInputs ui = new UserInputs(30, 105, 0.0, 10000, true, 25000, 60, 0.3, 100000);
		InvestmentPortfolio ip = new InvestmentPortfolio(0.06, 0.0000001);
		
		RetirementAnalyzer fp = new RetirementAnalyzer(ui.getPrincipal(), ui.getYearlyDeposits(), ui.getTargetRetirement(),
				ui.getCurrentAge(), ui.getMaxAge(),ui.getTargetRetirementAge(), ui.getInflation(), ip, ui.isRealMoney());
		
		assertEquals(105, fp.getAgeBroke());
	}
	
	@Test
	void testProjectedData1() {
		// Test Case #1
		UserInputs ui = UserInputs.getDefaultInputs();
		InvestmentPortfolio ip = new InvestmentPortfolio(0.05, 0.0000001);
		
		RetirementAnalyzer fp = new RetirementAnalyzer(ui.getPrincipal(), ui.getYearlyDeposits(), ui.getTargetRetirement(),
				ui.getCurrentAge(), ui.getMaxAge(),ui.getTargetRetirementAge(), ui.getInflation(), ip, ui.isRealMoney());
		
		assertEquals(214230.00, fp.getProjectedData(70).getPrincipal(), 1.0);
		assertEquals(-25000.00, fp.getProjectedData(70).getPmt(), 1.0);
			
		assertThrows(IllegalArgumentException.class, () -> {
			fp.getProjectedData(29);
	    });
		
		assertThrows(IllegalArgumentException.class, () -> {
			fp.getProjectedData(106);
	    });
	}

	@Test
	void testGetProbBrokeAtAge() {
		UserInputs ui = UserInputs.getDefaultInputs();
		InvestmentPortfolio ip = new InvestmentPortfolio(0.3);
		
		RetirementAnalyzer fp = new RetirementAnalyzer(ui.getPrincipal(), ui.getYearlyDeposits(), ui.getTargetRetirement(),
				ui.getCurrentAge(), ui.getMaxAge(),ui.getTargetRetirementAge(), ui.getInflation(), ip, ui.isRealMoney());
		
		fp.buildMonteCarlo(100000);

		assertEquals(0.68, fp.getProbBrokeAtAge(90), 0.05);

	}
	
	@Test
	void testMaxWithdrawal() {
		UserInputs ui = UserInputs.getDefaultInputs();
		InvestmentPortfolio ip = new InvestmentPortfolio(ui.getEquityPercentage());
		
		RetirementAnalyzer fp = new RetirementAnalyzer(250000, ui.getYearlyDeposits(), ui.getTargetRetirement(),
				ui.getCurrentAge(), ui.getMaxAge(),ui.getTargetRetirementAge(), ui.getInflation(), ip, ui.isRealMoney());
		
		double safe = fp.getMaxSafeWithdrawal(90, 0.05);
		
		RetirementAnalyzer fp2 = new RetirementAnalyzer(250000, ui.getYearlyDeposits(), safe,
				ui.getCurrentAge(), ui.getMaxAge(), ui.getCurrentAge(), ui.getInflation(), ip, true);
		
		assertEquals(0.05, fp2.getProbBrokeAtAge(90), 0.01);
	}

}