package com.ccs.baby.components;

import javax.swing.*;
import java.awt.*;
import com.ccs.baby.utils.ImageUtils;

class KeySwitch extends JButton
{

	public KeySwitch(String textValue, String downIconFileName, String upIconFileName)
	{
	
		ImageIcon upIcon = ImageUtils.loadImage(upIconFileName,2400);
		ImageIcon downIcon = ImageUtils.loadImage(downIconFileName, 2400);
		
		setIcon(upIcon);
		setPressedIcon(downIcon);
		
		setFocusPainted(false);
		setBorderPainted(false);
		setContentAreaFilled(false);
		
		// top, left, bottom, right
		Insets marginSpace = new Insets(3,3,3,3);
		setMargin(marginSpace);
		
		setHorizontalTextPosition(AbstractButton.CENTER);
		setVerticalTextPosition(AbstractButton.NORTH);
		
		setText(textValue);
	}

}