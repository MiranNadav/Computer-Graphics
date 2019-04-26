package edu.cg.scene.lightSources;

import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;
import edu.cg.scene.objects.Surface;

public class Spotlight extends PointLight {
	private Vec direction;
	
	public Spotlight initDirection(Vec direction) {
		this.direction = direction;
		return this;
	}
	
	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return "Spotlight: " + endl +
				description() + 
				"Direction: " + direction + endl;
	}
	
	@Override
	public Spotlight initPosition(Point position) {
		return (Spotlight)super.initPosition(position);
	}
	
	@Override
	public Spotlight initIntensity(Vec intensity) {
		return (Spotlight)super.initIntensity(intensity);
	}
	
	@Override
	public Spotlight initDecayFactors(double q, double l, double c) {
		return (Spotlight)super.initDecayFactors(q, l, c);
	}
	
	//TODO: add some methods

	@Override
	public boolean isOccludedBy(Surface surface, Ray rayToLight) {
		if (rayToLight.direction().neg().dot(this.direction.normalize()) < 1.0 * Math.pow(10,-5)) {
			return true;
		}
		return super.isOccludedBy(surface, rayToLight);
	}

	@Override
	public Vec intensity(Point hittingPoint, Ray rayToLight) {
		Vec L;
		Vec D = this.direction.normalize().neg();
		double cosGamma = D.dot(L = rayToLight.direction());
		if (cosGamma < 1.0 * Math.pow(10,-5)) {
			return new Vec(0.0);
		}
		return super.intensity(hittingPoint, rayToLight).mult(cosGamma);
	}
}
