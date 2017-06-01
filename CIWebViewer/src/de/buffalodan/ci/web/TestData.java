package de.buffalodan.ci.web;

import de.buffalodan.ci.network.Network;

public class TestData {

	private double[][] input;
	private double[][] output;

	private TestData(TestData old) {
		this.input = arrayClone(old.input);
		this.output = arrayClone(old.output);
	}

	public double[][] getInput() {
		return input;
	}

	public double[][] getOutput() {
		return output;
	}

	public synchronized TestData copy() {
		return new TestData(this);
	}

	private double[][] arrayClone(double[][] oldArray) {
		double[][] newArray = oldArray.clone();
		for (int i = 0; i < oldArray.length; i++) {
			newArray[i] = oldArray[i].clone();
		}
		return newArray;
	}

	public TestData(double[][] input) {
		this.input = input;
		output = new double[input.length][0];
	}

	public synchronized void collect(Network network) {
		output = new double[input.length][network.getOutputLayer().getNeurons().size()];
		for (int i = 0; i < input.length; i++) {
			network.run(input[i], null);
			output[i] = network.getOutput();
		}
	}

}
