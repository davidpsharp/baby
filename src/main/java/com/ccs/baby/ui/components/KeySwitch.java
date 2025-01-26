package com.ccs.baby.ui.components;

import com.ccs.baby.utils.ImageUtils;

import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.AbstractButton;
import javax.swing.SwingConstants;

import java.awt.Insets;
import java.awt.Image;

public class KeySwitch extends JButton {

    private static final Insets DEFAULT_MARGIN = new Insets(3, 3, 3, 3);
    private static final int DEFAULT_SCALE = 2400;

    /**
     * Enum for the colours of the KeySwitch.
     */
    public enum KeyColour {
        GREY("/images/greyup.png", "/images/greydown.png"),
        WHITE("/images/whiteup.png", "/images/whitedown.png");

        private final Image upIcon;
        private final Image downIcon;

        KeyColour(String upIconPath, String downIconPath) {
            this.upIcon = ImageUtils.loadImage(upIconPath, DEFAULT_SCALE);
            this.downIcon = ImageUtils.loadImage(downIconPath, DEFAULT_SCALE);
            if (this.upIcon == null || this.downIcon == null) {
                throw new IllegalArgumentException("Image could not be loaded: " + upIconPath + " or " + downIconPath);
            }
        }

        public Image getUpIcon() {
            return upIcon;
        }

        public Image getDownIcon() {
            return downIcon;
        }
    }

    /**
     * Constructs a KeySwitch with the specified tooltip text and color.
     *
     * @param toolTipText the text to display when the button is hovered over
     * @param keyColour   the color of the KeySwitch (GREY or WHITE)
     */
    public KeySwitch(String toolTipText, KeyColour keyColour) {
        if (keyColour == null) {
            throw new IllegalArgumentException("KeyColour cannot be null");
        }

        setIcon(new ImageIcon(keyColour.getUpIcon()));
        setPressedIcon(new ImageIcon(keyColour.getDownIcon()));

        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);

        setMargin(DEFAULT_MARGIN);

        setHorizontalTextPosition(AbstractButton.CENTER);
        setVerticalTextPosition(SwingConstants.TOP);

        setText("");
        setToolTipText(toolTipText);
    }
}