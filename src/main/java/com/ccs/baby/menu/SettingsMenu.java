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
        


        JMenu disassemblerSettingsMenu = new JMenu("Disassembler");
        
        JCheckBoxMenuItem dissNumBin = new JCheckBoxMenuItem("NUM as Binary");
        JCheckBoxMenuItem dissNumHex = new JCheckBoxMenuItem("NUM as Hex");
        JCheckBoxMenuItem dissNumDec = new JCheckBoxMenuItem("NUM as Decimal");
        JCheckBoxMenuItem dissNumLSBBin = new JCheckBoxMenuItem("NUM as LSB-first Binary");

        disassemblerSettingsMenu.add(dissNumBin);
        disassemblerSettingsMenu.add(dissNumHex);
        disassemblerSettingsMenu.add(dissNumDec);
        disassemblerSettingsMenu.add(dissNumLSBBin);

        // Set initial state based on current settings
        String currentFormat = AppSettings.getInstance().getNumDissFormat();
        dissNumBin.setSelected(currentFormat.equals("bin"));
        dissNumHex.setSelected(currentFormat.equals("hex"));
        dissNumDec.setSelected(currentFormat.equals("dec"));
        dissNumLSBBin.setSelected(currentFormat.equals("lsbbin"));

        dissNumBin.addActionListener(e -> {
            if (dissNumBin.isSelected()) {
                dissNumHex.setSelected(false);
                dissNumDec.setSelected(false);
                dissNumLSBBin.setSelected(false);
                AppSettings.getInstance().setNumDissFormat("bin");
            }
        });
        dissNumHex.addActionListener(e -> {
            if (dissNumHex.isSelected()) {
                dissNumBin.setSelected(false);
                dissNumDec.setSelected(false);
                dissNumLSBBin.setSelected(false);
                AppSettings.getInstance().setNumDissFormat("hex");
            }
        });
        dissNumDec.addActionListener(e -> {
            if (dissNumDec.isSelected()) {
                dissNumBin.setSelected(false);
                dissNumHex.setSelected(false);
                dissNumLSBBin.setSelected(false);
                AppSettings.getInstance().setNumDissFormat("dec");
            }
        });
        dissNumLSBBin.addActionListener(e -> {
            if (dissNumLSBBin.isSelected()) {
                dissNumBin.setSelected(false);
                dissNumHex.setSelected(false);
                dissNumDec.setSelected(false);
                AppSettings.getInstance().setNumDissFormat("lsbbin");
            }
        });

        settingsMenu.add(disassemblerSettingsMenu);

        return settingsMenu;
    }

}
