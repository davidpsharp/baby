package com.manchesterbaby.baby.menu;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

import com.manchesterbaby.baby.controller.CrtPanelController;
import com.manchesterbaby.baby.core.Control;
import com.manchesterbaby.baby.core.Store;
import com.manchesterbaby.baby.disassembler.Disassembler;
import com.manchesterbaby.baby.ui.DebugPanel;

public class MenuSetup {

    /**
     * Sets up the menus.
     *
     * @param menuBar         the menu bar
     * @param store           the store object
     * @param control         the control object
     * @param crtPanelController the CRT panel object
     * @param disassembler    the disassembler object
     * @param currentDir      the current directory path
     * @param debugPanel      the debug panel object
     */
    public MenuSetup(JMenuBar menuBar, Store store, Control control, CrtPanelController crtPanelController, Disassembler disassembler, String currentDir, JFrame frame, DebugPanel debugPanel) {
        menuBar.add(FileMenu.createFileMenu(store, currentDir, frame, crtPanelController));
        menuBar.add(ControlsMenu.createControlsMenu(control, menuBar));
        menuBar.add(ViewMenu.createViewMenu(crtPanelController, disassembler, debugPanel, frame));
        menuBar.add(ExamplesMenu.createExampleMenu(store, crtPanelController, frame));
        menuBar.add(SettingsMenu.createSettingsMenu());
        menuBar.add(HelpMenu.createHelpMenu(frame));
    }

}
