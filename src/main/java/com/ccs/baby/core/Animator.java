package com.ccs.baby.core;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.JFrame;
import java.awt.Frame;
import java.awt.event.*;

import java.text.DecimalFormat;

import com.vladium.utils.timing.ITimer;
import com.vladium.utils.timing.TimerFactory;

import com.ccs.baby.ui.CrtPanel;
import com.ccs.baby.ui.SwitchPanel;
import com.ccs.baby.core.Control;

class Animator extends Thread
{

	private CrtPanel crtPanel;
	private Control control;
	private SwitchPanel switchPanel;
	private final double speed = 1.44;
	
	// shows whether we should keep animating and indeed whether we currently are or not
	private boolean keepAnimating = false;
	
	public Animator(CrtPanel aCrtPanel, Control aControl, SwitchPanel aSwitchPanel)
	{
		crtPanel = aCrtPanel;
		control = aControl;
		switchPanel = aSwitchPanel;
		setDaemon(true); // if the main thread quits, so will this one
	}
	

	public synchronized void setKeepAnimating(boolean value)
	{
		keepAnimating = value;
	}
	
	public synchronized boolean getKeepAnimating()
	{
		return keepAnimating;
	}
	
	
	public void run() 
	{		
		// increment thread priority
		setPriority(getPriority() + 1);
		
		setKeepAnimating(true);
			
        //time = System.currentTimeMillis();
		final ITimer timer = TimerFactory.newTimer ();
		// Timer warmup
		for (int i = 0; i < 1000; ++ i)
        {
            timer.start ();
            timer.stop ();
            timer.getDuration ();
            timer.reset ();
        }
        

        double elapse = 0;
		// while allowed to keep animating
		while(getKeepAnimating())
		{
		 
		  timer.reset();
  	   	  timer.start();
			// if STP lamp lit then stop animating
			if(control.getStopFlag() )
			{
				setKeepAnimating(false);
			}
			// if STP lamp is not lit
			else
			{
				// execute X instructions before updating the display (while stop flag is unset)
				for(int x = 0; x<control.getInstructionsPerRefresh() && !control.getStopFlag(); x++)
				{	
					// if auto on switchpanel then use store for instructions
					if(switchPanel.getManAuto() )
					{
						control.executeAutomatic();
					}
					// else if man then use the line and functions switches from the switch panel
					else
					{
						control.executeManual();
					}
				}
				
				crtPanel.render();
				crtPanel.efficientRepaint();
				
				control.incCycleCount();
			}				
		  timer.stop();
		  elapse = timer.getDuration();
		  timer.reset();
		  while (elapse < 10) {
		  	timer.start();
		  	for(int n = 0; n < 1000; n++); // delay introduced here
		  	timer.stop();
		  	elapse += timer.getDuration();
		  	timer.reset();
		  }
		  //System.out.println(elapse);
		// end loop	
		 // set variable to show that animation has stopped
	   }
	   Baby.running = false;
    }
		
}

