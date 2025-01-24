package com.ccs.baby.core;

import java.util.concurrent.TimeUnit;

import com.ccs.baby.ui.CrtPanel;
import com.ccs.baby.ui.SwitchPanel;

/**
 * The Animator class is responsible for managing and executing an animation loop
 * for a simulation of the Baby computer. It is designed to run as a daemon thread,
 * meaning it will terminate automatically when the main application exits. It also
 * adjusts its thread priority for smoother execution.
 */
public class Animator extends Thread {

    private volatile boolean keepAnimating = false; // Flag to indicate whether to keep animating (or terminate thread)

    private final CrtPanel crtPanel;
    private final Control control;
    private final SwitchPanel switchPanel;

    /**
     * Constructs an Animator with the specified CrtPanel, Control, and SwitchPanel.
     *
     * @param crtPanel    the CrtPanel instance used for rendering the display
     * @param control     the Control instance used for executing instructions
     * @param switchPanel the SwitchPanel instance used for manual and automatic control
     */
    public Animator(CrtPanel crtPanel, Control control, SwitchPanel switchPanel) {
        this.crtPanel = crtPanel;
        this.control = control;
        this.switchPanel = switchPanel;

        setDaemon(true); // If the main thread quits, so will this one
    }

    /**
     * Starts the animation.
     */
    public void startAnimating() {
        // start the animation thread
        if (!isAlive()) {
            start();
        }
    }

    /**
     * Stops the animation.
     */
    public void stopAnimating() {
        keepAnimating = false;
        interrupt(); // Ensure the thread wakes up if sleeping
    }

    @Override
    public void run() {

        final long DEFAULT_FRAME_TIME = 10_000_000L; // 10 ms in nanoseconds
        setPriority(Thread.NORM_PRIORITY + 1); // Increment thread priority if needed
        
        keepAnimating = true;

        control.setCycleCount(0);

        Baby.running = true; // Indicate that the animation has started

        // Animation loop, while allowed to keep animating
        while (keepAnimating) {
            long startTime = System.nanoTime();

            // Stop animation if STP lamp lit
            if (control.getStopFlag()) {
                stopAnimating();
                break;
            }

            executeInstructions();

            // Render and repaint
            crtPanel.render();
            crtPanel.efficientRepaint();
            control.incCycleCount();

            // Calculate elapsed time
            long elapsedTime = System.nanoTime() - startTime;

            // Maintain consistent frame rate
            if (elapsedTime < DEFAULT_FRAME_TIME) {
                try {
                    TimeUnit.NANOSECONDS.sleep(DEFAULT_FRAME_TIME - elapsedTime);
                } catch (InterruptedException e) {
                    // Handle interruption appropriately
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        Baby.running = false; // Indicate that the animation has stopped

        // recall when run() completes the Thread terminates
    }

    /**
     * Executes instructions before updating the display.
     */
    private void executeInstructions() {
        for (int x = 0; x < control.getInstructionsPerRefresh() && !control.getStopFlag(); x++) {
            if (switchPanel.getManAuto()) {
                control.executeAutomatic(); // Use store for instructions
            } else {
                control.executeManual(); // Use line and functions switches from the SwitchPanel
            }
        }
    }
}
