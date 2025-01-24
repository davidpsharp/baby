package com.ccs.baby.ui;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;

import com.ccs.baby.ui.components.InterlockingPushButton;
import com.ccs.baby.ui.components.KeySwitch;
import com.ccs.baby.ui.components.PushButton;
import com.ccs.baby.ui.components.ToggleSwitch;
import com.ccs.baby.core.Baby;
import com.ccs.baby.core.Control;
import com.ccs.baby.core.Store;
import com.ccs.baby.disassembler.Disassembler;
import com.ccs.baby.ui.CrtPanel.DisplayType;

public class SwitchPanel extends JPanel implements ActionListener, ComponentListener {

    // background hardware
    private Store store;
    private Control control;
    public CrtPanel crtPanel;
    public Baby baby;
    private Disassembler disassembler;

    // buttons
    public PushButton[] numberKey;
    public ToggleSwitch[] lineSwitch;
    public ToggleSwitch[] functionSwitch;
    ToggleSwitch manAuto;
    public ToggleSwitch prePulse;

    public InterlockingPushButton crSelect;
    public InterlockingPushButton accSelect;
    public InterlockingPushButton storeSelect;
    ButtonGroup monitorSelectGroup;

    KeySwitch klcSwitch;
    KeySwitch kscSwitch;
    KeySwitch kacSwitch;
    KeySwitch kccSwitch;

    public KeySwitch kspSwitch;
    ToggleSwitch eraseWrite;

    public static Color backgroundColor = new Color(206, 205, 201);

    public SwitchPanel(Store aStore, Control aControl, CrtPanel aCrtPanel, Baby aBaby, Disassembler aDisassembler) {
        this.store = aStore;
        this.control = aControl;
        this.crtPanel = aCrtPanel;
        this.baby = aBaby;
        this.disassembler = aDisassembler;

        // set up window
        //setTitle("Switch panel");
        //this.setSize(300, 445);

        //Container contentPane = getContentPane();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));


        add(new TypewriterPanel(store, control, this));



        add(staticisor);
        add(displayControls);


        // add resize listener for window resize handling
        this.addComponentListener(this);
    }

    /**
     * Set the display type of the CRT panel.
     */
    public void redrawCrtPanel() {
        crtPanel.render();
        crtPanel.repaint();
    }

    public void updateActionLine() {
        // if automatic
        if (getManAuto()) {
            crtPanel.setActionLine(control.getLineNumber(control.getControlInstruction()));
        }
        // else manual
        else {
            crtPanel.setActionLine(getLineValue());
        }
    }

    public void singleStep() {
        setManAuto(true);
        // set to write
        setEraseWrite(true);
        // set L stat switches to all be on
        for (int lStatSwitch = 0; lStatSwitch < 5; lStatSwitch++)
            lineSwitch[lStatSwitch].setSelected(true);
        // likewise F stat switches
        for (int fStatSwitch = 0; fStatSwitch < 3; fStatSwitch++)
            functionSwitch[fStatSwitch].setSelected(true);

        kspSwitch.doClick();
    }





}