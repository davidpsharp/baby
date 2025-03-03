package com.manchesterbaby.baby.menu;

import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import com.manchesterbaby.baby.core.Control;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;



public class ControlsMenu {

    
    /**
     * Creates the Controls menu.
     *
     * @param control the control object
     * @param menuBar the menu bar
     * @return the Help menu
     */
    public static JMenu createControlsMenu(Control control, JMenuBar menuBar) {

        // Create the Help menu
        JMenu controlsMenu = new JMenu("Controls");  // is 'Run' or 'Start' better for first time users

        // Create menu items
        JMenuItem stepItem = new JMenuItem("Single Step");
        JMenuItem runItem = new JMenuItem("Run");
        JMenuItem stopItem = new JMenuItem("Stop");

        // Add action listeners
        stepItem.addActionListener(e -> control.singleStep());
        runItem.addActionListener(e -> control.startRunning());
        stopItem.addActionListener(e -> control.stopRunning());

        // Set up global single step on F10 without using menu accelerator as it doesn't auto-repeat very quickly if pressed over and over which
        // you commonly want to do when single-stepping a program, presumably due to menu redraw or something. With this approach you can just
        // hold it down and it will step through the program.
        SingleStepAction ssa = new SingleStepAction(control);
        String SINGLE_STEP_KEY = "singleStep";
        menuBar.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0), SINGLE_STEP_KEY);
        menuBar.getActionMap().put(SINGLE_STEP_KEY, ssa);

        // set up accelerators
        controlsMenu.setMnemonic(KeyEvent.VK_C); // Alt + C
        runItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0)); // F5
        stopItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, KeyEvent.SHIFT_DOWN_MASK)); // Shift + F5
        // do not set an accelerator for stepItem as it doesn't auto-repeat well if done from menu accelerators

        // Add items to the menu
        controlsMenu.add(stepItem);
        controlsMenu.add(runItem);
        controlsMenu.add(stopItem);

        return controlsMenu;
    }

}
    class SingleStepAction extends AbstractAction {

        private Control _control;

        public SingleStepAction(Control control)
        {
            _control = control;
        }

        @Override
        public void actionPerformed(java.awt.event.ActionEvent ae) {
            _control.singleStep();            
        }
    }  

