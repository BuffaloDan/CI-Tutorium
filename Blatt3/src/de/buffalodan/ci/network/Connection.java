package de.buffalodan.ci.network;

/**
 * @author Daniel
 *
 */
public class Connection {

	private Neuron n1;
	private Neuron n2;
	private double weight;
	private double newWeight = 0;

	/**
	 * N1 ist das Neuron, das seinen Output an N2 schickt und nie andersrum!
	 * N1 --> N2
	 * @param n1 Producer-Neuron
	 * @param n2 Consumer-Neuron
	 * @param weight 
	 */
	public Connection(Neuron n1, Neuron n2, double weight) {
		super();
		this.n1 = n1;
		this.n2 = n2;
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

	public Neuron getN1() {
		return n1;
	}

	public Neuron getN2() {
		return n2;
	}
	
	public void setNewWeight(double newWeight) {
		this.newWeight = newWeight;
	}
	
	public void update() {
		//if (newWeight!=weight) System.out.println(weight+"-->"+newWeight);
		weight = newWeight;
	}
}
