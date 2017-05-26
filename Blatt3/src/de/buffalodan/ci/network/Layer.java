import java.util.ArrayList;

public class Layer {

	private ArrayList<Neuron> neurons;
	private double bias;
	//dein bias hat so keinerlei funktion, da es von der backpropagation ignoriert wird
	//egtl input neuron mit input konst 1, was ebenfalls mit allen anderen in der
	//nächsten schicht verbunden ist

	public Layer(double input) {
		this.neurons = new ArrayList<>();
		Neuron n = new Neuron(input);
		this.neurons.add(n);
		this.bias = 0;
	}

	public Layer(int neurons, Neuron.Type type, ActivationFunction activationFunction, double bias) {
		this.neurons = new ArrayList<>();
		for (int i = 0; i < neurons; i++) {
			Neuron n = new Neuron(type, activationFunction);
			this.neurons.add(n);
		}
		this.bias = bias;
	}

	public Layer(ArrayList<Neuron> neurons, double bias) {
		this.neurons = neurons;
		this.bias = bias;
	}

	public ArrayList<Neuron> getNeurons() {
		return neurons;
	}

	public void pullAndProduce() {
		for (Neuron neuron : neurons) {
			neuron.pull();
			neuron.consume(bias);
			neuron.produce();
		}
	}

	public void reset() {
		for (Neuron neuron : neurons) {
			neuron.setInput(0);
			neuron.setOutput(0);
		}
	}

}
