package com.ccs.baby.menu;

import com.ccs.baby.core.Baby;
import com.ccs.baby.core.Store;
import com.ccs.baby.ui.CrtPanel;


import com.ccs.baby.io.LoadExample;

import javax.swing.*;

import java.awt.event.KeyEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;


import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.FileSystem;
import java.util.Collections;
import java.util.Enumeration;
import java.util.stream.Stream;

public class ExamplesMenu {

    // Toggle to determine whether to dynamically build the menu of demo programs from the folder structure
    // or use the previous hardcoded logic
    private static final boolean DYNAMIC_EXAMPLES = true;

    private static final String EXAMPLES_FOLDER = "demos";

    private static Store _store;
    private static CrtPanel _crtPanel;
    private static JFrame _frame;

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

            // save refs to objs to avoid passing them throughout the recursive call stack
            _store = store;
            _crtPanel = crtPanel;
            _frame = frame;

            try {
                exampleMenu = createMenuFromResource(exampleMenu, EXAMPLES_FOLDER);
            }
            catch (URISyntaxException exception) {
                System.out.println("Can't find example programs at URI\n" + exception.toString());
            }
            catch (IOException exception) {
                System.out.println(exception.toString());
            }

        } else {
            buildStaticMenu(exampleMenu, store, crtPanel, frame);
        }

        // Add mnemonics (keyboard shortcuts) for macOS, Windows, and Linux
        exampleMenu.setMnemonic(KeyEvent.VK_E); // Alt + E

        return exampleMenu;
    }


    /**
     * Deprecated - Build the old static examples menu that works in JAR or debugger
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
        try {
            diffeqt.addActionListener(new LoadExample(LoadExample.getUriStringForResource("demos/diffeqt.asm"), store, crtPanel, frame));
            baby9.addActionListener(new LoadExample(LoadExample.getUriStringForResource("demos/Baby9.snp"), store, crtPanel, frame));
            primegen.addActionListener(new LoadExample(LoadExample.getUriStringForResource("demos/primegen.asm"), store, crtPanel, frame));
            virpet.addActionListener(new LoadExample(LoadExample.getUriStringForResource("demos/virpet.asm"), store, crtPanel, frame));
            noodleTimer.addActionListener(new LoadExample(LoadExample.getUriStringForResource("demos/noodletimer.snp"), store, crtPanel, frame));
        }
        catch(URISyntaxException ex)
        {
            JOptionPane.showMessageDialog(frame.getContentPane(), "Error building menu of example programs. "  + ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Add items to the examples menu
        menu.add(diffeqt);
        menu.add(baby9);
        menu.add(primegen);
        menu.add(virpet);
        menu.add(noodleTimer);

        

                     
    }

    public static JMenu createMenuFromResource(JMenu rootMenu, String resourcePath) throws URISyntaxException, IOException {
        ClassLoader classLoader = Baby.class.getClassLoader();
        URI uri = classLoader.getResource(resourcePath).toURI();
    
        Path myPath;
        if ("jar".equals(uri.getScheme())) {
            FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
            myPath = fileSystem.getPath(resourcePath);
        } else {
            myPath = Paths.get(uri);
        }
        
        processDirectory(rootMenu, myPath);
        return rootMenu;
    }

    private static void processDirectory(JMenu parentMenu, Path directory) throws IOException {
        try (Stream<Path> paths = Files.walk(directory, 1)) {
            paths.filter(path -> !path.equals(directory))
                 .forEach(path -> {
                     try {
                         if (Files.isDirectory(path)) {
                             JMenu subMenu = new JMenu(path.getFileName().toString());
                             parentMenu.add(subMenu);
                             processDirectory(subMenu, path);
                         } else {
                             String fileName = path.getFileName().toString().toLowerCase();
                             if (fileName.endsWith(".snp") || fileName.endsWith(".asm")) {
                                 JMenuItem menuItem = createMenuItemForFile(path);
                                 parentMenu.add(menuItem);
                             }
                         }
                     } catch (URISyntaxException | IOException e) {
                         System.err.println("Error processing path: " + path);
                         e.printStackTrace();
                     }
                 });
        }
    }
   
/* 
    public static JMenu createMenuFromResource(JMenu rootMenu, String resourcePath) throws URISyntaxException, IOException {

        ClassLoader classLoader = Baby.class.getClassLoader();
        URI uri = classLoader.getResource(resourcePath).toURI();

        Path myPath;
        if ("jar".equals(uri.getScheme())) {
            FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
            myPath = fileSystem.getPath(resourcePath);
        } else {
            myPath = Paths.get(uri);
        }
        
        try (Stream<Path> paths = Files.walk(myPath, 1)) {
            paths.filter(path -> !path.equals(myPath))
                 .forEach(path -> {
                    try {
                         if (Files.isDirectory(path)) {
                             JMenu subMenu = new JMenu(path.getFileName().toString());
                             rootMenu.add(subMenu);
                             addSubMenuItems(subMenu, path);
                         } else {
                            String fileName = path.getFileName().toString().toLowerCase();
                            if(fileName.endsWith(".snp") || fileName.endsWith(".asm")) {
                                JMenuItem menuItem = createMenuItemForFile(path);
                                rootMenu.add(menuItem);
                            }
                         }
                     } catch (URISyntaxException | IOException e) {
                         System.err.println("Error processing path: " + path);
                         e.printStackTrace();
                     }
                 });
        }
        
        return rootMenu;
    }

    
    
    private static void addSubMenuItems(JMenu parentMenu, Path directory) throws IOException {
        try (Stream<Path> paths = Files.walk(directory, 1)) {
            paths.filter(path -> !path.equals(directory))
                 .forEach(path -> {
                     try {
                         if (Files.isDirectory(path)) {
                             JMenu subMenu = new JMenu(path.getFileName().toString());
                             parentMenu.add(subMenu);
                             addSubMenuItems(subMenu, path);
                         } else {
                             JMenuItem menuItem = createMenuItemForFile(path);
                             parentMenu.add(menuItem);
                         }
                     } catch (URISyntaxException | IOException e) {
                         System.err.println("Error processing subdirectory: " + path);
                         e.printStackTrace();
                     }
                 });
        }
    }
        */
    
    private static JMenuItem createMenuItemForFile(Path filePath) throws URISyntaxException, IOException {
        JMenuItem menuItem = new JMenuItem(filePath.getFileName().toString());
        menuItem.addActionListener(new LoadExample(filePath.toUri().toString(), _store, _crtPanel, _frame));
        return menuItem;
    }
    

}