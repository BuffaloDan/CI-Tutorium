import java.util.ArrayList;
import java.util.Random;

public class FFNetwork {

	private ArrayList<Layer> layers;

	private static final double LEARNING_RATE = 0.02; // viel zu gross

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
			Neuron consumer = connection.getConsumer();
			double consumerOut = consumer.getOutput();
			double producerOut = connection.getProducer().getOutput();
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
			Neuron consumer = connection.getConsumer();
			double consumerOut = consumer.getOutput();
			double producerOut = connection.getProducer().getOutput();
			double dconsumer = consumer.getActivationFunction().dcalculate(consumer.getInput());
			double deltaOut = (consumerOut-expected) * dconsumer;
			double dw = deltaOut * producerOut;
			connection.setNewWeight(connection.getWeight() - LEARNING_RATE * dw);
		}
		// Okay, das ist jetzt was hässlich geworden :(
		for (Neuron nh : layers.get(1).getNeurons()) {
			// Connection zum Input
			Connection c2 = nh.getProducerConnections().get(0);
			Neuron consumer = c2.getConsumer();
			double producerOut = c2.getProducer().getOutput();
			double dconsumer = consumer.getActivationFunction().dcalculate(consumer.getInput());

			// connection zum output
			Connection c = nh.getConsumerConnections().get(0);
			Neuron oconsumer = c.getConsumer();
			double oconsumerOut = consumer.getOutput();
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

	/*private double[] testWeights = { -0.5, 0.3, 0.2, -0.3, 0.3, -0.1, 0.2, 0.2, -0.4, -0.4, -0.1, 0.4, 0.3, 0.4, -0.2,
			0.5, 0.1, -0.2, 0.4, -0.4 };*/

	private void buildConnections() {
		Random r = new Random(System.currentTimeMillis());
		for (int i = 0; i < layers.size() - 1; i++) {
			Layer layer = layers.get(i);
			Layer next = layers.get(i + 1);
			for (Neuron n : layer.getNeurons()) {
				for (Neuron n2 : next.getNeurons()) {
					double weight = r.nextDouble() - 0.5; //testWeights[j];
					Connection connection = new Connection(n, n2, weight);
					n.addConsumerConnection(connection);
					n2.addProducerConnection(connection);
				}
			}
		}
		System.out.println();
	}

	public void reset() {
		for (Layer layer : layers) {
			layer.reset();
		}
	}

}
