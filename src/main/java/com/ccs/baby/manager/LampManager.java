package com.ccs.baby.manager;

import com.ccs.baby.core.Baby;

public class LampManager {

    /**
     * Update the stop lamp according to the internally held stop flag.
     * @param stopFlag the stop flag
     */
    public void updateStopLamp(boolean stopFlag) {
        Baby.mainPanel.setTexture(stopFlag);
    }
}
