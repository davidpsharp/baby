package com.ccs.baby.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.ccs.baby.ui.CrtPanel;
import com.ccs.baby.ui.SwitchPanel;

// change to display the accumulator
public class ViewAccumulator implements ActionListener {
    CrtPanel crtPanel;
    SwitchPanel switchPanel;

    public ViewAccumulator(CrtPanel aCrtPanel, SwitchPanel aSwitchPanel) {
        crtPanel = aCrtPanel;
        switchPanel = aSwitchPanel;
    }

    public void actionPerformed(ActionEvent e) {
        crtPanel.setCrtDisplay(CrtPanel.DisplayType.ACCUMULATOR);
        switchPanel.accSelect.setSelected(true);
    }
}