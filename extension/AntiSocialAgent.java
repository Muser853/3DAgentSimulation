import java.awt.Graphics;
import java.awt.Color;
public class AntiSocialAgent extends Agent {
    public AntiSocialAgent(double x0, double y0, int radius) {
        super(x0, y0, radius);
    }

    @Override
    public void updateState(Landscape scape) {
        LinkedList<Agent> neighbors = scape.getNeighbors(getX(), getY(), getZ(), radius);
        int count = 0;
        for (Agent a : neighbors) {
            if (a != this) {
                count++;
            }
        }

        if (count >= 4) {
            double newX = getX() + (Math.random() * 20 - 10);
            double newY = getY() + (Math.random() * 20 - 10);
            double newZ = getZ() + (Math.random() * 20 - 10);

            newX = Math.max(0, Math.min(scape.getWidth() - 5, newX));
            newY = Math.max(0, Math.min(scape.getHeight() - 5, newY));
            newZ = Math.max(0, Math.min(scape.getDepth() - 5, newZ));

            setX(newX);
            setY(newY);
            setZ(newZ);
            setMoved(true);
        }
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval((int) getX(), (int) getY(), getRadius(), getRadius());
    }
}