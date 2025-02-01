package com.ccs.baby.io;

import com.ccs.baby.core.Control;
import com.ccs.baby.core.Store;
import com.ccs.baby.ui.CrtPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

// deal with clicks on the "Load snapshot/assembly" menu item
public class LoadSnapshotAssembly implements ActionListener {

    boolean firstLoad = true;
    private File fileChooserDirectory;
    private static String currentDir;
    private final Store store;
    private final Control control;
    private final CrtPanel crtPanel;
    private final JFrame frame;


    public LoadSnapshotAssembly(Store store, Control control, JFrame frame, CrtPanel crtPanel) {
        this.store = store;
        this.control = control;
        this.crtPanel = crtPanel;
        this.frame = frame;

        currentDir = System.getProperty("user.home");
    }

    public void actionPerformed(ActionEvent e) {
        JFileChooser fc;
        // Open up a load box and select the item
        if (firstLoad)
            fc = new JFileChooser(currentDir);
        else
            fc = new JFileChooser(fileChooserDirectory);
        firstLoad = false;

        fc.setDialogTitle("Load snapshot or assembly...");

        int returnVal = fc.showOpenDialog(frame.getContentPane());

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            fileChooserDirectory = file.getParentFile();
            handleFileLoad(file);
        }
    }

    public void handleFileLoad(File file) {
        try {
            String currentFile = file.toString();
            // detect file type and then load appropriately if possible
            switch (store.getFileType(currentFile)) {
                case Store.UNACCEPTABLE:
                    JOptionPane.showMessageDialog(frame.getContentPane(), "Unrecognised file type", "Error", JOptionPane.ERROR_MESSAGE);
                    break;
                case Store.SNAPSHOT:
                    store.loadSnapshot(currentFile);
                    break;
                case Store.ASSEMBLY:
                    store.loadModernAssembly(currentFile);
                    break;
                default:
                    JOptionPane.showMessageDialog(frame.getContentPane(), "Unrecognised file type", "Error", JOptionPane.ERROR_MESSAGE);
                    break;
            }
            // update display
            crtPanel.render();
            frame.getContentPane().repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame.getContentPane(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("fnf");
        }
    }
}
