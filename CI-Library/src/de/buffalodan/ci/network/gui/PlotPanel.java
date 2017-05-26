package de.buffalodan.ci.network.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class PlotPanel extends JPanel {

	private ArrayList<Double[][]> plots;
	private ArrayList<Color> colors;
	private ArrayList<Boolean> render;
	private double minX = -1;
	private double maxX = 1;
	private double minY = -1;
	private double maxY = 1;

	/**
	 * Create the panel.
	 */
	public PlotPanel() {
		plots = new ArrayList<>();
		colors = new ArrayList<>();
		render = new ArrayList<>();
	}

	public void setRender(int index, boolean render) {
		this.render.set(index, render);
	}

	public void addPlot(double[][] data, Color color) {
		Double[][] toAdd = new Double[2][data[0].length];
		for (int i = 0; i < data[0].length; i++) {
			double x = data[0][i];
			if (x < minX)
				minX = Math.floor(x);
			if (x > maxX)
				maxX = Math.ceil(x);
			toAdd[0][i] = new Double(x);
		}
		for (int i = 0; i < data[1].length; i++) {
			double y = data[1][i];
			if (y < minY)
				minY = Math.floor(y);
			if (y > maxY)
				maxY = Math.ceil(y);
			toAdd[1][i] = new Double(y);
		}
		plots.add(toAdd);
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
		double dx = (maxX - minX);
		double dy = (maxY - minY);
		// Durch 0 teilen verhindern
		if (dx == 0)
			dx = 2;
		if (dy == 0)
			dy = 2;
		double stepX = getSize().width / dx;
		double stepY = getSize().height / dy;
		int x0 = (int) (-minX * stepX);
		int y0 = (int) (stepY * maxY);
		g2d.drawLine(0, y0, getSize().width - 1, y0);
		g2d.drawLine(x0, 0, x0, getSize().width - 1);

		// Plot
		int j = 0;
		for (Double[][] data : plots) {
			g2d.setColor(colors.get(j));
			if (render.get(j)) {
				Double[] xValues = data[0];
				Double[] yValues = data[1];
				for (int i = 0; i < xValues.length - 1; i++) {
					int x1 = (int) (xValues[i] * stepX + x0);
					int y1 = (int) (y0 - yValues[i] * stepY);
					int x2 = (int) (xValues[i + 1] * stepX + x0);
					int y2 = (int) (y0 - yValues[i + 1] * stepY);
					g2d.drawLine(x1, y1, x2, y2);
				}
			}
			j++;
		}
		g2d.setColor(Color.BLACK);
	}
}
