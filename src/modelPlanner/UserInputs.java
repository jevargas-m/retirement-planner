package modelPlanner;
/**
 * Class intended to consolidate user provided inputs
 * @author Team 11
 *
 */
public class UserInputs {
	private int currentAge;
	private int maxAge;
	private double inflation;
	private double yearlyDeposits;
	private boolean realMoney;
	private double targetRetirement;
	private int targetRetirementAge;
	private double equityPercentage;
	private double principal;
		
	/**
	 * 
	 * @param currentAge int, User Age at present day
	 * @param maxAge int, Maximum age to allow the simulation to run, safety margin
	 * @param inflation double, expected annual inflation rate
	 * @param deposits double, money the user plans to deposit every year
	 * @param real boolean, true use today's money
	 * @param withdrawals double, money user needs per year after retirement
	 * @param retirementage int, age in which the user plans to retire
	 * @param equity double, percentage of equity in the portfolio
	 * @param principal double, initial money at present age
	 */
	public UserInputs(int currentAge, int maxAge, double inflation, double deposits, 
			boolean real, double withdrawals, int retirementage, double equity, 
			double principal) {

		this.maxAge = maxAge;
		this.currentAge = currentAge;
		this.inflation = inflation;
		this.yearlyDeposits =deposits;
		this.realMoney = real;
		this.targetRetirement = withdrawals;
		this.targetRetirementAge = retirementage;
		this.equityPercentage = equity;
		this.principal = principal;
	}
	
	public UserInputs() {
	}
	
	/**
	 * Get UserInputs with default values used for testing
	 * @return UserInputs
	 */
	public static UserInputs getDefaultInputs() {
		// Default case # 1 used for testing
		UserInputs defui = new UserInputs();
		defui.setMaxAge(105);
		defui.setCurrentAge(30);
		defui.setInflation(0.03);
		defui.setYearlyDeposits(10000);
		defui.setRealMoney(true);
		defui.setTargetRetirement(25000);
		defui.setTargetRetirementAge(60);
		defui.setEquityPercentage(0.3);
		defui.setPrincipal(100000);
		return defui;
	}
	
	public int getMaxAge() {
		return maxAge;
	}


	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
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
	public boolean isRealMoney() {
		return realMoney;
	}
	public void setRealMoney(boolean realMoney) {
		this.realMoney = realMoney;
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
