package com.ccs.baby.ui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.ccs.baby.ui.components.PushButton;

import static com.ccs.baby.utils.CallbackUtils.runCallback;

public class TypewriterPanel extends JPanel {

    // 32 keys plus 8 unconnected
    public int CONNECTED_KEYS = 32;
    public int UNCONNECTED_KEYS = 8;
    public int TOTAL_KEYS = CONNECTED_KEYS + UNCONNECTED_KEYS;

    private final Runnable[] onKeyPressedCallbacks;
    private final Runnable[] onKeyReleasedCallbacks;

    private final PushButton[] numberKeys;

    // 8 (across) x 5 (down) grid
    public TypewriterPanel() {

        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(0, 250));
        setOpaque(false);

        numberKeys = new PushButton[TOTAL_KEYS];
        onKeyPressedCallbacks = new Runnable[TOTAL_KEYS];
        onKeyReleasedCallbacks = new Runnable[TOTAL_KEYS];

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.CENTER;

        // Initialize and layout keys
        initialiseNumberKeys(gridBagConstraints);
    }

    private void initialiseNumberKeys(GridBagConstraints gridBagConstraints) {
        for (int i = 0; i < TOTAL_KEYS; i++) {

            // add mouse listeners to keys 0-31
            if (i < CONNECTED_KEYS) {
                numberKeys[i] = new PushButton("Typewriter adjusts bit " + i + " of the action line.");

                int keyNumber = i; // Necessary for lambda
                numberKeys[i].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        runCallback(onKeyPressedCallbacks[keyNumber]);
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        runCallback(onKeyReleasedCallbacks[keyNumber]);
                    }
                });

            } else {
                numberKeys[i] = new PushButton("Left unconnected.");
            }
        }

        // add keys in correct order for display with this layout
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < TOTAL_KEYS; j += 5) {
                gridBagConstraints.gridx = j / 5;
                gridBagConstraints.gridy = i;
                add(numberKeys[i + j], gridBagConstraints);
            }
            gridBagConstraints.anchor = GridBagConstraints.LINE_END;
        }
    }

    public void setOnKeyPressed(int i, Runnable callback) {
        if (i >= 0 && i < onKeyPressedCallbacks.length) {
            onKeyPressedCallbacks[i] = callback;
        }
    }

    public void setOnKeyReleased(int i, Runnable callback) {
        if (i >= 0 && i < onKeyReleasedCallbacks.length) {
            onKeyReleasedCallbacks[i] = callback;
        }
    }

}