package de.buffalodan.ci.network.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;

import de.buffalodan.ci.network.ActivationFunction;
import de.buffalodan.ci.network.Connection;
import de.buffalodan.ci.network.Network;
import de.buffalodan.ci.network.Layer;
import de.buffalodan.ci.network.Neuron;
import de.buffalodan.ci.network.Neuron.Type;

@SuppressWarnings("serial")
public class NetworkPanel extends JPanel {

	private Network network;
	private int neuronRadius = 20;
	private boolean showBias = false;
	private HashMap<Neuron, Point> neuronPositions = new HashMap<>();

	private int startNeuron = 0;
	private int shownNeurons = 10;

	private static final DecimalFormat df = new DecimalFormat("0.00");

	public NetworkPanel() {
		Layer inputLayer = new Layer(1);
		Layer hiddenLayer = new Layer(1, Type.HIDDEN, ActivationFunction.FERMI);
		Layer outputLayer = new Layer(1, Type.OUTPUT, ActivationFunction.LINEAR);

		ArrayList<Layer> layers = new ArrayList<>();
		layers.add(inputLayer);
		layers.add(hiddenLayer);
		layers.add(outputLayer);

		network = new Network(layers);

		addComponentListener(new ResizeListener());
	}

	private class ResizeListener extends ComponentAdapter {
		@Override
		public void componentResized(ComponentEvent e) {
			initPositions();
		}
	}

	public void setShowBias(boolean showBias) {
		this.showBias = showBias;
		initPositions();
	}

	public int getStartNeuron() {
		return startNeuron;
	}

	public void setStartNeuron(int startNeuron) {
		this.startNeuron = startNeuron;
		initPositions();
		repaint();
	}

	private void initPositions() {
		neuronPositions.clear();
		ArrayList<Layer> layers = network.getLayers();
		int distanceX = (getSize().width - 1) / (layers.size());
		for (int i = 0; i < layers.size(); i++) {
			Layer layer = layers.get(i);
			int centerX = distanceX * i + distanceX / 2;
			List<Neuron> neurons = getToDrawNeurons(layer);
			int distanceY = (getSize().height - 1) / (neurons.size());
			for (int j = 0; j < neurons.size(); j++) {
				Neuron neuron = neurons.get(j);
				int centerY = distanceY * j + distanceY / 2;
				Point center = new Point(centerX, centerY);
				neuronPositions.put(neuron, center);
			}
		}
	}

	public void setNetwork(Network network) {
		this.network = network;
		initPositions();
		repaint();
	}

	public Network getNetwork() {
		return network;
	}

	private List<Neuron> getToDrawNeurons(Layer l) {
		ArrayList<Neuron> ns = showBias ? l.getNeurons() : l.getNeuronsWithoutBias();
		int from = startNeuron>ns.size()-1-shownNeurons?ns.size()-shownNeurons-1:startNeuron;
		if (from<0) from = 0;
		int to = startNeuron + shownNeurons > ns.size() ? ns.size() : startNeuron + shownNeurons;
		return ns.subList(from, to);
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		// Damit die Kreise schön rund werden :D
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Rand zeichnen
		g2d.drawRect(0, 0, getSize().width - 1, getSize().height - 1);

		// Neuronen + Connections zeichnen
		// Erst Connections, dann Neuronen
		for (Layer layer : network.getLayers()) {
			for (Neuron neuron : getToDrawNeurons(layer)) {
				drawConnections(g2d, neuron);
			}
		}
		for (Layer layer : network.getLayers()) {
			for (Neuron neuron : getToDrawNeurons(layer)) {
				drawNeuron(g2d, neuron);
			}
		}
	}

	private void drawConnections(Graphics2D g2d, Neuron neuron) {
		if (neuron.getType() == Type.INPUT) {
			// Hat keine Connections nach hinten
			return;
		}
		for (Connection connection : neuron.getProducerConnections()) {
			Point p1 = neuronPositions.get(connection.getProducer());
			Point p2 = neuronPositions.get(connection.getConsumer());
			// Neuron wird nicht gezeichnet
			if (p1 == null || p2 == null)
				continue;
			g2d.drawLine(p1.x, p1.y, p2.x, p2.y);

			Point pw = new Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
			String w = df.format(connection.getWeight());
			g2d.drawString(w, pw.x, pw.y);
		}
	}

	private void drawNeuron(Graphics2D g2d, Neuron neuron) {
		Point pos = neuronPositions.get(neuron);
		// Wir zeichnen einfach über die Conenction drüber, damit wir keine
		// winkel, etc. berechnen müssen um eine Teilgerade zu zeichnen
		g2d.setColor(Color.WHITE);
		g2d.fillOval(pos.x - neuronRadius, pos.y - neuronRadius, neuronRadius * 2, neuronRadius * 2);
		g2d.setColor(Color.BLACK);

		g2d.drawOval(pos.x - neuronRadius, pos.y - neuronRadius, neuronRadius * 2, neuronRadius * 2);

		String in = df.format(neuron.getInput());
		int inX = pos.x - g2d.getFontMetrics().stringWidth(in) - 2;
		int inY = pos.y + neuronRadius + g2d.getFontMetrics().getHeight() + 1;
		g2d.drawString(in, inX, inY);

		String out = df.format(neuron.getOutput());
		int outX = pos.x + 2;
		int outY = inY;
		g2d.drawString(out, outX, outY);
	}
}
