package com.ccs.baby.ui.display;

import com.ccs.baby.ui.CrtPanel;
import com.ccs.baby.ui.SwitchPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class is responsible for displaying the accumulator.
 */
public class DisplayAccumulator implements ActionListener {
    CrtPanel crtPanel;
    SwitchPanel switchPanel;

    public DisplayAccumulator(CrtPanel aCrtPanel, SwitchPanel aSwitchPanel) {
        crtPanel = aCrtPanel;
        switchPanel = aSwitchPanel;
    }

    public void actionPerformed(ActionEvent e) {
        crtPanel.setCrtDisplay(CrtPanel.DisplayType.ACCUMULATOR);
        switchPanel.accSelect.setSelected(true);
    }
}