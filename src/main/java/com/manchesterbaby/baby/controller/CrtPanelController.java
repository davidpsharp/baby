package com.manchesterbaby.baby.controller;

import java.util.ArrayList;
import java.util.List;

import com.manchesterbaby.baby.controller.listener.CrtPanelDisplayTypeListener;
import com.manchesterbaby.baby.event.CrtPanelRedrawListener;
import com.manchesterbaby.baby.ui.CrtPanel;

public class CrtPanelController {
    private final CrtPanel crtPanel;
    private final List<CrtPanelDisplayTypeListener> listeners = new ArrayList<>();
    private final List<CrtPanelRedrawListener> redrawListeners = new ArrayList<>();

    public CrtPanelController(CrtPanel crtPanel) {
        this.crtPanel = crtPanel;
    }

    public void addDisplayTypeListener(CrtPanelDisplayTypeListener listener) {
        listeners.add(listener);
    }

    public void addRedrawListener(CrtPanelRedrawListener listener) {
        if (!redrawListeners.contains(listener)) {
            redrawListeners.add(listener);
        }
    }

    public void removeRedrawListener(CrtPanelRedrawListener listener) {
        redrawListeners.remove(listener);
    }

    private void notifyRedrawListeners() {
        for (CrtPanelRedrawListener listener : redrawListeners) {
            listener.onCrtPanelRedrawn();
        }
    }

    private void notifyCrtPanelDisplayTypeListeners(CrtPanel.DisplayType displayType) {
        for (CrtPanelDisplayTypeListener listener : listeners) {
            listener.onDisplayChange(displayType);
        }
    }

    public void setCrtDisplay(CrtPanel.DisplayType displayType) {
        crtPanel.setCrtDisplay(displayType);
        notifyCrtPanelDisplayTypeListeners(displayType);
    }

    public void setActionLine(int lineNumber) {
        crtPanel.setActionLine(lineNumber);
    }

    public void resetActionLine() {
        crtPanel.setActionLine(-1); // Reset the action line by setting it to -1
    }

    public void redrawCrtPanel() {
        renderCurrentDisplay();
        crtPanel.repaint();
        notifyRedrawListeners();
    }

    public void efficientRepaint() {
        crtPanel.efficientRepaint();
        notifyRedrawListeners();
    }

    // New method to trigger rendering based on current display type
    public void renderCurrentDisplay() {
        switch (crtPanel.getCurrentDisplay()) {
            case STORE:
                renderStore();
                break;
            case ACCUMULATOR:
                renderAccumulator();
                break;
            case CONTROL:
                renderControl();
                break;
            default:
                renderStore();
                break;
        }
    }

    // Delegating rendering functions
    public void renderStore() {
        crtPanel.renderStore();
    }

    public void renderAccumulator() {
        crtPanel.renderAccumulator();
    }

    public void renderControl() {
        crtPanel.renderControl();
    }

}