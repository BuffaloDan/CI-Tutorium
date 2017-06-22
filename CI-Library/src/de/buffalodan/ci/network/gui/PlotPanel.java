package de.buffalodan.ci.network.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;

import de.buffalodan.ci.network.Point2d;

@SuppressWarnings("serial")
public class PlotPanel extends JPanel {

	private Dimension fixedSize = null;

	// Das sollte man mal in eine Plot Class auslagern...
	private ArrayList<ArrayList<Point2d>> plots;
	private ArrayList<PlotType> types;
	private ArrayList<Color> colors;
	private ArrayList<Boolean> render;
	private int coordSystemRenderOrder = 0;

	public HashMap<Integer, Integer> sizeOverride = new HashMap<>();

	private Color backgroundColor;

	private double minX = -1;
	private double maxX = 1;
	private double minY = -1;
	private double maxY = 1;

	private static final int DOT_SIZE = 3;
	private static final int SQUARE_SIZE = 3;

	/**
	 * Create the panel.
	 */
	public PlotPanel() {
		plots = new ArrayList<>();
		colors = new ArrayList<>();
		render = new ArrayList<>();
		types = new ArrayList<>();

		backgroundColor = Color.WHITE;
	}

	public void setRender(int index, boolean render) {
		this.render.set(index, render);
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public void setCoordSystemRenderOrder(int coordSystemRenderOrder) {
		this.coordSystemRenderOrder = coordSystemRenderOrder;
	}

	public void updatePlotData(Double[][] data, int index, boolean updateBounds) {
		ArrayList<Point2d> d = convertData(data);
		plots.set(index, d);
		if (updateBounds)
			updateBounds(d);
		repaint();
	}

	public void updatePlotData(ArrayList<Point2d> data, int index, boolean updateBounds) {
		plots.set(index, data);
		if (updateBounds)
			updateBounds(data);
		repaint();
	}

	public void updatePlotData(double[][] data, int index, boolean updateBounds) {
		updatePlotData(convertData(data), index, updateBounds);
	}

	public ArrayList<Point2d> convertData(double[][] data) {
		ArrayList<Point2d> newData = new ArrayList<>();
		for (int i = 0; i < data[0].length; i++) {
			double x = data[0][i];
			double y = data[1][i];
			newData.add(new Point2d(x, y));
		}
		return newData;
	}

	public ArrayList<Point2d> convertData(Double[][] data) {
		ArrayList<Point2d> newData = new ArrayList<>();
		for (int i = 0; i < data[0].length; i++) {
			double x = data[0][i];
			double y = data[1][i];
			newData.add(new Point2d(x, y));
		}
		return newData;
	}

	private void updateBounds(ArrayList<Point2d> data) {
		for (Point2d p : data) {
			if (p.y < minY)
				minY = Math.floor(p.y);
			if (p.y > maxY)
				maxY = Math.ceil(p.y);
			if (p.x < minX)
				minX = Math.floor(p.x);
			if (p.x > maxX)
				maxX = Math.ceil(p.x);
		}
	}

	public void addPlot(ArrayList<Point2d> data, Color color, PlotType type) {
		updateBounds(data);
		plots.add(data);
		colors.add(color);
		render.add(true);
		types.add(type);
	}

	public void addPlot(Double[][] data, Color color, PlotType type) {
		ArrayList<Point2d> d = convertData(data);
		updateBounds(d);
		plots.add(d);
		colors.add(color);
		render.add(true);
		types.add(type);
	}

	public void addPlot(double[][] data, Color color, PlotType type) {
		ArrayList<Point2d> d = convertData(data);
		updateBounds(d);
		plots.add(d);
		colors.add(color);
		render.add(true);
		types.add(type);
	}

	public void setFixedSize(Dimension fixedSize) {
		this.fixedSize = fixedSize;
	}

	public void renderToImage(BufferedImage bi) {
		paintComponent(bi.getGraphics());
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		Dimension coordSize = fixedSize == null ? getSize() : fixedSize;

		// Damit die Kreise schön rund werden :D
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Fill background
		g2d.setColor(backgroundColor);
		g2d.fillRect(0, 0, coordSize.width, coordSize.height);
		g2d.setColor(Color.BLACK);

		double dx = (maxX - minX);
		double dy = (maxY - minY);
		// Durch 0 teilen verhindern
		if (dx == 0)
			dx = 2;
		if (dy == 0)
			dy = 2;
		double stepX = coordSize.width / dx;
		double stepY = coordSize.height / dy;
		int x0 = (int) (-minX * stepX);
		int y0 = (int) (stepY * maxY);

		if (plots.isEmpty()) {
			// Draw CoordSystem, Keine Plots da
			g2d.drawLine(0, y0, coordSize.width - 1, y0);
			g2d.drawLine(x0, 0, x0, coordSize.width - 1);
		}

		// Plot
		int j = 0;
		for (ArrayList<Point2d> data : plots) {
			if (j == coordSystemRenderOrder) {
				g2d.setColor(Color.BLACK);
				// Draw CoordSystem
				g2d.drawLine(0, y0, coordSize.width - 1, y0);
				g2d.drawLine(x0, 0, x0, coordSize.width - 1);
			}
			if (render.get(j)) {
				int size;
				switch (types.get(j)) {
				case LINE:
					g2d.setColor(colors.get(j));
					for (int i = 0; i < data.size() - 1; i++) {
						Point2d p1 = data.get(i);
						Point2d p2 = data.get(i + 1);
						int x1 = (int) (p1.x * stepX + x0);
						int y1 = (int) (y0 - p1.y * stepY);
						int x2 = (int) (p2.x * stepX + x0);
						int y2 = (int) (y0 - p2.y * stepY);
						g2d.drawLine(x1, y1, x2, y2);
					}
					break;
				case DOT:
					size = DOT_SIZE;
					if (sizeOverride.get(j) != null) {
						size = sizeOverride.get(j);
					}
					for (Point2d p : data) {
						int x = (int) (p.x * stepX + x0);
						int y = (int) (y0 - p.y * stepY);
						g2d.setColor(Color.WHITE);
						g2d.fillOval(x - size - 1, y - size - 1, size * 2 + 2, size * 2 + 2);
						g2d.setColor(colors.get(j));
						g2d.fillOval(x - size, y - size, size * 2, size * 2);
					}
					break;
				case SQUARE:
					size = SQUARE_SIZE;
					if (sizeOverride.get(j) != null) {
						size = sizeOverride.get(j);
					}
					for (Point2d p : data) {
						int x = (int) (p.x * stepX + x0);
						int y = (int) (y0 - p.y * stepY);
						g2d.setColor(Color.WHITE);
						g2d.fillRect(x - size - 1, y - size - 1, size * 2 + 2, size * 2 + 2);
						g2d.setColor(colors.get(j));
						g2d.fillRect(x - size, y - size, size * 2, size * 2);
					}
					break;
				case CROSS:
					size = 5;
					if (sizeOverride.get(j) != null) {
						size = sizeOverride.get(j);
					}
					for (Point2d p : data) {
						int x = (int) (p.x * stepX + x0);
						int y = (int) (y0 - p.y * stepY);
						g2d.setColor(colors.get(j));
						g2d.drawLine(x - 2, y - 2, x + 2, y + 2);
						g2d.drawLine(x - 2, y + 2, x + 2, y - 2);
					}
				}
			}
			j++;
		}
		g2d.setColor(Color.BLACK);
	}
}
