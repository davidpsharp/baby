package com.ccs.baby.components;

import javax.swing.JRadioButton;
import javax.swing.ImageIcon;
import javax.swing.AbstractButton;
import java.awt.Insets;

import com.ccs.baby.utils.ImageUtils;

/**
 * A custom JRadioButton that simulates an interlocking push button with custom icons.
 */
public class InterlockingPushButton extends JRadioButton {

    private static final Insets DEFAULT_MARGIN = new Insets(3, 3, 3, 3);
    private static final int DEFAULT_SCALE = 2400;

    // Cached default icons
    private static final ImageIcon DEFAULT_IN_ICON = ImageUtils.loadImageIcon("/images/ibuttonin.gif", DEFAULT_SCALE);
    private static final ImageIcon DEFAULT_OUT_ICON = ImageUtils.loadImageIcon("/images/ibuttonout.gif", DEFAULT_SCALE);

    /**
     * Constructs an InterlockingPushButton with the specified button text and vertical position for the text.
     *
     * @param buttonText           the text to be displayed on the button
     * @param verticalTextPosition the vertical position of the text relative to the button icon
     */
    public InterlockingPushButton(String buttonText, int verticalTextPosition) {
        setIcon(DEFAULT_OUT_ICON);
        setSelectedIcon(DEFAULT_IN_ICON);

        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);

        setMargin(DEFAULT_MARGIN);

        setHorizontalTextPosition(AbstractButton.CENTER);
        setVerticalTextPosition(verticalTextPosition);

        setText(buttonText);
    }
}