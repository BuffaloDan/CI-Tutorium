package de.buffalodan.ci.network.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class PlotFrame extends JFrame {

	private JPanel contentPane;
	private PlotPanel plotPanel;
	private JPanel south;

	/**
	 * Create the frame.
	 */
	public PlotFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		plotPanel = new PlotPanel();
		contentPane.add(plotPanel, BorderLayout.CENTER);

		south = new JPanel();
		contentPane.add(south, BorderLayout.SOUTH);
	}

	public void setXValues(double[] xValues) {
		// Löschen der BeispielDaten
		plotPanel.resetY();
		plotPanel.setXValues(xValues);
	}

	public void addYValues(double[] yValues, Color color, String plotName, int index) {
		plotPanel.addYValues(yValues, color);
		JCheckBox plot = new JCheckBox(plotName);
		plot.setSelected(true);
		plot.setForeground(color);
		plot.addActionListener(new RenderActionListener(index, plot));
		south.add(plot);
		south.validate();
		repaint();
	}

	private class RenderActionListener implements ActionListener {
		private int index;
		private JCheckBox box;

		public RenderActionListener(int index, JCheckBox box) {
			this.index = index;
			this.box = box;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			plotPanel.setRender(index, box.isSelected());
			repaint();
		}
	}
}
