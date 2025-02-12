package com.ccs.baby.menu;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.ccs.baby.utils.AppSettings;

public class SettingsMenu {
    
    public static JMenu createSettingsMenu() {
        // Create the Help menu
        JMenu settingsMenu = new JMenu("Settings");
        
        JMenuItem interactiveLoadingItem = new JCheckBoxMenuItem("Interactive Loading");
        JMenuItem showDisconnectedButtonsItem = new JCheckBoxMenuItem("Show Disconnected Buttons");
        
        settingsMenu.add(interactiveLoadingItem);
        
        // alignment of buttons when some are removed is all over the place so leave this out for now
        //settingsMenu.add(showDisconnectedButtonsItem);
        
        interactiveLoadingItem.setSelected(AppSettings.getInstance().isInteractiveLoading());
        showDisconnectedButtonsItem.setSelected(AppSettings.getInstance().isShowDisconnectedButtons());

        interactiveLoadingItem.addActionListener(e -> AppSettings.getInstance().setInteractiveLoading(interactiveLoadingItem.isSelected()));
        showDisconnectedButtonsItem.addActionListener(e -> AppSettings.getInstance().setShowDisconnectedButtons(showDisconnectedButtonsItem.isSelected()));
        
        return settingsMenu;
    }
}
