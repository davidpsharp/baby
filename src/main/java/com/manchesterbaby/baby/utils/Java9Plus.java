package com.manchesterbaby.baby.utils;

import java.awt.Image;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Utility class to isolate Java 9+ features.
 * 
 * This is for functionality that is only available in Java 9 or later.
 * Nothing here should be core functionality. All methods should gracefully degrade if running on a Java 8 runtime.
 * Accessed via run-time reflection so Baby can be compiled for Java 8 for max compatibility, especially with CheerpJ v3.1.
 * 
 * @author David Sharp
 */
public class Java9Plus {
    
    public static void setDockIcon(Image icon) {
        // For macOS dock icon
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            // macOS-specific dock icon setting (requires Java 9 or later)
            // equivalent to
            //      java.awt.Taskbar.getTaskbar().setIconImage(icon);
            Class taskbarCls = null;
            try {
                taskbarCls = Class.forName("java.awt.Taskbar");
                java.lang.reflect.Method getTaskbarMethod = taskbarCls.getMethod("getTaskbar");
                Object taskbarObj = getTaskbarMethod.invoke(null);
                java.lang.reflect.Method setIconImageMethod = taskbarCls.getMethod("setIconImage", Image.class);
                setIconImageMethod.invoke(taskbarObj, icon);
            } catch (Exception e) {
                // Could not set macOS dock icon
            }
        }
    }

    public static String getJavaRuntimePath() {
        // Try to get the exact Java executable using reflection for Java 9+
        // typically this is because running on JRE11 under laucnh4j .exe in Windows
        // equivalent to - String javaPath = ProcessHandle.current().info().command().orElse(null);
        String javaPath = null;
        try {
            Class<?> processHandleClass = Class.forName("java.lang.ProcessHandle");
            Object currentProcess = processHandleClass.getMethod("current").invoke(null);
            Object processInfo = processHandleClass.getMethod("info").invoke(currentProcess);
            Method commandMethod = processInfo.getClass().getMethod("command");
            Object command = commandMethod.invoke(processInfo);
            if (command instanceof Optional) {
                Optional<?> optionalCommand = (Optional<?>) command;
                if (optionalCommand.isPresent()) {
                    javaPath = (String) optionalCommand.get();
                }
            }
        } catch (Exception e1) {
            // Reflection failed or ProcessHandle not available (Java 8)
        }
        return javaPath;
    }

}
