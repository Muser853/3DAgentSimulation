import java.awt.Graphics;
public abstract class Agent {
    private double x;
    private double y;
    private double z;
    protected int radius;
    protected boolean moved;

    public Agent(double x0, double y0, int radius) {
        this(x0, y0, 0, radius);
    }

    public Agent(double x0, double y0, double z0, int radius) {
        this.x = x0;
        this.y = y0;
        this.z = z0;
        this.radius = radius;
        this.moved = false;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double newZ) {
        this.z = newZ;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getRadius() {
        return radius;
    }

    public boolean getMoved() {
        return moved;
    }

    public void setX(double newX) {
        this.x = newX;
    }

    public void setY(double newY) {
        this.y = newY;
    }

    public void setRadius(int newRadius) {
        this.radius = newRadius;
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    public abstract void updateState(Landscape scape);
    public abstract void draw(Graphics g);
}

