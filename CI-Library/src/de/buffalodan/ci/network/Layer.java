package de.buffalodan.ci.network;

import java.util.ArrayList;

import de.buffalodan.ci.network.neuron.Neuron;
import de.buffalodan.ci.network.neuron.Neuron.Type;

public class Layer {

	private ArrayList<Neuron> neurons;
	private Neuron bias;

	public Layer(double input) {
		this.neurons = new ArrayList<>();
		Neuron n = new Neuron(input);
		this.neurons.add(n);
	}

	public Layer(int neurons, Type type, ActivationFunction activationFunction) {
		this.neurons = new ArrayList<>();
		for (int i = 0; i < neurons; i++) {
			Neuron n = new Neuron(type, activationFunction);
			this.neurons.add(n);
		}
	}

	public Layer(ArrayList<Neuron> neurons) {
		this.neurons = neurons;
	}

	public ArrayList<Neuron> getNeurons() {
		return neurons;
	}

	public ArrayList<Neuron> getNeuronsWithoutBias() {
		if (!hasBias())
			return neurons;
		ArrayList<Neuron> neuronsTmp = new ArrayList<>(neurons);
		neuronsTmp.remove(bias);
		return neuronsTmp;
	}

	public void addBias() {
		Neuron bias = new Neuron(1);
		this.bias = bias;
		neurons.add(bias);
	}

	public Neuron getBias() {
		return bias;
	}

	public boolean hasBias() {
		return bias != null;
	}

	public void pullAndProduce() {
		for (Neuron neuron : neurons) {
			neuron.pull();
			neuron.produce();
		}
	}

	/*
	 * Bereitet das Netzwerk für den nächsten Durchlauf vor
	 */
	public void reset() {
		for (Neuron neuron : neurons) {
			neuron.reset();
		}
	}

}
