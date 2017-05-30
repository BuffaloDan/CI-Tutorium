package de.buffalodan.ci.network.gui;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import de.buffalodan.ci.network.FFNetwork;
import de.buffalodan.ci.network.Function;
import de.buffalodan.ci.network.gui.PlotPanel.PlotType;

public class FFNetworkTool implements NetworkTool {

	private FFNetwork network;
	private Range range;
	private NetworkFrame networkFrame;
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
		networkFrame = new NetworkFrame(this);
		networkFrame.setVisible(true);
		plotFrame = new PlotFrame();
		plotFrame.addPlot(referenceValues, Color.BLUE, PlotType.LINE, "Function", plotNum);
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
		plotFrame.addPlot(data, plotColor, PlotType.LINE, "Runs: " + runsSum, plotNum);
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

	@Override
	public void screenShot() {
		BufferedImage networkFrameImage = new BufferedImage(networkFrame.getWidth(), networkFrame.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		networkFrame.paint(networkFrameImage.getGraphics()); // alternately use
																// .printAll(..)
		BufferedImage plotFrameImage = new BufferedImage(plotFrame.getWidth(), plotFrame.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		plotFrame.paint(networkFrameImage.getGraphics()); // alternately use
															// .printAll(..)
		BufferedImage combinedImage = new BufferedImage(networkFrameImage.getWidth() + plotFrameImage.getWidth(),
				Math.max(networkFrameImage.getHeight(), plotFrameImage.getHeight()), BufferedImage.TYPE_INT_RGB);
		combinedImage.getGraphics().drawImage(networkFrameImage, 0, 0, null);
		combinedImage.getGraphics().drawImage(plotFrameImage, networkFrameImage.getWidth(), 0, null);
		int screenshotNum = 0;
		File out = new File("SS_" + screenshotNum + ".png");
		while (out.exists()) {
			screenshotNum++;
			out = new File("SS_" + screenshotNum + ".png");
		}
		try {
			ImageIO.write(combinedImage, "PNG", out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
