import java.util.ArrayList;

/*import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.DefaultXYDataset;*/
// das scheint nicht so ganz standard zu sein...

public class Main {

	public static void main(String[] args) {
		int sampleRate = 1000; //sollten 1001 werte sein

		double[] xWerte = new double[sampleRate + 1];
		double[] yWerte = new double[sampleRate + 1];

		ActivationFunction fermi = new ActivationFunction() {
			public double calculate(double input) {
				return 1.0 / (1 + Math.exp(-1.0 * input));
			}

			@Override
			public double dcalculate(double input) {
				double fx = calculate(input);
				return fx * (1 - fx);
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
		Layer hiddenLayer = new Layer(10, Neuron.Type.HIDDEN, fermi, 1);
		Layer outputLayer = new Layer(1, Neuron.Type.OUTPUT, linear, 0.4);
		layers.add(inputLayer);
		layers.add(hiddenLayer);
		layers.add(outputLayer);

		FFNetwork network = new FFNetwork(layers);

		int runs = 1000;
		double[][] yWerteNetwork = new double[runs][sampleRate + 1];
		double[] errors = new double[sampleRate + 1];
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
				//das hier soll vermutlich die möglichkeit sein, zwischen 2 und 3 zu wechseln?
				double error = network.calculateError(yWerte[i]);
				errors[i] = error;
				errorTotal += error;
				network.reset();
			}
			System.out.println("Mittlerer quadratischer Fehler:" + (errorTotal / sampleRate + 1));
		}
/*
		DefaultXYDataset dataset = createSimpleXYDataset("funtion", xWerte, yWerte);
		for (int i = 0; i < runs; i += 20) {
			addToDataset(dataset, "network" + i, xWerte, yWerteNetwork[i]);
		}
		addToDataset(dataset, "error", xWerte, errors);
		JFreeChart chart = ChartFactory.createXYLineChart("Plot", "X", "Y", dataset);
		ChartFrame frame = new ChartFrame("Plotter", chart);
		frame.setVisible(true);
		frame.setSize(800, 600);
		*/
	}

	/*
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
	*/

}
