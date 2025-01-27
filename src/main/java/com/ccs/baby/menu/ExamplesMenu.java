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

    private static final String EXTERNAL_PROGRAMS_FOLDER = "baby_programs";
    private static final String EXTERNAL_PROGRAMS_ZIP = "baby_programs.zip";

    private static Store _store;
    private static CrtPanel _crtPanel;
    private static JFrame _frame;

    /**
     * Create the Examples menu reading files from an optional folder and zip file in the same
     * location as the simulator's JAR so that others can add to the Examples menu without
     * editing the code/JAR content.
     *
     * @param store    the store to load the example program into
     * @param crtPanel the CRT panel to display the store
     * @param frame    the frame to display the example program in
     * @return the Examples menu
     */
    public static JMenu createExampleMenu(Store store, CrtPanel crtPanel, JFrame frame) {
        JMenu exampleMenu = new JMenu("Examples");
    
        if (DYNAMIC_EXAMPLES) {

            // save refs to objects to avoid passing throughout recursive call stack
            _store = store;
            _crtPanel = crtPanel;
            _frame = frame;
    
            try {
                // Add built-in examples from resources
                exampleMenu = createMenuFromResource(exampleMenu, EXAMPLES_FOLDER);
    
                // Try to add programs from external folder
                Path jarPath = getJarPath();
                if (jarPath != null) {
                    Path externalFolder = jarPath.getParent().resolve(EXTERNAL_PROGRAMS_FOLDER);
                    Path externalZip = jarPath.getParent().resolve(EXTERNAL_PROGRAMS_ZIP);
    
                    // Add programs from external folder if it exists
                    if (Files.exists(externalFolder) && Files.isDirectory(externalFolder)) {
                        createMenuFromExternalPath(exampleMenu, externalFolder);
                    }
    
                    // Add programs from zip file if it exists
                    if (Files.exists(externalZip) && !Files.isDirectory(externalZip)) {
                        try (FileSystem zipFs = FileSystems.newFileSystem(externalZip, (ClassLoader) null)) {
                            Path root = zipFs.getPath("/");
                            createMenuFromExternalPath(exampleMenu, root);
                        }
                    }
                }
            } catch (URISyntaxException | IOException exception) {
                System.err.println("Error accessing programs: " + exception.toString());
            }
        } else {
            // deprecated approach
            buildStaticMenu(exampleMenu, store, crtPanel, frame);
        }
    
        exampleMenu.setMnemonic(KeyEvent.VK_E);
        return exampleMenu;
    }

    private static Path getJarPath() {
        try {
            URI uri = ExamplesMenu.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            return Paths.get(uri);
        } catch (URISyntaxException e) {
            System.err.println("Error getting JAR path: " + e.toString());
            return null;
        }
    }
  

    /**
     * Start scanning folder within the simulator's JAR/target folder for built-in example programs
     * @param rootMenu
     * @param resourcePath
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
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



    /**
     * Handle building menu entries for optional external folder/zip files of programs
     * @param parentMenu
     * @param directory
     * @throws IOException
     */
    private static void createMenuFromExternalPath(JMenu parentMenu, Path directory) throws IOException {
        try (Stream<Path> paths = Files.walk(directory, 1)) {
            paths.filter(path -> !path.equals(directory))
                 .forEach(path -> {
                     try {
                         if (Files.isDirectory(path)) {
                             JMenu subMenu = new JMenu(path.getFileName().toString());
                             parentMenu.add(subMenu);
                             createMenuFromExternalPath(subMenu, path);
                         } else {
                             String fileName = path.getFileName().toString().toLowerCase();
                             if (fileName.endsWith(".snp") || fileName.endsWith(".asm")) {
                                 JMenuItem menuItem = createMenuItemForFile(path);
                                 parentMenu.add(menuItem);
                             }
                         }
                    } catch (URISyntaxException | IOException e) {
                         System.err.println("Error processing external path: " + path);
                         e.printStackTrace();
                     }
                 });
        }
    }
    

    private static JMenuItem createMenuItemForFile(Path filePath) throws URISyntaxException, IOException {
        JMenuItem menuItem = new JMenuItem(filePath.getFileName().toString());
        String uriString = filePath.toUri().toString();
        menuItem.addActionListener(new LoadExample(uriString, _store, _crtPanel, _frame));
        return menuItem;
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
    

}