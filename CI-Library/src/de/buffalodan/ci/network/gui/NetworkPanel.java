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

import de.buffalodan.ci.network.Connection;
import de.buffalodan.ci.network.Network;
import de.buffalodan.ci.network.Layer;
import de.buffalodan.ci.network.Neuron;
import de.buffalodan.ci.network.NeuronType;

@SuppressWarnings("serial")
public class NetworkPanel extends JPanel {

	private Network network;
	private int neuronRadius = 20;
	private boolean showBias = false;
	private HashMap<Neuron, Point> neuronPositions = new HashMap<>();

	private int startNeuron = 0;
	private int shownNeurons = 10;

	private int distanceX = 0;
	private ArrayList<Integer> disancesY = new ArrayList<>();

	private static final DecimalFormat df = new DecimalFormat("0.00");

	public NetworkPanel() {
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

	public boolean isShowBias() {
		return showBias;
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
		if (getSize().getHeight() == 0 || getSize().getWidth() == 0)
			return;
		neuronPositions.clear();
		ArrayList<Layer> layers = network.getLayers();
		distanceX = (getSize().width - 1) / (layers.size());
		for (int i = 0; i < layers.size(); i++) {
			Layer layer = layers.get(i);
			int centerX = distanceX * i + distanceX / 2;
			List<Neuron> neurons = getToDrawNeurons(layer);
			int distanceY = (getSize().height - 1) / (neurons.size());
			disancesY.add(distanceY);
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

	private int getNumNeuronsShown(int layer) {
		ArrayList<Neuron> ns = showBias ? network.getLayers().get(layer).getNeurons()
				: network.getLayers().get(layer).getNeuronsWithoutBias();
		int from = startNeuron > ns.size() - 1 - shownNeurons ? ns.size() - shownNeurons - 1 : startNeuron;
		if (from < 0)
			from = 0;
		int to = startNeuron + shownNeurons > ns.size() ? ns.size() : startNeuron + shownNeurons;
		return to - from;
	}

	private List<Neuron> getToDrawNeurons(Layer l) {
		ArrayList<Neuron> ns = showBias ? l.getNeurons() : l.getNeuronsWithoutBias();
		int from = startNeuron > ns.size() - 1 - shownNeurons ? ns.size() - shownNeurons - 1 : startNeuron;
		if (from < 0)
			from = 0;
		int to = startNeuron + shownNeurons > ns.size() ? ns.size() : startNeuron + shownNeurons;
		return ns.subList(from, to);
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (network == null || neuronPositions.isEmpty())
			return;
		Graphics2D g2d = (Graphics2D) g;

		// Damit die Kreise schön rund werden :D
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Rand zeichnen
		g2d.drawRect(0, 0, getSize().width - 1, getSize().height - 1);

		// Neuronen + Connections zeichnen
		// Erst Connections, dann Neuronen
		for (int l = 0; l < network.getLayers().size(); l++) {
			Layer layer = network.getLayers().get(l);
			for (Neuron neuron : getToDrawNeurons(layer)) {
				drawConnections(g2d, neuron, l);
			}
		}
		for (Layer layer : network.getLayers()) {
			for (Neuron neuron : getToDrawNeurons(layer)) {
				drawNeuron(g2d, neuron);
			}
		}
	}

	private void drawConnections(Graphics2D g2d, Neuron neuron, int layer) {
		if (neuron.getType() == NeuronType.INPUT) {
			// Hat keine Connections nach hinten
			return;
		}
		for (Connection connection : neuron.getProducerConnections()) {
			Point p1 = neuronPositions.get(connection.getProducer());
			Point p2 = neuronPositions.get(connection.getConsumer());
			// Neuron wird nicht gezeichnet
			if (p1 == null || p2 == null)
				continue;
			// Ausnahmen bei Recurrent Networks
			if (p1 == p2) {
				g2d.drawOval(p1.x, p1.y - 50, 50, 50);
				Point pw = new Point(p1.x + 50, p1.y - 50);
				String w = df.format(connection.getWeight());
				g2d.drawString(w, pw.x, pw.y);
			} else if (p1.x == p2.x) {
				Point pUp;
				Point pDown;
				if (p2.y > p1.y) {
					pUp = p1;
					pDown = p2;
				} else {
					pUp = p2;
					pDown = p1;
				}
				int factor = (int) ((distanceX / 7f) * 2) / (getNumNeuronsShown(layer) - 1);
				factor *= (pDown.y - pUp.y) / disancesY.get(layer);
				g2d.drawArc(pUp.x - factor / 2, pUp.y, factor, pDown.y - pUp.y, p2.y > p1.y ? 90 : -90, 180);
				Point pw = new Point(pUp.x + 10, pUp.y + (pDown.y - pUp.y) / 2);
				if (p2.y > p1.y) {
					pw.translate(-factor / 2, 0);
				} else {
					pw.translate(factor / 2, 0);
				}
				String w = df.format(connection.getWeight());
				g2d.drawString(w, pw.x, pw.y);

			} else {
				g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
				Point pw = new Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
				String w = df.format(connection.getWeight());
				g2d.drawString(w, pw.x, pw.y);
			}
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

		String out = df.format(neuron.getOutput());
		int outX = pos.x - g2d.getFontMetrics().stringWidth(out) / 2;
		int outY = pos.y + neuronRadius + g2d.getFontMetrics().getHeight() + 1;
		g2d.drawString(out, outX, outY);
	}
}
