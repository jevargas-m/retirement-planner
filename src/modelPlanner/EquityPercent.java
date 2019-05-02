package modelPlanner;

/**
 * 
 * @author Team 11
 * takes in the risk score generated based on user's answers to the risk survey, and returns an equity percent. 
 */
public class EquityPercent {	

	public double getEquityPercent(int riskScore) {
		if (riskScore <= 4 && riskScore >= 1) {
			return 0.2; 
		} else if  (riskScore <= 8 && riskScore >= 5) {
			return 0.4; 
		} else if  (riskScore <= 12 && riskScore >= 9) {
			return 0.6; 
		} else {
			return 0.8;
		}
	}
}
