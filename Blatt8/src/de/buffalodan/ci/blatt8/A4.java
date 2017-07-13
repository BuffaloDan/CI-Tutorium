package de.buffalodan.ci.blatt8;

import java.awt.Color;
import java.util.ArrayList;

import de.buffalodan.ci.network.Point2d;
import de.buffalodan.ci.network.gui.PlotFrame;
import de.buffalodan.ci.network.gui.PlotType;

public class A4 {

	public static double f(double x) {
		return 3d * Math.cos(x / 5d) + Math.sin(1d / (2d * Math.abs(x) + 0.25d)) - 1d;
	}

	public static ArrayList<Point2d> creatureToPlot(Creature c) {
		ArrayList<Point2d> points = new ArrayList<>();
		for (int i = 0; i <= 40; i++) {
			double x = 0.5 * i - 10;
			double y = c.getParams()[i];
			points.add(new Point2d(x, y));
		}
		return points;
	}

	public static void main(String[] args) {
		ArrayList<Point2d> points = new ArrayList<>();
		double[] rightData = new double[41];

		for (int i = 0; i <= 1000; i++) {
			double x = 0.02 * i - 10;
			double y = f(x);

			points.add(new Point2d(x, y));
			if (i % 25 == 0) {
				rightData[i / 25] = y;
			}
		}

		FitnessFunction ff = new FitnessFunction() {

			@Override
			public double calcFitness(Creature c) {
				double error = 0;
				for (int i = 0; i <= 40; i++) {
					error += rightData[i] - c.getParams()[i];
				}
				return 1/(error / 41);
			}
		};

		ES es = new ES(60, 120, 41, 3, ff);

		PlotFrame pf = new PlotFrame();
		pf.addPlot(points, Color.BLUE, PlotType.LINE, "f(x)", 0);
		pf.addPlot(creatureToPlot(es.getFittestCreature()), Color.RED, PlotType.LINE, "g(x) - gen 0", 1);
		System.out.println(es.getFittestCreature().getFitness());
		int in = 2;
		int steps = 4;
		int colorOffset = 0;
		int colorStep = 200 / 4;

		int runs = 200;
		for (int i = 0; i < runs; i++) {
			es.nextGeneration();
			if ((i + 1) % (runs / steps) == 0) {
				Creature c= es.getFittestCreature();
				System.out.println(c.getFitness());
				pf.addPlot(creatureToPlot(c), new Color(0, 255 - colorOffset, 0), PlotType.LINE,
						"g(x) - gen " + es.getGeneration(), in++);
				colorOffset += colorStep;
			}
		}
		pf.setVisible(true);
	}

}
