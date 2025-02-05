package com.ccs.baby.core;

// Manchester Baby Simulator
// by David Sharp
// January 2001
// requires Java v8 or later

import java.awt.*;
import java.awt.event.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import javax.swing.*;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

import com.ccs.baby.controller.*;
import com.ccs.baby.controller.listener.*;
import com.ccs.baby.debug.*;
import com.ccs.baby.disassembler.*;
import com.ccs.baby.io.*;
import com.ccs.baby.manager.*;
import com.ccs.baby.menu.*;
import com.ccs.baby.ui.*;
import com.ccs.baby.utils.*;


public class Baby extends JFrame {

    // Get the current directory
    private static String currentDir;
    public static BackgroundPanel mainPanel;
    private CrtPanel crtPanel;
    private LoadSnapshotAssembly loadSnapshotAssembly;

    public Baby() {

        try {

            // turn off window title bar on CheerpJ
            if(CheerpJUtils.onCheerpj())
                this.setUndecorated(true);

            AppSettings settings = AppSettings.getInstance();

            setTitle("Manchester Baby Simulator v" + Version.getVersion());

            // In Baby constructor, after creating the window
            try {
                // Set window icon
                Image icon = new ImageIcon(getClass().getResource("/icons/baby.png")).getImage();
                setIconImage(icon);
                
                // For macOS dock icon
                if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                    // macOS-specific dock icon setting (requires Java 9 or later)
                    // Recall cheerpj is Java 8 only so can't compile this code and still target JRE 8.
                    // The line I want to have is...
                    //      java.awt.Taskbar.getTaskbar().setIconImage(icon);
                    // so I do equivalent code in reflection so can still compile for Java 8.
                    // If compiled/running on later version of JRE will set taskbar icon, and if running on Java 8
                    // then the exception will get trapped when reflection fails and the show goes on...
                    Class taskbarCls = Class.forName("java.awt.Taskbar");
                    java.lang.reflect.Method getTaskbarMethod = taskbarCls.getMethod("getTaskbar");
                    Object taskbarObj = getTaskbarMethod.invoke(null);

                    java.lang.reflect.Method setIconImageMethod=taskbarCls.getMethod("setIconImage", Image.class);
                    setIconImageMethod.invoke(taskbarObj, icon);
                }
            } catch (Exception e) {
                System.err.println("Could not load application icon: " + e);
            }

            try {
                currentDir = System.getProperty("user.home");
                System.out.println(currentDir);
            } catch (SecurityException e) {
                System.out.println("user.dir not accessible from applet");
                System.out.println(e.getMessage());
            }

            // Create LampManager
            LampManager lampManager = new LampManager();

            // Create main hardware components
            Store store = new Store();
            Control control = new Control(store, lampManager);
            store.setControl(control);

            crtPanel = new CrtPanel(store, control);
            crtPanel.setOpaque(false);
            crtPanel.setPreferredSize(new Dimension(400, 386));



            // Create switch panel components
            StaticisorPanel staticisorPanel = new StaticisorPanel();
            TypewriterPanel typewriterPanel = new TypewriterPanel();
            CrtControlPanel crtControlPanel = new CrtControlPanel();

            // A container for all switch panel components
            JPanel switchPanel = new JPanel();
            switchPanel.setLayout(new BoxLayout(switchPanel, BoxLayout.Y_AXIS));
            switchPanel.setOpaque(false);
            switchPanel.add(typewriterPanel);
            switchPanel.add(staticisorPanel);
            switchPanel.add(crtControlPanel);

            // Setup a DebugPanel (aka modernControls)
            DebugPanel debugPanel = new DebugPanel(control);
            debugPanel.setOpaque(true);

            // Get the FpsLabelService from the debugPanel
            FpsLabelService fpsLabelService = debugPanel.getFpsLabelService();

            // Create CrtPanelActionLineListener
            CrtPanelActionLineListener crtPanelActionLineListener = new CrtPanelActionLineListener(control, crtPanel);

            // Create StaticisorPanelController and register crtPanelActionLineListener as a listener
            StaticisorPanelController staticisorPanelController = new StaticisorPanelController(staticisorPanel);
            staticisorPanelController.addActionLineListener(crtPanelActionLineListener);


            CrtPanelController crtPanelController = new CrtPanelController(crtPanel);

            // Initialise AnimationManager
            AnimationManager animationManager = new AnimationManager(control, crtPanelController, staticisorPanelController, fpsLabelService);

            // Create LoadSnapshotAssembly instance
            loadSnapshotAssembly = new LoadSnapshotAssembly(store, this, crtPanelController);

            // Create Disassembler
            Disassembler disassembler = new Disassembler(store, control, crtPanelController);

            CrtControlPanelController crtControlPanelController = new CrtControlPanelController(store, control, disassembler, animationManager, crtControlPanel, crtPanelController, staticisorPanelController);

            // Register UI update listener
            CrtPanelDisplayTypeListener crtPanelDisplayTypeListener = new CrtPanelDisplayTypeListener(crtControlPanelController);
            crtPanelController.addDisplayTypeListener(crtPanelDisplayTypeListener);

            TypewriterPanelController typewriterPanelController = new TypewriterPanelController(store, control, typewriterPanel, crtPanelController, staticisorPanelController, crtControlPanelController);



            // Tell control about staticisorPanel and crtControlPanel
            control.setSwitchPanel(staticisorPanelController, crtControlPanelController);

            // Create a container mainPanel that wraps crtPanel and switchPanel
            mainPanel = new BackgroundPanel();
            mainPanel.setLayout(new BorderLayout());
            mainPanel.setSize(690, 905);
            mainPanel.add(crtPanel, BorderLayout.NORTH);
            mainPanel.add(switchPanel);

            // Set up display window GUI
            Container contentPane = getContentPane();
            contentPane.setLayout(new BorderLayout());
            contentPane.add(mainPanel, BorderLayout.CENTER);
            contentPane.add(debugPanel, BorderLayout.SOUTH);

            // Set up and add menu bars to the window
            JMenuBar menuBar = new JMenuBar();
            setJMenuBar(menuBar);
            new MenuSetup(menuBar, store, control, crtPanelController, disassembler, currentDir, this, debugPanel);

            // Reset the hardware to initial values
            store.reset();
            control.reset();

            // Load an initial program
            try {
                
                String defaultFile = settings.getInitialExample();
                String uriString = LoadExample.getUriStringForResource(defaultFile);
                store.loadLocalModernAssembly(uriString);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(getContentPane(), "Default program not loaded. " + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            }

            // render and display the CRT display
            crtPanelController.redrawCrtPanel();

            // Open window
            setVisible(true);

            // This helps toggle the visibility of the debugPanel
            contentPane.revalidate();
            contentPane.repaint();

            // Setup drag and drop
            setupDragAndDrop();

            // quit program when close icon clicked
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    PreferencesService preferences = PreferencesService.getInstance();
                    preferences.savePreferences(); 
                    System.exit(0);
                }
            });
        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(getContentPane(), MiscUtils.getStackTrace(e), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setupDragAndDrop() {
        // Create the drop target listener
        DropTargetListener dropListener = new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent event) {
                try {
                    event.acceptDrop(DnDConstants.ACTION_COPY);
                    Transferable transferable = event.getTransferable();
                    
                    if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        @SuppressWarnings("unchecked")
                        List<File> files = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
                        
                        List<File> allFiles = new ArrayList<>();
                        // Process all dropped files/folders
                        for (File file : files) {
                            if (file.isDirectory()) {
                                processDirectory(file, allFiles);
                            } else if (isValidFileType(file)) {
                                allFiles.add(file);
                            }
                        }
                        
                        // Process all collected files
                        for (int i = 0; i < allFiles.size(); i++) {
                            File file = allFiles.get(i);
                            try {
                                // Only render and repaint on the last file
                                boolean isLastFile = (i == allFiles.size() - 1);
                                loadSnapshotAssembly.handleFileLoad(file, !isLastFile);
                            } catch (Exception e) {
                                // Show error but continue processing other files
                                JOptionPane.showMessageDialog(getContentPane(), 
                                    "Error loading file " + file.getName() + ": " + e.getMessage(), 
                                    "Error", 
                                    JOptionPane.ERROR_MESSAGE);
                            }
                        }

                        // Request focus after processing files
                        requestFocus();
                        toFront();
                    }
                    
                    event.dropComplete(true);
                } catch (Exception e) {
                    event.dropComplete(false);
                    JOptionPane.showMessageDialog(getContentPane(), 
                        "Error handling dropped files: " + e.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        // Create and set the drop target for both mainPanel and crtPanel
        new DropTarget(mainPanel, DnDConstants.ACTION_COPY, dropListener, true);
        new DropTarget(crtPanel, DnDConstants.ACTION_COPY, dropListener, true);
    }

    private void processDirectory(File directory, List<File> fileList) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    processDirectory(file, fileList);
                } else if (isValidFileType(file)) {
                    fileList.add(file);
                }
            }
        }
    }

    private boolean isValidFileType(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".snp") || name.endsWith(".asm");
    }

    /** Method to enable javascript on cheerpj to poke a file load through to the simulator */
    public void openFile(String fileName) {
        loadSnapshotAssembly.handleFileLoad(new File(fileName), false);
    }

    // Main method to create main window
    public static void main(String args[]) {
        // On MacOS put menu at top of the screen like native MacOS software
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("apple.awt.application.name", "Baby");

        // scale whole UI (including text, menus, controls... everything) to handle hiDPI screens
        // does not appear to work on MacOS but tested on Windows 10
        // on OpenJDK Runtime Environment Temurin-21.0.5+11 (build 21.0.5+11-LTS)
        // if scales to taller than the screen then the width will keep increasing but height doesn't giving
        // a squashed effect so may be worth calculating scale from screen height
        // Scale factor of 1.5 works well on a 2560x1440 display.
        //   System.setProperty("sun.java2d.uiScale","1.5");
        // or alternatively can specify on the command line if not overridden in code here, e.g.
        //   java -Dsun.java2d.uiScale=1.5 -jar target/baby-3.0-SNAPSHOT-jar-with-dependencies.jar
        // If done in code may want to not execute that command so that command line params can override it.

        if (args.length > 0) {
            // parse args

            // TODO:
            //  -load <file> - load program & show GUI, handle snp/asm formats
            //  -asm <file> - cmd line only, assemble and output assembled store SNP format to stdout.
            //  -dis <file> - cmd line only, take SNP and output disassembled store to stdout.
            //  -exec <file> - load program, execute without GUI on command line only and output result to stdout on STP instruction (if ever halts).
            //  -autorun - start animation of whatever program is cmd-line loaded / there by default once GUI has started
        }

        Baby baby = new Baby();
      
        baby.setSize(700, 950);
    

        
        baby.setVisible(true);
        baby.setResizable(false);

        if(CheerpJUtils.onCheerpj()) {
            CheerpJUtils.setupJavascriptInteface(baby);
        }

        // test only
        // Debugger debugger = new Debugger();
    }

}