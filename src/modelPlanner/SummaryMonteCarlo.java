package modelPlanner;

import java.util.ArrayList;

/**
 * Auxiliary class, for summary output used on graphs
 * @author Team 11
 *
 */
public class SummaryMonteCarlo {
	private int retirementAge;
	
	private ArrayList<Integer> age;
	private ArrayList<Double> minPrincipal;
	private ArrayList<Double> maxPrincipal;
	private ArrayList<Double> meanPrincipal;
	private ArrayList<Integer> numBroke;
	private ArrayList<Double> probBroke;
	
	public SummaryMonteCarlo(int retiremetAge) {
		age = new ArrayList<Integer>();
		minPrincipal = new ArrayList<Double>();
		maxPrincipal = new ArrayList<Double>();
		meanPrincipal = new ArrayList<Double>();
		numBroke = new ArrayList<Integer>();
		probBroke = new ArrayList<Double>();
		this.retirementAge = retiremetAge;
	}

	public void addRow (int age, double minPrincipal, double maxPrincipal, double meanPrincipal, 
			int numBroke , double probBroke) {
		this.age.add(age);
		this.minPrincipal.add(minPrincipal);
		this.maxPrincipal.add(maxPrincipal);
		this.meanPrincipal.add(meanPrincipal);
		this.numBroke.add(numBroke);
		this.probBroke.add(probBroke);
		
		
	}

	public ArrayList<Integer> getAge() {
		return age;
	}

	public ArrayList<Double> getMinPrincipal() {
		return minPrincipal;
	}

	public ArrayList<Double> getMaxPrincipal() {
		return maxPrincipal;
	}

	public ArrayList<Double> getMeanPrincipal() {
		return meanPrincipal;
	}

	public ArrayList<Integer> getNumBroke() {
		return numBroke;
	}

	public ArrayList<Double> getProbBroke() {
		return probBroke;
	}
	
	public int getNumRows() {
		return age.size();
	}

	public int getRetirementAge() {
		return retirementAge;
	}
	
	public int getCurrentAge() {
		return age.get(0);
	}
	
	public int getMaxAge() {
		return age.get(age.size() - 2); // Return second to last age
	}
	
}
