package com.ccs.baby.io;

import com.ccs.baby.core.Baby;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.ccs.baby.core.Store;
import com.ccs.baby.ui.CrtPanel;


public class LoadExample implements ActionListener {

    private final String fileName;
    private final Store store;
    private final CrtPanel crtPanel;
    private final JFrame frame;

    public LoadExample(String name, Store store, CrtPanel crtPanel, JFrame frame) {
        fileName = name;
        this.store = store;
        this.crtPanel = crtPanel;
        this.frame = frame;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            if (fileName.equals("demos/noodletimer.snp") || fileName.equals("demos/Baby9.snp"))
                store.loadLocalSnapshot(fileName);
            else
                store.loadLocalModernAssembly(fileName);
            Baby.mainPanel.setTexture(false);
            crtPanel.render();
            crtPanel.repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame.getContentPane(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
