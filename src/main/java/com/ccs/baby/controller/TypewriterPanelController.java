package com.ccs.baby.controller;

import com.ccs.baby.manager.AnimationManager;
import com.ccs.baby.core.Control;
import com.ccs.baby.core.Store;
import com.ccs.baby.ui.CrtPanel;
import com.ccs.baby.ui.StaticisorPanel;
import com.ccs.baby.ui.CrtControlPanel;
import com.ccs.baby.ui.TypewriterPanel;

public class TypewriterPanelController {
    private final Store store;
    private final Control control;
    private final CrtPanel crtPanel;
    private final StaticisorPanel staticisorPanel;
    private final CrtControlPanel crtControlPanel;

    public TypewriterPanelController(TypewriterPanel typewriterPanel, Store store, Control control, CrtPanel crtPanel, StaticisorPanel staticisorPanel, CrtControlPanel crtControlPanel) {
        this.store = store;
        this.control = control;
        this.crtPanel = crtPanel;
        this.staticisorPanel = staticisorPanel;
        this.crtControlPanel = crtControlPanel;

        for (int i = 0; i < typewriterPanel.CONNECTED_KEYS; i++) {
            int keyNumber = i; // copy to final variable for lambda

            // Set key pressed callback
            typewriterPanel.setOnKeyPressed(i, () -> handleKeyPressed(keyNumber));

            // Set key released callback
            typewriterPanel.setOnKeyReleased(i, () -> handleKeyReleased(keyNumber));
        }
    }

    private void handleKeyPressed(int keyNumber) {
        // if running then adjust every action line
        if (AnimationManager.animationRunning) {
            // signal control that a key is pressed
            // arguments: key pressed, number of key pressed, is it a write?
            control.setKeyPressed(true, keyNumber);

            // note, m1sim incorrectly stops running in this case rather than corrupting store lines
        } else { // otherwise just do current action line
            int lineNumber = staticisorPanel.getLineValue();
            // true = write, false = erase
            if (crtControlPanel.getWriteErase()) {
                store.setLine(lineNumber, store.getLine(lineNumber) | (1 << keyNumber));
            } else {
                store.setLine(lineNumber, store.getLine(lineNumber) & ~(1 << keyNumber));
            }
            crtPanel.redrawCrtPanel();
        }
    }

    private void handleKeyReleased(int keyNumber) {
        // notify that the key is no longer pressed whether Baby is still
        // running or not.
        control.setKeyPressed(false, keyNumber);
    }
}