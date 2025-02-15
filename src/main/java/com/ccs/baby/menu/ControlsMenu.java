package com.ccs.baby.menu;

import com.ccs.baby.core.Control;

import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.JComponent;



public class ControlsMenu {

    
    /**
     * Creates the Controls menu.
     *
     * @param control the control object
     * @param menuBar the menu bar
     * @return the Help menu
     */
    public static JMenu createControlsMenu(Control control, JMenuBar menuBar) {

        // Create the Help menu
        JMenu controlsMenu = new JMenu("Controls");  // is 'Run' or 'Start' better for first time users

        // Create menu items
        JMenuItem stepItem = new JMenuItem("Single Step");
        JMenuItem runItem = new JMenuItem("Run");
        JMenuItem stopItem = new JMenuItem("Stop");

        // Add action listeners
        stepItem.addActionListener(e -> control.singleStep());
        runItem.addActionListener(e -> control.startRunning());
        stopItem.addActionListener(e -> control.stopRunning());

        // by default Swing sets F10 to open the menu so have to override this and point to none first to disable it otherwise menu pops up and
        // the Controls menu accelerators we want for F10 won't fire unless we disable this...
        menuBar.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0), "none");

        // set up accelerators
        controlsMenu.setMnemonic(KeyEvent.VK_C); // Alt + C
        stepItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0)); // F10
        runItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0)); // F5
        stopItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, KeyEvent.SHIFT_DOWN_MASK)); // Shift + F5

        // Add items to the menu
        controlsMenu.add(stepItem);
        controlsMenu.add(runItem);
        controlsMenu.add(stopItem);

        return controlsMenu;
    }
}