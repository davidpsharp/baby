package com.manchesterbaby.baby.disassembler;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.manchesterbaby.baby.controller.CrtPanelController;
import com.manchesterbaby.baby.core.Control;
import com.manchesterbaby.baby.core.Store;
import com.manchesterbaby.baby.menu.ControlsMenu;
import com.manchesterbaby.baby.manager.AnimationManager;

import java.util.*;

public class Disassembler extends JDialog
{
	
	Store store;
	Control control;
	CrtPanelController crtPanelController;
	AnimationManager animationManager;

	private final DisassemblerPanel panel;
    private static Disassembler instance;
    private static JPanel tabPanel;

	public Disassembler(Store store, Control control, CrtPanelController crtPanelController, AnimationManager animationManager, JFrame parentFrame)
	{
		super(parentFrame);

		this.store = store;
		this.control = control;
		this.crtPanelController = crtPanelController;
		this.animationManager = animationManager;

		panel = new DisassemblerPanel(store, control, crtPanelController, animationManager);

		// create frame
		setTitle("Disassembler");
		setSize(450, 750); // height 630 was fine on Mac, closer to 700 on Windows 10 to fit all text
		
		setContentPane(panel);

		// add controls menu so single step hotkey works when disassembler window has focus
		JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
		menuBar.add(ControlsMenu.createControlsMenu(control, menuBar));

	}

	public JPanel getTabPanel() {
        if (tabPanel == null) {
            tabPanel = new DisassemblerPanel(store, control, crtPanelController, animationManager);
        }
        return tabPanel;
    }

	public void updateDisassembler()
	{
		panel.updateDisassembler();
        if (tabPanel != null) {
            ((DisassemblerPanel)tabPanel).updateDisassembler();
        }
	}

	public void updateTextArea() {
		panel.updateTextArea();
	}
}