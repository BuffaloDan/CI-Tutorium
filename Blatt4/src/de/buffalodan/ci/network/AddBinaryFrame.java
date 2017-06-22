package de.buffalodan.ci.network;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import de.buffalodan.ci.network.gui.NetworkPanel;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;

public class AddBinaryFrame extends JFrame {

	private static final long serialVersionUID = 2948512030834197774L;
	private JPanel contentPane;
	private AddBinaryNetworkTool networkTool;
	private NetworkPanel networkPanel;

	/**
	 * Create the frame.
	 */
	public AddBinaryFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		networkPanel = new NetworkPanel();
		contentPane.add(networkPanel, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel_1 = new JLabel("New label");
		panel.add(lblNewLabel_1, BorderLayout.CENTER);
		
		JPanel panel_1 = new JPanel();
		panel.add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JSeparator separator = new JSeparator();
		panel_1.add(separator, BorderLayout.NORTH);
		
		JLabel lblNewLabel_2 = new JLabel("New label");
		panel_1.add(lblNewLabel_2, BorderLayout.SOUTH);
		
		JLabel lblNewLabel = new JLabel("New label");
		panel.add(lblNewLabel, BorderLayout.NORTH);
	}
	
	public AddBinaryFrame(AddBinaryNetworkTool networkTool) {
		this();
		this.networkTool = networkTool;
		networkPanel.setNetwork(networkTool.getNetwork());
	}

}
