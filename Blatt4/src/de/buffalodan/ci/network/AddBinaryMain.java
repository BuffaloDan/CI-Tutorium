package de.buffalodan.ci.network;

import java.util.ArrayList;

import de.buffalodan.ci.network.Neuron.Type;

public class AddBinaryMain {

	public static void main(String[] args) {
		ArrayList<Layer> layers = new ArrayList<>();
		Layer inputLayer = new Layer(2, Type.INPUT, null);
		Layer hiddenLayer = new Layer(3, Type.HIDDEN, ActivationFunction.FERMI);
		Layer outputLayer = new Layer(1, Type.OUTPUT, ActivationFunction.LINEAR);
		layers.add(inputLayer);
		layers.add(hiddenLayer);
		layers.add(outputLayer);
		RecurrentNetwork network = new RecurrentNetwork(layers);
		AddBinaryNetworkTool tool = new AddBinaryNetworkTool(network);
		tool.start();
	}

}
