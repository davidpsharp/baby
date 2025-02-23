package com.manchesterbaby.baby.manager;

import javax.swing.Timer;

import com.manchesterbaby.baby.controller.CrtPanelController;
import com.manchesterbaby.baby.controller.StaticisorPanelController;
import com.manchesterbaby.baby.core.Animator;
import com.manchesterbaby.baby.core.Control;
import com.manchesterbaby.baby.core.SimulationSpeedTracker;

import java.awt.event.ActionEvent;


public class AnimationManager {

    private final Control control;
    private final CrtPanelController crtPanelController;
    private final StaticisorPanelController staticisorPanelController;

    // timer for counting number of instructions executed each second to give speed
    private final Timer fpsTimer;

    // thread control for animation
    private Animator animator;

    private final SimulationSpeedTracker simulationSpeedTracker;

    public static volatile boolean animationRunning = false;

    public AnimationManager(
            Control control,
            CrtPanelController crtPanelController,
            StaticisorPanelController staticisorPanelController,
            SimulationSpeedTracker simulationSpeedTracker
    ) {
        this.control = control;
        this.crtPanelController = crtPanelController;
        this.staticisorPanelController = staticisorPanelController;
        this.simulationSpeedTracker = simulationSpeedTracker;

        // create timer to calculate speed to tick once a second
        fpsTimer = new Timer(1000, this::handleSimulationSpeedTracker);
        fpsTimer.setInitialDelay(0);
    }

    // start running animation
    public synchronized void startAnimation() {

        if (!animationRunning) {
            // create new thread for animation (create a new one as recall a thread can only be started once)
            animator = new Animator(control, crtPanelController, staticisorPanelController);
            animator.startAnimating();
        }

        // start fps timer *after* animation has started (timer self-terminates if animation is not running)
        fpsTimer.start();

    }

    // halt animation
    // only called if stop/run switch pressed or Stop button pressed on debug panel
    public synchronized void stopAnimation() {
        if (animationRunning) {
            animator.stopAnimating();
        }

        staticisorPanelController.updateActionLineListeners();

        // repaint so that control with the PI can be drawn if necessary
        crtPanelController.redrawCrtPanel();
    }


    /**
     * Timer event handler to measure execution speed and adjust simulation timing.
     */
    private void handleSimulationSpeedTracker(ActionEvent e) {

        // Update simulation speed tracking
        simulationSpeedTracker.updateSpeed();

        if (animationRunning) {
            // Adjust number of instructions per refresh to get 700 fps or as close as possible
            simulationSpeedTracker.regulateSpeed();
        } else {
            // If the Baby has stopped animating then no need to keep timing, make this the last FPS update.
            fpsTimer.stop();
        }
    }
}