package com.ccs.baby.ui;

import com.ccs.baby.disassembler.Disassembler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// open the disassembly window
public class ViewDisassembler implements ActionListener {

    Disassembler disassembler;

    public ViewDisassembler(Disassembler aDisassembler) {
        disassembler = aDisassembler;
    }

    public void actionPerformed(ActionEvent e) {
        disassembler.updateTextArea();
        disassembler.setVisible(true);
    }
}