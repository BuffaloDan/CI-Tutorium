package de.buffalodan.ci.network;

public class Point2d {

	public double x;
	public double y;

	public Point2d(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}

	public Point2d(Point2d p) {
		this.x = p.x;
		this.y = p.y;
	}

	public double getDistance(Point2d p) {
		return Math.sqrt(getDistanceSquared(p));
	}
	
	public double getDistanceSquared(Point2d p) {
		return Math.pow(x - p.x, 2) + Math.pow(y - p.y, 2);
	}
	
	public Point2d multScalar(double scalar) {
		x *= scalar;
		y*= scalar;
		return this;
	}
	
	public Point2d subNew(Point2d other) {
		Point2d p = new Point2d(this);
		p.x -= other.x;
		p.y -= other.y;
		return p;
	}
	
	public Point2d add(Point2d other) {
		x += other.x;
		y += other.y;
		return this;
	}

}
