package com.manchesterbaby.baby.menu;

import javax.swing.*;

import com.manchesterbaby.baby.core.Baby;
import com.manchesterbaby.baby.manual.ReferenceManual;
import com.manchesterbaby.baby.utils.CheerpJUtils;

import java.awt.event.KeyEvent;

public class HelpMenu {
    public static JMenu createHelpMenu(JFrame frame) {
        // Create the Help menu
        JMenu helpMenu = new JMenu("Help");

        // Create menu items
        JMenuItem about = new JMenuItem("About");

        // Add action listeners
        about.addActionListener(e -> showAboutDialog(frame));

        if(CheerpJUtils.onCheerpj()) {

            JCheckBoxMenuItem viewReferenceManualTab = new JCheckBoxMenuItem("View Reference Manual Tab");
            viewReferenceManualTab.setMnemonic(KeyEvent.VK_V); // Alt + V
            viewReferenceManualTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK | KeyEvent.ALT_DOWN_MASK)); // Ctrl + Alt + V

            viewReferenceManualTab.addActionListener(e -> {
                JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
                if (item.isSelected()) {
                    ((Baby) frame).addReferenceManualTab();
                } else {
                    ((Baby) frame).removeReferenceManualTab();
                }
            });

            helpMenu.add(viewReferenceManualTab);
        }
        else {

            JMenuItem refManual = new JMenuItem("Programmer's Reference Manual");

            refManual.addActionListener(e -> new ReferenceManual(frame).setVisible(true));

            refManual.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK | KeyEvent.ALT_DOWN_MASK)); // Ctrl + Alt + R

            helpMenu.add(refManual);
        }

        // Set mnemonics (keyboard shortcuts) for macOS, Windows, and Linux
        helpMenu.setMnemonic(KeyEvent.VK_H); // Alt + H
        about.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_DOWN_MASK | KeyEvent.ALT_DOWN_MASK )); // Ctrl + Alt + A

        // Add items to the menu
        helpMenu.add(about);

        return helpMenu;
    }

    private static void showAboutDialog(JFrame frame) {
        AboutDialog dialog = new AboutDialog(frame);
        dialog.displayAboutDialog();
    }
}