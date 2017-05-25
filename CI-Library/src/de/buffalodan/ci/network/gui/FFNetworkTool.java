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

	public FFNetworkTool(FFNetwork network, Function referenceFuntion, Range range) {
		this.network = network;
		this.range = range;
		generateReferenceValues(referenceFuntion);
	}

	public void start() {
		networkFrame = new FFNetworkFrame(this);
		networkFrame.setVisible(true);
		plotFrame = new PlotFrame();
		plotFrame.setXValues(referenceValues[0]);
		plotFrame.addYValues(referenceValues[1], Color.BLUE, "Function", plotNum);
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

		// Update NetworkFrame and PlotFrame
		networkFrame.repaint();
		plotFrame.addYValues(newYs, plotColor, "Runs: " + runsSum, plotNum);
		plotNum++;
	}

	private void generateReferenceValues(Function referenceFuntion) {
		referenceValues = new double[2][range.getSampleRate() + 1];
		int i = 0;
		for (Double x : range) {
			referenceValues[0][i] = x;
			referenceValues[1][i] = referenceFuntion.calculate(x);
			i++;
		}
	}

	public FFNetwork getNetwork() {
		return network;
	}
}
