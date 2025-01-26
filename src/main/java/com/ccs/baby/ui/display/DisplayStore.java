package com.ccs.baby.ui.display;

import com.ccs.baby.ui.CrtPanel;
import com.ccs.baby.ui.CrtControlPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class is responsible for displaying the store.
 */
public class DisplayStore implements ActionListener {
    private final CrtPanel crtPanel;
    private final CrtControlPanel crtControlPanel;

    public DisplayStore(CrtPanel crtPanel, CrtControlPanel crtControlPanel) {
        this.crtPanel = crtPanel;
        this.crtControlPanel = crtControlPanel;
    }

    public void actionPerformed(ActionEvent e) {
        crtPanel.setCrtDisplay(CrtPanel.DisplayType.STORE);
        crtControlPanel.displayStoreButton.setSelected(true);
    }
}
