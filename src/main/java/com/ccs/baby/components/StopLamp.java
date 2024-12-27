package com.ccs.baby.components;

import com.ccs.baby.utils.ImageUtils;

import javax.swing.*;
import java.awt.*;

public class StopLamp extends JButton {

    // stop lamp and icons
    public JButton stopLamp;
    private static ImageIcon onIcon;
    private static ImageIcon offIcon;

    public StopLamp() {
        stopLamp = new JButton(offIcon);
        stopLamp.setFocusPainted(false);
        stopLamp.setBorderPainted(false);
        stopLamp.setContentAreaFilled(false);
        Insets marginSpace = new Insets(3,3,3,3); // set margin
        stopLamp.setMargin(marginSpace);
        stopLamp.setToolTipText("Lamp lit when the STP instruction is executed.");
    }
}