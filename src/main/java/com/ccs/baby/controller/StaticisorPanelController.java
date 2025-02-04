package com.ccs.baby.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.ccs.baby.controller.listener.CrtPanelActionLineListener;
import com.ccs.baby.ui.StaticisorPanel;

public class StaticisorPanelController {

    private final StaticisorPanel staticisorPanel;
    private final List<CrtPanelActionLineListener> listeners = new ArrayList<>();

    public StaticisorPanelController(StaticisorPanel staticisorPanel) {
        this.staticisorPanel = staticisorPanel;

        // Set the default state of the panel
        setManAuto(true); // Default to auto
        toggleAllLineSwitches(true); // Set all L-stat switches to on
        toggleAllFunctionSwitches(true); // Set all F-stat switches to on

        // Set specific switches to the default "down" state based on the replica configuration
        staticisorPanel.getLineSwitch(5).setSelected(true);
        staticisorPanel.getLineSwitch(6).setSelected(true);
        staticisorPanel.getLineSwitch(12).setSelected(true);
        staticisorPanel.getFunctionSwitch(3).setSelected(true);

        // Bind events from StaticisorPanel to this controller
        staticisorPanel.setOnManAutoChange(this::notifyActionLineListeners);
        staticisorPanel.setOnLineSwitchChange(this::notifyActionLineListeners);
    }


    // --- State Modification Methods --- //

    /**
     * Sets the manual/auto mode.
     *
     * @param value true for automatic mode, false for manual mode.
     */
    public void setManAuto(boolean value) {
        staticisorPanel.setManAuto(value);
    }

    /**
     * Checks whether the panel is in automatic mode.
     *
     * @return true if in automatic mode, false otherwise.
     */
    public boolean isManAuto() {
        return staticisorPanel.getManAuto();
    }

    /**
     * Turns all line switches on or off.
     *
     * @param value true to enable all, false to disable all.
     */
    public void toggleAllLineSwitches(boolean value) {
        IntStream.range(0, 5).forEach(i -> staticisorPanel.setLineSwitch(i, value));
    }

    /**
     * Turns all function switches on or off.
     *
     * @param value true to enable all, false to disable all.
     */
    public void toggleAllFunctionSwitches(boolean value) {
        IntStream.range(0, 3).forEach(i -> staticisorPanel.setFunctionSwitch(i, value));
    }


    // --- Event Listener and Dispatch Methods --- //

    /**
     * Registers a new listener for action line changes.
     *
     * @param listener The listener to be notified when an action line change occurs.
     */
    public void addActionLineListener(CrtPanelActionLineListener listener) {
        listeners.add(listener);
    }

    /**
     * Notifies all registered listeners that the action line state has changed.
     */
    private void notifyActionLineListeners() {
        boolean isAutoMode = staticisorPanel.getManAuto();
        int lineValue = getSelectedLineSwitchesValue();

        for (CrtPanelActionLineListener listener : listeners) {
            listener.onActionLineChange(isAutoMode, lineValue);
        }
    }

    /**
     * Updates all registered listeners with the current state of the action line switches.
     */
    public void updateActionLineListeners() {
        notifyActionLineListeners();
    }


    // --- State Retrieval Methods --- //

    /**
     * Computes the bitmask representation of the selected line switches.
     * <p>
     * Each selected switch contributes a power-of-two value based on its position.
     * The result is a sum of these values, forming a binary representation of the selected switches.
     * </p>
     *
     * @return An integer where each bit represents a selected switch (e.g., if switches 0 and 2 are selected, returns 0b101 = 5).
     */
    public int getSelectedLineSwitchesValue() {
        return IntStream.range(0, 5)
                .filter(i -> staticisorPanel.getLineSwitch(i).isSelected())
                .map(i -> 1 << i) // Convert to binary representation
                .sum();
    }

    /**
     * Computes the bitmask representation of the selected function switches.
     * <p>
     * Each selected function switch contributes a power-of-two value based on its position.
     * The result is a sum of these values, forming a binary representation of the selected function switches.
     * </p>
     *
     * @return An integer where each bit represents a selected function switch (e.g., if function switches 0 and 1 are selected, returns 0b11 = 3).
     */
    public int getSelectedFunctionSwitchesValue() {
        return IntStream.range(0, 3)
                .filter(i -> staticisorPanel.getFunctionSwitch(i).isSelected())
                .map(i -> 1 << i) // Convert to binary representation
                .sum();
    }

}