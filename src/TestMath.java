import java.util.Random;

import org.apache.commons.math3.distribution.*;

public class TestMath {
	public static void main(String[] args) {
		NormalDistribution nd = new NormalDistribution(0.05, 0.02);
		Random r = new Random();
		for (int i = 0; i <= 100; i++) {
			System.out.println(nd.inverseCumulativeProbability(r.nextDouble()));
		}
		
	}
	
	
	
	
	
}
