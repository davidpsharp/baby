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

	private final DisassemblerPanel panel;
    private static Disassembler instance;
    private static JPanel tabPanel;

	public Disassembler(Store store, Control control, CrtPanelController crtPanelController, JFrame parentFrame)
	{
		super(parentFrame);

		this.store = store;
		this.control = control;
		this.crtPanelController = crtPanelController;

		panel = new DisassemblerPanel(store, control, crtPanelController);

		// create frame
		setTitle("Disassembler");
		setSize(400, 700); // height 630 was fine on Mac, closer to 700 on Windows 10 to fit all text
		
		setContentPane(panel);

		// add controls menu so single step hotkey works when disassembler window has focus
		JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
		menuBar.add(ControlsMenu.createControlsMenu(control, menuBar));

	}

	public JPanel getTabPanel() {
        if (tabPanel == null) {
            tabPanel = new DisassemblerPanel(store, control, crtPanelController);
        }
        return tabPanel;
    }

	public void updateDisassemblerOnStep()
	{
		panel.updateDisassemblerOnStep();
        if (tabPanel != null) {
            ((DisassemblerPanel)tabPanel).updateDisassemblerOnStep();
        }
	}

	public void updateTextArea() {
		panel.updateTextArea();
	}
}