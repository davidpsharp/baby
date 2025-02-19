package com.manchesterbaby.baby.io;

import javax.swing.*;

import com.manchesterbaby.baby.core.Store;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;


public class SaveAssembly implements ActionListener {

    private final String currentDir;
    private final Store store;
    private final JFrame frame;

    public SaveAssembly(String currentDir, Store store, JFrame frame) {
        this.currentDir = currentDir;
        this.store = store;
        this.frame = frame;
    }


    public void actionPerformed(ActionEvent e) {

        // Open up a save box and choose name
        JFileChooser fc = new JFileChooser(currentDir);
        fc.setDialogTitle("Save assembly as...");

        int returnVal = fc.showSaveDialog(frame.getContentPane());

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            try {
                store.saveAssembly(file.toString());

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame.getContentPane(), ex.getMessage(), "SaveAssembly Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        frame.getContentPane().repaint();
    }
}