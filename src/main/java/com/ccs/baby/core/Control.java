package com.ccs.baby.core;

import com.ccs.baby.ui.*;

public class Control
{

	// store
	private final Store store;

	private int accumulator;

	private int controlInstruction;
	
	private int presentInstruction;
	
	// used for special cases where old value affects new value
	private int oldPresentInstruction = 0;

	private final LampManager lampManager;
	private boolean stopFlag = false;
	
	// speed details here so can be synchronized with a get set method
	private int cycleCount = 0;
	private int instructionsPerRefresh = 2;
	
	public StaticisorPanel staticisorPanel;
	public CrtControlPanel crtControlPanel;
	
	// values as to various keys on the switchpanel which may be held down
	// and will affect execution
	private boolean keyPressed = false;
	private int keyNumberPressed;
	private boolean kscPressed = false;
	private boolean kccPressed = false;
	private boolean klcPressed = false;
	private boolean kacPressed = false;
	
	// function number encodings for different instructions 
	public static final int FUNC_JMP = 0;
	public static final int FUNC_JRP = 1;
	public static final int FUNC_LDN = 2;
	public static final int FUNC_STO = 3;
	public static final int FUNC_SUB = 4;
	public static final int FUNC_SUB5 = 5;
	public static final int FUNC_CMP = 6;
	public static final int FUNC_STP = 7;

	
	

	public Control(Store aStore, LampManager lampManager)
	{
		store = aStore;
		this.lampManager = lampManager;
	}
	
	// reset to default values
	public void reset()
	{
		setStopFlag(false);
		controlInstruction = 0;
		presentInstruction = 0;
		accumulator = 0;
	}
	
	// 2-stage construction to set up the switchpanel which is mutually dependent on the control
	public void setSwitchPanel(StaticisorPanel staticisorPanel, CrtControlPanel crtControlPanel)
	{
		this.staticisorPanel = staticisorPanel;
		this.crtControlPanel = crtControlPanel;
	}
	
	// get and set functions for....
		
	// control instruction	
	public int getControlInstruction()
	{
		return controlInstruction;
	}
	
	public void setControlInstruction(int value)
	{
		controlInstruction = value;
	}
	
	// get number of cycles of X instructions executed
	public synchronized int getCycleCount()
	{
		return cycleCount;
	}
	
	public synchronized void setCycleCount(int value)
	{
		cycleCount = value;
	}
	
	public synchronized void incCycleCount()
	{
		cycleCount++;
	}
	
	// get the number of instructions to execute before updating the CRT
	public synchronized int getInstructionsPerRefresh()
	{
		return instructionsPerRefresh;
	}
	
	public synchronized void setInstructionsPerRefresh(int value)
	{
		instructionsPerRefresh = value;
	}
	
	// if the CI incremented past 31 then the SSEM still functions as only bits 0-4
	// are read by the decode hardware
	public void incrementControlInstruction()
	{
		controlInstruction++;
	}

	// present instruction
	public int getPresentInstruction()
	{
		return presentInstruction;
	}
	
	public void setPresentInstruction(int value)
	{
		presentInstruction = value;
	}
	
	// accumulator
	public int getAccumulator()
	{
		return accumulator;
	}
	
	public void setAccumulator(int value)
	{
		accumulator = value;
	}
	
	// stop flag
	public synchronized boolean getStopFlag()
	{
		return stopFlag;
	}
	
	public void setStopFlag(boolean value)
	{
		stopFlag = value;
		lampManager.updateStopLamp(value);
	}

	// approximately executes 700 instructions and counts number of store changes in order to enable
	// estimation of what value to start the numberOfInstructionsPerFrame setting on
	// internals of this method are a cut down version of the real execution method but optimised for speed.
	// not actually used anymore!
	public int averageStoreChanges()
	{
		boolean localStopFlag = getStopFlag();
		int localControlInstruction = getControlInstruction();
		int localAccumulator = getAccumulator();
		int localPresentInstruction;
		int localStore[] = new int[32];
		for(int line = 0; line<32; line++)
			localStore[line] = store.getLine(line);
		
		int storeChanges = 0;
		
		for(int instructionCount = 0; instructionCount<700; instructionCount++)
		{
			localControlInstruction++;
			localPresentInstruction = localStore[ getLineNumber(localControlInstruction) ];
			switch( getFunctionNumber(localPresentInstruction) )
			{
				case FUNC_JMP : localControlInstruction = localStore[ getLineNumber(localPresentInstruction) ]; break;
				case FUNC_JRP : localControlInstruction += localStore[ getLineNumber(localPresentInstruction) ]; break;
				case FUNC_LDN : localAccumulator = -( localStore[ getLineNumber(localPresentInstruction) ] ); break;
				case FUNC_STO : localStore[ getLineNumber(localPresentInstruction) ] = localAccumulator;
						 storeChanges++;
						 break;
				case FUNC_SUB :					// also sub so drop through to case 5
				case FUNC_SUB5 : localAccumulator -= localStore[ getLineNumber(localPresentInstruction) ]; break;
				case FUNC_CMP : if( localAccumulator < 0 ) localControlInstruction++; break;
				case FUNC_STP : localStopFlag = true; break;
			}
		}
		// if stops in first second then execute it slowly
		if(localStopFlag)
			storeChanges = 0;
		
		return storeChanges;
	}


