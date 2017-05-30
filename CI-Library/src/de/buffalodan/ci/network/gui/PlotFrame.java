package de.buffalodan.ci.network.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import de.buffalodan.ci.network.gui.PlotPanel.PlotType;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class PlotFrame extends JFrame {

	private JPanel contentPane;
	private PlotPanel plotPanel;
	private JPanel south;
	private JLabel lblRuns;

	/**
	 * Create the frame.
	 */
	public PlotFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		plotPanel = new PlotPanel();
		contentPane.add(plotPanel, BorderLayout.CENTER);

		south = new JPanel();
		contentPane.add(south, BorderLayout.SOUTH);
		
		lblRuns = new JLabel("Runs: 0");
		south.add(lblRuns);
	}
	
	public void setRuns(int runs) {
		lblRuns.setText("Runs: "+runs);
	}
	
	public void addPlot(Double[][] data, Color color, PlotType type, String plotName, int index) {
		plotPanel.addPlot(data, color, type);
		JCheckBox plot = new JCheckBox(plotName);
		plot.setSelected(true);
		plot.setForeground(color);
		plot.addActionListener(new RenderActionListener(index, plot));
		south.add(plot);
		south.validate();
		repaint();
	}

	public void addPlot(double[][] data, Color color, PlotType type, String plotName, int index) {
		addPlot(plotPanel.convertData(data), color, type, plotName, index);
	}

	public PlotPanel getPlotPanel() {
		return plotPanel;
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
