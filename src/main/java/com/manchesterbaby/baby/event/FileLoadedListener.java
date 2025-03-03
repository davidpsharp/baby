package com.manchesterbaby.baby.event;

/**
 * Interface for listening to file load events in the Baby simulator
 */
public interface FileLoadedListener {
    /**
     * Called when a file has been loaded into the store
     */
    void onFileLoaded();
}
