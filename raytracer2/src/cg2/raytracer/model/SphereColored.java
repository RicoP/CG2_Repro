package cg2.raytracer.model;

import cg2.raytracer.Hit;
import cg2.raytracer.Ray;
import cg2.vecmath.Color;
import cg2.vecmath.Vector;

public class SphereColored implements IShapeColored {
	private final Vector origin;
	private final float radius;
	private final Color color;

	public SphereColored(Vector origin, float radius, Color color) {
		super();
		this.origin = origin;
		this.radius = radius;
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	public Vector getOrigin() {
		return origin;
	}

	public float getRadius() {
		return radius;
	}

	@Override
	public Hit getHit(Ray ray) {
		final float r = radius;
		final Vector x0 = ray.getOrigin(); // Ursprung des Rays
		final Vector d = ray.getGaze().normalize(); // Gaze Direction
		final Vector c = this.origin; // Ursprung der Sph�re

		final Vector x0subC = x0.sub(c);

		float q = x0subC.dot(x0subC) - r * r;
		float p = 2f * d.dot(x0subC);
		float pHalf = p / 2;
		float underSqrt = pHalf * pHalf - q;

		if (underSqrt < 0)
			return null;

		float minusPHalf = -pHalf;

		if (underSqrt == 0) {
			// TODO change null parameters
			return new Hit(minusPHalf, null, null);
		}

		float sqrt = (float) Math.sqrt(underSqrt);
		float t1 = minusPHalf + sqrt;
		float t2 = minusPHalf - sqrt;

		// TODO change null parameters
		if (t1 > 0 && t2 > 0)
			return t1 > t2 ? new Hit(t2, null, null) : new Hit(t1, null, null);

		// TODO change null parameters
		if (t1 <= 0 && t2 > 0)
			return new Hit(t2, null, null);

		if (t1 > 0 && t2 <= 0)
			return new Hit(t1, null, null);

		return null;
	}
}
