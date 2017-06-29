package de.buffalodan.ci.network;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import de.buffalodan.ci.network.gui.PlotPanel;
import de.buffalodan.ci.network.gui.PlotType;

public class Main {

	public static Random r = new Random();

	public static double f(double x) {
		return -4 * Math.cos(x / 3) + Math.sin(15 / (Math.abs(0.5 * x + 2) + 1)) + 0.2 * x;
	}

	public static double absoluteMeanError(ArrayList<Point2d> data1, ArrayList<Point2d> data2) {
		if (data1.size() != data2.size())
			throw new RuntimeException("Data size missmatch!");
		double errorSum = 0;
		for (int i = 0; i < data1.size(); i++) {
			errorSum += Math.abs(data1.get(i).y - data2.get(i).y);
		}
		return errorSum / data1.size();
	}

	public static void setRandomPolynomR3(Polynom poly) {
		poly.arguments.clear();
		for (int a = 0; a < 7; a++) {
			// Das sollte Random genug sein
			double rand = -3 + r.nextDouble() * 6;
			poly.arguments.add(rand);
		}
	}

	public static void printArgumentsAndError(ArrayList<Double> arguments, double error) {
		for (int i = 0; i < arguments.size(); i++) {
			if (i != 0)
				System.out.print(", ");
			System.out.print("a" + i + "=" + arguments.get(i));
		}
		System.out.println(" : " + error);
	}

	public static void main(String args[]) throws IOException {
		ArrayList<Point2d> data = new ArrayList<>();
		for (int i = 0; i <= 1000; i++) {
			double x = -10 + 0.02 * i;
			data.add(new Point2d(x, f(x)));
		}

		// A2
		ArrayList<Point2d> testData = new ArrayList<>();
		Polynom g = new Polynom();
		g.arguments.add(-1.0 / 2);
		g.arguments.add(1.0 / 4);
		g.arguments.add(-1.0 / 8);
		g.arguments.add(1.0 / 16);
		g.arguments.add(-1.0 / 32);
		g.arguments.add(1.0 / 64);
		g.arguments.add(-1.0 / 128);
		for (int i = 0; i <= 1000; i++) {
			double x = -10 + 0.02 * i;
			testData.add(new Point2d(x, g.calculate(x)));
		}
		double error = absoluteMeanError(data, testData);
		double A2_error = error;
		printArgumentsAndError(g.arguments, error);

		PlotPanel pp = new PlotPanel();
		pp.setFixedSize(new Dimension(800, 600));
		pp.addPlot(data, Color.BLUE, PlotType.LINE);

		BufferedImage bi = pp.renderToImage();
		ImageIO.write(bi, "PNG", new File("Plot.png"));

		pp.addPlot(testData, Color.RED, PlotType.LINE);
		bi = pp.renderToImage();
		ImageIO.write(bi, "PNG", new File("Plot_A2.png"));

		// A3
		// Ist mir obriger Fehler der aus A2 gemeint?
		// A3 liefert bei mir nicht ein mal ein besseres Ergebnis
		double lowError = Double.MAX_VALUE; // error;
		ArrayList<Point2d> winnerData = new ArrayList<>(testData);
		ArrayList<Double> winnerArguments = new ArrayList<>(g.arguments);
		for (int run = 0; run < 1000; run++) {
			testData.clear();
			setRandomPolynomR3(g);
			for (int i = 0; i <= 1000; i++) {
				double x = -10 + 0.02 * i;
				testData.add(new Point2d(x, g.calculate(x)));
			}
			error = absoluteMeanError(data, testData);
			if (error < lowError) {
				lowError = error;
				winnerData = new ArrayList<>(testData);
				winnerArguments = new ArrayList<>(g.arguments);
			}
		}
		printArgumentsAndError(winnerArguments, lowError);
		if (lowError == A2_error)
			System.out.println("No better solution found!");
		pp.updatePlotData(winnerData, 1, true);
		bi = pp.renderToImage();
		ImageIO.write(bi, "PNG", new File("Plot_A3.png"));
		double A3_error = lowError;

		// A4
		// Auch hier wieder die Frage, ob der Error neu initialisiert werden
		// soll...
		lowError = Double.MAX_VALUE;
		ArrayList<Point2d> totalWinnerData = new ArrayList<>();
		ArrayList<Double> totalWinnerArguments = new ArrayList<>();
		for (int run = 0; run < 100; run++) {
			testData.clear();
			setRandomPolynomR3(g);
			for (int i = 0; i <= 1000; i++) {
				double x = -10 + 0.02 * i;
				testData.add(new Point2d(x, g.calculate(x)));
			}
			error = absoluteMeanError(data, testData);
			for (int run2 = 0; run2 < 1000; run2++) {
				testData.clear();
				Polynom newG = new Polynom();
				newG.arguments = new ArrayList<>(g.arguments);
				for (int a = 0; a < 7; a++) {
					newG.arguments.add(g.arguments.get(a) + (-0.1 + r.nextDouble() * 0.2));
				}
				for (int i = 0; i <= 1000; i++) {
					double x = -10 + 0.02 * i;
					testData.add(new Point2d(x, g.calculate(x)));
				}
				double err = absoluteMeanError(data, testData);
				if (err<error) {
					error = err;
					g = newG;
					winnerData = new ArrayList<>(testData);
					winnerArguments = new ArrayList<>(g.arguments);
				}
			}
			if (error<lowError) {
				lowError = error;
				totalWinnerData = new ArrayList<>(winnerData);
				totalWinnerArguments = new ArrayList<>(winnerArguments);
			}
			if ((run+1)%10==0) System.out.println((run+1));
		}
		printArgumentsAndError(totalWinnerArguments, lowError);
		if (lowError == A3_error)
			System.out.println("No better solution found!");
		pp.updatePlotData(totalWinnerData, 1, true);
		bi = pp.renderToImage();
		ImageIO.write(bi, "PNG", new File("Plot_4.png"));
	}

}
