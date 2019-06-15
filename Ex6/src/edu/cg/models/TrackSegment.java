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
	public final static double ASPHALT_TEXTURE_WIDTH = 20.0;
	public final static double ASPHALT_TEXTURE_DEPTH = 10.0;
	public final static double GRASS_TEXTURE_WIDTH = 10.0;
	public final static double GRASS_TEXTURE_DEPTH = 10.0;
	public final static double TRACK_LENGTH = 500.0;
	public final static double BOX_LENGTH = 1.5;
	private LinkedList<Point> boxesLocations; // Store the boxes centroids (center points) here.
	//		- Add wooden box model (you will only need one object which can be rendered many times)
	//      - Add grass and asphalt textures.
	private SkewedBox box = new SkewedBox(1.5, true);
	private Texture texRoad;
	private Texture texGrass;
	private double STRIP_DEPTH = 10;

	public void setDifficulty(double difficulty) {
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
		// Here by setting up the difficulty, we decide on the boxes locations.
		setDifficulty(difficulty);
	}

	@Override
	public void render(GL2 gl) {
		this.renderBoxes(gl);
		this.renderAsphalt(gl);
		this.renderGrass(gl);
	}

	// Render all boxes on the track
	private void renderBoxes(GL2 gl) {
		Materials.setWoodenBoxMaterial(gl);
		for (Point p : this.boxesLocations) {
			gl.glPushMatrix();
			gl.glTranslated(p.x, 0.0, p.z);
			this.box.render(gl);
			gl.glPopMatrix();
		}
	}

	// Render asphalt texture for the track
	private void renderAsphalt(GL2 gl) {
		Materials.setAsphaltMaterial(gl);
		gl.glPushMatrix();
		this.paintTexture(gl, this.texRoad, ASPHALT_TEXTURE_WIDTH, TRACK_LENGTH);
		gl.glPopMatrix();
	}

	// Render grass texture for both sides of the road
	private void renderGrass(GL2 gl) {
		Materials.setGreenMaterial(gl);
		double dx = 15.0;
		gl.glTranslated(dx, 0.0, 0.0);
		this.paintTexture(gl, this.texGrass, GRASS_TEXTURE_WIDTH, TRACK_LENGTH);
		gl.glTranslated(-2.0 * dx, 0.0, 0.0);
		this.paintTexture(gl, this.texGrass, GRASS_TEXTURE_WIDTH, TRACK_LENGTH);
		gl.glPopMatrix();
	}

	/**
	 * This method is a helper to paint a given texture, within the current track segment.

	 * @param gl - necessary opengl object for rendering
	 * @param texture - the given texture to be painted
	 * @param texture_width - the value of the texture width
	 * @param track_length - the track segment length
	 */
	private void paintTexture(GL2 gl, Texture texture, double texture_width, double track_length) {
		gl.glEnable(gl.GL_TEXTURE_2D);
		texture.bind(gl);
		gl.glTexEnvi(gl.GL_TEXTURE_ENV, gl.GL_TEXTURE_ENV_MODE, gl.GL_MODULATE);
		gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MIN_FILTER, gl.GL_LINEAR_MIPMAP_LINEAR);
		gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MAG_FILTER, gl.GL_LINEAR);
		gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MAX_LOD, 1);
		gl.glColor3d(1.0, 0.0, 0.0);

		GLU glu = new GLU();
		GLUquadric q = glu.gluNewQuadric();
		gl.glColor3d(1.0, 0.0, 0.0);
		gl.glNormal3d(0.0, 1.0, 0.0);
		gl.glTexCoord2d(0, 0);

		double current_depth = 0;
		while (current_depth < track_length){
			gl.glBegin(gl.GL_QUADS);
			gl.glTexCoord2d(0, 0);
			gl.glVertex3d(-texture_width / 2, 0.0, -current_depth);
			gl.glTexCoord2d(1, 0);
			gl.glVertex3d(texture_width / 2, 0.0, -current_depth);
			gl.glTexCoord2d(1, 1);
			gl.glVertex3d(texture_width / 2, 0.0, -(current_depth + 10));
			gl.glTexCoord2d(0, 1);
			gl.glVertex3d(-texture_width / 2, 0.0, -(current_depth + 10));
			gl.glEnd();
			//increase track current depth
			current_depth += STRIP_DEPTH;
		}
		glu.gluDeleteQuadric(q);
		gl.glDisable(gl.GL_TEXTURE_2D);
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
