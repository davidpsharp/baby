package com.ccs.baby.ui;

import com.ccs.baby.utils.AppSettings;
import com.ccs.baby.ui.components.DebugButton;
import com.ccs.baby.core.SimulationSpeedData;

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

    private JLabel instructionsPerSecLabel;
    private JLabel executionSpeedPercentageLabel;
    private JLabel elapsedTimeLabel;
    private JLabel instructionsPerRedrawLabel;


    public static Color BACKGROUND_COLOR = new Color(206, 205, 201);

    public DebugPanel() {
        setLayout(new BorderLayout(0, 0)); // Ensure no gaps
        setBackground(BACKGROUND_COLOR);
        setPreferredSize(new Dimension(700, 90));
        setVisible(AppSettings.getInstance().isShowDebugPanel());

        // Create tool panel and info panel
        JPanel toolPanel = createToolPanel();
        JPanel infoPanel = createInfoPanel();

        // Add toolPanel at the TOP (NORTH) and infoPanel BELOW it (SOUTH)
        add(toolPanel, BorderLayout.NORTH);
        add(infoPanel, BorderLayout.SOUTH);
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
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Center align info
        infoPanel.setBackground(BACKGROUND_COLOR);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Add spacing

        // Create a structured display for simulation speed
        JPanel speedBox = new JPanel(new GridLayout(2, 4, 10, 2)); // 2 rows, 4 columns
        speedBox.setBackground(new Color(220, 220, 220)); // Light gray background
        speedBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),  // Outer border
                BorderFactory.createEmptyBorder(0, 10, 0, 10)   // Inner padding
        ));

        // Titles
        JLabel instrSecTitle = new JLabel("Instr/sec", SwingConstants.CENTER);
        instrSecTitle.setToolTipText("Number of instructions executed per second.");

        JLabel speedPercentTitle = new JLabel("Speed", SwingConstants.CENTER);
        speedPercentTitle.setToolTipText("Percentage of real-time execution speed (700 instr/sec = 100%).");

        JLabel elapsedTimeTitle = new JLabel("Elapsed", SwingConstants.CENTER);
        elapsedTimeTitle.setToolTipText("Total elapsed simulation time in seconds.");

        JLabel instrRedrawTitle = new JLabel("Instr/Redraw", SwingConstants.CENTER);
        instrRedrawTitle.setToolTipText("Number of instructions executed before screen redraw.");

        // Values (updated dynamically)
        instructionsPerSecLabel = new JLabel("0", SwingConstants.CENTER);
        executionSpeedPercentageLabel = new JLabel("0%", SwingConstants.CENTER);
        elapsedTimeLabel = new JLabel("0.0s", SwingConstants.CENTER);
        instructionsPerRedrawLabel = new JLabel("0", SwingConstants.CENTER);

        // Add components in GridLayout
        speedBox.add(instrSecTitle);
        speedBox.add(speedPercentTitle);
        speedBox.add(elapsedTimeTitle);
        speedBox.add(instrRedrawTitle);
        speedBox.add(instructionsPerSecLabel);
        speedBox.add(executionSpeedPercentageLabel);
        speedBox.add(elapsedTimeLabel);
        speedBox.add(instructionsPerRedrawLabel);

        infoPanel.add(speedBox);

        // Add Reset Button next to it
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
     * @param speedData The latest simulation speed data.
     */
    public void updateSimulationSpeedDisplay(SimulationSpeedData speedData) {
        instructionsPerSecLabel.setText(String.valueOf(speedData.getInstructionsPerSecond()));
        executionSpeedPercentageLabel.setText(String.format("%.1f%%", speedData.getExecutionSpeedPercentage()));
        elapsedTimeLabel.setText(String.format("%.1fs", speedData.getElapsedTime()));
        instructionsPerRedrawLabel.setText(String.valueOf(speedData.getInstructionsPerRedraw()));
    }

    public void setOnResetElapsedTimePressed(Runnable callback) {
        this.onResetElapsedTimePressed = callback;
    }

}
