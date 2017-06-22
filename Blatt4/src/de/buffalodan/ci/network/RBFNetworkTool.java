package de.buffalodan.ci.network;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.madgag.gif.fmsware.AnimatedGifEncoder;

import de.buffalodan.ci.network.gui.NetworkFrame;
import de.buffalodan.ci.network.gui.NetworkTool;
import de.buffalodan.ci.network.gui.PlotFrame;

public class RBFNetworkTool implements NetworkTool {

	private Network network;
	private long runsAll = 0;
	private double[] c1x1;
	private double[] c1x2;
	private double[] c2x1;
	private double[] c2x2;
	private PlotFrame plotFrame;
	private NetworkFrame networkFrame;

	private static final boolean WITH_GIF = false;

	public RBFNetworkTool(Network network) {
		this.network = network;
	}

	public void init(PlotFrame plotFrame, double[] c1x1, double[] c1x2, double[] c2x1, double[] c2x2) {
		this.c1x1 = c1x1;
		this.c1x2 = c1x2;
		this.c2x1 = c2x1;
		this.c2x2 = c2x2;
		this.plotFrame = plotFrame;
	}

	@Override
	public void run(int runs, Color plotColor) {
		AnimatedGifEncoder gif = new AnimatedGifEncoder();
		if (WITH_GIF) {
			File g = new File("output.gif");
			if (g.exists())
				g.delete();
			gif.start("output.gif");
			gif.setFrameRate(15);
		}
		for (int run = 0; run < runs; run++) {
			for (int i = 0; i < 100; i++) {
				network.run(new double[] { c1x1[i], c1x2[i] }, new double[] { 1 });
				network.run(new double[] { c2x1[i], c2x2[i] }, new double[] { -1 });
			}
			// 100 mal gif frames
			double r100 = runs / 100;
			if (r100 == 0) {
				r100 = runs;
			}
			if (run % r100 == 0 && WITH_GIF) {
				BufferedImage bi = new BufferedImage(plotFrame.getPlotPanel().getWidth(),
						plotFrame.getPlotPanel().getHeight(), BufferedImage.TYPE_INT_RGB);
				plotFrame.getPlotPanel().renderToImage(bi);
				gif.addFrame(bi);
			}
			// 20 mal aktualisieren
			double r20 = runs / 20;
			// Divide by zero verhindern
			if (r20 == 0)
				r20 = runs;
			if (run % r20 == 0) {
				// Network Output für Klasse 1 und 2
				Double[][] networkC1DataTmp = new Double[2][301 * 301];
				Double[][] networkC2DataTmp = new Double[2][301 * 301];
				Range noRange = new Range(-15, 15);
				int c1 = 0, c2 = 0;
				for (Double x1 : noRange.getIterable(300)) {
					for (Double x2 : noRange.getIterable(300)) {
						// Durchlaufen lassen, null weil keine Backpropagation
						network.run(new double[] { x1, x2 }, null);

						// nur ein output, deshalb können wir diese funktion
						// benutzen
						double out = network.getSingleOutput();
						if (out > 0) {
							networkC1DataTmp[0][c1] = x1;
							networkC1DataTmp[1][c1++] = x2;
						} else if (out < 0) {
							networkC2DataTmp[0][c2] = x1;
							networkC2DataTmp[1][c2++] = x2;
						}
					}
				}
				// Update Visuals
				Double[][] networkC1Data = new Double[2][c1];
				Double[][] networkC2Data = new Double[2][c2];
				// Das wird benötigt, da nur networkC1Data im gegensatz zu
				// networkC1DataTmp die richtige länge hat, die wir vorher auch
				// nicht wissen können
				// Kann man auch anders und schneller lösen, klappt aber so auch
				// ganz gut :D
				System.arraycopy(networkC1DataTmp[0], 0, networkC1Data[0], 0, c1);
				System.arraycopy(networkC1DataTmp[1], 0, networkC1Data[1], 0, c1);
				System.arraycopy(networkC2DataTmp[0], 0, networkC2Data[0], 0, c2);
				System.arraycopy(networkC2DataTmp[1], 0, networkC2Data[1], 0, c2);
				plotFrame.getPlotPanel().updatePlotData(networkC1Data, 0, false);
				plotFrame.getPlotPanel().updatePlotData(networkC2Data, 1, false);

				// Update Centers
				int hiddenSize = network.getLayers().get(1).getNeuronsWithoutBias().size();
				Double[][] centerData = new Double[2][hiddenSize];
				for (int i = 0; i < hiddenSize; i++) {
					Neuron n = network.getLayers().get(1).getNeuronsWithoutBias().get(i);
					centerData[0][i] = n.getProducerConnections().get(0).getWeight();
					centerData[1][i] = n.getProducerConnections().get(1).getWeight();
				}
				plotFrame.getPlotPanel().updatePlotData(centerData, 4, false);

				plotFrame.setRuns(runsAll + run);
				plotFrame.repaint();
				networkFrame.repaint();
			}
		}
		if (WITH_GIF)
			gif.finish();
		runsAll += runs;
	}

	@Override
	public Network getNetwork() {
		return network;
	}

	@Override
	public void screenShot() {
		BufferedImage networkFrameImage = new BufferedImage(networkFrame.getWidth(), networkFrame.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		networkFrame.paint(networkFrameImage.getGraphics());
		BufferedImage plotFrameImage = new BufferedImage(plotFrame.getWidth(), plotFrame.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		plotFrame.paint(plotFrameImage.getGraphics());
		try {
			ImageIO.write(plotFrameImage, "PNG", new File("plot.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

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

	@Override
	public void start() {
		networkFrame = new NetworkFrame(this);
		networkFrame.setVisible(true);
	}

}
