package com.ccs.baby.ui.components;

import javax.swing.JButton;

public class DebugButton extends JButton {

    public DebugButton(String buttonName, String toolTipText) {
        super(buttonName);
        setToolTipText(toolTipText);
    }
}