package com.ccs.baby.ui;

import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ViewDebugPanel implements ActionListener {

    private final DebugPanel debugPanel;

    public ViewDebugPanel(DebugPanel aDebugPanel) {
        this.debugPanel = aDebugPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        boolean isVisible = !debugPanel.isVisible();
        debugPanel.setVisible(isVisible);
        JMenuItem source = (JMenuItem) e.getSource();
        source.setSelected(isVisible); // Update the menu item's checkmark
    }
}