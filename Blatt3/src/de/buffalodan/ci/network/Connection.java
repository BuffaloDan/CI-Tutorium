/**
 * @author Daniel
 *
 */
public class Connection {

	private Neuron producer;
	private Neuron consumer;
	private double weight;
	private double newWeight = 0;

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
		newWeight = weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
		this.newWeight = weight;
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

	public void setNewWeight(double newWeight) {
		this.newWeight = newWeight;
	}

	public void update() {
		//if (newWeight!=weight) System.out.println(weight+"-->"+newWeight);
		weight = newWeight;
	}
}
