package com.ccs.baby.core;

// Manchester Baby Simulator
// by David Sharp
// January 2001
// requires Java v1.2 or later

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.BoxLayout;
import javax.swing.event.*;
import java.io.*;
import java.util.*;
import java.net.URL;
import javax.swing.text.html.HTMLDocument;

import javax.swing.JFrame;
import java.applet.*;

import com.ccs.baby.menu.MenuSetup;

import com.ccs.baby.ui.CrtPanel;
import com.ccs.baby.ui.SwitchPanel;
import com.ccs.baby.ui.TexturedJPanel;
import com.ccs.baby.disassembler.Disassembler;

import com.ccs.baby.ui.FpsLabelService;
import com.ccs.baby.ui.FpsLabelPushed;

public class Baby extends JFrame implements ActionListener
{
	
	// get current dir
	private File fileChooserDirectory;
	private static String currentDir;
	
	// there are two implementations of the animation, one using the Timer class and one
	// using threads, if this is set to true then the threaded version is used which
	// seems to be the best all round solution (Timer is poor on Solaris).
	private final boolean threadedAnimation = true;

	// main component objects
	private Store store;
	private Control control;
	public SwitchPanel switchPanel;	
	private Disassembler disassembler;
	CrtPanel crtPanel;
	// thread control for animation
	private Animator animator;

	// modern controls
	protected JButton stepButton = new JButton("Step");
	protected JButton runButton = new JButton("Run");
	protected JButton stopButton = new JButton("Stop");
	private JButton fpsLabel;
	private FpsLabelService fpsLabelService;


	private Color backgroundColor = SwitchPanel.backgroundColor;

	// timer control for animation (alternative to thread)
	private javax.swing.Timer animateTimer;
	public static boolean running = false;
	public static TexturedJPanel mainPanel;
        public static JPanel globalPanel;
        //public static JPanel refManualPanel;
        //JFrame ref = new JFrame();
        //javax.swing.JEditorPane viewer;
        //javax.swing.JTextPane viewer;
	
	// timer for counting number of instructions executed each second to give speed
	private javax.swing.Timer fpsTimer;
	private java.util.Timer clockTimer;
	
	// stop lamp and icons
	public JButton stopLamp;
	ImageIcon onIcon;
	ImageIcon offIcon;

	// convert to java.sound.sampled.Clip as AudioClip lost to JApplet
        //public static AudioClip gong;
      

