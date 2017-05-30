package de.buffalodan.ci.network.gui;

import java.awt.Color;

import de.buffalodan.ci.network.FFNetwork;

public interface NetworkTool {

	public void run(int runs, Color plotColor);

	public FFNetwork getNetwork();
	
	public void screenShot();

}
