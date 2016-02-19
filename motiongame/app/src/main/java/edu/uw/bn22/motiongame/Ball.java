package edu.uw.bn22.motiongame;

//Creates a ball class that stores the circles created in the drawing surface view
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
}
