package com.ccs.baby.menu;

import com.ccs.baby.core.Store;
import com.ccs.baby.ui.CrtPanel;
import com.ccs.baby.io.LoadExample;

import javax.swing.*;

import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class ExamplesMenu {

    // Toggle to determine whether to dynamically build the menu of demo programs from the folder structure
    // (experimental) or use the previous hardcoded logic
    private static final boolean DYNAMIC_EXAMPLES = false;
    private static final String EXAMPLES_FOLDER = "demos/";

    /**
     * Create the Examples menu
     *
     * @param store    the store to load the example program into
     * @param crtPanel the CRT panel to display the store
     * @param frame    the frame to display the example program in
     * @return the Examples menu
     */
    public static JMenu createExampleMenu(Store store, CrtPanel crtPanel, JFrame frame) {
        JMenu exampleMenu = new JMenu("Examples");

        if (DYNAMIC_EXAMPLES) {
            // TODO: Resolve issues with absolute and relative paths, and the differences between loading files from a
            //  JAR versus iterating over files in a folder on disk. Ensure compatibility in both debugging and JAR
            //  execution modes.

            try {
                URL resource = ExamplesMenu.class.getClassLoader().getResource(EXAMPLES_FOLDER);
                if (resource == null) {
                    throw new URISyntaxException(EXAMPLES_FOLDER, "Resource not found");
                }
                String uriString = resource.toURI().toString(); // Previous logic passed in this string, but only worked in debugger
                buildDynamicMenu(uriString, exampleMenu, store, crtPanel, frame);
            } catch (URISyntaxException exception) {
                System.out.println("Can't find example programs at URI");
            }

        } else {
            buildStaticMenu(exampleMenu, store, crtPanel, frame);
        }

        // Add mnemonics (keyboard shortcuts) for macOS, Windows, and Linux
        exampleMenu.setMnemonic(KeyEvent.VK_E); // Alt + E

        return exampleMenu;
    }

    /**
     * Build the old static examples menu that worked in JAR or debugger
     *
     * @param menu     the menu to add the items to
     * @param store    the store to load the example program into
     * @param crtPanel the CRT panel to display the store
     * @param frame    the frame to display the example program in
     */
    private static void buildStaticMenu(JMenu menu, Store store, CrtPanel crtPanel, JFrame frame) {

        // Create menu items
        JMenuItem diffeqt = new JMenuItem("demos/diffeqt.asm");
        JMenuItem baby9 = new JMenuItem("demos/Baby9.snp");
        JMenuItem primegen = new JMenuItem("demos/primegen.asm");
        JMenuItem virpet = new JMenuItem("demos/virpet.asm");
        JMenuItem noodleTimer = new JMenuItem("demos/noodletimer.snp");

        // Add action listeners for each item
        diffeqt.addActionListener(new LoadExample("demos/diffeqt.asm", store, crtPanel, frame));
        baby9.addActionListener(new LoadExample("demos/Baby9.snp", store, crtPanel, frame));
        primegen.addActionListener(new LoadExample("demos/primegen.asm", store, crtPanel, frame));
        virpet.addActionListener(new LoadExample("demos/virpet.asm", store, crtPanel, frame));
        noodleTimer.addActionListener(new LoadExample("demos/noodletimer.snp", store, crtPanel, frame));

        // Add items to the examples menu
        menu.add(diffeqt);
        menu.add(baby9);
        menu.add(primegen);
        menu.add(virpet);
        menu.add(noodleTimer);
    }

    /**
     * Build the dynamic example programs menu from folder/file structure within JAR
     *
     * @param menu     the menu to add the items to
     * @param store    the store to load the example program into
     * @param crtPanel the CRT panel to display the store
     * @param frame    the frame to display the example program in
     */
    private static void buildDynamicMenu(String uriString, JMenu menu, Store store, CrtPanel crtPanel, JFrame frame) {

        // TODO: The current implementation works fine in the debugger but is broken when running in two modes:
        //  1. A folder structure for when running in debugger
        //  2. JAR mode when files are packaged in a JAR
        //  Additionally, we may want to load files from a folder outside the JAR if users provide more programs.
        //  Consider removing in-JAR files to make the application more self-contained, especially for running in a WASM JVM.

        URI uri;

        try {
            uri = new URI(uriString);
            System.out.println("uri:" + uri);
        } catch (URISyntaxException exception) {
            // Give up if it can't get uri
            System.err.println("Can't find example programs at URI:" + uriString);
            return;
        }

        File dir = new File(uri);
        File[] files = dir.listFiles();

        if (files != null) {
            for (File nextFile : files) {
                System.out.println(nextFile.isDirectory() + ": " + nextFile.getName());

                if (nextFile.isFile()) {
                    if (nextFile.getName().endsWith(".snp") || nextFile.getName().endsWith(".asm")) {
                        JMenuItem menuItem = new JMenuItem(nextFile.getName());
                        String temp = nextFile.getPath(); //$ .getAbsolutePath();
                        menuItem.addActionListener(new LoadExample(nextFile.getAbsolutePath(), store, crtPanel, frame));
                        menu.add(menuItem);
                    }
                } else if (nextFile.isDirectory()) {
                    JMenu newSubMenu = new JMenu(nextFile.getName());
                    menu.add(newSubMenu);

                    // Recursively call this method on contents of the next folder down
                    buildDynamicMenu("file:" + nextFile.getAbsolutePath(), newSubMenu, store, crtPanel, frame);
                }
            }
        } else {
            System.err.println("Error: Unable to list files in directory " + dir.getAbsolutePath());
        }


        /*
        // experimental
        Path pathO;

        if ("jar".equals(uri.getScheme()))
        {
            FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap(), null);
            pathO = fileSystem.getPath(path);
        }
        else
        {
            pathO = Paths.get(uri);
        }

        //File dir = new File(pathO.getResource().toUri());
        uri = getClass().getClassLoader().getResource(path).toURI();

        if (uri == null) {
            // error - missing folder
        }
        else
        {
            File dir = new File(uri);
            for (File nextFile : dir.listFiles())
            {
                // Do something with nextFile
                System.out.println(nextFile.getName());
            }
        }
        */

    }


}