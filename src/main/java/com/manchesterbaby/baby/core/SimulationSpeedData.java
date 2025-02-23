package com.manchesterbaby.baby.core;

/**
 * Holds simulation speed-related metrics for easy access and passing.
 */
public class SimulationSpeedData {
    private final int instructionsPerSecond;
    private final double executionSpeedPercentage;
    private final double elapsedTime;
    private final int instructionsPerRedraw;

    public SimulationSpeedData(int instructionsPerSecond, double executionSpeedPercentage, double elapsedTime, int instructionsPerRedraw) {
        this.instructionsPerSecond = instructionsPerSecond;
        this.executionSpeedPercentage = executionSpeedPercentage;
        this.elapsedTime = elapsedTime;
        this.instructionsPerRedraw = instructionsPerRedraw;
    }

    public int getInstructionsPerSecond() {
        return instructionsPerSecond;
    }

    public double getExecutionSpeedPercentage() {
        return executionSpeedPercentage;
    }

    public double getElapsedTime() {
        return elapsedTime;
    }

    public int getInstructionsPerRedraw() {
        return instructionsPerRedraw;
    }
}