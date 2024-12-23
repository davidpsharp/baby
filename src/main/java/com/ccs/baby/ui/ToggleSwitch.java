
import javax.swing.*;
import java.awt.*;
import java.io.*;




class ToggleSwitch extends JCheckBox
{

	public ToggleSwitch(String textValue, int horizontalPos, int verticalPos)
	{
	
		ImageIcon downIcon = loadImage("toggle_switch_down.png");
		ImageIcon upIcon = loadImage("toggle_switch_up.png");
		ImageIcon holeIcon = loadImage("hole.gif");
		
		setIcon(upIcon);
		setSelectedIcon(downIcon);
		setDisabledIcon(holeIcon);
		
		setFocusPainted(false);
		setBorderPainted(false);
		setContentAreaFilled(false);
		
		setSelected(false);
				
		setHorizontalTextPosition(horizontalPos);
		setVerticalTextPosition(verticalPos);
		
		setText(textValue);
	}
	
	protected ImageIcon loadImage(String image) {
      int MAX_IMAGE_SIZE = 12400;  //Change this to the size of
                                 //your biggest image, in bytes.
      int count = 0;
      BufferedInputStream imgStream = new BufferedInputStream(
                                    this.getClass().getResourceAsStream(image));
      if (imgStream != null) {
        byte buf[] = new byte[MAX_IMAGE_SIZE];
        try {
            count = imgStream.read(buf);
            imgStream.close();
        } catch (java.io.IOException ioe) {
            System.err.println("Couldn't read stream from file: " + image);
            return null;
        }
        if (count <= 0) {
            System.err.println("Empty file: " + image);
            return null;
        }
        return new ImageIcon(Toolkit.getDefaultToolkit().createImage(buf));
      } 
      else {
        System.err.println("Couldn't find file: " + image);
        return null;
      }
    }

}




