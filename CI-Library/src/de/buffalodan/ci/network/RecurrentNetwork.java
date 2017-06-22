package de.buffalodan.ci.network;

import java.util.ArrayList;

public class RecurrentNetwork extends Network {

	public RecurrentNetwork(ArrayList<Layer> layers) {
		super(layers);
	}
	@Override
	protected void buildConnections() { 
		super.buildConnections();
		// Recurrent Connections
		// Hat nur eine HiddenLayer
		Layer hidden = getLayers().get(1);
		for (Neuron n:hidden.getNeurons()) {
			for (Neuron n2:hidden.getNeurons()) {
				Connection c = new Connection(n, n2, 0);
				n.addConsumerConnection(c);
				n2.addProducerConnection(c);
			}
		}
	}
}
