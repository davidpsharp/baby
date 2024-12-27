package com.ccs.baby.disassembler;

import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import java.util.StringTokenizer;

import com.ccs.baby.core.Store;
import com.ccs.baby.core.Control;
import com.ccs.baby.ui.CrtPanel;

public class Disassembler extends JFrame {

    Store store;
    Control control;
    CrtPanel crtPanel;

    JTextArea textArea;

    public Disassembler(Store aStore, Control aControl, CrtPanel aCrtPanel) {
        store = aStore;
        control = aControl;
        crtPanel = aCrtPanel;

        // Create frame
        setTitle("Disassembler");
        setSize(400, 630);

        Container contentPane = getContentPane();

        JPanel backPanel = new JPanel(new BorderLayout());

        JButton loadFromStore = new JButton("Load from store");
        JButton saveToStore = new JButton("Save to store");
        JCheckBox updateOnStep = new JCheckBox("Update");

        textArea = new JTextArea();
        textArea.setEditable(true);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        loadFromStore.addActionListener(new UpdateTextArea());
        saveToStore.addActionListener(new UpdateStore());
        updateOnStep.addActionListener(new ChangeUpdateOnStep());

        // Add scroll pane to the text area
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(100, 100));

        JPanel controlsPanel = new JPanel();
        controlsPanel.add(loadFromStore);
        controlsPanel.add(saveToStore);
        controlsPanel.add(updateOnStep);

        backPanel.add(controlsPanel, BorderLayout.NORTH);
        backPanel.add(scrollPane, BorderLayout.CENTER);

        contentPane.add(backPanel);
    }

    // Disassemble every line of the store and add to the displayed text area
    public void updateTextArea() {
        StringBuilder output = new StringBuilder();

        int controlInstruction = control.getControlInstruction();

        output.append("; CI: ").append(controlInstruction).append("\n");
        output.append("; PI: ").append(Store.disassembleModern(control.getPresentInstruction())).append("\n");
        output.append("; ACC: ").append(control.getAccumulator()).append("\n\n");

        for (int lineNumber = 0; lineNumber < 32; lineNumber++) {
            String lineNumberS = String.format("%02d", lineNumber);

            output.append(lineNumberS).append("  ").append(Store.disassembleModern(store.getLine(lineNumber), (lineNumber == controlInstruction)))  // Pad with preceding 0's
                    .append("\n");
        }

        textArea.setText(output.toString());
    }


    class UpdateTextArea implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            updateTextArea();
        }
    }

    // Assemble the information in the store into the text area
    class UpdateStore implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String fullText = textArea.getText();

            // Create a new tokenizer, tokenizing on newlines
            StringTokenizer tokenizer = new StringTokenizer(fullText, "\n");

            try {
                // Any exception will cause drop out of while loop and be handled
                int tokenCounter = 0;
                while (tokenizer.hasMoreElements()) {
                    tokenCounter++;
                    store.assembleModernToStore(tokenizer.nextToken(), tokenCounter);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(getContentPane(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

            crtPanel.render();
            crtPanel.repaint();
        }
    }

    public static class ChangeUpdateOnStep implements ActionListener {
        public void actionPerformed(ActionEvent e) {

        }
    }

}