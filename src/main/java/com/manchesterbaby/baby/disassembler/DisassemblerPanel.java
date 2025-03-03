package com.manchesterbaby.baby.disassembler;

import java.awt.*;
import java.util.StringTokenizer;

import javax.swing.*;

import com.manchesterbaby.baby.controller.CrtPanelController;
import com.manchesterbaby.baby.core.Control;
import com.manchesterbaby.baby.core.Store;
import com.manchesterbaby.baby.event.StopFlagListener;
import com.manchesterbaby.baby.event.FileLoadedListener;
import com.manchesterbaby.baby.event.CrtPanelRedrawListener;
import com.manchesterbaby.baby.utils.AppSettings;

public class DisassemblerPanel extends JPanel implements StopFlagListener, FileLoadedListener, CrtPanelRedrawListener {
    private final Store store;
    private final Control control;
    private final CrtPanelController crtPanelController;
    private final JTextArea textArea;
    private boolean updateAutomatically = true;
    private JCheckBox updateAutomaticallyCheckbox;
    private StringBuilder sb = new StringBuilder();

    public DisassemblerPanel(Store store, Control control, CrtPanelController crtPanelController) {
        this.store = store;
        this.control = control;
        this.crtPanelController = crtPanelController;

        setLayout(new BorderLayout());
        
        JPanel backPanel = new JPanel(new BorderLayout());
        
        JButton singleStep = new JButton("Step");
        JButton loadFromStore = new JButton("Load from store");
        JButton saveToStore = new JButton("Save to store");
        updateAutomaticallyCheckbox = new JCheckBox("Update");
        
        textArea = new JTextArea();
        textArea.setEditable(true);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        updateAutomatically = AppSettings.getInstance().isDisassemblerAutoUpdate();
        updateAutomaticallyCheckbox.setSelected(updateAutomatically);

        singleStep.addActionListener(e -> control.singleStep());
        loadFromStore.addActionListener(e -> updateTextArea());
        saveToStore.addActionListener(e -> updateStore());
        updateAutomaticallyCheckbox.addActionListener(e -> setAutoUpdateSetting());
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(100, 100));
        
        JPanel controlsPanel = new JPanel();
        controlsPanel.add(singleStep);
        controlsPanel.add(loadFromStore);
        controlsPanel.add(saveToStore);
        controlsPanel.add(updateAutomaticallyCheckbox);
        
        backPanel.add(controlsPanel, BorderLayout.NORTH);
        backPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(backPanel);  

        updateTextArea();

        // Register as a stop flag listener
        control.addStopFlagListener(this);
        // Register as a file loaded listener
        store.addFileLoadedListener(this);
        // Register as a CRT panel redraw listener
        crtPanelController.addRedrawListener(this);
    }

    private void setAutoUpdateSetting() {
        updateAutomatically = updateAutomaticallyCheckbox.isSelected();
        AppSettings.getInstance().setDisassemblerAutoUpdate(updateAutomatically);
        updateDisassembler();
    }

    public void updateDisassembler() {
        if (updateAutomatically) {
            updateTextArea();
        }
    }

    public void updateTextArea()
	{
        sb.setLength(0);

		int controlInstruction = control.getControlInstruction();
		
		sb.append("; CI: ");
        sb.append(controlInstruction);
        sb.append("\n");
		sb.append("; PI: ");
        sb.append(store.disassembleModern(control.getPresentInstruction()));
        sb.append("\n");
		sb.append("; ACC: ");
        sb.append(control.getAccumulator());
        sb.append("\n\n");
		// TODO: add disassembled instruction to accumulator (minus the comment and NUM stuff) so can see self modifying code getting built
		
		for(int lineNumber=0; lineNumber<32; lineNumber++)
		{
            // pad with preceeding 0's
            if(lineNumber <10)
                sb.append("0");
            sb.append(lineNumber);
			
			// identify if the line being written is the next one to be executed so it can be marked, recall that CI increments
			// immediately before executing the next instruction (hence why JMPing to line 0 executes the instriction on line 1).
            sb.append("  ");
            sb.append(store.disassembleModern( store.getLine(lineNumber), (lineNumber==controlInstruction+1) ));
            sb.append("\n");
		}
		
		textArea.setText(sb.toString());
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

    @Override
    public void onStopFlagChanged(boolean newState) {
        if (newState) {
            // Only update when the stop flag is set to true
            updateDisassembler();
        }
    }

    @Override
    public void onFileLoaded() {
        updateDisassembler();
    }

    @Override
    public void onCrtPanelRedrawn() {
        updateDisassembler();
    }
}
