package edu.cg.models.Car;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import edu.cg.models.IRenderable;

public class PairOfWheels implements IRenderable {

    private final Wheel wheel = new Wheel();


    @Override
    public void render(GL2 gl) {
        GLU glu = new GLU();
        GLUquadric quad = glu.gluNewQuadric();

        gl.glPushMatrix();

        // First wheel
        gl.glTranslated(0, 0, (Specification.TIRE_DEPTH + Specification.PAIR_OF_WHEELS_ROD_DEPTH) / 2);
        this.wheel.render(gl);

        // Second wheel
        gl.glTranslated(0, 0, (-1) * (Specification.TIRE_DEPTH + Specification.PAIR_OF_WHEELS_ROD_DEPTH));
        this.wheel.render(gl);

        // Rod between wheels
        gl.glTranslated(0, 0, Specification.TIRE_DEPTH / 2);
        Materials.SetDarkGreyMetalMaterial(gl);
        glu.gluCylinder(quad, Specification.PAIR_OF_WHEELS_ROD_RADIUS, Specification.PAIR_OF_WHEELS_ROD_RADIUS, Specification.PAIR_OF_WHEELS_ROD_DEPTH, 30, 1);

        gl.glPopMatrix();
        glu.gluDeleteQuadric(quad);

    }

    @Override
    public void init(GL2 gl) {
        // TODO Auto-generated method stub

    }

    @Override
    public String toString() {
        return "PairOfWheels";
    }

    @Override
    public void destroy(GL2 gl) {}

}
