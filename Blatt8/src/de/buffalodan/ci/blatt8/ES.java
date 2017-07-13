package de.buffalodan.ci.blatt8;

import java.util.Arrays;
import java.util.Random;

public class ES {

	// Damit nicht überall eine Instanz erstellt werden muss, ist aber
	// eigentlich egal
	public static final Random r = new Random();

	private int generation = 0;
	private int numParents;
	private int numChildren;
	private int numParams;
	private int parentsPerChild;
	private double standardDeviation = 1;
	private FitnessFunction ff;
	private Creature[] currentGeneration;

	public int getNumParents() {
		return numParents;
	}

	public int getNumChildren() {
		return numChildren;
	}

	public ES(int numParents, int numChildren, int numParams, int parentsPerChild, FitnessFunction ff) {
		this.numParents = numParents;
		this.numChildren = numChildren;
		this.numParams = numParams;
		this.parentsPerChild = parentsPerChild;
		this.ff = ff;
		currentGeneration = new Creature[numParents];
		for (int i = 0; i < numParents; i++) {
			currentGeneration[i] = new Creature(numParams);
			currentGeneration[i].calcFitness(ff);
		}
		Arrays.sort(currentGeneration);
	}

	public Creature getFittestCreature() {
		// Klappt, weil die Liste immer nach Fitness sortiert ist
		return currentGeneration[0];
	}

	public void nextGeneration() {
		Creature[] nextGeneration = new Creature[numChildren];
		Creature[] bothGenerations = new Creature[numChildren + numParents];
		// Reproduction
		for (int i = 0; i < numChildren; i++) {
			Creature[] parents = new Creature[parentsPerChild];
			for (int j = 0; j < parentsPerChild; j++) {
				parents[j] = currentGeneration[r.nextInt(numParents)];
			}
			nextGeneration[i] = new Creature(numParams, parents);
			bothGenerations[i] = nextGeneration[i];
			// Mutation
			nextGeneration[i].mutate(standardDeviation);
			// Fitness berechnen
			nextGeneration[i].calcFitness(ff);
		}
		for (int i = 0; i < numParents; i++) {
			bothGenerations[i + numChildren] = currentGeneration[i];
		}
		// Selection
		double ps = 0;
		Arrays.sort(bothGenerations);
		for (int i = 0; i < numParents; i++) {
			currentGeneration[i] = bothGenerations[i];
			if (Arrays.asList(nextGeneration).contains(bothGenerations[i])) {
				ps++;
			}
		}
		ps /= numChildren;
		if (ps < 0.2) {
			standardDeviation *= 0.99;
		} else if (ps > 0.2) {
			standardDeviation /= 0.99;
		}
		System.out.println("sd: "+standardDeviation);
		generation++;
	}

	public int getGeneration() {
		return generation;
	}
}
