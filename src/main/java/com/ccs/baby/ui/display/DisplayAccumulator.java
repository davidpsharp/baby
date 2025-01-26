package com.ccs.baby.ui.display;

import com.ccs.baby.ui.CrtPanel;
import com.ccs.baby.ui.CrtControlPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class is responsible for displaying the accumulator.
 */
public class DisplayAccumulator implements ActionListener {
    private final CrtPanel crtPanel;
    private final CrtControlPanel crtControlPanel;

    public DisplayAccumulator(CrtPanel crtPanel, CrtControlPanel crtControlPanel) {
        this.crtPanel = crtPanel;
        this.crtControlPanel = crtControlPanel;
    }

    public void actionPerformed(ActionEvent e) {
        crtPanel.setCrtDisplay(CrtPanel.DisplayType.ACCUMULATOR);
        crtControlPanel.displayAccumulatorButton.setSelected(true);
    }
}