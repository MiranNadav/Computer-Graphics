package edu.cg.scene.lightSources;

import edu.cg.algebra.Hit;
import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;
import edu.cg.scene.objects.Surface;

public class PointLight extends Light {
	protected Point position;
	
	//Decay factors:
	protected double kq = 0.01;
	protected double kl = 0.1;
	protected double kc = 1;
	
	protected String description() {
		String endl = System.lineSeparator();
		return "Intensity: " + intensity + endl +
				"Position: " + position + endl +
				"Decay factors: kq = " + kq + ", kl = " + kl + ", kc = " + kc + endl;
	}
	
	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return "Point Light:" + endl + description();
	}
	
	@Override
	public PointLight initIntensity(Vec intensity) {
		return (PointLight)super.initIntensity(intensity);
	}
	
	public PointLight initPosition(Point position) {
		this.position = position;
		return this;
	}
	
	public PointLight initDecayFactors(double kq, double kl, double kc) {
		this.kq = kq;
		this.kl = kl;
		this.kc = kc;
		return this;
	}

	//TODO: add some methods


	@Override
	public Vec intensity(Point hittingPoint, Ray rayToLight) {
        double distance = hittingPoint.dist(this.position);
        double decay = distance * (this.kl + this.kq * distance) + this.kc;
        return this.intensity.mult(1.0 / decay);
	}

	@Override
	public boolean isOccludedBy(Surface surface, Ray rayToLight) {
        Hit hit = surface.intersect(rayToLight);
        if (hit == null) {
            return false;
        }

        Point source = rayToLight.source();
        Point hittingPoint = rayToLight.getHittingPoint(hit);
        return source.distSqr(this.position) > source.distSqr(hittingPoint);
	}

	@Override
	public Ray rayToLight(Point fromPoint) {
        return new Ray(fromPoint, this.position);
	}
}
