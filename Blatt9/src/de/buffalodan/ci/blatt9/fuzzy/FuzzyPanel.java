package de.buffalodan.ci.blatt9.fuzzy;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;

public class FuzzyPanel extends JPanel {

	private static final long serialVersionUID = 1692041968538379242L;

	private FuzzySet fs;
	private Scale scale;

	private Color[] colors = new Color[] { Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.PINK };

	/**
	 * Create the panel.
	 */
	public FuzzyPanel(FuzzySet fs, Scale scale) {
		this.fs = fs;
		this.scale = scale;
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				scale.setWidth(getWidth() - 80);
				scale.setHeight(getHeight() - 80);
				scale.calculateScale();
			}
		});
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.translate(40, 40);

		scale.draw(g2);
		int i = 0;
		for (FuzzyMember fm : fs.getMembers()) {
			g2.setColor(colors[i]);
			fm.draw(g2, scale);
			i++;
			if (i > colors.length)
				i = 0;
		}
	}

}
