public abstract class Agent {
    protected double x, y, z, radius;
    public int socialCount;
    protected boolean moved;

    public Agent(double x0, double y0, double z0, double radius, int socialCount) {
        this.x = x0;
        this.y = y0;
        this.z = z0;
        this.radius = radius;
        this.socialCount = socialCount;
        this.moved = false;
    }

    public double getZ() {return z;}
    public void setZ(double newZ) {this.z = newZ;}
    public double getX() {return x;}
    public double getY() {return y;}
    public double getRadius() { return radius;}
    public boolean getMoved() { return moved;}
    public void setX(double newX) {this.x = newX;}
    public void setY(double newY) {this.y = newY;}
    public void setMoved(boolean moved) { this.moved = moved;}
    public abstract void updateState(Landscape scape);
}