package com.ccs.baby.controller.listener;

import com.ccs.baby.controller.CrtControlPanelController;
import com.ccs.baby.ui.CrtPanel;

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