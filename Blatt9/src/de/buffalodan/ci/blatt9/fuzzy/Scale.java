package de.buffalodan.ci.blatt9.fuzzy;

import java.awt.Graphics2D;

public class Scale {

	private double scaleX;
	private double scaleY;
	private double startX;
	private double endX;
	private double startY;
	private double endY;
	private double width;
	private double height;
	private int scaleXStep;
	private int scaleYStep;

	public Scale(double startX, double endX, double startY, double endY, double width, double height, int scaleXStep,
			int scaleYStep) {
		super();
		this.startX = startX;
		this.endX = endX;
		this.startY = startY;
		this.endY = endY;
		this.width = width;
		this.height = height;
		this.scaleXStep = scaleXStep;
		this.scaleYStep = scaleYStep;
	}

	public void calculateScale() {
		scaleX = width / (endX - startX);
		scaleY = height / (endY - startY);
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double getScaleX() {
		return scaleX;
	}

	public double getScaleY() {
		return scaleY;
	}

	public double getStartX() {
		return startX;
	}

	public double getStartY() {
		return startY;
	}

	public double getEndX() {
		return endX;
	}

	public double getEndY() {
		return endY;
	}

	public int scaleX(double x) {
		return (int) ((x - startX) * scaleX);
	}

	public int scaleY(double y) {
		return (int) (height - (y * scaleY));
	}

	public void draw(Graphics2D g2) {
		g2.drawLine(scaleX(startX), scaleY(0), scaleX(endX) + 10, scaleY(0));
		g2.drawLine(scaleX(endX) + 10, scaleY(0), scaleX(endX) + 5, scaleY(0) - 5);
		g2.drawLine(scaleX(endX) + 10, scaleY(0), scaleX(endX) + 5, scaleY(0) + 5);

		g2.drawLine(scaleX(0), scaleY(startY), scaleX(0), scaleY(endY) - 10);
		g2.drawLine(scaleX(0), scaleY(endY) - 10, scaleX(0) - 5, scaleY(endY) - 5);
		g2.drawLine(scaleX(0), scaleY(endY) - 10, scaleX(0) + 5, scaleY(endY) - 5);

		double x = startX;
		while (x <= endX) {
			String s = String.valueOf(x);
			double yoffset = g2.getFontMetrics().getHeight();
			double xoffset = g2.getFontMetrics().stringWidth(s)/2;
			g2.drawString(s, (int) (scaleX(x)-xoffset), (int) (scaleY(0) + yoffset));
			g2.drawLine(scaleX(x), scaleY(0)-2, scaleX(x), scaleY(0)+2);
			x += scaleXStep;
		}
		
		double y = startY;
		while (y <= endY) {
			String s = String.valueOf(y);
			double yoffset = g2.getFontMetrics().getHeight()/2;
			double xoffset = g2.getFontMetrics().stringWidth(s);
			g2.drawString(s, (int) (scaleX(0)-xoffset), (int) (scaleY(y) + yoffset));
			g2.drawLine(scaleX(0)-2, scaleY(y), scaleX(0)+2, scaleY(y));
			y += scaleYStep;
		}
	}

}
