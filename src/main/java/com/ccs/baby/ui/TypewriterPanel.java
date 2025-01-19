package com.ccs.baby.ui;

import javax.swing.*;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Dimension;

import java.awt.event.MouseListener;

import com.ccs.baby.core.Baby;
import com.ccs.baby.ui.components.PushButton;
import com.ccs.baby.core.Store;
import com.ccs.baby.core.Control;

public class TypewriterPanel extends JPanel {

    public TypewriterPanel(Store aStore, Control aControl, SwitchPanel aSwitchPanel) {

        setOpaque(false);
        //setBackground(backgroundColor);
        setLayout(new GridBagLayout());   // 8 across. 5 down


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.CENTER;
        // gbc.weightx = 2;

        PushButton[] numberKeys = new PushButton[40];

        // create 40 keys
        for (int keyNumber = 0; keyNumber < 40; keyNumber++) {
            numberKeys[keyNumber] = new PushButton("", AbstractButton.CENTER);

            // add mouse listeners to keys 0-31
            if (keyNumber < 32) {
                numberKeys[keyNumber].addMouseListener(createTypewriterListener(keyNumber, aStore, aControl, aSwitchPanel));
            }

        }

        // add keys in correct order for display with this layout
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 40; j += 5) {
                gbc.gridx = j / 5;
                gbc.gridy = i;
                add(numberKeys[i + j], gbc);
            }
            gbc.anchor = gbc.LINE_END;
        }

        for (int keyNumber = 0; keyNumber < 32; keyNumber++) {
            numberKeys[keyNumber].setToolTipText("Typewriter adjusts bit " + keyNumber + " of the action line.");
        }
        for (int keyNumber = 32; keyNumber < 40; keyNumber++) {
            numberKeys[keyNumber].setToolTipText("Left unconnected.");
        }

        //setBorder( BorderFactory.createLoweredBevelBorder() );
        //setBorder( BorderFactory.createLoweredBevelBorder() );
        setPreferredSize(new Dimension(0, 270));

    }


    // typewriter button pushed
    private MouseListener createTypewriterListener(int aKeyNumber, Store aStore, Control aControl, SwitchPanel aSwitchPanel) {
        return new MouseListener() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                // if running then adjust every action line
                if (Baby.running) {
                    // signal control that a key is pressed
                    // arguments: key pressed, number of key pressed, is it a write?
                    aControl.setKeyPressed(true, aKeyNumber);

                    // note, m1sim incorrectly stops running in this case rather than corrupting store lines
                }
                // otherwise just do current action line
                else {
                    int lineNumber = aSwitchPanel.getLineValue();
                    // true = write, false = erase
                    if (aSwitchPanel.getEraseWrite()) {
                        aStore.setLine(lineNumber, aStore.getLine(lineNumber) | (1 << aKeyNumber));
                    } else {
                        aStore.setLine(lineNumber, aStore.getLine(lineNumber) & (~(1 << aKeyNumber)));
                    }
                    aSwitchPanel.redrawCrtPanel();
                }
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                // notify that the key is no longer pressed whether Baby is still
                // running or not.
                aControl.setKeyPressed(false, aKeyNumber);
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
            }
        };
    }
}