package com.manchesterbaby.baby.menu;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.Image;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.manchesterbaby.baby.controller.CrtPanelController;
import com.manchesterbaby.baby.core.Store;
import com.manchesterbaby.baby.io.LoadSnapshotAssembly;
import com.manchesterbaby.baby.io.SaveAssembly;
import com.manchesterbaby.baby.io.SaveSnapshot;
import com.manchesterbaby.baby.utils.CheerpJUtils;
import com.manchesterbaby.baby.utils.MiscUtils;
import com.manchesterbaby.baby.utils.Version;
import com.manchesterbaby.baby.utils.RecentFilesManager.FileLocation;
import com.manchesterbaby.baby.utils.RecentFilesManager.RecentFileEntry;
import com.manchesterbaby.baby.menu.AboutDialog;

import javax.swing.JOptionPane;

import java.awt.Color;
import java.awt.Desktop;
import javax.swing.ImageIcon;

/**
 * Creates the File menu.
 *
 * @author [Your Name]
 */
public class FileMenu {
    private static JMenu recentFilesMenu;
    private static CrtPanelController _crtPanelController;
    
    /**
     * Creates the File menu.
     *
     * @param store      the store object
     * @param currentDir the current directory path
     * @param frame      the frame object
     * @param crtPanelController   the crt panel controller
     * @return the File menu
     */
    public static JMenu createFileMenu(Store store, String currentDir, JFrame frame, CrtPanelController crtPanelController) {
        // Create the File menu
        JMenu fileMenu = new JMenu("File");

        // Create menu items
        JMenuItem loadSnapshotAssembly = new JMenuItem("Load snapshot/assembly");
        JMenuItem loadURL = new JMenuItem("Load URL");

        // if on cheerpj add special menu item to ask javascript to show an open file dialog for host machine file system
        if(CheerpJUtils.onCheerpj())
        {
            JMenuItem loadLocalSnapshotAssembly = new JMenuItem("Load Local snapshot/assembly");
            // set up call direct to javascript function
            loadLocalSnapshotAssembly.addActionListener(e -> CheerpJUtils.getFileForSimulator());
            fileMenu.add(loadLocalSnapshotAssembly);
        }

        _crtPanelController = crtPanelController;
        recentFilesMenu = new JMenu("Load Recent");

        updateRecentFilesMenu(store, frame, crtPanelController);
        JMenuItem saveSnapshot = new JMenuItem("Save snapshot");
        JMenuItem saveAssembly = new JMenuItem("Save assembly");
        JMenuItem saveAsURL = new JMenuItem("Save as URL");

        JMenuItem close = new JMenuItem("Close");

        // Add action listeners for each item
        loadSnapshotAssembly.addActionListener(new LoadSnapshotAssembly(store, frame, crtPanelController));
        loadURL.addActionListener(e -> {
            String message = "Enter the Manchester Baby URL to load:";
            String title = "Paste in URL to Load";
            
            // Create a text area for URL input
            JTextArea textArea = new JTextArea();
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setBackground(Color.WHITE);
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 100));
            
            // Load and scale the baby icon
            ImageIcon originalIcon = new ImageIcon(AboutDialog.class.getResource("/icons/baby.png"));
            Image scaled = originalIcon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
            ImageIcon icon = new ImageIcon(scaled);
            
            int result = JOptionPane.showConfirmDialog(
                frame,
                scrollPane,
                title,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                icon
            );
            
            if (result == JOptionPane.OK_OPTION) {
                String url = textArea.getText().trim();
                if (!url.isEmpty()) {
                    try {
                        // Extract the program parameter from the URL
                        // TODO: move this logic all into standard URL loading code
                        URI uri = new URI(url);
                        String query = uri.getQuery();
                        // if is a wellformed query and found 's='' param name
                        if (query != null && query.indexOf("s=") != -1) {
                            int programParamIndex = query.indexOf("s=");
                            int endOfProgramIndex = query.indexOf("&", programParamIndex); // look for end pam
                            if(endOfProgramIndex == -1)
                            {
                                endOfProgramIndex = query.length();
                            }
                            String base64Program = query.substring(programParamIndex +2, endOfProgramIndex); // Remove "s=" and any trailing parameters
                            store.loadFromURLparam(base64Program);
                            _crtPanelController.redrawCrtPanel();
                        } else {
                            // just try and load it
                            store.loadFromURLparam(url);
                            _crtPanelController.redrawCrtPanel();
                        }
                    } catch (URISyntaxException | IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(
                            frame,
                            "Invalid URL format: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            }
        });
        saveSnapshot.addActionListener(new SaveSnapshot(currentDir, store, frame));
        saveAssembly.addActionListener(new SaveAssembly(currentDir, store, frame));
        saveAsURL.addActionListener(e -> {
            String url = "https://manchesterbaby.com?s=" + store.saveToURLparam();
            JTextArea textArea = new JTextArea(url);
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setBackground(Color.WHITE);
            
            // Set preferred size for wrapping
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 100));
            
            // Create custom buttons
            Object[] options = {
                "Open URL",
                "Copy to Clipboard",
                "Close"
            };
            
            // Load and scale the baby icon
            ImageIcon originalIcon = new ImageIcon(AboutDialog.class.getResource("/icons/baby.png"));
            Image scaled = originalIcon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
            ImageIcon icon = new ImageIcon(scaled);
            
            int choice = JOptionPane.showOptionDialog(
                frame,
                scrollPane,
                "Manchester Baby URL",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                icon,
                options,
                options[2] // Default to Close button
            );
            
            if (choice == 0) { // Open URL
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (IOException | URISyntaxException ex) {
                    JOptionPane.showMessageDialog(
                        frame,
                        "Failed to open URL: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            } else if (choice == 1) { // Copy to Clipboard
                Toolkit.getDefaultToolkit()
                    .getSystemClipboard()
                    .setContents(new StringSelection(url), null);
            }
        });

        close.addActionListener(e -> System.exit(0));

        // Set mnemonics (keyboard shortcuts)
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            // MacOS specific mnemonics
            loadSnapshotAssembly.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.META_DOWN_MASK)); // Cmd + L
            loadURL.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, KeyEvent.META_DOWN_MASK)); // Cmd + U
            saveSnapshot.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.META_DOWN_MASK)); // Cmd + S
            saveAssembly.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.META_DOWN_MASK)); // Cmd + A
            close.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.META_DOWN_MASK)); // Cmd + W
        } else {
            // Windows/Linux specific mnemonics
            fileMenu.setMnemonic(KeyEvent.VK_F); // Alt + F
            loadSnapshotAssembly.setMnemonic(KeyEvent.VK_L); // Alt + L
            loadURL.setMnemonic(KeyEvent.VK_U); // Alt + U
            saveSnapshot.setMnemonic(KeyEvent.VK_S); // Alt + S
            saveAssembly.setMnemonic(KeyEvent.VK_A); // Alt + A
            close.setMnemonic(KeyEvent.VK_C); // Alt + C
        }

        // Add items to the file menu
        fileMenu.add(loadSnapshotAssembly);
        fileMenu.add(loadURL);
        fileMenu.add(recentFilesMenu);
        fileMenu.addSeparator();
        fileMenu.add(saveSnapshot);
        fileMenu.add(saveAssembly);
        fileMenu.add(saveAsURL);
        
        fileMenu.addSeparator();

        // doesn't make sense if on CheerpJ, just close the browser tab/window when done
        if(!CheerpJUtils.onCheerpj()) {    
            fileMenu.add(close);
        }
        else {
            JMenuItem downloadSimulator = new JMenuItem("Download Simulator Java App");
            downloadSimulator.addActionListener(e -> MiscUtils.launchUrlInBrowser("https://davidsharp.com/baby/baby.jar"));
            fileMenu.add(downloadSimulator);
        }

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
                FileLocation location = entry.getLocation();
                JMenuItem menuItem = new JMenuItem(location.getDisplayName());
                menuItem.addActionListener(e -> {
                    try {
                        String path = location.getPath();
                        switch (entry.getLoadMethod()) {
                            case "assembly:filePath":  // TODO: switch all these to enums rather than strings
                                store.loadModernAssembly(path);
                                break;
                            case "assembly:URL":
                                store.loadLocalModernAssembly(path);
                                break;
                            case "snapshot:filePath":
                                store.loadSnapshot(path);
                                break;
                            case "snapshot:URL":
                                store.loadLocalSnapshot(path);
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
                menuItem.setToolTipText(location.getPath());
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