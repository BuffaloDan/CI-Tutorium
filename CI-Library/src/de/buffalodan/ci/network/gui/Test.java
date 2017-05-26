package de.buffalodan.ci.network.gui;

import java.util.ArrayList;

import de.buffalodan.ci.network.ActivationFunction;
import de.buffalodan.ci.network.FFNetwork;
import de.buffalodan.ci.network.Function;
import de.buffalodan.ci.network.Layer;
import de.buffalodan.ci.network.Neuron.Type;

public class Test {

	public static void main(String[] args) {
		ArrayList<Layer> layers = new ArrayList<>();
		Layer inputLayer = new Layer(0);
		inputLayer.addBias();
		Layer hiddenLayer = new Layer(10, Type.HIDDEN, ActivationFunction.FERMI);
		hiddenLayer.addBias();
		Layer outputLayer = new Layer(1, Type.OUTPUT, ActivationFunction.LINEAR);
		layers.add(inputLayer);
		layers.add(hiddenLayer);
		layers.add(outputLayer);

		FFNetwork network = new FFNetwork(layers);
		Function refFuntion = new Function() {
			
			@Override
			public double calculate(double x) {
				return -4 * Math.cos(x / 3) + Math.sin(15 / (Math.abs(0.5 * x + 2) + 1)) + 0.2 * x;
			}
		};
		Range range = new Range(-10, 10);

		FFNetworkTool networkTool = new FFNetworkTool(network, refFuntion, range, 1000);
		networkTool.start();
	}

}
