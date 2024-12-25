package com.ccs.baby.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// change to display store
public class ViewStore implements ActionListener {
    CrtPanel crtPanel;
    SwitchPanel switchPanel;

    public ViewStore(CrtPanel aCrtPanel, SwitchPanel aSwitchPanel) {
        crtPanel = aCrtPanel;
        switchPanel = aSwitchPanel;
    }

    public void actionPerformed(ActionEvent e) {
        crtPanel.setCrtDisplay(CrtPanel.DisplayType.STORE);
        switchPanel.storeSelect.setSelected(true);
    }
}
