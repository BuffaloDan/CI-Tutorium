package de.buffalodan.ci.network;

public interface ActivationFunction {

	public double calculate(double input);
	
	// Ableitung
	public double dcalculate(double input);
	
}
