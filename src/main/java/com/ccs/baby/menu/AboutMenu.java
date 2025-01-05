package com.ccs.baby.menu;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Displays the "About" window.
 */
public class AboutMenu implements ActionListener {

    private static final String TITLE = "Manchester Baby";
    private static final String MESSAGE = String.join("\n",
            "Manchester Baby Simulator",
            "by David Sharp",
            "January 2001",
            "With thanks to Chris Burton for his consultation on historical matters.",
            "The GUI was created from pictures of the Baby remake",
            "by Gulzaman Khan",
            "August 2006"
    );

    private final JFrame frame;

    public AboutMenu(JFrame frame) {
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(
                frame.getContentPane(),
                MESSAGE,
                TITLE,
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}