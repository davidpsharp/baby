package com.ccs.baby.menu;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ReferenceManual implements ActionListener {

    private final JFrame ref;
    private final JEditorPane viewer;
    public static JPanel refManualPanel;

    public ReferenceManual() {

        refManualPanel = new JPanel();

        // Create the JFrame for the reference manual
        ref = new JFrame("Reference Manual");
        viewer = new JEditorPane();

        viewer.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(viewer);
        ref.add(scrollPane);
        ref.setSize(505, 700);
        ref.setAlwaysOnTop(true);
        ref.setLocation(730, 250);
        ref.setVisible(false); // Initially hidden


        // Add a hyperlink listener
        viewer.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent event) {
                if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        viewer.setPage(event.getURL());
                    } catch (IOException ioe) {
                        // Some warning to user
                    }
                }
            }
        });
    }

    public void actionPerformed(ActionEvent e) {
        ref.setVisible(true);
    }

}