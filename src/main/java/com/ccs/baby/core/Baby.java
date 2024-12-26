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

import com.ccs.baby.ui.*;
import com.ccs.baby.ui.DebugPanel;

import com.ccs.baby.disassembler.Disassembler;


import com.ccs.baby.animation.AnimationManager;

public class Baby extends JFrame
{
	
	// get current dir
	private File fileChooserDirectory;
	private static String currentDir;

	// main component objects
	private Store store;
	private Control control;
	public SwitchPanel switchPanel;	
	private Disassembler disassembler;
	CrtPanel crtPanel;
	DebugPanel debugPanel;


	private AnimationManager animationManager;
	public static volatile boolean running = false;

	public static TexturedJPanel mainPanel;
        public static JPanel globalPanel;
        //public static JPanel refManualPanel;
        //JFrame ref = new JFrame();
        //javax.swing.JEditorPane viewer;
        //javax.swing.JTextPane viewer;

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



                            


		// Create LampManager
		LampManager lampManager = new LampManager();

		// create main hardware components
		store = new Store();
		control = new Control(store, lampManager);
		store.setControl(control);
		
		// set up display window gui
		Container contentPane = getContentPane();
		contentPane.setLayout( new BorderLayout() );
			

		
		crtPanel = new CrtPanel(store, control);
		crtPanel.setOpaque(false);		




		
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

		// Add modernControls to mainPanel
		debugPanel = new DebugPanel(control, switchPanel);
		debugPanel.setOpaque(true);
		contentPane.add(debugPanel, BorderLayout.SOUTH);

		disassembler = new Disassembler(store, control, crtPanel);

		// ???
		//animator = new Animator(crtPanel, control, switchPanel);
		
		// note, thread immediate waits so we can always use notify to restart it


		FpsLabelService fpsLabelService = debugPanel.getFpsLabelService();

		// Initialize AnimationManager
		animationManager = new AnimationManager(control, crtPanel, switchPanel, true, fpsLabelService);





		// Set up and add menu bars to the window
		JMenuBar menuBar = new JMenuBar();
		new MenuSetup(menuBar, store, control, crtPanel, switchPanel, disassembler, currentDir, this, debugPanel);
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
		

		crtPanel.setToolTipText("The monitor.");
//		stopLamp.setToolTipText("Lamp lit when the STP instruction is executed.");
		
		crtPanel.render();
		// open window
		setVisible(true);

		contentPane.revalidate();
		contentPane.repaint();
		
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

	// Delegate animation control methods
	public synchronized void startAnimation() {
		animationManager.startAnimation();
		running = true;
	}

	public synchronized void stopAnimation() {
		animationManager.stopAnimation();
		running = false;
	}


}