package de.buffalodan.ci.network;

public class RBFNeuron extends Neuron {

	private double sigma;

	public RBFNeuron(double sigma) {
		super(Type.HIDDEN, ActivationFunction.createGaussian(sigma));
		this.sigma = sigma;
	}

	@Override
	public void pull() {
		if (type == Type.INPUT) {
			// do nothing
		} else {
			for (Connection c : producerConnections) {
				Neuron producer = c.getProducer();
				// Naja, ist ja eigentlich kein wirkliches "Gewicht", aber vom
				// Prinzip her klappt das so :D
				double consume = Math.pow(producer.getOutput() - c.getWeight(), 2);
				consume(consume);
			}
		}
	}

	@Override
	public void produce() {
		output = activationFunction.calculate(input);
	}

	@Override
	public void updateWeights(double learningRate) {
		for (Connection connection : producerConnections) {
			// Test
			double irgendwas = (connection.getProducer().getOutput() - connection.getWeight()) / Math.pow(sigma, 2);
			double newWeight = connection.getWeight() + learningRate * delta * output * irgendwas;
			connection.setWeight(newWeight);
		}
	}
}
