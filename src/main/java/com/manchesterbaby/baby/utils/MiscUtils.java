package com.manchesterbaby.baby.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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

    /**
     * Gets the version of the currently running Java Runtime Environment.
     * This will return the full version string from Runtime.version() for Java 9+,
     * or System.getProperty("java.version") for Java 8 and below.
     * 
     * @return The version string of the current JRE
     */
    public static String getJREversion() {
        // First try Runtime.version() (Java 9+)
        try {
            Object version = Runtime.class.getMethod("version").invoke(null);
            return version.toString();
        } catch (Exception e) {
            // Fallback to System property (Java 8 and below)
            return System.getProperty("java.version");
        }
    }

    // returns true if java 9 or later (which support scaleUI param)
    public static boolean jreCanScaleUI() {
        try {
            // use reflection to call a java 9 and later method
            Object version = Runtime.class.getMethod("version").invoke(null);
            return true;
        } catch (Exception e) {
            // Fallback to System property (Java 8 and below)
            return false;
        }
    }

    /**
     * Gets the name and vendor of the Java Runtime Environment.
     * For example: "Eclipse Temurin" or "Oracle OpenJDK"
     * 
     * @return A string describing the JRE implementation
     */
    public static String getJREname() {
        try{

        
            String vendor = System.getProperty("java.vendor");
            String vmName = System.getProperty("java.vm.name");
            
            // Check for common JRE implementations
            if (vmName.contains("OpenJ9")) {
                return "Eclipse OpenJ9";
            } else if (vendor.contains("Adoptium") || vendor.contains("Eclipse")) {
                return "Eclipse Temurin";
            } else if (vendor.contains("Oracle")) {
                if (vmName.contains("OpenJDK")) {
                    return "Oracle OpenJDK";
                } else {
                    return "Oracle JDK";
                }
            } else if (vendor.contains("Amazon")) {
                return "Amazon Corretto";
            } else if (vendor.contains("Azul")) {
                return "Azul Zulu";
            } else if (vendor.contains("Microsoft")) {
                return "Microsoft Build of OpenJDK";
            } else if (vendor.contains("Red Hat")) {
                return "Red Hat OpenJDK";
            } else if (vendor.contains("SAP")) {
                return "SAP SapMachine";
            } else if (vendor.contains("BellSoft")) {
                return "BellSoft Liberica";
            } else if (vendor.contains("CheerpJ")) {
                return "CheerpJ WebVM";
            }
            
            // If we can't identify a specific distribution, return vendor and VM name
            return vendor + " " + vmName;
        } catch (Exception e) {
            System.err.println("Error getting JRE info: " + e.getMessage());
            return "Error getting JRE info";
        }
    }

    /**
     * Gets a string describing the operating system name and version.
     * For example: "macOS 14.3.1" or "Windows 11 (10.0)"
     * 
     * @return A string containing the OS name and version
     */
    public static String getOSversion() {
        
        try {

            String osName = System.getProperty("os.name");
            String osVersion = System.getProperty("os.version");
            String osArch = System.getProperty("os.arch");
            
            // Format the version string based on OS
            if (osName.startsWith("Mac")) {
                // Use sw_vers command to get accurate macOS version
                try {
                    // Get macOS version
                    Process p = Runtime.getRuntime().exec("sw_vers -productVersion");
                    BufferedReader reader = new BufferedReader(
                        new InputStreamReader(p.getInputStream())
                    );
                    String macVersion = reader.readLine();
                    if (macVersion != null && !macVersion.isEmpty()) {
                        osVersion = macVersion;
                    }
                    
                    // Get actual hardware architecture using sysctl
                    p = Runtime.getRuntime().exec(new String[] { "sysctl", "-n", "machdep.cpu.brand_string" });
                    reader = new BufferedReader(
                        new InputStreamReader(p.getInputStream())
                    );
                    String cpuInfo = reader.readLine();
                    
                    if (cpuInfo != null) {
                        if (cpuInfo.contains("Apple M")) {
                            osArch = "aarch64";
                        } else if (cpuInfo.contains("Intel")) {
                            osArch = "x86_64";
                        }
                        // Add CPU model to architecture string
                        osArch += " (" + cpuInfo.trim() + ")";
                    }
                } catch (Exception e) {
                    // Fallback to os.version and os.arch if commands fail
                    System.err.println("Error getting OS info: " + e.getMessage());            }
                return String.format("macOS %s (%s)", osVersion, osArch);
            } else if (osName.startsWith("Windows")) {
                String displayVersion = getWindowsDisplayVersion();
                if (displayVersion != null) {
                    return String.format("Windows %s (%s, %s)", displayVersion, osVersion, osArch);
                } else {
                    return String.format("Windows %s (%s)", osVersion, osArch);
                }
            } else {
                // Linux or other OS
                return String.format("%s %s (%s)", osName, osVersion, osArch);
            }
        } catch (Exception e) {
            System.err.println("Error getting OS info: " + e.getMessage());
            return "Error getting OS info";
        }
    }

    /**
     * Gets the Windows display version (e.g. "11" or "10") by reading the registry.
     * Returns null if unable to determine or not on Windows.
     */
    private static String getWindowsDisplayVersion() {
        try {
            Process p = Runtime.getRuntime().exec(new String[] {
                "cmd", "/c",
                "reg query \"HKLM\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\" /v DisplayVersion"
            });
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(p.getInputStream())
            );
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("DisplayVersion")) {
                    String[] parts = line.trim().split("\\s+");
                    if (parts.length >= 3) {
                        return parts[parts.length - 1];
                    }
                }
            }
        } catch (Exception e) {
            // Silently fail and return null
        }
        return null;
    }

    /**
     * Gets the Java version that this program was compiled for by checking
     * the class file version of MiscUtils.class.
     * 
     * @return A string describing the target Java version (e.g. "Java 8" or "Java 11")
     */
    public static String getJavaBuildVersion() {
        try {
            // Use reflection to get the class file version
            java.io.DataInputStream in = new java.io.DataInputStream(
                MiscUtils.class.getResourceAsStream("/" + 
                MiscUtils.class.getName().replace('.', '/') + ".class"));
            
            if (in != null) {
                // Skip the magic number
                in.readInt();
                // Read the minor and major version
                int minor = in.readUnsignedShort();
                int major = in.readUnsignedShort();
                in.close();
                
                // Map class file version to Java version
                // Reference: https://en.wikipedia.org/wiki/Java_class_file#General_layout
                switch (major) {
                    case 45: return "Java 1.1";
                    case 46: return "Java 1.2";
                    case 47: return "Java 1.3";
                    case 48: return "Java 1.4";
                    case 49: return "Java 5";
                    case 50: return "Java 6";
                    case 51: return "Java 7";
                    case 52: return "Java 8";
                    case 53: return "Java 9";
                    case 54: return "Java 10";
                    case 55: return "Java 11";
                    case 56: return "Java 12";
                    case 57: return "Java 13";
                    case 58: return "Java 14";
                    case 59: return "Java 15";
                    case 60: return "Java 16";
                    case 61: return "Java 17";
                    case 62: return "Java 18";
                    case 63: return "Java 19";
                    case 64: return "Java 20";
                    case 65: return "Java 21";
                    default: return "Unknown Java version (class version " + major + "." + minor + ")";
                }
            }
        } catch (Exception e) {
            return "Unable to determine Java build version";
        }
        return "Unable to determine Java build version";
    }
}
