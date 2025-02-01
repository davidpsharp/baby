package com.ccs.baby.io;

import com.ccs.baby.core.Baby;
import com.ccs.baby.core.Control;
import com.ccs.baby.core.Store;
import com.ccs.baby.menu.FileMenu;
import com.ccs.baby.ui.CrtPanel;
import com.ccs.baby.utils.MiscUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;

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
            if (fileName.endsWith(".snp")) {
                store.loadLocalSnapshot(fileName);
            } else if(fileName.endsWith(".asm")) {
                store.loadLocalModernAssembly(fileName);
            } else {
                throw new IOException("Unknown example file load, not .asm or .snp");
            }

            Baby.mainPanel.setTexture(false);
            crtPanel.render();
            crtPanel.repaint();
            
            // Update recent files menu
            FileMenu.updateRecentFilesMenu(store, frame, crtPanel);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame.getContentPane(), MiscUtils.getStackTrace(ex), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static String getUriStringForResource(String fileName) throws URISyntaxException
    {
        ClassLoader classLoader = Baby.class.getClassLoader();
        return classLoader.getResource(fileName).toURI().toString();
    }
}
