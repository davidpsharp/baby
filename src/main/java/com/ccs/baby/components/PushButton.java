package com.ccs.baby.components;

import javax.swing.*;
import java.awt.*;

import com.ccs.baby.utils.ImageUtils;

class PushButton extends JButton {

    private ImageIcon inIcon;
    private ImageIcon outIcon;

    public PushButton(String textValue, int verticalPosition) {
        inIcon = ImageUtils.loadImage("/images/pushin.gif", 2400);
        outIcon = ImageUtils.loadImage("/images/pushout.gif", 2400);

        setIcon(outIcon);
        setPressedIcon(inIcon);

        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);

        setHorizontalTextPosition(AbstractButton.CENTER);
        setVerticalTextPosition(verticalPosition);

        setText(textValue);
    }

}