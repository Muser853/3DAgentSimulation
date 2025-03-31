import java.awt.Graphics;
import java.util.HashMap;

public class Landscape {
    private int width;
    private int height;
    private int depth;
    private LinkedList<Agent> agents;
    private HashMap<Integer, LinkedList<Agent>> grid;

    public Landscape(int w, int h, int d) {
        this.width = w;
        this.height = h;
        this.depth = d;
        this.agents = new LinkedList<>();
        this.grid = new HashMap<>();
    }

    public void addAgent(Agent a) {
        agents.addFirst(a);
        int gridSize = Math.max(1, (int) (Math.cbrt(agents.size()) + 1));
        int gridX = (int) (a.getX() / gridSize);
        int gridY = (int) (a.getY() / gridSize);
        int gridZ = (int) (a.getZ() / gridSize);
        int key = gridX * 1000000 + gridY * 1000 + gridZ;
        grid.computeIfAbsent(key, _ -> new LinkedList<>()).add(a);
    }

    public LinkedList<Agent> getNeighbors(double x0, double y0, double z0, double radius) {
        LinkedList<Agent> neighbors = new LinkedList<>();
        int gridSize = Math.max(1, (int) radius);
        int gridX = (int) (x0 / gridSize);
        int gridY = (int) (y0 / gridSize);
        int gridZ = (int) (z0 / gridSize);

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    int key = (gridX + dx) * 1000000 + (gridY + dy) * 1000 + (gridZ + dz);
                    LinkedList<Agent> cellAgents = grid.getOrDefault(key, new LinkedList<>());
                    for (Agent a : cellAgents) {
                        if (Math.pow(a.getX() - x0, 2) + Math.pow(a.getY() - y0, 2) + Math.pow(a.getZ() - z0, 2) <= Math.pow(radius, 2)) {
                            neighbors.add(a);
                        }
                    }
                }
            }
        }

        return neighbors;
    }

    public void clearAgents() {
        this.agents.clear();
        this.grid.clear();
    }

    public int getDepth() {
        return depth;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void draw(Graphics g) {
        for (Agent a : agents) {
            a.draw(g);
        }
    }

    public int updateAgents() {
        int movedCount = 0;

        for (Agent a : agents) {
            a.setMoved(false);
        }

        for (Agent a : agents) {
            a.updateState(this);
            if (a.getMoved()) {
                movedCount++;
            }
        }

        return movedCount;
    }
}