	public void singleStep() {
		staticisorPanel.setManAuto(true);
		// set to write
		crtControlPanel.setWriteErase(true);
		// set L stat switches to all be on
		staticisorPanel.setLineSwitches(true);
		// likewise F stat switches
		staticisorPanel.setFunctionSwitches(true);

		crtControlPanel.simulateKspClick();
	}

	public void startRunning() {
		staticisorPanel.setManAuto(true);
		// set to write
		crtControlPanel.setWriteErase(true);
		staticisorPanel.setLineSwitches(true);

		// flick CS switch which starts the animation
		if (!crtControlPanel.getStopRun()) {
			crtControlPanel.simulateStopRunToggle();
		}
	}

	public void stopRunning() {
		if (crtControlPanel.getStopRun()) {
			crtControlPanel.simulateStopRunToggle();
		}
	}


	// execute a single instruction from store
	public synchronized void executeAutomatic()
	{
		
		oldPresentInstruction = presentInstruction;
		
		// increment control instruction
		incrementControlInstruction();
		
		// if any of the F and L stat switches are set to off (0) then the bit can't
		// get from the C tube to the staticisors so clear any bits not set to 1 in the staticisor
		// in the CI to be used
		int switchValue = getLineAndFunctionValue();
		int controlInstructionValue = getControlInstruction() & switchValue;
		
		// fetch instruction into present instruction
		// only read bits 0-4 in the decoding
		presentInstruction = store.getLine( getLineNumber( controlInstructionValue ) );
				
		// if key on typewriter held down while running then affects every action line
		// An action line is one that is accessed, either to get the instruction or as an
		// operand, either reading or writing. Every line that is accessed while the button
		// is pressed will be affected, either a one or a zero written into that place,
		// depending on the position of the write/erase switch.
		if(keyPressed)
		{	
			// if write selected
			if( crtControlPanel.getWriteErase())
			{
				// so corrupt present instruction
				presentInstruction |= (1<<keyNumberPressed);
				// note the operand line DOES get corrupted even if it's not actually used e.g. in CMP and STP
				// get action line
				int actionLine = getLineNumber(presentInstruction);
				// corrupt operand
				store.setLine(actionLine, store.getLine(actionLine) | (1<<keyNumberPressed));
			}
			// else erase selected
			else
			{
				// corrupt present instruction
				presentInstruction &= (~(1<<keyNumberPressed));
				// get the operand line number
				int actionLine = getLineNumber(presentInstruction);
				// corrupt operand
				store.setLine(actionLine, store.getLine(actionLine) & (~(1<<keyNumberPressed)) );
			}	
		}
		
		// if KLC pressed then blank every action line as it's read
		if(klcPressed)
		{
			// the present instruction is blanked also as it's read
			presentInstruction = 0;
			
			// action line number taken from the present instruction is going to be 0
			store.setLine(0, 0 );
		}
		
		// note
		// according to the PRM in section A3.3.3 Erase/Write set to erase should erase
		// every action line as it's accessed (in the same way as holding KLC). Having
		// queried Chris Burton about this he doesn't seem to think so from the IEE paper schematic
		// but reasoned that it must have been in the PRM for a reason, for the moment
		// this feature is left out and the erase/write switch only affects the store if
		// a typewriter button is pressed.
		
		// if kac button held down then set accumulator to 0
		if(kacPressed)
			setAccumulator( 0 );
			
		// if kac button held down then set accumulator to 0
		if(kacPressed)
			setAccumulator( 0 );
		
		if(kscPressed)
			store.reset();
			
		if(kccPressed)
		{
			setControlInstruction(0);
			setPresentInstruction(0);
			setAccumulator(0);
		}
				
		// since fetch of PI from store uses same hardware as the fetch of CI from
		// the store then if bits 13-15 (the function number) of the CI are non-zero
		// then they will corrupt the PI being fetched and crash the SSEM when it's
		// erroneously decoded.
		if( getFunctionNumber(controlInstructionValue) != 0 )
		{
			// if function number is 1 then add the line of store being fetched into
			// the present instruction to the previous value of the present instruction
			// see PRM A1.4
			if(getFunctionNumber(controlInstructionValue) == 1)
			{
				presentInstruction += oldPresentInstruction;
			}
			else
			{
				
				// some kind of crash or incorrect execution should be simulated,
				// it is not clear exactly what so has not been handled
				
			}
		}
		
		// if any of the F and L stat switches are set to off (0) then the bit can't
		// get from the C tube to the staticisors so clear any bits not set to 1 in the staticisor
		// in the PI to be used
		// note, this effect does not corrupt lines fetched from the store
		int presentInstructionValue = presentInstruction & switchValue;
				
		// get function number from present instruction and perform
		performInstruction( presentInstructionValue );
		
	}


