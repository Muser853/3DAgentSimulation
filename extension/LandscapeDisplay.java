import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;

public class LandscapeDisplay {
    protected JFrame win;
    protected Landscape scape;
    private LandscapePanel canvas;
    private Timer animationTimer;

    public LandscapeDisplay(Landscape scape) {
        if (scape == null) {
            throw new IllegalArgumentException("Landscape cannot be null");
        }

        this.win = new JFrame("Agents");
        this.win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.scape = scape;
        this.canvas = new LandscapePanel(this.scape.getWidth(), this.scape.getHeight());
        this.win.add(this.canvas, BorderLayout.CENTER);
        this.win.pack();
        this.win.setVisible(true);
    }

    public void saveImage(String filename) {
        String ext = filename.substring(filename.lastIndexOf('.') + 1);
        Component tosave = this.win.getRootPane();
        BufferedImage image = new BufferedImage(tosave.getWidth(), tosave.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = image.createGraphics();
        tosave.paint(g);
        g.dispose();

        try {
            ImageIO.write(image, ext, new File(filename));
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    private class LandscapePanel extends JPanel {
        public LandscapePanel(int width, int height) {
            super();
            this.setPreferredSize(new Dimension(width, height));
            this.setBackground(Color.white);
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (scape.getDepth() > 1) {
                int gridSize = scape.getWidth();
                int layerOffset = Math.max(2, getWidth() / (gridSize * 5));
                for (int i = 0; i < gridSize; i++) {
                    for (int j = 0; j < gridSize; j++) {
                        for (int k = 0; k < gridSize; k++) {
                            int x = i * (getWidth() / gridSize) + k * layerOffset;
                            int y = j * (getHeight() / gridSize) + k * layerOffset;
                            int colorValue = 255 - (int)(k * 255 / (gridSize - 1));
                            g.setColor(new Color(colorValue, colorValue, 255));
                            g.drawRect(x, y, 2, 2);
                        }
                    }
                }
            } else {
                int gridSize = scape.getWidth();
                for (int i = 0; i < gridSize; i++) {
                    g.drawLine(i * (getWidth() / gridSize), 0, i * (getWidth() / gridSize), getHeight());
                    g.drawLine(0, i * (getHeight() / gridSize), getWidth(), i * (getHeight() / gridSize));
                }
            }
            scape.draw(g);
        }
    }

    public void repaint() {
        this.win.repaint();
    }

    public void startAnimation(int delay) {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        animationTimer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scape.updateAgents();
                repaint();
            }
        });
        animationTimer.start();
    }

    public void stopAnimation() {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
    }

    public static void main(String[] args) {
        Landscape scape = new Landscape(500, 500, 1);
        Random gen = new Random();

        for (int i = 0; i < 100; i++) {
            scape.addAgent(new SocialAgent(gen.nextDouble() * scape.getWidth(),
                    gen.nextDouble() * scape.getHeight(), 25));
            scape.addAgent(new AntiSocialAgent(gen.nextDouble() * scape.getWidth(),
                    gen.nextDouble() * scape.getHeight(), 50));
        }

        LandscapeDisplay display = new LandscapeDisplay(scape);
        display.startAnimation(10);
    }
}