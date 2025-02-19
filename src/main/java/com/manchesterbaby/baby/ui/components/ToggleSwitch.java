package com.manchesterbaby.baby.ui.components;

import javax.swing.*;

import com.manchesterbaby.baby.utils.ImageUtils;

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
     *  Constructs a ToggleSwitch with the specified button text and vertical position for the text.
     *
     * @param toolTipText The text to display when the button is hovered over.
     */
    public ToggleSwitch(String toolTipText) {

        setIcon(DEFAULT_UP_ICON);
        setSelectedIcon(DEFAULT_DOWN_ICON);
        setDisabledIcon(DEFAULT_HOLE_ICON);

        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);

        setSelected(false);

        setHorizontalTextPosition(AbstractButton.CENTER);
        setVerticalTextPosition(SwingConstants.TOP);

        setToolTipText(toolTipText);
    }
}




