package com.manchesterbaby.baby.menu;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;

import com.manchesterbaby.baby.utils.CheerpJUtils;
import com.manchesterbaby.baby.utils.MiscUtils;
import com.manchesterbaby.baby.utils.Version;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class AboutDialog extends JFrame {
    private static final String ABOUT_TITLE = "Manchester Baby Simulator";
    private static final String ABOUT_MESSAGE = String.join("<br>",
            "Manchester Baby Simulator",
            "",
            "v" + Version.getVersion(),
            (MiscUtils.getBuildTime() == null) ? "" : "Built " + MiscUtils.getBuildTime(),
            "",
            "Originally by <a href='https://davidsharp.com'>David Sharp</a>",
            "January 2001",
            "With thanks to Chris Burton for his consultation on historical matters.",
            "The GUI was created from pictures of the Baby replica by Gulzaman Khan",
            "August 2006",
            "",
            "<a href='https://manchesterbaby.com'>manchesterbaby.com</a>",
            "<a href='https://github.com/davidpsharp/baby'>github.com/davidpsharp/baby</a>",
            "",
            "Licensed under the GPL 3.0",
            "",
            "Thanks to Leaning Technologies for providing the <a href='https://cheerpj.com/'>CheerpJ</a> JVM",
            "to run the simulator in a browser."
    );
    private static final ImageIcon ICON = new ImageIcon(AboutDialog.class.getResource("/icons/baby.png"));

    private JButton okButton;

    public AboutDialog(JFrame parent) {
        super(ABOUT_TITLE);

        // Create HTML pane for the about message
        JEditorPane messagePane = new JEditorPane();
        messagePane.setContentType("text/html");
        messagePane.setText("<html><div style='text-align: center;'>" + ABOUT_MESSAGE + "</div></html>");
        messagePane.setEditable(false);
        messagePane.setBackground(UIManager.getColor("Panel.background"));

        // Add hyperlink listener
        messagePane.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    Desktop.getDesktop().browse(new URI(e.getURL().toString()));
                } catch (IOException | URISyntaxException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Could not open link: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Create main panel with icon and message
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Add icon to the left
        if (ICON != null) {
            JLabel iconLabel = new JLabel(ICON);
            mainPanel.add(iconLabel, BorderLayout.WEST);
        }
        
        // Add message to the center
        mainPanel.add(messagePane, BorderLayout.CENTER);

        // Add OK button at the bottom
        okButton = new JButton("OK");
        okButton.addActionListener(e -> okButtonClicked());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Set dialog properties
        setContentPane(mainPanel);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(parent);
    }

    public void displayAboutDialog() {
        this.setVisible(true);

        // workaround cheerpj bug that won't close dialogs by having a 20 second timer til it auto-closes
        if(CheerpJUtils.onCheerpj()) { 
            
            okButton.setText("OK (20)");
            
            javax.swing.Timer timer = new javax.swing.Timer(1000, new ActionListener() {
                private int countdown = 20;
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    countdown--;
                    okButton.setText("OK (" + countdown + ")");
                    if (countdown <= 0) {
                        ((javax.swing.Timer)e.getSource()).stop();
                        okButtonClicked();
                    }
                }
            });
            timer.start();
        }
    }

    private void okButtonClicked() {
        System.out.println("About Dialog: OK Clicked");
        this.setVisible(false);
        this.dispose();
    }
}
