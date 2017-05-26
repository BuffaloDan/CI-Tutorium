package de.buffalodan.ci.network.gui;

import java.awt.Color;

import de.buffalodan.ci.network.FFNetwork;
import de.buffalodan.ci.network.Function;

public class FFNetworkTool {

	private FFNetwork network;
	private Range range;
	private FFNetworkFrame networkFrame;
	private PlotFrame plotFrame;
	private int plotNum = 0;
	private int runsSum = 0;
	/*
	 * Die Werte der zu trainierenden Funktion referenceValues[0] = X-Werte
	 * referenceValues[1] = Y-Werte
	 */
	private double[][] referenceValues;

	public FFNetworkTool(FFNetwork network, Function referenceFuntion, Range range, int samplerate) {
		this.network = network;
		this.range = range;
		generateReferenceValues(referenceFuntion, samplerate);
	}

	public void start() {
		networkFrame = new FFNetworkFrame(this);
		networkFrame.setVisible(true);
		plotFrame = new PlotFrame();
		plotFrame.addPlot(referenceValues, Color.BLUE, "Function", plotNum);
		plotNum++;
		plotFrame.setVisible(true);
	}

	public void run(int runs, Color plotColor) {
		double[] newYs = new double[referenceValues[0].length];
		for (int run = 0; run < runs; run++) {
			for (int i = 0; i < referenceValues[0].length; i++) {
				// Wir gehen erstmal nur von einem Input aus
				double out = network.run(referenceValues[0][i], referenceValues[1][i]);
				if (run == runs - 1) {
					newYs[i] = out;
				}
			}
		}
		runsSum += runs;
		double[][] data = new double[2][0];
		// Gleiche X-Werte
		data[0] = referenceValues[0];
		data[1] = newYs;

		// Update NetworkFrame and PlotFrame
		networkFrame.repaint();
		plotFrame.addPlot(data, plotColor, "Runs: " + runsSum, plotNum);
		plotNum++;
	}

	private void generateReferenceValues(Function referenceFuntion, int samplerate) {
		referenceValues = new double[2][samplerate + 1];
		int i = 0;
		for (Double x : range.getIterable(samplerate)) {
			referenceValues[0][i] = x;
			referenceValues[1][i] = referenceFuntion.calculate(x);
			i++;
		}
	}

	public FFNetwork getNetwork() {
		return network;
	}
}
