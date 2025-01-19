package com.ccs.baby.ui.display;

import com.ccs.baby.disassembler.Disassembler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class is responsible for displaying the disassembler window.
 */
public class DisplayDisassemblerWindow implements ActionListener {

    Disassembler disassembler;

    public DisplayDisassemblerWindow(Disassembler aDisassembler) {
        disassembler = aDisassembler;
    }

    public void actionPerformed(ActionEvent e) {
        disassembler.updateTextArea();
        disassembler.setVisible(true);
    }
}