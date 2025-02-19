package com.manchesterbaby.baby.manual;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import com.manchesterbaby.baby.io.LoadExample;
import com.manchesterbaby.baby.utils.MiscUtils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;

public class ReferenceManual extends JDialog {

    private final ReferenceManualPanel panel;
    private static JPanel tabPanel;

    public ReferenceManual(JFrame parentFrame) {
        super(parentFrame);
        
        panel = new ReferenceManualPanel();

        setTitle("Reference Manual");
        setSize(800, 600);
        
        setContentPane(panel);
    }

    public JPanel getTabPanel() {
        if (tabPanel == null) {
            tabPanel = new ReferenceManualPanel();
        }
        return tabPanel;
    }

    public static class ReferenceManualPanel extends JPanel {

        private final JEditorPane viewer;

        public ReferenceManualPanel() {

            // Create the JFrame for the reference manual
            setLayout(new BorderLayout());

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
            add(scrollPane, BorderLayout.CENTER);

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
    }
}