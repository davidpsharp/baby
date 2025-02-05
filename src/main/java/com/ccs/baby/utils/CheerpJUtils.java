package com.ccs.baby.utils;

import com.ccs.baby.core.Baby;

/** CheerpJUtils handles interaction between Java and JavaScript when running simulator on the CheerpJ JVM in a browser */
public class CheerpJUtils {

    private Baby _baby;

    // all as per CheerpJ docs at https://cheerpj.com/docs/tutorials/interoperability-tutorial.html 

    // ask jscript to have user select a file
    public static native void getFileForSimulator();

    public CheerpJUtils(Baby baby) {
        _baby = baby;
    }

    // receives data from JavaScript and processes it (not currently used)
    public String processInput(String input, String fileName) {
        System.out.println("Java received: " + fileName + "\n" + input);
        return "Java received: " + input;
    }

    // receives data from JavaScript and processes it
    public String openFile(String fileName) {
        System.out.println("Java received: " + fileName);
        // thread safety probably not needed, if the machine is running then the behaviour is beyond undefined anyway...
        _baby.openFile(fileName);
        return "Java received: " + fileName;
    }

    /** Return true if the current platform is CheerpJ, the web browser-based-javascript/webasm-JVM.
     * Useful to check if the simulator is running in a web browser as several behavioural differences
     * over running as a native JVM app exist.
     * @return true if the current platform is CheerpJ, the web browser-based-javascript/webasm-JVM.
     */
    public static boolean onCheerpj() {
        return System.getProperty("os.arch").equals("cheerpj");
    }

    // Captures the running Java thread for persistent communication with JavaScript.
    public static native void nativeSetApplication(CheerpJUtils myApplication);

    // set up interface to javascript on new thread
    public static void setupJavascriptInteface(Baby baby) {
        CheerpJUtils app = new CheerpJUtils(baby);
        new Thread(() -> {
            nativeSetApplication(app);
            System.out.println("Starting cheerpj interface thread");
        }).start();
    }
}
