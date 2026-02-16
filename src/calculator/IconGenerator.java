package calculator;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class IconGenerator {
    public static void main(String[] args) {
        try {
            int size = 512;
            BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            // Modern gradient background (Pastel Green to Soft Pink)
            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(148, 180, 159), // Totoro Green
                size, size, new Color(212, 134, 156) // Ponyo Pink
            );
            g2d.setPaint(gradient);
            g2d.fill(new RoundRectangle2D.Float(30, 30, size - 60, size - 60, 100, 100));

            // Calculator screen (display)
            g2d.setColor(new Color(255, 253, 245, 240)); // Soft white
            g2d.fill(new RoundRectangle2D.Float(80, 100, size - 160, 100, 30, 30));
            
            // Display text "123"
            g2d.setColor(new Color(58, 58, 58));
            g2d.setFont(new Font("Arial", Font.BOLD, 60));
            g2d.drawString("123", 120, 170);

            // Calculator buttons (4x4 grid)
            int buttonSize = 70;
            int buttonSpacing = 20;
            int startX = 100;
            int startY = 240;
            
            Color[] buttonColors = {
                new Color(167, 196, 212), // Blue (C)
                new Color(212, 197, 169), // Amber
                new Color(148, 180, 159), // Green
                new Color(212, 134, 156)  // Pink (=)
            };

            // Draw 4x4 button grid
            for (int row = 0; row < 4; row++) {
                for (int col = 0; col < 4; col++) {
                    int x = startX + col * (buttonSize + buttonSpacing);
                    int y = startY + row * (buttonSize + buttonSpacing);
                    
                    // Choose color based on position
                    Color buttonColor;
                    if (col == 3) {
                        buttonColor = buttonColors[3]; // Right column (operators) = Pink
                    } else if (row == 0) {
                        buttonColor = buttonColors[0]; // Top row = Blue
                    } else {
                        buttonColor = buttonColors[2].brighter(); // Others = Light Green
                    }
                    
                    g2d.setColor(buttonColor);
                    g2d.fill(new RoundRectangle2D.Float(x, y, buttonSize, buttonSize, 20, 20));
                    
                    // Button shadow
                    g2d.setColor(new Color(0, 0, 0, 30));
                    g2d.setStroke(new BasicStroke(2));
                    g2d.draw(new RoundRectangle2D.Float(x, y, buttonSize, buttonSize, 20, 20));
                }
            }

            // Special highlight on "=" button (bottom right)
            g2d.setColor(new Color(212, 134, 156, 200));
            int eqX = startX + 3 * (buttonSize + buttonSpacing);
            int eqY = startY + 3 * (buttonSize + buttonSpacing);
            g2d.fill(new RoundRectangle2D.Float(eqX, eqY, buttonSize, buttonSize, 20, 20));
            
            // Draw "=" symbol
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 50));
            g2d.drawString("=", eqX + 20, eqY + 52);

            g2d.dispose();

            File outputFile = new File("src/calculator/icon.png");
            ImageIO.write(image, "png", outputFile);
            System.out.println("✨ Modern calculator icon generated at: " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
