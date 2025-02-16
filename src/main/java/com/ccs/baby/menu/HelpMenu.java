package com.ccs.baby.menu;

import com.ccs.baby.manual.ReferenceManual;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class HelpMenu {
    public static JMenu createHelpMenu(JFrame frame) {
        // Create the Help menu
        JMenu helpMenu = new JMenu("Help");

        // Create menu items
        JMenuItem about = new JMenuItem("About");
        JMenuItem refManual = new JMenuItem("Programmer's Reference Manual");

        // Add action listeners
        about.addActionListener(e -> showAboutDialog(frame));
        refManual.addActionListener(e -> new ReferenceManual(frame).setVisible(true));

        // Set mnemonics (keyboard shortcuts) for macOS, Windows, and Linux
        helpMenu.setMnemonic(KeyEvent.VK_H); // Alt + H
        about.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_DOWN_MASK | KeyEvent.ALT_DOWN_MASK )); // Ctrl + Alt + A
        refManual.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK | KeyEvent.ALT_DOWN_MASK)); // Ctrl + Alt + R

        // Add items to the menu
        helpMenu.add(refManual);
        helpMenu.add(about);

        return helpMenu;
    }

    private static void showAboutDialog(JFrame frame) {
        AboutDialog dialog = new AboutDialog(frame);
        dialog.displayAboutDialog();
    }
}