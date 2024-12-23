package com.ccs.baby.components;

import javax.swing.JRadioButton;
import javax.swing.ImageIcon;
import javax.swing.AbstractButton;
import java.awt.Insets;
import com.ccs.baby.utils.ImageUtils;

public class InterlockingPushButton extends JRadioButton {

    public InterlockingPushButton(String textValue, int verticalPosition) {

        ImageIcon inIcon = ImageUtils.loadImage("/images/ibuttonin.gif", 2400);
        ImageIcon outIcon = ImageUtils.loadImage("/images/ibuttonout.gif", 2400);

        setIcon(outIcon);
        setSelectedIcon(inIcon);

        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);

        Insets marginSpace = new Insets(3, 3, 3, 3);
        setMargin(marginSpace);

        setHorizontalTextPosition(AbstractButton.CENTER);
        setVerticalTextPosition(verticalPosition);

        setText(textValue);
    }



}