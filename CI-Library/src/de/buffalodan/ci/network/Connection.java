package de.buffalodan.ci.network;

/**
 * @author Daniel
 *
 */
public class Connection {

	private Neuron producer;
	private Neuron consumer;
	private double weight;

	/**
	 * N1 ist das Neuron, das seinen Output an N2 schickt und nie andersrum!
	 * N1 --> N2
	 * @param producer Producer-Neuron
	 * @param consumer Consumer-Neuron
	 * @param weight 
	 */
	public Connection(Neuron producer, Neuron consumer, double weight) {
		super();
		this.producer = producer;
		this.consumer = consumer;
		this.weight = weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getWeight() {
		return weight;
	}

	public Neuron getProducer() {
		return producer;
	}

	public Neuron getConsumer() {
		return consumer;
	}
}
