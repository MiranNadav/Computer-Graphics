package edu.cg.models;

import java.io.File;
import java.util.LinkedList;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import edu.cg.algebra.Point;
import edu.cg.models.Car.Materials;

public class TrackSegment implements IRenderable {
	// TODO: Some constants you can use
	public final static double ASPHALT_TEXTURE_WIDTH = 20.0;
	public final static double ASPHALT_TEXTURE_DEPTH = 10.0;
	public final static double GRASS_TEXTURE_WIDTH = 10.0;
	public final static double GRASS_TEXTURE_DEPTH = 10.0;
	public final static double TRACK_LENGTH = 500.0;
	public final static double BOX_LENGTH = 1.5;
	private LinkedList<Point> boxesLocations; // Store the boxes centroids (center points) here.
	// TODO Add additional fields, for example:
	//		- Add wooden box model (you will only need one object which can be rendered many times)
	//      - Add grass and asphalt textures.
	private SkewedBox box = new SkewedBox(1.5, true);
	private Texture texRoad;
	private Texture texGrass;

	public void setDifficulty(double difficulty) {
		// TODO: Set the difficulty of the track segment. Here you decide what are the boxes locations.
		//		 We provide a simple implementation. You can change it if you want. But if you do decide to use it, then it is your responsibility to understand the logic behind it.
		//       Note: In our implementation, the difficulty is the probability of a box to appear in the scene. 
		//             We divide the scene into rows of boxes and we sample boxes according the difficulty probability.
		difficulty = Math.min(difficulty, 0.95);
		difficulty = Math.max(difficulty, 0.05);
		double numberOfLanes = 4.0;
		double deltaZ = 0.0;
		if (difficulty < 0.25) {
			deltaZ = 100.0;
		} else if (difficulty < 0.5) {
			deltaZ = 75.0;
		} else {
			deltaZ = 50.0;
		}
		boxesLocations = new LinkedList<Point>();
		for (double dz = deltaZ; dz < TRACK_LENGTH - BOX_LENGTH / 2.0; dz += deltaZ) {
			int cnt = 0; // Number of boxes sampled at each row.
			boolean flag = false;
			for (int i = 0; i < 12; i++) {
				double dx = -((double) numberOfLanes / 2.0) * ((ASPHALT_TEXTURE_WIDTH - 2.0) / numberOfLanes) + BOX_LENGTH / 2.0
						+ i * BOX_LENGTH;
				if (Math.random() < difficulty) {
					boxesLocations.add(new Point(dx, BOX_LENGTH / 2.0, -dz));
					cnt += 1;
				} else if (!flag) {// The first time we don't sample a box then we also don't sample the box next to. We want enough space for the car to pass through. 
					i += 1;
					flag = true;
				}
				if (cnt > difficulty * 10) {
					break;
				}
			}
		}
	}

	public TrackSegment(double difficulty) {
		// TODO initialize your fields here.
		// Here by setting up the difficulty, we decide on the boxes locations.
		setDifficulty(difficulty);
	}

	@Override
	public void render(GL2 gl) {
		this.renderBoxes(gl);
		this.renderAsphalt(gl);
		this.renderGrass(gl);
	}

	private void renderBoxes(GL2 gl) {
		Materials.setWoodenBoxMaterial(gl);
		for (Point p : this.boxesLocations) {
			gl.glPushMatrix();
			gl.glTranslated(p.x, 0.0, p.z);
			this.box.render(gl);
			gl.glPopMatrix();
		}
	}

	private void renderAsphalt(GL2 gl) {
		Materials.setAsphaltMaterial(gl);
		gl.glPushMatrix();
		this.renderQuadraticTexture(gl, this.texRoad, 20.0, 10.0, 6, 500.0);
		gl.glPopMatrix();
	}

	private void renderGrass(GL2 gl) {
		Materials.setGreenMaterial(gl);
		double dx = 15.0;
		gl.glTranslated(dx, 0.0, 0.0);
		this.renderQuadraticTexture(gl, this.texGrass, 10.0, 10.0, 2, 500.0);
		gl.glTranslated(-2.0 * dx, 0.0, 0.0);
		this.renderQuadraticTexture(gl, this.texGrass, 10.0, 10.0, 2, 500.0);
		gl.glPopMatrix();
	}

	private void renderQuadraticTexture(GL2 gl, Texture tex, double quadWidth, double quadDepth, int split, double totalDepth) {
		double splitInDouble = (double)split;
		gl.glEnable(GL2.GL_TEXTURE_2D);
		tex.bind(gl);
		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR_MIPMAP_LINEAR);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAX_LOD, 1);
		gl.glColor3d(1.0, 0.0, 0.0);
		GLU glu = new GLU();
		GLUquadric quad = glu.gluNewQuadric();
		gl.glColor3d(1.0, 0.0, 0.0);
		gl.glNormal3d(0.0, 1.0, 0.0);
		double d = 1.0 / splitInDouble;
		double dz = quadDepth / splitInDouble;
		double dx = quadWidth / splitInDouble;
		double tz = 0;
		while (tz < totalDepth) {
			for (double i = 0.0; i < splitInDouble; i++) {
				gl.glBegin(5);
				for (double j = 0.0; j <= splitInDouble; j++) {
					gl.glTexCoord2d(d * j, d * (i + 1));
					gl.glVertex3d(dx * j - quadWidth / 2.0 , 0.0, -(tz + (i + 1) * dz));
					gl.glTexCoord2d(d * j, d * i);
					gl.glVertex3d(dx * j - quadWidth / 2.0, 0.0, -(tz + i * dz));
				}
				gl.glEnd();
			}
			tz += quadDepth;
		}
		glu.gluDeleteQuadric(quad);
		gl.glDisable(GL2.GL_TEXTURE_2D);
	}

	@Override
	public void init(GL2 gl) {
		this.box.init(gl);
		try {
			this.texRoad = TextureIO.newTexture(new File("Textures/RoadTexture.jpg"), true);
			this.texGrass = TextureIO.newTexture(new File("Textures/GrassTexture.jpg"), true);
		}
		catch (Exception e) {
			System.err.print("Unable to read texture : " + e.getMessage());
		}
	}

	@Override
	public void destroy(GL2 gl) {
		this.texRoad.destroy(gl);
		this.texGrass.destroy(gl);
		this.box.destroy(gl);
	}

}
