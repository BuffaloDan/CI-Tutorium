package de.buffalodan.ci.network;

import java.util.ArrayList;
import java.util.Random;

import de.buffalodan.ci.network.neuron.Neuron;
import de.buffalodan.ci.network.neuron.Neuron.Type;

public class FFNetwork {

	private ArrayList<Layer> layers;
	private Layer inputLayer;
	private Layer outputLayer;

	private double learningRate = 0.03;

	public FFNetwork(ArrayList<Layer> layers) {
		this.layers = layers;
		this.inputLayer = layers.get(0);
		this.outputLayer = layers.get(layers.size()-1);
		buildConnections();
	}

	public void setLearningRate(double learningRate) {
		this.learningRate = learningRate;
	}

	public double getLearningRate() {
		return learningRate;
	}
	
	public double run(double input, double expected) {
		reset();
		setInput(input);
		calculate();
		backpropagate(expected);
		
		return outputLayer.getNeurons().get(0).getOutput();
	}
	
	public void setInput(double input) {
		inputLayer.getNeurons().get(0).setInput(input);
	}

	public double calculateError(double expected) {
		Neuron outN = outputLayer.getNeurons().get(0);
		double tmo = expected - outN.getOutput();
		return (tmo * tmo) / 2;
	}

	// Das ganze nicht so hardgecodet zu machen mach ich ein andermal, dazu
	// fehlt mir jetzt die Muße, auch wenn das system dafür da ist
	public void hardcodeBackpropagateOutput(double expected) {
		Neuron outN = layers.get(2).getNeurons().get(0);
		ArrayList<Connection> cs = outN.getProducerConnections();
		for (Connection connection : cs) {
			Neuron consumer = connection.getConsumer();
			double consumerOut = consumer.getOutput();
			double producerOut = connection.getProducer().getOutput();
			double dconsumer = consumer.getActivationFunction().dcalculate(consumer.getInput());
			double dw = (consumerOut - expected) * dconsumer * producerOut;
			connection.setNewWeight(connection.getWeight() - learningRate * dw);
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

	public ArrayList<Layer> getLayers() {
		return layers;
	}

	public void hardcodeBackpropagateOutputAndHidden(double expected) {
		Neuron outN = layers.get(2).getNeurons().get(0);
		ArrayList<Connection> cs = outN.getProducerConnections();
		for (Connection connection : cs) {
			Neuron consumer = connection.getConsumer();
			double consumerOut = consumer.getOutput();
			double producerOut = connection.getProducer().getOutput();
			double dconsumer = consumer.getActivationFunction().dcalculate(consumer.getInput());
			double deltaOut = (consumerOut - expected) * dconsumer;
			double dw = deltaOut * producerOut;
			connection.setNewWeight(connection.getWeight() - learningRate * dw);
		}
		// Okay, das ist jetzt was hässlich geworden :(
		for (Neuron nh : layers.get(1).getNeurons()) {
			if (nh.getType() == Type.INPUT)
				continue;
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
			double odeltaOut = (oconsumerOut - expected) * odconsumer;

			double dw = dconsumer * odeltaOut * c.getWeight() * producerOut;
			c2.setNewWeight(c2.getWeight() - learningRate * dw);
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
	
	public void backpropagate(double expected) {
		// Berechne die Deltas
		// Die Inputschicht wird dabei natürlich ausgelassen
		for (int i=layers.size()-1;i>0;i--) {
			for (Neuron neuron : layers.get(i).getNeurons()) {
				neuron.calcDelta(expected);
				neuron.updateWeights(learningRate);
			}
		}
		// Da die Deltas gespeichert werden braucht man auch nicht mehr diesen "newWeight"-Quatsch
	}

	public void calculate() {
		for (Layer layer : layers) {
			layer.pullAndProduce();
		}
	}

	/*
	 * private double[] testWeights = { -0.5, 0.3, 0.2, -0.3, 0.3, -0.1, 0.2,
	 * 0.2, -0.4, -0.4, -0.1, 0.4, 0.3, 0.4, -0.2, 0.5, 0.1, -0.2, 0.4, -0.4 };
	 */

	private void buildConnections() {
		Random r = new Random(System.currentTimeMillis());
		for (int i = 0; i < layers.size() - 1; i++) {
			Layer layer = layers.get(i);
			Layer next = layers.get(i + 1);
			for (Neuron n : layer.getNeurons()) {
				for (Neuron n2 : next.getNeurons()) {
					if (n2.getType() == Type.INPUT)
						continue;
					double weight = r.nextDouble() - 0.5; // testWeights[j];
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
