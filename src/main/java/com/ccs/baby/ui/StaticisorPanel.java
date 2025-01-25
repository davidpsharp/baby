package com.ccs.baby.ui;

import javax.swing.*;
import java.awt.*;

import com.ccs.baby.ui.components.ToggleSwitch;
import static com.ccs.baby.utils.CallbackUtils.runCallback;


public class StaticisorPanel extends JPanel {
    private Runnable onManAutoChange;
    private Runnable onLineSwitchChange;

    private final ToggleSwitch manAuto;
    private final ToggleSwitch[] lineSwitch = new ToggleSwitch[13];
    private final ToggleSwitch[] functionSwitch = new ToggleSwitch[8];

    public StaticisorPanel() {

        setLayout(new GridBagLayout());
        setOpaque(false);
        setPreferredSize(new Dimension(300, 0));

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;

        ToggleSwitch dud = new ToggleSwitch("Left unconnected.");
        add(dud); // Needed to align ManAuto switch with the L stat switches

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

        setManAuto(true); // Default to auto
        setLineSwitches(true); // set L stat switches to all be on
        setFunctionSwitches(true); // likewise F stat switches

        //  set all unconnected L & F switches to all be down per Bob's comment that's what they do on the replica
        dud.setSelected(true);
        lineSwitch[5].setSelected(true);
        lineSwitch[6].setSelected(true);
        lineSwitch[12].setSelected(true);
        functionSwitch[3].setSelected(true);
    }


    public void setOnManAutoChange(Runnable callback) {
        this.onManAutoChange = callback;
    }

    public void setOnLineSwitchChange(Runnable callback) {
        this.onLineSwitchChange = callback;
    }



    private void initialiseLineSwitches(GridBagConstraints gridBagConstraints) {

        // SwitchLin Panel
        JPanel linePanel = new JPanel();
        linePanel.setOpaque(false);


        for (int i = 0; i < 13; i++) {
            if (i < 5) {
                lineSwitch[i] = new ToggleSwitch("Selects the action line to be adjusted or executed.");
            } else if (i == 5 || i == 6 || i == 12) {
                lineSwitch[i] = new ToggleSwitch("Left unconnected.");
                lineSwitch[i].setSelected(true);
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
        // Function Panel
                JPanel functionPanel = new JPanel();
                functionPanel.setOpaque(false);
        for (int i = 0; i < 7; i++) {

            if (i < 3) {
                functionSwitch[i] = new ToggleSwitch("Selects the function number to be manually executed.");
            }

            if (i == 3) {
                functionSwitch[i] = new ToggleSwitch("Left unconnected.");
                functionSwitch[i].setSelected(true);
            }

            if (i > 3) {
                functionSwitch[i] = new ToggleSwitch("Blah");
                functionSwitch[i].setEnabled(false);
                functionSwitch[i].setVisible(false);
            }

            // Set GridBagConstraints and add to the panel
            gridBagConstraints.gridx = i + 3;
            gridBagConstraints.anchor = GridBagConstraints.CENTER;
            add(functionSwitch[i], gridBagConstraints);
        }
    }

    // set L stat switches to all be on
    public void setLineSwitches(boolean value) {
        for (int i = 0; i < 5; i++) {
            lineSwitch[i].setSelected(value);
        }
    }

    // likewise F stat switches
    public void setFunctionSwitches(boolean value) {
        for (int i = 0; i < 5; i++) {
            functionSwitch[i].setSelected(value);
        }
    }

    /**
     * Get the value of the manAuto switch
     * TRUE = AUTO
     * FALSE = MANUAL
     *
     * @return boolean value of the manAuto switch
     */
    public boolean getManAuto() {
        return manAuto.isSelected();
    }

    /**
     * Set the value of the manAuto switch
     * TRUE = AUTO
     * FALSE = MANUAL
     *
     * @param value boolean value of the manAuto switch
     */
    public void setManAuto(boolean value) {
        manAuto.setSelected(value);
    }

    /**
     * Iterate through all line switches and return the value of the line selected
     *
     * @return int value of line selected
     */
    public int getLineValue() {
        int result = 0;
        for (int i = 0; i < 5; i++) {
            if (lineSwitch[i].isSelected()) result += (1 << i);
        }
        return result;
    }

    /**
     * Iterate through all function switches and return the value of the function selected
     *
     * @return int value of function selected
     */
    public int getFunctionValue() {
        int result = 0;
        for (int i = 0; i < 3; i++) {
            if (functionSwitch[i].isSelected()) result += (1 << i);
        }
        return result;
    }

}