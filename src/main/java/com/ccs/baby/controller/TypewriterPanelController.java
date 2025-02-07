package com.ccs.baby.controller;

import com.ccs.baby.manager.AnimationManager;
import com.ccs.baby.core.Control;
import com.ccs.baby.core.Store;
import com.ccs.baby.controller.CrtPanelController;
import com.ccs.baby.controller.CrtControlPanelController;
import com.ccs.baby.ui.TypewriterPanel;

public class TypewriterPanelController {
    private final Store store;
    private final Control control;
    private final CrtPanelController crtPanelController;
    final StaticisorPanelController staticisorPanelController;
    private final CrtControlPanelController crtControlPanelController;  
    private final TypewriterPanel typewriterPanel;

    public TypewriterPanelController(
            Store store,
            Control control,
            TypewriterPanel typewriterPanel,
            CrtPanelController crtPanelController,
            StaticisorPanelController staticisorPanelController,
            CrtControlPanelController crtControlPanelController
    ) {
        this.store = store;
        this.control = control;
        this.crtPanelController = crtPanelController;
        this.staticisorPanelController = staticisorPanelController;
        this.crtControlPanelController = crtControlPanelController;
        this.typewriterPanel = typewriterPanel;

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
            int lineNumber = staticisorPanelController.getSelectedLineSwitchesValue();
            // true = write, false = erase
            if (crtControlPanelController.isWriteErase()) {
                store.setLine(lineNumber, store.getLine(lineNumber) | (1 << keyNumber));
            } else {
                store.setLine(lineNumber, store.getLine(lineNumber) & ~(1 << keyNumber));
            }
            crtPanelController.redrawCrtPanel();
        }
    }

    private void handleKeyReleased(int keyNumber) {
        // notify that the key is no longer pressed whether Baby is still
        // running or not.
        control.setKeyPressed(false, keyNumber);
    }

    public void pressKey(int keyNumber) {
        typewriterPanel.pressKey(keyNumber);
    }

}