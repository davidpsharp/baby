package com.ccs.baby.manager;

import javax.swing.Timer;
import java.awt.event.ActionEvent;

import com.ccs.baby.core.Animator;
import com.ccs.baby.core.Baby;
import com.ccs.baby.core.Control;
import com.ccs.baby.ui.CrtPanel;
import com.ccs.baby.ui.FpsLabelService;
import com.ccs.baby.ui.StaticisorPanel;


public class AnimationManager {

    private final Control control;
    private final CrtPanel crtPanel;
    private final StaticisorPanel staticisorPanel;
    private final ActionLineManager actionLineManger;

    // timer for counting number of instructions executed each second to give speed
    private final Timer fpsTimer;

    // thread control for animation
    private Animator animator;

    private final FpsLabelService fpsLabelService;

    public static volatile boolean animationRunning = false;

    public AnimationManager(Control control, CrtPanel crtPanel, StaticisorPanel staticisorPanel, FpsLabelService fpsLabelService, ActionLineManager actionLineManger) {
        this.control = control;
        this.crtPanel = crtPanel;
        this.staticisorPanel = staticisorPanel;
        this.fpsLabelService = fpsLabelService;
        this.actionLineManger = actionLineManger;

        // create timer to calculate speed to tick once a second
        fpsTimer = new Timer(1000, this::handleFpsTimer);
        fpsTimer.setInitialDelay(0);
    }

    // start running animation
    public synchronized void startAnimation() {

        if (!animationRunning) {
            // create new thread for animation (create a new one as recall a thread can only be started once)
            animator = new Animator(crtPanel, control, staticisorPanel);
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

        actionLineManger.updateActionLine();

        // repaint so that control with the PI can be drawn if necessary
        crtPanel.render();
        crtPanel.repaint();
    }


    // one second timer to measure the speed and correct to real machine speed
    private void handleFpsTimer(ActionEvent e) {

        fpsLabelService.updateFpsLabel();

        if (animationRunning) {
            // we don't want to adjust speed if baby has finished executing as may have only done a few
            // instructions before hit STP instruction which would make this adjust the speed to go faster

            int actualFpsValue = control.getCycleCount() * control.getInstructionsPerRefresh();

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
        } else {
            // if the Baby has stopped animating then no need to keep timing, make this the last FPS update...
            fpsTimer.stop();
        }

    }
}