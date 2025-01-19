package com.ccs.baby.utils;

import javax.swing.ImageIcon;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class for loading and processing images from the classpath.
 */
public class ImageUtils {

    /**
     * Loads an image from the classpath and returns it as an ImageIcon.
     *
     * @param imagePath    The path to the image file in the classpath.
     * @param maxImageSize The maximum size of the image in bytes.
     * @return An ImageIcon if the image is successfully loaded, null otherwise.
     */
    public static ImageIcon loadImageIcon(String imagePath, int maxImageSize) {
        Image image = loadImage(imagePath, maxImageSize);
        return image != null ? new ImageIcon(image) : null;
    }

    /**
     * Loads an image from the classpath.
     *
     * @param imagePath    The path to the image file in the classpath.
     * @param maxImageSize The maximum size of the image in bytes.
     * @return The loaded Image, or null if loading fails.
     */
    public static Image loadImage(String imagePath, int maxImageSize) {
        InputStream resourceStream = ImageUtils.class.getResourceAsStream(imagePath);
        if (resourceStream == null) {
            System.err.println("Couldn't find file: " + imagePath);
            return null;
        }

        try (BufferedInputStream imgStream = new BufferedInputStream(resourceStream)) {
            byte[] buf = new byte[maxImageSize];
            int count = imgStream.read(buf);

            if (count <= 0) {
                System.err.println("Empty or unreadable file: " + imagePath);
                return null;
            }

            Image image = Toolkit.getDefaultToolkit().createImage(buf);
            try {
                ensureImageIsLoaded(image);
                return image;
            } catch (IllegalArgumentException e) {
                System.err.println("Failed to fully load image: " + e.getMessage());
                return null;
            }
        } catch (IOException e) {
            System.err.println("Error loading image: " + imagePath + " - " + e.getMessage());
            return null;
        }
    }


    /**
     * Ensures that the provided image is fully loaded.
     *
     * @param image The Image to check.
     * @throws IllegalArgumentException if the image cannot be fully loaded.
     */
    static void ensureImageIsLoaded(Image image) {

        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null.");
        }

        MediaTracker tracker = new MediaTracker(new Component() {
        });
        tracker.addImage(image, 0);
        try {
            tracker.waitForAll();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Image loading interrupted: " + e.getMessage());
        }
        if (tracker.isErrorAny()) {
            throw new IllegalArgumentException("Failed to fully load image.");
        }
    }

    /**
     * Converts an Image to a BufferedImage.
     *
     * @param image The Image to convert.
     * @return The converted BufferedImage.
     * @throws IllegalArgumentException if the input image is null or has invalid dimensions.
     */
    public static BufferedImage toBufferedImage(Image image) {
        if (image == null) {
            throw new IllegalArgumentException("Input image cannot be null.");
        }

        int width = image.getWidth(null);
        int height = image.getHeight(null);

        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException(String.format("Invalid image dimensions: width=%d, height=%d", width, height));
        }

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = bufferedImage.createGraphics();
        try {
            graphics.drawImage(image, 0, 0, null);
        } finally {
            graphics.dispose();
        }
        return bufferedImage;
    }
}