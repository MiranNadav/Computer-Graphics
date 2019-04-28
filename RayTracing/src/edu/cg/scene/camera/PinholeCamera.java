//package edu.cg.scene.camera;
//
//import edu.cg.UnimplementedMethodException;
//import edu.cg.algebra.Point;
//import edu.cg.algebra.Vec;
//import edu.cg.algebra.Ray;

//public class PinholeCamera {
//	Point cameraPosition;
//	Point centerPosition;
//	Vec towardsVec;
//	Vec upVec;
//	Vec rightVec;
//	double distToPlain;
//	double viewWidth;
//	double Rx;
//	double Ry;


//	/**
//	 * Initializes a pinhole camera model with default resolution 200X200 (RxXRy) and image width 2.
//	 * @param cameraPosition - The position of the camera.
//	 * @param towardsVec - The towards vector of the camera (not necessarily normalized).
//	 * @param upVec - The up vector of the camera.
//	 * @param distanceToPlain - The distance of the camera (position) to the center point of the image-plain.
//	 *
//	 */
//	public PinholeCamera(Point cameraPosition, Vec towardsVec, Vec upVec, double distanceToPlain) {
//		this.cameraPosition = cameraPosition;
//		this.distToPlain = distanceToPlain;
//		this.viewWidth = 2;
//		this.Rx = this.Ry = 200;
//		this.towardsVec = towardsVec.normalize();
//		this.upVec = upVec.normalize();
//		this.rightVec = this.towardsVec.cross(upVec).normalize();
//		this.centerPosition = new Ray(cameraPosition, towardsVec).add(distToPlain);
//	}
//
//
//	/**
//	 * Initializes the resolution and width of the image.
//	 * @param height - the number of pixels in the y direction.
//	 * @param width - the number of pixels in the x direction.
//	 * @param viewPlainWidth - the width of the image plain in world coordinates.
//	 */
//	public void initResolution(int height, int width, double viewPlainWidth) {
//		this.viewWidth = viewPlainWidth;
//		this.Rx = width;
//		this.Ry = height;
//	}
//
//	/**
//	 * Transforms from pixel coordinates to the center point of the corresponding pixel in model coordinates.
//	 * @param x - the index of the x direction of the pixel.
//	 * @param y - the index of the y direction of the pixel.
//	 * @return the middle point of the pixel (x,y) in the model coordinates.
//	 */
//	public Point transform(int x, int y) {
//		double pixelWidth = this.viewWidth / this.Rx;
//		double pixelHeight = pixelWidth;
//		double rightDistance = pixelWidth * (x - (int)(this.Rx / 2.0));
//		double upDistance = pixelHeight * ((int)(this.Ry / 2.0) - y);
//		Vec upMovement = this.upVec.mult(upDistance);
//		Vec rightMovement = this.rightVec.mult(rightDistance);
//		Point result = this.centerPosition.add(upMovement).add(rightMovement);
//		return result;
//	}
//
//	/**
//	 * Returns a copy of the camera position
//	 * @return a "new" point representing the camera position.
//	 */
//	public Point getCameraPosition() {
//		return new Point(this.cameraPosition.x, this.cameraPosition.y, this.cameraPosition.z);
//	}

//}

//
///*
// * Decompiled with CFR 0.143.
// */

package edu.cg.scene.camera;

import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;

public class PinholeCamera {
	Point cameraPosition;
	Point centerPoint;
	Vec towardsVec;
	Vec upVec;
	Vec rightVec;
	double viewPlainWidth;
	double distanceToPlain;
	double resX;
	double resY;
	double pixelRatio;

	public PinholeCamera(Point cameraPosition, Vec towardsVec, Vec upVec, double distanceToPlain) {
		this.cameraPosition = cameraPosition;
		this.towardsVec = towardsVec.normalize();
		this.rightVec = this.towardsVec.cross(upVec).normalize();
		this.upVec = this.rightVec.cross(this.towardsVec).normalize();
		this.distanceToPlain = distanceToPlain;
		this.centerPoint = new Ray(cameraPosition, towardsVec).add(distanceToPlain);
		this.resX = 200.0;
		this.resY = 200.0;
		this.viewPlainWidth = 2.0;
	}

	public void initResolution(int height, int width, double viewPlainWidth) {
		this.viewPlainWidth = viewPlainWidth;
		this.resX = width;
		this.resY = height;
	}

	public Point transform(int x, int y) {
		double pixelWidth;
		double pixelHeight = pixelWidth = this.viewPlainWidth / this.resX;
		double upDistance = (double)(y - (int)(this.resY / 2.0)) * pixelHeight * -1.0;
		double rightDistance = (double)(x - (int)(this.resX / 2.0)) * pixelWidth;
		Vec upMovement = this.upVec.mult(upDistance);
		Vec rightMovement = this.rightVec.mult(rightDistance);
		Point fovPoint = this.centerPoint.add(upMovement).add(rightMovement);
		return fovPoint;
	}

	public Point getCameraPosition() {
		return new Point(this.cameraPosition.x, this.cameraPosition.y, this.cameraPosition.z);
	}
}

