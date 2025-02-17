package com.ccs.baby.io;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import com.ccs.baby.core.Store;

public class SaveSnapshot implements ActionListener {

    private final String currentDir;
    private final Store store;
    private final JFrame frame;

    public SaveSnapshot(String currentDir, Store store, JFrame frame) {
        this.currentDir = currentDir;
        this.store = store;
        this.frame = frame;
    }

    public void actionPerformed(ActionEvent e) {

        // Open up a save box and choose name
        JFileChooser fc = new JFileChooser(currentDir);
        fc.setDialogTitle("Save snapshot as...");

        int returnVal = fc.showSaveDialog(frame.getContentPane());

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            try {
                store.saveSnapshot(file.toString());

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame.getContentPane(), ex.getMessage(), "SaveSnapshot Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        frame.getContentPane().repaint();
    }
}