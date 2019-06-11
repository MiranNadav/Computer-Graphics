package edu.cg;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import edu.cg.GameState;
import edu.cg.algebra.Vec;
import edu.cg.models.Car.F1Car;
import edu.cg.models.Track;
import java.awt.Component;

public class NeedForSpeed
		implements GLEventListener {
	private GameState gameState = null;
	private F1Car car = null;
	private Vec carCameraTranslation = null;
	private Track gameTrack = null;
	private FPSAnimator ani;
	private Component glPanel;
	private boolean isModelInitialized = false;
	private boolean isDayMode = true;

	public NeedForSpeed(Component glPanel) {
		this.glPanel = glPanel;
		this.gameState = new GameState();
		this.gameTrack = new Track();
		this.carCameraTranslation = new Vec(0.0);
		this.car = new F1Car();
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		if (!this.isModelInitialized) {
			this.initModel(gl);
		}
		if (this.isDayMode) {
			gl.glClearColor(0.52f, 0.824f, 1.0f, 1.0f);
		} else {
			gl.glClearColor(0.0f, 0.0f, 0.32f, 1.0f);
		}
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		this.updateCarCameraTranslation(gl);
		this.setupCamera(gl);
		this.setupLights(gl);
		this.renderCar(gl);
		this.renderTrack(gl);
	}

	private void updateCarCameraTranslation(GL2 gl) {
		Vec ret = this.gameState.getNextTranslation();
		this.carCameraTranslation = this.carCameraTranslation.add(ret);
		double dx = Math.max((double)this.carCameraTranslation.x, -7.0);
		this.carCameraTranslation.x = (float)Math.min(dx, 7.0);
		if ((double)Math.abs(this.carCameraTranslation.z) >= 510.0) {
			this.carCameraTranslation.z = -((float)((double)Math.abs(this.carCameraTranslation.z) % 500.0));
			this.gameTrack.changeTrack(gl);
		}
	}

	private void setupCamera(GL2 gl) {
		GLU glu = new GLU();
		glu.gluLookAt(this.carCameraTranslation.x, this.carCameraTranslation.y + 1.8, this.carCameraTranslation.z + 2,
				this.carCameraTranslation.x, this.carCameraTranslation.y + 1.5, this.carCameraTranslation.z - 5,
				0, 0.7, -0.3);
	}

	private void setupSun(GL2 gl, int light) {
		float[] color = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
		Vec dir = new Vec(0.0, 1.0, 1.0).normalize();
		float[] pos = new float[]{dir.x, dir.y, dir.z, 0.0f};
		gl.glLightfv(light, GL2.GL_SPECULAR, color, 0);
		gl.glLightfv(light, GL2.GL_DIFFUSE, color, 0);
		gl.glLightfv(light, GL2.GL_POSITION, pos, 0);
		gl.glLightfv(light, GL2.GL_AMBIENT, new float[]{0.1f, 0.1f, 0.1f, 1.0f}, 0);
		gl.glEnable(light);
	}

	private void setupMoon(GL2 gl) {
		gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, new float[]{0.15f, 0.15f, 0.18f, 1.0f}, 0);
		float[] pos1 = new float[]{0.0f + this.carCameraTranslation.x, 8.0f + this.carCameraTranslation.y, -0.0f + this.carCameraTranslation.z, 1.0f};
		this.setupSpotlight(gl, 16384, pos1);
		float[] pos2 = new float[]{0.0f + this.carCameraTranslation.x, 8.0f + this.carCameraTranslation.y, -15.0f + this.carCameraTranslation.z, 1.0f};
		this.setupSpotlight(gl, 16385, pos2);
	}

	private void setupSpotlight(GL2 gl, int light, float[] pos) {
		float[] sunColor = new float[]{0.85f, 0.85f, 0.85f, 1.0f};
		gl.glLightfv(light, gl.GL_POSITION, pos, 0);
		gl.glLightf(light, gl.GL_SPOT_CUTOFF, 75.0f);
		gl.glLightfv(light, gl.GL_SPOT_DIRECTION, new float[]{0.0f, -1.0f, 0.0f}, 0);
		gl.glLightfv(light, gl.GL_SPECULAR, sunColor, 0);
		gl.glLightfv(light, gl.GL_DIFFUSE, sunColor, 0);
		gl.glEnable(light);
	}

	private void setupLights(GL2 gl) {
		if (this.isDayMode) {
			gl.glDisable(gl.GL_LIGHT1);
			this.setupSun(gl, gl.GL_LIGHT0);
		} else {
			this.setupMoon(gl);
		}
	}

	private void renderTrack(GL2 gl) {
		gl.glPushMatrix();
		this.gameTrack.render(gl);
		gl.glPopMatrix();
	}

	private void renderCar(GL2 gl) {
		double carRotation = this.gameState.getCarRotation();
		gl.glPushMatrix();
		gl.glTranslated(0.0 + (double)this.carCameraTranslation.x, 0.15 + (double)this.carCameraTranslation.y, -6.6 + (double)this.carCameraTranslation.z);
		gl.glRotated(-carRotation, 0.0, 1.0, 0.0);
		gl.glRotated(90.0, 0.0, 0.1, 0.0);
		gl.glScaled(4.0, 4.0, 4.0);
		this.car.render(gl);
		gl.glPopMatrix();
	}

	public GameState getGameState() {
		return this.gameState;
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		this.ani = new FPSAnimator(30, true);
		this.ani.add(drawable);
		this.glPanel.repaint();
		this.initModel(gl);
		this.ani.start();
	}

	public void initModel(GL2 gl) {
		gl.glCullFace(gl.GL_BACK);
		gl.glEnable(gl.GL_CULL_FACE);
		gl.glEnable(gl.GL_NORMALIZE);
		gl.glEnable(gl.GL_DEPTH_TEST);
		gl.glEnable(gl.GL_LIGHTING);
		gl.glEnable(gl.GL_SMOOTH);
		this.car.init(gl);
		this.gameTrack.init(gl);
		this.isModelInitialized = true;
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();
		GLU glu = new GLU();
		double aspect = (double)width / (double)height;
		gl.glMatrixMode(gl.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(57.0, aspect, 2.0, 500.0);
	}

	public void startAnimation() {
		if (!this.ani.isAnimating()) {
			this.ani.start();
		}
	}

	public void stopAnimation() {
		if (this.ani.isAnimating()) {
			this.ani.stop();
		}
	}

	public void toggleNightMode() {
		this.isDayMode = !this.isDayMode;
	}
}

