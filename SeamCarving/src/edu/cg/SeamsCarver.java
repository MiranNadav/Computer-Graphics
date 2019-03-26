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
    private double[][] costMatrix;
    private double[][] energyMatrix;
    private int[][] offsetMatrix;
    private boolean[][] maskAfterSeamCarving;
    int[][] allSeamsList;
    private BufferedImage greyedImage;
    private BufferedImage gradientMagnitudeImage;
    int currentImageWidth;
    int currentImageHeight;
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

        allSeamsList = new int[numOfSeams][inHeight];
        currentImageWidth = inWidth;
        currentImageHeight = inHeight;
        minimalSeam = new int[inHeight];
        costMatrix = new double[inWidth][inHeight];
//		energyMatrix = new double[inWidth][inHeight];
        greyedImage = greyscale();
        gradientMagnitudeImage = calcMagnitude();
        energyMatrix = setEnergyMatrix();
        offsetMatrix = new int[inWidth][inHeight];

        setOffsetMatrix();
        calculateCostMatrix();
        findKSeams();

        this.logger.log("preliminary calculations were ended.");
    }

    private double[][] setEnergyMatrix() {
        setForEachWidth(currentImageWidth);
        setForEachHeight(currentImageHeight);
        double[][] resultMatrix = new double[currentImageWidth][currentImageHeight];
        forEach((y, x) -> {
            resultMatrix[x][y] = (new Color(gradientMagnitudeImage.getRGB(x, y)).getRed());
        });

        return resultMatrix;
    }

    public BufferedImage resize() {
        return resizeOp.resize();
    }

    private BufferedImage reduceImageWidth() {
        boolean[][] filter = new boolean[inWidth][inHeight];
        BufferedImage currentReducedImage = newEmptyImage(currentImageWidth, currentImageHeight);
        markPixels(filter);
        for (int i = 0; i < currentImageHeight; i++) {
            int col = 0;
            for (int j = 0; j < currentImageWidth; j++) {
                while (col < inWidth && filter[col][i]) {
                    col++;
                }
                currentReducedImage.setRGB(j, i, workingImage.getRGB(col, i));
                col++;
            }
        }

        return currentReducedImage;
    }

    private void markPixels(boolean[][] filter) {
        for (int i = 0; i < allSeamsList.length; i++) {
            for (int j = 0; j < allSeamsList[0].length; j++) {
                filter[allSeamsList[i][j]][j] = true;
            }
        }
    }

    private BufferedImage calcMagnitude() {
        BufferedImage tempImage = newEmptyImage(currentImageWidth, currentImageHeight);
        setForEachWidth(currentImageWidth);
        setForEachHeight(currentImageHeight);
        forEach((y, x) -> {
            int dx, dy;
            Color c = new Color(greyedImage.getRGB(x, y));
            Color cx = new Color(greyedImage.getRGB(x < currentImageWidth - 1 ? x + 1 : x - 1, y));
            Color cy = new Color(greyedImage.getRGB(x, y < currentImageHeight - 1 ? y + 1 : y - 1));

            dx = cx.getRed() - c.getRed();
            dy = cy.getRed() - c.getRed();
            int grad = (int) Math.sqrt((Math.pow(dx, 2) + Math.pow(dy, 2)) / 2);
            Color gradColor = new Color(grad, grad, grad);
            tempImage.setRGB(x, y, gradColor.getRGB());
        });

        return tempImage;
    }

    private void setOffsetMatrix() {
        for (int i = 0; i < inWidth; i++) {
            for (int j = 0; j < inHeight; j++) {
                offsetMatrix[i][j] = i;
            }
        }
    }

    private void calculateCostMatrix() {
        costMatrix = new double[currentImageWidth][currentImageHeight];
        setForEachHeight(currentImageHeight);
        setForEachWidth(currentImageWidth);

        forEach((y, x) -> {
            double CR, CV, CL;
            if (y == 0) {
                costMatrix[x][y] = energyMatrix[x][y];
            } else {
                //Calculation for cost matrix, taking care of special cases - left edge, right edge, or regular calculation.
                if (x == 0) {
                    CV = Math.abs(new Color(greyedImage.getRGB(x + 1, y)).getRed() - 0);
                    CR = CV + Math.abs(new Color(greyedImage.getRGB(x, y - 1)).getRed() - new Color(greyedImage.getRGB(x + 1, y)).getRed());
                    costMatrix[x][y] = energyMatrix[x][y] + Math.min(costMatrix[x][y - 1] + CV, costMatrix[x + 1][y - 1] + CR);

                } else if (x == currentImageWidth - 1) {
                    CV = Math.abs(0 - new Color(greyedImage.getRGB(x - 1, y)).getRed());
                    CL = CV + Math.abs(new Color(greyedImage.getRGB(x, y - 1)).getRed() - new Color(greyedImage.getRGB(x - 1, y)).getRed());
                    costMatrix[x][y] = energyMatrix[x][y] + Math.min(costMatrix[x][y - 1] + CV, costMatrix[x - 1][y - 1] + CL);
                } else {
                    CV = Math.abs(new Color(greyedImage.getRGB(x + 1, y)).getRed() - new Color(greyedImage.getRGB(x - 1, y)).getRed());
                    CL = CV + Math.abs(new Color(greyedImage.getRGB(x, y - 1)).getRed() - new Color(greyedImage.getRGB(x - 1, y)).getRed());
                    CR = CV + Math.abs(new Color(greyedImage.getRGB(x, y - 1)).getRed() - new Color(greyedImage.getRGB(x + 1, y)).getRed());
                    costMatrix[x][y] = energyMatrix[x][y] + Math.min(costMatrix[x][y - 1] + CV, Math.min(costMatrix[x - 1][y - 1] + CL, costMatrix[x + 1][y - 1] + CR));
                }

                //checking mask matrix.
                if (imageMask[y][x]) {
                    costMatrix[x][y] = Double.MAX_VALUE;
                }

            }
        });
    }

    private void findKSeams() {
        for (int i = 0; i < numOfSeams; i++) {
            findMinimalSeam(currentImageHeight - 1, 0, currentImageWidth - 1);
            addSeam(i);
            removeMinimalSeam();
            updateOffsetMatrix();

            //setting up new energy matrix
            energyMatrix = setEnergyMatrix();
            //calculating new offset matrix
            calculateCostMatrix();
        }
    }

    private void findMinimalSeam(int row, int minimumCol, int maximumCol) {
        if (row == -1) {
            return;
        }
        if (maximumCol > currentImageWidth - 1) {
            maximumCol = currentImageWidth - 1;
        }
        if (minimumCol < 0) {
            minimumCol = 0;
        }

        double minValue = Double.MAX_VALUE;
        int minXValueIndex = 0;

        for (int i = minimumCol; i <= maximumCol; i++) {
            if (costMatrix[i][row] < minValue) {
                minXValueIndex = i;
                minValue = costMatrix[i][row];
            }
        }
        minimalSeam[row] = minXValueIndex;
        findMinimalSeam(row - 1, minXValueIndex - 1, minXValueIndex + 1);
    }

    private void addSeam(int seamIndex) {
        for (int i = 0; i < minimalSeam.length; i++) {
            allSeamsList[seamIndex][i] = offsetMatrix[minimalSeam[i]][i];
        }
    }

    private void removeMinimalSeam() {
        currentImageWidth--;
        BufferedImage temporaryImage = newEmptyImage(currentImageWidth, currentImageHeight);
        for (int row = 0; row < currentImageHeight; row++) {
            for (int col = 0; col < currentImageWidth; col++) {
                int colToSet = col < minimalSeam[row] ? col : col + 1;
                temporaryImage.setRGB(col, row, greyedImage.getRGB(colToSet, row));
            }
        }
        greyedImage = temporaryImage;
        gradientMagnitudeImage = calcMagnitude();
    }

    private void updateOffsetMatrix() {
        int[][] tempOffsetMatrix = new int[currentImageWidth][currentImageHeight];
        for (int i = 0; i < currentImageHeight; i++) {
            int col = 0;
            for (int j = 0; j < currentImageWidth; j++) {
                if (minimalSeam[i] == j) {
                    col++;
                }
                tempOffsetMatrix[j][i] = offsetMatrix[col][i];
                col++;
            }
        }
        offsetMatrix = tempOffsetMatrix;
    }

    private BufferedImage increaseImageWidth() {
        boolean[][] filter = new boolean[inWidth][inHeight];
        BufferedImage resultedImage = newEmptyImage(inWidth + numOfSeams, inHeight);
        markPixels(filter);

        for (int i = 0; i < inHeight; i++) {
            int col = 0;
            for (int j = 0; j < inWidth + numOfSeams; j++) {
                if (filter[col][i]) {
                    resultedImage.setRGB(j, i, workingImage.getRGB(col, i));
                    resultedImage.setRGB(j + 1, i, workingImage.getRGB(col, i));
                    j++;
                } else {
                    resultedImage.setRGB(j, i, workingImage.getRGB(col, i));
                }
                col++;
            }
        }
        return resultedImage;
    }

    public BufferedImage showSeams(int seamColorRGB) {
        boolean[][] filter = new boolean[inWidth][inHeight];
        BufferedImage imageWithSeams = newEmptyImage(inWidth, inHeight);
        markPixels(filter);
        Color seamColor = new Color(seamColorRGB);

        forEach((y,x) -> {
            if (filter[x][y]){
                imageWithSeams.setRGB(x, y, seamColor.getRGB());
            } else {
                imageWithSeams.setRGB(x, y, workingImage.getRGB(x, y));

            }
        });
        return imageWithSeams;
    }

    public boolean[][] getMaskAfterSeamCarving() {
        boolean[][] newMask = new boolean[outHeight][outWidth];
        for (int y = 0; y < outHeight; y++) {
            for (int x = 0; x < outWidth; x++) {
                if (imageMask[y][offsetMatrix[x][y]]) {
                    newMask[y][x] = true;
                }
            }
        }
        return newMask;
    }
}
