
public class UserInputs {
	private int currentAge;
	private double inflation;
	private double yearlyDeposits;
	private boolean adjustDepositsByInflation;
	private double targetRetirement;
	private int targetRetirementAge;
	private double equityPercentage;
	private double principal;
		
	public UserInputs() {
		// Default values only for testing 
		
		this.currentAge = 30;
		this.inflation = 0.03;
		this.yearlyDeposits =10000;
		this.adjustDepositsByInflation = true;
		this.targetRetirement = 2000 * 12;
		this.targetRetirementAge = 60;
		this.equityPercentage = 50;
		this.principal = 0;
	}
	
	
	public int getCurrentAge() {
		return currentAge;
	}
	public void setCurrentAge(int currentAge) {
		this.currentAge = currentAge;
	}
	public double getInflation() {
		return inflation;
	}
	public void setInflation(double inflation) {
		this.inflation = inflation;
	}
	public double getYearlyDeposits() {
		return yearlyDeposits;
	}
	public void setYearlyDeposits(double yearlyDeposits) {
		this.yearlyDeposits = yearlyDeposits;
	}
	public boolean isAdjustDepositsByInflation() {
		return adjustDepositsByInflation;
	}
	public void setAdjustDepositsByInflation(boolean adjustDepositsByInflation) {
		this.adjustDepositsByInflation = adjustDepositsByInflation;
	}
	public double getTargetRetirement() {
		return targetRetirement;
	}
	public void setTargetRetirement(double targetRetirement) {
		this.targetRetirement = targetRetirement;
	}
	public int getTargetRetirementAge() {
		return targetRetirementAge;
	}
	public void setTargetRetirementAge(int targetRetirementAge) {
		this.targetRetirementAge = targetRetirementAge;
	}
	public double getEquityPercentage() {
		return equityPercentage;
	}
	public void setEquityPercentage(double equityPercentage) {
		this.equityPercentage = equityPercentage;
	}
	public double getPrincipal() {
		return principal;
	}
	public void setPrincipal(double principal) {
		this.principal = principal;
	}
	
	
	
}
