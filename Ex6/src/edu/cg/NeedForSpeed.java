package edu.cg;

import java.awt.Component;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;

import edu.cg.algebra.Vec;
import edu.cg.models.Track;
import edu.cg.models.TrackSegment;
import edu.cg.models.Car.F1Car;
//import edu.cg.models.Car.Specification;

/**
 * An OpenGL 3D Game.
 *
 */
public class NeedForSpeed implements GLEventListener {
	private GameState gameState = null; // Tracks the car movement and orientation
	private F1Car car = null; // The F1 car we want to render
	private Vec carCameraTranslation = null; // The accumulated translation that should be applied on the car, camera
	// and light sources
	private Track gameTrack = null; // The game track we want to render
	private FPSAnimator ani; // This object is responsible to redraw the model with a constant FPS
	private Component glPanel; // The canvas we draw on.
	private boolean isModelInitialized = false; // Whether model.init() was called.
	private boolean isDayMode = true; // Indicates whether the lighting mode is day/night.
	// TODO: add fields as you want. For example:
	// - Car initial position (should be fixed).
	// - Camera initial position (should be fixed)

	public NeedForSpeed(Component glPanel) {
		this.glPanel = glPanel;
		gameState = new GameState();
		gameTrack = new Track();
		carCameraTranslation = new Vec(0.0);
		car = new F1Car();
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		if (!isModelInitialized) {
			initModel(gl);
		}
		if (isDayMode) {
			// TODO: Setup background color when day mode is on (You can choose differnt color)
			gl.glClearColor(0.52f, 0.824f, 1.0f, 1.0f);
		} else {
			// TODO: Setup background color when night mode is on (You can choose differnt color)
			gl.glClearColor(0.0f, 0.0f, 0.32f, 1.0f);
		}
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		// TODO: This is the flow in which we render the scene. You can use different flow.
		// Step (1) You should update the accumulated translation that needs to be
		// applied on the car, camera and light sources.
		updateCarCameraTranslation(gl);
		// Step (2) Position the camera and setup its orientation
		setupCamera(gl);
		// Step (3) setup the lighting.
		setupLights(gl);
		// Step (4) render the car.
		renderCar(gl);
		// Step (5) render the track.
		renderTrack(gl);
	}

	private void updateCarCameraTranslation(GL2 gl) {
		// TODO: Update the car and camera translation values (not the
		// ModelView-Matrix).
		// - You should always keep track on the car offset relative to the starting
		// point.
		// - You should change the track segments here.
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
		// TODO: Setup the camera.
		GLU glu = new GLU();
		glu.gluLookAt(0.0 + (double)this.carCameraTranslation.x, 1.8 + (double)this.carCameraTranslation.y, 2.0 + (double)this.carCameraTranslation.z, 0.0 + (double)this.carCameraTranslation.x, 1.5 + (double)this.carCameraTranslation.y, -5.0 + (double)this.carCameraTranslation.z, 0.0, 0.7, -0.3);
	}


	private void setupLights(GL2 gl) {
		if (isDayMode) {
			// TODO Setup day lighting.
			// * Remember: switch-off any light sources that were used in night mode
			gl.glDisable(16385);
			this.setupSun(gl, 16384);
		} else {
			// TODO Setup night lighting.
			// * Remember: switch-off any light sources that are used in day mode
			// * Remember: spotlight sources also move with the camera.
			this.setupMoon(gl);
			float[] pos1 = new float[]{0.0f + this.carCameraTranslation.x, 8.0f + this.carCameraTranslation.y, -0.0f + this.carCameraTranslation.z, 1.0f};
			this.setupSpotlight(gl, 16384, pos1);
			float[] pos2 = new float[]{0.0f + this.carCameraTranslation.x, 8.0f + this.carCameraTranslation.y, -15.0f + this.carCameraTranslation.z, 1.0f};
			this.setupSpotlight(gl, 16385, pos2);
		}
	}

	private void setupSun(GL2 gl, int light) {
		float[] sunColor = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
		Vec dir = new Vec(0.0, 1.0, 1.0).normalize();
		float[] pos = new float[]{dir.x, dir.y, dir.z, 0.0f};
		gl.glLightfv(light, 4610, sunColor, 0);
		gl.glLightfv(light, 4609, sunColor, 0);
		gl.glLightfv(light, 4611, pos, 0);
		gl.glLightfv(light, 4608, new float[]{0.1f, 0.1f, 0.1f, 1.0f}, 0);
		gl.glEnable(light);
	}

	private void setupMoon(GL2 gl) {
		gl.glLightModelfv(2899, new float[]{0.15f, 0.15f, 0.18f, 1.0f}, 0);
	}

	private void setupSpotlight(GL2 gl, int light, float[] pos) {
		float[] sunColor = new float[]{0.85f, 0.85f, 0.85f, 1.0f};
		gl.glLightfv(light, 4611, pos, 0);
		gl.glLightf(light, 4614, 75.0f);
		gl.glLightfv(light, 4612, new float[]{0.0f, -1.0f, 0.0f}, 0);
		gl.glLightfv(light, 4610, sunColor, 0);
		gl.glLightfv(light, 4609, sunColor, 0);
		gl.glEnable(light);
	}

	private void renderTrack(GL2 gl) {
		// TODO: Render the track.
		//       * Note: the track shouldn't be translated. It should be fixed.
		gl.glPushMatrix();
		this.gameTrack.render(gl);
		gl.glPopMatrix();
	}

	private void renderCar(GL2 gl) {
		// TODO: Render the car.
		//       * Remember: the car position should be the initial position + the accumulated translation.
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
		return gameState;
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO: Destroy all models.
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		// Initialize display callback timer
		ani = new FPSAnimator(30, true);
		ani.add(drawable);
		glPanel.repaint();

		initModel(gl);
		ani.start();
	}

	public void initModel(GL2 gl) {
		// TODO: You can change OpenGL modes during implementation for debug purposes.
		//		 Remember to change OpenGL mode as it was before.
		gl.glCullFace(GL2.GL_BACK);
		gl.glEnable(GL2.GL_CULL_FACE);

		gl.glEnable(GL2.GL_NORMALIZE);
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glEnable(GL2.GL_SMOOTH);

		car.init(gl);
		gameTrack.init(gl);
		isModelInitialized = true;
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		// TODO Setup the projection matrix here.
		//		- It is recommended to use gluPerspective - with fovy 57.0
		GL2 gl = drawable.getGL().getGL2();
		GLU glu = new GLU();
		double aspect = (double)width / (double)height;
		gl.glMatrixMode(5889);
		gl.glLoadIdentity();
		glu.gluPerspective(57.0, aspect, 2.0, 500.0);
	}

	/**
	 * Start redrawing the scene with 30 FPS
	 */
	public void startAnimation() {
		if (!ani.isAnimating())
			ani.start();
	}

	/**
	 * Stop redrawing the scene with 30 FPS
	 */
	public void stopAnimation() {
		if (ani.isAnimating())
			ani.stop();
	}

	public void toggleNightMode() {
		isDayMode = !isDayMode;
	}

}
