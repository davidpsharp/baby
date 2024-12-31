package com.ccs.baby.ui;



import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.ccs.baby.core.Store;
import com.ccs.baby.core.Control;
import com.ccs.baby.core.Baby;

import com.ccs.baby.components.InterlockingPushButton;
import com.ccs.baby.components.PushButton;
import com.ccs.baby.components.KeySwitch;
import com.ccs.baby.components.ToggleSwitch;
import com.ccs.baby.ui.CrtPanel.DisplayType;

public class SwitchPanel extends JPanel implements ActionListener, ComponentListener
{
	
	// background hardware
	private Store store;
	private Control control;
	public CrtPanel crtPanel;
	public Baby baby;
	
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
	
	public SwitchPanel(Store aStore, Control aControl, CrtPanel aCrtPanel, Baby aBaby)
	{
		store = aStore;
		control = aControl;
		crtPanel = aCrtPanel;
		baby = aBaby;
		
		// set up window
		//setTitle("Switch panel");
		//this.setSize(300, 445);
		
		//Container contentPane = getContentPane();
	    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS) );
		
		
		//setBackground(backgroundColor);
		
		
		// typewriter
		
		JPanel typeWriter = new JPanel();
		typeWriter.setOpaque(false);//.setBackground(backgroundColor);
		typeWriter.setLayout(new GridBagLayout() );			// 8 across. 5 down
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.CENTER;
		//gbc.weightx = 2;
		//typeWriter.setBorder( BorderFactory.createLoweredBevelBorder() );
		
		numberKey = new PushButton[40];
		// create 40 keys
		for(int keyNumber=0; keyNumber<40; keyNumber++)
		{
			numberKey[keyNumber] = new PushButton("", AbstractButton.CENTER);
			// add mouse listeners to keys 0-31
			if(keyNumber < 32)
				numberKey[keyNumber].addMouseListener(new TypeWriterPushed(keyNumber, control, store, this));
		}
		
		// add keys in correct order for display with this layout
		for (int i = 0; i < 5; i++) {
		  for (int j = 0; j < 40; j += 5) {
	        gbc.gridx = j/5;
            gbc.gridy = i;
			typeWriter.add(numberKey[i+j], gbc);
          }
          gbc.anchor = gbc.LINE_END;
        }
        
        //typeWriter.setBorder( BorderFactory.createLoweredBevelBorder() );
        typeWriter.setPreferredSize(new Dimension(0,270));
        
        /*
		typeWriter.add(numberKey[0]);
		typeWriter.add(numberKey[5]);
		typeWriter.add(numberKey[10]);
		typeWriter.add(numberKey[15]);
		typeWriter.add(numberKey[20]);
		typeWriter.add(numberKey[25]);
		typeWriter.add(numberKey[30]);
		typeWriter.add(numberKey[35]);
		
		typeWriter.add(numberKey[1]);
		typeWriter.add(numberKey[6]);
		typeWriter.add(numberKey[11]);
		typeWriter.add(numberKey[16]);
		typeWriter.add(numberKey[21]);
		typeWriter.add(numberKey[26]);
		typeWriter.add(numberKey[31]);
		typeWriter.add(numberKey[36]);
		
		typeWriter.add(numberKey[2]);
		typeWriter.add(numberKey[7]);
		typeWriter.add(numberKey[12]);
		typeWriter.add(numberKey[17]);
		typeWriter.add(numberKey[22]);
		typeWriter.add(numberKey[27]);
		typeWriter.add(numberKey[32]);
		typeWriter.add(numberKey[37]);
		
		typeWriter.add(numberKey[3]);
		typeWriter.add(numberKey[8]);
		typeWriter.add(numberKey[13]);
		typeWriter.add(numberKey[18]);
		typeWriter.add(numberKey[23]);
		typeWriter.add(numberKey[28]);
		typeWriter.add(numberKey[33]);
		typeWriter.add(numberKey[38]);
		
		typeWriter.add(numberKey[4]);
		typeWriter.add(numberKey[9]);
		typeWriter.add(numberKey[14]);
		typeWriter.add(numberKey[19]);
		typeWriter.add(numberKey[24]);
		typeWriter.add(numberKey[29]);
		typeWriter.add(numberKey[34]);
		typeWriter.add(numberKey[39]);
		*/
		
		// staticisor
		//setBorder( BorderFactory.createLoweredBevelBorder() );
		
		JPanel staticisor = new JPanel();
		staticisor.setOpaque(false);
	
	    GridBagLayout gb = new GridBagLayout();
	    GridBagConstraints c = new GridBagConstraints();
	    
	    c.insets = new Insets(0,0,0,0);
	    //staticisor.setBorder( BorderFactory.createLoweredBevelBorder() );
	    staticisor.setPreferredSize(new Dimension(300,135));
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
		for(int switchNumber=0; switchNumber<13; switchNumber++)
		{
			if (switchNumber >6 && switchNumber < 12) {
			  lineSwitch[switchNumber] = new ToggleSwitch("", AbstractButton.CENTER, AbstractButton.NORTH);
			  lineSwitch[switchNumber].setEnabled(false);
			}
			else 
			  lineSwitch[switchNumber] = new ToggleSwitch("" /*+ switchNumber*/, AbstractButton.CENTER, AbstractButton.NORTH);
			  
			c.gridx = switchNumber+1;
			staticisor.add(lineSwitch[switchNumber], c);
			lineSwitch[switchNumber].addActionListener(lineSwitchActionListener);
		}
		
	    c.anchor = c.LINE_END;
	    c.gridy = 1;
        c.gridwidth = 2;
	    c.insets = new Insets(0,0,0,0);
				
		JPanel functionPanel = new JPanel();
		functionPanel.setOpaque(false);//.setBackground(backgroundColor);
		functionSwitch = new ToggleSwitch[8];
		for(int switchNumber=0; switchNumber<7; switchNumber++)
		{
			if (switchNumber >3) {
			  functionSwitch[switchNumber] = new ToggleSwitch("", AbstractButton.CENTER, AbstractButton.NORTH);
			  functionSwitch[switchNumber].setEnabled(false);
			}
			else
			  functionSwitch[switchNumber] = new ToggleSwitch("" /*+ (13 + switchNumber)*/, AbstractButton.CENTER, AbstractButton.NORTH);
			c.gridx = 3+switchNumber;
            c.anchor = c.CENTER;
			staticisor.add(functionSwitch[switchNumber], c);
		}
		
		manAuto = new ToggleSwitch("", AbstractButton.CENTER, AbstractButton.NORTH);
		manAuto.addActionListener( new ManAutoPressed() );
		
		c.gridx = 10;
		staticisor.add(manAuto, c);
		//staticisor.add(staticisorTop, BorderLayout.NORTH);
		//staticisor.add(staticisorBottom, BorderLayout.SOUTH);
		
		// display controls
		
		JPanel displayControls = new JPanel();
		displayControls.setOpaque(false);
		displayControls.setPreferredSize(new Dimension(0,110));
		//.setBackground(backgroundColor);
		//displayControls.setBorder( BorderFactory.createLoweredBevelBorder() );
		displayControls.setLayout(new GridBagLayout());
		GridBagConstraints con = new GridBagConstraints();
		
		
		// note, CS switch == prePulse switch
		JPanel csSwitchPanel = new JPanel();
		csSwitchPanel.setOpaque(false);//.setBackground(backgroundColor);
		//csSwitchPanel.setBorder( BorderFactory.createLoweredBevelBorder() );
		prePulse = new ToggleSwitch("", AbstractButton.LEFT, AbstractButton.NORTH);
		prePulse.addActionListener( new PrePulsePushed() );
		csSwitchPanel.add(prePulse);
		
		JPanel monitorSelector = new JPanel();
		monitorSelector.setOpaque(false);//.setBackground(backgroundColor);
		//monitorSelector.setBorder( BorderFactory.createLoweredBevelBorder() );
		
		
		crSelect = new InterlockingPushButton("", AbstractButton.NORTH);
		accSelect = new InterlockingPushButton("", AbstractButton.NORTH);
		storeSelect = new InterlockingPushButton("", AbstractButton.NORTH);
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
		storageClearingKeys.setLayout( new GridBagLayout());
		
		
		kspSwitch = new KeySwitch("", "/images/greydown.png", "/images/greyup.png");		// KC == KSP
		kspSwitch.addActionListener(new KspPushed() );
		klcSwitch = new KeySwitch("", "/images/whitedown.png", "/images/whiteup.png");
		klcSwitch.addMouseListener(new KlcPressed() );
		kscSwitch = new KeySwitch("", "/images/whitedown.png", "/images/whiteup.png");
		kscSwitch.addMouseListener(new KscPushed() );
		kacSwitch = new KeySwitch("", "/images/whitedown.png", "/images/whiteup.png");
		kacSwitch.addMouseListener(new KacPushed() );
		kccSwitch = new KeySwitch("", "/images/greydown.png", "/images/greyup.png");
		kccSwitch.addMouseListener(new KccPushed() );
		
		
		KeySwitch kbcSwitch = new KeySwitch("", "/images/greydown.png", "/images/greyup.png");		// unconnected buttons
		KeySwitch kecSwitch = new KeySwitch("", "/images/greydown.png", "/images/greyup.png");
		KeySwitch kmcSwitch = new KeySwitch("", "/images/greydown.png", "/images/greyup.png");
		
		GridBagConstraints sc = new GridBagConstraints();
		sc.anchor = sc.WEST;
		sc.gridx = 0;
		sc.gridy = 0;
	    sc.insets = new Insets(0,42,0,223);
		storageClearingKeys.add(kspSwitch, sc);
		sc.insets = new Insets(0,115,0,0);
		storageClearingKeys.add(klcSwitch, sc);
		sc.insets = new Insets(0,152,0,0);
		storageClearingKeys.add(kscSwitch, sc);
		sc.insets = new Insets(0,187,0,0);
		storageClearingKeys.add(kacSwitch, sc);
		sc.insets = new Insets(0,226,0,0);
		storageClearingKeys.add(kbcSwitch, sc);
		sc.insets = new Insets(0,263,0,0);
		storageClearingKeys.add(kccSwitch, sc);
		sc.insets = new Insets(0,296,0,0);
		storageClearingKeys.add(kecSwitch, sc);
		sc.insets = new Insets(0,334,0,0);
		storageClearingKeys.add(kmcSwitch, sc);
		
		JPanel eraseWritePanel = new JPanel();
		eraseWritePanel.setOpaque(false);
		eraseWrite = new ToggleSwitch("", AbstractButton.CENTER, AbstractButton.NORTH);;
		eraseWritePanel.add(eraseWrite, BorderLayout.CENTER);
		
		// set up panel with monitor select switches above and CS switch below
		
		//con.fill = con.VERTICAL;
		con.fill = con.PAGE_START;
		con.gridx = 0;
		con.gridy = 0;
		con.gridwidth = 1;
		con.insets = new Insets(15,0,0,0);
		
		//monitorSelector.setPreferredSize(new Dimension(180, 60));
		//displayControls.add(monitorSelector, con);
		
		displayControls.add(crSelect, con);
		
		con.insets = new Insets(15,50,0,0);
		displayControls.add(accSelect, con);
		
		con.insets = new Insets(15,100,0,0);
		displayControls.add(storeSelect, con);
		
		con.gridx = 0;
		con.gridy = 1;
		con.gridwidth = 2;
		con.insets = new Insets(0,75,0,0);
		
		displayControls.add(prePulse, con);
		
		con.gridx = 1;
		con.gridy = 1;
		con.gridwidth = 6;
		con.gridheight = 2;

		con.insets = new Insets(0,0,0,0);
		
		storageClearingKeys.setPreferredSize(new Dimension(430, 50));
		displayControls.add(storageClearingKeys, con);
		
		con.gridx = 8;
		con.gridy = 0;
		
		con.insets = new Insets(0,15,30,0);
		displayControls.add(eraseWritePanel, con);
						
		// add each panel to main panel
		
		add(typeWriter);
		add(staticisor);
		add(displayControls);
		
		// add tool tips
		dud.setToolTipText("Left unconnected.");
		for(int keyNumber = 0; keyNumber < 32; keyNumber++)
			numberKey[keyNumber].setToolTipText("Typewriter adjusts bit " + keyNumber + " of the action line.");
		for(int keyNumber = 32; keyNumber < 40; keyNumber++)
			numberKey[keyNumber].setToolTipText("Left unconnected.");
		for(int x=0; x<5; x++)
			lineSwitch[x].setToolTipText("Selects the action line to be adjusted or executed.");
		lineSwitch[5].setToolTipText("Left unconnected.");
		lineSwitch[6].setToolTipText("Left unconnected.");
		lineSwitch[12].setToolTipText("Left unconnected.");
		
		for(int x=0; x<3; x++)
			functionSwitch[x].setToolTipText("Selects the function number to be manually executed.");
		functionSwitch[3].setToolTipText("Left unconnected.");
		manAuto.setToolTipText("Select whether to execute the store or the manual instruction.");
		prePulse.setToolTipText("If switched down then continually executes instructions.");
		klcSwitch.setToolTipText("Clears the current action line of the store.");
		kscSwitch.setToolTipText("Clears the entire store.");
		kccSwitch.setToolTipText("Clears the CI, PI and Accumulator.");
		kspSwitch.setToolTipText("Executes a single instruction.");
		eraseWrite.setToolTipText("Selects whether typewriter erases or writes bits.");
		accSelect.setToolTipText("Displays the accumulator on the monitor.");
		crSelect.setToolTipText("Displays the control on the monitor");
		storeSelect.setToolTipText("Displays the store on the monitor.");
		kacSwitch.setToolTipText("Clears the accumulator");
		
		kbcSwitch.setToolTipText("Not connected");
		kecSwitch.setToolTipText("Not connected");
		kmcSwitch.setToolTipText("Not connected");
		
		// set up default settings
		storeSelect.setSelected(true);	// default to display store on monitor
		eraseWrite.setSelected(false);	// default to write
		setManAuto(true);				// default to auto
		// set L stat switches to all be on
		for(int lStatSwitch=0; lStatSwitch<5; lStatSwitch++)
			lineSwitch[lStatSwitch].setSelected(true);
		// likewise F stat switches
		for(int fStatSwitch=0; fStatSwitch<3; fStatSwitch++)
			functionSwitch[fStatSwitch].setSelected(true);

		// add resize listener for window resize handling
        this.addComponentListener(this);
	}
	
	
	
	
	public void actionPerformed(ActionEvent e)
	{
	}
	
	// go through all line switches and return int of line selected
	public int getLineValue()
	{
		int result = 0;
		for(int switchNumber = 0; switchNumber<5; switchNumber++)
		{
			if( lineSwitch[switchNumber].isSelected() )
				result += (1<<switchNumber);
		}
		return result;
	}
	
	// go through all function switches and return int of function selected
	public int getFunctionValue()
	{
		int result = 0;
		for(int switchNumber = 0; switchNumber<3; switchNumber++)
		{
			if(functionSwitch[switchNumber].isSelected())
				result += (1<<switchNumber);
		}
		return result;
	}
	
	// return executable value of the line and function switches
	// (as would be represented if taken from store)
	public synchronized int getLineAndFunctionValue()
	{
		int value = getLineValue() | (getFunctionValue() << 13);
		return value;
	}
	
	public void setEraseWrite(boolean value)
	{
		eraseWrite.setSelected(value);
	}
	
	public boolean getEraseWrite()
	{
		return !eraseWrite.isSelected();
	}
	
	// true is pulse, false is pre
	public boolean getPrePulse()
	{
		return prePulse.isSelected();
	}
	
	// true is pulse, false is pre
	public void setPrePulse(boolean value)
	{
		prePulse.setSelected(value);
	}
	
	// true is auto, false is man
	public boolean getManAuto()
	{
		return manAuto.isSelected();
	}
	
	// true is auto, false is man
	public void setManAuto(boolean value)
	{
		manAuto.setSelected(value);
	}
	
	public void redrawCrtPanel()
	{
		crtPanel.render();
		crtPanel.repaint();
	}
	
	public void updateActionLine()
	{
		// if automatic
		if( getManAuto() )
		{
			crtPanel.setActionLine( control.getLineNumber( control.getControlInstruction() ) );
		}
		// else manual
		else
		{
			crtPanel.setActionLine( getLineValue() );
		}
	}
	
	public void singleStep()
	{
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
	////////////////////////////////////////////////////////////
	
	// if pulse execute instructions repeatedly either from store or from line and function switches
	class PrePulsePushed implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			// are we now on pre or pulse?
			// on pulse
			if( getPrePulse() )
			{
				// turn off the highlighted action line
				crtPanel.setActionLine(-1);				
				// whether to take instructions from store or line and function switches
				// is handled in the actual execution of the animation
				baby.startAnimation();
			}
			// on pre
			else
			{
				baby.stopAnimation();
				updateActionLine();
			}
		}
	}
	
	// change selected display
	class DisplaySelectPressed implements ActionListener
	{
		DisplayType displayValue;
		
		public DisplaySelectPressed(DisplayType value)
		{
			displayValue = value;
		}
		
		public void actionPerformed(ActionEvent e)
		{
			crtPanel.setCrtDisplay(displayValue);
		}
	}
	
	// clear store
	class KscPushed implements MouseListener
	{
		public void mousePressed(MouseEvent e)
		{
			if(Baby.running)
			{
				control.setKscPressed(true);
			}
			else
			{
				store.reset();
				redrawCrtPanel();
			}
		}
		
		public void mouseReleased(MouseEvent e)
		{
			control.setKscPressed(false);
		}
		
		public void mouseEntered(MouseEvent e) { }
		public void mouseClicked(MouseEvent e) { }
		public void mouseExited(MouseEvent e) {	}
	}
	
	// clear CI,PI and ACC
	class KccPushed implements MouseListener
	{
		public void mousePressed(MouseEvent e)
		{
			if(Baby.running)
			{
				control.setKccPressed(true);
			}
			else
			{
				control.setControlInstruction(0);
				control.setPresentInstruction(0);
				control.setAccumulator(0);
				updateActionLine();
				redrawCrtPanel();
			}
		}
		
		public void mouseReleased(MouseEvent e)
		{
			control.setKccPressed(false);
		}
		
		public void mouseEntered(MouseEvent e) { }
		public void mouseClicked(MouseEvent e) { }
		public void mouseExited(MouseEvent e) {	}
	}
	
	// execute a single instruction (either from store or from line and function switches)
	class KspPushed implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			// clear stop flag if set
			if(control.getStopFlag() )
				control.setStopFlag(false);
			
			// true is Auto, false is Man
			if(getManAuto() )
			{
				// perform single instruction from store
				control.executeAutomatic();
				updateActionLine();
				crtPanel.render();
				crtPanel.repaint();
			}
			else
			{
				// perform single instruction from line and function switches
				control.executeManual();
				updateActionLine();
				crtPanel.render();
				crtPanel.repaint();
			}
		}
	}

	// typewriter button pushed
	class TypeWriterPushed implements MouseListener
	{
		private int keyNumber;
		private Control control;
		private SwitchPanel switchPanel;
		private Store store;
		
		public TypeWriterPushed(int aKeyNumber, Control aControl, Store aStore, SwitchPanel aSwitchPanel)
		{
			keyNumber = aKeyNumber;
			control = aControl;
			switchPanel = aSwitchPanel;
			store = aStore;
		}
		
		public void mousePressed(MouseEvent e)
		{
			// if running then adjust every action line
			if(Baby.running)
			{
				// signal control that a key is pressed
				// arguments: key pressed, number of key pressed, is it a write?
				control.setKeyPressed(true, keyNumber);
				
				// note, m1sim incorrectly stops running in this case rather than corrupting store lines
			}
			// otherwise just do current action line
			else
			{
				int lineNumber = switchPanel.getLineValue();
				// true = write, false = erase
				if(switchPanel.getEraseWrite() )
				{
					store.setLine( lineNumber, store.getLine(lineNumber) | (1<<keyNumber) );
				}
				else
				{
					store.setLine( lineNumber, store.getLine(lineNumber) & (~(1<<keyNumber)) );
				}
				switchPanel.redrawCrtPanel();
			}
		}
	
		public void mouseReleased(MouseEvent e)
		{
			// notify that the key is no longer pressed whether Baby is still
			// running or not.
			control.setKeyPressed(false, keyNumber );
		}
		
		public void mouseEntered(MouseEvent e) { }
		public void mouseClicked(MouseEvent e) { }
		public void mouseExited(MouseEvent e) {	}
	}

	class KlcPressed implements MouseListener
	{
		public void mousePressed(MouseEvent e)
		{
			if(Baby.running)
			{
				control.setKlcPressed(true);
			}
			else
			{
				store.setLine( getLineValue(), 0 );
				crtPanel.render();
				crtPanel.repaint();
			}
		}
		
		public void mouseReleased(MouseEvent e)
		{
			control.setKlcPressed(false);
		}
	
		public void mouseEntered(MouseEvent e) { }
		public void mouseClicked(MouseEvent e) { }
		public void mouseExited(MouseEvent e) {	}
	}
	
	class KacPushed implements MouseListener
	{
		public void mousePressed(MouseEvent e)
		{
			if(!Baby.running)
			{
				control.setAccumulator( 0 );
				crtPanel.render();
				crtPanel.repaint();
			}
			else
			{
				control.setKacPressed(true);
			}
		}
		
		public void mouseReleased(MouseEvent e)
		{
			control.setKacPressed(false);
		}
	
		public void mouseEntered(MouseEvent e) { }
		public void mouseClicked(MouseEvent e) { }
		public void mouseExited(MouseEvent e) {	}
	}
	
	class LineSwitchActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			updateActionLine();
		}
	}

	class ManAutoPressed implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
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