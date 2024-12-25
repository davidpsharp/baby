package com.ccs.baby.menu;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// display the about window
public class AboutMenu implements ActionListener {

    private final JFrame frame;

    public AboutMenu(JFrame frame) {
        this.frame = frame;
    }

    public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(frame.getContentPane(), "Manchester Baby Simulator\n" +
                "by David Sharp\nJanuary 2001\nWith thanks to Chris Burton for his consultation\n" +
                "on historical matters.\nGUI created from pictures of the Baby remake\nby Gulzaman Khan\nAugust 2006 ", "Baby", JOptionPane.INFORMATION_MESSAGE);
    }
}