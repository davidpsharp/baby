package com.ccs.baby.disassembler;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import com.ccs.baby.core.Store;
import com.ccs.baby.menu.ControlsMenu;
import com.ccs.baby.core.Control;
import com.ccs.baby.controller.CrtPanelController;

public class Disassembler extends JDialog
{
	
	Store store;
	Control control;
	CrtPanelController crtPanelController;

	JTextArea textArea;

	boolean _updateOnStep = true;


	public Disassembler(Store store, Control control, CrtPanelController crtPanelController, JFrame parentFrame)
	{
		super(parentFrame);

		this.store = store;
		this.control = control;
		this.crtPanelController = crtPanelController;

		// create frame
		setTitle("Disassembler");
		setSize(400, 700); // height 630 was fine on Mac, closer to 700 on Windows 10 to fit all text
		
		Container contentPane = getContentPane();
		
		JPanel backPanel = new JPanel( new BorderLayout() );
		
		JButton loadFromStore = new JButton("Load from store");
		JButton saveToStore = new JButton("Save to store");
		JCheckBox updateOnStep = new JCheckBox("Update");
		
		textArea = new JTextArea();
		textArea.setEditable(true);
		textArea.setFont( new Font("Monospaced",Font.PLAIN,12) );
		
		loadFromStore.addActionListener( new UpdateTextArea() );
		saveToStore.addActionListener( new UpdateStore() );
		updateOnStep.addActionListener( new ChangeUpdateOnStep() );
		
		// Add scroll pane to the text area
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setPreferredSize(new Dimension(100, 100));
		
		JPanel controlsPanel = new JPanel();
		controlsPanel.add(loadFromStore);
		controlsPanel.add(saveToStore);
		controlsPanel.add(updateOnStep);
		
		backPanel.add(controlsPanel, BorderLayout.NORTH);
		backPanel.add(scrollPane, BorderLayout.CENTER);
	
		contentPane.add(backPanel);

		updateOnStep.setSelected(_updateOnStep);

		// add controls menu so single step hotkey works when disassembler window has focus
		JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
		menuBar.add(ControlsMenu.createControlsMenu(control, menuBar));

	}

	public void updateDisassemblerOnStep()
	{
		if(_updateOnStep)
			updateTextArea();
	}

	// disassemble every line of the store and add to the displayed text area
	public void updateTextArea()
	{
		String output = "";

		int controlInstruction = control.getControlInstruction();
		
		output += "; CI: " + controlInstruction + "\n";
		output += "; PI: " + store.disassembleModern(control.getPresentInstruction() ) + "\n";
		output += "; ACC: " + control.getAccumulator() + "\n\n";
		// TODO: add disassembled instruction to accumulator (minus the comment and NUM stuff) so can see self modifying code getting built
		
		for(int lineNumber=0; lineNumber<32; lineNumber++)
		{
			String lineNumberS = "" + lineNumber;
			// pad with preceeding 0's
			while(lineNumberS.length() < 2)
				lineNumberS = "0" + lineNumberS;
			
			// identify if the line being written is the next one to be executed so it can be marked, recall that CI increments
			// immediately before executing the next instruction (hence why JMPing to line 0 executes the instriction on line 1).
			output += lineNumberS + "  " + store.disassembleModern( store.getLine(lineNumber), (lineNumber==controlInstruction+1) ) + "\n";
		}
		
		textArea.setText(output);
	}
	

	class UpdateTextArea implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			updateTextArea();			
		}
	}
	
	// assemble the information in the text area into the Store
	class UpdateStore implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			String fullText = textArea.getText();
			
			// create a new tokenizer, tokenizing on newlines
			StringTokenizer tokenizer = new StringTokenizer(fullText, "\n");
			
			try
			{
				// any exception will cause drop out of while loop and be handled
				int tokenCounter = 0;
				while(tokenizer.hasMoreElements() )
				{
					tokenCounter++;
					store.assembleModernToStore(tokenizer.nextToken(), tokenCounter);
				}
			}
			catch(Exception ex)
			{
				JOptionPane.showMessageDialog(getContentPane(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
			
			crtPanelController.redrawCrtPanel();
		}
	}

	class ChangeUpdateOnStep implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			_updateOnStep = ((JCheckBox)e.getSource()).isSelected();

		}
	}
	
}