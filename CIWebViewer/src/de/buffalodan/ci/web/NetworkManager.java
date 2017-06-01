package de.buffalodan.ci.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;

import de.buffalodan.ci.network.ActivationFunction;
import de.buffalodan.ci.network.Connection;
import de.buffalodan.ci.network.Layer;
import de.buffalodan.ci.network.Network;
import de.buffalodan.ci.network.Neuron;
import de.buffalodan.ci.network.RBFNeuron;
import de.buffalodan.ci.network.Neuron.Type;
import de.buffalodan.ci.web.NetworkThread.TrainingData;

public class NetworkManager {

	private NetworkThread networkThread;

	private static class Loader {
		static NetworkManager INSTANCE = new NetworkManager();
	}

	private NetworkManager() {
		createData();
		Network network = createRBFNetwork(30, 1);
		networkThread = new NetworkThread(network, trainingDatas, 5001, testData);
	}

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

	public TestData getTestData() {
		// wir geben nur eine Kopie zurück, damit die Daten nicht vom
		// NetworkThread überschrieben werden
		return testData.copy();
	}

	public synchronized boolean start() {
		if (networkThread.isRunning())
			return false;
		new Thread(networkThread).start();
		return true;
	}

	private TestData testData;
	private TrainingData[] trainingDatas;
	private ArrayList<DoublePoint> points;
	private RBFNetworkTrainingData rbfNetworkTrainingData = new RBFNetworkTrainingData();

	private void createData() {
		ArrayList<TrainingData> trainingDatasList = new ArrayList<>();
		rbfNetworkTrainingData.classPoints = new double[2][100][2];

		points = new ArrayList<>();
		double[] c1Expected = new double[] { 1 };
		double[] c2Expected = new double[] { -1 };
		for (int i = 0; i < 100; i++) {
			// u=1,...,100
			int u = i + 1;
			double c1x1 = c1x1(u);
			double c1x2 = c1x2(u);
			double c2x1 = c2x1(u);
			double c2x2 = c2x2(u);
			double[] inputC1 = new double[] { c1x1, c1x2 };
			double[] inputC2 = new double[] { c2x1, c2x2 };

			rbfNetworkTrainingData.classPoints[0][i][0] = c1x1;
			rbfNetworkTrainingData.classPoints[0][i][1] = c1x2;
			rbfNetworkTrainingData.classPoints[1][i][0] = c2x1;
			rbfNetworkTrainingData.classPoints[1][i][1] = c2x2;

			TrainingData c1 = new TrainingData(inputC1, c1Expected);
			TrainingData c2 = new TrainingData(inputC2, c2Expected);
			trainingDatasList.add(c1);
			trainingDatasList.add(c2);
			points.add(new DoublePoint(new double[] { c1x1, c1x2 }));
			points.add(new DoublePoint(new double[] { c2x1, c2x2 }));
		}
		trainingDatas = trainingDatasList.toArray(new TrainingData[0]);

		double input[][] = new double[301 * 301][2];
		for (int i = 0; i <= 300; i++) {
			double x1 = -15 + 0.1 * i;
			for (int j = 0; j <= 300; j++) {
				double x2 = -15 + 0.1 * j;
				input[301 * i + j][0] = x1;
				input[301 * i + j][1] = x2;
			}
		}
		testData = new TestData(input);
	}

	public synchronized RBFNetworkTrainingData getRbfNetworkTrainingData() {
		return rbfNetworkTrainingData;
	}

	public synchronized boolean setRuns(int runs) {
		if (networkThread.isRunning())
			return false;
		networkThread.setRuns(runs);
		return true;
	}

	public synchronized boolean newRBFNetwork(int rbfs, int sigmaMethod) {
		if (networkThread.isRunning())
			return false;
		Network newNetwork = createRBFNetwork(rbfs, sigmaMethod);
		networkThread.setNetwork(newNetwork);
		return true;
	}
	
	public boolean isRunning() {
		return networkThread.isRunning();
	}

	public int getCurrentRun() {
		return networkThread.getCurrentRun();
	}
	
	public int getRuns() {
		return networkThread.getRuns();
	}
	
