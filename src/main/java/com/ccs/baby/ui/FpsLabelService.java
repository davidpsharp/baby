package com.ccs.baby.ui;

import javax.swing.*;

import com.ccs.baby.core.Control;

public class FpsLabelService {
    private final JButton fpsLabel;
    private final Control control;

    // number of instructions real Baby executed in a second
    private static final double REAL_CYCLES_PER_SECOND = 700.0;
    private double elapsedTime = 0; // in seconds

    public FpsLabelService(JButton fpsLabel, Control control) {
        this.fpsLabel = fpsLabel;
        this.control = control;
    }

    public void updateFpsLabel() {
        elapsedTime += (((double) (control.getCycleCount() * control.getInstructionsPerRefresh())) / REAL_CYCLES_PER_SECOND);

        // round percentage of real speed to 1 dec place
        String percentage = "" + (((control.getCycleCount() * control.getInstructionsPerRefresh()) / REAL_CYCLES_PER_SECOND) * 100);
        int pointPos = percentage.indexOf('.');
        if (pointPos != -1)
            percentage = percentage.substring(0, pointPos + 2);

        // round elapsed time in seconds to 1 dec place
        String elapsedTimeS = "" + elapsedTime;
        pointPos = elapsedTimeS.indexOf('.');
        if (pointPos != -1)
            elapsedTimeS = elapsedTimeS.substring(0, pointPos + 2);

        fpsLabel.setText("" + (control.getCycleCount() * control.getInstructionsPerRefresh()) + " fps "
                + percentage + "% "
                + elapsedTimeS + "s");
    }
}
