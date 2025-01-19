package com.ccs.baby.ui.components;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import com.ccs.baby.utils.ImageUtils;

/**
 * A custom JButton that simulates a push switch with custom icons.
 */
public class PushButton extends JButton {

    private static final int DEFAULT_SCALE = 2400;

    // Cached default icons
    private static final ImageIcon DEFAULT_IN_ICON = ImageUtils.loadImageIcon("/images/pushin.gif", DEFAULT_SCALE);
    private static final ImageIcon DEFAULT_OUT_ICON = ImageUtils.loadImageIcon("/images/pushout.gif", DEFAULT_SCALE);

    /**
     * Constructs an PushButton with the specified button text and vertical position for the text.
     *
     * @param buttonText           the text to be displayed on the button
     * @param verticalTextPosition the vertical position of the text relative to the button icon
     */
    public PushButton(String buttonText, int verticalTextPosition) {
        setIcon(DEFAULT_OUT_ICON);
        setPressedIcon(DEFAULT_IN_ICON);

        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);

        setHorizontalTextPosition(AbstractButton.CENTER);
        setVerticalTextPosition(verticalTextPosition);

        setText(buttonText);
    }
}