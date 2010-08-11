package com.vividsolutions.jts.algorithm;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.util.Assert;

/**
 * A 2-dimensional vector represented by double-precision X and Y components.
 * 
 * @author mbdavis
 * 
 */
public class Vector2D {
	public static Vector2D create(double x, double y) {
		return new Vector2D(x, y);
	}

	public static Vector2D create(Vector2D v) {
		return new Vector2D(v);
	}

	public static Vector2D create(Coordinate v) {
		return new Vector2D(v);
	}

	public static Vector2D create(Coordinate from, Coordinate to) {
		return new Vector2D(from, to);
	}

	/**
	 * The X component of this vector.
	 */
	private double x;

	/**
	 * The Y component of this vector.
	 */
	private double y;

	public Vector2D() {
		this(0.0, 0.0);
	}

	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Vector2D(Vector2D v) {
		x = v.x;
		y = v.y;
	}

	public Vector2D(Coordinate from, Coordinate to) {
		x = to.x - from.x;
		y = to.y - from.y;
	}

	public Vector2D(Coordinate v) {
		x = v.x;
		y = v.y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getComponent(int index) {
		if (index == 0)
			return x;
		return y;
	}

	public Vector2D add(Vector2D v) {
		return create(x + v.x, y + v.y);
	}

	public Vector2D subtract(Vector2D v) {
		return create(x - v.x, y - v.y);
	}

	public Vector2D multiply(double d) {
		return create(x * d, y * d);
	}

	public Vector2D divide(double d) {
		return create(x / d, y / d);
	}

	public double length() {
		return Math.sqrt(x * x + y * y);
	}

	public double lengthSquared() {
		return x * x + y * y;
	}

	public Vector2D normalize() {
		double length = length();
		if (length > 0.0)
			return divide(length());
		return create(0.0, 0.0);
	}

	/**
	 * Computes the dot-product of two vectors
	 * 
	 * @param v1
	 *          a vector
	 * @param v2
	 *          a vector
	 * @return the dot product of the vectors
	 */
	public double dot(Vector2D v) {
		return x * v.x + y * v.y;
	}

	/**
	 * Rotates a vector by a given number of quarter-circles (i.e. multiples of 90
	 * degrees or Pi/2 radians). A positive number rotates counter-clockwise, a
	 * negative number rotates clockwise. Under this operation the magnitude of
	 * the vector and the absolute values of the ordinates do not change, only
	 * their sign and ordinate index.
	 * 
	 * @param v
	 *          the vector to rotate.
	 * @param numQuarters
	 *          the number of quarter-circles to rotate by
	 * @return the rotated vector.
	 */
	public Vector2D rotateByQuarterCircle(int numQuarters) {
		int nQuad = numQuarters % 4;
		if (numQuarters < 0 && nQuad != 0) {
			nQuad = nQuad + 4;
		}
		switch (nQuad) {
		case 0:
			return create(x, y);
		case 1:
			return create(-y, x);
		case 2:
			return create(-x, -y);
		case 3:
			return create(y, -x);
		}
		Assert.shouldNeverReachHere();
		return null;
	}

	public Coordinate translate(Coordinate coord) {
		return new Coordinate(x + coord.x, y + coord.y);
	}

	public Coordinate toCoordinate() {
		return new Coordinate(x, y);
	}

	public String toString() {
		return "[" + x + ", " + y + "]";
	}
	
	/**
	 * Tests if a vector <tt>o</tt> has the same values for the x and y
	 * components.
	 * 
	 * @param o
	 *          a <tt>Vector2D</tt> with which to do the comparison.
	 * @return true if <tt>other</tt> is a <tt>Vector2D</tt> with the same
	 *         values for the x and y components.
	 */
	public boolean equals(Object o) {
		if (!(o instanceof Vector2D)) {
			return false;
		}
		Vector2D v = (Vector2D) o;
		return x == v.x && y == v.y;
	}

	/**
	 * Gets a hashcode for this vector.
	 * 
	 * @return a hashcode for this vector
	 */
	public int hashCode() {
		// Algorithm from Effective Java by Joshua Bloch
		int result = 17;
		result = 37 * result + Coordinate.hashCode(x);
		result = 37 * result + Coordinate.hashCode(y);
		return result;
	}


}
