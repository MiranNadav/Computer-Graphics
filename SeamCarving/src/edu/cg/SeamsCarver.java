package edu.cg;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SeamsCarver extends ImageProcessor {

	// MARK: An inner interface for functional programming.
	@FunctionalInterface
	interface ResizeOperation {
		BufferedImage resize();
	}

	// MARK: Fields
	private int numOfSeams;
	private ResizeOperation resizeOp;
	boolean[][] imageMask;
	// TODO: Add some additional fields
	private double[][] costMatrix;
	private double[][] energyMatrix;
	private int[][] offsetMatrix;
	private boolean[][] shiftedMask;
	private boolean[][] maskAfterSeamCarving;
	int[][] allSeams;
	private BufferedImage greyedImage;
	private BufferedImage gradientMagnitudeImage;
	int tempImageWidth;
	int tempImageHeight;
	int[] minimalSeam;

	public SeamsCarver(Logger logger, BufferedImage workingImage, int outWidth, RGBWeights rgbWeights,
			boolean[][] imageMask) {
		super((s) -> logger.log("Seam carving: " + s), workingImage, rgbWeights, outWidth, workingImage.getHeight());
		numOfSeams = Math.abs(outWidth - inWidth);
		this.imageMask = imageMask;
		if (inWidth < 2 | inHeight < 2)
			throw new RuntimeException("Can not apply seam carving: workingImage is too small");

		if (numOfSeams > inWidth / 2)
			throw new RuntimeException("Can not apply seam carving: too many seams...");

		// Setting resizeOp by with the appropriate method reference
		if (outWidth > inWidth)
			resizeOp = this::increaseImageWidth;
		else if (outWidth < inWidth)
			resizeOp = this::reduceImageWidth;
		else
			resizeOp = this::duplicateWorkingImage;

		// TODO: You may initialize your additional fields and apply some preliminary
		// calculations.

		allSeams = new int[numOfSeams][inHeight];
		tempImageWidth = inWidth;
		tempImageHeight = inHeight;
		minimalSeam = new int[inHeight];
		costMatrix = new double[inWidth][inHeight];
//		energyMatrix = new double[inWidth][inHeight];
		gradientMagnitudeImage = calcMagnitude();
		energyMatrix = setEnergyMatrix();
		offsetMatrix = new int [inWidth][inHeight];

		setOffsetMatrix();
		calcCostMatrix();
		findKSeams();

		this.logger.log("preliminary calculations were ended.");
	}

	private double[][] setEnergyMatrix() {
		setForEachWidth(tempImageWidth);
		setForEachHeight(tempImageHeight);
		double[][] resultMatrix = new double[tempImageWidth][tempImageHeight];
		forEach((y,x) -> {
			resultMatrix[x][y] = (new Color(gradientMagnitudeImage.getRGB(x,y)).getRed());
		});

		return resultMatrix;
	}

	public BufferedImage resize() {
		return resizeOp.resize();
	}

	private BufferedImage reduceImageWidth() {
		// 1. Create a matrix M (same size of pic)
		// 2. Calc gradient magnitude for each pixel
		// 3. Calc with dynamic programming the new formula (minimum of top 3 pixels)
		// 4. Backtrack
		// 5. Remove the Seam
		// 6. recalculate matrix M (neighbors of the removed pixels)

//		calcMagnitude();
//		calcDynamic();
//		System.out.println(costMatrix);
		// TODO: Implement this method, remove the exception.
		boolean[][] filter = new boolean[inWidth][inHeight];
		BufferedImage ans = newEmptyImage(tempImageWidth, tempImageHeight);
		markPixelsToRemove(filter);
		for (int i = 0; i < tempImageHeight; i++){
			int col = 0;
			for (int j = 0; j < tempImageWidth; j++){
				while (col < inWidth && filter[col][i]) {
					col++;
				}
				ans.setRGB(j, i, workingImage.getRGB(col, i));
				col++;
			}
		}
//		this.popForEachParameters();
//		setMaskAfterWidthReduce();
		return ans;
	}

	private void markPixelsToRemove(boolean[][] filter){
		for (int i = 0; i < allSeams.length; i++){
			for (int j = 0; j < allSeams[0].length; j++){
				filter[allSeams[i][j]][j] = true;
			}
		}
	}
	private BufferedImage calcMagnitude(){
		greyedImage = greyscale();
		BufferedImage tempImage = newEmptyImage(tempImageWidth, tempImageHeight);
		setForEachWidth(tempImageWidth);
		setForEachHeight(tempImageHeight);
		forEach((y,x) ->{
			int dx, dy;
			Color c = new Color(greyedImage.getRGB(x,y));
			Color cx = new Color(greyedImage.getRGB(x < tempImageWidth - 1? x + 1 : x - 1,y));
			Color cy = new Color(greyedImage.getRGB(x ,y < tempImageHeight - 1 ? y + 1 : y - 1));

			dx = cx.getRed() - c.getRed();
			dy = cy.getRed() - c.getRed();
			int grad = (int) Math.sqrt((Math.pow(dx,2) + Math.pow(dy,2))/2);
			Color gradColor = new Color(grad, grad, grad);
			tempImage.setRGB(x,y, gradColor.getRGB());
		});
		System.out.println();
		return tempImage;

	}

	private void setOffsetMatrix(){
		for (int i = 0; i < inWidth; i++){
			for (int j = 0; j < inHeight; j++){
				offsetMatrix[i][j] = i;
			}
		}
	}


	private void calcCostMatrix(){
		costMatrix = new double[tempImageWidth][tempImageHeight];
		setForEachHeight(tempImageHeight);
		setForEachWidth(tempImageWidth);

		forEach((y, x) -> {
			if (y == 0) { //first row cost matrix value are the pixel's energy.
				costMatrix[x][y] = energyMatrix[x][y];
			}
			else {
				if (x == 0){ //when calculating cost matrix at left most edge
					long cV = Math.abs(new Color(greyedImage.getRGB(x + 1,y)).getRed() - 0);
					long cR = cV + Math.abs(new Color(greyedImage.getRGB(x,y-1)).getRed() - new Color(greyedImage.getRGB(x + 1,y)).getRed());

					costMatrix[x][y] = energyMatrix[x][y] + Math.min(costMatrix[x][y-1] + cV , costMatrix[x+1][y-1] + cR);

				} else if (x == tempImageWidth - 1) { //when calculating cost matrix at right most edge
					long cV = Math.abs(0 - new Color(greyedImage.getRGB(x -1 ,y)).getRed());
					long cL = cV + Math.abs(new Color(greyedImage.getRGB(x,y-1)).getRed() - new Color(greyedImage.getRGB(x - 1,y)).getRed());

					costMatrix[x][y] = energyMatrix[x][y] + Math.min(costMatrix[x][y-1] + cV , costMatrix[x-1][y-1] + cL);
				} else {
					long cV = Math.abs(new Color(greyedImage.getRGB(x + 1,y)).getRed() - new Color(greyedImage.getRGB(x -1 ,y)).getRed());
					long cL = cV + Math.abs(new Color(greyedImage.getRGB(x,y-1)).getRed() - new Color(greyedImage.getRGB(x - 1,y)).getRed());
					long cR = cV + Math.abs(new Color(greyedImage.getRGB(x,y-1)).getRed() - new Color(greyedImage.getRGB(x + 1,y)).getRed());

					costMatrix[x][y] = energyMatrix[x][y] + Math.min(costMatrix[x][y-1] + cV , Math.min(costMatrix[x-1][y-1] + cL , costMatrix[x+1][y-1] + cR));
				}

			}
		});
	}

	private void findKSeams(){
		for(int i = 0; i < numOfSeams; i++){
			findMinSeam(tempImageHeight - 1, 0, tempImageWidth -1);
			addSeam(i);
			removeMinSeam();
			updateOffsetMatrix();

			//setting up new energy matrix
			energyMatrix = setEnergyMatrix();
			//calculating new offset matrix
			calcCostMatrix();
		}
	}

	/**
	 * finds the minimum seam in the temporary image recursively.
	 * @param row the image row in which to find the minimal pixel
	 * @param minCol left most column of pixel bound to find minimum pixel from
	 * @param maxCol right most column of pixel bound to find minimum pixel from
	 */
	private void findMinSeam(int row, int minCol, int maxCol){
		if (row == -1){
			return;
		}
		if (minCol < 0) {
			minCol = 0;
		}
		if (maxCol > tempImageWidth - 1){
			maxCol = tempImageWidth - 1;
		}

		double minValue = Double.MAX_VALUE;
		int minXValueIndex = 0;

		for(int i = minCol; i <= maxCol; i++){
			if (costMatrix[i][row] < minValue){
				minXValueIndex = i;
				minValue = costMatrix[i][row];
			}
		}
		minimalSeam[row] = minXValueIndex;
		findMinSeam(row - 1, minXValueIndex - 1, minXValueIndex + 1); //searching recursively
	}

	private void addSeam(int seamIndex){
		for (int i = 0; i < minimalSeam.length; i++){
			allSeams[seamIndex][i] = offsetMatrix[minimalSeam[i]][i];
		}
	}

	private void removeMinSeam(){
		tempImageWidth--;
		BufferedImage temporaryImage = newEmptyImage(tempImageWidth, tempImageHeight);
		for(int row = 0; row < tempImageHeight; row++){
			for (int col = 0; col < tempImageWidth; col++){

				if (col < minimalSeam[row]){
					temporaryImage.setRGB(col, row, greyedImage.getRGB(col, row));
				} else {
					temporaryImage.setRGB(col, row, greyedImage.getRGB(col + 1, row));
				}
			}
		}
		greyedImage = temporaryImage;
		gradientMagnitudeImage = calcMagnitude();
	}

	private void updateOffsetMatrix(){
		int[][] tempMatrix = new int[tempImageWidth][tempImageHeight];
		for (int i = 0; i < tempImageHeight; i++){
			int col = 0;
			for (int j = 0; j < tempImageWidth; j++){
				if (minimalSeam[i] == j){
					col++;
				}
				tempMatrix[j][i] = offsetMatrix[col][i];
				col++;
			}
		}
		offsetMatrix = tempMatrix;
	}

	private BufferedImage increaseImageWidth() {
		// TODO: Implement this method, remove the exception.
		throw new UnimplementedMethodException("increaseImageWidth");
	}

	public BufferedImage showSeams(int seamColorRGB) {
		boolean[][] filter = new boolean[inWidth][inHeight];
		BufferedImage ans = newEmptyImage(inWidth, inHeight);
		markPixelsToRemove(filter);
		Color seamColor = new Color(seamColorRGB);
		for (int i = 0; i < inHeight; i++){
			for (int j = 0; j < inWidth; j++){
				if (filter[j][i]){
					ans.setRGB(j, i, seamColor.getRGB());
				} else {
					ans.setRGB(j, i, workingImage.getRGB(j, i));
				}
			}
		}
		return ans;
	}


	private void setMaskAfterWidthReduce() {
		this.maskAfterSeamCarving = new boolean[this.inHeight][this.outWidth];
		this.pushForEachParameters();
		this.setForEachWidth(this.outWidth);
		this.forEach((y, x) -> {
			this.maskAfterSeamCarving[y.intValue()][x.intValue()] = this.shiftedMask[y][x];
		});
		this.popForEachParameters();
	}

	private boolean[][] duplicateWorkingMask() {
		boolean[][] res = new boolean[this.inHeight][this.inWidth];
		this.forEach((y, x) -> {
			res[y.intValue()][x.intValue()] = this.imageMask[y][x];
		});
		return res;
	}
	public boolean[][] getMaskAfterSeamCarving() {
		// This method should return the mask of the resize image after seam carving. Meaning,
		// after applying Seam Carving on the input image, getMaskAfterSeamCarving() will return
		// a mask, with the same dimensions as the resized image, where the mask values match the
		// original mask values for the corresponding pixels.
		// HINT:
		// Once you remove (replicate) the chosen seams from the input image, you need to also
		// remove (replicate) the matching entries from the mask as well.
		return this.maskAfterSeamCarving != null ? this.maskAfterSeamCarving : this.duplicateWorkingMask();

	}
}
