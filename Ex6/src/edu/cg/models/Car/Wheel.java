package edu.cg.models.Car;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import edu.cg.models.IRenderable;

public class Wheel implements IRenderable {

    @Override
    public void render(GL2 gl) {
        GLU glu = new GLU();
        GLUquadric quad = glu.gluNewQuadric();

        Materials.setMaterialTire(gl);
        // Build Cylinder
        gl.glPushMatrix();
        gl.glTranslated(0, 0, -0.05);
        glu.gluCylinder(quad, Specification.TIRE_OUT_RADIUS, Specification.TIRE_OUT_RADIUS, Specification.TIRE_DEPTH, 50, 1);
        gl.glRotated(180, 1, 0, 0);

        // Outer Disk
        glu.gluDisk(quad, Specification.TIRE_IN_RADIUS, Specification.TIRE_OUT_RADIUS, 50, 1);
        gl.glRotated(180, 1, 0, 0);
        gl.glTranslated(0, 0, 0.1);
        glu.gluDisk(quad, Specification.TIRE_IN_RADIUS, Specification.TIRE_OUT_RADIUS, 50, 1);

        // Inner Disk
        Materials.setMaterialRims(gl);
        glu.gluDisk(quad, 0, Specification.TIRE_IN_RADIUS, 50, 1);
        gl.glTranslated(0, 0, -0.1);
        gl.glRotated(180, 1, 0, 0);
        glu.gluDisk(quad, 0, Specification.TIRE_IN_RADIUS, 50, 1);

        gl.glPopMatrix();
        glu.gluDeleteQuadric(quad);
    }

    @Override
    public void init(GL2 gl) {
        // TODO Auto-generated method stub

    }

    @Override
    public String toString() {
        return "Wheel";
    }

    @Override
    public void destroy(GL2 gl){}
}
