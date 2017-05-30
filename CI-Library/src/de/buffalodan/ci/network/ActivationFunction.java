package de.buffalodan.ci.network;

public interface ActivationFunction extends Function {

	// Ableitung
	public double dcalculate(double input);

	// Ein paar standard Funktionen

	public static final ActivationFunction FERMI = new ActivationFunction() {
		public double calculate(double input) {
			return 1 / (1 + Math.exp(-input));
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

	public static ActivationFunction createGaussian(final double sigma) {
		return new ActivationFunction() {

			@Override
			public double calculate(double r) {
				// Hier wird ein wenig getrickst, da ich es ausnutze, dass
				// normalerweise aus dem Input die Quadratwurzel gezogen wird
				// Lässt man das weg, kann man einfach r nehmen
				// => -(r*r) -> (-r)
				return Math.exp(-r / (2 * Math.pow(sigma, 2)));
			}

			@Override
			public double dcalculate(double input) {
				// Test
				return 1;
			}
		};
	}

}
