package com.ccs.baby.debug;

import com.ccs.baby.core.Control;
import com.ccs.baby.ui.SimulationSpeedController;
import com.ccs.baby.ui.SimulationSpeedTracker;
import com.ccs.baby.utils.AppSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DebugPanel extends JPanel implements ActionListener {

    // modern controls
    protected JButton stepButton = new JButton("Step");
    protected JButton runButton = new JButton("Run");
    protected JButton stopButton = new JButton("Stop");
    private final SimulationSpeedTracker simulationSpeedTracker;

    public static Color backgroundColor = new Color(206, 205, 201);

    private final Control control;

    public DebugPanel(Control control) {

        this.control = control;


        setLayout(new BorderLayout(0, 0)); // Ensure no gaps
        setBackground(backgroundColor);

        setVisible(AppSettings.getInstance().isShowDebugPanel());

        JPanel toolPanel = new JPanel();
        toolPanel.setBackground(backgroundColor);
        toolPanel.setBorder(null);
        toolPanel.add(stepButton);
        toolPanel.add(runButton);
        toolPanel.add(stopButton);

        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(backgroundColor);
        infoPanel.setBorder(null);

        JButton fpsLabel = new JButton("Speed and elapsed time info.");
        fpsLabel.addActionListener(new SimulationSpeedController(fpsLabel, control));
        infoPanel.add(fpsLabel);

        simulationSpeedTracker = new SimulationSpeedTracker(fpsLabel, control);

        // check for clicks on modern controls
        stepButton.addActionListener(this);
        runButton.addActionListener(this);
        stopButton.addActionListener(this);

        JPanel container = new JPanel();
        container.setBorder(null);
        container.setBackground(backgroundColor);
        container.add(infoPanel);
        container.add(toolPanel);

        // Add modernControls to ModernPanel
        add(container, BorderLayout.CENTER);

        // set tool tips
        stepButton.setToolTipText("Execute the next instruction.");
        runButton.setToolTipText("Start executing the instructions in the store.");
        stopButton.setToolTipText("Stop executing instructions.");
        fpsLabel.setToolTipText("Displays the speed of the simulation.");
    }

    public SimulationSpeedTracker getFpsLabelService() {
        return simulationSpeedTracker;
    }

    public void actionPerformed(ActionEvent e) {
        // if step button pressed
        if (e.getSource() == stepButton) {
            control.singleStep();
        } else if (e.getSource() == runButton) {
            control.startRunning();
        }
        // if stop button pressed the turn off the CS switch
        else if (e.getSource() == stopButton) {
            control.stopRunning();
        }
    }
}
