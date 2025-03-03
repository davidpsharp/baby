package com.manchesterbaby.baby.controller;

import com.manchesterbaby.baby.core.Control;
import com.manchesterbaby.baby.core.Store;
import com.manchesterbaby.baby.disassembler.Disassembler;
import com.manchesterbaby.baby.manager.AnimationManager;
import com.manchesterbaby.baby.ui.CrtControlPanel;
import com.manchesterbaby.baby.ui.CrtPanel.DisplayType;

public class CrtControlPanelController {

    private final Store store;
    private final Control control;
    private final Disassembler disassembler;
    private final AnimationManager animationManager;
    private final CrtControlPanel crtControlPanel;
    private final CrtPanelController crtPanelController;
    private final StaticisorPanelController staticisorPanelController;
    private TypewriterPanelController typewriterPanelController;

    public CrtControlPanelController(
            Store store,
            Control control,
            Disassembler disassembler,
            AnimationManager animationManager,
            CrtControlPanel crtControlPanel,
            CrtPanelController crtPanelController,
            StaticisorPanelController staticisorPanelController
    ) {
        this.store = store;
        this.control = control;
        this.disassembler = disassembler;
        this.animationManager = animationManager;
        this.crtControlPanel = crtControlPanel;
        this.crtPanelController = crtPanelController;
        this.staticisorPanelController = staticisorPanelController;

        crtControlPanel.setOnStopRunChange(this::handleStopRunChange);

        crtControlPanel.setOnDisplayControlChange(() -> crtPanelController.setCrtDisplay(DisplayType.CONTROL));
        crtControlPanel.setOnDisplayAccumulatorChange(() -> crtPanelController.setCrtDisplay(DisplayType.ACCUMULATOR));
        crtControlPanel.setOnDisplayStoreChange(() -> crtPanelController.setCrtDisplay(DisplayType.STORE));

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

    public void addTypewriterPanelController(TypewriterPanelController aTypewriterPanelController) {
        this.typewriterPanelController = aTypewriterPanelController;
    }


    // --- State Modification Methods --- //

    public boolean isStopRun() {
        return crtControlPanel.getStopRun();
    }

    public void setStopRun(boolean value) {
        crtControlPanel.setStopRun(value);
    }

    public boolean isWriteErase() {
        return crtControlPanel.getWriteErase();
    }

    public void setWriteErase(boolean value) {
        crtControlPanel.setWriteErase(value);
    }

    public void simulateKspClick() {
        crtControlPanel.getKspSwitch().doClick();
    }

    public void simulateStopRunToggle() {
        crtControlPanel.getStopRunSwitch().doClick();
    }

    public void setDisplayControlButton(boolean value) {
        crtControlPanel.getDisplayControlButton().setSelected(value);
    }

    public void setDisplayAccumulatorButton(boolean value) {
        crtControlPanel.getDisplayAccumulatorButton().setSelected(value);
    }

    public void setDisplayStoreButton(boolean value) {
        crtControlPanel.getDisplayStoreButton().setSelected(value);
    }

    public void setManAuto(boolean value)
    {
        staticisorPanelController.setManAuto(value);
    }

    /**
     * Handle interactively pushing buttons on the staticisor panel and typewriter with delay to load
     * values into the store as a person would have had to to load a program, rather than just pushing
     * the value straight into the store. This is intended to be used when loading a program from file
     * and run in a background thread
     * @param lineNumber The line number to set (0-31)
     * @param value The value to set at that line
     */
    public void setLine(int lineNumber, int value) {

        // set line number
        // should probably call doClick on the specific control, but see if this works first...
        for (int i = 0; i < 5; i++) {
            boolean newValue = ((lineNumber >> i) & 1)==1 ? true : false;
            staticisorPanelController.setLineSwitch(i, newValue);
            actionDelay();
        }

        // start pressing typewriter buttons for every bit set
        for (int i = 0; i < 32; i++) {
            if(((value >> i) & 1)==1) {
                typewriterPanelController.pressKey(i);
                actionDelay();
            }
        }

        // TODO: currently no way of stopping the interactive loading algorithm, can get really
        // funky if you load another program during the time it's loading, have to just wait a while
        // for it to finish
        
    }

    private void actionDelay() {
        final int ACTION_DELAY = 10; // ms

        try {
            Thread.sleep(ACTION_DELAY);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    // if pulse execute instructions repeatedly either from store or from line and function switches
    private void handleStopRunChange() {
        // are we now on pre or pulse?
        // on pulse
        if (isStopRun()) {
            // turn off the highlighted action line
            crtPanelController.resetActionLine();

            // whether to take instructions from store or line and function switches
            // is handled in the actual execution of the animation
            animationManager.startAnimation();

        }
        // on pre
        else {
            animationManager.stopAnimation();
            staticisorPanelController.updateActionLineListeners();
        }
    }

    // execute a single instruction (either from store or from line and function switches)
    private void handleKspPushed() {
        // clear stop flag if set
        if (control.getStopFlag())
            control.setStopFlag(false);

        // true is Auto, false is Man
        if (staticisorPanelController.isManAuto()) {
            // perform single instruction from store
            control.executeAutomatic();
            staticisorPanelController.updateActionLineListeners();
            crtPanelController.redrawCrtPanel();
        } else {
            // perform single instruction from line and function switches
            control.executeManual();
            staticisorPanelController.updateActionLineListeners();
            crtPanelController.redrawCrtPanel();
        }
    }

    private void handleKlcPressed() {
        if (AnimationManager.animationRunning) {
            control.setKlcPressed(true);
        } else {
            store.setLine(staticisorPanelController.getSelectedLineSwitchesValue(), 0);
            crtPanelController.redrawCrtPanel();
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
            crtPanelController.redrawCrtPanel();
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
            staticisorPanelController.updateActionLineListeners();
            crtPanelController.redrawCrtPanel();
        }
    }

    public void handleKccReleased() {
        control.setKccPressed(false);
    }


    private void handleKacPushed() {
        if (!AnimationManager.animationRunning) {
            control.setAccumulator(0);
            crtPanelController.redrawCrtPanel();
        } else {
            control.setKacPressed(true);
        }
    }

    private void handleKacReleased() {
        control.setKacPressed(false);
    }

    public void redrawCrtPanel() {
        crtPanelController.redrawCrtPanel();
    }

    public void setAllLineSwitchesDown() {
        for (int i = 0; i < 5; i++) {
            staticisorPanelController.setLineSwitch(i, true);
        }
    }

}
