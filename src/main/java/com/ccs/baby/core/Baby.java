package com.ccs.baby.core;

// Manchester Baby Simulator
// by David Sharp
// January 2001
// requires Java v8 or later

import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import java.awt.Image;


import com.ccs.baby.disassembler.*;
import com.ccs.baby.io.LoadExample;
import com.ccs.baby.manager.*;
import com.ccs.baby.menu.*;
import com.ccs.baby.ui.*;
import com.ccs.baby.utils.AppSettings;
import com.ccs.baby.utils.PreferencesService;
import com.ccs.baby.utils.Version;
import com.ccs.baby.controller.*;
import com.ccs.baby.debug.*;
import com.ccs.baby.utils.MiscUtils;

public class Baby extends JFrame {

    // Get the current directory
    private static String currentDir;
    public static BackgroundPanel mainPanel;

    public Baby() {

        try {
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

            CrtPanel crtPanel = new CrtPanel(store, control);
            crtPanel.setOpaque(false);
            crtPanel.setPreferredSize(new Dimension(400, 386));

            // Create Disassembler
            Disassembler disassembler = new Disassembler(store, control, crtPanel);

            // Create switch panel components
            StaticisorPanel staticisorPanel = new StaticisorPanel();
            TypewriterPanel typewriterPanel = new TypewriterPanel();
            CrtControlPanel crtControlPanel = new CrtControlPanel();

            // Tell control about staticisorPanel and crtControlPanel
            control.setSwitchPanel(staticisorPanel, crtControlPanel);

            // A container for all switch panel components
            JPanel switchPanel = new JPanel();
            switchPanel.setLayout(new BoxLayout(switchPanel, BoxLayout.Y_AXIS));
            switchPanel.setOpaque(false);
            switchPanel.add(typewriterPanel);
            switchPanel.add(staticisorPanel);
            switchPanel.add(crtControlPanel);

            // Create Action Line Manager
            ActionLineManager actionLineManager = new ActionLineManager(staticisorPanel, crtPanel, control);

            // Setup a DebugPanel (aka modernControls)
            DebugPanel debugPanel = new DebugPanel(control, staticisorPanel, crtControlPanel);
            debugPanel.setOpaque(true);

            // Get the FpsLabelService from the debugPanel
            FpsLabelService fpsLabelService = debugPanel.getFpsLabelService();

            // Initialise AnimationManager
            AnimationManager animationManager = new AnimationManager(control, crtPanel, staticisorPanel, fpsLabelService, actionLineManager);

            new TypewriterPanelController(typewriterPanel, store, control, crtPanel, staticisorPanel, crtControlPanel);
            new StaticisorPanelController(staticisorPanel, actionLineManager);
            new CrtControlPanelController(actionLineManager, animationManager, crtControlPanel, store, control, crtPanel, staticisorPanel, disassembler);

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
            new MenuSetup(menuBar, store, control, crtPanel, crtControlPanel, disassembler, currentDir, this, debugPanel);
            

            // Setup keypress F10 to single step the simulator (similar to Visual Studio keypress-style)
            // What follows is a perfect example of how java swing can make something simple horribly complex and verbose....
            // by default Swing sets F10 to open the menu so have to override this and point to none first to disable it otherwise menu pops up.
            menuBar.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0), "none");
            // Then set up F10 to do something useful...
            KeyStroke ks_f10 = KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0);
            Action performStep = new AbstractAction("Step") {
                public void actionPerformed(ActionEvent e) {
                    control.singleStep();
                }
            };
            // TODO: have to register this for a JComponent in every window otherwise won't work for example if the disassembler window has the focus.
            mainPanel.getActionMap().put("performStep", performStep);
            mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ks_f10, "performStep");

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
            crtPanel.setToolTipText("The monitor.");
            crtPanel.render();
            crtPanel.repaint();

            // Open window
            setVisible(true);

            // This helps toggle the visibility of the debugPanel
            contentPane.revalidate();
            contentPane.repaint();


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
            //  -inbrowser - parameter passed to indicate that the application is running in cheerpj or similar web browser-based-javascript/webasm-JVM.
        }

        Baby baby = new Baby();
      
        baby.setSize(700, 950);
    

        baby.setVisible(true);
        baby.setResizable(false);

        // test only
        // Debugger debugger = new Debugger();
    }

}