package com.ccs.baby.ui;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.awt.Image;

import com.ccs.baby.utils.ImageUtils;

/**
 * A custom JPanel that displays the Main Panel texture.
 */
public class BackgroundPanel extends JPanel {

    BufferedImage currentImage;

    // Cached images
    private static final Image DEFAULT_PANEL = ImageUtils.loadImage("/images/main.png", 800000);
    private static Image PANEL_LAMP_ON;
    private static final Image PANEL_LAMP_ONLY = ImageUtils.loadImage("/images/mainonlamp.png", 800000);

    /**
     * Constructs a new BackgroundPanel with the default panel texture.
     */
    public BackgroundPanel() {
        if (DEFAULT_PANEL == null) {
            throw new IllegalArgumentException("Default panel image could not be loaded");
        }

        // create the combined overlay image for when lamp is on at run-time to reduce JAR size
        PANEL_LAMP_ON = ImageUtils.combineImages(DEFAULT_PANEL, PANEL_LAMP_ONLY);

        currentImage = ImageUtils.toBufferedImage(DEFAULT_PANEL);
    }

    /**
     * Sets the texture based on the given state.
     *
     * @param isLampOn If true, sets the texture to the "lamp on" panel.
     *                 If false, sets the texture to the default panel.
     */
    public void setTexture(boolean isLampOn) {
        
        Image targetImage = isLampOn ? PANEL_LAMP_ON : DEFAULT_PANEL;

        if (targetImage == null) {
            System.err.println("Target image could not be loaded");
            return;
        }

        currentImage = ImageUtils.toBufferedImage(targetImage);

        repaint(); // Repaint to reflect the change immediately
    }


    /**
     * Paints the component with the current texture.
     *
     * @param graphics The Graphics object to paint with.
     */
    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        // Ensure we have a valid Graphics2D object
        if (!(graphics instanceof Graphics2D)) {
            System.err.println("Graphics object is not an instance of Graphics2D.");
            return;
        }

        Graphics2D graphics2D = (Graphics2D) graphics;

        // Define the area for the texture paint
        java.awt.geom.Rectangle2D textureArea = new java.awt.geom.Rectangle2D.Double(0, 0, getWidth(), getHeight());
        TexturePaint texturePaint = new TexturePaint(currentImage, textureArea);

        // Set the texture paint and fill the component's bounds
        graphics2D.setPaint(texturePaint);
        graphics2D.fill(new java.awt.geom.Rectangle2D.Double(0, 0, getWidth(), getHeight()));
    }
}