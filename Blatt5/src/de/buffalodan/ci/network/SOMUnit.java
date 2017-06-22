package de.buffalodan.ci.network;

public class SOMUnit {

	public Point2d location;
	public SOMUnit connection;

	public SOMUnit(Point2d location) {
		super();
		this.location = location;
	}

	public void connect(SOMUnit unit) {
		this.connection = unit;
	}

}
