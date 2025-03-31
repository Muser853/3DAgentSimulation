import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AgentSimulation {
    
    public static void main(String[] args) throws InterruptedException {
        if (args.length == 0) {
            simulateAndPlotWithDisplay();
        } else if (args[0].equals("3")) {
            simulate3DAndPlot();
        } else {
            int N = Integer.parseInt(args[0]);
            int iterations = runExpt(N);
            System.out.println("Simulation completed after " + iterations + " iterations");
        }
    }

    public static void simulateAndPlotWithDisplay() throws InterruptedException {
        int maxSize = 256;
        int[] agentNumbers = new int[maxSize * maxSize];
        int[] iterationsData = new int[maxSize * maxSize];

        for (int size = 2; size <= maxSize; size++) {
            int maxAgents = size * size;
            agentNumbers = new int[maxAgents];
            iterationsData = new int[maxAgents];

            for (int socialAgents = 1; socialAgents < maxAgents; socialAgents++) {
                for (int antiSocialAgents = 1; antiSocialAgents < maxAgents - socialAgents; antiSocialAgents++) {
                    int totalAgents = socialAgents + antiSocialAgents;
                    if (totalAgents - 1 < agentNumbers.length) { // prevent array index out of bounds
                        int iterations = runExptWithBothAgents(socialAgents, antiSocialAgents, size);
                        agentNumbers[totalAgents - 1] = totalAgents;
                        iterationsData[totalAgents - 1] = iterations;
                    }
                }
            }

            if (size == maxSize) {
                JPanel chartPanel = new LineChartPanel(agentNumbers, iterationsData, 800, 400);
                JFrame frame = new JFrame("2D Agent Simulation Iteration Plot (" + size + "x" + size + ")");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(chartPanel);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            } else {
                plot3DChart(agentNumbers, iterationsData, size);
            }
        }
    }

    private static void plot3DChart(int[] agentNumbers, int[] iterationsData, int size) {
        
        System.out.println("Plotting 3D chart for size: " + size);
    
    }

    public static void simulate3DAndPlot() {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        for (int size = 2; size <= 32; size++) {
            final int n = size;
            executor.submit(() -> runExpt3D(new Landscape(n, n, n), n * n * n));
        }
        executor.shutdown();
    }

    public static int runExpt(int N, int size) {
        Landscape scape = new Landscape(size, size, 1); // 2d simulation

        // add N random positioned agents
        Random random = new Random();
        for (int i = 0; i < N; i++) {
            double x = random.nextDouble() * scape.getWidth();
            double y = random.nextDouble() * scape.getHeight();
            SocialAgent agent = new SocialAgent(x, y, 25); // 2d constructor
            scape.addAgent(agent);
        }
        int movedAgents = 1;
        int iterations = 0;

        while (movedAgents > 0 && iterations < 5000) {
            movedAgents = scape.updateAgents();
            iterations++;
        }
        return iterations;
    }

    public static int runExpt3D(Landscape scape, int N) {
        scape.clearAgents(); // Clear agents from previous simulation
        // N randomly positioned agents
        Random random = new Random();
        for (int i = 0; i < N; i++) {
            double x = random.nextDouble() * scape.getWidth();
            double y = random.nextDouble() * scape.getHeight();
            double z = random.nextDouble() * scape.getDepth();
            SocialAgent agent = new SocialAgent(x, y, z, 25); // 3d constructor
            scape.addAgent(agent);
        }
        int movedAgents = 1;
        int iterations = 0;

        while (movedAgents > 0 && iterations < 5000) {
            movedAgents = scape.updateAgents();
            iterations++;
        }
        return iterations;
    }

    public static int runExpt(int N) {
        // Create landscape and LandscapeDisplay
        Landscape scape = new Landscape(5000, 5000, 1);
        
        // Add N SocialAgents at random positions
        for (int i = 0; i < N; i++) {
            double x = Math.random() * 5000;
            double y = Math.random() * 5000;
            SocialAgent agent = new SocialAgent(x, y, 25);
            scape.addAgent(agent);
        }
        int movedAgents = 1; // Start non-zero to enter loop
        int iterations = 0;
        
        // Run simulation until no agents move or timeout
        while (movedAgents > 0 && iterations < 5000) {
            movedAgents = scape.updateAgents();
            iterations++;
        }
        return iterations;
    }

    public static int runExptWithBothAgents(int socialAgentsCount, int antiSocialAgentsCount, int size) {
        Landscape scape = new Landscape(size, size, 1);

        Random random = new Random(); // Reuse a single Random instance
        for (int i = 0; i < socialAgentsCount; i++) {
            double x = random.nextDouble() * scape.getWidth();
            double y = random.nextDouble() * scape.getHeight();
            SocialAgent agent = new SocialAgent(x, y, 25);
            scape.addAgent(agent);
        }
        for (int i = 0; i < antiSocialAgentsCount; i++) {
            double x = random.nextDouble() * scape.getWidth();
            double y = random.nextDouble() * scape.getHeight();
            AntiSocialAgent agent = new AntiSocialAgent(x, y, 50);
            scape.addAgent(agent);
        }
        int movedAgents = 1;
        int iterations = 0;

        while (movedAgents > 0 && iterations < 5000) {
            movedAgents = scape.updateAgents();
            iterations++;
        }
        return iterations;
    }
    
    // Custom JPanel for drawing the line chart
    static class LineChartPanel extends JPanel {
        private final int[] xData;
        private final int[] yData;
        private final int width;
        private final int height;

        public LineChartPanel(int[] xData, int[] yData, int width, int height) {
            this.xData = xData;
            this.yData = yData;
            this.width = width;
            this.height = height;
            setPreferredSize(new Dimension(width, height));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);
            g.setColor(Color.BLACK);

            int maxX = xData.length > 0 ? xData[xData.length - 1] : 0;
            int maxY = Arrays.stream(yData).max().orElse(0);

            // Draw axes
            g.drawLine(50, height - 50, width - 50, height - 50); // X-axis
            g.drawLine(50, 50, 50, height - 50); // Y-axis

            // Draw labels
            g.drawString("Number of Agents", width / 2, height - 20);
            g.drawString("Iterations", 10, height / 2);

            // Add X-axis tick marks and labels
            for (int i = 0; i <= 40; i ++) {
                int xTick = 50 + (int) ((xData[i] * (width - 100)) / (maxX != 0 ? maxX : 1));
                g.drawLine(xTick, height - 50, xTick, height - 45); // Tick mark
                g.drawString(String.valueOf(i), xTick - 10, height - 30); // Label
            }

            // Add Y-axis tick marks and labels
            for (int i = 0; i <= 100; i += 10) {
                int yTick = height - 50 - (int) ((yData[i] * (height - 100)) / (maxY != 0 ? maxY : 1));
                g.drawLine(50, yTick, 55, yTick); // Tick mark
                g.drawString(String.valueOf(i), 20, yTick + 5); // Label
            }

            // Draw line chart
            for (int i = 1; i < xData.length && i < yData.length; i++) {
                int x1 = 50 + (int) ((xData[i - 1] * (width - 100)) / (maxX != 0 ? maxX : 1));
                int y1 = height - 50 - (int) ((yData[i - 1] * (height - 100)) / (maxY != 0 ? maxY : 1));
                int x2 = 50 + (int) ((xData[i] * (width - 100)) / (maxX != 0 ? maxX : 1));
                int y2 = height - 50 - (int) ((yData[i] * (height - 100)) / (maxY != 0 ? maxY : 1));
                g.drawLine(x1, y1, x2, y2);
            }
        }
    }
}