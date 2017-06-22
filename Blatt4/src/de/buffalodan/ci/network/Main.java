package de.buffalodan.ci.network;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;

import de.buffalodan.ci.network.gui.PlotFrame;
import de.buffalodan.ci.network.gui.PlotType;

public class Main {

	private static float c1x1(int u) {
		return (float) (2d + Math.sin(0.2d * u + 8d) * Math.sqrt(u + 10d));
	}

	private static float c1x2(int u) {
		return (float) (-1d + Math.cos(0.2d * u + 8d) * Math.sqrt(u + 10d));
	}

	private static float c2x1(int u) {
		return (float) (2d + Math.sin(0.2d * u - 8d) * Math.sqrt(u + 10d));
	}

	private static float c2x2(int u) {
		return (float) (-1d + Math.cos(0.2d * u - 8d) * Math.sqrt(u + 10d));
	}

	/*
	 * Soll eine tolle Formel für ein gutes Sigma sein Liefert auch mit die
	 * besten Ergebnisse
	 */
	private static void sigmaMethod1(ArrayList<RBFNeuron> rbfNeurons, int numRBFs, double[][] clusterData,
			double[][] dataC1, double[][] dataC2) {
		for (int i = 0; i < numRBFs; i++) {
			double sigma = 0;
			// Durchschnittliche Entfernung
			double c1 = clusterData[0][i];
			double c2 = clusterData[1][i];
			for (int j = 0; j < 100; j++) {
				double x1 = dataC1[0][j];
				double x2 = dataC1[1][j];
				double distance = Math.hypot(x1 - c1, x2 - c2);
				sigma += distance;

				x1 = dataC2[0][j];
				x2 = dataC2[1][j];
				distance = Math.hypot(x1 - c1, x2 - c2);
				sigma += distance;
			}
			sigma /= 200;
			sigma = Math.sqrt(sigma);
			rbfNeurons.add(new RBFNeuron(sigma));
		}
	}

	// Ähnlich wie 1, leifert aber ein anderes Ergebnis
	private static void sigmaMethod4(ArrayList<RBFNeuron> rbfNeurons, int numRBFs, double[][] clusterData,
			double[][] dataC1, double[][] dataC2) {
		for (int i = 0; i < numRBFs; i++) {
			double distanceMax = 0;
			// Durchschnittliche Entfernung
			double c1 = clusterData[0][i];
			double c2 = clusterData[1][i];
			for (int j = 0; j < 100; j++) {
				double x1 = dataC1[0][j];
				double x2 = dataC1[1][j];
				double distance = Math.hypot(x1 - c1, x2 - c2);
				distanceMax = Math.max(distanceMax, distance);

				x1 = dataC2[0][j];
				x2 = dataC2[1][j];
				distance = Math.hypot(x1 - c1, x2 - c2);
				distanceMax = Math.max(distanceMax, distance);
			}
			double sigma = distanceMax / Math.sqrt(2 * numRBFs);
			rbfNeurons.add(new RBFNeuron(sigma));
		}
	}

	/*
	 * MaxDistance zwischen den Centers benutzen um sigma zu berechnen --> Alle
	 * RBFs haben das gleiche sigma
	 */
	private static void sigmaMethod2(ArrayList<RBFNeuron> rbfNeurons, int numRBFs, double[][] clusterData) {
		double maxDist = 0;
		for (int i = 0; i < numRBFs; i++) {
			double c1x = clusterData[0][i];
			double c1y = clusterData[1][i];
			for (int j = 0; j < numRBFs; j++) {
				double c2x = clusterData[0][j];
				double c2y = clusterData[1][j];
				maxDist = Math.max(maxDist, Math.hypot(c2x - c1x, c2y - c1y));
			}
		}
		double sigma = maxDist / Math.sqrt(2 * numRBFs);
		for (int i = 0; i < numRBFs; i++) {
			rbfNeurons.add(new RBFNeuron(sigma));
		}
	}

	/*
	 * Random Sigma
	 */
	private static void sigmaMethod3(ArrayList<RBFNeuron> rbfNeurons, int numRBFs) {
		Random r = new Random(System.currentTimeMillis());
		for (int i = 0; i < numRBFs; i++) {
			rbfNeurons.add(new RBFNeuron(r.nextInt(100) / 100d + 0.5));
		}
	}

	private static void visualize(int sigmaMethod, int rbfs) {
		double[][] dataC1 = new double[2][0];
		double[][] dataC2 = new double[2][0];
		double[][] clusterData = new double[2][rbfs];
		double[] c1x1 = new double[100];
		double[] c1x2 = new double[100];
		double[] c2x1 = new double[100];
		double[] c2x2 = new double[100];

		// Die
		ArrayList<DoublePoint> pointsc1 = new ArrayList<>();
		ArrayList<DoublePoint> pointsc2 = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			// u=1,...,100
			int u = i + 1;
			c1x1[i] = c1x1(u);
			c1x2[i] = c1x2(u);
			c2x1[i] = c2x1(u);
			c2x2[i] = c2x2(u);
			pointsc1.add(new DoublePoint(new double[] { c1x1[i], c1x2[i] }));
			pointsc2.add(new DoublePoint(new double[] { c2x1[i], c2x2[i] }));
		}

