package com.manchesterbaby.baby.event;

/**
 * Interface for listening to stop flag changes in the Baby simulator
 */
public interface StopFlagListener {
    /**
     * Called when the stop flag changes state
     * @param newState the new state of the stop flag
     */
    void onStopFlagChanged(boolean newState);
}
