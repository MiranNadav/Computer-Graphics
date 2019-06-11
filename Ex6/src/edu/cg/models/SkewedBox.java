package edu.cg.models;

import com.jogamp.opengl.GL2;

public class SkewedBox implements IRenderable {
    private double length, height1, height2, depth1, depth2;

    public SkewedBox() {
        length = .1;
        height1 = .2;
        height2 = .1;
        depth1 = .2;
        depth2 = .1;
    };

    public SkewedBox(double length, double h1, double h2, double d1, double d2) {
        this.length = length;
        this.height1 = h1;
        this.height2 = h2;
        this.depth1 = d1;
        this.depth2 = d2;
    }

    @Override
    public void render(GL2 gl) {
        renderFrontEdge(gl);
        renderBackEdge(gl);
        renderTopEdge(gl);
        renderButtomEdge(gl);
        renderLeftEdge(gl);
        renderRightEdge(gl);
    }

    private void renderRightEdge(GL2 gl) {
        gl.glNormal3d(0, 0, -1);
        gl.glBegin(gl.GL_QUADS);
        gl.glVertex3d(-this.length / 2, 0, -this.depth1 / 2);
        gl.glVertex3d(-this.length / 2, this.height1, -this.depth1 / 2);
        gl.glVertex3d(this.length / 2, this.height2, -this.depth2 / 2);
        gl.glVertex3d(this.length / 2, 0, -this.depth2 / 2);
        gl.glEnd();
    }

    private void renderLeftEdge(GL2 gl) {
        gl.glNormal3d(0, 0, 1);
        gl.glBegin(gl.GL_QUADS);
        gl.glVertex3d(-this.length / 2, this.height1, this.depth1 / 2);
        gl.glVertex3d(-this.length / 2, 0, this.depth1 / 2);
        gl.glVertex3d(this.length / 2, 0, this.depth2 / 2);
        gl.glVertex3d(this.length / 2, this.height2, this.depth2 / 2);
        gl.glEnd();
    }

    private void renderButtomEdge(GL2 gl) {
        gl.glNormal3d(0, -1, 0);
        gl.glBegin(gl.GL_QUADS);
        gl.glVertex3d(-this.length / 2, 0, this.depth1 / 2);
        gl.glVertex3d(-this.length / 2, 0, -this.depth1 / 2);
        gl.glVertex3d(this.length / 2, 0, -this.depth2 / 2);
        gl.glVertex3d(this.length / 2, 0, this.depth2 / 2);
        gl.glEnd();
    }

    private void renderTopEdge(GL2 gl) {
        gl.glNormal3d(0, 1, 0);
        gl.glBegin(gl.GL_QUADS);
        gl.glVertex3d(-this.length / 2, this.height1, this.depth1 / 2);
        gl.glVertex3d(this.length / 2, this.height2, this.depth2 / 2);
        gl.glVertex3d(this.length / 2, this.height2, -this.depth2 / 2);
        gl.glVertex3d(-this.length / 2, this.height1, -this.depth1 / 2);
        gl.glEnd();
    }

    private void renderBackEdge(GL2 gl) {
        gl.glNormal3d(-1, 0, 0);
        gl.glBegin(gl.GL_QUADS);
        gl.glVertex3d(-this.length / 2, 0, -this.depth1 / 2);
        gl.glVertex3d(-this.length / 2, 0, this.depth1 / 2);
        gl.glVertex3d(-this.length / 2, this.height1, this.depth1 / 2);
        gl.glVertex3d(-this.length / 2, this.height1, -this.depth1 / 2);
        gl.glEnd();
    }

    private void renderFrontEdge(GL2 gl) {
        gl.glNormal3d(1, 0, 0);
        gl.glBegin(gl.GL_QUADS);
        gl.glVertex3d(this.length / 2, 0, this.depth2 / 2);
        gl.glVertex3d(this.length / 2, 0, -this.depth2 / 2);
        gl.glVertex3d(this.length / 2, this.height2, -this.depth2 / 2);
        gl.glVertex3d(this.length / 2, this.height2, this.depth2 / 2);
        gl.glEnd();
    }

    @Override
    public void init(GL2 gl) {
    }

    @Override
    public String toString() {
        return "SkewedBox";
    }

    @Override
    public void destroy(GL2 gl){}
}
