package com.ccs.baby.ui.components;

import javax.swing.JCheckBox;
import javax.swing.ImageIcon;

import com.ccs.baby.utils.ImageUtils;

/**
 * A custom JCheckBox that simulates a toggle switch with custom icons.
 */
public class ToggleSwitch extends JCheckBox {

    private static final int DEFAULT_SCALE = 12400;

    // Cached default icons
    private static final ImageIcon DEFAULT_UP_ICON = ImageUtils.loadImageIcon("/images/toggle_switch_up.png", DEFAULT_SCALE);
    private static final ImageIcon DEFAULT_DOWN_ICON = ImageUtils.loadImageIcon("/images/toggle_switch_down.png", DEFAULT_SCALE);
    private static final ImageIcon DEFAULT_HOLE_ICON = ImageUtils.loadImageIcon("/images/hole.gif", DEFAULT_SCALE);

    /**
     * Constructs an ToggleSwitch with the specified button text with vertical and horizontal position for the text.
     *
     * @param buttonText             the text to be displayed on the button
     * @param verticalTextPosition   the vertical position of the text relative to the button icon
     * @param horizontalTextPosition the horizontal position of the text relative to the button icon
     */
    public ToggleSwitch(String buttonText, int horizontalTextPosition, int verticalTextPosition) {

        setIcon(DEFAULT_UP_ICON);
        setSelectedIcon(DEFAULT_DOWN_ICON);
        setDisabledIcon(DEFAULT_HOLE_ICON);

        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);

        setSelected(false);

        setHorizontalTextPosition(horizontalTextPosition);
        setVerticalTextPosition(verticalTextPosition);

        setText(buttonText);
    }
}




