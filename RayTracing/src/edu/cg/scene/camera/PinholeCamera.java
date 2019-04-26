package edu.cg.scene.camera;

import edu.cg.UnimplementedMethodException;
import edu.cg.algebra.Point;
import edu.cg.algebra.Vec;
import edu.cg.algebra.Ray;

public class PinholeCamera {
	Point cameraPosition, centerPosition;
	Vec towardsVec, upVec, rightVec;
	double Rx,Ry;
	double distToPlain;
	double viewWidth;


	/**
	 * Initializes a pinhole camera model with default resolution 200X200 (RxXRy) and image width 2.
	 * @param cameraPosition - The position of the camera.
	 * @param towardsVec - The towards vector of the camera (not necessarily normalized).
	 * @param upVec - The up vector of the camera.
	 * @param distanceToPlain - The distance of the camera (position) to the center point of the image-plain.
	 *
	 */
	public PinholeCamera(Point cameraPosition, Vec towardsVec, Vec upVec, double distanceToPlain) {
		this.cameraPosition = cameraPosition;
		this.centerPosition = new Ray(cameraPosition, towardsVec).add(distToPlain);
		this.towardsVec = towardsVec.normalize();
		this.upVec = upVec.normalize();
		this.rightVec = this.towardsVec.cross(upVec).normalize();
		this.distToPlain = distanceToPlain;
		this.viewWidth = 2;
		this.Rx = this.Ry = 200;
	}
	/**
	 * Initializes the resolution and width of the image.
	 * @param height - the number of pixels in the y direction.
	 * @param width - the number of pixels in the x direction.
	 * @param viewPlainWidth - the width of the image plain in world coordinates.
	 */
	public void initResolution(int height, int width, double viewPlainWidth) {
		this.Rx = width;
		this.Ry = height;
		this.viewWidth = viewPlainWidth;
	}

	/**
	 * Transforms from pixel coordinates to the center point of the corresponding pixel in model coordinates.
	 * @param x - the index of the x direction of the pixel.
	 * @param y - the index of the y direction of the pixel.
	 * @return the middle point of the pixel (x,y) in the model coordinates.
	 */
	public Point transform(int x, int y) {
		double pixelWidth = this.viewWidth / this.Rx;
		double pixelHeight = pixelWidth;
		double rightDistance = pixelWidth * (x - (int)(this.Rx / 2.0));
		double upDistance = pixelHeight * ((int)(this.Ry / 2.0) - y);
		Vec upMovement = this.upVec.mult(upDistance);
		Vec rightMovement = this.rightVec.mult(rightDistance);
		Point result = this.centerPosition.add(upMovement).add(rightMovement);
		return result;
	}

	/**
	 * Returns a copy of the camera position
	 * @return a "new" point representing the camera position.
	 */
	public Point getCameraPosition() {
		return new Point(this.cameraPosition.x, this.cameraPosition.y, this.cameraPosition.z);
	}
}
