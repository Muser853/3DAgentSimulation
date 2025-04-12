import java.util.HashMap;
import java.util.Objects;

public class Landscape {
    protected final int width, height, depth;
    private final LinkedList<Agent> agents;
    private final HashMap<Integer, LinkedList<Agent>> grid;

    public Landscape(int w, int h, int d) {
        this.width = w;
        this.height = h;
        this.depth = d;
        this.agents = new LinkedList<>();
        this.grid = new HashMap<>();
    }

    public void addAgent(Agent a) {
        agents.addFirst(a);
        addAgentToGrid(a);
    }

    public LinkedList<Agent> getNeighbors(double x0, double y0, double z0, double radius) {
        LinkedList<Agent> neighbors = new LinkedList<>();
        int gridSize = Math.max(1, (int) radius);
        int gridX = (int) (x0 / gridSize);
        int gridY = (int) (y0 / gridSize);
        int gridZ = (int) (z0 / gridSize);

        for (int dx = -1; dx < 2; dx++) {
            for (int dy = -1; dy < 2; dy++) {
                for (int dz = -1; dz < 2; dz++) {
                    int key = (gridX + dx) * AgentSimulationSVG.SIDE_LENGTH * AgentSimulationSVG.SIDE_LENGTH + (gridY + dy) * AgentSimulationSVG.SIDE_LENGTH + (gridZ + dz);
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

    public int updateAgents() {
        int movedCount = 0;

        for (Agent a : agents)
            a.setMoved(false);

        for (Agent a : agents) {
            a.updateState(this);
            if (a.getMoved()) movedCount++;
        }
        grid.clear();
        for (Agent a : agents) addAgentToGrid(a);
        return movedCount;
    }

    private void addAgentToGrid(Agent a) {
        grid.computeIfAbsent(Objects.hash((int) a.getX(), (int) a.getY(), (int) a.getZ()),
                _ -> new LinkedList<>()).add(a);
    }
}