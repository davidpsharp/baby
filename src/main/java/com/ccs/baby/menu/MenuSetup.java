package com.ccs.baby.menu;

import com.ccs.baby.core.Store;
import com.ccs.baby.core.Control;

import com.ccs.baby.ui.CrtPanel;
import com.ccs.baby.ui.SwitchPanel;
import com.ccs.baby.disassembler.Disassembler;
import com.ccs.baby.debug.DebugPanel;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

public class MenuSetup {
    private final JFrame frame;

    public MenuSetup(JMenuBar m, Store store, Control control, CrtPanel crtPanel, SwitchPanel switchPanel, Disassembler disassembler, String currentDir, JFrame frame, DebugPanel debugPanel) {
        this.frame = frame;
        setupMenus(m, store, control, crtPanel, switchPanel, disassembler, currentDir, debugPanel);
    }

    /**
     * Sets up the menus.
     *
     * @param menuBarItem  the menu bar item
     * @param store        the store object
     * @param control      the control object
     * @param crtPanel     the CRT panel object
     * @param switchPanel  the switch panel object
     * @param disassembler the disassembler object
     * @param currentDir   the current directory path
     * @param debugPanel   the debug panel object
     */
    public void setupMenus(JMenuBar menuBarItem, Store store, Control control, CrtPanel crtPanel, SwitchPanel switchPanel, Disassembler disassembler, String currentDir, DebugPanel debugPanel) {
        menuBarItem.add(FileMenu.createFileMenu(store, control, currentDir, frame));
        menuBarItem.add(ViewMenu.createViewMenu(crtPanel, switchPanel, disassembler, debugPanel));
        menuBarItem.add(ExamplesMenu.createExampleMenu(store, crtPanel, frame));
        menuBarItem.add(HelpMenu.createHelpMenu(frame));
    }
}
