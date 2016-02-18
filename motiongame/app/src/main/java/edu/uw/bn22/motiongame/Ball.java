package edu.uw.bn22.motiongame;

/**
 * Created by bruceng on 2/18/16.
 */
public class Ball {
    public float cx; //center
    public float cy;
    public float radius; //radius
    public float dx; //velocity
    public float dy;

    public Ball(float cx, float cy, float radius) {
        this.cx = cx;
        this.cy = cy;
        this.radius = radius;
        this.dx = 0;
        this.dy = 0;
    }

    /*public float getCX() {
        return cx;
    }

    public float getCY() {
        return cy;
    }

    public float getRadius() {
        return radius;
    }

    public float getDX() {
        return dx;
    }

    public float getDY() {
        return dy;
    }

    public void setCX(float newCX) {
        this.cx = newCX;
    }

    public void setCY(float newCY) {
        this.cx = newCY;
    }

    public void setRadius(float newRadius) {
        this.cx = newRadius;
    }

    public void setDX(float newDX) {
        this.cx = newDX;
    }

    public void setDY(float newDY) {
        this.cx = newDY;
    }*/
}
