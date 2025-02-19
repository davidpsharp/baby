package com.manchesterbaby.baby.ui.components;
import javax.swing.*;
import java.awt.*;

public class StopLamp extends JButton {

    // stop lamp and icons
    public JButton stopLamp;
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