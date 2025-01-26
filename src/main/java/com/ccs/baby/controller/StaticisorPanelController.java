package com.ccs.baby.controller;

import com.ccs.baby.core.Control;
import com.ccs.baby.manager.ActionLineManager;
import com.ccs.baby.ui.StaticisorPanel;
import com.ccs.baby.ui.CrtPanel;

import javax.swing.*;

public class StaticisorPanelController {

    private final ActionLineManager actionLineManager;

    public StaticisorPanelController(StaticisorPanel staticisorPanel, ActionLineManager actionLineManager) {
        this.actionLineManager = actionLineManager;

        staticisorPanel.setOnManAutoChange(this::handleManAutoChange);
        staticisorPanel.setOnLineSwitchChange(this::handleLineSwitchChange);
    }

    /**
     * Delegate the switch change event to ActionLineManager to update the action line.
     */
    public void handleManAutoChange() {
        actionLineManager.updateActionLine();
    }

    /**
     * Delegate the line switch change event to ActionLineManager to update the action line.
     */
    public void handleLineSwitchChange() {
        actionLineManager.updateActionLine();
    }
}