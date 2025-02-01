package com.ccs.baby.menu;

import com.ccs.baby.core.Control;
import com.ccs.baby.manual.ReferenceManual;

import javax.swing.KeyStroke;

import java.awt.event.KeyEvent;

import javax.swing.*;



public class ControlsMenu {

    
    /**
     * Creates the Controls menu.
     *
     * @param frame the frame object
     * @return the Help menu
     */
    public static JMenu createControlsMenu(JFrame frame, Control control) {

        // Create the Help menu
        JMenu controlsMenu = new JMenu("Controls");  // is 'Run' or 'Start' better for first time users

        // TODO: add more controls
        // KLC clear current action line
        // KSC clear entire store
        // KAC clear accumulator


        // Create menu items
        JMenuItem stepItem = new JMenuItem("Single Step");
        JMenuItem runItem = new JMenuItem("Run");
        JMenuItem stopItem = new JMenuItem("Stop");

        // Add action listeners
        stepItem.addActionListener(e -> control.singleStep());
        runItem.addActionListener(e -> control.startRunning());
        stopItem.addActionListener(e -> control.stopRunning());

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