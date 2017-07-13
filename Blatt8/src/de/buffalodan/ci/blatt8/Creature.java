package de.buffalodan.ci.blatt8;

public class Creature implements Comparable<Creature> {

	private double[] params;

	private double fitness = 0;

	public Creature(int numParams) {
		params = new double[numParams];
		for (int i = 0; i < numParams; i++) {
			// Zufäliger Wert
			// Das ist willkürlich gewählt und nur dafür da, damit die nicht bei
			// 0 starten
			params[i] = ES.r.nextDouble() * 2 - 1;
		}
	}

	public double[] getParams() {
		return params;
	}

	/*
	 * Intermediäre Rekombination
	 */
	public Creature(int numParams, Creature... creatures) {
		params = new double[numParams];
		for (int i = 0; i < numParams; i++) {
			double paramMean = 0;
			for (Creature creature : creatures) {
				paramMean += creature.params[i];
			}
			paramMean /= numParams;
			params[i] = paramMean;
		}
	}

	public void mutate(double standardDeviation) {
		for (int i = 0; i < params.length; i++) {
			params[i] += ES.r.nextGaussian() * standardDeviation;
		}
	}

	public void calcFitness(FitnessFunction ff) {
		fitness = ff.calcFitness(this);
	}

	public double getFitness() {
		return fitness;
	}

	@Override
	public int compareTo(Creature o) {
		return Double.compare(o.fitness, fitness);
	}

}
