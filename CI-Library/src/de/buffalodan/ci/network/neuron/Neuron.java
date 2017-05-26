package de.buffalodan.ci.network.neuron;

import java.util.ArrayList;

import de.buffalodan.ci.network.ActivationFunction;
import de.buffalodan.ci.network.Connection;

/*
 * Evtl abstract machen und Klassen InputNeuron, OutputNeuron, etc. erstellen 
 */
public class Neuron {

	private double input = 0;
	private double output = 0;
	private double delta = 0;
	private Type type;
	private ActivationFunction activationFunction;

	/**
	 * All connections from producers (Layer before)
	 */
	private ArrayList<Connection> producerConnections = null;

	/**
	 * All connections to consumers (Layer after)
	 */
	private ArrayList<Connection> consumerConnections = null;

	public Neuron(double input) {
		this(Type.INPUT, null);
		this.input = input;
	}

	public Neuron(Type type, ActivationFunction activationFunction) {
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
		if (type == Type.INPUT) {
			// do nothing
		} else {
			for (Connection c : producerConnections) {
				Neuron producer = c.getProducer();
				double consume = producer.getOutput() * c.getWeight();
				consume(consume);
			}
		}
	}

	public void consume(double input) {
		this.input += input;
	}
	
	public void updateWeights(double learningRate) {
		if (type == Type.INPUT) return;
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
		if (type == Type.INPUT) {
			return;
		} else if (type == Type.OUTPUT) {
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
		if (type == Type.INPUT) {
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

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public void reset() {
		output = 0;
		if (type != Type.INPUT)
			input = 0;
	}

	public enum Type {
		INPUT, OUTPUT, HIDDEN;
	}

}
