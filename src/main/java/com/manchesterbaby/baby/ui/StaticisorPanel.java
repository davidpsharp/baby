package com.manchesterbaby.baby.ui;

import javax.swing.*;

import com.manchesterbaby.baby.ui.components.ToggleSwitch;
import com.manchesterbaby.baby.utils.AppSettings;

import static com.manchesterbaby.baby.utils.CallbackUtils.runCallback;

import java.awt.*;


public class StaticisorPanel extends JPanel {
    private Runnable onManAutoChange;
    private Runnable onLineSwitchChange;

    private final ToggleSwitch manAuto;
    private final ToggleSwitch[] lineSwitch = new ToggleSwitch[13];
    private final ToggleSwitch[] functionSwitch = new ToggleSwitch[8];

    public StaticisorPanel() {
        setLayout(new GridBagLayout());
        setOpaque(false);
        setPreferredSize(new Dimension(300, 135));

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;

        ToggleSwitch dud = new ToggleSwitch("Left unconnected.");
        dud.setSelected(true); // Ensures it starts in the correct default state
        add(dud); // Needed to align ManAuto switch with the L stat switches
        dud.setVisible(AppSettings.getInstance().isShowDisconnectedButtons());

        initialiseLineSwitches(gridBagConstraints);  // L stat switches

        gridBagConstraints.anchor = GridBagConstraints.LINE_END;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);

        initialiseFunctionSwitches(gridBagConstraints); // F stat switches

        // ManAuto switch
        manAuto = new ToggleSwitch("Select whether to execute the store or the manual instruction.");
        manAuto.addActionListener(e -> runCallback(onManAutoChange));
        gridBagConstraints.gridx = 10;
        add(manAuto, gridBagConstraints);
    }


    public void setOnManAutoChange(Runnable callback) {
        this.onManAutoChange = callback;
    }

    public void setOnLineSwitchChange(Runnable callback) {
        this.onLineSwitchChange = callback;
    }


    public ToggleSwitch getLineSwitch(int index) {
        if (index < 0 || index >= lineSwitch.length) {
            throw new IndexOutOfBoundsException("Invalid line switch index: " + index);
        }
        return lineSwitch[index];
    }

    public void setLineSwitch(int index, boolean selected) {
        if (index >= 0 && index < lineSwitch.length) {
            lineSwitch[index].setSelected(selected);
        }
    }


    public ToggleSwitch getFunctionSwitch(int index) {
        if (index < 0 || index >= functionSwitch.length) {
            throw new IndexOutOfBoundsException("Invalid function switch index: " + index);
        }
        return functionSwitch[index];
    }

    public void setFunctionSwitch(int index, boolean selected) {
        if (index >= 0 && index < functionSwitch.length) {
            functionSwitch[index].setSelected(selected);
        }
    }


    public boolean getManAuto() {
        return manAuto.isSelected();
    }

    public void setManAuto(boolean value) {
        manAuto.setSelected(value);
    }


    private void initialiseLineSwitches(GridBagConstraints gridBagConstraints) {
        JPanel linePanel = new JPanel();
        linePanel.setOpaque(false);

        for (int i = 0; i < 13; i++) {
            if (i < 5) {
                lineSwitch[i] = new ToggleSwitch("Selects the action line to be adjusted or executed.");
            } else if (i == 5 || i == 6 || i == 12) {
                lineSwitch[i] = new ToggleSwitch("Left unconnected.");
                lineSwitch[i].setSelected(true);
                lineSwitch[i].setVisible(AppSettings.getInstance().isShowDisconnectedButtons());
            } else {
                lineSwitch[i] = new ToggleSwitch(""); // No tooltip for other switches
            }

            // Disable switches between 7 and 11
            if (i > 6 && i < 12) {
                lineSwitch[i].setEnabled(false);
            }

            // Set GridBagConstraints and add to the panel
            gridBagConstraints.gridx = i + 1;
            add(lineSwitch[i], gridBagConstraints);

            // Add ActionListener
            lineSwitch[i].addActionListener(e -> runCallback(onLineSwitchChange));
        }
    }


    private void initialiseFunctionSwitches(GridBagConstraints gridBagConstraints) {
        JPanel functionPanel = new JPanel();
        functionPanel.setOpaque(false);

        for (int i = 0; i < 7; i++) {

            if (i < 3) {
                functionSwitch[i] = new ToggleSwitch("Selects the function number to be manually executed.");
            }

            if (i == 3) {
                functionSwitch[i] = new ToggleSwitch("Left unconnected.");
                functionSwitch[i].setSelected(true);
                functionSwitch[i].setVisible(AppSettings.getInstance().isShowDisconnectedButtons());
            }

            if (i > 3) {
                functionSwitch[i] = new ToggleSwitch("");
                functionSwitch[i].setEnabled(false);
                functionSwitch[i].setVisible(false);
            }

            // Set GridBagConstraints and add to the panel
            gridBagConstraints.gridx = i + 3;
            gridBagConstraints.anchor = GridBagConstraints.CENTER;
            add(functionSwitch[i], gridBagConstraints);
        }
    }


}