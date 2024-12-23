/*
 * BabyAppletStub.java
 *
 * Created on 14 December 2007, 22:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author Gulzaman
 */
import java.applet.*;
import java.awt.*;
import java.awt.image.ColorModel;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Hashtable;
import java.util.Locale;
import javax.accessibility.*;

public class BabyAppletStub implements AppletStub {
    
    Applet parent; 
    
    /** Creates a new instance of BabyAppletStub */
    public BabyAppletStub(Applet parent_) {
        parent = parent_;
    }
     
    
    public void appletResize(int width, int height) {
        parent.resize(width, height);
    }
    
    public AppletContext getAppletContext()
    {
        return parent.getAppletContext();
    }
    
    
    public String getParameter(String parameter)
    {
        return parent.getParameter(parameter);
    }
    
    public URL getCodeBase()
    {
        return parent.getCodeBase();
    }
    
    public URL getDocumentBase()
    {
        return parent.getDocumentBase();
    }
    
    public boolean isActive()
    {
        return parent.isActive();
    }
}
