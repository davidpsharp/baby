package com.ccs.baby.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class MiscUtils {
    
    public static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    /** Return true if the current platform is CheerpJ, the web browser-based-javascript/webasm-JVM.
     * Useful to check if the simulator is running in a web browser as several behavioural differences
     * over running as a native JVM app exist.
     * @return true if the current platform is CheerpJ, the web browser-based-javascript/webasm-JVM.
     */
    public static boolean onCheerpj() {
        return System.getProperty("os.arch").equals("cheerpj");
    }

}
