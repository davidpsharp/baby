package com.ccs.baby.ui;

import com.ccs.baby.core.Baby;

public class LampManager {

    /**
     * Update the stop lamp according to the internally held stop flag.
     * @param stopFlag the stop flag
     */
    public void updateStopLamp(boolean stopFlag) {
        if (stopFlag) {
            Baby.mainPanel.setTexture(true);
        } else {
            Baby.mainPanel.setTexture(false);
        }
    }
}
