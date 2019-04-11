
public class UserInputs {
	private int currentAge;
	private double inflation;
	private double monthlyDeposits;
	private boolean adjustDepositsByInflation;
	private double targetRetirement;
	private double targetRetirementAge;
	private double equityPercentage;
	private double principal;
	
	public UserInputs() {
		// Default values only for testing 
		
		this.currentAge = 41;
		this.inflation = 0.02;
		this.monthlyDeposits = 500;
		this.adjustDepositsByInflation = true;
		this.targetRetirement = 3000 * 12;
		this.targetRetirementAge = 60;
		this.equityPercentage = 50;
		this.principal = 100000;
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
	public double getMonthlyDeposits() {
		return monthlyDeposits;
	}
	public void setMonthlyDeposits(double monthlyDeposits) {
		this.monthlyDeposits = monthlyDeposits;
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
	public double getTargetRetirementAge() {
		return targetRetirementAge;
	}
	public void setTargetRetirementAge(double targetRetirementAge) {
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
