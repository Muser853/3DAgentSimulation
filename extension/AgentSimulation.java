import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class AgentSimulation {
    public static final int SIDE_LENGTH = 512, CHART_WIDTH = 800, CHART_HEIGHT = 400;

    public static void main(String[] args) {
        try (ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())) {
            for (int width = 1; width < SIDE_LENGTH; width++) {
                for (int height = 1; height < width; height++) {
                    for (int depth = 1; depth < height; depth++) {
                        Landscape scape = new Landscape(width, height, depth);
                        int finalVolume = width * height * depth;
                        int finalMaxRadius = (int) (Math.cbrt(finalVolume) / 2);

                        executor.submit(() -> {
                            for (double socialRadius = 1; socialRadius < finalMaxRadius; socialRadius += 0.25) {
                                for (double antiRadius = 1; antiRadius < finalMaxRadius; antiRadius += 0.25) {
                                    for (int socialSocialCount = 1; socialSocialCount < finalVolume; socialSocialCount++) {
                                        for (int antiSocialCount = 1; antiSocialCount < finalVolume; antiSocialCount++) {
                                            processCombination(scape, finalVolume, socialRadius, antiRadius, socialSocialCount, antiSocialCount);
                                        }
                                    }
                                }
                            }
                        } );
                    }
                }
            }
            executor.shutdown();
        }
    }

    private static void processCombination(Landscape sc, int volume,
                                           double socialRadius, double antiRadius,
                                           int socialSocialCount, int antiSocialCount) {
        try {
            int[] minIterations = new int[volume], maxIterations = new int[volume];
            double[] avgIterations = new double[volume];

            Arrays.fill(minIterations, Integer.MAX_VALUE);
            Arrays.fill(maxIterations, 0);
            Arrays.fill(avgIterations, 0);

            for (int social = 1; social <= volume; social++) {
                for (int anti = 1; anti <= volume - social; anti++) {
                    int total = social + anti;
                    int[] results = runExperiments(social, anti, sc, socialRadius, antiRadius, socialSocialCount, antiSocialCount, volume);
                    
                    if (total - 1 < minIterations.length) {
                        minIterations[total - 1] = Math.min(minIterations[total - 1], results[0]);
                        maxIterations[total - 1] = Math.max(maxIterations[total - 1], results[1]);
                        avgIterations[total - 1] = (avgIterations[total - 1] * (total - 1) + results[2]) / total;
                    }
                }
            }
            List<Integer> xData = new ArrayList<>(), minData = new ArrayList<>(), maxData = new ArrayList<>();
            List<Double> avgData = new ArrayList<>();
            
            for (int i = 0; i < minIterations.length; i++) {
                if (minIterations[i] != Integer.MAX_VALUE) {
                    xData.add(i + 1);
                    minData.add(minIterations[i]);
                    maxData.add(maxIterations[i]);
                    avgData.add(avgIterations[i]);
                }
            }
            String title = String.format(
                "W=%d H=%d D=%d SocRad=%.2f AntiRad=%.2f SocCnt=%d AntiCnt=%d",  // 修改部分：%d → %.2f
                sc.width, sc.height, sc.depth, socialRadius, antiRadius,
                socialSocialCount, antiSocialCount
            );
            saveChart(
                xData.stream().mapToInt(i->i).toArray(),
                minData.stream().mapToInt(i->i).toArray(),
                maxData.stream().mapToInt(i->i).toArray(),
                avgData.stream().mapToDouble(d->d).toArray(),
                title
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int[] runExperiments(int socialAgents, int antiAgents, Landscape sc,
                                        double socialRadius, double antiRadius, int socialSocialCount, int antiSocialCount, int volume) {
        int min = Integer.MAX_VALUE, max = 0, validTrials = 0;
        double total = 0;
        List<int[]> allCoordinates = new ArrayList<>();

        for (int x = 0; x < sc.width; x++) {
            for (int y = 0; y < sc.height; y++) {
                for (int z = 0; z < sc.depth; z++) {
                    allCoordinates.add(new int[]{x, y, z});
                }
            }
        }
        List<List<int[]>> socialAgentPositions = generateCombinations(allCoordinates, socialAgents);
        List<List<int[]>> antiAgentPositions = generateCombinations(allCoordinates, antiAgents);

        for (List<int[]> socialPositions : socialAgentPositions) {
            for (List<int[]> antiPositions : antiAgentPositions) {
                if (hasOverlap(socialPositions, antiPositions)) continue;

                sc.clearAgents();
                for (int[] coord : socialPositions) {
                    sc.addAgent(new SocialAgent(coord[0], coord[1], coord[2], socialRadius, socialSocialCount));
                }
                for (int[] coord : antiPositions) {
                    sc.addAgent(new AntiSocialAgent(coord[0], coord[1], coord[2], antiRadius, antiSocialCount));
                }
                int bound = volume * 512;
                int iterations; // Run the simulation
                for (iterations = 0; iterations < bound; iterations++) {
                    if (sc.updateAgents() == 0) break;
                }
                if (iterations != bound) {
                    min = Math.min(min, iterations);
                    max = Math.max(max, iterations);
                    total += iterations;
                    validTrials++;
                }  // Record statistics only if the simulation did not reach MAX_ITERATIONS
            }
        }  // Return results: [min, max, average]
        return new int[]{min, max, validTrials > 0 ? (int) (total / validTrials) : 0};
    }

    private static List<List<int[]>> generateCombinations(List<int[]> coordinates, int k) {
        List<List<int[]>> combinations = new ArrayList<>();
        generateCombinationsHelper(coordinates, k, 0, new ArrayList<>(), combinations);
        return combinations;    // Helper method to generate all combinations of size k from a list
    }

    private static void generateCombinationsHelper(List<int[]> coordinates, int k, int start,
                                                List<int[]> current, List<List<int[]>> combinations) {
        if (current.size() == k) { //The size of each combination.
            combinations.add(new ArrayList<>(current));
            return; //combinations The list where all valid combinations are stored.
        } //The starting index for generating combinations.
        for (int i = start; i < coordinates.size(); i++) {
            current.add(coordinates.get(i)); //The current combination being constructed.
            generateCombinationsHelper(coordinates, k, i + 1, current, combinations);
            current.removeLast(); //A list of coordinates from which to generate combinations
        } //Recursively generates all possible combinations of coordinates of size `k`.
    }

    private static boolean hasOverlap(List<int[]> socialPositions, List<int[]> antiPositions) {
        for (int[] social : socialPositions) {
            for (int[] anti : antiPositions) {
                if (Arrays.equals(social, anti)) return true;
            }    // help check for overlap between social and anti-social agent positions
        }
        return false;
    }

    private static void saveChart(int[] xData, int[] minData, int[] maxData, 
                                 double[] avgData, String title) {
        try {
            LineChartPanel chartPanel = new LineChartPanel(xData, minData, maxData, avgData, CHART_WIDTH, CHART_HEIGHT);
            File outputDir = new File("charts");
            if (!outputDir.exists()) outputDir.mkdir();
            ImageIO.write(chartPanel.getImage(), "png", new File(outputDir, sanitizeFilename(title) + ".png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String sanitizeFilename(String input) {
        return input.replaceAll("[^a-zA-Z0-9-_.]", "_");
    }

    static class LineChartPanel extends JPanel {
        private final int[] xData, minData, maxData;
        private final double[] avgData;
        private final int width, height;

        public LineChartPanel(int[] xData, int[] minData, int[] maxData, 
                             double[] avgData, int width, int height) {
            this.xData = xData;
            this.minData = minData;
            this.maxData = maxData;
            this.avgData = avgData;
            this.width = width;
            this.height = height;
            setPreferredSize(new Dimension(width, height));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);

            if (xData.length == 0) return;
            int maxX = Arrays.stream(xData).max().orElse(1), maxY = Math.max(
                Arrays.stream(maxData).max().orElse(1),
                (int)Math.ceil(Arrays.stream(avgData).max().orElse(1))
            );
            g.setColor(Color.BLACK);
            g.drawLine(50, height - 50, width - 50, height - 50); // X-axis
            g.drawLine(50, 50, 50, height - 50); // Y-axis
            g.drawString("Number of Social Agents", width / 2 - 50, height - 20);
            g.drawString("Iterations", 20, height / 2);
            g.setColor(Color.RED);
            g.drawString("Min", width - 100, 40);
            g.setColor(Color.BLUE);
            g.drawString("Avg", width - 100, 60);
            g.setColor(Color.GREEN);
            g.drawString("Max", width - 100, 80);
            drawLine(g, Color.RED, minData, maxX, maxY);
            drawLine(g, avgData, maxX, maxY);
            drawLine(g, Color.GREEN, maxData, maxX, maxY);
        }
        private void drawLine(Graphics g, Color color, int[] yValues, int maxX, int maxY) {
            g.setColor(color);
            for (int i = 1; i < xData.length && i < yValues.length; i++) {
                int x1 = mapX(xData[i - 1], maxX), y1 = mapY(yValues[i - 1], maxY), x2 = mapX(xData[i], maxX), y2 = mapY(yValues[i], maxY);
                g.drawLine(x1, y1, x2, y2);
            }
        }
        private void drawLine(Graphics g, double[] yValues, int maxX, int maxY) {
            g.setColor(Color.BLUE);
            for (int i = 1; i < xData.length && i < yValues.length; i++) {
                int x1 = mapX(xData[i - 1], maxX), y1 = mapY(yValues[i - 1], maxY), x2 = mapX(xData[i], maxX), y2 = mapY(yValues[i], maxY);
                g.drawLine(x1, y1, x2, y2);
            }
        }
        private int mapX(int value, int max) {return 50 + (int)((value * (width - 100)) / (double)max);}
        private int mapY(double value, int max) {return height - 50 - (int)((value * (height - 100)) / max);}

        public BufferedImage getImage() {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();
            paintComponent(g2d);
            g2d.dispose();
            return image;
        }
    }
}