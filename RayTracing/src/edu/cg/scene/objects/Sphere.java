/*
 * Decompiled with CFR 0.143.
 */
package edu.cg.scene.objects;

import edu.cg.algebra.*;
import edu.cg.scene.objects.Shape;

public class Sphere
		extends Shape {
	private Point center;
	private double radius;

	public Sphere(Point center, double radius) {
		this.center = center;
		this.radius = radius;
	}

	public Sphere() {
		this(new Point(0.0, -0.5, -6.0), 0.5);
	}

	public String toString() {
		String endl = System.lineSeparator();
		return "Sphere:" + endl + "Center: " + this.center + endl + "Radius: " + this.radius + endl;
	}

	@Override
	public Hit intersect(Ray ray) {
		//TODO : refactor a little
		double b = ray.direction().mult(2.0).dot(ray.source().sub(this.center));
		double c = ray.source().distSqr(this.center) - this.radius * this.radius;
		double discriminant = Math.sqrt(b * b - 4.0 * c);
		if (Double.isNaN(discriminant)) {
			return null;
		}

		double t1 = (-b - discriminant) / 2.0;
		double t2 = (-b + discriminant) / 2.0;

		Hit hit = null;
		Vec v;
		if (t1 <= Ops.infinity && t1 >= Ops.epsilon) {
			v = ray.add(t1).sub(this.center).normalize();
			hit = new Hit(t1, v);
		} else if (t2 <= Ops.infinity && t2 >= Ops.epsilon) {
			v = ray.add(t2).sub(this.center).neg();
			hit = new Hit(t2, v);
			hit.setWithin();
		}

		return hit;

	}
}

