package com.ccs.baby.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// display the switch panel (done initially by default)
public class ViewSwitchPanel implements ActionListener {

    SwitchPanel switchPanel;

    public ViewSwitchPanel(SwitchPanel aSwitchPanel) {
        switchPanel = aSwitchPanel;
    }

    public void actionPerformed(ActionEvent e) {
        switchPanel.setVisible(true);
    }
}