	// return executable value of the line and function switches
	// (as would be represented if taken from store)
	public synchronized int getLineAndFunctionValue() {
		return staticisorPanel.getLineValue() | (staticisorPanel.getFunctionValue() << 13);
	}
	
	// note,
	// Chris Burton suggests that he recalls something about:
	// the CI does not change, i.e. you can do a manual instruction
	// without losing your place in a program, on Single Shot, but it counts up
	// on Run. 
	// this has not been implemented since the details are not clear
	
	// execute the instruction encoded on the staticisor switches
	public void executeManual()
	{
		
		// get instruction from switches
		int instructionValue = getLineAndFunctionValue();
		
		// if kac button held down then set accumulator to 0
		if(kacPressed)
			setAccumulator( 0 );
		
		if(kscPressed)
			store.reset();
			
		if(klcPressed)
			store.setLine( staticisorPanel.getLineValue(), 0 );
			
		if(kccPressed)
		{
			setControlInstruction(0);
			setPresentInstruction(0);
			setAccumulator(0);
		}
		
		// if typewriter button pressed corrupt action lines
		// present instruction is NOT corrupted since it's encoded on the switches!
		if(keyPressed)
		{
			// if write selected
			if( crtControlPanel.getWriteErase())
			{
				// note the operand line DOES get corrupted even if it's not actually used e.g. in CMP and STP
				// get action line
				int actionLine = getLineNumber(instructionValue);
				// corrupt operand
				store.setLine(actionLine, store.getLine(actionLine) | (1<<keyNumberPressed));
			}
			// else erase selected
			else
			{
				// get the operand line number
				int actionLine = getLineNumber(instructionValue);
				// corrupt operand
				store.setLine(actionLine, store.getLine(actionLine) & (~(1<<keyNumberPressed)) );
			}
		}
				
		performInstruction( instructionValue );
		
		// when executing a manual instruction the instruction displayed in the PI
		// is that which is in the store line pointed to by the CI
		// the line executed is the one encoded on the L and F stat switches
		// hence set the present instruction displayed to that pointed to by the CI
		setPresentInstruction( store.getLine( getControlInstruction() ) );
	}
	
	// the performance of the instruction is separate to the associated fetching and CI incrementing
	// as manual instructions can be selected on the switch panel and executed also
	public synchronized void performInstruction(int instructionValue)
	{
		
		switch( getFunctionNumber(instructionValue) )
		{
			case FUNC_JMP : 
					// jmp	(s, C)
					controlInstruction = store.getLine( getLineNumber(instructionValue) );
					break;
			case FUNC_JRP :
					// jrp	(c+s, C)
					controlInstruction += store.getLine( getLineNumber(instructionValue) );
					break;
			case FUNC_LDN :
					// ldn	(-s, A)
					accumulator = -( store.getLine( getLineNumber(instructionValue) ) );
					break;
			case FUNC_STO :
					// sto	(a, S)
					store.setLine( getLineNumber(instructionValue), accumulator);
					break;
			case FUNC_SUB :					// also sub so drop through to case 5
			case FUNC_SUB5 :
					// sub	(a-s, A)
					accumulator -= store.getLine( getLineNumber(instructionValue) );
					break;
			case FUNC_CMP :
					// cmp	(Test)
					if( accumulator < 0 )
						incrementControlInstruction();
					break;
			case FUNC_STP :
					// stp	(Stop)
					setStopFlag(true);
					break;
		}
	}
		

	// return true if the function from the line will make use of the action number
	// used to check if the action line is used so it can be corrupted if necessary
	public boolean makesUseOfActionLine(int instructionValue)
	{
		// only STP and CMP do not refer to an action line (i.e. function numbers 6 & 7)
		// so return true if it's not one of those
		int funcNumber = getFunctionNumber(instructionValue);
		return (funcNumber != FUNC_CMP) && (funcNumber != FUNC_STP);
	}

	
	public String toString()
	{
		String output = "";
		
		output += "Control Instruction: " + controlInstruction + "\n";
		output += "Present Instruction: " + presentInstruction + "\n";
		output += "Accumulator: " + accumulator + "\n";
		
		output += "Stopped: ";
		if(getStopFlag() )
			output += "true";
		else
			output += "false";
		
		return output;
	}
	
	// indicate that a key on the keypad is pressed and to affect every action line
	public void setKeyPressed(boolean set, int keyNumber)
	{
		keyPressed = set;
		keyNumberPressed = keyNumber;
	}
	
	// indicate that KLC switch is being held down (clear all action lines)
	public void setKlcPressed(boolean value)
	{
		klcPressed = value;
	}
	
	public void setKacPressed(boolean value)
	{
		kacPressed = value;
	}
	
	public void setKscPressed(boolean value)
	{
		kscPressed = value;
	}

	public void setKccPressed(boolean value)
	{
		kccPressed = value;
	}	
	
	// private methods
	
	// returns the line number to be acted upon by the present instruction
	public static int getLineNumber(int value)
	{
		return (value & 0x1F);
	}
	
	// return the number in bits 13-15 of argument
	public static int getFunctionNumber(int value)
	{
		return ((value >> 13) & 0x07);
	}

}
