package com.ccs.baby.menu;

import com.ccs.baby.manual.ReferenceManual;

import javax.swing.*;

import java.awt.event.KeyEvent;


public class ControlsMenu {

    
    /**
     * Creates the Controls menu.
     *
     * @param frame the frame object
     * @return the Help menu
     */
    public static JMenu createControlsMenu(JFrame frame) {

        // Create the Help menu
        JMenu controlsMenu = new JMenu("Controls");  // is 'Run' or 'Start' better for first time users

        // TODO: add more controls
        // KLC clear current action line
        // KSC clear entire store
        // KAC clear accumulator


        // Create menu items
        JMenuItem stepItem = new JMenuItem("Step");
        JMenuItem runItem = new JMenuItem("Run");
        JMenuItem stopItem = new JMenuItem("Stop");

        // Add action listeners
        stepItem.addActionListener(new ReferenceManual());
        runItem.addActionListener(new ReferenceManual());
        stopItem.addActionListener(new ReferenceManual());

        // TODO: Set mnemonics (keyboard shortcuts) for macOS, Windows, and Linux
        //stepItem.setMnemonic(KeyEvent.VK_H); // Alt + H
        //runItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_DOWN_MASK | KeyEvent.ALT_DOWN_MASK )); // Ctrl + Alt + A
        //stopItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK | KeyEvent.ALT_DOWN_MASK)); // Ctrl + Alt + R

        // Add items to the menu
        controlsMenu.add(stepItem);
        controlsMenu.add(runItem);
        controlsMenu.add(stopItem);

        return controlsMenu;
    }
}