	public Baby()
	{
		
		// quit program when close icon clicked
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
		
		try {
		  currentDir = System.getProperty("user.home");
		  System.out.println(currentDir);
		}
		catch (SecurityException e) {
		  System.out.println("user.dir not accessible from applet");
                  System.out.println(e.getMessage());
		}



                            
		// check for clicks on modern controls
		stepButton.addActionListener(this);
		runButton.addActionListener(this);
		stopButton.addActionListener(this);
		
		// create main hardware components
		store = new Store();
		control = new Control(store);
		store.setControl(control);
		
		// set up display window gui
		Container contentPane = getContentPane();
		contentPane.setLayout( new BorderLayout() );
			
		JPanel toolPanel = new JPanel();
		toolPanel.setBackground(backgroundColor);
		toolPanel.add(stepButton);
		toolPanel.add(runButton);
		toolPanel.add(stopButton);
		 
		JPanel infoPanel = new JPanel();
		infoPanel.setBackground(backgroundColor);
		fpsLabel = new JButton("Speed and elapsed time info.");
		fpsLabel.addActionListener( new FpsLabelPushed(fpsLabel, control) );
		infoPanel.add(fpsLabel);

		fpsLabelService = new FpsLabelService(fpsLabel, control);
		
		crtPanel = new CrtPanel(store, control);
		crtPanel.setOpaque(false);		
			
		stopLamp = new JButton(offIcon);
		stopLamp.setFocusPainted(false);
		stopLamp.setBorderPainted(false);
		stopLamp.setContentAreaFilled(false);
		Insets marginSpace = new Insets(3,3,3,3); // set margin
		stopLamp.setMargin(marginSpace);
		
		JPanel modernControls = new JPanel();
		modernControls.setBackground(backgroundColor);
		modernControls.add(infoPanel);
		modernControls.add(toolPanel);
		
		mainPanel = new TexturedJPanel();
		//mainPanel.setBackground(backgroundColor);
		mainPanel.setLayout( new BorderLayout() );
                globalPanel = new JPanel();
                globalPanel.setLayout( new BorderLayout() );
                //refManualPanel = new JPanel();
                
                //viewer = new javax.swing.JEditorPane();
                //viewer = new javax.swing.JTextPane();
               // viewer.setEditable(false);
               
                
                //JScrollPane scrollPane = new JScrollPane(viewer);     
                //ref.add(scrollPane);
                //ref.setSize(505,700);
                //ref.setVisible(false);
                //ref.setAlwaysOnTop(true);
                //ref.setLocation(730,250);
                //viewer.addHyperlinkListener(new LinkClicked());

	    
                crtPanel.setPreferredSize(new Dimension(400, 400));
		mainPanel.add(crtPanel, BorderLayout.NORTH);
	    
                switchPanel = new SwitchPanel(store, control, crtPanel, this);
                switchPanel.setOpaque(false);
		control.setSwitchPanel(switchPanel);	// tell control about switchPanel
		crtPanel.setPreferredSize(new Dimension(400, 386));
		mainPanel.add(switchPanel);
		mainPanel.setSize(690, 905);
		contentPane.add(mainPanel, BorderLayout.CENTER);
		
		disassembler = new Disassembler(store, control, crtPanel);
		
		// set up timer animation (currently not used)
		// delay is in milliseconds
		// unless timer is 0 then we get horrendous slow down
		animateTimer = new javax.swing.Timer(0, this);
		animateTimer.setInitialDelay(0);
		animateTimer.setCoalesce(true);
		
		// create timer to calculate speed to tick once a second
		fpsTimer = new javax.swing.Timer(1000, this);
		fpsTimer.setInitialDelay(0);
		
		
		
		// ???
		//animator = new Animator(crtPanel, control, switchPanel);
		
		// note, thread immediate waits so we can always use notify to restart it
		
		
		
		// Set up and add menu bars to the window
		JMenuBar menuBar = new JMenuBar();
		new MenuSetup(menuBar, store, control, crtPanel, switchPanel, disassembler, currentDir, this);
		setJMenuBar(menuBar);
		
		// set main size of window
		//setTitle("Baby Simulator");
		
		// reset the hardware to initial values		
		store.reset();
		control.reset();
		
		// load a program by default "hcf.asm"
		try
		{
			store.loadLocalModernAssembly("demos/diffeqt.asm");
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(getContentPane(), "Default program not loaded. " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
				
		// render and display the CRT display
		crtPanel.render();
		crtPanel.repaint();
		
		// set tool tips
		stepButton.setToolTipText("Execute the next instruction.");
		runButton.setToolTipText("Start executing the instructions in the store.");
		stopButton.setToolTipText("Stop executing instructions.");
		fpsLabel.setToolTipText("Displays the speed of the simulation.");
		crtPanel.setToolTipText("The monitor.");
		stopLamp.setToolTipText("Lamp lit when the STP instruction is executed.");
		
		crtPanel.render();
		// open window
		setVisible(true);
		
		
		// open switch panel window too
		//switchPanel.setVisible(true);
	}
        /*
        class LinkClicked implements HyperlinkListener
        {
            public void hyperlinkUpdate(HyperlinkEvent event)
            {
                if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                  try {
                    viewer.setPage(event.getURL());
                  } catch(IOException ioe) {
                    // Some warning to user
                  }
                }
            }
        }*/
	
	public void init() {
            //String s = this.getDocumentBase().toString();
                
            try
            {
                File aud = new File("horn.wav");
                //Baby.gong = this.getAudioClip(aud.toURI().toURL()); 
                
               
            }
            /*
            catch (java.net.MalformedURLException e)
            {
              System.out.println(e.getMessage());
            }
            catch (IOException e)
            {
              System.out.println(e.getMessage());  
            }
            */
            catch (NullPointerException e)
            {
              System.out.println(e.getMessage());
            }
	
		
	}
	
	
	// main method, create main window
	public static void main(String args[]) {

		
                Baby baby = new Baby();
                //JApplet app1 = new JApplet();
                //BabyAppletStub babyAppletStub = new BabyAppletStub(baby);
                //baby.setStub(babyAppletStub);
                
                baby.init();
                

        /*
		Frame mainFrame = new Frame();
		mainFrame.setSize(700, 950);
		mainFrame.setTitle("Baby");
		mainFrame.add(baby);
		mainFrame.addWindowListener(new WindowAdapter(){public void windowClosing(WindowEvent e){System.exit(0);}});
		
		mainFrame.setVisible(true);
		mainFrame.setResizable(false);
		*/

		baby.setSize(700, 950);
		baby.setTitle("Baby");
		baby.addWindowListener(new WindowAdapter(){public void windowClosing(WindowEvent e){System.exit(0);}});
		
		baby.setVisible(true);
		baby.setResizable(false);


	}
	



	// start running animation either using threads or timer
	// this is hardcoded and cannot me changed mid-session
	public synchronized void startAnimation()
	{
		if(threadedAnimation)
		{
			if(!running)
			{			
				// create new thread for animation
				animator = new Animator(crtPanel, control, switchPanel);
				animator.startAnimating();
			}
		}
		else
		{
			if( !animateTimer.isRunning() )
			{
				// start the animation timer running
				animateTimer.start();
			}
		}
		
		// start fps timer
		fpsTimer.start();
		control.setCycleCount(0);
		running = true;
	}

	// halt animation
	public void stopAnimation()
	{	
		if(threadedAnimation)
		{	
			if(running)
			{
				animator.stopAnimating();
				running = false;
			}
		}
		else
		{
			if(animateTimer.isRunning() )
			{
				animateTimer.stop();
				running = false;
			}
		}
		switchPanel.updateActionLine();

		// repaint so that control with the PI can be drawn if necessary
		crtPanel.render();
		crtPanel.repaint();
		fpsTimer.stop();
	}
	
	
	// handle timers
	public void actionPerformed(ActionEvent e)
	{
		// animation timer tick
		if( e.getSource() == animateTimer )
		{
			
			// if STP instruction has been executed stop the animation
			if( control.getStopFlag() )
			{
				stopAnimation();
			}
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
				
				// update display
				crtPanel.render();
				crtPanel.efficientRepaint();
				
				// increment number of cycles of X instructions executed
				control.incCycleCount();	
			}
			
		}
		// if the one second timer to measure the speed
		else if( e.getSource() == fpsTimer )
		{
			fpsLabelService.updateFpsLabel();
			
			int actualFpsValue = control.getCycleCount()*control.getInstructionsPerRefresh();
						
			// speed adjustment
			// adjust number of instructions per refresh to get 700 fps or as close as possible
			if(actualFpsValue > 730)		
			{						
				int newValue = control.getInstructionsPerRefresh();
				newValue--;
				if(newValue < 1) newValue = 1;
				control.setInstructionsPerRefresh(newValue);
			}
			else if(actualFpsValue < 670)		
			{	
				int newValue = control.getInstructionsPerRefresh();
				newValue++;		
				if(newValue > 20) newValue = 20;
				control.setInstructionsPerRefresh(newValue);
			}
			
			// reset counter ready for the next second
			control.setCycleCount(0);
			
			// if the Baby has stopped animating then no need to keep timing.
			if(!Baby.running)
				fpsTimer.stop();
		}
		// if step button pressed
		else if( e.getSource() == stepButton )
		{
			switchPanel.setManAuto(true);
			// set to write
			switchPanel.setEraseWrite(true);
			// set L stat switches to all be on
			for(int lStatSwitch=0; lStatSwitch<5; lStatSwitch++)
				switchPanel.lineSwitch[lStatSwitch].setSelected(true);
			// likewise F stat switches
			for(int fStatSwitch=0; fStatSwitch<3; fStatSwitch++)
				switchPanel.functionSwitch[fStatSwitch].setSelected(true);
				
			switchPanel.kspSwitch.doClick();
		}
		else if( e.getSource() == runButton )
		{
			switchPanel.setManAuto(true);
			// set to write
			switchPanel.setEraseWrite(true);
			// set L stat switches to all be on
			for(int lStatSwitch=0; lStatSwitch<5; lStatSwitch++)
				switchPanel.lineSwitch[lStatSwitch].setSelected(true);
			// likewise F stat switches
			for(int fStatSwitch=0; fStatSwitch<3; fStatSwitch++)
				switchPanel.functionSwitch[fStatSwitch].setSelected(true);
			// flick CS switch which starts the animation
			if(!switchPanel.getPrePulse() )
			{
				switchPanel.prePulse.doClick();
			}
		}
		// if stop button pressed the turn off the CS switch
		else if( e.getSource() == stopButton )
		{
			if(switchPanel.getPrePulse() )
				switchPanel.prePulse.doClick();
		}
	}
	

        
	// update the stop lamp according to the internally held stop flag
	public void updateStopLamp()
	{
		if(control.getStopFlag() )
		{
			Baby.mainPanel.setTexture(true);

                        
                        try
                        {
                            //Baby.gong = getAudioClip(getCodeBase(), "TestSnd.wav"); 

                            //Baby.gong.play();
                        }
                        catch (Exception e)
                        {
                            System.out.println("sound: "+e.getMessage());
                        }
                                
			//switchPanel.setPrePulse(false);
		}
		else
		{
			Baby.mainPanel.setTexture(false);
		}
	}





















	

	

	
	
}