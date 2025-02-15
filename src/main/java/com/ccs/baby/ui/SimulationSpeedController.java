package com.ccs.baby.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.ccs.baby.core.Control;

import javax.swing.*;


// reset the real world elapsed time to 0
public class SimulationSpeedController implements ActionListener {

    private final Control control;
    private final SimulationSpeedTracker simulationSpeedTracker;

    public SimulationSpeedController(JButton fpsLabel, Control control){
        this.control = control;
        this.simulationSpeedTracker = new SimulationSpeedTracker(fpsLabel, control);
    }

    public void actionPerformed(ActionEvent e) {
        control.setCycleCount(0);
        simulationSpeedTracker.updateFpsLabel();
    }
}
