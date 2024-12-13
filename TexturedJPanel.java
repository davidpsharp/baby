import java.awt.*;
import javax.swing.*;
import java.awt.image.*;
import javax.imageio.*; 
import java.io.*; 

public class TexturedJPanel extends JPanel 
{
  java.io.File filetexture;
  BufferedImage  mImage;
  
  public TexturedJPanel(String fileName) {
      mImage = convert(loadImage(fileName).getImage()); //convert to BI
  }
  


  public void paintComponent(Graphics g) {
	super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        if (g2==null) {
            System.out.println("error");
            return; 
        }
	  	java.awt.geom.Rectangle2D tr = new   java.awt.geom.Rectangle2D.Double(0, 0, this.getWidth(), this.getHeight());
	  	TexturePaint tp = new TexturePaint(mImage, tr);
    	        g2.setPaint(tp);
                java.awt.geom.Rectangle2D  r =  (java.awt.geom.Rectangle2D)this.getBounds();  
    	        g2.fill(r);

 
  }
  
  public void changeTexture(String fileName) {
    mImage = convert(loadImage(fileName).getImage()); //convert to BI
  }
  
  private ImageIcon loadImage(String image) {
      int MAX_IMAGE_SIZE = 800000;  //Change this to the size of
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
    
  public BufferedImage convert(Image im) {
    BufferedImage bi = new BufferedImage(im.getWidth(null),im.getHeight(null),BufferedImage.TYPE_INT_ARGB);
    Graphics bg = bi.getGraphics();
    bg.drawImage(im, 0, 0, null);
    bg.dispose();
    return bi;
  }
 
}