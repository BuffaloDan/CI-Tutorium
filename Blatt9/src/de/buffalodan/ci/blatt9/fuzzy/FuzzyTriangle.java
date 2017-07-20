package de.buffalodan.ci.blatt9.fuzzy;

import java.awt.Color;
import java.awt.Graphics2D;

public class FuzzyTriangle extends FuzzyMember {

	private double a, b, c;
	/*
	 * 0 = normal 1 = nur 1. Hälfte 2 = nur 2. Hälfte
	 */
	private int mode = 0;

	public FuzzyTriangle(double a, double b, double c, String label) {
		super(label);
		this.a = a;
		this.b = b;
		this.c = c;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	@Override
	public void draw(Graphics2D g2, Scale scale) {
		if (mode != 1)
			g2.drawLine(scale.scaleX(a), scale.scaleY(0), scale.scaleX(b), scale.scaleY(1));
		Color col = g2.getColor();
		g2.setColor(Color.BLACK);
		g2.drawString(label, scale.scaleX(b) - g2.getFontMetrics().stringWidth(label) / 2, scale.scaleY(1));
		g2.setColor(col);
		if (mode != 2)
			g2.drawLine(scale.scaleX(b), scale.scaleY(1), scale.scaleX(c), scale.scaleY(0));
	}

}
