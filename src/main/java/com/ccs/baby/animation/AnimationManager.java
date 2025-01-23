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

    // timer for counting number of instructions executed each second to give speed
    private Timer fpsTimer;

    // thread control for animation
    private Animator animator;

    private final FpsLabelService fpsLabelService;

    private boolean running = false;

    public AnimationManager(Control control, CrtPanel crtPanel, SwitchPanel switchPanel, FpsLabelService fpsLabelService) {
        this.control = control;
        this.crtPanel = crtPanel;
        this.switchPanel = switchPanel;
        this.fpsLabelService = fpsLabelService;

        // create timer to calculate speed to tick once a second
        fpsTimer = new Timer(1000, this::handleFpsTimer);
        fpsTimer.setInitialDelay(0);
    }

    // start running animation
    public synchronized void startAnimation() {
        
        if (!running) {
            // create new thread for animation
            animator = new Animator(crtPanel, control, switchPanel);
            animator.startAnimating();
        }
        
        // start fps timer
        fpsTimer.start();
        control.setCycleCount(0);
        running = true;
    }

    // halt animation
    // TODO: bug, this doesn't get called if STP instruction executed (in threaded running mode, possibly in timer mode too)
    // only called if stop/run switch pressed or Stop button pressed on debug panel; means keeps fpsTimer going when needs to stop :(
    public synchronized void stopAnimation() {
        if (running) {
            animator.stopAnimating();
            running = false;
        }
       
        switchPanel.updateActionLine();

        // repaint so that control with the PI can be drawn if necessary
        crtPanel.render();
        crtPanel.repaint();
        fpsTimer.stop();
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