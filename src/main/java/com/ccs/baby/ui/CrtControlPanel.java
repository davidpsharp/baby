package com.ccs.baby.ui;

import javax.swing.*;
import java.awt.*;

import com.ccs.baby.ui.components.InterlockingPushButton;
import com.ccs.baby.ui.components.KeySwitch;
import com.ccs.baby.ui.components.ToggleSwitch;

import static com.ccs.baby.utils.CallbackUtils.runCallback;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class CrtControlPanel extends JPanel {
    private Runnable onStopRunChange;

    private Runnable onDisplayControlChange;
    private Runnable onDisplayAccumulatorChange;
    private Runnable onDisplayStoreChange;

    private Runnable onKspPressed;
    private Runnable onKlcPressed;
    private Runnable onKlcReleased;
    private Runnable onKscPressed;
    private Runnable onKscReleased;
    private Runnable onKacPressed;
    private Runnable onKacReleased;
    private Runnable onKccPressed;
    private Runnable onKccReleased;

    private final ToggleSwitch stopRunSwitch;
    private final ToggleSwitch writeErase;


    public InterlockingPushButton displayControlButton;
    public InterlockingPushButton displayAccumulatorButton;
    public InterlockingPushButton displayStoreButton;

    private final KeySwitch kspSwitch;

    public CrtControlPanel() {
        setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        setOpaque(false);
        setPreferredSize(new Dimension(0, 110));
        setLayout(new GridBagLayout());

        // NOTE: The CS switch is called the stopRunSwitch switch
        stopRunSwitch = new ToggleSwitch("If switched down then continually executes instructions.");
        stopRunSwitch.addActionListener(e -> runCallback(onStopRunChange));


        // displayControlButtons
        displayControlButton = new InterlockingPushButton("Displays the control on the monitor.");
        displayControlButton.addActionListener(e -> runCallback(onDisplayControlChange));

        displayAccumulatorButton = new InterlockingPushButton("Displays the accumulator on the monitor.");
        displayAccumulatorButton.addActionListener(e -> runCallback(onDisplayAccumulatorChange));

        displayStoreButton = new InterlockingPushButton("Displays the store on the monitor.");
        displayStoreButton.addActionListener(e -> runCallback(onDisplayStoreChange));


        // controlSwitchPanel
        JPanel controlSwitchPanel = new JPanel();
        controlSwitchPanel.setOpaque(false);
        controlSwitchPanel.setLayout(new GridBagLayout());

        kspSwitch = new KeySwitch("Executes a single instruction.", KeySwitch.KeyColour.GREY);    // KC == KSP
        kspSwitch.addActionListener(e -> runCallback(onKspPressed));

        KeySwitch klcSwitch = new KeySwitch("Clears the current action line of the store.", KeySwitch.KeyColour.WHITE);
        klcSwitch.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                runCallback(onKlcPressed);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                runCallback(onKlcReleased);
            }
        });

        KeySwitch kscSwitch = new KeySwitch("Clears the store.", KeySwitch.KeyColour.WHITE);
        kscSwitch.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                runCallback(onKscPressed);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                runCallback(onKscReleased);
            }
        });

        KeySwitch kacSwitch = new KeySwitch("Clears the accumulator.", KeySwitch.KeyColour.WHITE);
        kacSwitch.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                runCallback(onKacPressed);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                runCallback(onKacReleased);
            }
        });

        KeySwitch kccSwitch = new KeySwitch("Clears the CI, PI and Accumulator.", KeySwitch.KeyColour.GREY);
        kccSwitch.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                runCallback(onKccPressed);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                runCallback(onKccReleased);
            }
        });

        // These buttons are not connected as per the original hardware
        KeySwitch kbcSwitch = new KeySwitch("Not connected", KeySwitch.KeyColour.GREY);
        KeySwitch kecSwitch = new KeySwitch("Not connected", KeySwitch.KeyColour.GREY);
        KeySwitch kmcSwitch = new KeySwitch("Not connected", KeySwitch.KeyColour.GREY);

        GridBagConstraints controlSwitchPanelConstraints = new GridBagConstraints();
        controlSwitchPanelConstraints.anchor = GridBagConstraints.WEST;
        controlSwitchPanelConstraints.gridx = 0;
        controlSwitchPanelConstraints.gridy = 0;

        controlSwitchPanelConstraints.insets = new Insets(0, 42, 0, 223);
        controlSwitchPanel.add(kspSwitch, controlSwitchPanelConstraints);

        controlSwitchPanelConstraints.insets = new Insets(0, 115, 0, 0);
        controlSwitchPanel.add(klcSwitch, controlSwitchPanelConstraints);

        controlSwitchPanelConstraints.insets = new Insets(0, 152, 0, 0);
        controlSwitchPanel.add(kscSwitch, controlSwitchPanelConstraints);

        controlSwitchPanelConstraints.insets = new Insets(0, 187, 0, 0);
        controlSwitchPanel.add(kacSwitch, controlSwitchPanelConstraints);

        controlSwitchPanelConstraints.insets = new Insets(0, 226, 0, 0);
        controlSwitchPanel.add(kbcSwitch, controlSwitchPanelConstraints);

        controlSwitchPanelConstraints.insets = new Insets(0, 263, 0, 0);
        controlSwitchPanel.add(kccSwitch, controlSwitchPanelConstraints);

        controlSwitchPanelConstraints.insets = new Insets(0, 296, 0, 0);
        controlSwitchPanel.add(kecSwitch, controlSwitchPanelConstraints);

        controlSwitchPanelConstraints.insets = new Insets(0, 334, 0, 0);
        controlSwitchPanel.add(kmcSwitch, controlSwitchPanelConstraints);


        // writeErasePanel
        JPanel writeErasePanel = new JPanel();
        writeErasePanel.setOpaque(false);
        writeErase = new ToggleSwitch("Selects whether typewriter erases or writes bits.");
        writeErasePanel.add(writeErase, BorderLayout.CENTER);


        // set up panel with monitor select switches above and CS switch below
        GridBagConstraints gridBagConstraints = new GridBagConstraints();

        gridBagConstraints.fill = GridBagConstraints.PAGE_START;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.insets = new Insets(15, 0, 0, 0);

        add(displayControlButton, gridBagConstraints);

        gridBagConstraints.insets = new Insets(15, 50, 0, 0);
        add(displayAccumulatorButton, gridBagConstraints);

        gridBagConstraints.insets = new Insets(15, 100, 0, 0);
        add(displayStoreButton, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new Insets(0, 75, 0, 0);

        add(stopRunSwitch, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.gridheight = 2;

        gridBagConstraints.insets = new Insets(0, 0, 0, 0);

        controlSwitchPanel.setPreferredSize(new Dimension(430, 50));
        add(controlSwitchPanel, gridBagConstraints);

        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 0;

        gridBagConstraints.insets = new Insets(0, 15, 30, 0);
        add(writeErasePanel, gridBagConstraints);

        // set up default settings
        displayStoreButton.setSelected(true);    // default to display store on monitor
        writeErase.setSelected(false);    // default to write

    }


    // true is pulse, false is pre
    public boolean getStopRun() {
        return stopRunSwitch.isSelected();
    }

    // true is pulse, false is pre
    public void setStopRun(boolean value) {
        stopRunSwitch.setSelected(value);
    }

    public void setWriteErase(boolean value) {
        writeErase.setSelected(value);
    }

    public boolean getWriteErase() {
        return !writeErase.isSelected();
    }

    public void setOnStopRunChange(Runnable callback) {
        this.onStopRunChange = callback;
    }

    public boolean isStopRunSelected() {
        return stopRunSwitch.isSelected();
    }

    public void setOnDisplayControlChange(Runnable callback) {
        this.onDisplayControlChange = callback;
    }

    public void setOnDisplayAccumulatorChange(Runnable callback) {
        this.onDisplayAccumulatorChange = callback;
    }

    public void setOnDisplayStoreChange(Runnable callback) {
        this.onDisplayStoreChange = callback;
    }

    public void setOnKspPressed(Runnable callback) {
        this.onKspPressed = callback;
    }

    public void setOnKlcPressed(Runnable callback) {
        this.onKlcPressed = callback;
    }

    public void setOnKlcReleased(Runnable callback) {
        this.onKlcReleased = callback;
    }

    public void setOnKscPressed(Runnable callback) {
        this.onKscPressed = callback;
    }

    public void setOnKscReleased(Runnable callback) {
        this.onKscReleased = callback;
    }

    public void setOnKacPressed(Runnable callback) {
        this.onKacPressed = callback;
    }

    public void setOnKacReleased(Runnable callback) {
        this.onKacReleased = callback;
    }

    public void setOnKccPressed(Runnable callback) {
        this.onKccPressed = callback;
    }

    public void setOnKccReleased(Runnable callback) {
        this.onKccReleased = callback;
    }

    /**
     * Simulate a click on the KSP switch programmatically.
     */
    public void simulateKspClick() {
        kspSwitch.doClick();
    }

    public void simulateStopRunToggle() {
        stopRunSwitch.doClick();
    }

}