		dataC1[0] = c1x1;
		dataC1[1] = c1x2;
		dataC2[0] = c2x1;
		dataC2[1] = c2x2;

		// Die Center für die RBF-Units mit K-Means berechnen
		KMeansPlusPlusClusterer<DoublePoint> clusterer = new KMeansPlusPlusClusterer<>(rbfs / 2, 10000);
		List<CentroidCluster<DoublePoint>> results = clusterer.cluster(pointsc1);
		int cl = 0;
		// Random r = new Random(System.currentTimeMillis());
		for (CentroidCluster<DoublePoint> cluster : results) {
			clusterData[0][cl] = cluster.getCenter().getPoint()[0];
			clusterData[1][cl] = cluster.getCenter().getPoint()[1];
			cl++;
			// System.out.println(cluster.getCenter());
		}
		// Die Center für die RBF-Units mit K-Means berechnen
		clusterer = new KMeansPlusPlusClusterer<>(rbfs / 2, 10000);
		results = clusterer.cluster(pointsc2);
		// Random r = new Random(System.currentTimeMillis());
		for (CentroidCluster<DoublePoint> cluster : results) {
			clusterData[0][cl] = cluster.getCenter().getPoint()[0];
			clusterData[1][cl] = cluster.getCenter().getPoint()[1];
			cl++;
			// System.out.println(cluster.getCenter());
		}

		// Create Network
		ArrayList<Layer> layers = new ArrayList<>();

		ArrayList<Neuron> inputNeurons = new ArrayList<>();
		inputNeurons.add(new Neuron(1));
		inputNeurons.add(new Neuron(1));
		Layer inputLayer = new Layer(inputNeurons);

		ArrayList<RBFNeuron> rbfNeurons = new ArrayList<>();

		switch (sigmaMethod) {
		case 1:
			sigmaMethod1(rbfNeurons, rbfs, clusterData, dataC1, dataC2);
			break;
		case 2:
			sigmaMethod2(rbfNeurons, rbfs, clusterData);
			break;
		case 3:
			sigmaMethod3(rbfNeurons, rbfs);
			break;
		case 4:
			sigmaMethod4(rbfNeurons, rbfs, clusterData, dataC1, dataC2);
			break;
		default:
			System.out.println("sigmamethode muss zwischen 1-4 liegen!");
			return;
		}

		Layer hiddenLayer = new Layer(rbfNeurons);
		hiddenLayer.addBias();
		Layer outputLayer = new Layer(1, NeuronType.OUTPUT, ActivationFunction.LINEAR);

		layers.add(inputLayer);
		layers.add(hiddenLayer);
		layers.add(outputLayer);

		Network network = new Network(layers);
		network.setPropagationMode(PropagationMode.OUTPUT);

		// Update RBF "Gewichte"
		for (int i = 0; i < rbfs; i++) {
			RBFNeuron n = rbfNeurons.get(i);
			Connection x1Conn = n.getProducerConnections().get(0);
			Connection x2Conn = n.getProducerConnections().get(1);
			double x1 = clusterData[0][i];
			double x2 = clusterData[1][i];
			x1Conn.setWeight(x1);
			x2Conn.setWeight(x2);
		}

		// PlotFrame initialisieren
		PlotFrame plotFrame = new PlotFrame();
		plotFrame.addPlot(new Double[2][0], Color.PINK, PlotType.SQUARE, "Class1Area", 0);
		plotFrame.addPlot(new Double[2][0], Color.CYAN, PlotType.SQUARE, "Class2Area", 1);
		plotFrame.addPlot(dataC1, Color.RED, PlotType.SQUARE, "Class1", 2);
		plotFrame.addPlot(dataC2, Color.BLUE, PlotType.DOT, "Class2", 3);
		plotFrame.addPlot(clusterData, Color.BLACK, PlotType.CROSS, "Centroids", 4);

		plotFrame.getPlotPanel().setBackgroundColor(Color.WHITE);
		plotFrame.getPlotPanel().setCoordSystemRenderOrder(2);
		plotFrame.setVisible(true);

		RBFNetworkTool tool = new RBFNetworkTool(network);
		tool.init(plotFrame, c1x1, c1x2, c2x1, c2x2);
		tool.start();

		// Die eigentliche logik, die das Netzwerk lernen lässt ist in
		// RBFNetworkTool
	}

	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("usage: Main <sigmamethode(1-4)> <num rbfs>");
			return;
		}
		try {
			int method = Integer.parseInt(args[0]);
			int rbfs = Integer.parseInt(args[1]);
			visualize(method, rbfs);
		} catch (NumberFormatException e) {
			System.out.println("Das ist keine Zahl...");
		}
	}

}
