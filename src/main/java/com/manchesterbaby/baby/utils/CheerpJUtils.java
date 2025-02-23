package com.manchesterbaby.baby.utils;

import com.manchesterbaby.baby.core.Baby;

/** CheerpJUtils handles interaction between Java and JavaScript when running simulator on the CheerpJ JVM in a browser */
public class CheerpJUtils {

    private Baby _baby;

    // all as per CheerpJ docs at https://cheerpj.com/docs/tutorials/interoperability-tutorial.html 

    // ask javascript to have user select a file
    public static native void getFileForSimulator();

    public CheerpJUtils(Baby baby) {
        _baby = baby;
    }

    // receives data from JavaScript and processes it, usually file copied to /str/...
    public String openFile(String fileName) {
        System.out.println("Java received: " + fileName);
        // thread safety probably not needed, if the machine is running then the behaviour is beyond undefined anyway...
        _baby.openFile(fileName);
        return "Java received: " + fileName;
    }

    // called from javascript when the browser zoom level changes
    public void onBrowserZoomChange(double newPixelRatio) {
        System.out.println("Java side: browser zoom level changed to: " + newPixelRatio);
        // browser zoom changing tends to mess up so repaint the display
        _baby.repaint();
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
            Thread.currentThread().setName("CheerpJ Java-side Interface");
            nativeSetApplication(app);
            System.out.println("Starting cheerpj interface thread");
        }).start();
    }
}

