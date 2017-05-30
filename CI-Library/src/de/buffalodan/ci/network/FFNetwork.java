package de.buffalodan.ci.network;

import java.util.ArrayList;
import java.util.Random;

import de.buffalodan.ci.network.Neuron.Type;

public class FFNetwork {

	private ArrayList<Layer> layers;
	private Layer inputLayer;
	private Layer outputLayer;
	private double lastError = 0;

	private double learningRate = 0.03;

	public FFNetwork(ArrayList<Layer> layers) {
		this.layers = layers;
		this.inputLayer = layers.get(0);
		this.outputLayer = layers.get(layers.size() - 1);
		buildConnections();
	}

	public void setLearningRate(double learningRate) {
		this.learningRate = learningRate;
	}

	public double getLearningRate() {
		return learningRate;
	}

	public double run(double[] inputs, double expected, boolean backpropagateAll) {
		reset();
		for (int i = 0; i < inputs.length; i++) {
			inputLayer.getNeurons().get(i).setInput(inputs[i]);
		}
		calculate();
		lastError = calculateError(expected);
		if (backpropagateAll) {
			backpropagate(expected);
		} else {
			backpropagateOutputLayer(new double[] { expected });
		}

		return getSingleOutput();
	}

	public Layer getInputLayer() {
		return inputLayer;
	}

	public Layer getOutputLayer() {
		return outputLayer;
	}

	public double getSingleOutput() {
		return outputLayer.getNeurons().get(0).getOutput();
	}

	public double run(double input, double expected) {
		return run(new double[] { input }, expected, true);
	}

	public void setInput(double input) {
		inputLayer.getNeurons().get(0).setInput(input);
	}

	public double getLastError() {
		return lastError;
	}

	public double calculateError(double expected) {
		Neuron outN = outputLayer.getNeurons().get(0);
		double tmo = expected - outN.getOutput();
		return (tmo * tmo) / 2;
	}

	public ArrayList<Layer> getLayers() {
		return layers;
	}

	public void backpropagateOutputLayer(double[] expected) {
		// Nur Output Layer
		int i = 0;
		for (Neuron neuron : outputLayer.getNeurons()) {
			neuron.calcDelta(expected[i]);
			neuron.updateWeights(learningRate);
			i++;
		}
	}

	public void backpropagate(double expected) {
		// Berechne die Deltas
		// Die Inputschicht wird dabei natürlich ausgelassen
		for (int i = layers.size() - 1; i > 0; i--) {
			for (Neuron neuron : layers.get(i).getNeurons()) {
				neuron.calcDelta(expected);
				neuron.updateWeights(learningRate);
			}
		}
		// Da die Deltas gespeichert werden braucht man auch nicht mehr diesen
		// "newWeight"-Quatsch
	}

	public void calculate() {
		for (Layer layer : layers) {
			layer.pullAndProduce();
		}
	}

	private void buildConnections() {
		Random r = new Random(System.currentTimeMillis());
		for (int i = 0; i < layers.size() - 1; i++) {
			Layer layer = layers.get(i);
			Layer next = layers.get(i + 1);
			for (Neuron n : layer.getNeurons()) {
				for (Neuron n2 : next.getNeurons()) {
					if (n2.getType() == Type.INPUT)
						continue;
					double weight = r.nextDouble() - 0.5; // testWeights[j];
					Connection connection = new Connection(n, n2, weight);
					n.addConsumerConnection(connection);
					n2.addProducerConnection(connection);
				}
			}
		}
	}

	public void reset() {
		for (Layer layer : layers) {
			layer.reset();
		}
	}

}
