package modelPlanner;

import java.util.ArrayList;

/**
 * Auxiliary class, for summary output used on graphs
 * @author Team 11
 *
 */
public class SummaryMonteCarlo {
	private int retirementAge;
	private double maxValue;
	private double minValue;
	
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
		this.maxValue = Double.NEGATIVE_INFINITY;
		this.minValue = Double.POSITIVE_INFINITY;
	}

	public void addRow (int age, double minPrincipal, double maxPrincipal, double meanPrincipal, 
			int numBroke , double probBroke) {
		this.age.add(age);
//		if (minPrincipal < 0) minPrincipal = 0;
//		if (meanPrincipal < 0) meanPrincipal = 0;
//		if (maxPrincipal < 0) maxPrincipal = 0;
		
		this.minPrincipal.add(minPrincipal);
		this.maxPrincipal.add(maxPrincipal);
		this.meanPrincipal.add(meanPrincipal);
		this.numBroke.add(numBroke);
		this.probBroke.add(probBroke);
		if (maxPrincipal > maxValue) {
			maxValue = maxPrincipal;
		}
		if (minPrincipal < minValue) {
			minValue = minPrincipal;
		}
		
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

	public double getMaxValue() {
		return maxValue;
	}

	public double getMinValue() {
		return minValue;
	}
	
	
	
	
}
