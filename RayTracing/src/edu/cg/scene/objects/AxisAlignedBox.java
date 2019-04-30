package edu.cg.scene.objects;

import edu.cg.algebra.Hit;
import edu.cg.algebra.Ops;
import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;

public class AxisAlignedBox extends Shape {
	private Point minPoint;
	private Point maxPoint;
	private String name = "";
	static private int CURR_IDX;

	/**
	 * Creates an axis aligned box with a specified minPoint and maxPoint.
	 */
	public AxisAlignedBox(Point minPoint, Point maxPoint) {
		this.minPoint = minPoint;
		this.maxPoint = maxPoint;
		name = new String("Box " + CURR_IDX);
		CURR_IDX += 1;
		fixBoundryPoints();
	}

	/**
	 * Creates a default axis aligned box with a specified minPoint and maxPoint.
	 */
	public AxisAlignedBox() {
		minPoint = new Point(-1.0, -1.0, -1.0);
		maxPoint = new Point(1.0, 1.0, 1.0);
	}

	/**
	 * This methods fixes the boundary points minPoint and maxPoint so that the values are consistent.
	 */
	private void fixBoundryPoints() {
		double min_x = Math.min(minPoint.x, maxPoint.x), max_x = Math.max(minPoint.x, maxPoint.x),
				min_y = Math.min(minPoint.y, maxPoint.y), max_y = Math.max(minPoint.y, maxPoint.y),
				min_z = Math.min(minPoint.z, maxPoint.z), max_z = Math.max(minPoint.z, maxPoint.z);
		minPoint = new Point(min_x, min_y, min_z);
		maxPoint = new Point(max_x, max_y, max_z);
	}

	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return name + endl + "Min Point: " + minPoint + endl + "Max Point: " + maxPoint + endl;
	}

	//Initializers
	public AxisAlignedBox initMinPoint(Point minPoint) {
		this.minPoint = minPoint;
		fixBoundryPoints();
		return this;
	}

	public AxisAlignedBox initMaxPoint(Point maxPoint) {
		this.maxPoint = maxPoint;
		fixBoundryPoints();
		return this;
	}

	@Override
	public Hit intersect(Ray ray) {
		double t1,t2;
		double tNear = Double.NEGATIVE_INFINITY;
		double tFar = Double.POSITIVE_INFINITY;
		Vec direction = ray.direction();
		Point origin = ray.source();
		if(direction.x != 0) {
			double rx = 1 / direction.x;
			t1 = (minPoint.x - origin.x) * rx;
			t2 = (maxPoint.x - origin.x) * rx;

			if(t1 > t2) {
				double temp = t1;
				t1 = t2;
				t2 = temp;
			}

			tNear = t1;
			tFar = t2;
		}

		if(direction.y != 0) {
			double ry = 1 / direction.y;
			t1 = (minPoint.y - origin.y) * ry;
			t2 = (maxPoint.y - origin.y) * ry;

			if(t1 > t2) {
				double temp = t1;
				t1 = t2;
				t2 = temp;
			}

			if(t1 > tNear) {
				tNear = t1;
			}

			if(t2 < tFar) {
				tFar = t2;
			}
		}

		if(direction.z != 0) {
			double rz = 1 / direction.z;
			t1 = (minPoint.z - origin.z) * rz;
			t2 = (maxPoint.z - origin.z) * rz;

			if(t1 > t2) {
				double temp = t1;
				t1 = t2;
				t2 = temp;
			}

			if(t1 > tNear) {
				tNear = t1;
			}

			if(t2 < tFar) {
				tFar = t2;
			}
		}
		double tMin = tNear;
		boolean isWithin = false;
		if(tMin < Ops.epsilon) {
			isWithin = true;
			tMin = tFar;
		}
		Vec normal = normal(ray.add(tMin));
		if(isWithin) {
			normal = normal.mult(-1D);
		}
		return new Hit(tMin, normal).setIsWithin(isWithin);

	}

	private Vec normal(Point p)
	{
		Vec normal = null;
		if (Math.abs(p.x - this.minPoint.x) <= Ops.epsilon) {
			normal =  new Vec(-1.0D, 0.0D, 0.0D);
		}
		if (Math.abs(p.x - this.maxPoint.x) <= Ops.epsilon) {
			normal = new Vec(1.0D, 0.0D, 0.0D);
		}
		if (Math.abs(p.y - this.minPoint.y) <= Ops.epsilon) {
			normal = new Vec(0.0D, -1.0D, 0.0D);
		}
		if (Math.abs(p.y - this.maxPoint.y) <= Ops.epsilon) {
			normal = new Vec(0.0D, 1.0D, 0.0D);
		}
		if (Math.abs(p.z - this.minPoint.z) <= Ops.epsilon) {
			normal = new Vec(0.0D, 0.0D, -1.0D);
		}
		if (Math.abs(p.z - this.maxPoint.z) <= Ops.epsilon) {
			normal = new Vec(0.0D, 0.0D, 1.0D);
		}
		return normal;
	}
}


