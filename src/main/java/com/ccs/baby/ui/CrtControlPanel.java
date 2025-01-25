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



        setOpaque(false);

        JPanel crtControls = new JPanel();
        crtControls.setOpaque(true);
        crtControls.setPreferredSize(new Dimension(0, 110));
        crtControls.setLayout(new GridBagLayout());

        crtControls.setBorder(BorderFactory.createLineBorder(Color.YELLOW,2 ));

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


        // kathodeControlSwitches
        // to be added to crtControls panel
        JPanel kathodeControlSwitches = new JPanel();

        kathodeControlSwitches.setOpaque(false);
        kathodeControlSwitches.setLayout(new GridBagLayout());
//        kathodeControlSwitches.setBorder(BorderFactory.createLineBorder(Color.BLUE));


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


        GridBagConstraints kathodeControlSwitchesGrid = new GridBagConstraints();
        kathodeControlSwitchesGrid.anchor = GridBagConstraints.WEST;
        kathodeControlSwitchesGrid.gridx = 0;
        kathodeControlSwitchesGrid.gridy = 0;

        kathodeControlSwitchesGrid.insets = new Insets(0, 42, 0, 223);
        kathodeControlSwitches.add(kspSwitch, kathodeControlSwitchesGrid);

        kathodeControlSwitchesGrid.insets = new Insets(0, 115, 0, 0);
        kathodeControlSwitches.add(klcSwitch, kathodeControlSwitchesGrid);

        kathodeControlSwitchesGrid.insets = new Insets(0, 152, 0, 0);
        kathodeControlSwitches.add(kscSwitch, kathodeControlSwitchesGrid);

        kathodeControlSwitchesGrid.insets = new Insets(0, 187, 0, 0);
        kathodeControlSwitches.add(kacSwitch, kathodeControlSwitchesGrid);

        kathodeControlSwitchesGrid.insets = new Insets(0, 226, 0, 0);
        kathodeControlSwitches.add(kbcSwitch, kathodeControlSwitchesGrid);

        kathodeControlSwitchesGrid.insets = new Insets(0, 263, 0, 0);
        kathodeControlSwitches.add(kccSwitch, kathodeControlSwitchesGrid);

        kathodeControlSwitchesGrid.insets = new Insets(0, 296, 0, 0);
        kathodeControlSwitches.add(kecSwitch, kathodeControlSwitchesGrid);

        kathodeControlSwitchesGrid.insets = new Insets(0, 334, 0, 0);
        kathodeControlSwitches.add(kmcSwitch, kathodeControlSwitchesGrid);

        JPanel writeErasePanel = new JPanel();
        writeErasePanel.setBorder(BorderFactory.createLineBorder(Color.RED,2 ));
        writeErasePanel.setOpaque(false);
        writeErase = new ToggleSwitch("Selects whether typewriter erases or writes bits.");
        writeErasePanel.add(writeErase, BorderLayout.CENTER);

        // set up default settings
        displayStoreButton.setSelected(true);    // default to display store on monitor
        writeErase.setSelected(false);    // default to write

        // set up panel with monitor select switches above and CS switch below
        GridBagConstraints gbc = new GridBagConstraints();

        //gbc.fill = gbc.VERTICAL;
        gbc.fill = GridBagConstraints.PAGE_START;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(15, 0, 0, 0);

//        crtDisplaySelector.setPreferredSize(new Dimension(180, 60));
        //crtControls.add(crtDisplaySelector, gbc);

        crtControls.add(displayControlButton, gbc);

        gbc.insets = new Insets(15, 50, 0, 0);
        crtControls.add(displayAccumulatorButton, gbc);

        gbc.insets = new Insets(15, 100, 0, 0);
        crtControls.add(displayStoreButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 75, 0, 0);

        crtControls.add(stopRunSwitch, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 6;
        gbc.gridheight = 2;

        gbc.insets = new Insets(0, 0, 0, 0);

        kathodeControlSwitches.setPreferredSize(new Dimension(430, 50));
        crtControls.add(kathodeControlSwitches, gbc);

        gbc.gridx = 8;
        gbc.gridy = 0;

        gbc.insets = new Insets(0, 15, 30, 0);
        crtControls.add(writeErasePanel, gbc);

        crtControls.setBackground(Color.LIGHT_GRAY);
        kathodeControlSwitches.setBackground(Color.CYAN);
        writeErasePanel.setBackground(Color.PINK);

        crtControls.setOpaque(true);
        kathodeControlSwitches.setOpaque(true);
        writeErasePanel.setOpaque(true);

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