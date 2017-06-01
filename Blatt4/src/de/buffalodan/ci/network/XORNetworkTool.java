package de.buffalodan.ci.network;

import java.awt.Color;

import de.buffalodan.ci.network.gui.NetworkFrame;
import de.buffalodan.ci.network.gui.NetworkTool;

public class XORNetworkTool implements NetworkTool {

	private Network network;
	private NetworkFrame networkFrame;

	public XORNetworkTool(Network network) {
		this.network = network;
	}

	@Override
	public void run(int runs, Color plotColor) {
		network.reset();
		network.getInputLayer().getNeurons().get(0).setInput(0);
		network.getInputLayer().getNeurons().get(1).setInput(0);
		network.calculate();
	}

	@Override
	public Network getNetwork() {
		return network;
	}

	@Override
	public void screenShot() {
		
	}

	@Override
	public void start() {
		networkFrame = new NetworkFrame(this);
		networkFrame.setVisible(true);
	}

}
