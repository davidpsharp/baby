package com.manchesterbaby.baby.controller.listener;

import com.manchesterbaby.baby.controller.CrtControlPanelController;
import com.manchesterbaby.baby.ui.CrtPanel;

/**
 * Listens for display type changes and updates UI elements accordingly.
 */
public class CrtPanelDisplayTypeListener {

    private final CrtControlPanelController crtControlPanelController;

    public CrtPanelDisplayTypeListener(CrtControlPanelController crtControlPanelController) {
        this.crtControlPanelController = crtControlPanelController;
    }

    public void onDisplayChange(CrtPanel.DisplayType displayType) {
        switch (displayType) {
            case STORE:
                crtControlPanelController.setDisplayStoreButton(true);
                break;
            case ACCUMULATOR:
                crtControlPanelController.setDisplayAccumulatorButton(true);
                break;
            case CONTROL:
                crtControlPanelController.setDisplayControlButton(true);
                break;
        }
    }
}