package com.manchesterbaby.baby.event;

/**
 * Interface for listening to CRT panel redraw events in the Baby simulator
 */
public interface CrtPanelRedrawListener {
    /**
     * Called when the CRT panel has been redrawn
     */
    void onCrtPanelRedrawn();
}
