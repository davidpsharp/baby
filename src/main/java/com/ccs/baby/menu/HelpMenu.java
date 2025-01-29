package com.ccs.baby.menu;

import com.ccs.baby.manual.ReferenceManual;

import javax.swing.*;

import java.awt.event.KeyEvent;

import com.ccs.baby.utils.Version;


public class HelpMenu {

    private static final String ABOUT_TITLE = "Manchester Baby";
    private static final String ABOUT_MESSAGE = String.join("\n",
            "Manchester Baby Simulator",
            "v" + Version.getVersion(),
            "Originally by David Sharp",
            "January 2001",
            "With thanks to Chris Burton for his consultation on historical matters.",
            "The GUI was created from pictures of the Baby replica by Gulzaman Khan",
            "August 2006",
            "https://davidsharp.com/baby"
    );

    /**
     * Creates the Help menu.
     *
     * @param frame the frame object
     * @return the Help menu
     */
    public static JMenu createHelpMenu(JFrame frame) {

        // Create the Help menu
        JMenu helpMenu = new JMenu("Help");

        // Create menu items
        JMenuItem about = new JMenuItem("About");
        JMenuItem refManual = new JMenuItem("Reference Manual");

        // Add action listeners
        about.addActionListener(e -> JOptionPane.showMessageDialog(
                frame.getContentPane(),
                ABOUT_MESSAGE,
                ABOUT_TITLE,
                JOptionPane.INFORMATION_MESSAGE
        ));
        refManual.addActionListener(new ReferenceManual());

        // Set mnemonics (keyboard shortcuts) for macOS, Windows, and Linux
        helpMenu.setMnemonic(KeyEvent.VK_H); // Alt + H
        about.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_DOWN_MASK | KeyEvent.ALT_DOWN_MASK )); // Ctrl + Alt + A
        refManual.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK | KeyEvent.ALT_DOWN_MASK)); // Ctrl + Alt + R

        // Add items to the menu
        helpMenu.add(refManual);
        helpMenu.add(about);

        return helpMenu;
    }
}