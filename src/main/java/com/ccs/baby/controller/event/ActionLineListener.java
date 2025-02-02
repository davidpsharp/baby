package com.ccs.baby.controller.event;

/**
 * Listener interface for handling action line changes.
 */
@FunctionalInterface
public interface ActionLineListener {

    /**
     * Called when the action line state changes.
     *
     * @param isAutoMode true if in automatic mode, false otherwise.
     * @param lineValue the current line value.
     */
    void onActionLineChange(boolean isAutoMode, int lineValue);
}