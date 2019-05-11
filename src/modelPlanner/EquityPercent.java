package modelPlanner;

/**
 * 
 * @author Team 11
 * takes in the risk score generated based on user's answers to the risk survey, and returns an equity percent. 
 */
public class EquityPercent {	

	private final String[][] RISK_SURVEY_ANSWERS = {
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
		numQuestions = RISK_SURVEY_ANSWERS.length;
	}
	
	/**
	 * Recommended equity based on answers to each one of the questions
	 * @param userAnswers Array with answers for each question
	 * @return Recommended equity percent [0,1]
	 */
	public double getEquityPercent(String[] userAnswers) {
		int riskScore = getRiskScore(userAnswers);
		
//		return (riskScore - 4) / 16 * 0.8;
		
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
			for (int j = 0; j < RISK_SURVEY_ANSWERS[i].length; j++) {
				if (userAnswers[i].equals(RISK_SURVEY_ANSWERS[i][j])) {
					riskScore += j + 1;
				}
			}
		}
		return riskScore;
	}
	
	/**
	 * Array with all answers to a particular question
	 * @param question number between 1 and 4
	 * @return
	 */
	public String[] getAnswers(int question) {
		return RISK_SURVEY_ANSWERS[question - 1];
	}
}
