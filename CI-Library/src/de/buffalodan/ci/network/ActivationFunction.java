package de.buffalodan.ci.network;

public interface ActivationFunction {

	public double calculate(double input);
	
	// Ableitung
	public double dcalculate(double input);
	
	// Ein paar standard Funktionen
	
	public static final ActivationFunction FERMI = new ActivationFunction() {
		public double calculate(double input) {
			return 1.0 / (1 + Math.exp(-1.0 * input));
		}

		@Override
		public double dcalculate(double input) {
			double fx = calculate(input);
			return fx * (1 - fx);
		}
	};
	
	public static final ActivationFunction LINEAR = new ActivationFunction() {
		public double calculate(double input) {
			return input;
		}

		@Override
		public double dcalculate(double input) {
			return 1;
		}
	};
	
}
