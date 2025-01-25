package com.ccs.baby.ui.display;

import com.ccs.baby.ui.CrtPanel;
import com.ccs.baby.ui.CrtControlPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class is responsible for displaying the control.
 */
public class DisplayControl implements ActionListener {
    private final CrtPanel crtPanel;
    private final CrtControlPanel crtControlPanel;

    public DisplayControl(CrtPanel crtPanel, CrtControlPanel crtControlPanel) {
        this.crtPanel = crtPanel;
        this.crtControlPanel = crtControlPanel;
    }

    public void actionPerformed(ActionEvent e) {
        crtPanel.setCrtDisplay(CrtPanel.DisplayType.CONTROL);
        crtControlPanel.displayControlButton.setSelected(true);
    }
}
