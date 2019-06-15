package edu.cg.models.Car;

import com.jogamp.opengl.GL2;

public class Materials {
	private static final float DARK_GRAY[] = { 0.2f, 0.2f, 0.2f };
	private static final float DARK_RED[] = { 0.25f, 0.01f, 0.01f };
	private static final float RED[] = { 0.7f, 0f, 0f };
	private static final float BLACK[] = { 0.05f, 0.05f, 0.05f };

	public static void SetMetalMaterial(GL2 gl, float[] color) {
		gl.glColor3fv(color, 0);
	}

	public static void SetBlackMetalMaterial(GL2 gl) {
		SetMetalMaterial(gl, BLACK);
	}

	public static void SetRedMetalMaterial(GL2 gl) {
		SetMetalMaterial(gl, RED);
	}


	public static void SetDarkRedMetalMaterial(GL2 gl) {
		SetMetalMaterial(gl, DARK_RED);
	}

	public static void SetDarkGreyMetalMaterial(GL2 gl) {
		SetMetalMaterial(gl, DARK_GRAY);
	}

	public static void setMaterialTire(GL2 gl) {
		float col[] = { .05f, .05f, .05f };
		gl.glColor3fv(col,0);
	}

	public static void setMaterialRims(GL2 gl) {
		gl.glColor3fv(DARK_GRAY,0);
	}

	public static void setGreenMaterial(GL2 gl) {
		float[] mat_ambient = new float[]{0.1f, 0.2f, 0.1f, 1.0f};
		float[] mat_diffuse = new float[]{0.1f, 0.6f, 0.1f, 1.0f};
		float[] mat_specular = new float[]{0.6f, 0.7f, 0.6f, 1.0f};
		float shine = 100f;
		gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, shine);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, mat_ambient, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, mat_diffuse, 0);

		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, mat_specular, 0);
	}

	public static void setAsphaltMaterial(GL2 gl) {
		float[] mat_ambient = new float[]{0.2f, 0.2f, 0.2f, 1.0f};
		float[] mat_diffuse = new float[]{0.7f, 0.7f, 0.7f, 1.0f};
		float[] mat_specular = new float[]{0.3f, 0.3f, 0.3f, 1.0f};
		float shine = 50f;
		gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, shine);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, mat_ambient, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, mat_diffuse, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, mat_specular, 0);
	}

	public static void setWoodenBoxMaterial(GL2 gl) {
		float[] mat_ambient = new float[]{0.4f, 0.4f, 0.4f, 1.0f};
		float[] mat_diffuse = new float[]{0.7f, 0.4f, 0.2f, 1.0f};
		float[] mat_specular = new float[]{0.4f, 0.3f, 0.2f, 1.0f};
		float shine = 20f;
		gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, shine);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, mat_ambient, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, mat_diffuse, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, mat_specular, 0);
	}
}
