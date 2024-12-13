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

// main class to initialise program and owns display frame

public class Baby extends JApplet implements ActionListener
{
	
	// get current dir
	File fileChooserDirectory;
	static String currentDir;
	
	// there are two implementations of the animation, one using the Timer class and one
	// using threads, if this is set to true then the threaded version is used which
	// seems to be the best all round solution (Timer is poor on Solaris).
	private final boolean threadedAnimation = true;
	
	// number of instructions real Baby executed in a second
	private static final double realCyclesPerSecond = 700.0;
	private double elapsedTime = 0; // in seconds
	
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
        public static AudioClip gong;
      

	public Baby()
	{
		
		// quit program when close icon clicked
		/*
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
		*/
		
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
		fpsLabel.addActionListener( new FpsLabelPushed() );
		infoPanel.add(fpsLabel);
		
		
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
		
		mainPanel = new TexturedJPanel("main.png");
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
		menuSetup(menuBar);
		setJMenuBar(menuBar);
		
		// set main size of window
		//setTitle("Baby Simulator");
		
		// reset the hardware to initial values		
		store.reset();
		control.reset();
		
		// load a program by default "hcf.asm"
		try
		{
			store.loadLocalModernAssembly("diffeqt.asm");
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
                Baby.gong = this.getAudioClip(aud.toURI().toURL()); 
                
               
            }
            catch (java.net.MalformedURLException e)
            {
              System.out.println(e.getMessage());
            }
            catch (IOException e)
            {
              System.out.println(e.getMessage());  
            }
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
                

		Frame mainFrame = new Frame();
		mainFrame.setSize(700, 950);
		mainFrame.setTitle("Baby");
		mainFrame.add(baby);
		mainFrame.addWindowListener(new WindowAdapter(){public void windowClosing(WindowEvent e){System.exit(0);}});
		
		mainFrame.setVisible(true);
		mainFrame.setResizable(false);
		

	}
	
	private ImageIcon loadImage(String image) {
      int MAX_IMAGE_SIZE = 2400;  //Change this to the size of
                                 //your biggest image, in bytes.
      int count = 0;
      BufferedInputStream imgStream = new BufferedInputStream(
                                    this.getClass().getResourceAsStream(image));
      if (imgStream != null) {
        byte buf[] = new byte[MAX_IMAGE_SIZE];
        try {
            count = imgStream.read(buf);
            imgStream.close();
        } catch (java.io.IOException ioe) {
            System.err.println("Couldn't read stream from file: " + image);
            return null;
        }
        if (count <= 0) {
            System.err.println("Empty file: " + image);
            return null;
        }
        return new ImageIcon(Toolkit.getDefaultToolkit().createImage(buf));
      } 
      else {
        System.err.println("Couldn't find file: " + image);
        return null;
      }
    }

