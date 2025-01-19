package com.ccs.baby.menu;

import com.ccs.baby.manual.ReferenceManual;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.awt.event.KeyEvent;


public class HelpMenu {

    private static final String ABOUT_TITLE = "Manchester Baby";
    private static final String ABOUT_MESSAGE = String.join("\n",
            "Manchester Baby Simulator",
            "by David Sharp",
            "January 2001",
            "With thanks to Chris Burton for his consultation on historical matters.",
            "The GUI was created from pictures of the Baby remake",
            "by Gulzaman Khan",
            "August 2006"
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

        // Set mnemonics (keyboard shortcuts)
        helpMenu.setMnemonic(KeyEvent.VK_H); // Alt + H
        about.setMnemonic(KeyEvent.VK_A); // Alt + A
        refManual.setMnemonic(KeyEvent.VK_R); // Alt + R

        // Add items to the menu
        helpMenu.add(refManual);
        helpMenu.add(about);

        return helpMenu;
    }
}