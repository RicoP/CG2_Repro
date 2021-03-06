package model;

import main.Constants;
import vecmath.Color;
import vecmath.Vector;

public class Material {

	public static float VAKUUM = 1.0f;
	public static float AIR = 1.0029f;
	public static float WATER = 1.333f;
	public static float GLAS = 1.500f;
	public static float DIAMOND = 2.419f;
	public static float SOLID = Float.POSITIVE_INFINITY;
	public static float hitcount;

	private final Color kAmbient;
	private final Color kDiffuse;
	private final Color kSpecular;
	private final float phongExponent;
	private final float refractionIndex;

	public Material(Color kAmbient, Color kDiffuse, Color kSpecular,
			Color kReflect, Color kRefract, float phongExponent) {
		this.kAmbient = kAmbient;
		this.kDiffuse = kDiffuse;
		this.kSpecular = kSpecular;
		this.phongExponent = phongExponent;
		this.refractionIndex = Material.SOLID;
	}

	public Material(Color kAmbient, Color kDiffuse, Color kSpecular,
			Color kReflect, Color kRefract, float phongExponent,
			float refractionIndex) {
		super();
		this.kAmbient = kAmbient;
		this.kDiffuse = kDiffuse;
		this.kSpecular = kSpecular;
		this.phongExponent = phongExponent;
		this.refractionIndex = refractionIndex;
	}

	/**
	 * @param ray
	 * @param hit
	 * @param scene
	 * @param depth
	 * @return
	 */
	public Color shade(Ray ray, Hit hit, Scene scene, int depth, float n1) {

		// handle the simple ambient lighting
		Color result = kAmbient.modulate(scene.getAmbientLight());

		Vector point = ray.getPoint(hit.getT());

		// some colors
		Color diffuse = new Color(0, 0, 0);
		Color specular = new Color(0, 0, 0);
		Color reflection = new Color(0, 0, 0);
		Color refraction = new Color(0, 0, 0);
		Vector s;
		Vector r;
		Vector normal = hit.getNormal();
		Vector v = ray.getDirection().mult(-1.0f);

		for (LightSource light : scene.getLights()) {

			s = (light.getOrigin().sub(point)).normalize();
			r = ((normal.mult(2 * (normal.dot(s)))).sub(s)).normalize();

			float cosNS = hit.getNormal().dot(s);
			float cosRV = r.dot(v);

			float shadowCoefficient = 1;

			// fire a shadowray from the object to the light source
			Ray shadowray = new Ray(light.getOrigin().sub(point), point);
			Hit shadowhit = scene.intersect(shadowray, Constants.EPSILON,
					Float.POSITIVE_INFINITY);

			// nothing in the way
			if (shadowhit != null && shadowhit.getT() > Constants.EPSILON) {
				shadowCoefficient = 0;
			}

			if (cosNS < Constants.EPSILON) {
				continue;
			}

			// diffuse lighting
			diffuse = diffuse.add(kDiffuse.modulate(
					light.getColor().modulate(cosNS)).modulate(
					shadowCoefficient));

			if (cosRV < Constants.EPSILON) {
				continue;
			}

			// specular lighting
			specular = specular.add(kSpecular.modulate((float) Math.pow(cosRV,
					phongExponent)));
		}

		// reflection
		Vector reflectionRayDirection = (hit.getNormal()).mult(
				(hit.getNormal().dot(v)) * (2)).sub(v);
		Ray reflectionRay = new Ray(reflectionRayDirection, point);
		Hit reflectionHit = scene.intersect(reflectionRay, Constants.EPSILON,
				Float.POSITIVE_INFINITY);

		if (depth > 0 && reflectionHit != null
				&& reflectionHit.getT() > Constants.EPSILON) {
			reflection = (reflectionHit.getShape().getMaterial().shade(
					reflectionRay, reflectionHit, scene, depth - 1,
					refractionIndex));
		}

		// refraction
		if (refractionIndex != Material.SOLID) {
			Hit refHit = null;

			float n2;

			float cosOi = v.dot(normal);

			if (cosOi > 0) {
				n1 = Material.AIR;
				n2 = refractionIndex;
				hitcount++;
			} else {
				normal = normal.mult(-1);
				n2 = Material.AIR;
				n1 = refractionIndex;
				// hitcount--;
			}

			float ratio = n1 / n2;
			float ratioN1N2_pow2 = (float) Math.pow(n1 / n2, 2);

			float sinOi = 1 - cosOi;
			float sin2Oi = (float) Math.pow(sinOi, 2);
			float sin2Ot = ratioN1N2_pow2 * (sin2Oi);
			float sqrt = (float) Math.sqrt(1 - sin2Ot);

			if (depth > 0 && sqrt >= 0 && sin2Ot <= 1) {
				// if (!(n1 > n2)) {
				Vector t1 = ray.getDirection().mult(ratio);
				Vector t2 = normal.mult((ratio * cosOi) - (sqrt));
				Vector t = t1.add(t2);

				Ray refractionRay = new Ray(t.normalize(), point);

				// float scalar = refractionRay.getDirection().dot(
				// ray.getDirection());

				Hit refractionHit = scene.intersect(refractionRay,
						Constants.EPSILON, Float.POSITIVE_INFINITY);

				if (refractionHit != null
						&& refractionHit.getT() > Constants.EPSILON) {

					if (refractionHit.getMaterial().getRefractionIndex() == Material.SOLID) {
						// System.out.println("Rote Kugel getroffen");
					} else if (refractionHit.getMaterial().getRefractionIndex() < Material.AIR + 1f) {
						// System.out.println("Gr�ne Kugel getroffen");
						// System.out.println(point.z
						// - (refractionRay.getPoint(refractionHit
						// .getT()).z));
					}

					refraction = refractionHit.getMaterial().shade(
							refractionRay, refractionHit, scene, depth - 1, n2);
					refHit = refractionHit;
				}
				// }
			}

			if (refHit != null) {
				float r0 = (float) Math.pow((n1 - n2) / (n1 + n2), 2);
				float R = (float) (r0 + (1 - r0) * (Math.pow(1 - sin2Oi, 5)));
				float T = 1 - R;

				refraction = refraction.modulate(T);
				reflection = reflection.modulate(R);
			}
		}

		return result.add(diffuse).add(specular)
		// .add(reflection.modulate(kReflect))
				.add(refraction);
	}
	public float getRefractionIndex() {
		return refractionIndex;
	}
}