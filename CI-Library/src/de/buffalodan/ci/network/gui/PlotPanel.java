package de.buffalodan.ci.network.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class PlotPanel extends JPanel {

	private double[] xValues;
	private ArrayList<Double[]> yValues;
	private ArrayList<Color> colors;
	private ArrayList<Boolean> render;
	private double minY = 0;
	private double maxY = 0;

	/**
	 * Create the panel.
	 */
	public PlotPanel() {
		yValues = new ArrayList<>();
		colors = new ArrayList<>();
		render = new ArrayList<>();

		// Beispiel Data
		Range r = new Range(-10, 10, 1000);
		double[] xValues = new double[1001];
		double[] yValues = new double[1001];
		int i = 0;
		for (double d : r) {
			xValues[i] = d;
			yValues[i] = 0.2 * d + 1;
			i++;
		}
		setXValues(xValues);
		addYValues(yValues, Color.BLUE);
	}

	public void setXValues(double[] xValues) {
		this.xValues = xValues;
	}
	
	public void setRender(int index, boolean render) {
		this.render.set(index, render);
	}

	public void resetY() {
		yValues.clear();
		colors.clear();
		minY = 0;
		maxY = 0;
	}

	public void addYValues(double[] yValues, Color color) {
		Double[] toAdd = new Double[yValues.length];
		int i = 0;
		for (double y : yValues) {
			if (y < minY)
				minY = Math.floor(y);
			if (y > maxY)
				maxY = Math.ceil(y);
			toAdd[i] = new Double(y);
			i++;
		}
		this.yValues.add(toAdd);
		colors.add(color);
		render.add(true);
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		// Fill background
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, getSize().width, getSize().height);
		g2d.setColor(Color.BLACK);

		// Draw CoordSystem
		double stepX = getSize().width / (xValues[xValues.length - 1] - xValues[0]);
		double stepY = getSize().height / (maxY - minY);
		int x0 = (int) (-xValues[0] * stepX);
		int y0 = (int) (stepY * maxY);
		g2d.drawLine(0, y0, getSize().width - 1, y0);
		g2d.drawLine(x0, 0, x0, getSize().width - 1);

		// Plot
		int j = 0;
		for (Double[] ys : yValues) {
			g2d.setColor(colors.get(j));
			if (render.get(j)) {
				for (int i = 0; i < xValues.length - 1; i++) {
					int x1 = (int) (xValues[i] * stepX + x0);
					int y1 = (int) (y0 - ys[i] * stepY);
					int x2 = (int) (xValues[i + 1] * stepX + x0);
					int y2 = (int) (y0 - ys[i + 1] * stepY);
					g2d.drawLine(x1, y1, x2, y2);
				}
			}
			j++;
		}
		g2d.setColor(Color.BLACK);
	}
}
