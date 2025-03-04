package com.manchesterbaby.baby.menu;

import javax.swing.*;

import com.manchesterbaby.baby.core.Baby;
import com.manchesterbaby.baby.manual.ReferenceManual;
import com.manchesterbaby.baby.utils.CheerpJUtils;
import com.manchesterbaby.baby.utils.MiscUtils;
import com.manchesterbaby.baby.utils.Version;

import java.awt.event.KeyEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.awt.Desktop;

public class HelpMenu {
    public static JMenu createHelpMenu(JFrame frame) {
        // Create the Help menu
        JMenu helpMenu = new JMenu("Help");

        // Create menu items
        JMenuItem about = new JMenuItem("About");

        // Add action listeners
        about.addActionListener(e -> showAboutDialog(frame));

        // TODO: probably move reference manual to a web browser launch and include a quick start guide embedded instead
        // anyone who wants to program the baby will need to invest some time and do some reading which doesn't have to be embedded in the simulator

        JMenuItem quickStartGuide = new JMenuItem("Quick Start Guide");
        quickStartGuide.addActionListener(e -> MiscUtils.launchUrlInBrowser("https://github.com/davidpsharp/baby/blob/main/docs/quick-start-guide.md") );
        helpMenu.add(quickStartGuide);

        JMenuItem introToProgramming = new JMenuItem("Intro to Programming Baby");
        introToProgramming.addActionListener(e -> MiscUtils.launchUrlInBrowser("https://github.com/davidpsharp/baby/blob/main/docs/intro-to-programming-the-baby.md") );
        helpMenu.add(introToProgramming);

        if(CheerpJUtils.onCheerpj()) {

            JCheckBoxMenuItem viewReferenceManualTab = new JCheckBoxMenuItem("Programmer's Reference Manual");
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

        JMenuItem sourceCode = new JMenuItem("Source Code");
        sourceCode.addActionListener(e -> MiscUtils.launchUrlInBrowser("https://github.com/davidpsharp/baby/") );
        helpMenu.add(sourceCode);

        // Set mnemonics (keyboard shortcuts) for macOS, Windows, and Linux
        helpMenu.setMnemonic(KeyEvent.VK_H); // Alt + H
        quickStartGuide.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK )); // Ctrl + Shift + H
        about.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_DOWN_MASK | KeyEvent.ALT_DOWN_MASK )); // Ctrl + Alt + A


        helpMenu.addSeparator();
        helpMenu.add(about);

        return helpMenu;
    }

    private static void showAboutDialog(JFrame frame) {
        AboutDialog dialog = new AboutDialog(frame);
        dialog.displayAboutDialog();
    }
}