	// set up all the menus
	public void menuSetup(JMenuBar m){

		// create 3 menus
		JMenu fileMenu = new JMenu("File");
		JMenu viewMenu = new JMenu("View");
		JMenu exampleMenu = new JMenu("Examples");
		JMenu helpMenu = new JMenu("Help");
		
		// file menu
		JMenuItem loadSnapshotAssembly = new JMenuItem("Load snapshot/assembly");
		JMenuItem saveSnapshot = new JMenuItem("Save snapshot");
		JMenuItem saveAssembly = new JMenuItem("Save assembly");
		JMenuItem close = new JMenuItem("Close");
                JMenuItem refManual = new JMenuItem("Reference Manual");
		
		// examples menu
		JMenuItem diffeqt = new JMenuItem("diffeqt.asm");
		JMenuItem baby9 = new JMenuItem("Baby9.snp");
		JMenuItem primegen = new JMenuItem("primegen.asm");
		JMenuItem virpet = new JMenuItem("virpet.asm");
		JMenuItem noodleTimer = new JMenuItem("noodletimer.snp");
		
		// view menu
		JMenuItem viewStore = new JMenuItem("Store");
		JMenuItem viewControl = new JMenuItem("Control");
		JMenuItem viewAccumulator = new JMenuItem("Accumulator");
		//JMenuItem viewSwitchPanel = new JMenuItem("Switch Panel");
		JMenuItem viewDisassembler = new JMenuItem("Disassembler");
		
		// help menu
		JMenuItem about = new JMenuItem("About");
		
		// add action listeners for each item		
		loadSnapshotAssembly.addActionListener(new LoadSnapshotAssembly());
		saveSnapshot.addActionListener(new SaveSnapshot());
		saveAssembly.addActionListener(new SaveAssembly());
		
		diffeqt.addActionListener(new LoadExample("diffeqt.asm"));
		baby9.addActionListener(new LoadExample("Baby9.snp"));
		primegen.addActionListener(new LoadExample("primegen.asm"));
		virpet.addActionListener(new LoadExample("virpet.asm"));
		noodleTimer.addActionListener(new LoadExample("noodletimer.snp"));
		/*
                refManual.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				ref.setVisible(true);
                                try
                                {
                                    //viewer.setPage(new URL("http://www.cs.man.ac.uk/~toby/ssem/ssemref.html"));
                                    //javax.swing.text.html.HTMLDocument r = new javax.swing.text.html.HTMLDocument();
                                    //r.setBase(new URL("progref1.html"));
                                    //viewer.setDocument(r);
                                    
                                    //java.net.URL refurl = Baby.class.getResource("refman.htm");
                                    /viewer.setPage(new URL("http://www.cs.man.ac.uk/~toby/ssem/ssemref.html"));
                                    //javax.swing.text.DefaultStyledDocument refmanual = new javax.swing.text.DefaultStyledDocument();
                                    //refmanual.
                                    //viewer.setDocument(refmanual);
                                    //javax.swing.text.SimpleAttributeSet attr = new javax.swing.text.SimpleAttributeSet();

                                   // attr.addAttribute(javax.swing.text.StyleConstants.FontSize, new Integer(12));

                                    //viewer.getgetStyledDocument().setCharacterAttributes(0,viewer.getDocument().getText(0,viewer.getDocument().getLength()),attr,false);
                                    
                                    
                                }
                                catch (java.io.IOException err)
                                {
                                    viewer.setContentType("text/html");
                                    viewer.setText("<html>Could not load reference manual </html>");
                                }
                                catch (NullPointerException err)
                                {
                                    viewer.setContentType("text/html");
                                    viewer.setText("<html>Could not load reference manual </html>");
                                }
			}
		});
                */
               
                
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});
		
		viewStore.addActionListener(new ViewStore(crtPanel));
		viewControl.addActionListener(new ViewControl(crtPanel));
		viewAccumulator.addActionListener(new ViewAccumulator(crtPanel));
		//viewSwitchPanel.addActionListener(new ViewSwitchPanel(switchPanel));
		viewDisassembler.addActionListener(new ViewDisassembler(disassembler));
		
		
		
		
		about.addActionListener(new About());
	
		// set the keyboard click equivalent for each menu item
		loadSnapshotAssembly.setMnemonic(KeyEvent.VK_L);

		// add items to file menu
		fileMenu.add(loadSnapshotAssembly);
		fileMenu.add(saveSnapshot);
		fileMenu.add(saveAssembly);
		fileMenu.add(close);
		
		// add items to view menu
		viewMenu.add(viewStore);
		viewMenu.add(viewControl);
		viewMenu.add(viewAccumulator);
		//viewMenu.add(viewSwitchPanel);
		viewMenu.add(viewDisassembler);
		
		exampleMenu.add(diffeqt);
		exampleMenu.add(baby9);
		exampleMenu.add(primegen);
		exampleMenu.add(virpet);
		exampleMenu.add(noodleTimer);
		
		// add items to help menu
		//helpMenu.add(refManual);
                helpMenu.add(about);
                
		
		// add menus to bar
		m.add(fileMenu);
		m.add(viewMenu);	
		m.add(exampleMenu);
		m.add(helpMenu);
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
				animator.start();
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
				animator.setKeepAnimating(false);
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
			updateFpsLabel();
			
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
	
	// update the speed display with the latest data
	private void updateFpsLabel()
	{
		elapsedTime += (  ((double)(control.getCycleCount() * control.getInstructionsPerRefresh())) / realCyclesPerSecond  );
			
		// round percentage of real speed to 1 dec place
		String percentage = "" + (((control.getCycleCount()*control.getInstructionsPerRefresh())/realCyclesPerSecond)*100);
		int pointPos = percentage.indexOf('.');
		if(pointPos != -1)
			percentage = percentage.substring(0, pointPos+2);
		
		// round elapsed time in seconds to 1 dec place
		String elapsedTimeS = "" + elapsedTime;
		pointPos = elapsedTimeS.indexOf('.');
		if(pointPos != -1)
			elapsedTimeS = elapsedTimeS.substring(0, pointPos+2);
							
		fpsLabel.setText("" + (control.getCycleCount()*control.getInstructionsPerRefresh()) + " fps "
							+ percentage + "% "
							+ elapsedTimeS + "s");
	}
	
        
        
	// update the stop lamp according to the internally held stop flag
	public void updateStopLamp()
	{
		if(control.getStopFlag() )
		{
			Baby.mainPanel.changeTexture("mainon.png");
                        Baby.mainPanel.repaint();
                        
                        try
                        {
                            Baby.gong = getAudioClip(getCodeBase(), "TestSnd.wav"); 

                            Baby.gong.play();
                        }
                        catch (Exception e)
                        {
                            System.out.println("sound: "+e.getMessage());
                        }
                                
			//switchPanel.setPrePulse(false);
		}
		else
		{
			Baby.mainPanel.changeTexture("main.png");
		    Baby.mainPanel.repaint();
		}
	}
	
	boolean firstLoad = true;
	// deal with clicks on the "Load snapshot/assembly" menu item
	class LoadSnapshotAssembly implements ActionListener
	{
   
		public void actionPerformed(ActionEvent e)
		{
			JFileChooser fc;
			// Open up a load box and select the item
			if (firstLoad)
			  fc=new JFileChooser(currentDir);
			else 
			  fc=new JFileChooser(fileChooserDirectory);
			firstLoad = false;
			
			fc.setDialogTitle("Load snapshot or assembly...");
			
			
			File file;
			int returnVal = fc.showOpenDialog(getContentPane() );
			
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				file = fc.getSelectedFile();
				fileChooserDirectory = file.getParentFile();
				String currentFile = file.toString();
				try
				{
				// detect file type and then load appropriately if possible
				
					control.setInstructionsPerRefresh(4);
					switch(store.getFileType(currentFile) )
					{
						case Store.UNACCEPTABLE		: JOptionPane.showMessageDialog(getContentPane(), "Unrecognised file type", "Error", JOptionPane.ERROR_MESSAGE); break;
						case Store.SNAPSHOT			: store.loadSnapshot(currentFile); break;
						case Store.ASSEMBLY			: store.loadModernAssembly(currentFile); break;
						default						: JOptionPane.showMessageDialog(getContentPane(), "Unrecognised file type", "Error", JOptionPane.ERROR_MESSAGE); break;
					}
				}
				catch(Exception ex)
				{
					JOptionPane.showMessageDialog(getContentPane(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					System.out.println("fnf");
				}
				
			}
			// update display
			crtPanel.render();
			getContentPane().repaint();
		}
	}
        
       
	
	class SaveSnapshot implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			
			// Open up a save box and choose name
			JFileChooser fc = new JFileChooser(currentDir);
			fc.setDialogTitle("Save snapshot as...");
			
			int returnVal = fc.showSaveDialog(getContentPane() );
			
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
         		File file = fc.getSelectedFile();
         		
         		try
         		{
         			store.saveSnapshot(file.toString() );
         			
         		}
         		catch(Exception ex)
         		{
         			JOptionPane.showMessageDialog(getContentPane(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
         		}
        	}
        	getContentPane().repaint();
		}
	}
	
	class SaveAssembly implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			
			// Open up a save box and choose name
			JFileChooser fc = new JFileChooser(currentDir);
			fc.setDialogTitle("Save assembly as...");
			
			int returnVal = fc.showSaveDialog(getContentPane() );
			
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
         		File file = fc.getSelectedFile();
         		
         		try
         		{
         			store.saveAssembly(file.toString() );
         			
         		}
         		catch(Exception ex)
         		{
         			JOptionPane.showMessageDialog(getContentPane(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
         		}
        	}
        	getContentPane().repaint();
		}
	}
	
	// change to display store
	class ViewStore implements ActionListener
	{
		CrtPanel crtPanel;
		
		public ViewStore(CrtPanel aCrtPanel)
		{
			crtPanel = aCrtPanel;
		}
		
		public void actionPerformed(ActionEvent e)
		{
			crtPanel.setCrtDisplay(CrtPanel.STORE);
			switchPanel.storeSelect.setSelected(true);
		}
	}
	
	// change to display the Control
	class ViewControl implements ActionListener
	{
		CrtPanel crtPanel;
		
		public ViewControl(CrtPanel aCrtPanel)
		{
			crtPanel = aCrtPanel;
		}
		
		public void actionPerformed(ActionEvent e)
		{
			crtPanel.setCrtDisplay(CrtPanel.CONTROL);
			switchPanel.crSelect.setSelected(true);
		}
	}
	
	// change to display the accumulator
	class ViewAccumulator implements ActionListener
	{
		CrtPanel crtPanel;
		
		public ViewAccumulator(CrtPanel aCrtPanel)
		{
			crtPanel = aCrtPanel;
		}
		
		public void actionPerformed(ActionEvent e)
		{
			crtPanel.setCrtDisplay(CrtPanel.ACCUMULATOR);
			switchPanel.accSelect.setSelected(true);
		}
	}
	
	// display the switch panel (done initially by default)
	class ViewSwitchPanel implements ActionListener
	{
		
		SwitchPanel switchPanel;
		
		public ViewSwitchPanel(SwitchPanel aSwitchPanel)
		{
			switchPanel = aSwitchPanel;
		}
		
		public void actionPerformed(ActionEvent e)
		{
			switchPanel.setVisible(true);
		}
	}
	
	// open the disassembly window
	class ViewDisassembler implements ActionListener
	{
		
		Disassembler disassembler;
		
		public ViewDisassembler(Disassembler aDisassembler)
		{
			disassembler = aDisassembler;
		}
		
		public void actionPerformed(ActionEvent e)
		{
			disassembler.updateTextArea();
			disassembler.setVisible(true);
		}
	}
	
	// reset the real world elapsed time to 0
	class FpsLabelPushed implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			elapsedTime = 0;
			control.setCycleCount(0);
			updateFpsLabel();
		}
	}
	
	// display the about window
	class About implements ActionListener
	{
		public About()
		{
		}
		public void actionPerformed(ActionEvent e)
		{
			JOptionPane.showMessageDialog(getContentPane(), "Manchester Baby Simulator\n" +
			"by David Sharp\nJanuary 2001\nWith thanks to Chris Burton for his consultation\n"+
			"on historical matters.\nGUI created from pictures of the Baby remake\nby Gulzaman Khan\nAugust 2006 ","Baby", JOptionPane.INFORMATION_MESSAGE);
		}
	}        
	
	class LoadExample implements ActionListener
	{
		String fileName;
		public LoadExample(String name)
		{
	      fileName = name;
		}
		public void actionPerformed(ActionEvent e)
		{
		  try {
		  	if (fileName.equals("noodletimer.snp") || fileName.equals("Baby9.snp")) 
		  	   store.loadLocalSnapshot(fileName);
		  	else 
		           store.loadLocalModernAssembly(fileName);	
		    Baby.mainPanel.changeTexture("main.png");
		    Baby.mainPanel.repaint();
		    crtPanel.render();
		    crtPanel.repaint();
		  }
		  catch(Exception ex)
		  {
		 	JOptionPane.showMessageDialog(getContentPane(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		  }
		}
	}
	
	
}