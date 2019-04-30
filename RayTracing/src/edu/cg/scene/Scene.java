package edu.cg.scene;

import edu.cg.Logger;
import edu.cg.algebra.Hit;
import edu.cg.algebra.Ops;
import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;
import edu.cg.scene.camera.PinholeCamera;
import edu.cg.scene.lightSources.Light;
import edu.cg.scene.objects.Surface;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Scene {
    private String name = "scene";
    private int maxRecursionLevel = 1;
    private int antiAliasingFactor = 1;
    private boolean renderRefarctions = false;
    private boolean renderReflections = false;
    private PinholeCamera camera;
    private Vec ambient = new Vec(1.0, 1.0, 1.0);
    private Vec backgroundColor = new Vec(0.0, 0.5, 1.0);
    private List<Light> lightSources = new LinkedList<Light>();
    private List<Surface> surfaces = new LinkedList<Surface>();
    private transient ExecutorService executor = null;
    private transient Logger logger = null;

    public Scene initCamera(Point eyePoistion, Vec towardsVec, Vec upVec, double distanceToPlain) {
        this.camera = new PinholeCamera(eyePoistion, towardsVec, upVec, distanceToPlain);
        return this;
    }

    public Scene initAmbient(Vec ambient) {
        this.ambient = ambient;
        return this;
    }

    public Scene initBackgroundColor(Vec backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public Scene addLightSource(Light lightSource) {
        this.lightSources.add(lightSource);
        return this;
    }

    public Scene addSurface(Surface surface) {
        this.surfaces.add(surface);
        return this;
    }

    public Scene initMaxRecursionLevel(int maxRecursionLevel) {
        this.maxRecursionLevel = maxRecursionLevel;
        return this;
    }

    public Scene initAntiAliasingFactor(int antiAliasingFactor) {
        this.antiAliasingFactor = antiAliasingFactor;
        return this;
    }

    public Scene initName(String name) {
        this.name = name;
        return this;
    }

    public Scene initRenderRefarctions(boolean renderRefarctions) {
        this.renderRefarctions = renderRefarctions;
        return this;
    }

    public Scene initRenderReflections(boolean renderReflections) {
        this.renderReflections = renderReflections;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public int getFactor() {
        return this.antiAliasingFactor;
    }

    public int getMaxRecursionLevel() {
        return this.maxRecursionLevel;
    }

    public boolean getRenderRefarctions() {
        return this.renderRefarctions;
    }

    public boolean getRenderReflections() {
        return this.renderReflections;
    }

    public String toString() {
        String endl = System.lineSeparator();
        return "Camera: " + this.camera + endl + "Ambient: " + this.ambient + endl + "Background Color: " + this.backgroundColor + endl + "Max recursion level: " + this.maxRecursionLevel + endl + "Anti aliasing factor: " + this.antiAliasingFactor + endl + "Light sources:" + endl + this.lightSources + endl + "Surfaces:" + endl + this.surfaces;
    }

    private void initSomeFields(int imgWidth, int imgHeight, Logger logger) {
        this.logger = logger;
    }

    public BufferedImage render(int imgWidth, int imgHeight, double viewPlainWidth, Logger logger) throws InterruptedException, ExecutionException {
        int y;
        int x;
        this.initSomeFields(imgWidth, imgHeight, logger);
        BufferedImage img = new BufferedImage(imgWidth, imgHeight, 1);
        this.camera.initResolution(imgHeight, imgWidth, viewPlainWidth);
        int nThreads = Runtime.getRuntime().availableProcessors();
        nThreads = nThreads < 2 ? 2 : nThreads;
        this.logger.log("Intitialize executor. Using " + nThreads + " threads to render " + this.name);
        this.executor = Executors.newFixedThreadPool(nThreads);
        Future[][] futures = new Future[imgHeight][imgWidth];
        this.logger.log("Starting to shoot " + imgHeight * imgWidth * this.antiAliasingFactor * this.antiAliasingFactor + " rays over " + this.name);
        for (y = 0; y < imgHeight; ++y) {
            for (x = 0; x < imgWidth; ++x) {
                futures[y][x] = this.calcColor(x, y);
            }
        }
        this.logger.log("Done shooting rays.");
        this.logger.log("Wating for results...");
        for (y = 0; y < imgHeight; ++y) {
            for (x = 0; x < imgWidth; ++x) {
                Color color = (Color)futures[y][x].get();
                img.setRGB(x, y, color.getRGB());
            }
        }

        this.executor.shutdown();
        this.logger.log("Ray tracing of " + this.name + " has been completed.");
        this.executor = null;
        this.logger = null;
        return img;

    }


    private Future<Color> calcColor(int x, int y) {
        return this.executor.submit(() -> {
            Point topLeftPoint = this.camera.transform(x, y);
            Point buttomRightPoint = this.camera.transform(x + 1, y + 1);
            Vec currentColor = new Vec();
            for (int i = 0; i < this.antiAliasingFactor; i++) {
                for (int j = 0; j < this.antiAliasingFactor; j++) {
                    Point leftUpWeight = calcLeftUpWeight(j,i);
                    Point rightDownWeight = calcRightDownWeight(j,i);
                    Point pointInSurface = Ops.add(buttomRightPoint.mult(rightDownWeight), topLeftPoint.mult(leftUpWeight));
                    Ray currentRay = new Ray(this.camera.getCameraPosition(), pointInSurface);
                    currentColor = currentColor.add(this.calcColor(currentRay, 0));
                }
            }
            Color colorFound = currentColor.mult(1 / (Math.pow(this.antiAliasingFactor,2))).toColor();
            return colorFound;
        });
    }

    private Point calcRightDownWeight(int j, int i) {
        return new Point (j,i,0.0).mult(1.0 / this.antiAliasingFactor);
    }

    private Point calcLeftUpWeight(int j, int i){
        return new Point(this.antiAliasingFactor - j , this.antiAliasingFactor - i, this.antiAliasingFactor)
                .mult(1.0/antiAliasingFactor);
    }

    private Vec calcColor(Ray ray, int recursionLevel) {
        if (recursionLevel >= this.maxRecursionLevel) {
            return new Vec();
        }

        Hit minimalHit = this.intersect(ray);
        if (minimalHit == null) {
            return this.backgroundColor;
        }

        Surface surface = minimalHit.getSurface();
        Point hittingPoint = ray.getHittingPoint(minimalHit);
        Vec color = surface.Ka().mult(this.ambient);
        Vec temporaryColor;
        Vec intensity;
        Ray rayToLight;

        for (Light light : this.lightSources) {
            rayToLight = light.rayToLight(hittingPoint);
            if (!this.isLightOccluded(light, rayToLight)){
                temporaryColor = this.colorDiffuse(minimalHit, rayToLight);
                temporaryColor = temporaryColor.add(this.colorSpecular(minimalHit, rayToLight, ray));
                intensity = light.intensity(hittingPoint, rayToLight);
                color = color.add(temporaryColor.mult(intensity));
            }
        }

        // Handle Refractions
        if (this.renderRefarctions) {
            color = handleRefraction(ray, recursionLevel, minimalHit, surface, hittingPoint, color);
        }

        // Handle Reflections
        if (this.renderReflections) {
            color = handleReflection(ray, recursionLevel, minimalHit, surface, hittingPoint, color);
        }

        return color;
    }

    private Vec handleReflection(Ray ray, int recursionLevel, Hit minimalHit, Surface surface, Point hittingPoint, Vec color) {
        Vec reflectDirection = Ops.reflect(ray.direction(), minimalHit.getNormalToSurface());
        Vec reflectIntensity = new Vec(surface.reflectionIntensity());
        Vec reflectColor = this.calcColor(new Ray(hittingPoint, reflectDirection), recursionLevel + 1).mult(reflectIntensity);
        color = color.add(reflectColor);
        return color;
    }

    private Vec handleRefraction(Ray ray, int recursionLevel, Hit minimalHit, Surface surface, Point hittingPoint, Vec color) {
        Vec refractColor;
        if (surface.isTransparent()) {
            double n1 = surface.n1(minimalHit);
            double n2 = surface.n2(minimalHit);
            Vec refractDirection = Ops.refract(ray.direction(), minimalHit.getNormalToSurface(), n1, n2);
            Vec refractWeight = new Vec(surface.refractionIntensity());
            refractColor = this.calcColor(new Ray(hittingPoint, refractDirection), recursionLevel + 1).mult(refractWeight);
            color = color.add(refractColor);
        }
        return color;
    }

    private Vec colorDiffuse(Hit minHit, Ray rayToLight) {
        Vec normalVector = minHit.getNormalToSurface();
        Vec lightVector = rayToLight.direction();
        Vec Kd = minHit.getSurface().Kd();
        return Kd.mult(Math.max(normalVector.dot(lightVector), 0.0));
    }

    private Vec colorSpecular(Hit minHit, Ray rayToLight, Ray rayFromViewer) {
        Vec normalVector = minHit.getNormalToSurface();
        Vec lightVector = rayToLight.direction();
        Vec Ks = minHit.getSurface().Ks();
        Vec v = rayFromViewer.direction();
        Vec reflection = Ops.reflect(lightVector.neg(), normalVector);
        int shine = minHit.getSurface().shininess();
        double dot = reflection.dot(v.neg());
        if (dot < 0){
            return new Vec();
        } else {
            return Ks.mult(Math.pow(dot, shine));
        }
    }

    private boolean isLightOccluded(Light light, Ray rayToLight) {
        for (Surface surface : this.surfaces) {
            if (light.isOccludedBy(surface, rayToLight)){
                return true;
            }
        }
        return false;
    }

    private Hit intersect(Ray ray) {
        Hit minimalHit = null;
        for (Surface surface : this.surfaces) {
            Hit newHit = surface.intersect(ray);
            if (minimalHit == null  || (newHit != null && newHit.compareTo(minimalHit) < 0)){
                minimalHit = newHit;
            }
        }
        return minimalHit;
    }
}