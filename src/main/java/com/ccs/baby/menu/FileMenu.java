package com.ccs.baby.menu;

import com.ccs.baby.io.LoadSnapshotAssembly;
import com.ccs.baby.io.SaveAssembly;
import com.ccs.baby.io.SaveSnapshot;
import com.ccs.baby.core.Store;
import com.ccs.baby.core.Control;
import com.ccs.baby.controller.CrtPanelController;
import com.ccs.baby.utils.RecentFilesManager.RecentFileEntry;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.JOptionPane;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Creates the File menu.
 *
 * @author [Your Name]
 */
public class FileMenu {
    private static JMenu recentFilesMenu;

    /**
     * Creates the File menu.
     *
     * @param store      the store object
     * @param control    the control object
     * @param currentDir the current directory path
     * @param frame      the frame object
     * @param crtPanel   the crt panel object
     * @return the File menu
     */
    public static JMenu createFileMenu(Store store, String currentDir, JFrame frame, CrtPanelController crtPanelController) {
        // Create the File menu
        JMenu fileMenu = new JMenu("File");

        // Create menu items
        JMenuItem loadSnapshotAssembly = new JMenuItem("Load snapshot/assembly");
        recentFilesMenu = new JMenu("Load Recent");
        updateRecentFilesMenu(store, frame, crtPanelController);
        JMenuItem saveSnapshot = new JMenuItem("Save snapshot");
        JMenuItem saveAssembly = new JMenuItem("Save assembly");
        JMenuItem close = new JMenuItem("Close");

        // Add action listeners for each item
        loadSnapshotAssembly.addActionListener(new LoadSnapshotAssembly(store, frame, crtPanelController));
        saveSnapshot.addActionListener(new SaveSnapshot(currentDir, store, frame));
        saveAssembly.addActionListener(new SaveAssembly(currentDir, store, frame));
        close.addActionListener(e -> System.exit(0));

        // Set mnemonics (keyboard shortcuts)
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            // MacOS specific mnemonics
            loadSnapshotAssembly.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.META_DOWN_MASK)); // Cmd + L
            saveSnapshot.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.META_DOWN_MASK)); // Cmd + S
            saveAssembly.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.META_DOWN_MASK)); // Cmd + A
            close.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.META_DOWN_MASK)); // Cmd + W
        } else {
            // Windows/Linux specific mnemonics
            fileMenu.setMnemonic(KeyEvent.VK_F); // Alt + F
            loadSnapshotAssembly.setMnemonic(KeyEvent.VK_L); // Alt + L
            saveSnapshot.setMnemonic(KeyEvent.VK_S); // Alt + S
            saveAssembly.setMnemonic(KeyEvent.VK_A); // Alt + A
            close.setMnemonic(KeyEvent.VK_C); // Alt + C
        }

        // Add items to the file menu
        fileMenu.add(loadSnapshotAssembly);
        fileMenu.add(recentFilesMenu);
        fileMenu.addSeparator();
        fileMenu.add(saveSnapshot);
        fileMenu.add(saveAssembly);
        fileMenu.addSeparator();
        fileMenu.add(close);

        return fileMenu;
    }

    public static void updateRecentFilesMenu(Store store, JFrame frame, CrtPanelController crtPanelController) {
        if (recentFilesMenu == null) {
            return;
        }
        recentFilesMenu.removeAll();
        List<RecentFileEntry> recentFiles = store.getRecentFilesManager().getRecentFiles();

        if (recentFiles.isEmpty()) {
            JMenuItem noRecentFiles = new JMenuItem("No Recent Files");
            noRecentFiles.setEnabled(false);
            recentFilesMenu.add(noRecentFiles);
        } else {
            for (RecentFileEntry entry : recentFiles) {
                JMenuItem menuItem = new JMenuItem(entry.getFile().getName());
                menuItem.addActionListener(e -> {
                    try {
                        switch (entry.getLoadMethod()) {
                            case "loadModernAssembly":
                                store.loadModernAssembly(entry.getFile().getPath());
                                break;
                            case "loadLocalModernAssembly":
                                store.loadLocalModernAssembly(entry.getFile().getPath());
                                break;
                            case "loadSnapshot":
                                store.loadSnapshot(entry.getFile().getPath());
                                break;
                            default:
                                throw new IllegalStateException("Unknown load method: " + entry.getLoadMethod());
                        }
                        crtPanelController.redrawCrtPanel();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame,
                                "Error loading file: " + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                });
                menuItem.setToolTipText(entry.getFile().getAbsolutePath());
                recentFilesMenu.add(menuItem);
            }
        }

        // Add separator and Clear List option
        recentFilesMenu.addSeparator();
        JMenuItem clearList = new JMenuItem("Clear List");
        clearList.addActionListener(e -> {
            store.getRecentFilesManager().clearRecentFiles();
            updateRecentFilesMenu(store, frame, crtPanelController);
        });
        recentFilesMenu.add(clearList);
    }
}