package com.ccs.baby.ui.display;

import com.ccs.baby.debug.DebugPanel;
import com.ccs.baby.utils.AppSettings;

import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class is responsible for displaying the debug panel.
 */
public class DisplayDebugPanel implements ActionListener {

    private final DebugPanel debugPanel;

    public DisplayDebugPanel(DebugPanel aDebugPanel) {
        this.debugPanel = aDebugPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        boolean isVisible = !debugPanel.isVisible();
        debugPanel.setVisible(isVisible);
        JMenuItem source = (JMenuItem) e.getSource();
        source.setSelected(isVisible); // Update the menu item's checkmark
        AppSettings.getInstance().setShowDebugPanel(isVisible);
    }
}