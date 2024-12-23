package com.ccs.baby.utils;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;

public class ImageUtils {

    /**
     * Loads an image from the classpath and returns it as an ImageIcon.
     *
     * @param imagePath    The path to the image file in the classpath.
     * @param maxImageSize The biggest image in bytes.
     * @return An ImageIcon if successful, null otherwise.
     */
    public static ImageIcon loadImage(String imagePath, int maxImageSize) {
        // Use the current class loader to get the resource stream
        try (BufferedInputStream imgStream = new BufferedInputStream(
                ImageUtils.class.getResourceAsStream(imagePath))) {
            if (imgStream == null) {
                System.err.println("Couldn't find file: " + imagePath);
                return null;
            }

            byte[] buf = new byte[maxImageSize];
            int count;
            try {
                count = imgStream.read(buf);
            } catch (IOException ioe) {
                System.err.println("Couldn't read stream from file: " + imagePath + " - " + ioe.getMessage());
                return null;
            }

            if (count <= 0) {
                System.err.println("Empty file: " + imagePath);
                return null;
            }

            return new ImageIcon(Toolkit.getDefaultToolkit().createImage(buf));
        } catch (IOException e) {
            System.err.println("Error opening the stream for file: " + imagePath + " - " + e.getMessage());
            return null;
        }
    }

    /**
     * Loads an image from the classpath and returns it as an {@link Image}.
     *
     * @param fileName The name or path of the image file relative to the classpath.
     *                 For example, "images/example.png" if the image is located in
     *                 the "src/main/resources/images" directory.
     * @return An {@link Image} object if the file is found and loaded successfully,
     * or {@code null} if the file does not exist or cannot be loaded.
     */
    public static Image getImage(String fileName) {
        // Get the URL of the resource file
        URL imageURL = ImageUtils.class.getClassLoader().getResource(fileName);
        if (imageURL == null) {
            System.err.println("Error: Image file not found - " + fileName);
            return null;
        }

        // Load and return the image using ImageIcon
        return new ImageIcon(imageURL).getImage();
    }
}