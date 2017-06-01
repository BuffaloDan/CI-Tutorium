package de.buffalodan.ci.web;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;

import de.buffalodan.ci.network.ActivationFunction;
import de.buffalodan.ci.network.Connection;
import de.buffalodan.ci.network.Layer;
import de.buffalodan.ci.network.Network;
import de.buffalodan.ci.network.Neuron;
import de.buffalodan.ci.network.RBFNeuron;
import de.buffalodan.ci.network.Range;
import de.buffalodan.ci.network.Neuron.Type;
import de.buffalodan.ci.network.gui.PlotPanel;
import de.buffalodan.ci.network.gui.PlotPanel.PlotType;

/**
 * Servlet implementation class RBFWebViewer
 */
@WebServlet("/RBFWebViewer")
public class RBFWebViewer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static long lastRun = 0;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RBFWebViewer() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter pw = response.getWriter();
		if (System.currentTimeMillis()-lastRun < 10000) {
			pw.print("Sorry, nur eine Anfrage alle 10 Sekunden erlaubt, sonst überlastet der Server!");
			return;
		}
		lastRun = System.currentTimeMillis();
		String methodStr = request.getParameter("method");
		if (methodStr == null || methodStr.equals("")) {
			methodStr = "1";
		}
		int method = 0;
		try {
			method = Integer.parseInt(methodStr);
		} catch (NumberFormatException e) {
			method = -1;
		}
		if (method < 1 || method > 4) {
			pw.print("Ungültige Methode " + methodStr + "! (1-4 erlaubt");
			return;
		}
		String rbfsStr = request.getParameter("rbfs");
		if (rbfsStr == null || rbfsStr.equals("")) {
			rbfsStr = "30";
		}
		int rbfs = 0;
		try {
			rbfs = Integer.parseInt(rbfsStr);
		} catch (NumberFormatException e) {
			rbfs = -1;
		}
		if (rbfs < 1 || rbfs > 100) {
			pw.print("Ungültige Anzahl an RBF-Units " + rbfsStr + "! (1-100 erlaubt");
			return;
		}

		pw.print("Methode: " + method);
		pw.print("<br>Anzahl RBF-Units: " + rbfs);

		double[][] dataC1 = new double[2][0];
		double[][] dataC2 = new double[2][0];
		double[][] clusterData = new double[2][rbfs];
		double[] c1x1 = new double[100];
		double[] c1x2 = new double[100];
		double[] c2x1 = new double[100];
		double[] c2x2 = new double[100];

		ArrayList<DoublePoint> points = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			// u=1,...,100
			int u = i + 1;
			c1x1[i] = c1x1(u);
			c1x2[i] = c1x2(u);
			c2x1[i] = c2x1(u);
			c2x2[i] = c2x2(u);
			points.add(new DoublePoint(new double[] { c1x1[i], c1x2[i] }));
			points.add(new DoublePoint(new double[] { c2x1[i], c2x2[i] }));
		}
		// test
		/*
		 * File csvFile = new File("data.csv"); try { PrintStream ps = new
		 * PrintStream(csvFile); for (int i = 0; i < 100; i++) {
		 * ps.printf(Locale.ENGLISH, "%f,%f,1\n", c1x1[i], c1x2[i]); } for (int
		 * i = 0; i < 100; i++) { ps.printf(Locale.ENGLISH, "%f,%f,2\n",
		 * c2x1[i], c2x2[i]); } ps.close(); } catch (FileNotFoundException e) {
		 * // TODO Auto-generated catch block e.printStackTrace(); }
		 */

		dataC1[0] = c1x1;
		dataC1[1] = c1x2;
		dataC2[0] = c2x1;
		dataC2[1] = c2x2;

		// Die Center für die RBF-Units mit K-Means berechnen
		KMeansPlusPlusClusterer<DoublePoint> clusterer = new KMeansPlusPlusClusterer<>(rbfs, 10000);
		List<CentroidCluster<DoublePoint>> results = clusterer.cluster(points);
		int cl = 0;
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
		switch (method) {
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
			pw.print("Error!");
			return;
		}

		Layer hiddenLayer = new Layer(rbfNeurons);
		hiddenLayer.addBias();
		Layer outputLayer = new Layer(1, Type.OUTPUT, ActivationFunction.LINEAR);

		layers.add(inputLayer);
		layers.add(hiddenLayer);
		layers.add(outputLayer);

		Network network = new Network(layers);

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
		PlotPanel pp = new PlotPanel();

		pp.addPlot(new Double[2][0], Color.PINK, PlotType.SQUARE);
		pp.addPlot(new Double[2][0], Color.CYAN, PlotType.SQUARE);
		pp.addPlot(dataC1, Color.RED, PlotType.SQUARE);
		pp.addPlot(dataC2, Color.BLUE, PlotType.DOT);
		pp.addPlot(clusterData, Color.BLACK, PlotType.CROSS);
		pp.setCoordSystemRenderOrder(2);
		pp.setBackgroundColor(Color.WHITE);
		pp.setFixedSize(new Dimension(800, 600));

		int runs = 5001;
		for (int run = 0; run < runs; run++) {
			for (int i = 0; i < 100; i++) {
				network.reset();
				network.getInputLayer().getNeurons().get(0).setInput(c1x1[i]);
				network.getInputLayer().getNeurons().get(1).setInput(c1x2[i]);
				network.calculate();
				// System.out.println(c1x1[i] + "," + c1x2[i] + ": " +
				// network.getSingleOutput());
				// network.backpropagate(1);
				network.backpropagateOutputLayer(new double[] { 1 });
				// double out1 = network.getSingleOutput();
				// double out2 =
				// network.getOutputLayer().getNeurons().get(1).getOutput();
				// System.out.println(out1+" "+out2);

				network.reset();
				network.getInputLayer().getNeurons().get(0).setInput(c2x1[i]);
				network.getInputLayer().getNeurons().get(1).setInput(c2x2[i]);
				network.calculate();
				// System.out.println(c2x1[i] + "," + c2x2[i] + ": " +
				// network.getSingleOutput());
				// network.backpropagate(-1);
				network.backpropagateOutputLayer(new double[] { -1 });
			}
			if (run == runs-1) {
				// Network Output für Klasse 1 und 2
				Double[][] networkC1DataTmp = new Double[2][301 * 301];
				Double[][] networkC2DataTmp = new Double[2][301 * 301];
				Range noRange = new Range(-15, 15);
				int c1 = 0, c2 = 0;
				for (Double x1 : noRange.getIterable(300)) {
					for (Double x2 : noRange.getIterable(300)) {
						network.reset();
						network.getInputLayer().getNeurons().get(0).setInput(x1);
						network.getInputLayer().getNeurons().get(1).setInput(x2);
						network.calculate();
						double out = network.getSingleOutput();
						// double out2 =
						// network.getOutputLayer().getNeurons().get(1).getOutput();
						// System.out.println(out);
						if (out > 0) { // && out2 < 0) {
							networkC1DataTmp[0][c1] = x1;
							networkC1DataTmp[1][c1++] = x2;
						} else if (out < 0) { // && out2 > 0) {
							networkC2DataTmp[0][c2] = x1;
							networkC2DataTmp[1][c2++] = x2;
						}
					}
				}
				// Update Visuals
				Double[][] networkC1Data = new Double[2][c1];
				Double[][] networkC2Data = new Double[2][c2];
				System.arraycopy(networkC1DataTmp[0], 0, networkC1Data[0], 0, c1);
				System.arraycopy(networkC1DataTmp[1], 0, networkC1Data[1], 0, c1);
				System.arraycopy(networkC2DataTmp[0], 0, networkC2Data[0], 0, c2);
				System.arraycopy(networkC2DataTmp[1], 0, networkC2Data[1], 0, c2);
				pp.updatePlotData(networkC1Data, 0, false);
				pp.updatePlotData(networkC2Data, 1, false);

				// Update Centers
				int hiddenSize = network.getLayers().get(1).getNeuronsWithoutBias().size();
				Double[][] centerData = new Double[2][hiddenSize];
				for (int i = 0; i < hiddenSize; i++) {
					Neuron n = network.getLayers().get(1).getNeuronsWithoutBias().get(i);
					centerData[0][i] = n.getProducerConnections().get(0).getWeight();
					centerData[1][i] = n.getProducerConnections().get(1).getWeight();
				}
				pp.updatePlotData(centerData, 4, false);
			}
		}
		BufferedImage bi = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
		pp.renderToImage(bi);
		ImageIO.write(bi, "PNG", new File("/var/tomcat/CI/out.png"));//new File("C:\\Users\\Daniel\\Documents\\Java\\CI\\out.png"));
		
		pw.print("<br>Ausgabe nach 5001 Durchläufen:");
		pw.print("<br><img src=/CIWebViewer/GetImage />");
		pw.print("<br>Rot=Klasse1 Blau=Klasse2 Schwarz=RBFs");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
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

}
