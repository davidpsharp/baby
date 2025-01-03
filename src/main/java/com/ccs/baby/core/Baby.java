package com.ccs.baby.core;

// Manchester Baby Simulator
// by David Sharp
// January 2001
// requires Java v1.2 or later

import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import com.ccs.baby.disassembler.Disassembler;
import com.ccs.baby.animation.AnimationManager;
import com.ccs.baby.menu.MenuSetup;
import com.ccs.baby.ui.SwitchPanel;
import com.ccs.baby.ui.CrtPanel;
import com.ccs.baby.ui.TexturedJPanel;
import com.ccs.baby.ui.LampManager;
import com.ccs.baby.ui.FpsLabelService;
import com.ccs.baby.ui.DebugPanel;

public class Baby extends JFrame {

    // Get the current directory
    private static String currentDir;

    private final AnimationManager animationManager;
    public static volatile boolean running = false;

    public static TexturedJPanel mainPanel;

    public Baby() {

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

        SwitchPanel switchPanel = new SwitchPanel(store, control, crtPanel, this, disassembler);
        switchPanel.setOpaque(false);
        control.setSwitchPanel(switchPanel);    // Tell control about switchPanel

        

        // Create a container mainPanel that wraps crtPanel and switchPanel
        mainPanel = new TexturedJPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setSize(690, 905);
        mainPanel.add(crtPanel, BorderLayout.NORTH);
        mainPanel.add(switchPanel);

        // Setup a DebugPanel (aka modernControls)
        DebugPanel debugPanel = new DebugPanel(control, switchPanel);
        debugPanel.setOpaque(true);

        // Set up display window GUI
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(mainPanel, BorderLayout.CENTER);
        contentPane.add(debugPanel, BorderLayout.SOUTH);

        // Get the FpsLabelService from the debugPanel
        FpsLabelService fpsLabelService = debugPanel.getFpsLabelService();

        // Initialise AnimationManager
        animationManager = new AnimationManager(control, crtPanel, switchPanel, true, fpsLabelService);

        // Set up and add menu bars to the window
        JMenuBar menuBar = new JMenuBar();
        new MenuSetup(menuBar, store, control, crtPanel, switchPanel, disassembler, currentDir, this, debugPanel);
        setJMenuBar(menuBar);

        // Setup keypress F10 to single step the simulator (similar to Visual Studio keypress-style)
        // What follows is a perfect example of how java swing can make something simple horribly complex and verbose....
        // by default Swing sets F10 to open the menu so have to override this and point to none first to disable it otherwise menu pops up.
        menuBar.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0), "none");
        // Then set up F10 to do something useful...
        KeyStroke ks_f10 = KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0);
        Action performStep = new AbstractAction("Step") {  
            public void actionPerformed(ActionEvent e) {
                 switchPanel.singleStep();
            }
        };
        // TODO: have to register this for a JComponent in every window otherwise won't work for example if the disassembler window has the focus.
        mainPanel.getActionMap().put("performStep", performStep);
        mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ks_f10, "performStep");



        // Reset the hardware to initial values
        store.reset();
        control.reset();

        // Load a program by default "diffeqt.asm"
        try {
            store.loadLocalModernAssembly("demos/diffeqt.asm");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(getContentPane(), "Default program not loaded. " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
                System.exit(0);
            }
        });
    }

    // Main method to create main window
    public static void main(String args[]) {

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
        
        if(args.length > 0)
        {
            // parse args
            // TODO:
            // -load - load program & show GUI, handle snp/asm formats
            // -assemble - cmd line only, assemble and output assembled store SNP format to stdout.
            // -disassemble - cmd line only, take SNP and output disassembled store to stdout.
            // -execute - load program, execute in memory on command line and output result to stdout.
        }

        Baby baby = new Baby();
        baby.setSize(700, 950);
        baby.setTitle("Baby");
        baby.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        baby.setVisible(true);
        baby.setResizable(false);
    }

    // Delegate animation control methods
    public synchronized void startAnimation() {
        animationManager.startAnimation();
        running = true;
    }

    public synchronized void stopAnimation() {
        animationManager.stopAnimation();
        running = false;
    }
}