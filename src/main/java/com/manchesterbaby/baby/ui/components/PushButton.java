package com.manchesterbaby.baby.ui.components;

import javax.swing.JButton;

import com.manchesterbaby.baby.utils.ImageUtils;

import javax.swing.ImageIcon;
import javax.swing.AbstractButton;

public class PushButton extends JButton {

    private static final int DEFAULT_SCALE = 2400;

    // Cached default icons
    private static final ImageIcon DEFAULT_IN_ICON = ImageUtils.loadImageIcon("/images/pushin.gif", DEFAULT_SCALE);
    private static final ImageIcon DEFAULT_OUT_ICON = ImageUtils.loadImageIcon("/images/pushout.gif", DEFAULT_SCALE);

    /**
     * A custom JButton that simulates a push switch with custom icons.
     *
     * @param toolTipText the text to display when the button is hovered over
     */
    public PushButton(String toolTipText) {
        setIcon(DEFAULT_OUT_ICON);
        setPressedIcon(DEFAULT_IN_ICON);

        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);

        setHorizontalTextPosition(AbstractButton.CENTER);
        setVerticalTextPosition(AbstractButton.CENTER);

        setText("");
        setToolTipText(toolTipText);
    }

    // hacky way to change icon without doClick()
    public void setIconPressed(boolean pressed)
    {
        if(pressed)
            setIcon(DEFAULT_IN_ICON);
        else
            setIcon(DEFAULT_OUT_ICON);
    }
}