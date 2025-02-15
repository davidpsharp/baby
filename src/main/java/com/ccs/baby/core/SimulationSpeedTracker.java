package com.ccs.baby.core;

public class SimulationSpeedTracker {

    private final Control control;
    private Runnable listener;

    // The number of instructions real Baby executed in a second
    private static final double REAL_CYCLES_PER_SECOND = 700.0;

    // TODO: is this simulated or wall-clock time?
    private double elapsedTime = 0;
    private int instructionsPerSecond = 0;
    private int instructionsPerRedraw = 0;


    public SimulationSpeedTracker(Control control) {
        this.control = control;
    }

    public void setListener(Runnable listener) {
        this.listener = listener;
    }

    private void notifyListener() {
        if (listener != null) {
            listener.run();
        }
    }

    public void resetElapsedTime() {
        elapsedTime = 0;
        control.setCycleCount(0);
        notifyListener();
    }

    public void updateSpeed() {
        // Calculate the elapsed time in seconds
        elapsedTime += (((double) (control.getCycleCount() * control.getInstructionsPerRefresh())) / REAL_CYCLES_PER_SECOND);

        // Calculate the number of instructions executed per second
        instructionsPerSecond = control.getCycleCount() * control.getInstructionsPerRefresh();

        // Get the number of instructions processed per screen redraw
        instructionsPerRedraw = control.getInstructionsPerRefresh();

        notifyListener();
    }

    /**
     * <p>Dynamically adjusts the number of instructions executed per redraw
     * to keep execution speed close to 700 instructions per second.</p>
     *
     * <p>We don't want to adjust speed if Baby has finished executing as may
     * have only done a few instructions before hit STP instruction which
     * would make this adjust the speed to go faster.</p>
     */
    public void regulateSpeed() {
        if (instructionsPerSecond > 730) {
            // Reduce speed
            int newInstructionsPerRedraw = instructionsPerRedraw - 1;
            control.setInstructionsPerRefresh(Math.max(newInstructionsPerRedraw, 1));
        } else if (instructionsPerSecond < 670) {
            // Increase speed
            int newInstructionsPerRedraw = instructionsPerRedraw + 1;
            control.setInstructionsPerRefresh(Math.min(newInstructionsPerRedraw, 20));
        }

        // Reset cycle count for the next measurement
        control.setCycleCount(0);
    }


    public String formatSpeedText() {

        // Calculate the percentage of real-world execution speed ()
        double executionSpeedPercentage = (instructionsPerSecond / REAL_CYCLES_PER_SECOND) * 100;

        // Format and return the speed information
        return String.format("%d instr/sec | %.1f%% | %.1fs | %d instr/redraw",
                instructionsPerSecond,
                executionSpeedPercentage, // Rounded to 1 decimal place
                elapsedTime, // Rounded to 1 decimal place
                instructionsPerRedraw
        );
    }
}
