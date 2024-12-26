package com.ccs.baby.ui;

import com.ccs.baby.core.Baby;

// update the stop lamp according to the internally held stop flag
public class LampManager {

    public void updateStopLamp(boolean stopFlag) {
        if (stopFlag) {
            Baby.mainPanel.setTexture(true);
        } else {
            Baby.mainPanel.setTexture(false);
        }
    }
}
