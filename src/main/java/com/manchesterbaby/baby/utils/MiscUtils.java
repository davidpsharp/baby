package com.manchesterbaby.baby.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.jar.Manifest;
import com.manchesterbaby.baby.core.Baby;
import java.awt.Desktop;

public class MiscUtils {
    
    public static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    /** Return date/time the JAR was built, taken from the JAR manifest */
    public static String getBuildTime() {
        String buildTime = null;
        try {
            // Get the URL of the Baby class to find its containing jar
            String babyClassPath = Baby.class.getResource("Baby.class").toString();
            
            // Find the manifest in the same jar as the Baby class
            String manifestPath;
            if (babyClassPath.startsWith("jar:")) {
                // When running from jar, convert jar:file:/path/to/baby.jar!/com/... to jar:file:/path/to/baby.jar!/META-INF/MANIFEST.MF
                manifestPath = babyClassPath.substring(0, babyClassPath.indexOf("!")) + "!/META-INF/MANIFEST.MF";
            } else {
                // When running from IDE/classes, look for manifest in classpath
                manifestPath = Baby.class.getClassLoader().getResource("META-INF/MANIFEST.MF").toString();
            }

            Manifest manifest = new Manifest(new java.net.URL(manifestPath).openStream());
            buildTime = manifest.getMainAttributes().getValue("Build-Time");
        } catch (Exception ex) {
            System.err.println("Error reading manifest: " + ex.getMessage());
        }
        return buildTime;
    }

    public static void launchUrlInBrowser(String url)
    {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (URISyntaxException ex) {
            System.err.println("Error opening download URL: " + ex.getMessage());
        } catch (Exception ex) {
            System.err.println("Error opening download URL: " + ex.getMessage());
        }
    }
        

}
