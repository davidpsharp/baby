package com.ccs.baby.io;

import com.ccs.baby.core.Store;
import com.ccs.baby.menu.FileMenu;
import com.ccs.baby.controller.CrtPanelController;

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
    private final CrtPanelController crtPanelController;
    private final JFrame frame;


    public LoadSnapshotAssembly(Store store, JFrame frame, CrtPanelController crtPanelController) {
        this.store = store;
        this.crtPanelController = crtPanelController;
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
        handleFileLoad(file, false);
    }

    public void handleFileLoad(File file, boolean skipRender) {
        try {
            String name = file.getName().toLowerCase();
            String loadMethod;

            if (name.endsWith(".snp")) {
                store.loadSnapshot(file.getPath());
                loadMethod = "loadSnapshot";
            } else if (name.endsWith(".asm")) {
                store.loadModernAssembly(file.getPath());
                loadMethod = "loadModernAssembly";
            } else {
                throw new IllegalArgumentException("Unknown file type. Must be .snp or .asm");
            }

            // Add to recent files
            store.getRecentFilesManager().addRecentFile(file.getPath(), loadMethod);
            FileMenu.updateRecentFilesMenu(store, frame, crtPanelController);

            // Only render if not skipped
            if (!skipRender) {
                crtPanelController.redrawCrtPanel();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame.getContentPane(),
                    "Error loading file: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(e);
        }
    }
}
