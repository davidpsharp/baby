package com.ccs.baby.controller;

import com.ccs.baby.manager.AnimationManager;
import com.ccs.baby.core.Baby;
import com.ccs.baby.core.Control;
import com.ccs.baby.core.Store;
import com.ccs.baby.disassembler.Disassembler;

import com.ccs.baby.manager.ActionLineManager;
import com.ccs.baby.ui.CrtPanel;
import com.ccs.baby.ui.CrtControlPanel;
import com.ccs.baby.ui.StaticisorPanel;


public class CrtControlPanelController {
    private final ActionLineManager actionLineManager;
    private final CrtControlPanel crtControlPanel;
    private final AnimationManager animationManager;
    private final Store store;
    private final Control control;
    private final CrtPanel crtPanel;
    private final StaticisorPanel staticisorPanel;
    private final Disassembler disassembler;

    public CrtControlPanelController(ActionLineManager actionLineManager, AnimationManager animationManager, CrtControlPanel crtControlPanel, Baby baby, Store store, Control control, CrtPanel crtPanel, StaticisorPanel staticisorPanel, Disassembler disassembler) {
        this.actionLineManager = actionLineManager;
        this.animationManager = animationManager;
        this.crtControlPanel = crtControlPanel;
        this.store = store;
        this.control = control;
        this.crtPanel = crtPanel;
        this.staticisorPanel = staticisorPanel;
        this.disassembler = disassembler;


        crtControlPanel.setOnStopRunChange(this::handleStopRunChange);

        crtControlPanel.setOnDisplayControlChange(() ->
                crtPanel.setCrtDisplay(CrtPanel.DisplayType.CONTROL)
        );

        crtControlPanel.setOnDisplayAccumulatorChange(() ->
                crtPanel.setCrtDisplay(CrtPanel.DisplayType.ACCUMULATOR)
        );

        crtControlPanel.setOnDisplayStoreChange(() ->
                crtPanel.setCrtDisplay(CrtPanel.DisplayType.STORE)
        );

        crtControlPanel.setOnKspPressed(this::handleKspPushed);

        crtControlPanel.setOnKlcPressed(this::handleKlcPressed);
        crtControlPanel.setOnKlcReleased(this::handleKlcReleased);

        crtControlPanel.setOnKscPressed(this::handleKscPushed);
        crtControlPanel.setOnKscReleased(this::handleKscReleased);

        crtControlPanel.setOnKccPressed(this::handleKccPushed);
        crtControlPanel.setOnKccReleased(this::handleKccReleased);

        crtControlPanel.setOnKacPressed(this::handleKacPushed);
        crtControlPanel.setOnKacReleased(this::handleKacReleased);
    }

    // if pulse execute instructions repeatedly either from store or from line and function switches
    private void handleStopRunChange() {
        // are we now on pre or pulse?
        // on pulse
        if (crtControlPanel.getStopRun()) {
            // turn off the highlighted action line
            crtPanel.setActionLine(-1);
            // whether to take instructions from store or line and function switches
            // is handled in the actual execution of the animation
            animationManager.startAnimation();

        }
        // on pre
        else {
            animationManager.stopAnimation();
            actionLineManager.updateActionLine();
        }
    }

    // execute a single instruction (either from store or from line and function switches)
    private void handleKspPushed() {
        // clear stop flag if set
        if (control.getStopFlag())
            control.setStopFlag(false);

        // true is Auto, false is Man
        if (staticisorPanel.getManAuto()) {
            // perform single instruction from store
            control.executeAutomatic();
            actionLineManager.updateActionLine();
            crtPanel.render();
            crtPanel.repaint();

            disassembler.updateDisassemblerOnStep();
        } else {
            // perform single instruction from line and function switches
            control.executeManual();
            actionLineManager.updateActionLine();
            crtPanel.render();
            crtPanel.repaint();
        }
    }

    private void handleKlcPressed() {
        if (AnimationManager.animationRunning) {
            control.setKlcPressed(true);
        } else {
            store.setLine(staticisorPanel.getLineValue(), 0);
            crtPanel.render();
            crtPanel.repaint();
        }
    }

    private void handleKlcReleased() {
        control.setKlcPressed(false);
    }


    // clear store
    private void handleKscPushed() {
        if (AnimationManager.animationRunning) {
            control.setKscPressed(true);
        } else {
            store.reset();
            crtPanel.redrawCrtPanel();
        }
    }

    private void handleKscReleased() {
        control.setKscPressed(false);
    }


    // clear CI,PI and ACC
    public void handleKccPushed() {
        if (AnimationManager.animationRunning) {
            control.setKccPressed(true);
        } else {
            control.setControlInstruction(0);
            control.setPresentInstruction(0);
            control.setAccumulator(0);
            actionLineManager.updateActionLine();
            crtPanel.redrawCrtPanel();
        }
    }

    public void handleKccReleased() {
        control.setKccPressed(false);
    }


    private void handleKacPushed() {
        if (!AnimationManager.animationRunning) {
            control.setAccumulator(0);
            crtPanel.render();
            crtPanel.repaint();
        } else {
            control.setKacPressed(true);
        }
    }

    private void handleKacReleased() {
        control.setKacPressed(false);
    }

}
