package com.ccs.baby.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.io.File;

public class RecentFilesManager {
    private static final int MAX_RECENT_FILES = 10;
    private final LinkedList<RecentFileEntry> recentFiles;

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
    }

    public RecentFilesManager() {
        recentFiles = new LinkedList<>();
    }

    public void addRecentFile(File file, String loadMethod) {
        // Remove if already exists
        recentFiles.removeIf(entry -> entry.getFile().equals(file));
        
        // Add to front
        recentFiles.addFirst(new RecentFileEntry(file, loadMethod));
        
        // Trim if necessary
        while (recentFiles.size() > MAX_RECENT_FILES) {
            recentFiles.removeLast();
        }
    }

    public List<RecentFileEntry> getRecentFiles() {
        return new ArrayList<>(recentFiles);
    }
}
