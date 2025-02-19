package com.manchesterbaby.baby.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Version {
    private static final String VERSION_PROPERTIES = "version.properties";
    private static String version;

    static {
        try {
            Properties props = new Properties();
            InputStream is = Version.class.getClassLoader().getResourceAsStream(VERSION_PROPERTIES);
            if (is != null) {
                props.load(is);
                version = props.getProperty("version", "unknown");
                is.close();
            } else {
                version = "unknown";
            }
        } catch (IOException e) {
            version = "unknown";
            System.err.println("Error loading version: " + e.toString());
        }
    }

    public static String getVersion() {
        return version;
    }
}
