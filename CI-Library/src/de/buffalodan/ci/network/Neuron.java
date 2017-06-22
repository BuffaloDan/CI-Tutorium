package de.buffalodan.ci.network;

import java.util.ArrayList;

/*
 * Evtl abstract machen und Klassen InputNeuron, OutputNeuron, etc. erstellen 
 */
public class Neuron {

	protected double input = 0;
	protected double output = 0;
	protected double delta = 0;
	protected NeuronType type;
	protected ActivationFunction activationFunction;

	/**
	 * All connections from producers (Layer before)
	 */
	protected ArrayList<Connection> producerConnections = null;

	/**
	 * All connections to consumers (Layer after)
	 */
	protected ArrayList<Connection> consumerConnections = null;

	public Neuron(double input) {
		this(NeuronType.INPUT, null);
		this.input = input;
	}

	public Neuron(NeuronType type, ActivationFunction activationFunction) {
		this.activationFunction = activationFunction;
		this.type = type;
		switch (type) {
		case INPUT:
			consumerConnections = new ArrayList<>();
			break;
		case OUTPUT:
			producerConnections = new ArrayList<>();
			break;
		case HIDDEN:
			consumerConnections = new ArrayList<>();
			producerConnections = new ArrayList<>();
			break;
		}
	}

	public void addProducerConnection(Connection c) {
		producerConnections.add(c);
	}

	public void addConsumerConnection(Connection c) {
		consumerConnections.add(c);
	}

	public void pull() {
		if (type == NeuronType.INPUT) {
			// do nothing
		} else {
			for (Connection c : producerConnections) {
				Neuron producer = c.getProducer();
				double consume = producer.getOutput() * c.getWeight();
				consume(consume);
			}
		}
	}

	protected void consume(double input) {
		this.input += input;
	}
	
	public void updateWeights(double learningRate) {
		if (type == NeuronType.INPUT) return;
		for (Connection connection : producerConnections) {
			double inputFromProducer = connection.getProducer().getOutput();
			double newWeight = connection.getWeight() + learningRate * delta * inputFromProducer;
			connection.setWeight(newWeight);
		}
	}

	/*
	 * Eigentlich brauchen nur die Output Neuronen den erwarteten Wert. Löse ich
	 * hier, indem die anderen den ignorieren
	 */
	public void calcDelta(double expected) {
		double error = 0;
		// Inputneuronen berechnen kein Delta!
		if (type == NeuronType.INPUT) {
			return;
		} else if (type == NeuronType.OUTPUT) {
			error = (expected - output);
		} else {
			for (Connection connection : consumerConnections) {
				error += connection.getWeight() * connection.getConsumer().getDelta();
			}
		}
		delta = error * activationFunction.dcalculate(input);
	}

	public double getDelta() {
		return delta;
	}

	public void produce() {
		if (type == NeuronType.INPUT) {
			output = input;
		} else {
			output = activationFunction.calculate(input);
		}
	}

	public double getInput() {
		return input;
	}

	public void setInput(double input) {
		this.input = input;
	}

	public double getOutput() {
		return output;
	}

	public void setOutput(double output) {
		this.output = output;
	}

	public ActivationFunction getActivationFunction() {
		return activationFunction;
	}

	public ArrayList<Connection> getProducerConnections() {
		return producerConnections;
	}

	public void setProducerConnections(ArrayList<Connection> producerConnections) {
		this.producerConnections = producerConnections;
	}

	public ArrayList<Connection> getConsumerConnections() {
		return consumerConnections;
	}

	public void setConsumerConnections(ArrayList<Connection> consumerConnections) {
		this.consumerConnections = consumerConnections;
	}

	public NeuronType getType() {
		return type;
	}

	public void setType(NeuronType type) {
		this.type = type;
	}

	public void reset() {
		output = 0;
		if (type != NeuronType.INPUT)
			input = 0;
	}

}
