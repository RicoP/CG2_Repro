package main;

import java.util.ArrayList;
import java.util.List;

import model.Camera;
import model.LightSource;
import model.Material;
import model.Scene;
import model.shapes.AxisAlignedBox;
import model.shapes.IShapeColored;
import model.shapes.Plane;
import model.shapes.Sphere;
import model.shapes.Triangle;
import vecmath.Color;
import vecmath.Vector;

public class Main {

	public static void main(String[] args) {
		System.out.println("Raytracer start");

		String path = System.getProperty("user.home");
		String filename = path + "/" + "raytracer.png";

		List<IShapeColored> objects = new ArrayList<IShapeColored>();
		List<LightSource> lights = new ArrayList<LightSource>();

		// pixel dimensions for the picture
		final int nx = 1280;
		final int ny = 960;			

		// the colors used for the objects in the scene
		final Color red = new Color(0.8f, 0, 0);
		final Color ambientRed = new Color(0.5f, 0, 0);
		final Color reflectRed = new Color(0.3f, 0, 0);
		final Color refractRed = new Color(0.3f, 0.001f, 0.001f);

		final Color green = new Color(0, 0.2f, 0);
		final Color ambientGreen = new Color(0, 0.1f, 0);
		final Color refractGreen = new Color(0.8f, 0.8f, 0.8f);

		final Color blue = new Color(0, 0, 0.8f);
		final Color ambientBlue = new Color(0, 0, 0.5f);
		final Color refractBlue = new Color(0, 0, 0.3f);

		final Color yellow = new Color(1, 1, 0);
		final Color ambientYellow = new Color(0.5f, 0.5f, 0);
		final Color refractYellow = new Color(0.3f, 0.3f, 0);

		final Color dirt = new Color(0.2f, 0.2f, 0.2f);
		final Color boxColor = new Color(0, 0.8f, 1.0f);
		final Color background = new Color(0, 0, 0);
		final Color white = new Color(0.8f, 0.8f, 0.8f);
		final float phongExp = 300f;			

		final Material redMaterial = new Material(ambientRed, red, white,
				white, refractRed, phongExp, Material.GLAS);
 
		final Material redBoxMaterial = new Material(ambientRed, red, white,
				white, refractRed, phongExp, Material.DIAMOND);
 
		final Material greenMaterial = new Material(ambientGreen, green, white,
				white, refractGreen, phongExp, Material.AIR + 0.00001f);

		final Material blueMaterial = new Material(ambientBlue, blue, white,
				white, refractBlue, phongExp, Material.DIAMOND);

		final Material yellowMaterial = new Material(ambientYellow, yellow,
				white, refractYellow, refractYellow, phongExp);

		final Material planeMaterial = new Material(dirt, dirt, white, white,
				white, phongExp, Material.SOLID);

		// plane requires any point, a normal, and a color
		Plane groundPlane = new Plane(new Vector(0f, -6.0f, -1.0f), new Vector(
				0f, 1f, 0f), planeMaterial);
		Plane rightPlane = new Plane(new Vector(10f, -6.0f, -1f), new Vector(
				-10f, 0f, -5f), planeMaterial);
		Plane backPlane = new Plane(new Vector(0, 0, -150), Vector.Z,
				planeMaterial);

		// sphere requires specification of center, radius, and color
		float px = -8.0f;
		float py = -3.0f;
		float pz = -13.0f;
		
		
		Sphere redSphere2 = new Sphere(new Vector(px, py, pz), 1.3f,
				redMaterial);
		Sphere yellowSphere = new Sphere(new Vector(-5f, py + 2f, pz - 13f), 2.9f,
				yellowMaterial);
		Sphere blueSphere = new Sphere(new Vector(px + 1f, py + 1f, pz), 1.0f,
				blueMaterial);
		Sphere greenSphere = new Sphere(new Vector(px - 1f, py + 1f, pz), 1.0f,
				greenMaterial);

		// an axis aligned box requires only two points
		AxisAlignedBox box = new AxisAlignedBox(new Vector(-0.2f, -5, -15),
				new Vector(6, 0.2f, -12), redBoxMaterial);

		// a triangle is defined by 3 points
		Triangle triangle = new Triangle(new Vector(-9f, 5f, -10f), new Vector(
				-7f, 5f, -10f), new Vector(-8f, 7f, -10f), greenMaterial);

		Triangle triangle2 = new Triangle(new Vector(-7f, 5f, -10f),
				new Vector(-5f, 5f, -10f), new Vector(-6f, 7f, -10f),
				redMaterial);
		
		AxisAlignedBox box2 = new AxisAlignedBox(new Vector(-1, 6, -27),
				new Vector(5, 12, -18), blueMaterial);


		objects.add(groundPlane);
		//objects.add(rightPlane);
		//objects.add(backPlane);
		objects.add(redSphere2);
		objects.add(yellowSphere);
		objects.add(blueSphere);
		objects.add(greenSphere);
		objects.add(box);
		objects.add(box2);
		//objects.add(triangle);
		//objects.add(triangle2);

		// camera (field of view, viewPlane width, viewPlane height, picture
		// width, picture height)
		Camera camera = new Camera(90f, 12, 9, nx, ny);

		// add some lights
		lights.add(new LightSource(new Vector(-600f, 90, -30), white));
		lights.add(new LightSource(new Vector(0f, 6000f, 0.0f), white));
		//lights.add(new LightSource(new Vector(-8.0f, 9.0f, 10.0f), white));
		//lights.add(new LightSource(new Vector(-1.0f, 200.0f, -10.0f), white));

		// instantiate scene an empty scene with yellow ambient light source
		Scene scene = new Scene(objects, lights, new Color(0.5f, 0.5f, 0.5f),
				camera);

		new ImageGenerator(new Raytracer(scene, background), nx, ny, filename,
				"png");
		ImageGenerator.showImage(filename);

		System.out.println("Raytracer end");
		System.out.printf("You can find the image at %s%n", filename);
		// System.out.println(Material.hitcount);
	}
}
