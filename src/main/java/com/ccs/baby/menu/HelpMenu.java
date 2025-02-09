package com.ccs.baby.menu;

import com.ccs.baby.manual.ReferenceManual;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URI;

import com.ccs.baby.utils.MiscUtils;
import com.ccs.baby.utils.Version;

public class HelpMenu {

    private static final String ABOUT_TITLE = "Manchester Baby Simulator";
    private static final String ABOUT_MESSAGE = String.join("<br>",
            "Manchester Baby Simulator",
            "",
            "v" + Version.getVersion(),
            (MiscUtils.getBuildTime() == null) ? "" : "Built " + MiscUtils.getBuildTime(),
            "",
            "Originally by <a href='https://davidsharp.com'>David Sharp</a>",
            "January 2001",
            "With thanks to Chris Burton for his consultation on historical matters.",
            "The GUI was created from pictures of the Baby replica by Gulzaman Khan",
            "August 2006",
            "",
            "<a href='https://manchesterbaby.com'>manchesterbaby.com</a>"
    );

    private static ImageIcon icon = new ImageIcon(HelpMenu.class.getResource("/icons/baby.png"));

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
        JMenuItem refManual = new JMenuItem("Programmer's Reference Manual");

        // Add action listeners
        about.addActionListener(e -> showAboutDialog(frame));
        refManual.addActionListener(new ReferenceManual(frame));

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
        JEditorPane editorPane = new JEditorPane();
        editorPane.setContentType("text/html");
        editorPane.setText("<html><body style='width: 300px; padding: 5px; font-family: Arial, sans-serif;'>" + ABOUT_MESSAGE + "</body></html>");
        editorPane.setEditable(false);
        editorPane.setBackground(UIManager.getColor("Panel.background"));
        
        // Make links clickable
        editorPane.addHyperlinkListener(e -> {
            if (e.getEventType() == javax.swing.event.HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    Desktop.getDesktop().browse(new URI(e.getURL().toString()));
                } catch (IOException | URISyntaxException ex) {
                    JOptionPane.showMessageDialog(frame,
                            "Could not open link: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JOptionPane.showMessageDialog(
                frame.getContentPane(),
                editorPane,
                ABOUT_TITLE,
                JOptionPane.INFORMATION_MESSAGE,
                icon
        );
    }
}