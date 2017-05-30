package de.buffalodan.ci.network.gui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.event.ActionListener;
import java.util.Random;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JTextField;

//Sonst nervt Eclipse mit seiner nervigen SerialID Warnung
@SuppressWarnings("serial")
public class NetworkFrame extends JFrame {

	private JPanel contentPane;
	private NetworkPanel networkPanel;
	private NetworkTool networkTool;
	private JTextField txtRuns;
	private Random colorRandom;
	private JTextField textField;

	public NetworkFrame(NetworkTool networkTool) {
		this();
		this.networkTool = networkTool;
		networkPanel.setNetwork(networkTool.getNetwork());
	}

	/**
	 * Create the frame. Nur für den Eclipse WindowBuilder!
	 */
	public NetworkFrame() {
		colorRandom = new Random(System.currentTimeMillis());

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(900, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		networkPanel = new NetworkPanel();
		contentPane.add(networkPanel, BorderLayout.CENTER);

		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.SOUTH);

		JButton btnColor = new JButton("Color");
		setRandomColor(btnColor);
		final NetworkFrame thisFrame = this;
		btnColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Color newColor = JColorChooser.showDialog(thisFrame, "Choose Plot Color", btnColor.getForeground());
				if (newColor != null) {
					btnColor.setForeground(newColor);
				}
			}
		});
		panel_1.add(btnColor);

		txtRuns = new JTextField();
		txtRuns.setText("1");
		panel_1.add(txtRuns);
		txtRuns.setColumns(10);

		JButton btnNewButton_1 = new JButton("Run");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int runs;
				try {
					runs = Integer.parseInt(txtRuns.getText());
				} catch (NumberFormatException ex) {
					return;
				}
				new Thread(() -> {
					networkTool.run(runs, btnColor.getForeground());
				}).start();
				setRandomColor(btnColor);
			}
		});
		panel_1.add(btnNewButton_1);

		JCheckBox chckbxShowBias = new JCheckBox("Show Bias");
		chckbxShowBias.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				networkPanel.setShowBias(chckbxShowBias.isSelected());
				repaint();
			}
		});
		panel_1.add(chckbxShowBias);
		
		textField = new JTextField();
		textField.setText("0.03");
		panel_1.add(textField);
		textField.setColumns(10);
		
		JButton btnSetLearningrate = new JButton("Set Lernrate");
		btnSetLearningrate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				double learningRate;
				try {
					learningRate = Double.parseDouble(textField.getText());
				} catch (NumberFormatException ex) {
					return;
				}
				networkTool.getNetwork().setLearningRate(learningRate);
			}
		});
		panel_1.add(btnSetLearningrate);
		
		JButton btnScreenshot = new JButton("ScreenShot");
		btnScreenshot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				networkTool.screenShot();
			}
		});
		panel_1.add(btnScreenshot);
	}

	private void setRandomColor(JButton colorBtn) {
		int r = colorRandom.nextInt(255);
		int g = colorRandom.nextInt(255);
		int b = colorRandom.nextInt(255);
		Color c = new Color(r, g, b);
		colorBtn.setForeground(c);
	}

}
