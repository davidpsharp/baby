package com.ccs.baby.ui;

import com.ccs.baby.utils.AppSettings;
import com.ccs.baby.ui.components.DebugButton;

import static com.ccs.baby.utils.CallbackUtils.runCallback;

import javax.swing.*;
import java.awt.*;

public class DebugPanel extends JPanel {

    // Callbacks
    private Runnable onStepPressed;
    private Runnable onRunPressed;
    private Runnable onStopPressed;
    private Runnable onResetElapsedTimePressed;

    public DebugButton stepButton;
    public DebugButton runButton;
    public DebugButton stopButton;
    public DebugButton resetElapsedTimeButton;

    private JLabel simulationSpeedLabel;

    public static Color BACKGROUND_COLOR = new Color(206, 205, 201);

    public DebugPanel() {
        setLayout(new BorderLayout(0, 0)); // Ensure no gaps
        setBackground(BACKGROUND_COLOR);
        setVisible(AppSettings.getInstance().isShowDebugPanel());

        JPanel toolPanel = createToolPanel();
        JPanel infoPanel = createInfoPanel();

        JPanel container = new JPanel();
        container.setBorder(null);
        container.setBackground(BACKGROUND_COLOR);
        container.add(toolPanel);
        container.add(infoPanel);

        add(container, BorderLayout.CENTER);
    }

    private JPanel createToolPanel() {
        JPanel toolPanel = new JPanel();
        toolPanel.setBackground(BACKGROUND_COLOR);
        toolPanel.setBorder(null);

        stepButton = new DebugButton("Step", "Execute the next instruction.");
        runButton = new DebugButton("Run", "Start executing the instructions in the store.");
        stopButton = new DebugButton("Stop", "Stop executing instructions.");

        toolPanel.add(stepButton);
        toolPanel.add(runButton);
        toolPanel.add(stopButton);

        // Attach event listeners using callbacks
        stepButton.addActionListener(e -> runCallback(onStepPressed));
        runButton.addActionListener(e -> runCallback(onRunPressed));
        stopButton.addActionListener(e -> runCallback(onStopPressed));

        return toolPanel;
    }

    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBackground(BACKGROUND_COLOR);
        infoPanel.setBorder(null);

        // Wrap label in a box with padding and background color
        JPanel speedBox = new JPanel();
        speedBox.setBackground(new Color(220, 220, 220)); // Light gray box
        speedBox.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        simulationSpeedLabel = new JLabel("Simulation Speed Initializing...");
        simulationSpeedLabel.setToolTipText("Displays the current execution speed of the simulation.");

        speedBox.add(simulationSpeedLabel);
        infoPanel.add(speedBox);

        // Add Reset Elapsed Time Button
        resetElapsedTimeButton = new DebugButton("Reset", "Reset the elapsed time counter.");
        resetElapsedTimeButton.addActionListener(e -> runCallback(onResetElapsedTimePressed));
        infoPanel.add(resetElapsedTimeButton);

        return infoPanel;
    }

    public void setOnStepPressed(Runnable callback) {
        this.onStepPressed = callback;
    }

    public void setOnRunPressed(Runnable callback) {
        this.onRunPressed = callback;
    }

    public void setOnStopPressed(Runnable callback) {
        this.onStopPressed = callback;
    }

    /**
     * Updates the simulation speed display.
     * Called by DebugPanelController when speed updates occur.
     *
     * @param speedText The new simulation speed text.
     */
    public void updateSimulationSpeedDisplay(String speedText) {
        simulationSpeedLabel.setText(speedText);
    }

    public void setOnResetElapsedTimePressed(Runnable callback) {
        this.onResetElapsedTimePressed = callback;
    }

}
