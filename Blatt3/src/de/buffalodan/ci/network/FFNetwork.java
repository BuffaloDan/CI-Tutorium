package de.buffalodan.ci.network;

import java.util.ArrayList;
import java.util.Random;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.DefaultXYDataset;

import de.buffalodan.ci.network.Neuron.Type;

public class FFNetwork {

	private ArrayList<Layer> layers;

	private static final double LEARNING_RATE = 0.03;

	public FFNetwork(ArrayList<Layer> layers) {
		this.layers = layers;
		buildConnections();
	}

	public double calculateError(double expected) {
		Neuron outN = layers.get(2).getNeurons().get(0);
		double tmo = expected - outN.getOutput();
		return (tmo * tmo) / 2;
	}

	// Das ganze nicht so hardgecodet zu machen mach ich ein andermal, dazu fehlt mir jetzt die Muße, auch wenn das system dafür da ist 
	public void hardcodeBackpropagateOutput(double expected) {
		Neuron outN = layers.get(2).getNeurons().get(0);
		ArrayList<Connection> cs = outN.getProducerConnections();
		for (Connection connection : cs) {
			Neuron consumer = connection.getN2();
			double consumerOut = consumer.getOutput();
			double producerOut = connection.getN1().getOutput();
			double dconsumer = consumer.getActivationFunction().dcalculate(consumer.getInput());
			double dw = (consumerOut-expected) * dconsumer * producerOut;
			connection.setNewWeight(connection.getWeight() - LEARNING_RATE * dw);
		}

		// update AFTER backpropagating
		for (int i = 0; i < layers.size() - 1; i++) {
			Layer layer = layers.get(i);
			for (Neuron n : layer.getNeurons()) {
				cs = n.getConsumerConnections();
				for (Connection connection : cs) {
					connection.update();
				}
			}
		}
	}

	public void hardcodeBackpropagateOutputAndHidden(double expected) {
		
		Neuron outN = layers.get(2).getNeurons().get(0);
		ArrayList<Connection> cs = outN.getProducerConnections();
		for (Connection connection : cs) {
			Neuron consumer = connection.getN2();
			double consumerOut = consumer.getOutput();
			double producerOut = connection.getN1().getOutput();
			double dconsumer = consumer.getActivationFunction().dcalculate(consumer.getInput());
			double deltaOut = (consumerOut-expected) * dconsumer;
			double dw = deltaOut * producerOut;
			connection.setNewWeight(connection.getWeight() - LEARNING_RATE * dw);
		}
		double out = outN.getOutput();
		for (Neuron nh : layers.get(1).getNeurons()) {
			// Connection zum Input
			Connection c2 = nh.getProducerConnections().get(0);
			Neuron consumer = c2.getN2();
			double consumerOut = consumer.getOutput();
			double producerOut = c2.getN1().getOutput();
			double dconsumer = consumer.getActivationFunction().dcalculate(consumer.getInput());
			
			// connection zum output
			Connection c = nh.getConsumerConnections().get(0);
			Neuron oconsumer = c.getN2();
			double oconsumerOut = consumer.getOutput();
			double oproducerOut = c.getN1().getOutput();
			double odconsumer = oconsumer.getActivationFunction().dcalculate(oconsumer.getInput());
			double odeltaOut = (oconsumerOut-expected) * odconsumer;
			
			double dw = dconsumer*odeltaOut*c.getWeight()*producerOut;
			
			//double tmp = (out - expected) * (out * (1 - out)) * c.getWeight();
			//double dw = tmp * (nh.getOutput() * (1 - nh.getOutput())) * c2.getN1().getOutput();
			c2.setNewWeight(c2.getWeight() - LEARNING_RATE * dw);
		}
		// update AFTER backpropagating
		for (int i = 0; i < layers.size() - 1; i++) {
			Layer layer = layers.get(i);
			for (Neuron n : layer.getNeurons()) {
				cs = n.getConsumerConnections();
				for (Connection connection : cs) {
					connection.update();
				}
			}
		}
	}

	public void calculate() {
		for (Layer layer : layers) {
			layer.pullAndProduce();
		}
	}

	private double[] testWeights = { -0.5, 0.3, 0.2, -0.3, 0.3, -0.1, 0.2, 0.2, -0.4, -0.4, -0.1, 0.4, 0.3, 0.4, -0.2,
			0.5, 0.1, -0.2, 0.4, -0.4 };

