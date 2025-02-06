package com.ccs.baby.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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

    public synchronized void addRecentFile(String location, String loadMethod) {
        if (location == null || loadMethod == null) {
            LOGGER.log(Level.WARNING, "Attempted to add null location or load method to recent files");
            return;
        }

        try {
            // Try parsing as URL first
            FileLocation fileLocation;
            try {
                URL url = new URL(location);
                fileLocation = new FileLocation(url);
            } catch (Exception e) {
                // If not a URL, treat as file path
                File file = new File(location).getCanonicalFile();
                if (!file.exists()) {
                    LOGGER.log(Level.WARNING, "File does not exist: {0}", file.getAbsolutePath());
                    return;
                }
                fileLocation = new FileLocation(file);
            }

            final FileLocation finalFileLocation = fileLocation;
            // Remove if already exists
            recentFiles.removeIf(entry -> entry.getLocation().equals(finalFileLocation));
            
            // Add to front
            recentFiles.addFirst(new RecentFileEntry(fileLocation, loadMethod));
            
            // Trim if necessary
            while (recentFiles.size() > MAX_RECENT_FILES) {
                recentFiles.removeLast();
            }

            // Save to disk
            saveRecentFiles();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to process recent file location: " + location, e);
        }
    }

    public synchronized List<RecentFileEntry> getRecentFiles() {
        // Clean up any files that no longer exist
        recentFiles.removeIf(entry -> !entry.getLocation().exists());
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
                String location = props.getProperty("location." + i);
                String type = props.getProperty("type." + i);
                String loadMethod = props.getProperty("method." + i);
                
                if (location != null && type != null && loadMethod != null) {
                    try {
                        FileLocation fileLocation;
                        if ("url".equals(type)) {
                            fileLocation = new FileLocation(new URL(location));
                        } else {
                            File file = new File(location).getCanonicalFile();
                            fileLocation = new FileLocation(file);
                        }
                        
                        if (fileLocation.exists()) {
                            recentFiles.add(new RecentFileEntry(fileLocation, loadMethod));
                        }
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Failed to process location: " + location, e);
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
            FileLocation location = entry.getLocation();
            props.setProperty("location." + i, location.toString());
            props.setProperty("type." + i, location.isUrl() ? "url" : "file");
            props.setProperty("method." + i, entry.getLoadMethod());
            i++;
        }
        
        try (FileOutputStream fos = new FileOutputStream(recentFilesFile)) {
            props.store(fos, "Manchester Baby Simulator Recent Files");
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to save recent files", e);
        }
    }

    public static class FileLocation {
        private final File file;
        private final URL url;

        public FileLocation(File file) {
            this.file = file;
            this.url = null;
        }

        public FileLocation(URL url) {
            this.file = null;
            this.url = url;
        }

        public boolean isUrl() {
            return url != null;
        }

        public File getFile() {
            return file;
        }

        public String getDisplayName() {
            if (isUrl()) {
                String path = url.getPath();
                int lastSlash = path.lastIndexOf('/');
                return lastSlash >= 0 ? path.substring(lastSlash + 1) : path;
            } else {
                return file.getName();
            }
        }

        public String getPath() {
            return isUrl() ? url.toString() : file.getPath();
        }

        public boolean exists() {
            if (isUrl()) {
                try {
                    url.openStream().close();
                    return true;
                } catch (IOException e) {
                    return false;
                }
            }
            return file.exists();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof FileLocation)) {
                return false;
            }
            FileLocation other = (FileLocation) obj;
            if (this.isUrl() != other.isUrl()) {
                return false;
            }
            return this.getPath().equals(other.getPath());
        }

        @Override
        public int hashCode() {
            return getPath().hashCode();
        }

        @Override
        public String toString() {
            return getPath();
        }
    }

    public static class RecentFileEntry {
        private final FileLocation location;
        private final String loadMethod;

        public RecentFileEntry(FileLocation location, String loadMethod) {
            this.location = location;
            this.loadMethod = loadMethod;
        }

        public FileLocation getLocation() {
            return location;
        }

        public String getLoadMethod() {
            return loadMethod;
        }

        @Override
        public String toString() {
            return location.toString();
        }
    }
}
