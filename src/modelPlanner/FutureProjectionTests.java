package modelPlanner;


public class FutureProjectionTests {
	// For testing only
	public static void main(String[] args) {
		UserInputs ui = UserInputs.getDefaultInputs();
		InvestmentPortfolio ip;
		try {
			ip = new InvestmentPortfolio(0.3);
			RetirementAnalyzer fp = new RetirementAnalyzer(200000, ui.getYearlyDeposits(), ui.getTargetRetirement(),
					42, ui.getMaxAge(),ui.getTargetRetirementAge(), ui.getInflation(), ip, true);
			
//			fp.printAmortizationTable();
//			System.out.println("Broke at age = " + fp.getAgeBroke());
//			
//			System.out.println("Prob broke at 90 is " + fp.getProbBrokeAtAge(90, 10000));
//			System.out.println("Prob broke at 120 is " + fp.getProbBrokeAtAge(120, 10000));
//			
//			
//			System.out.println("Prob broke at 100 @ 25000 /yr is " + fp.getProbBrokeAtAge(12000,80, 10000));
			
			double safe = fp.getMaxSafeWithdrawal(85, 0.05);
			
			RetirementAnalyzer fp2 = new RetirementAnalyzer(200000, ui.getYearlyDeposits(), safe,
					42, ui.getMaxAge(),ui.getCurrentAge(), ui.getInflation(), ip, true);
			
			
			
			System.out.println("Safe withdrawal is " + safe );
			System.out.println("Prob broke at 85 is " + fp2.getProbBrokeAtAge(85));

			fp2.printAmortizationTable();
			
			fp2.buildMonteCarlo(75000);
			
	
				// Test principal confidence intervals
				String filename = "output.csv";
				fp2.writeMontecarloOutputCSV(filename);
				System.out.println("DONE!: check " + filename);
				
			} catch (Exception e) {
				System.out.println("ERROR: Close the output file first!");
			}
		}
		
		
	}

