package com.ccs.baby.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.ccs.baby.core.Control;
import com.ccs.baby.ui.FpsLabelService;

import javax.swing.*;


// reset the real world elapsed time to 0
public class FpsLabelPushed implements ActionListener {

    private final Control control;
    private final FpsLabelService fpsLabelService;
    private int elapsedTime;
    private JButton fpsLabel;

    public FpsLabelPushed(JButton fpsLabel, Control control){
        this.control = control;
        this.elapsedTime = 0;
        this.fpsLabelService = new FpsLabelService(fpsLabel, control);
    }

    public void actionPerformed(ActionEvent e) {
        elapsedTime = 0;
        control.setCycleCount(0);
        fpsLabelService.updateFpsLabel();
    }
}
