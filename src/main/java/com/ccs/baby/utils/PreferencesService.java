package com.ccs.baby.utils;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PreferencesService {
    private static final Logger LOGGER = Logger.getLogger(PreferencesService.class.getName());
    private static final String PREFERENCES_FILE = "baby-preferences.properties";
    private static PreferencesService instance;
    private final Properties properties;
    private final File preferencesFile;

    private PreferencesService() {
        properties = new Properties();
        String userHome = System.getProperty("user.home");
        File appDir = new File(userHome, ".baby");
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        preferencesFile = new File(appDir, PREFERENCES_FILE);
        loadPreferences();
    }

    public static synchronized PreferencesService getInstance() {
        if (instance == null) {
            instance = new PreferencesService();
        }
        return instance;
    }

    private void loadPreferences() {
        if (preferencesFile.exists()) {
            try (FileInputStream fis = new FileInputStream(preferencesFile)) {
                properties.load(fis);
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Failed to load preferences", e);
            }
        }
    }

    // save preferences to ~/.baby/baby-preferences.properties
    // only saves preferences actively set, so won't save down current defaults
    // this is probably desirable as means defaults can be changed with new versions
    public void savePreferences() {
        try (FileOutputStream fos = new FileOutputStream(preferencesFile)) {
            properties.store(fos, "Manchester Baby Simulator Preferences");
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to save preferences", e);
        }
    }

    public String getPreference(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public void setPreference(String key, String value) {
        properties.setProperty(key, value);
        savePreferences();
    }

    public boolean getBooleanPreference(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    public void setBooleanPreference(String key, boolean value) {
        properties.setProperty(key, String.valueOf(value));
        savePreferences();
    }

    public int getIntPreference(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "Invalid integer preference value for key: " + key);
            }
        }
        return defaultValue;
    }

    public void setIntPreference(String key, int value) {
        properties.setProperty(key, String.valueOf(value));
        savePreferences();
    }
}
