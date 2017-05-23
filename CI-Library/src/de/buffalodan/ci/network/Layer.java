package de.buffalodan.ci.network;

import java.util.ArrayList;

import de.buffalodan.ci.network.Neuron.Type;

public class Layer {

	private ArrayList<Neuron> neurons;
	
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

	public void pullAndProduce() {
		for (Neuron neuron : neurons) {
			neuron.pull();
			neuron.produce();
		}
	}
	
	public void reset() {
		for (Neuron neuron : neurons) {
			neuron.setInput(0);
			neuron.setOutput(0);
		}
	}

}
