package com.ccs.baby.controller;

import com.ccs.baby.core.Control;
import com.ccs.baby.core.SimulationSpeedTracker;
import com.ccs.baby.ui.DebugPanel;

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
        simulationSpeedTracker.setListener(() -> updateSpeedDisplay(simulationSpeedTracker.formatSpeedText()));

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
     * @param speedText The updated speed text.
     */
    private void updateSpeedDisplay(String speedText) {
        debugPanel.updateSimulationSpeedDisplay(speedText);
    }

    private void handleResetElapsedTimePressed() {
        simulationSpeedTracker.resetElapsedTime();
    }
}