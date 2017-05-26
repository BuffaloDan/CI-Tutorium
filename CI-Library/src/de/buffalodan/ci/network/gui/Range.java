package de.buffalodan.ci.network.gui;

import java.util.Iterator;

public class Range {

	private final double start;
	private final double end;

	public Range(double start, double end) {
		super();
		this.start = start;
		this.end = end;
	}

	public double getStart() {
		return start;
	}

	public double getEnd() {
		return end;
	}

	public Iterable<Double> getIterable(int samplerate) {
		return new RangeIterable(this, samplerate);
	}

	public class RangeIterable implements Iterator<Double>, Iterable<Double> {

		private double current;
		private double step;
		private Range range;

		private RangeIterable(Range range, int sampleRate) {
			this.range = range;
			current = range.start;
			step = (range.end - range.start) / sampleRate;
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

		@Override
		public Iterator<Double> iterator() {
			return this;
		}

	}

}
