package com.ccs.baby.disassembler;

import java.awt.*;
import java.util.StringTokenizer;

import javax.swing.*;

import com.ccs.baby.core.Store;
import com.ccs.baby.core.Control;
import com.ccs.baby.controller.CrtPanelController;

public class DisassemblerPanel extends JPanel {
    private final Store store;
    private final Control control;
    private final CrtPanelController crtPanelController;
    private final JTextArea textArea;
    private boolean updateOnStep = true;

    public DisassemblerPanel(Store store, Control control, CrtPanelController crtPanelController) {
        this.store = store;
        this.control = control;
        this.crtPanelController = crtPanelController;

        setLayout(new BorderLayout());
        
        JPanel backPanel = new JPanel(new BorderLayout());
        
        JButton singleStep = new JButton("Step");
        JButton loadFromStore = new JButton("Load from store");
        JButton saveToStore = new JButton("Save to store");
        JCheckBox updateOnStep = new JCheckBox("Update");
        
        textArea = new JTextArea();
        textArea.setEditable(true);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        singleStep.addActionListener(e -> control.singleStep());
        loadFromStore.addActionListener(e -> updateTextArea());
        saveToStore.addActionListener(e -> updateStore());
        updateOnStep.addActionListener(e -> this.updateOnStep = updateOnStep.isSelected());
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(100, 100));
        
        JPanel controlsPanel = new JPanel();
        controlsPanel.add(singleStep);
        controlsPanel.add(loadFromStore);
        controlsPanel.add(saveToStore);
        controlsPanel.add(updateOnStep);
        
        backPanel.add(controlsPanel, BorderLayout.NORTH);
        backPanel.add(scrollPane, BorderLayout.CENTER);
    
        add(backPanel);

        updateOnStep.setSelected(this.updateOnStep);

        updateTextArea();
    }

    public void updateDisassemblerOnStep() {
        if (updateOnStep) {
            updateTextArea();
        }
    }

    public void updateTextArea()
	{
		String output = "";

		// TODO: would be cool if this updated as interactive loading goes on for each line loaded so can see program getting entered

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

    private void updateStore() {
        String fullText = textArea.getText();
        
        // create a new tokenizer, tokenizing on newlines
        StringTokenizer tokenizer = new StringTokenizer(fullText, "\n");
        
        boolean assemblyErrors = false;

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
            assemblyErrors = true;
            JOptionPane.showMessageDialog(null, ex.getMessage(), "updateStore Error", JOptionPane.ERROR_MESSAGE);
        }
        
        crtPanelController.redrawCrtPanel();

        // if assembled without issue then disassemble again from the store so that the comments
        // update when assembled instruction alters them
        if(!assemblyErrors)
            updateTextArea();
    }

}
