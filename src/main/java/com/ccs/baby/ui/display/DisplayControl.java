package com.ccs.baby.ui.display;

import com.ccs.baby.ui.CrtPanel;
import com.ccs.baby.ui.SwitchPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class is responsible for displaying the control.
 */
public class DisplayControl implements ActionListener {
    CrtPanel crtPanel;
    SwitchPanel switchPanel;

    public DisplayControl(CrtPanel aCrtPanel, SwitchPanel aSwitchPanel) {
        crtPanel = aCrtPanel;
        switchPanel = aSwitchPanel;
    }

    public void actionPerformed(ActionEvent e) {
        crtPanel.setCrtDisplay(CrtPanel.DisplayType.CONTROL);
        switchPanel.crSelect.setSelected(true);
    }
}
