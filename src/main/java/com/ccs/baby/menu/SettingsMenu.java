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
        
        settingsMenu.add(interactiveLoadingItem);

        interactiveLoadingItem.setSelected(AppSettings.getInstance().isInteractiveLoading());

        interactiveLoadingItem.addActionListener(e -> AppSettings.getInstance().setInteractiveLoading(interactiveLoadingItem.isSelected()));
        
        return settingsMenu;
    }
}
