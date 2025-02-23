package com.manchesterbaby.baby.controller;

import com.manchesterbaby.baby.core.Control;
import com.manchesterbaby.baby.core.SimulationSpeedData;
import com.manchesterbaby.baby.core.SimulationSpeedTracker;
import com.manchesterbaby.baby.ui.DebugPanel;

/**
 * Handles interactions between DebugPanel and Control.
 */
public class DebugPanelController {

    private final Control control;
    private final DebugPanel debugPanel;
    private final SimulationSpeedTracker simulationSpeedTracker;

    public DebugPanelController(Control control, SimulationSpeedTracker simulationSpeedTracker, DebugPanel debugPanel) {
        this.control = control;
        this.debugPanel = debugPanel;
        this.simulationSpeedTracker = simulationSpeedTracker;

        // Set up button callbacks
        debugPanel.setOnStepPressed(this::handleStepPressed);
        debugPanel.setOnRunPressed(this::handleRunPressed);
        debugPanel.setOnStopPressed(this::handleStopPressed);

        // Register as the listener for simulation speed updates
        simulationSpeedTracker.setListener(this::updateSpeedDisplay);

        debugPanel.setOnResetElapsedTimePressed(this::handleResetElapsedTimePressed);
    }

    private void handleStepPressed() {
        control.singleStep();
    }

    private void handleRunPressed() {
        control.startRunning();
    }

    private void handleStopPressed() {
        control.stopRunning();
    }

    /**
     * Updates the speed display in DebugPanel.
     */
    private void updateSpeedDisplay() {
        SimulationSpeedData speedData = simulationSpeedTracker.getSimulationSpeedData();
        debugPanel.updateSimulationSpeedDisplay(speedData);
    }

    private void handleResetElapsedTimePressed() {
        simulationSpeedTracker.resetElapsedTime();
    }
}