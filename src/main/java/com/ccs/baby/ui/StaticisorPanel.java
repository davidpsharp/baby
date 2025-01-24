package com.ccs.baby.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.ccs.baby.ui.components.ToggleSwitch;
import com.ccs.baby.core.Control;


public class StaticisorPanel extends JPanel {

    private ToggleSwitch manAuto;

    public StaticisorPanel(Control control, Runnable onSwitchChange) {

        GridBagLayout gb = new GridBagLayout();

        setLayout(gb);
        setOpaque(false);
        setPreferredSize(new Dimension(300, 135));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.gridx = 0;
        gbc.gridy = 1;

        gbc.anchor = gbc.LINE_END;
        gbc.gridwidth = 2;


        // L stat switches
        ToggleSwitch dud = new ToggleSwitch("Left unconnected.");
        add(dud);

        // set all unconnected L & F switches to all be down per Bob's comment that's what they do on the replica
        dud.setSelected(true);

        // SwitchLin Panel
        JPanel linePanel = new JPanel();
        linePanel.setOpaque(false);
        ToggleSwitch[] lineSwitch = new ToggleSwitch[13];
        ActionListener lineSwitchActionListener = new LineSwitchActionListener();

        for (int switchNumber = 0; switchNumber < 13; switchNumber++) {

            // Assign tooltips based on the switch number
            if (switchNumber < 5) {
                lineSwitch[switchNumber] = new ToggleSwitch("Selects the action line to be adjusted or executed.");
            } else if (switchNumber == 5 || switchNumber == 6 || switchNumber == 12) {
                lineSwitch[switchNumber] = new ToggleSwitch("Left unconnected.");
                lineSwitch[switchNumber].setSelected(true);
            } else {
                lineSwitch[switchNumber] = new ToggleSwitch(""); // No tooltip for other switches
            }

            // Disable switches between 7 and 11
            if (switchNumber > 6 && switchNumber < 12) {
                lineSwitch[switchNumber].setEnabled(false);
            }

            // Set GridBagConstraints and add to the panel
            gbc.gridx = switchNumber + 1;
            add(lineSwitch[switchNumber], gbc);

            // Add ActionListener
            lineSwitch[switchNumber].addActionListener(lineSwitchActionListener);
        }

        // Function Panel
        JPanel functionPanel = new JPanel();
        functionPanel.setOpaque(false);
        ToggleSwitch[] functionSwitch = new ToggleSwitch[8];
        for (int switchNumber = 0; switchNumber < 7; switchNumber++) {

            if (switchNumber < 3) {
                functionSwitch[switchNumber] = new ToggleSwitch("Selects the function number to be manually executed.");
            } else if (switchNumber == 3) {
                functionSwitch[switchNumber] = new ToggleSwitch("Left unconnected.");
                functionSwitch[switchNumber].setSelected(true);
            } else {
                functionSwitch[switchNumber] = new ToggleSwitch("");
                functionSwitch[switchNumber].setEnabled(false);
            }

            // Set GridBagConstraints and add to the panel
            gbc.gridx = 3 + switchNumber;
            gbc.anchor = gbc.CENTER;
            add(functionSwitch[switchNumber], gbc);
        }


        manAuto = new ToggleSwitch("Select whether to execute the store or the manual instruction.");
        manAuto.addActionListener(new ManAutoPressed());

        add(manAuto, gbc);
        gbc.gridx = 10;

        // Default to auto
        setManAuto(true);

        // set L stat switches to all be on
        for (int lStatSwitch = 0; lStatSwitch < 5; lStatSwitch++)
            lineSwitch[lStatSwitch].setSelected(true);
        // likewise F stat switches
        for (int fStatSwitch = 0; fStatSwitch < 3; fStatSwitch++)
            functionSwitch[fStatSwitch].setSelected(true);

    }



    // true is auto, false is man
    public boolean getManAuto() {
        return manAuto.isSelected();
    }

    // true is auto, false is man
    public void setManAuto(boolean value) {
        manAuto.setSelected(value);
    }


    private class LineSwitchActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Call the method to update the action line
            updateActionLine();
        }
    }

    class ManAutoPressed implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            updateActionLine();
        }
    }

    // return executable value of the line and function switches
    // (as would be represented if taken from store)
    public synchronized int getLineAndFunctionValue() {
        int value = getLineValue() | (getFunctionValue() << 13);
        return value;
    }

    // go through all line switches and return int of line selected
    public int getLineValue() {
        int result = 0;
        for (int switchNumber = 0; switchNumber < 5; switchNumber++) {
            if (lineSwitch[switchNumber].isSelected())
                result += (1 << switchNumber);
        }
        return result;
    }

    // go through all function switches and return int of function selected
    public int getFunctionValue() {
        int result = 0;
        for (int switchNumber = 0; switchNumber < 3; switchNumber++) {
            if (functionSwitch[switchNumber].isSelected())
                result += (1 << switchNumber);
        }
        return result;
    }

}