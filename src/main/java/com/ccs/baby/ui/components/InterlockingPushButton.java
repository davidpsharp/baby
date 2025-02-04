package com.ccs.baby.ui.components;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

import java.awt.Insets;

import com.ccs.baby.utils.ImageUtils;

public class InterlockingPushButton extends JRadioButton {

    private static final Insets DEFAULT_MARGIN = new Insets(3, 3, 3, 3);
    private static final int DEFAULT_SCALE = 2400;

    // Cached default icons
    private static final ImageIcon DEFAULT_IN_ICON = ImageUtils.loadImageIcon("/images/ibuttonin.gif", DEFAULT_SCALE);
    private static final ImageIcon DEFAULT_OUT_ICON = ImageUtils.loadImageIcon("/images/ibuttonout.gif", DEFAULT_SCALE);

    /**
     * A custom JRadioButton that simulates an interlocking push button with custom icons.
     *
     * @param toolTipText The text to display when the button is hovered over.
     */
    public InterlockingPushButton(String toolTipText) {
        setIcon(DEFAULT_OUT_ICON);
        setSelectedIcon(DEFAULT_IN_ICON);

        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);

        setMargin(DEFAULT_MARGIN);

        setHorizontalTextPosition(AbstractButton.CENTER);
        setVerticalAlignment(SwingConstants.TOP);

        setToolTipText(toolTipText);
    }
}