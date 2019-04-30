package edu.cg.scene.objects;

import edu.cg.algebra.*;

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
        double b = ray.direction().mult(2.0).dot(ray.source().sub(this.center));
        double discriminant = Math.sqrt(Math.pow(b, 2) - 4.0 * (this.distanceFromCenter(ray.source())));
        if(!Double.isNaN(discriminant)) {
            double t1 = (-b - discriminant) / 2.0;
            double t2 = (-b + discriminant) / 2.0;

            Vec normal = this.normal(ray.add(t1));

            if (t1 < Ops.infinity && t2 > Ops.epsilon) {
                return new Hit(t1, normal);
            }
        }
        return null;
    }

    /**
     * Calculates the distance from a point p to the center of the sphere.
     * This allows us to check if point p is in sphere:
     * In case the return value is equal or greater than 0, it means that the point is in the sphere. else, no.
     *
     * @param p - point to check the distance for
     * @return distance from p to sphere center squared - r squared
     */
    public double distanceFromCenter(Point p) {
        return p.distSqr(this.center) - this.radius * this.radius;
    }

    private Vec normal(Point p) {
        return p.sub(this.center).normalize();
    }

}

