package de.buffalodan.ci.blatt9.fuzzy;

import java.awt.Graphics2D;

public abstract class FuzzyMember {

	protected String label;
	
	public FuzzyMember(String label) {
		this.label = label;
	}

	public abstract void draw(Graphics2D g2, Scale scale);
}
