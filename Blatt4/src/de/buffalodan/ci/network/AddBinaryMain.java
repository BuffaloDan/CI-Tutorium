package de.buffalodan.ci.network;

import java.util.ArrayList;

public class AddBinaryMain {

	public static void main(String[] args) {
		ArrayList<Layer> layers = new ArrayList<>();
		Layer inputLayer = new Layer(2, NeuronType.INPUT, null);
		Layer hiddenLayer = new Layer(3, NeuronType.HIDDEN, ActivationFunction.FERMI);
		Layer outputLayer = new Layer(1, NeuronType.OUTPUT, ActivationFunction.LINEAR);
		layers.add(inputLayer);
		layers.add(hiddenLayer);
		layers.add(outputLayer);
		RecurrentNetwork network = new RecurrentNetwork(layers);
		AddBinaryNetworkTool tool = new AddBinaryNetworkTool(network);
		tool.start();
		tool.run(30000);
	}

}
