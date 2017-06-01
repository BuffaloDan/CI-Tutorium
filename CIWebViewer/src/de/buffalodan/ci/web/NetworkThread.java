package de.buffalodan.ci.web;

import de.buffalodan.ci.network.Network;

public class NetworkThread implements Runnable {

	private Network network;
	private TrainingData[] trainingData;
	private int runs;
	private int currentRun = 0;
	private TestData testData;
	private boolean running = false;

	private long lastTestDataCollect = 0;

	public NetworkThread(Network network, TrainingData[] trainingData, int runs, TestData testData) {
		super();
		this.network = network;
		this.trainingData = trainingData;
		this.runs = runs;
		this.testData = testData;
	}

	public synchronized void setNetwork(Network network) {
		this.network = network;
	}

	@Override
	public synchronized void run() {
		running = true;
		for (currentRun = 0; currentRun < runs; currentRun++) {
			for (int i = 0; i < trainingData.length; i++) {
				TrainingData td = trainingData[i];
				network.run(td.input, td.expected);
			}
			if (System.currentTimeMillis() - lastTestDataCollect > 700) {
				testData.collect(network);
				lastTestDataCollect = System.currentTimeMillis();
			}
		}
		running = false;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRuns(int runs) {
		this.runs = runs;
	}

	public int getCurrentRun() {
		return currentRun;
	}

	public int getRuns() {
		return runs;
	}

	public static class TrainingData {
		public double[] input;
		public double[] expected;

		public TrainingData(double[] input, double[] expected) {
			this.input = input;
			this.expected = expected;
		}

	}

}
