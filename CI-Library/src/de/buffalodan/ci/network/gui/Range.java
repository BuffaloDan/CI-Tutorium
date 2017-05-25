package de.buffalodan.ci.network.gui;

import java.util.Iterator;

public class Range implements Iterable<Double> {

	private final double start;
	private final double end;
	private final int sampleRate;

	public Range(double start, double end, int sampleRate) {
		super();
		this.start = start;
		this.end = end;
		this.sampleRate = sampleRate;
	}

	public double getStart() {
		return start;
	}

	public double getEnd() {
		return end;
	}

	public int getSampleRate() {
		return sampleRate;
	}

	@Override
	public Iterator<Double> iterator() {
		return new RangeIterator(this);
	}

	private class RangeIterator implements Iterator<Double> {

		private double current;
		private double step;
		private Range range;

		public RangeIterator(Range range) {
			this.range = range;
			current = range.start;
			step = (range.end - range.start) / range.sampleRate;
		}

		@Override
		public boolean hasNext() {
			return current < range.end;
		}

		@Override
		public Double next() {
			current += step;
			return current;
		}

	}

}
