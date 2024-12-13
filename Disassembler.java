

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.util.*;


class Disassembler extends JFrame
{
	
	Store store;
	Control control;
	CrtPanel crtPanel;

	JTextArea textArea;

	public Disassembler(Store aStore, Control aControl, CrtPanel aCrtPanel)
	{
		store = aStore;
		control = aControl;
		crtPanel = aCrtPanel;
		
		// create frame
		setTitle("Disassembler");
		setSize(300, 500);
		
		Container contentPane = getContentPane();
		
		JPanel backPanel = new JPanel( new BorderLayout() );
		
		JButton loadFromStore = new JButton("Load from store");
		JButton saveToStore = new JButton("Save to store");
		
		textArea = new JTextArea();
		textArea.setEditable(true);
		textArea.setFont( new Font("Monospaced",Font.PLAIN,12) );
		
		loadFromStore.addActionListener( new UpdateTextArea() );
		saveToStore.addActionListener( new UpdateStore() );
		
		// Add scroll pane to the text area
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setPreferredSize(new Dimension(100, 100));
		
		JPanel controlsPanel = new JPanel();
		controlsPanel.add(loadFromStore);
		controlsPanel.add(saveToStore);
		
		backPanel.add(controlsPanel, BorderLayout.NORTH);
		backPanel.add(scrollPane, BorderLayout.CENTER);
	
		contentPane.add(backPanel);	
	}
	
	// disassemble every line of the store and add to the displayed text area
	public void updateTextArea()
	{
		String output = "";
		
		output += "; CI: " + control.getControlInstruction() + "\n";
		output += "; PI: " + store.disassembleModern(control.getPresentInstruction() ) + "\n";
		output += "; ACC: " + control.getAccumulator() + "\n\n";
		
		for(int lineNumber=0; lineNumber<32; lineNumber++)
		{
			String lineNumberS = "" + lineNumber;
			// pad with preceeding 0's
			while(lineNumberS.length() < 2)
				lineNumberS = "0" + lineNumberS;
			
			
			output += lineNumberS + "  " + store.disassembleModern( store.getLine(lineNumber) ) + "\n";
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
	
	// assemble the information in the store into the text area
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
			
			crtPanel.render();
			crtPanel.repaint();
		
		}
		
	}
	
}