package edu.cg.models.Car;

import com.jogamp.opengl.GL2;

public class Materials {
	// TODO: Use this class to update the color of the vertexes when drawing.
	private static final float DARK_GRAY[] = { 0.2f, 0.2f, 0.2f };
	private static final float DARK_RED[] = { 0.25f, 0.01f, 0.01f };
	private static final float RED[] = { 0.7f, 0f, 0f };
	private static final float BLACK[] = { 0.05f, 0.05f, 0.05f };

	public static void SetMetalMaterial(GL2 gl, float[] color) {
		gl.glColor3fv(color, 0);
	}

//	public static void SetBlackMetalMaterial(GL2 gl) {
//		SetMetalMaterial(gl, BLACK);
//	}
//
//	public static void SetRedMetalMaterial(GL2 gl) {
//		SetMetalMaterial(gl, RED);
//	}


//	public static void SetDarkRedMetalMaterial(GL2 gl) {
//		SetMetalMaterial(gl, DARK_RED);
//	}
//
//	public static void SetDarkGreyMetalMaterial(GL2 gl) {
//		SetMetalMaterial(gl, DARK_GRAY);
//	}
//
//	public static void setMaterialTire(GL2 gl) {
//		float col[] = { .05f, .05f, .05f };
//		gl.glColor3fv(col,0);
//	}
//
//	public static void setMaterialRims(GL2 gl) {
//		gl.glColor3fv(DARK_GRAY,0);
//	}

	public static void SetBlackMetalMaterial(GL2 gl) {
		float[] mat_ambient = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
		float[] mat_diffuse = new float[]{0.01f, 0.01f, 0.01f, 1.0f};
		float[] mat_specular = new float[]{0.5f, 0.5f, 0.5f, 1.0f};
		float shine = 32.0f;
		gl.glMaterialf(1028, 5633, shine);
		gl.glMaterialfv(1028, 4608, mat_ambient, 0);
		gl.glMaterialfv(1028, 4609, mat_diffuse, 0);
		gl.glMaterialfv(1028, 4610, mat_specular, 0);
	}

	public static void SetRedMetalMaterial(GL2 gl) {
		float[] mat_ambient = new float[]{0.1745f, 0.01175f, 0.01175f, 1.0f};
		float[] mat_diffuse = new float[]{0.61424f, 0.04136f, 0.04136f, 1.0f};
		float[] mat_specular = new float[]{0.727811f, 0.626959f, 0.626959f, 1.0f};
		float shine = 76.8f;
		gl.glMaterialf(1028, 5633, shine);
		gl.glMaterialfv(1028, 4608, mat_ambient, 0);
		gl.glMaterialfv(1028, 4609, mat_diffuse, 0);
		gl.glMaterialfv(1028, 4610, mat_specular, 0);
	}

	public static void SetDarkRedMetalMaterial(GL2 gl) {
		float[] mat_ambient = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
		float[] mat_diffuse = new float[]{0.4f, 0.0f, 0.0f, 1.0f};
		float[] mat_specular = new float[]{0.4f, 0.3f, 0.3f, 1.0f};
		float shine = 32.0f;
		gl.glMaterialf(1028, 5633, shine);
		gl.glMaterialfv(1028, 4608, mat_ambient, 0);
		gl.glMaterialfv(1028, 4609, mat_diffuse, 0);
		gl.glMaterialfv(1028, 4610, mat_specular, 0);
	}

	public static void SetDarkGreyMetalMaterial(GL2 gl) {
		float[] mat_ambient = new float[]{0.25f, 0.25f, 0.25f, 1.0f};
		float[] mat_diffuse = new float[]{0.4f, 0.4f, 0.4f, 1.0f};
		float[] mat_specular = new float[]{0.774597f, 0.774597f, 0.774597f, 1.0f};
		float shine = 76.8f;
		gl.glMaterialf(1028, 5633, shine);
		gl.glMaterialfv(1028, 4608, mat_ambient, 0);
		gl.glMaterialfv(1028, 4609, mat_diffuse, 0);
		gl.glMaterialfv(1028, 4610, mat_specular, 0);
	}

	public static void setMaterialTire(GL2 gl) {
		float[] col = new float[]{0.05f, 0.05f, 0.05f};
		gl.glColor3fv(col, 0);
		gl.glMaterialf(1028, 5633, 100.0f);
		gl.glMaterialfv(1028, 4609, col, 0);
		gl.glMaterialfv(1028, 4610, col, 0);
	}

	public static void setMaterialRims(GL2 gl) {
		float[] col = new float[]{0.8f, 0.8f, 0.8f};
		gl.glColor3fv(DARK_GRAY, 0);
		gl.glMaterialf(1028, 5633, 20.0f);
		gl.glMaterialfv(1028, 4609, DARK_GRAY, 0);
		gl.glMaterialfv(1028, 4610, col, 0);
	}
	public static void setGreenMaterial(GL2 gl) {
		float[] mat_ambient = new float[]{0.0215f, 0.1745f, 0.0215f, 1.0f};
		float[] mat_diffuse = new float[]{0.07568f, 0.61424f, 0.07568f, 1.0f};
		float[] mat_specular = new float[]{0.633f, 0.727811f, 0.633f, 1.0f};
		float shine = 128.0f;
		gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, shine);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, mat_ambient, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, mat_diffuse, 0);

		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, mat_specular, 0);
	}

	public static void setAsphaltMaterial(GL2 gl) {
		float[] mat_ambient = new float[]{0.15375f, 0.15f, 0.16625f, 1.0f};
		float[] mat_diffuse = new float[]{0.68275f, 0.67f, 0.72525f, 1.0f};
		float[] mat_specular = new float[]{0.332741f, 0.328634f, 0.346435f, 1.0f};
		float shine = 38.4f;
		gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, shine);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, mat_ambient, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, mat_diffuse, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, mat_specular, 0);
	}

	public static void setWoodenBoxMaterial(GL2 gl) {
		float[] mat_ambient = new float[]{0.4f, 0.4f, 0.4f, 1.0f};
		float[] mat_diffuse = new float[]{0.714f, 0.4284f, 0.18144f, 1.0f};
		float[] mat_specular = new float[]{0.393548f, 0.271906f, 0.166721f, 1.0f};
		float shine = 25.6f;
		gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, shine);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, mat_ambient, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, mat_diffuse, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, mat_specular, 0);
	}
}
