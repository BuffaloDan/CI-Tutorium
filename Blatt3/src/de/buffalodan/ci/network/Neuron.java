import java.util.ArrayList;

public class Neuron {

	private double input = 0;
	private double output = 0;
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
		this.output = input;
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
				// System.out.print(consume+"+");
				consume(consume);
			}
		}
	}

	public void consume(double input) {
		this.input += input;
	}

	public void produce() {
		if (type == Type.INPUT) {
			output = input;
		} else {
			output = activationFunction.calculate(input);
			// System.out.print(output+"+");
			// System.out.println("i:"+input+" o:"+output);
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

	public enum Type {
		INPUT, OUTPUT, HIDDEN;
	}

}
