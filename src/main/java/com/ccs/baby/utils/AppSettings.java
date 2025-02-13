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
    public static final String KEY_SHOW_DEBUG_PANEL = "show_debug_panel";
    public static final String KEY_INTERACTIVE_LOADING = "interactive_loading";
    public static final String KEY_LOAD_PATH = "load_path";
    public static final String KEY_SHOW_DISCONNECTED_BUTTONS = "show_disconnected_buttons";
    
    // Default values
    public static final boolean DEFAULT_SHOW_FPS = true;
    public static final int DEFAULT_MAX_FRAME_RATE = 60;
    public static final boolean DEFAULT_DEBUG_MODE = false;
    public static final String DEFAULT_INITIAL_EXAMPLE = "demos/diffeqt.asm";
    public static final boolean DEFAULT_SHOW_DEBUG_PANEL = false;
    public static final boolean DEFAULT_INTERACTIVE_LOADING = true;
    public static final String DEFAULT_LOAD_PATH = System.getProperty("user.home");
    public static final boolean DEFAULT_SHOW_DISCONNECTED_BUTTONS = true;

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


    // TODO:
    // add historical accuracy menu
    // - run at original speed
    // - list of zoom settings for scaleUi; 0.5, 0.75, default (no scale), 1.25, 1.5, 2, 2.5, 3 
    // - auto run program on startup
    // - save default program (so persisted in settings, not just a link to a program file)


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

    public boolean isShowDebugPanel() {
        return prefs.getBooleanPreference(KEY_SHOW_DEBUG_PANEL, DEFAULT_SHOW_DEBUG_PANEL);
    }

    public boolean isInteractiveLoading() {
        return prefs.getBooleanPreference(KEY_INTERACTIVE_LOADING, DEFAULT_INTERACTIVE_LOADING);
    }

    public String getLoadPath() {
        return prefs.getPreference(KEY_LOAD_PATH, DEFAULT_LOAD_PATH);
    }

    public boolean isShowDisconnectedButtons() {
        return prefs.getBooleanPreference(KEY_SHOW_DISCONNECTED_BUTTONS, DEFAULT_SHOW_DISCONNECTED_BUTTONS);
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

    public void setShowDebugPanel(boolean value) {
        prefs.setBooleanPreference(KEY_SHOW_DEBUG_PANEL, value);
    }

    public void setInteractiveLoading(boolean value) {
        prefs.setBooleanPreference(KEY_INTERACTIVE_LOADING, value);
    }   

    public void setLoadPath(String value) {
        prefs.setPreference(KEY_LOAD_PATH, value);
    }

    public void setShowDisconnectedButtons(boolean value) {
        prefs.setBooleanPreference(KEY_SHOW_DISCONNECTED_BUTTONS, value);
    }
    
}
