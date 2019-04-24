/**
 * Auxiliary class with amortization table data, each object is a row in the
 * amortization table 
 * immutable object only getters
 * @author Team 11
 *
 */
public class AmotizationTableRow {
	private int age;
	private double realRate;
	private double inflation;
	private double principal;
	private double pmt;

	/**
	 * A row in the amortization table
	 * @param age user age
	 * @param r real returns
	 * @param f inflation
	 * @param p principal at the beginning of period
	 * @param pmt deposit or withdrawal at the end of period
	 */
	public AmotizationTableRow(int age, double r, double f, double p, double pmt) {
		this.age = age;
		this.realRate = r;
		this.inflation = f;
		this.principal = p;
		this.pmt = pmt;
	}
	
	public int getAge() {
		return age;
	}

	public double getRealRate() {
		return realRate;
	}
	public double getInflation() {
		return inflation;
	}
	public double getPrincipal() {
		return principal;
	}
	public double getPmt() {
		return pmt;
	}
	
	
}
