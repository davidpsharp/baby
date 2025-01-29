package com.ccs.baby.menu;

import com.ccs.baby.core.Baby;
import com.ccs.baby.core.Store;
import com.ccs.baby.ui.CrtPanel;
import com.ccs.baby.utils.MiscUtils;
import com.ccs.baby.io.LoadExample;

import javax.swing.*;

import java.awt.event.KeyEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.io.*;
import java.util.Comparator;
import java.util.jar.*;
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

                // experimental
                //scanJarManually(EXAMPLES_FOLDER);

                // Add built-in examples from resources
                if(!MiscUtils.onCheerpj())
                    exampleMenu = createMenuFromResource(exampleMenu, EXAMPLES_FOLDER);
    
                // Try to add programs from external folder
                Path jarPath = getJarPath();
                if (jarPath != null) {
                    Path externalFolder = jarPath.getParent().resolve(EXTERNAL_PROGRAMS_FOLDER);
                    Path externalZip = jarPath.getParent().resolve(EXTERNAL_PROGRAMS_ZIP);
    
                    // Add programs from external folder if it exists)
                    if (Files.exists(externalFolder) && Files.isDirectory(externalFolder)) {
                        createMenuFromExternalPath(exampleMenu, externalFolder);
                    }
    
                    // Add programs from zip file if it exists
                    if (Files.exists(externalZip) && !Files.isDirectory(externalZip)) {
                        createMenuFromZipFile(exampleMenu, externalZip);
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
  

    // experimental attempt to read examples from JAR when running in cheerpj - basically works, merge in
    // functionality from createMenuFromZipFile to sort and scan tree and setup load with getUriStringForResource
    private static void scanJarManually(String folder) throws IOException {
    
        System.out.println("jarpath:" + getJarPath());
        
        try (java.util.jar.JarFile jarFile = new java.util.jar.JarFile(getJarPath().toString())) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    System.out.println("dir: " + entry.getName());
                }
                else {
                    InputStream input = jarFile.getInputStream(entry);
                    System.out.println("file: " + entry.getName());
                }
                
            }
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
            System.out.println("createMenu uri:" + uri.toString());
            FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
            myPath = fileSystem.getPath(resourcePath);
        } else {
            myPath = Paths.get(uri);
        }

        System.out.println("createMenu myPath:" + myPath);
        
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
    

    /**
     * Create menu items from a zip file's contents
     * Created as cheerpj doesn't seem to like newFileSystem() at all so scanning directly
     * @param parentMenu The menu to add items to
     * @param zipPath Path to the zip file
     * @throws IOException If there's an error reading the zip file
     */
    private static void createMenuFromZipFile(JMenu parentMenu, Path zipPath) throws IOException {
        try (ZipFile zipFile = new ZipFile(zipPath.toFile())) {
            // Create a map of menus for each directory path
            Map<String, JMenu> menuMap = new HashMap<>();
            menuMap.put("", parentMenu);

            // Sort entries to ensure directories are processed before their contents
            List<? extends ZipEntry> entries = Collections.list(zipFile.entries());
            entries.sort(Comparator.comparing(ZipEntry::getName));

            for (ZipEntry entry : entries) {
                String entryName = entry.getName();
                
                // Skip directories and non-relevant files
                if (entry.isDirectory()) {
                    // Create menu for this directory
                    String dirPath = entryName.substring(0, entryName.length() - 1); // remove trailing slash
                    String parentPath = new File(dirPath).getParent();
                    if (parentPath == null) parentPath = "";
                    
                    JMenu parentPathMenu = menuMap.get(parentPath);
                    if (parentPathMenu != null) {
                        JMenu dirMenu = new JMenu(new File(dirPath).getName());
                        parentPathMenu.add(dirMenu);
                        menuMap.put(dirPath, dirMenu);
                    }
                } else {
                    String fileName = new File(entryName).getName().toLowerCase();
                    if (fileName.endsWith(".snp") || fileName.endsWith(".asm")) {
                        // Get the parent directory path
                        String parentPath = new File(entryName).getParent();
                        if (parentPath == null) parentPath = "";
                        
                        // Get the menu for this file's directory
                        JMenu targetMenu = menuMap.get(parentPath);
                        if (targetMenu != null) {
                            // Create menu item
                            JMenuItem menuItem = new JMenuItem(fileName);
                            String uriString = "jar:" + zipPath.toUri() + "!" + entryName;
                            // TODO: the uriString here doesn't work with loading files when clicked on
                            menuItem.addActionListener(new LoadExample(uriString, _store, _crtPanel, _frame));
                            targetMenu.add(menuItem);
                        }
                    }
                }
            }
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