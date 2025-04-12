import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class AgentSimulationSVG {
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
        } catch (Exception _) {}
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

                for (int[] coord : socialPositions)
                    sc.addAgent(new SocialAgent(coord[0], coord[1], coord[2], socialRadius, socialSocialCount));
                for (int[] coord : antiPositions) 
                    sc.addAgent(new AntiSocialAgent(coord[0], coord[1], coord[2], antiRadius, antiSocialCount));
                
                int bound = volume * 512;
                int iterations;
                for (iterations = 0; iterations < bound; iterations++) if (sc.updateAgents() == 0) break;

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
    @SuppressWarnings("unchecked")
    private static void saveChart(int[] xData, int[] minData, int[] maxData, double[] avgData, String title) {
        try {
            Platform.runLater(() -> {
                // Create JavaFX LineChart
                LineChart<Number, Number> chart = new LineChart<>(new NumberAxis(), new NumberAxis());
                chart.setTitle(title);
                XYChart.Series<Number, Number> minSeries = new XYChart.Series<>();
                minSeries.setName("Min");
                XYChart.Series<Number, Number> avgSeries = new XYChart.Series<>();
                avgSeries.setName("Avg");
                XYChart.Series<Number, Number> maxSeries = new XYChart.Series<>();
                maxSeries.setName("Max");
    
                for (int i = 0; i < xData.length; i++) {
                    minSeries.getData().add(new XYChart.Data<>(xData[i], minData[i]));
                    avgSeries.getData().add(new XYChart.Data<>(xData[i], avgData[i]));
                    maxSeries.getData().add(new XYChart.Data<>(xData[i], maxData[i]));
                }
                chart.getData().addAll(minSeries, avgSeries, maxSeries);
                StringBuilder svgContent = new StringBuilder();
                svgContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
                svgContent.append("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"" + CHART_WIDTH + "\" height=\"" + CHART_HEIGHT + "\">\n");
                svgContent.append("<text x=\"10\" y=\"20\" font-family=\"Arial\" font-size=\"14\">" + title + "</text>\n");
                for (int i = 0; i < xData.length; i++) {
                    int x = 50 + i * 20; // Adjust x position
                    int yMin = CHART_HEIGHT - minData[i] * 2; // Adjust y position
                    int yAvg = CHART_HEIGHT - (int) (avgData[i] * 2);
                    int yMax = CHART_HEIGHT - maxData[i] * 2;    // Add data points as circles
                    svgContent.append("<circle cx=\"" + x + "\" cy=\"" + yMin + "\" r=\"3\" fill=\"red\"/>\n");
                    svgContent.append("<circle cx=\"" + x + "\" cy=\"" + yAvg + "\" r=\"3\" fill=\"blue\"/>\n");
                    svgContent.append("<circle cx=\"" + x + "\" cy=\"" + yMax + "\" r=\"3\" fill=\"green\"/>\n");
                }
                svgContent.append("</svg>");
                File outputDir = new File("charts");// Save SVG to file
                if (!outputDir.exists()) outputDir.mkdir();
                String filename = title.replaceAll("[^a-zA-Z0-9-_.]", "_") + ".svg";
                try (FileWriter writer = new FileWriter(new File(outputDir, filename))){writer.write(svgContent.toString());}catch(IOException _){}
            });
        }catch(Exception _){}
    }
}