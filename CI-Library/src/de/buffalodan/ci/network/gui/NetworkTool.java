package de.buffalodan.ci.network.gui;

import java.awt.Color;

import de.buffalodan.ci.network.Network;

public interface NetworkTool {

	public void run(int runs, Color plotColor);

	public Network getNetwork();
	
	public void screenShot();
	
	public void start();

}
