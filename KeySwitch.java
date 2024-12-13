import javax.swing.*;
import java.awt.*;
import java.io.*;



class KeySwitch extends JButton
{

	public KeySwitch(String textValue, String downIconFileName, String upIconFileName)
	{
	
		ImageIcon upIcon = loadImage(upIconFileName);
		ImageIcon downIcon = loadImage(downIconFileName);
		
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
	
	private ImageIcon loadImage(String image) {
      int MAX_IMAGE_SIZE = 2400;  //Change this to the size of
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