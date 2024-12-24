package com.ccs.baby.components;

import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.AbstractButton;
import java.awt.Insets;

import com.ccs.baby.utils.ImageUtils;

public class KeySwitch extends JButton {

    private static final Insets DEFAULT_MARGIN = new Insets(3, 3, 3, 3);
    private static final int DEFAULT_SCALE = 2400;

    /**
     * Constructs an InterlockingPushButton with the specified button text and vertical position for the text.
     *
     * @param buttonText       the text to be displayed on the button
     * @param downIconFilePath the file path of the icon to be displayed when the button is pressed
     * @param upIconFilePath   the file path of the icon to be displayed when the button is not pressed
     */
    public KeySwitch(String buttonText, String downIconFilePath, String upIconFilePath) {
        ImageIcon upIcon = ImageUtils.loadImage(upIconFilePath, DEFAULT_SCALE);
        ImageIcon downIcon = ImageUtils.loadImage(downIconFilePath, DEFAULT_SCALE);

        setIcon(upIcon);
        setPressedIcon(downIcon);

        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);

        setMargin(DEFAULT_MARGIN);

        setHorizontalTextPosition(AbstractButton.CENTER);
        setVerticalTextPosition(AbstractButton.NORTH);

        setText(buttonText);
    }
}