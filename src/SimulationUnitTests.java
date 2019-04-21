import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SimulationUnitTests {

	@Test
	void testGetAgeBroke1() {
		// Test Case #1
		UserInputs ui = UserInputs.getDefaultInputs();
		InvestmentPortfolio ip = new InvestmentPortfolio(0.05, 0.0000001);
		
		FutureProjection fp = new FutureProjection(ui.getPrincipal(), ui.getYearlyDeposits(), ui.getTargetRetirement(),
				ui.getCurrentAge(), ui.getMaxAge(),ui.getTargetRetirementAge(), ui.getInflation(), ip, ui.isRealMoney());
		
		assertEquals(81, fp.getAgeBroke());
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
		
		FutureProjection fp = new FutureProjection(ui.getPrincipal(), ui.getYearlyDeposits(), ui.getTargetRetirement(),
				ui.getCurrentAge(), ui.getMaxAge(),ui.getTargetRetirementAge(), ui.getInflation(), ip, ui.isRealMoney());
		
		assertEquals(66, fp.getAgeBroke());
	}
	
	@Test
	void testGetAgeBroke3() {
		// Test Case #3
		UserInputs ui = new UserInputs(30, 105, 0.0, 10000, true, 25000, 60, 0.3, 100000);
		InvestmentPortfolio ip = new InvestmentPortfolio(0.06, 0.0000001);
		
		FutureProjection fp = new FutureProjection(ui.getPrincipal(), ui.getYearlyDeposits(), ui.getTargetRetirement(),
				ui.getCurrentAge(), ui.getMaxAge(),ui.getTargetRetirementAge(), ui.getInflation(), ip, ui.isRealMoney());
		
		assertEquals(105, fp.getAgeBroke());
	}
	
	@Test
	void testProjectedData1() {
		// Test Case #1
		UserInputs ui = UserInputs.getDefaultInputs();
		InvestmentPortfolio ip = new InvestmentPortfolio(0.05, 0.0000001);
		
		FutureProjection fp = new FutureProjection(ui.getPrincipal(), ui.getYearlyDeposits(), ui.getTargetRetirement(),
				ui.getCurrentAge(), ui.getMaxAge(),ui.getTargetRetirementAge(), ui.getInflation(), ip, ui.isRealMoney());
		
		assertEquals(255843.00, fp.getProjectedData(70).getPrincipal(), 1.0);
		assertEquals(-25000.00, fp.getProjectedData(70).getPmt(), 1.0);
		assertEquals(0.0194, fp.getProjectedData(70).getRealRate(), 0.0001);
		
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
		
		FutureProjection fp = new FutureProjection(ui.getPrincipal(), ui.getYearlyDeposits(), ui.getTargetRetirement(),
				ui.getCurrentAge(), ui.getMaxAge(),ui.getTargetRetirementAge(), ui.getInflation(), ip, ui.isRealMoney());
		
		SimulationAnalyzer sa = new SimulationAnalyzer(fp.monteCarloSimulation(50000));
				
		assertEquals(0.54, sa.getProbBrokeAtAge(90), 0.02);
	}
}