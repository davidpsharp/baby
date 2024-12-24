package com.ccs.baby.ui;

import javax.swing.*;
import java.awt.image.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.event.*;
import javax.imageio.*;  
import javax.swing.JFrame;

import com.ccs.baby.core.Store;
import com.ccs.baby.core.Baby;
import com.ccs.baby.core.Control;

public class CrtPanel extends JPanel
{

	// values for different tubes to be displayed on monitor
	public static final int STORE = 0;
	public static final int CONTROL = 1;
	public static final int ACCUMULATOR = 2;
	
	// note, was 291x291 for raster area before circular border
	public static final int crtWidth = 400;
	public static final int crtHeight = 290;
	
	// number of pixels horizontally and vertically that the
	// grid of CRT dots is draw from the top left of crtpanel
	public static final int rasterOffsetx = 200;
	public static final int rasterOffsety = 95;
	
	// length of a bit as appears on the crt
	public static final int bitLength = 6;
	
	public static final int spacing = 3;

	// current display type
	private int currentDisplay = STORE;	
	
	// whether to render the Control and Accumulator as the Baby did
	// or as M1SIM incorrectly did - now obsolete, always do it as
	// the Baby did.
	private boolean renderAccurately = true;
	
	// icon file names
	static String bitOneFileName = "/images/bit1.gif";
	static String bitZeroFileName = "/images/bit0.gif";
	static String bitOneBrightFileName = "/images/bit1bright.gif";
	static String bitZeroBrightFileName = "/images/bit0bright.gif";
	
	private Store store;
	private Control control;
	
	// current highlighted action line
	private int actionLine = 0;
	
	// the images are all 7x3 GIFs
	private Image bit1;
	private Image bit0;
	private Image bit1bright;
	private Image bit0bright;
	
	// display
	private BufferedImage bi = new BufferedImage(crtWidth, crtHeight, BufferedImage.TYPE_INT_ARGB);

	// background colour, same as switchpanel
	private Color backgroundFill = SwitchPanel.backgroundColor;

	// constructor
	public CrtPanel(Store aStore, Control aControl)
	{
		
		store = aStore;
		control = aControl;
		
		bit1 = loadImage(bitOneFileName);
		bit0 = loadImage(bitZeroFileName);
		bit1bright = loadImage(bitOneBrightFileName);
		bit0bright = loadImage(bitZeroBrightFileName);
	
		render();
		repaint();
	}
	
