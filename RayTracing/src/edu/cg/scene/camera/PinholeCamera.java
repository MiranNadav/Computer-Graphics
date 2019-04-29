package edu.cg.scene.camera;

import edu.cg.UnimplementedMethodException;
import edu.cg.algebra.Point;
import edu.cg.algebra.Vec;
import edu.cg.algebra.Ray;

public class PinholeCamera {
	Point cameraPosition;
	Point centerPoint;
	Vec towardsVec;
	Vec upVec;
	Vec rightVec;
	double distanceToPlain;
	double viewWidth;
	double Rx;
	double Ry;


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
		this.distanceToPlain = distanceToPlain;
		this.towardsVec = towardsVec.normalize();
		this.upVec = upVec.normalize();
		this.rightVec = this.towardsVec.cross(upVec).normalize();
		this.centerPoint = new Ray(cameraPosition, towardsVec).add(distanceToPlain);
	}


	/**
	 * Initializes the resolution and width of the image.
	 * @param height - the number of pixels in the y direction.
	 * @param width - the number of pixels in the x direction.
	 * @param viewPlainWidth - the width of the image plain in world coordinates.
	 */
	public void initResolution(int height, int width, double viewPlainWidth) {
		this.viewWidth = viewPlainWidth;
		this.Rx = width;
		this.Ry = height;
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
		double rightDist = pixelWidth * (x - (int)(this.Rx / 2.0));
		double upDist = pixelHeight * ((int)(this.Ry / 2.0) - y);
		Vec upVector = this.upVec.mult(upDist);
		Vec rightVector = this.rightVec.mult(rightDist);
		Point result = this.centerPoint.add(upVector).add(rightVector);
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
