import javax.swing.*;
import java.awt.*;
import java.io.*;


class PushButton extends JButton
{
	
	private ImageIcon inIcon;
	private ImageIcon outIcon;
	

	public PushButton(String textValue, int verticalPosition)
	{
	
		inIcon = loadImage("pushin.gif");
		outIcon = loadImage("pushout.gif");
		
		setIcon(outIcon);
		setPressedIcon(inIcon);
		
		setFocusPainted(false);
		setBorderPainted(false);
		setContentAreaFilled(false);
		
		setHorizontalTextPosition(AbstractButton.CENTER);
		setVerticalTextPosition(verticalPosition);
		
		setText(textValue);
	}
	
	public Image getImage(String fileName) { 
      java.net.URL imageURL = getClass().getClassLoader().getResource(fileName);
      if(imageURL == null) 
        return null; 
      return new ImageIcon(imageURL).getImage();
    }
	
	protected ImageIcon loadImage(String image) {
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