	private Image loadImage(String image) {
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
        return Toolkit.getDefaultToolkit().createImage(buf);
      } 
      else {
        System.err.println("Couldn't find file: " + image);
        return null;
      }
    }
    
    
	
	// change the tube displayed
	public void setCrtDisplay(int display)
	{
		int beforeDisplay = currentDisplay;
		
		for(int x=0; x<32; x++)
			store.lineAltered[x] = true;

		switch(display)
		{
			case STORE			:
			case ACCUMULATOR	:
			case CONTROL		: currentDisplay = display; break;
			default				: currentDisplay = STORE; break;
		}
		
		// if changed display then redraw
		if(currentDisplay != beforeDisplay)
		{
			
			// set so all lines need redrawing
			for(int lineNumber=0; lineNumber<32; lineNumber++)
				store.lineAltered[lineNumber] = true;
								
			render();
			repaint();
		}
	}
	
	public int getCrtDisplay()
	{
		return currentDisplay;
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		update(g);
	}
	
	// only redraw the amount of screen necessary
	public synchronized void efficientRepaint()
	{
		switch(currentDisplay)
		{
			// only redraw the store lines that have changed
			case STORE			:	for(int lineNumber=0; lineNumber<31; lineNumber++)
									{
										if(store.lineAltered[lineNumber])
										{
											repaint(0, spacing+lineNumber*bitLength,crtWidth, spacing+((lineNumber+1)*bitLength) );
											store.lineAltered[lineNumber] = false;
										}
									}			
									break;
			// redraw top line only
			case ACCUMULATOR	: 	if(renderAccurately) repaint();
									else repaint(0,0,crtWidth,3+bitLength);
									break;
			// redraw top 2 lines only
			case CONTROL		:	if(renderAccurately) repaint();
									else repaint(0,0,crtWidth,3+bitLength+bitLength);
									break;
			// redraw entire display
			default				: repaint(); break;
		}
	}

	// draw the image to the graphics context
	public void update(Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;
		
		g2.drawImage(bi, 0, 0, this);
	}
	
	public void setActionLine(int newActionLine)
	{
		// negative values turn off the action line
		if(newActionLine < 0)
		{
			// wipe out old action line
			if(actionLine>0 && actionLine<32)
				store.lineAltered[actionLine] = true;
			actionLine = newActionLine;
		}
		else
		{
			if(actionLine != newActionLine)
			{
				// old actionLine could be negative if turned off
				if(actionLine>0 && actionLine<32)
					store.lineAltered[actionLine] = true;
				// store new action line
				actionLine = newActionLine;
				// set to draw new action line
				store.lineAltered[actionLine] = true;
				render();
				repaint();
			}
		}
	}
	
	// render the graphics to the buffered image
	public synchronized void render()
	{		
		switch(currentDisplay)
		{
			case STORE			: renderStore(); break;
			case ACCUMULATOR	: renderAccumulator(); break;
			case CONTROL		: renderControl(); break;
			default				: renderStore(); break;
		}
	}
	
	// to be run once at start of program, draws circular border
	private void initialRender()
	{
		Graphics2D big = bi.createGraphics();
		
		big.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//Color c = new Color(0f, 1f, 0f, 0.1f);
		//big.drawImage(mImage, 0, 0, null);
		//big.drawImage()
		//big.drawRenderedImage(mImage, AfineTransform.TYPE_IDENTITY);
		
		//big.clearRect(0, 0, crtWidth, crtHeight);
		
		big.setColor(Color.black);
		
		big.fillOval(0,0,crtWidth,crtHeight);

	}
	
	// renders the Store to the buffered image	
	private void renderStore()
	{	
		Graphics2D big = bi.createGraphics();
		
		for(int lineNumber = 0; lineNumber<32; lineNumber++)
		{
			// for each line that has been changed since last redraw
			if(store.lineAltered[lineNumber])
			{
				// if redrawing the action line then draw it brighter
				if(actionLine == lineNumber)
				{
					drawActionDataAtLine(store.getLine(lineNumber), lineNumber, big);
				}
				// otherwise draw a normal line
				else
				{
					drawDataAtLine(store.getLine(lineNumber), lineNumber, big);
				}
			}
		}		
	}
	
	// render control
	public void renderControl()
	{
		Graphics2D big = bi.createGraphics();
		
		// if rendering accurately repeat display all the way down
		if(renderAccurately)
		{
			// if the baby is running then PI exists so draw it 
			if(Baby.running)
			{
				// if baby is running then CI and PI are displayed
				for(int line=0; line<32; line+=2)
				{
					drawDataAtLine(control.getControlInstruction(), line, big);
					drawDataAtLine(control.getPresentInstruction(), line+1, big);
				}
			}
			else
			{
				// when not running PI doesn't exist so only display CI
				for(int line=0; line<32; line++)
				{
					drawDataAtLine(control.getControlInstruction(), line, big);
				}
			}
		}
		else
		{
			// otherwise display both CI and PI but only in top two lines
			drawDataAtLine(control.getControlInstruction(), 0, big);
			drawDataAtLine(control.getPresentInstruction(), 1, big);
		}
	}
	
	// render accumulator
	public void renderAccumulator()
	{
		Graphics2D big = bi.createGraphics();
	
		drawDataAtLine(control.getAccumulator(), 0, big);
		
		// if rendering accuraterly draw accumulator on all lines
		if(renderAccurately)
		{
			for(int line = 1; line<32; line++)
				drawDataAtLine(control.getAccumulator(), line, big);
		}
	}
	
	// draw the given data for a line at the specified position on the specified graphics context
	private void drawDataAtLine(int data, int line, Graphics2D graphicsContext)
	{
		for(int bitNumber = 31; bitNumber>=0; bitNumber--)
		{
			// if bit set then
			if( ((data>>bitNumber) & 1) == 1)
			{
				graphicsContext.drawImage(bit1, rasterOffsetx + spacing + bitNumber*bitLength, rasterOffsety + spacing + line*bitLength, null);
			}
			else
			{
				graphicsContext.drawImage(bit0, rasterOffsetx + spacing + bitNumber*bitLength, rasterOffsety + spacing + line*bitLength, null);
			}
		}
	}
	
	// same as drawDataAtLine() but for the brighter action line
	private void drawActionDataAtLine(int data, int line, Graphics2D graphicsContext)
	{
		for(int bitNumber = 31; bitNumber>=0; bitNumber--)
		{
			// if bit set then
			if( ((data>>bitNumber) & 1) == 1)
			{
				graphicsContext.drawImage(bit1bright, rasterOffsetx + spacing + bitNumber*bitLength, rasterOffsety + spacing + line*bitLength, null);
			}
			else
			{
				graphicsContext.drawImage(bit0bright, rasterOffsetx + spacing + bitNumber*bitLength, rasterOffsety + spacing + line*bitLength, null);
			}
		}
	}
	
}