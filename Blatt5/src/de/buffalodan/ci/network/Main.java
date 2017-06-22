package de.buffalodan.ci.network;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import de.buffalodan.ci.network.gui.PlotFrame;
import de.buffalodan.ci.network.gui.PlotPanel.PlotType;

public class Main {

	// 0 = Linear
	// 1 = Exponential
	private final static int MODE = 1;

	private final static double LEARN_RATE = 0.008d;
	private final static int ITERATIONS = 2000;

	private static double tau0 = 0.01d;
	private static double tau = ITERATIONS / tau0;

	private static double expDecay(int t) {
		return tau0 * Math.exp(-t / tau);
	}

	private static double expDecayLR(int t) {
		return LEARN_RATE * Math.exp(-t / tau);
	}

	private static double expReachFunction(double distSquared, int t) {
		return Math.exp(-distSquared / (2 * Math.pow(sigma(t, ITERATIONS), 2)));
	}
	
	public static double sigma(int currentIteration, int finalIteration){
		double start = 0.9;
		double end = 0.1;
		return start*Math.pow((end/start),((double)currentIteration/finalIteration));
	}

	public static int getWinner(Point2d p, ArrayList<Point2d> units) {
		Point2d winner = null;
		double dist = 0;
		int index = 0;
		int i = 0;
		for (Point2d unit : units) {
			if (winner == null) {
				index = i;
				winner = unit;
				dist = winner.getDistanceSquared(p);
			} else {
				if (unit.getDistance(p) < dist) {
					index = i;
					winner = unit;
					dist = winner.getDistanceSquared(p);
				}
			}
			i++;
		}
		return index;
	}

	private static double xi(double u, Random r) {
		return 2 * (3 + Math.sqrt(u)) * Math.sin(u) + r.nextGaussian() * (0.1d * u);
	}

	private static double yi(double u, Random r) {
		return 3 * (3 + Math.sqrt(u)) * Math.cos(u) + r.nextGaussian() * (0.15d * u);
	}

	public static void main(String[] args) {
		ArrayList<Point2d> points = new ArrayList<Point2d>(1001);
		ArrayList<Point2d> pointsTestdata = new ArrayList<>();
		Random r = new Random();
		ArrayList<Integer> randomList = new ArrayList<>();
		for (int i = 0; i <= 1000; i++) {
			randomList.add(i);
			double u = 0.02d * i;
			double x = xi(u, r);
			double y = yi(u, r);
			Point2d p = new Point2d(x, y);
			points.add(p);
			if (i % 100 == 0) {
				pointsTestdata.add(p);
			}
		}
		Collections.shuffle(randomList);
		ArrayList<Point2d> units = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			units.add(new Point2d(points.get(randomList.get(i))));
		}

		ArrayList<Point2d> winners = new ArrayList<>();
		for (Point2d p : pointsTestdata) {
			Point2d winner = units.get(getWinner(p, units));
			winners.add(winner);
		}

		PlotFrame pf = new PlotFrame();
		pf.addPlot(points, Color.BLACK, PlotType.DOT, "data", 0);
		pf.getPlotPanel().sizeOverride.put(0, 2);
		pf.addPlot(pointsTestdata, Color.BLUE, PlotType.SQUARE, "testdata", 1);
		pf.addPlot(units, Color.MAGENTA, PlotType.SQUARE, "SOMLocs", 2);
		pf.getPlotPanel().sizeOverride.put(2, 2);
		pf.addPlot(winners, Color.RED, PlotType.SQUARE, "SOMLocs", 3);
		pf.addPlot(units, Color.GRAY, PlotType.LINE, "SOMConnections", 4);

		pf.setVisible(true);

		// Training
		for (int i = 0; i < ITERATIONS; i++) {
			// Random Points
			Collections.shuffle(randomList);
			for (int j = 0; j < points.size(); j++) {
				Point2d p = points.get(j);
				int winner = getWinner(p, units);
				int u = 0;
				for (Point2d unit : units) {
					Point2d delta = p.subNew(unit);
					double d = LEARN_RATE * expReachFunction(Math.pow(u - winner, 2), i);
					unit.x += d * delta.x;
					unit.y += d * delta.y;
					u++;
				}
			}
			if (i % 1 == 0) {
				pf.setRuns(i);
				pf.repaint();
			}
		}
	}
}