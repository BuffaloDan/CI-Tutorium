package de.buffalodan.ci.network;

import java.util.ArrayList;

public class Polynom implements Function {

	public ArrayList<Double> arguments = new ArrayList<>();

	@Override
	public double calculate(double x) {
		if (arguments.size() < 0)
			throw new RuntimeException("No Arguments");
		double gx = arguments.get(0);
		for (int a = 1; a < arguments.size(); a++) {
			double t1 = arguments.get(a);
			double t2 = Math.pow(x, a);
			gx += t1 * t2;
		}
		return gx;
	}

}