	private void buildConnections() {
		Random r = new Random(System.currentTimeMillis());
		int j = 0;
		for (int i = 0; i < layers.size() - 1; i++) {
			Layer layer = layers.get(i);
			Layer next = layers.get(i + 1);
			int ni = 0;
			for (Neuron n : layer.getNeurons()) {
				int ni2 = 0;
				for (Neuron n2 : next.getNeurons()) {
					double weight = testWeights[j]; // r.nextDouble() - 0.5;
					Connection connection = new Connection(n, n2, weight);
					n.addConsumerConnection(connection);
					n2.addProducerConnection(connection);

					System.out.print(weight + ",");
					// System.out.println("Added Connection between L" + i + "N"
					// + ni + " and L" + (i + 1) + "N" + ni2
					// + " with weight " + weight);
					ni2++;
					j++;
				}
				ni++;
			}
		}
		System.out.println();
	}

	public void reset() {
		for (Layer layer : layers) {
			layer.reset();
		}
	}

	public static void main(String[] args) {
		int sampleRate = 80;

		double[] xWerte = new double[sampleRate + 1];
		double[] yWerte = new double[sampleRate + 1];

		ActivationFunction fermi = new ActivationFunction() {
			public double calculate(double input) {
				return 1.0 / (1 + Math.exp(-1.0 * input));
			}
			@Override
			public double dcalculate(double input) {
				double fx = calculate(input);
				return fx*(1-fx);
			}
		};

		ActivationFunction linear = new ActivationFunction() {
			public double calculate(double input) {
				return input;
			}
			@Override
			public double dcalculate(double input) {
				return 1;
			}
		};

		ArrayList<Layer> layers = new ArrayList<>();
		Layer inputLayer = new Layer(0);
		Layer hiddenLayer = new Layer(10, Type.HIDDEN, fermi, 1);
		Layer outputLayer = new Layer(1, Type.OUTPUT, linear, 0.4);
		layers.add(inputLayer);
		layers.add(hiddenLayer);
		layers.add(outputLayer);

		FFNetwork network = new FFNetwork(layers);
		
		int runs = 3000;
		double[][] yWerteNetwork = new double[runs][sampleRate + 1];
		double step = 20d / sampleRate;
		for (int j = 0; j < runs; j++) {
			double errorTotal = 0;
			for (int i = 0; i <= sampleRate; i++) {
				double x = i * step - 10;
				xWerte[i] = x;
				yWerte[i] = -4 * Math.cos(x / 3) + Math.sin(15 / (Math.abs(0.5 * x + 2) + 1)) + 0.2 * x;
				// Nur ein Input da
				inputLayer.getNeurons().get(0).setInput(xWerte[i]);
				network.calculate();
				// Auch nur ein Output
				double output = outputLayer.getNeurons().get(0).getOutput();
				// System.out.println("x:"+xWerte[i]+" y:"+yWerte[i]+ "
				// o:"+output);
				yWerteNetwork[j][i] = output;

				//network.hardcodeBackpropagateOutput(yWerte[i]);
				network.hardcodeBackpropagateOutputAndHidden(yWerte[i]);
				errorTotal += network.calculateError(yWerte[i]);
				network.reset();
			}
			System.out.println("Mittlerer quadratischer Fehler:" +
			 (errorTotal / sampleRate+1));
		}

		DefaultXYDataset dataset = createSimpleXYDataset("funtion", xWerte, yWerte);
		for (int i = 0;i<runs;i+=300) {
			addToDataset(dataset, "network"+i, xWerte, yWerteNetwork[i]);
		}
		JFreeChart chart = ChartFactory.createXYLineChart("Plot", "X", "Y", dataset);
		ChartFrame frame = new ChartFrame("Plotter", chart);
		frame.setVisible(true);
		frame.setSize(800, 600);
	}

	public static DefaultXYDataset createSimpleXYDataset(String key, double[] xs, double[] ys) {
		DefaultXYDataset dataset = new DefaultXYDataset();
		double[][] data = new double[2][0];
		data[0] = xs;
		data[1] = ys;
		dataset.addSeries(key, data);
		return dataset;
	}

	public static void addToDataset(DefaultXYDataset dataset, String key, double[] xs, double[] ys) {
		double[][] data = new double[2][0];
		data[0] = xs;
		data[1] = ys;
		dataset.addSeries(key, data);
	}

}
