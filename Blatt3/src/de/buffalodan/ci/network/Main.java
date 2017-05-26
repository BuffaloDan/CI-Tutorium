package de.buffalodan.ci.network;

import java.text.DecimalFormat;
import java.util.ArrayList;
import org.jfree.data.xy.DefaultXYDataset;

import de.buffalodan.ci.network.neuron.Neuron.Type;

public class Main {

	private static DecimalFormat df = new DecimalFormat("0.00000");

	public static void main(String[] args) {
		int sampleRate = 1001;

		double[] xWerte = new double[sampleRate];
		double[] yWerte = new double[sampleRate];

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
		int runs = 6001;
		double[][] yWerteNetwork = new double[4][sampleRate];
		double[] errors = new double[sampleRate];
		double step = 20d / (sampleRate-1);
		for (int run = 0; run < runs; run++) {
			double errorTotal = 0;
			for (int i = 0; i < sampleRate; i++) {
				double x = i * step - 10;
				xWerte[i] = x;
				yWerte[i] = -4 * Math.cos(x / 3) + Math.sin(15 / (Math.abs(0.5 * x + 2) + 1)) + 0.2 * x;
				// Nur ein Input da
				inputLayer.getNeurons().get(0).setInput(xWerte[i]);
				network.calculate();
				// Auch nur ein Output
				double output = outputLayer.getNeurons().get(0).getOutput();
				if (run % 2000 == 0)
					yWerteNetwork[run / 2000][i] = output;

				network.backpropagate(yWerte[i]);
				double error = network.calculateError(yWerte[i]);
				errors[i] = error;
				errorTotal += error;
				network.reset();
			}
			if (run <= 10 || run % 500 == 0) {
				double meanError = errorTotal / sampleRate;
				System.out.println("Finished run " + run + " with error: " + df.format(meanError));
			}
		}
		DefaultXYDataset dataset = createSimpleXYDataset("funtion", xWerte, yWerte);
		for (int i = 0; i < 4; i++) {
			addToDataset(dataset, "network" + i, xWerte, yWerteNetwork[i]);
		}
		// addToDataset(dataset, "error", xWerte, errors);
		JFreeChart chart = ChartFactory.createXYLineChart("Plot", "X", "Y", dataset);
		ChartFrame frame = new ChartFrame("Plotter", chart);
		frame.setVisible(true);
		frame.setSize(800, 600);
	}

	public static DefaultXYDataset createSimpleXYDataset(String key, double[] xs, double[] ys) {
		DefaultXYDataset dataset = new DefaultXYDataset();
		double[][] data = new double[2][0];
		data[0] = xs;
		data[1] = ys;
		dataset.addSeries(key, data);
		return dataset;
	}

	public static void addToDataset(DefaultXYDataset dataset, String key, double[] xs, double[] ys) {
		double[][] data = new double[2][0];
		data[0] = xs;
		data[1] = ys;
		dataset.addSeries(key, data);
	}

}
