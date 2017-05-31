package de.buffalodan.ci.network;

import java.awt.Color;
import java.util.ArrayList;

import de.buffalodan.ci.network.Neuron.Type;
import de.buffalodan.ci.network.gui.FFNetworkTool;
import de.buffalodan.ci.network.gui.Range;

public class Main {

	public static void main(String[] args) {
		int sampleRate = 1000;

		ArrayList<Layer> layers = new ArrayList<>();
		Layer inputLayer = new Layer(0);
		inputLayer.addBias();
		Layer hiddenLayer = new Layer(10, Type.HIDDEN, ActivationFunction.FERMI);
		hiddenLayer.addBias();
		Layer outputLayer = new Layer(1, Type.OUTPUT, ActivationFunction.LINEAR);
		layers.add(inputLayer);
		layers.add(hiddenLayer);
		layers.add(outputLayer);

		Network network = new Network(layers);
		Function refFuntion = new Function() {

			@Override
			public double calculate(double x) {
				return -4 * Math.cos(x / 3) + Math.sin(15 / (Math.abs(0.5 * x + 2) + 1)) + 0.2 * x;
			}
		};
		Range range = new Range(-10, 10);

		FFNetworkTool networkTool = new FFNetworkTool(network, refFuntion, range, sampleRate);
		networkTool.start();
		networkTool.run(2000, Color.RED);
	}

}
