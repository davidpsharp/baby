package com.ccs.baby.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RecentFilesManager {
    private static final Logger LOGGER = Logger.getLogger(RecentFilesManager.class.getName());
    private static final int MAX_RECENT_FILES = 20;
    private static final String RECENT_FILES_FILE = "baby-recent-files.properties";
    private static RecentFilesManager instance;
    private final LinkedList<RecentFileEntry> recentFiles;
    private final File recentFilesFile;

    private RecentFilesManager() {
        recentFiles = new LinkedList<>();
        String userHome = System.getProperty("user.home");
        File appDir = new File(userHome, ".baby");
        if (!appDir.exists() && !appDir.mkdirs()) {
            LOGGER.log(Level.WARNING, "Failed to create app directory: {0}", appDir.getAbsolutePath());
        }
        recentFilesFile = new File(appDir, RECENT_FILES_FILE);
        loadRecentFiles();
    }

    public static synchronized RecentFilesManager getInstance() {
        if (instance == null) {
            instance = new RecentFilesManager();
        }
        return instance;
    }

    public synchronized void addRecentFile(File file, String loadMethod) {
        if (file == null || loadMethod == null) {
            LOGGER.log(Level.WARNING, "Attempted to add null file or load method to recent files");
            return;
        }

        try {
            // Normalize the file path and verify it exists
            File canonicalFile = file.getCanonicalFile();
            if (!canonicalFile.exists()) {
                LOGGER.log(Level.WARNING, "File does not exist: {0}", canonicalFile.getAbsolutePath());
                return;
            }

            // Remove if already exists
            recentFiles.removeIf(entry -> {
                try {
                    return entry.getFile().getCanonicalPath().equals(canonicalFile.getCanonicalPath());
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Failed to compare file paths", e);
                    return false;
                }
            });
            
            // Add to front
            recentFiles.addFirst(new RecentFileEntry(canonicalFile, loadMethod));
            
            // Trim if necessary
            while (recentFiles.size() > MAX_RECENT_FILES) {
                recentFiles.removeLast();
            }

            // Save to disk
            saveRecentFiles();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to process recent file: " + file.getAbsolutePath(), e);
        }
    }

    public synchronized List<RecentFileEntry> getRecentFiles() {
        // Clean up any files that no longer exist
        recentFiles.removeIf(entry -> !entry.getFile().exists());
        return new ArrayList<>(recentFiles);
    }

    public synchronized void clearRecentFiles() {
        recentFiles.clear();
        saveRecentFiles();
    }

    private void loadRecentFiles() {
        if (!recentFilesFile.exists()) {
            return;
        }

        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(recentFilesFile)) {
            props.load(fis);
            int count = Integer.parseInt(props.getProperty("count", "0"));
            for (int i = 0; i < count; i++) {
                String filePath = props.getProperty("file." + i);
                String loadMethod = props.getProperty("method." + i);
                if (filePath != null && loadMethod != null) {
                    try {
                        File file = new File(filePath).getCanonicalFile();
                        if (file.exists()) {
                            recentFiles.add(new RecentFileEntry(file, loadMethod));
                        }
                    } catch (IOException e) {
                        LOGGER.log(Level.WARNING, "Failed to process file path: " + filePath, e);
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to load recent files", e);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid count in recent files", e);
        }
    }

    private synchronized void saveRecentFiles() {
        Properties props = new Properties();
        props.setProperty("count", String.valueOf(recentFiles.size()));
        int i = 0;
        for (RecentFileEntry entry : recentFiles) {
            try {
                props.setProperty("file." + i, entry.getFile().getCanonicalPath());
                props.setProperty("method." + i, entry.getLoadMethod());
                i++;
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Failed to save file path: " + entry.getFile().getAbsolutePath(), e);
            }
        }
        
        try (FileOutputStream fos = new FileOutputStream(recentFilesFile)) {
            props.store(fos, "Manchester Baby Simulator Recent Files");
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to save recent files", e);
        }
    }

    public static class RecentFileEntry {
        private final File file;
        private final String loadMethod;

        public RecentFileEntry(File file, String loadMethod) {
            this.file = file;
            this.loadMethod = loadMethod;
        }

        public File getFile() {
            return file;
        }

        public String getLoadMethod() {
            return loadMethod;
        }

        @Override
        public String toString() {
            return file.getName();
        }
    }
}
