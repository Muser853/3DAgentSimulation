public class SocialAgent extends Agent {
    public SocialAgent(double x0, double y0, double z0, double radius, int socialCount) {
        super(x0, y0, z0, radius, socialCount);
    }

    @Override
    public void updateState(Landscape scape) {
        LinkedList<Agent> neighbors = scape.getNeighbors(getX(), getY(), getZ(), radius);
        int count = 0;
        for (Agent a : neighbors) if (a != this) count++;

        if (count < socialCount) {
            double sumX = 0.0, sumY = 0.0, sumZ = 0.0;
            for (Agent neighbor : neighbors) {
                if (neighbor == this) continue;
                sumX += 1 / (getX() - neighbor.getX());
                sumY += 1 / (getY() - neighbor.getY());
                sumZ += 1 / (getZ() - neighbor.getZ());
            }
            setX(Math.max(0, Math.min(scape.getWidth(), getX() - sumX)));
            setY(Math.max(0, Math.min(scape.getHeight(), getY() - sumY)));
            setZ(Math.max(0, Math.min(scape.getDepth(), getZ() - sumZ)));
            setMoved(true);
        }
    }
}