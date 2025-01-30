package com.ccs.baby.manual;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import com.ccs.baby.io.LoadExample;
import com.ccs.baby.utils.MiscUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;

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

        try {
            String uri = LoadExample.getUriStringForResource("html/ssem_prm.html");
            viewer.setPage(uri);
        }
        catch(IOException | URISyntaxException ex)
        {
            System.out.println(MiscUtils.getStackTrace(ex));
        }

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
                        java.net.URL url = event.getURL();
                        viewer.setPage(url);
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