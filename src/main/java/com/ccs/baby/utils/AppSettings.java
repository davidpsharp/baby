package com.ccs.baby.utils;

/**
 * Centralizes all application settings and their default values.
 * Use this class to define new settings and their defaults.
 */
public class AppSettings {
    // Settings keys
    public static final String KEY_SHOW_FPS = "show_fps";
    public static final String KEY_MAX_FRAME_RATE = "max_frame_rate";
    public static final String KEY_DEBUG_MODE = "debug_mode";
    public static final String KEY_INITIAL_EXAMPLE = "initial_example";
    
    // Default values
    public static final boolean DEFAULT_SHOW_FPS = true;
    public static final int DEFAULT_MAX_FRAME_RATE = 60;
    public static final boolean DEFAULT_DEBUG_MODE = false;
    public static final String DEFAULT_INITIAL_EXAMPLE = "demos/diffeqt.asm";

    private static AppSettings instance;
    private final PreferencesService prefs;

    private AppSettings() {
        prefs = PreferencesService.getInstance();
    }

    public static synchronized AppSettings getInstance() {
        if (instance == null) {
            instance = new AppSettings();
        }
        return instance;
    }



    // Getter methods
    public boolean isShowFps() {
        return prefs.getBooleanPreference(KEY_SHOW_FPS, DEFAULT_SHOW_FPS);
    }

    public int getMaxFrameRate() {
        return prefs.getIntPreference(KEY_MAX_FRAME_RATE, DEFAULT_MAX_FRAME_RATE);
    }

    public boolean isDebugMode() {
        return prefs.getBooleanPreference(KEY_DEBUG_MODE, DEFAULT_DEBUG_MODE);
    }

    public String getInitialExample() {
        return prefs.getPreference(KEY_INITIAL_EXAMPLE, DEFAULT_INITIAL_EXAMPLE);
    }
    


    // Setter methods
    public void setShowFps(boolean value) {
        prefs.setBooleanPreference(KEY_SHOW_FPS, value);
    }

    public void setMaxFrameRate(int value) {
        prefs.setIntPreference(KEY_MAX_FRAME_RATE, value);
    }

    public void setDebugMode(boolean value) {
        prefs.setBooleanPreference(KEY_DEBUG_MODE, value);
    }

    public void setInitialExample(String value) {
        prefs.setPreference(KEY_INITIAL_EXAMPLE, value);
    }
    
}
