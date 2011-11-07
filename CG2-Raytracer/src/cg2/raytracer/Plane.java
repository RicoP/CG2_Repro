package cg2.raytracer;

import cg2.vecmath.Color;
import cg2.vecmath.Vector;

public class Plane implements IShapeColored {
	private final Vector x;
	private final Vector n;
	private final Color color;

	public Plane(Vector x, Vector n, Color color) {
		super();
		this.x = x;
		this.n = n;
		this.color = color;
	}

	public Vector getX() {
		return x;
	}

	public Vector getN() {
		return n;
	}

	public Color getColor() {
		return color;
	}

	@Override
	public Hit getHit(Ray ray) {
		float d = n.dot(x);
		float ndotd = n.dot(ray.getGaze());

		if (ndotd == 0)
			return null;

		float t = (d - n.dot(ray.getOrigin())) / ndotd;

		if (t < 0)
			return null;

		if (((int) (t)) % 2 == 0)
			return new Hit(t, color);
		else
			return new Hit(t, new Color(1, 1, 1));
	}
}
