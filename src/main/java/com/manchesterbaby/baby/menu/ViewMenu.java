package com.manchesterbaby.baby.menu;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.KeyStroke;

import com.manchesterbaby.baby.controller.CrtPanelController;
import com.manchesterbaby.baby.core.Baby;
import com.manchesterbaby.baby.disassembler.Disassembler;
import com.manchesterbaby.baby.manual.ReferenceManual;
import com.manchesterbaby.baby.ui.DebugPanel;
import com.manchesterbaby.baby.ui.CrtPanel.DisplayType;
import com.manchesterbaby.baby.ui.display.DisplayDebugPanel;
import com.manchesterbaby.baby.ui.display.DisplayDisassemblerWindow;
import com.manchesterbaby.baby.utils.AppSettings;
import com.manchesterbaby.baby.utils.CheerpJUtils;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.event.KeyEvent;

public class ViewMenu {

    private static JFrame _parentFrame;

    /**
     * Create the View menu
     *
     * @param crtPanelController the CRT control panel to control the CRT panel
     * @param disassembler    the disassembler to display the disassembled code
     * @param debugPanel      the debug panel to display the debug information
     * @return the View menu
     */
    public static JMenu createViewMenu(
            CrtPanelController crtPanelController,
            Disassembler disassembler,
            DebugPanel debugPanel,
            JFrame parentFrame
    ) {
        _parentFrame = parentFrame;

        // Create the View menu
        JMenu viewMenu = new JMenu("View");

        // Create menu items
        JMenuItem viewStore = new JMenuItem("Store");
        JMenuItem viewControl = new JMenuItem("Control");
        JMenuItem viewAccumulator = new JMenuItem("Accumulator");
        JMenuItem viewDisassemblerWindow = new JMenuItem("Disassembler");
        JCheckBoxMenuItem viewDisassemblerTab = new JCheckBoxMenuItem("Disassembler Tab");
        JMenuItem viewDebugPanel = new JCheckBoxMenuItem("Debug");
        JMenuItem viewRefreshView = new JMenuItem("Refresh View");

        // Add action listeners for each item
        viewStore.addActionListener(e -> crtPanelController.setCrtDisplay(DisplayType.STORE));
        viewControl.addActionListener(e -> crtPanelController.setCrtDisplay(DisplayType.CONTROL));
        viewAccumulator.addActionListener(e -> crtPanelController.setCrtDisplay(DisplayType.ACCUMULATOR));

        if(CheerpJUtils.onCheerpj()) {
        
            
            viewDisassemblerTab.addActionListener(e -> {
                JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
                if (item.isSelected()) {
                    ((Baby) _parentFrame).addDisassemblerTab(disassembler);
                } else {
                    ((Baby) _parentFrame).removeDisassemblerTab(disassembler);
                }
            });
            
        }
        else
        {
            viewDisassemblerWindow.addActionListener(new DisplayDisassemblerWindow(disassembler));
        }
        viewDebugPanel.addActionListener(new DisplayDebugPanel(debugPanel));
        viewRefreshView.addActionListener(e -> parentFrame.repaint());

        // Add mnemonics (keyboard shortcuts) for macOS, Windows, and Linux
        viewMenu.setMnemonic(KeyEvent.VK_V); // Alt + V
        viewStore.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.ALT_DOWN_MASK | KeyEvent.CTRL_DOWN_MASK)); // Alt + Ctrl + S
        viewControl.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.ALT_DOWN_MASK | KeyEvent.CTRL_DOWN_MASK)); // Alt + Ctrl + C
        viewAccumulator.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.ALT_DOWN_MASK | KeyEvent.CTRL_DOWN_MASK)); // Alt + Ctrl + A
        viewDebugPanel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.ALT_DOWN_MASK)); // Alt + B

        // Add items to the view menu
        viewMenu.add(viewStore);
        viewMenu.add(viewControl);
        viewMenu.add(viewAccumulator);
        viewMenu.addSeparator();
        if(CheerpJUtils.onCheerpj()) {
            viewMenu.add(viewDisassemblerTab);
            viewDisassemblerTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.ALT_DOWN_MASK)); // Alt + T
        }
        else {  
            viewMenu.add(viewDisassemblerWindow);
            viewDisassemblerWindow.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.ALT_DOWN_MASK)); // Alt + D
        }
        viewMenu.addSeparator();
        viewMenu.add(viewDebugPanel);
        
        // this feature is only needed on CheerpJ when zooming certain browsers it doesn't redraw properly
        // doesn't seem to work reliably though to address redraw issue
        if(CheerpJUtils.onCheerpj()) {
            viewRefreshView.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0)); // F1 // TODO: find a shortcut that works on all platforms esp chrome windows
            viewMenu.add(viewRefreshView);
        }

        viewDebugPanel.setSelected(AppSettings.getInstance().isShowDebugPanel());

        return viewMenu;
    }
}