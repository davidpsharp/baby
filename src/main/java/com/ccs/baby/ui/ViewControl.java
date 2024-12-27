package com.ccs.baby.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// change to display the Control
public class ViewControl implements ActionListener {
    CrtPanel crtPanel;
    SwitchPanel switchPanel;

    public ViewControl(CrtPanel aCrtPanel, SwitchPanel aSwitchPanel) {
        crtPanel = aCrtPanel;
        switchPanel = aSwitchPanel;
    }

    public void actionPerformed(ActionEvent e) {
        crtPanel.setCrtDisplay(CrtPanel.DisplayType.CONTROL);
        switchPanel.crSelect.setSelected(true);
    }
}
