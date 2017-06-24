package de.buffalodan.ci.network;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import de.buffalodan.ci.network.gui.PlotFrame;
import de.buffalodan.ci.network.gui.PlotType;

public class Main {

	public static double f(double x) {
		return -4 * Math.cos(x / 3) + Math.sin(15 / Math.abs(0.5 * x + 2) + 1) + 0.2 * x;
	}

	public static void main(String args[]) throws IOException {
		ArrayList<Point2d> points = new ArrayList<>();
		
		for (int i = 0;i<=1000;i++) {
			double x = -10+ 0.02*i;
			points.add(new Point2d(x, f(x)));
		}
		
		PlotFrame pf = new PlotFrame();
		pf.addPlot(points, Color.BLACK, PlotType.LINE, "data", 0);
		pf.setVisible(true);
		BufferedImage bi = new BufferedImage(pf.getPlotPanel().getWidth(), pf.getPlotPanel().getHeight(), BufferedImage.TYPE_INT_RGB);
		pf.getPlotPanel().renderToImage(bi);
		ImageIO.write(bi, "PNG", new File("Plot.png"));
	}

}
