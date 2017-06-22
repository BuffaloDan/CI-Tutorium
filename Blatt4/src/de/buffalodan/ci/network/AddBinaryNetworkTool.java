package de.buffalodan.ci.network;

import java.util.Random;

public class AddBinaryNetworkTool {

	private Network network;
	private AddBinaryFrame addBinaryFrame;

	public AddBinaryNetworkTool(Network network) {
		this.network = network;
	}

	public void run(int runs) {
		int[] expected = new int[runs + 1];
		int[] in1 = new int[runs+1];
		int[] in2 = new int[runs+1];
		Random r = new Random();
		int more = 0;
		for (int run = 0; run < runs + 3; run++) {
			int i1 = 0, i2 = 0;
			if (run < runs) {
				i1 = r.nextInt(2);
				i2 = r.nextInt(2);
			}
			if (run <= runs) {
				in1[run] = i1;
				in2[run] = i2;
				int i3 = i1 + i2 + more;
				more = 0;
				if (i3 == 2) {
					more = 1;
					i3 = 0;
				} else if (i3 == 3) {
					more = 1;
					i3 = 1;
				}
				expected[run] = i3;
			}
			network.reset();
			network.getInputLayer().getNeurons().get(0).setInput(i1);
			network.getInputLayer().getNeurons().get(1).setInput(i2);
			network.calculate();
			if (run - 2 >= 0) {
				network.backpropagate(new double[] { expected[run - 2] });
			}
			
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		test();
		addBinaryFrame.repaint();
	}
	
	private void test() {
		network.run(new double[]{1,0}, null);
		network.run(new double[]{1,1}, null);
		network.run(new double[]{0,0}, null);
		System.out.println("1,0: "+network.getSingleOutput());
		network.run(new double[]{0,0}, null);
		System.out.println("1,1: "+network.getSingleOutput());
		network.run(new double[]{0,0}, null);
		System.out.println("0,0: "+network.getSingleOutput());
	}

	public Network getNetwork() {
		return network;
	}

	public void start() {
		addBinaryFrame = new AddBinaryFrame(this);
		addBinaryFrame.setVisible(true);
	}

}
