package com.ccs.baby.animation;

import com.ccs.baby.core.Animator;
import com.ccs.baby.core.Baby;
import com.ccs.baby.core.Control;
import com.ccs.baby.ui.CrtPanel;
import com.ccs.baby.ui.SwitchPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;

import com.ccs.baby.ui.FpsLabelService;

public class AnimationManager {

    private final Control control;
    private final CrtPanel crtPanel;
    private final SwitchPanel switchPanel;

    // there are two implementations of the animation, one using the Timer class and one
    // using threads, if this is set to true then the threaded version is used which
    // seems to be the best all round solution (Timer is poor on Solaris).
    private final boolean threadedAnimation;

    // timer control for animation (alternative to thread)
    private Timer animateTimer;

    // timer for counting number of instructions executed each second to give speed
    private Timer fpsTimer;

    private java.util.Timer clockTimer;

    // thread control for animation
    private Animator animator;

    private final FpsLabelService fpsLabelService;

    private boolean running = false;

    public AnimationManager(Control control, CrtPanel crtPanel, SwitchPanel switchPanel, boolean threadedAnimation, FpsLabelService fpsLabelService) {
        this.control = control;
        this.crtPanel = crtPanel;
        this.switchPanel = switchPanel;
        this.threadedAnimation = threadedAnimation;
        this.fpsLabelService = fpsLabelService;
        setupTimers();
    }

    private void setupTimers() {

        // set up timer animation (currently not used)
        // delay is in milliseconds
        // unless timer is 0 then we get horrendous slow down
        animateTimer = new Timer(0, this::handleAnimateTimer);
        animateTimer.setInitialDelay(0);
        animateTimer.setCoalesce(true);

        // create timer to calculate speed to tick once a second
        fpsTimer = new Timer(1000, this::handleFpsTimer);
        fpsTimer.setInitialDelay(0);
    }

    // start running animation either using threads or timer
    // this is hardcoded and cannot me changed mid-session
    public synchronized void startAnimation() {
        if (threadedAnimation) {
            if (!running) {
                // create new thread for animation
                animator = new Animator(crtPanel, control, switchPanel);
                animator.startAnimating();
            }
        } else {
            if (!animateTimer.isRunning()) {
                // start the animation timer running
                animateTimer.start();
            }
        }

        // start fps timer
        fpsTimer.start();
        control.setCycleCount(0);
        running = true;
    }

    // halt animation
    public synchronized void stopAnimation() {
        if (threadedAnimation) {
            if (running) {
                animator.stopAnimating();
                running = false;
            }
        } else {
            if (animateTimer.isRunning()) {
                animateTimer.stop();
                running = false;
            }
        }
        switchPanel.updateActionLine();

        // repaint so that control with the PI can be drawn if necessary
        crtPanel.render();
        crtPanel.repaint();
        fpsTimer.stop();
    }

    // animation timer tick
    private void handleAnimateTimer(ActionEvent e) {
        // if STP instruction has been executed stop the animation
        if (control.getStopFlag()) {
            stopAnimation();
        } else {
            // execute X instructions before updating the display (while stop flag is unset)
            for (int x = 0; x < control.getInstructionsPerRefresh() && !control.getStopFlag(); x++) {
                // if auto on switchpanel then use store for instructions
                if (switchPanel.getManAuto()) {
                    control.executeAutomatic();
                }
                // else if man then use the line and functions switches from the switch panel
                else {
                    control.executeManual();
                }
            }

            // update display
            crtPanel.render();
            crtPanel.efficientRepaint();

            // increment number of cycles of X instructions executed
            control.incCycleCount();
        }
    }

    // if the one second timer to measure the speed
    private void handleFpsTimer(ActionEvent e) {


        fpsLabelService.updateFpsLabel();

        int actualFpsValue = control.getCycleCount() * control.getInstructionsPerRefresh();

        // speed adjustment
        // adjust number of instructions per refresh to get 700 fps or as close as possible
        if (actualFpsValue > 730) {
            int newValue = control.getInstructionsPerRefresh();
            newValue--;
            if (newValue < 1) newValue = 1;
            control.setInstructionsPerRefresh(newValue);
        } else if (actualFpsValue < 670) {
            int newValue = control.getInstructionsPerRefresh();
            newValue++;
            if (newValue > 20) newValue = 20;
            control.setInstructionsPerRefresh(newValue);
        }

        // reset counter ready for the next second
        control.setCycleCount(0);

        // if the Baby has stopped animating then no need to keep timing.
        if (!running)
            fpsTimer.stop();


    }
}