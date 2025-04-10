import java.awt.Graphics;
import java.awt.Color;

public class SocialAgent extends Agent {

    public SocialAgent(double x0, double y0, double z0, double radius, int socialCount) {
        super(x0, y0, z0, radius, socialCount);
    }

    @Override
    public void updateState(Landscape scape) {
        LinkedList<Agent> neighbors = scape.getNeighbors(getX(), getY(), getZ(), radius);
        int count = 0;
        for (Agent a : neighbors) {
            if (a != this) count++;
        }
        if (count < socialCount) {
            setX(Math.max(0, Math.min(scape.getWidth(), getX() + (Math.random() - 0.5) * radius)));
            setY(Math.max(0, Math.min(scape.getHeight(), getY() + (Math.random() - 0.5) * radius)));
            setZ(Math.max(0, Math.min(scape.getDepth(), getZ() + (Math.random() - 0.5) * radius)));
            setMoved(true);
        }
    }
}