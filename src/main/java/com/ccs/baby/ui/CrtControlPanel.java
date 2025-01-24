package com.ccs.baby.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.ccs.baby.ui.components.*;
import com.ccs.baby.core.*;
import com.ccs.baby.ui.components.InterlockingPushButton;
import com.ccs.baby.ui.components.KeySwitch;
import com.ccs.baby.ui.components.ToggleSwitch;

public class CrtControlPanel extends JPanel {
    public CrtControlPanel(Store store, Control control, CrtPanel crtPanel, Baby baby, Disassembler disassembler) {
        JPanel displayControls = new JPanel();
        displayControls.setOpaque(false);
        displayControls.setPreferredSize(new Dimension(0, 110));
        //.setBackground(backgroundColor);
        //displayControls.setBorder( BorderFactory.createLoweredBevelBorder() );
        displayControls.setLayout(new GridBagLayout());
        GridBagConstraints con = new GridBagConstraints();


        // note, CS switch == prePulse switch
        JPanel csSwitchPanel = new JPanel();
        csSwitchPanel.setOpaque(false);//.setBackground(backgroundColor);
        //csSwitchPanel.setBorder( BorderFactory.createLoweredBevelBorder() );
        prePulse = new ToggleSwitch("If switched down then continually executes instructions.");
        prePulse.addActionListener(new SwitchPanel.PrePulsePushed());
        csSwitchPanel.add(prePulse);

        JPanel monitorSelector = new JPanel();
        monitorSelector.setOpaque(false);//.setBackground(backgroundColor);
        //monitorSelector.setBorder( BorderFactory.createLoweredBevelBorder() );


        crSelect = new InterlockingPushButton("Displays the control on the monitor.");
        accSelect = new InterlockingPushButton("Displays the accumulator on the monitor.");
        storeSelect = new InterlockingPushButton("Displays the store on the monitor.");

        monitorSelectGroup = new ButtonGroup();
        monitorSelectGroup.add(crSelect);
        monitorSelectGroup.add(accSelect);
        monitorSelectGroup.add(storeSelect);
        monitorSelector.add(crSelect);
        monitorSelector.add(accSelect);
        monitorSelector.add(storeSelect);
        crSelect.addActionListener(new SwitchPanel.DisplaySelectPressed(CrtPanel.DisplayType.CONTROL));
        accSelect.addActionListener(new SwitchPanel.DisplaySelectPressed(CrtPanel.DisplayType.ACCUMULATOR));
        storeSelect.addActionListener(new SwitchPanel.DisplaySelectPressed(CrtPanel.DisplayType.STORE));


        // storageClearingKeys
        // to be added to displayControls panel

        JPanel storageClearingKeys = new JPanel();
        storageClearingKeys.setOpaque(false);//.setBackground(Color.black);
        //storageClearingKeys.setBorder( BorderFactory.createLoweredBevelBorder() );
        storageClearingKeys.setLayout(new GridBagLayout());


        kspSwitch = new KeySwitch("Executes a single instruction.", KeySwitch.KeyColour.GREY);    // KC == KSP
        klcSwitch = new KeySwitch("Clears the current action line of the store.", KeySwitch.KeyColour.WHITE);
        kscSwitch = new KeySwitch("Clears the entire store.", KeySwitch.KeyColour.WHITE);
        kacSwitch = new KeySwitch("Clears the accumulator", KeySwitch.KeyColour.WHITE);
        kccSwitch = new KeySwitch("Clears the CI, PI and Accumulator.", KeySwitch.KeyColour.GREY);

        kspSwitch.addActionListener(new SwitchPanel.KspPushed());
        klcSwitch.addMouseListener(new SwitchPanel.KlcPressed());
        kscSwitch.addMouseListener(new SwitchPanel.KscPushed());
        kacSwitch.addMouseListener(new SwitchPanel.KacPushed());
        kccSwitch.addMouseListener(new SwitchPanel.KccPushed());

        // These buttons are not connected as per the original hardware
        KeySwitch kbcSwitch = new KeySwitch("Not connected", KeySwitch.KeyColour.GREY);
        KeySwitch kecSwitch = new KeySwitch("Not connected", KeySwitch.KeyColour.GREY);
        KeySwitch kmcSwitch = new KeySwitch("Not connected", KeySwitch.KeyColour.GREY);


        GridBagConstraints sc = new GridBagConstraints();
        sc.anchor = sc.WEST;
        sc.gridx = 0;
        sc.gridy = 0;
        sc.insets = new Insets(0, 42, 0, 223);
        storageClearingKeys.add(kspSwitch, sc);
        sc.insets = new Insets(0, 115, 0, 0);
        storageClearingKeys.add(klcSwitch, sc);
        sc.insets = new Insets(0, 152, 0, 0);
        storageClearingKeys.add(kscSwitch, sc);
        sc.insets = new Insets(0, 187, 0, 0);
        storageClearingKeys.add(kacSwitch, sc);
        sc.insets = new Insets(0, 226, 0, 0);
        storageClearingKeys.add(kbcSwitch, sc);
        sc.insets = new Insets(0, 263, 0, 0);
        storageClearingKeys.add(kccSwitch, sc);
        sc.insets = new Insets(0, 296, 0, 0);
        storageClearingKeys.add(kecSwitch, sc);
        sc.insets = new Insets(0, 334, 0, 0);
        storageClearingKeys.add(kmcSwitch, sc);

        JPanel eraseWritePanel = new JPanel();
        eraseWritePanel.setOpaque(false);
        eraseWrite = new ToggleSwitch("Selects whether typewriter erases or writes bits.");
        eraseWritePanel.add(eraseWrite, BorderLayout.CENTER);

        // set up default settings
        storeSelect.setSelected(true);    // default to display store on monitor
        eraseWrite.setSelected(false);    // default to write

        // set up panel with monitor select switches above and CS switch below

        //con.fill = con.VERTICAL;
        con.fill = con.PAGE_START;
        con.gridx = 0;
        con.gridy = 0;
        con.gridwidth = 1;
        con.insets = new Insets(15, 0, 0, 0);

        //monitorSelector.setPreferredSize(new Dimension(180, 60));
        //displayControls.add(monitorSelector, con);

        displayControls.add(crSelect, con);

        con.insets = new Insets(15, 50, 0, 0);
        displayControls.add(accSelect, con);

        con.insets = new Insets(15, 100, 0, 0);
        displayControls.add(storeSelect, con);

        con.gridx = 0;
        con.gridy = 1;
        con.gridwidth = 2;
        con.insets = new Insets(0, 75, 0, 0);

        displayControls.add(prePulse, con);

        con.gridx = 1;
        con.gridy = 1;
        con.gridwidth = 6;
        con.gridheight = 2;

        con.insets = new Insets(0, 0, 0, 0);

        storageClearingKeys.setPreferredSize(new Dimension(430, 50));
        displayControls.add(storageClearingKeys, con);

        con.gridx = 8;
        con.gridy = 0;

        con.insets = new Insets(0, 15, 30, 0);
        displayControls.add(eraseWritePanel, con);

    }

