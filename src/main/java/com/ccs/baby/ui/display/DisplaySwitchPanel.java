package com.ccs.baby.ui.display;

import com.ccs.baby.ui.SwitchPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class is responsible for displaying the switch panel (done initially by default).
 */
public class DisplaySwitchPanel implements ActionListener {

    SwitchPanel switchPanel;

    public DisplaySwitchPanel(SwitchPanel aSwitchPanel) {
        switchPanel = aSwitchPanel;
    }

    public void actionPerformed(ActionEvent e) {
        switchPanel.setVisible(true);
    }
}
