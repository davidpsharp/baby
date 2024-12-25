package com.ccs.baby.menu;

import com.ccs.baby.core.Baby;
import com.ccs.baby.io.LoadExample;
import com.ccs.baby.io.LoadSnapshotAssembly;
import com.ccs.baby.io.SaveAssembly;
import com.ccs.baby.io.SaveSnapshot;
import com.ccs.baby.ui.ViewAccumulator;
import com.ccs.baby.ui.ViewControl;
import com.ccs.baby.ui.ViewDisassembler;
import com.ccs.baby.ui.ViewStore;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import com.ccs.baby.core.Store;
import com.ccs.baby.core.Control;
import com.ccs.baby.ui.CrtPanel;
import com.ccs.baby.ui.SwitchPanel;
import com.ccs.baby.disassembler.Disassembler;

import com.ccs.baby.menu.AboutMenu;

public class MenuSetup {

    private final JFrame frame;

    public MenuSetup(JMenuBar m, Store store, Control control, CrtPanel crtPanel, SwitchPanel switchPanel, Disassembler disassembler, String currentDir, JFrame frame) {
        this.frame = frame;
        setupMenus(m, store, control, crtPanel, switchPanel, disassembler, currentDir);
    }

    public void setupMenus(JMenuBar m, Store store, Control control, CrtPanel crtPanel, SwitchPanel switchPanel, Disassembler disassembler, String currentDir) {

        // create 3 menus
        JMenu fileMenu = new JMenu("File");
        JMenu viewMenu = new JMenu("View");
        JMenu exampleMenu = new JMenu("Examples");
        JMenu helpMenu = new JMenu("Help");

        // file menu
        JMenuItem loadSnapshotAssembly = new JMenuItem("Load snapshot/assembly");
        JMenuItem saveSnapshot = new JMenuItem("Save snapshot");
        JMenuItem saveAssembly = new JMenuItem("Save assembly");
        JMenuItem close = new JMenuItem("Close");
        JMenuItem refManual = new JMenuItem("Reference Manual");

        // examples menu
        JMenuItem diffeqt = new JMenuItem("demos/diffeqt.asm");
        JMenuItem baby9 = new JMenuItem("demos/Baby9.snp");
        JMenuItem primegen = new JMenuItem("demos/primegen.asm");
        JMenuItem virpet = new JMenuItem("demos/virpet.asm");
        JMenuItem noodleTimer = new JMenuItem("demos/noodletimer.snp");

        // view menu
        JMenuItem viewStore = new JMenuItem("Store");
        JMenuItem viewControl = new JMenuItem("Control");
        JMenuItem viewAccumulator = new JMenuItem("Accumulator");
        //JMenuItem viewSwitchPanel = new JMenuItem("Switch Panel");
        JMenuItem viewDisassembler = new JMenuItem("Disassembler");

        // help menu
        JMenuItem about = new JMenuItem("About");

        // add action listeners for each item
        loadSnapshotAssembly.addActionListener(new LoadSnapshotAssembly(store, control, frame));
        saveSnapshot.addActionListener(new SaveSnapshot(currentDir, store, frame));
        saveAssembly.addActionListener(new SaveAssembly(currentDir, store, frame));

        diffeqt.addActionListener(new LoadExample("demos/diffeqt.asm", store, crtPanel, frame));
        baby9.addActionListener(new LoadExample("demos/Baby9.snp", store, crtPanel, frame));
        primegen.addActionListener(new LoadExample("demos/primegen.asm", store, crtPanel, frame));
        virpet.addActionListener(new LoadExample("demos/virpet.asm", store, crtPanel, frame));
        noodleTimer.addActionListener(new LoadExample("demos/noodletimer.snp", store, crtPanel, frame));


        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        viewStore.addActionListener(new ViewStore(crtPanel, switchPanel));
        viewControl.addActionListener(new ViewControl(crtPanel, switchPanel));
        viewAccumulator.addActionListener(new ViewAccumulator(crtPanel, switchPanel));
        //viewSwitchPanel.addActionListener(new ViewSwitchPanel(switchPanel));
        viewDisassembler.addActionListener(new ViewDisassembler(disassembler));


        about.addActionListener(new AboutMenu(frame));

        // set the keyboard click equivalent for each menu item
        loadSnapshotAssembly.setMnemonic(KeyEvent.VK_L);

        // add items to file menu
        fileMenu.add(loadSnapshotAssembly);
        fileMenu.add(saveSnapshot);
        fileMenu.add(saveAssembly);
        fileMenu.add(close);

        // add items to view menu
        viewMenu.add(viewStore);
        viewMenu.add(viewControl);
        viewMenu.add(viewAccumulator);
        //viewMenu.add(viewSwitchPanel);
        viewMenu.add(viewDisassembler);

        exampleMenu.add(diffeqt);
        exampleMenu.add(baby9);
        exampleMenu.add(primegen);
        exampleMenu.add(virpet);
        exampleMenu.add(noodleTimer);

        // add items to help menu
        //helpMenu.add(refManual);
        helpMenu.add(about);


        // add menus to bar
        m.add(fileMenu);
        m.add(viewMenu);
        m.add(exampleMenu);
        m.add(helpMenu);
    }


}
