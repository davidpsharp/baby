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



        // staticisor
        //setBorder( BorderFactory.createLoweredBevelBorder() );

        JPanel staticisor = new JPanel();
        staticisor.setOpaque(false);

        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        c.insets = new Insets(0, 0, 0, 0);
        //staticisor.setBorder( BorderFactory.createLoweredBevelBorder() );
        staticisor.setPreferredSize(new Dimension(300, 135));
        //c.fill = GridBagConstraints.HORIZONTAL;


        staticisor.setLayout(gb);

        c.gridx = 0;
        c.gridy = 0;
        ToggleSwitch dud = new ToggleSwitch("" /*+ switchNumber*/, AbstractButton.CENTER, AbstractButton.NORTH);
        staticisor.add(dud);

        //setBackground(backgroundColor);
        //staticisor.setOpaque(false);
        //staticisor.setBorder( BorderFactory.createLoweredBevelBorder() );

        JPanel linePanel = new JPanel();
        //linePanel.setBorder( BorderFactory.createLoweredBevelBorder() );
        linePanel.setOpaque(false);//.setBackground(backgroundColor);
        lineSwitch = new ToggleSwitch[13];
        ActionListener lineSwitchActionListener = new LineSwitchActionListener();
        for (int switchNumber = 0; switchNumber < 13; switchNumber++) {
            if (switchNumber > 6 && switchNumber < 12) {
                lineSwitch[switchNumber] = new ToggleSwitch("", AbstractButton.CENTER, AbstractButton.NORTH);
                lineSwitch[switchNumber].setEnabled(false);
            } else
                lineSwitch[switchNumber] = new ToggleSwitch("" /*+ switchNumber*/, AbstractButton.CENTER, AbstractButton.NORTH);

            c.gridx = switchNumber + 1;
            staticisor.add(lineSwitch[switchNumber], c);
            lineSwitch[switchNumber].addActionListener(lineSwitchActionListener);
        }

        c.anchor = c.LINE_END;
        c.gridy = 1;
        c.gridwidth = 2;
        c.insets = new Insets(0, 0, 0, 0);

        JPanel functionPanel = new JPanel();
        functionPanel.setOpaque(false);//.setBackground(backgroundColor);
        functionSwitch = new ToggleSwitch[8];
        for (int switchNumber = 0; switchNumber < 7; switchNumber++) {
            if (switchNumber > 3) {
                functionSwitch[switchNumber] = new ToggleSwitch("", AbstractButton.CENTER, AbstractButton.NORTH);
                functionSwitch[switchNumber].setEnabled(false);
            } else
                functionSwitch[switchNumber] = new ToggleSwitch("" /*+ (13 + switchNumber)*/, AbstractButton.CENTER, AbstractButton.NORTH);
            c.gridx = 3 + switchNumber;
            c.anchor = c.CENTER;
            staticisor.add(functionSwitch[switchNumber], c);
        }

        manAuto = new ToggleSwitch("", AbstractButton.CENTER, AbstractButton.NORTH);
        manAuto.addActionListener(new ManAutoPressed());

        c.gridx = 10;
        staticisor.add(manAuto, c);
        //staticisor.add(staticisorTop, BorderLayout.NORTH);
        //staticisor.add(staticisorBottom, BorderLayout.SOUTH);

        // display controls

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
        prePulse = new ToggleSwitch("", AbstractButton.LEFT, AbstractButton.NORTH);
        prePulse.addActionListener(new PrePulsePushed());
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
        crSelect.addActionListener(new DisplaySelectPressed(CrtPanel.DisplayType.CONTROL));
        accSelect.addActionListener(new DisplaySelectPressed(CrtPanel.DisplayType.ACCUMULATOR));
        storeSelect.addActionListener(new DisplaySelectPressed(CrtPanel.DisplayType.STORE));

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

        kspSwitch.addActionListener(new KspPushed());
        klcSwitch.addMouseListener(new KlcPressed());
        kscSwitch.addMouseListener(new KscPushed());
        kacSwitch.addMouseListener(new KacPushed());
        kccSwitch.addMouseListener(new KccPushed());

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
        eraseWrite = new ToggleSwitch("", AbstractButton.CENTER, AbstractButton.NORTH);
        ;
        eraseWritePanel.add(eraseWrite, BorderLayout.CENTER);

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

        // add each panel to main panel


        add(staticisor);
        add(displayControls);

        // add tool tips
        dud.setToolTipText("Left unconnected.");

        for (int x = 0; x < 5; x++)
            lineSwitch[x].setToolTipText("Selects the action line to be adjusted or executed.");
        lineSwitch[5].setToolTipText("Left unconnected.");
        lineSwitch[6].setToolTipText("Left unconnected.");
        lineSwitch[12].setToolTipText("Left unconnected.");

        for (int x = 0; x < 3; x++)
            functionSwitch[x].setToolTipText("Selects the function number to be manually executed.");
        functionSwitch[3].setToolTipText("Left unconnected.");
        manAuto.setToolTipText("Select whether to execute the store or the manual instruction.");
        prePulse.setToolTipText("If switched down then continually executes instructions.");

        eraseWrite.setToolTipText("Selects whether typewriter erases or writes bits.");


        // set up default settings
        storeSelect.setSelected(true);    // default to display store on monitor
        eraseWrite.setSelected(false);    // default to write
        setManAuto(true);                // default to auto
        // set L stat switches to all be on
        for (int lStatSwitch = 0; lStatSwitch < 5; lStatSwitch++)
            lineSwitch[lStatSwitch].setSelected(true);
        // likewise F stat switches
        for (int fStatSwitch = 0; fStatSwitch < 3; fStatSwitch++)
            functionSwitch[fStatSwitch].setSelected(true);
        // set all unconnected L & F switches to all be down per Bob's comment that's what they do on the replica
        dud.setSelected(true);
        lineSwitch[5].setSelected(true);
        lineSwitch[6].setSelected(true);
        lineSwitch[12].setSelected(true);
        functionSwitch[3].setSelected(true);

        // add resize listener for window resize handling
        this.addComponentListener(this);
    }


    public void actionPerformed(ActionEvent e) {
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

    // return executable value of the line and function switches
    // (as would be represented if taken from store)
    public synchronized int getLineAndFunctionValue() {
        int value = getLineValue() | (getFunctionValue() << 13);
        return value;
    }

    public void setEraseWrite(boolean value) {
        eraseWrite.setSelected(value);
    }

    public boolean getEraseWrite() {
        return !eraseWrite.isSelected();
    }

    // true is pulse, false is pre
    public boolean getPrePulse() {
        return prePulse.isSelected();
    }

    // true is pulse, false is pre
    public void setPrePulse(boolean value) {
        prePulse.setSelected(value);
    }

    // true is auto, false is man
    public boolean getManAuto() {
        return manAuto.isSelected();
    }

    // true is auto, false is man
    public void setManAuto(boolean value) {
        manAuto.setSelected(value);
    }

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

    ////////////////////////////////////////////////////////////
    // START OF EVENT HANDLING INNER CLASSES

    /// /////////////////////////////////////////////////////////

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

    // change selected display
    class DisplaySelectPressed implements ActionListener {
        DisplayType displayValue;

        public DisplaySelectPressed(DisplayType value) {
            displayValue = value;
        }

        public void actionPerformed(ActionEvent e) {
            crtPanel.setCrtDisplay(displayValue);
        }
    }

    // clear store
    class KscPushed implements MouseListener {
        public void mousePressed(MouseEvent e) {
            if (Baby.running) {
                control.setKscPressed(true);
            } else {
                store.reset();
                redrawCrtPanel();
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

    class LineSwitchActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            updateActionLine();
        }
    }

    class ManAutoPressed implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            updateActionLine();
        }
    }

    // NoOp
    public void componentHidden(ComponentEvent e) {
    }

    // NoOp
    public void componentMoved(ComponentEvent e) {
    }

    // handle panel resize
    public void componentResized(ComponentEvent e) {
        //System.out.println(e.getComponent().getClass().getName() + ", width:" + e.getComponent().getWidth() + " height:" + e.getComponent().getHeight()); 
    }

    // NoOp
    public void componentShown(ComponentEvent e) {
    }

}