	private synchronized Network createRBFNetwork(int rbfs, int sigmaMethod) {
		double[][] clusterData = new double[2][rbfs];

		// Die Center für die RBF-Units mit K-Means berechnen
		KMeansPlusPlusClusterer<DoublePoint> clusterer = new KMeansPlusPlusClusterer<>(rbfs);
		List<CentroidCluster<DoublePoint>> results = clusterer.cluster(points);
		rbfNetworkTrainingData.centroids = new double[results.size()][2];
		int cl = 0;
		for (CentroidCluster<DoublePoint> cluster : results) {
			double c1 = cluster.getCenter().getPoint()[0];
			double c2 = cluster.getCenter().getPoint()[1];
			rbfNetworkTrainingData.centroids[cl][0] = c1;
			rbfNetworkTrainingData.centroids[cl][1] = c2;
			clusterData[0][cl] = c1;
			clusterData[1][cl] = c2;
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
			sigmaMethod1(rbfNeurons, rbfs, clusterData);
			break;
		case 2:
			sigmaMethod2(rbfNeurons, rbfs, clusterData);
			break;
		case 3:
			sigmaMethod3(rbfNeurons, rbfs);
			break;
		case 4:
			sigmaMethod4(rbfNeurons, rbfs, clusterData);
			break;
		case 5:
			fixedSigma(1, rbfNeurons, rbfs);
			break;
		}

		Layer hiddenLayer = new Layer(rbfNeurons);
		hiddenLayer.addBias();
		Layer outputLayer = new Layer(1, Type.OUTPUT, ActivationFunction.LINEAR);

		layers.add(inputLayer);
		layers.add(hiddenLayer);
		layers.add(outputLayer);

		Network network = new Network(layers);
		network.setLearningRate(0.01d);
		network.setPropagationMode(Network.PropagationMode.OUTPUT);

		// Update RBF "Gewichte"
		for (int i = 0; i < rbfs; i++) {
			RBFNeuron n = rbfNeurons.get(i);
			Connection x1Conn = n.getProducerConnections().get(0);
			Connection x2Conn = n.getProducerConnections().get(1);
			double x1 = results.get(i).getCenter().getPoint()[0];
			double x2 = results.get(i).getCenter().getPoint()[1];
			x1Conn.setWeight(x1);
			x2Conn.setWeight(x2);
		}

		return network;
	}

	/*
	 * Soll eine tolle Formel für ein gutes Sigma sein Liefert auch mit die
	 * besten Ergebnisse
	 */
	private void sigmaMethod1(ArrayList<RBFNeuron> rbfNeurons, int numRBFs, double[][] clusterData) {
		for (int i = 0; i < numRBFs; i++) {
			double sigma = 0;
			// Durchschnittliche Entfernung
			double c1 = clusterData[0][i];
			double c2 = clusterData[1][i];
			for (int j = 0; j < trainingDatas.length; j++) {
				double[] data = trainingDatas[j].input;
				double x1 = data[0];
				double x2 = data[1];
				double distance = Math.hypot(x1 - c1, x2 - c2);
				sigma += distance;
			}
			sigma /= trainingDatas.length;
			sigma = Math.sqrt(sigma);
			rbfNeurons.add(new RBFNeuron(sigma));
		}
	}
	
	private void fixedSigma(double sigma, ArrayList<RBFNeuron> rbfNeurons, int numRBFs) {
		for (int i = 0; i < numRBFs; i++) {
			rbfNeurons.add(new RBFNeuron(sigma));
		}
	}

	private void sigmaMethod4(ArrayList<RBFNeuron> rbfNeurons, int numRBFs, double[][] clusterData) {
		for (int i = 0; i < numRBFs; i++) {
			double distanceMax = 0;
			// Durchschnittliche Entfernung
			double c1 = clusterData[0][i];
			double c2 = clusterData[1][i];
			for (int j = 0; j < 100; j++) {
				double[] data = trainingDatas[j].input;
				double x1 = data[0];
				double x2 = data[1];
				double distance = Math.hypot(x1 - c1, x2 - c2);
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
	private void sigmaMethod2(ArrayList<RBFNeuron> rbfNeurons, int numRBFs, double[][] clusterData) {
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
	private void sigmaMethod3(ArrayList<RBFNeuron> rbfNeurons, int numRBFs) {
		Random r = new Random(System.currentTimeMillis());
		for (int i = 0; i < numRBFs; i++) {
			rbfNeurons.add(new RBFNeuron(r.nextInt(100) / 100d + 0.5));
		}
	}

	public static NetworkManager getInstance() {
		return Loader.INSTANCE;
	}
}
