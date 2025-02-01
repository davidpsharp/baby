package com.ccs.baby.menu;

import com.ccs.baby.core.Store;
import com.ccs.baby.core.Control;

import com.ccs.baby.ui.CrtPanel;
import com.ccs.baby.ui.CrtControlPanel;
import com.ccs.baby.disassembler.Disassembler;
import com.ccs.baby.debug.DebugPanel;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

public class MenuSetup {

    /**
     * Sets up the menus.
     *
     * @param menuBar         the menu bar
     * @param store           the store object
     * @param control         the control object
     * @param crtPanel        the CRT panel object
     * @param crtControlPanel the CRT control panel object
     * @param disassembler    the disassembler object
     * @param currentDir      the current directory path
     * @param debugPanel      the debug panel object
     */
    public MenuSetup(JMenuBar menuBar, Store store, Control control, CrtPanel crtPanel, CrtControlPanel crtControlPanel, Disassembler disassembler, String currentDir, JFrame frame, DebugPanel debugPanel) {
        menuBar.add(FileMenu.createFileMenu(store, control, currentDir, frame, crtPanel));
        menuBar.add(ControlsMenu.createControlsMenu(frame, control, menuBar));
        menuBar.add(ViewMenu.createViewMenu(crtPanel, crtControlPanel, disassembler, debugPanel));
        menuBar.add(ExamplesMenu.createExampleMenu(store, crtPanel, frame));
        menuBar.add(HelpMenu.createHelpMenu(frame));
    }

}
