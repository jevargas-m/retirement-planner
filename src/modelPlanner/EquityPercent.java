package modelPlanner;

/**
 * 
 * @author Team 11
 * takes in the risk score generated based on user's answers to the risk survey, and returns an equity percent. 
 */
public class EquityPercent {	

	private final String[][] riskSurveyAnswers = {
			{"Nonexistent", "I sometimes watch CNBC.", "I read the WSJ.", "I'm the Wolf of Wall St."}, 
			{"Save my money! I don't like the volatility.", "Get some income, with low volatility.",
				"Some income, some growth; Some volatility is ok.", "I want to make it rain $$$; I can handle the risk."}, 
			{"Panic and sell!!!","Cancel my vacation, sell a little, and cry.", 
					"Have two shots of Tequila and buy a little.", "Bring it on market!!! I'd wave it in!"}, 
			{"No way! Keep me safe and snug!","Gulp, maybe a little.", "I can take some risk, not too crazy.", 
						"Volatility is my middle name!!!"}
	};
	
	private int numQuestions;
	
	public EquityPercent() {
		numQuestions = riskSurveyAnswers.length;
	}
	
	public double getEquityPercent(String[] userAnswers) {
		int riskScore = getRiskScore(userAnswers);
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
	
	private int getRiskScore(String[] userAnswers) {
		int riskScore = 0;
		for (int i = 0; i < numQuestions; i++) {
			for (int j = 0; j < numQuestions; j++) {
				if (userAnswers[i].equals(riskSurveyAnswers[i][j])) {
					riskScore += j + 1;
				}
			}
		}
		return riskScore;
	}
	
	public String[] getAnswers(int question) {
		return riskSurveyAnswers[question - 1];
	}
}