    // true is pulse, false is pre
    public boolean getPrePulse() {
        return prePulse.isSelected();
    }

    // true is pulse, false is pre
    public void setPrePulse(boolean value) {
        prePulse.setSelected(value);
    }

    // if pulse execute instructions repeatedly either from store or from line and function switches
    class PrePulsePushed implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // are we now on pre or pulse?
            // on pulse
            if (getPrePulse()) {
                // turn off the highlighted action line
                crtPanel.setActionLine(-1);
                // whether to take instructions from store or line and function switches
                // is handled in the actual execution of the animation
                baby.startAnimation();
            }
            // on pre
            else {
                baby.stopAnimation();
                updateActionLine();
            }
        }
    }

    public void setEraseWrite(boolean value) {
        eraseWrite.setSelected(value);
    }

    public boolean getEraseWrite() {
        return !eraseWrite.isSelected();
    }

    // clear store
    class KscPushed implements MouseListener {
        public void mousePressed(MouseEvent e) {
            if (Baby.running) {
                control.setKscPressed(true);
            } else {
                store.reset();
                switchPanel.redrawCrtPanel();
            }
        }

        public void mouseReleased(MouseEvent e) {
            control.setKscPressed(false);
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }
    }

    // clear CI,PI and ACC
    class KccPushed implements MouseListener {
        public void mousePressed(MouseEvent e) {
            if (Baby.running) {
                control.setKccPressed(true);
            } else {
                control.setControlInstruction(0);
                control.setPresentInstruction(0);
                control.setAccumulator(0);
                updateActionLine();
                redrawCrtPanel();
            }
        }

        public void mouseReleased(MouseEvent e) {
            control.setKccPressed(false);
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }
    }

    // execute a single instruction (either from store or from line and function switches)
    class KspPushed implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // clear stop flag if set
            if (control.getStopFlag())
                control.setStopFlag(false);

            // true is Auto, false is Man
            if (getManAuto()) {
                // perform single instruction from store
                control.executeAutomatic();
                updateActionLine();
                crtPanel.render();
                crtPanel.repaint();

                disassembler.updateDisassemblerOnStep();
            } else {
                // perform single instruction from line and function switches
                control.executeManual();
                updateActionLine();
                crtPanel.render();
                crtPanel.repaint();
            }
        }
    }



    class KlcPressed implements MouseListener {
        public void mousePressed(MouseEvent e) {
            if (Baby.running) {
                control.setKlcPressed(true);
            } else {
                store.setLine(getLineValue(), 0);
                crtPanel.render();
                crtPanel.repaint();
            }
        }

        public void mouseReleased(MouseEvent e) {
            control.setKlcPressed(false);
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }
    }

    class KacPushed implements MouseListener {
        public void mousePressed(MouseEvent e) {
            if (!Baby.running) {
                control.setAccumulator(0);
                crtPanel.render();
                crtPanel.repaint();
            } else {
                control.setKacPressed(true);
            }
        }

        public void mouseReleased(MouseEvent e) {
            control.setKacPressed(false);
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }
    }


    // change selected display
    class DisplaySelectPressed implements ActionListener {
        CrtPanel.DisplayType displayValue;

        public DisplaySelectPressed(CrtPanel.DisplayType value) {
            displayValue = value;
        }

        public void actionPerformed(ActionEvent e) {
            crtPanel.setCrtDisplay(displayValue);
        }
    }

}