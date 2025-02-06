package com.ccs.baby.menu;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import com.ccs.baby.controller.CrtPanelController;
import com.ccs.baby.core.Baby;
import com.ccs.baby.core.Store;
import com.ccs.baby.io.LoadExample;
import com.ccs.baby.utils.CheerpJUtils;

public class ExamplesMenu {

    // Toggle to determine whether to dynamically build the menu of demo programs from the folder structure
    // or use the previous hardcoded logic
    private static final boolean DYNAMIC_EXAMPLES = true;

    private static final String EXAMPLES_FOLDER = "demos";

    private static final String EXTERNAL_PROGRAMS_FOLDER = "baby_programs";
    private static final String EXTERNAL_PROGRAMS_ZIP = "baby_programs.zip";

    private static Store _store;
    private static CrtPanelController _crtPanelController;
    private static JFrame _frame;

    /**
     * Create the Examples menu reading files from an optional folder and zip file in the same
     * location as the simulator's JAR so that others can add to the Examples menu without
     * editing the code/JAR content.
     *
     * @param store    the store to load the example program into
     * @param crtPanelController the CRT panel to display the store
     * @param frame    the frame to display the example program in
     * @return the Examples menu
     */
    public static JMenu createExampleMenu(Store store, CrtPanelController crtPanelController, JFrame frame) {
        JMenu exampleMenu = new JMenu("Examples");
    
        if (DYNAMIC_EXAMPLES) {

            // save refs to objects to avoid passing throughout recursive call stack
            _store = store;
            _crtPanelController = crtPanelController;
            _frame = frame;
    
            try {

                // Add built-in examples from resources
                if(CheerpJUtils.onCheerpj())
                    createMenuFromJar(exampleMenu, EXAMPLES_FOLDER);    
                else
                    createMenuFromResource(exampleMenu, EXAMPLES_FOLDER);
    
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
            buildStaticMenu(exampleMenu, store, crtPanelController, frame);
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
     * Scan a JAR file manually and create menu items from its contents.
     * This method is used when running in CheerpJ environment where newFileSystem() used by
     * createMenuFromResource() doesn't work.
     * @param parentMenu The menu to add items to
     * @param folder The folder path within the JAR to scan
     * @throws IOException If there's an error reading the JAR file
     */
    private static void createMenuFromJar(JMenu parentMenu, String folder) throws IOException, URISyntaxException {
        try (java.util.jar.JarFile jarFile = new java.util.jar.JarFile(getJarPath().toString())) {
            // Create a map of menus for each directory path
            Map<String, JMenu> menuMap = new HashMap<>();
            menuMap.put("", parentMenu);

            // Sort entries to ensure directories are processed before their contents
            List<JarEntry> entries = Collections.list(jarFile.entries());
            entries.sort(Comparator.comparing(JarEntry::getName));

            // First pass: identify directories that contain relevant files
            Set<String> relevantDirs = new HashSet<>();
            for (JarEntry entry : entries) {
                if (!entry.isDirectory()) {
                    String fileName = new File(entry.getName()).getName().toLowerCase();
                    if (fileName.endsWith(".snp") || fileName.endsWith(".asm")) {
                        // Add all parent directories of this file to the relevant dirs set
                        String parentPath = new File(entry.getName()).getParent();
                        while (parentPath != null) {
                            relevantDirs.add(parentPath);
                            parentPath = new File(parentPath).getParent();
                        }
                        // Also add empty string for root directory if file is in root
                        if (new File(entry.getName()).getParent() == null) {
                            relevantDirs.add("");
                        }
                    }
                }
            }

            // Second pass: create menus only for directories that contain relevant files
            for (JarEntry entry : entries) {
                String entryName = entry.getName();
                
                if (entry.isDirectory()) {
                    // Only create menu for this directory if it contains relevant files
                    String dirPath = entryName.substring(0, entryName.length() - 1); // remove trailing slash
                    if (relevantDirs.contains(dirPath)) {
                        String parentPath = new File(dirPath).getParent();
                        if (parentPath == null) parentPath = "";
                        
                        JMenu parentPathMenu = menuMap.get(parentPath);
                        if (parentPathMenu != null) {
                            JMenu dirMenu = new JMenu(new File(dirPath).getName());
                            parentPathMenu.add(dirMenu);
                            menuMap.put(dirPath, dirMenu);
                        }
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
                            String uriString = LoadExample.getUriStringForResource(entryName);
                            menuItem.addActionListener(new LoadExample(uriString, _store, _crtPanelController, _frame));
                            targetMenu.add(menuItem);
                        }
                    }
                }
            }
        }
    }

    /**
     * Start scanning folder within the simulator's JAR/target folder for built-in example programs
     * This works great when running the JAR or in Debugger but not on CheerpJ as doesn't support newFileSystem()
     * (Errors with ProviderNotFoundException: Provider "jar" not found) so have to use createMenuFromJar() on CheerpJ
     * @param rootMenu
     * @param resourcePath
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static void createMenuFromResource(JMenu rootMenu, String resourcePath) throws URISyntaxException, IOException {
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

            // First pass: identify directories that contain relevant files
            Set<String> relevantDirs = new HashSet<>();
            for (ZipEntry entry : entries) {
                if (!entry.isDirectory()) {
                    String fileName = new File(entry.getName()).getName().toLowerCase();
                    if (fileName.endsWith(".snp") || fileName.endsWith(".asm")) {
                        // Add all parent directories of this file to the relevant dirs set
                        String parentPath = new File(entry.getName()).getParent();
                        while (parentPath != null) {
                            relevantDirs.add(parentPath);
                            parentPath = new File(parentPath).getParent();
                        }
                        // Also add empty string for root directory if file is in root
                        if (new File(entry.getName()).getParent() == null) {
                            relevantDirs.add("");
                        }
                    }
                }
            }

            // Second pass: create menus only for directories that contain relevant files
            for (ZipEntry entry : entries) {
                String entryName = entry.getName();
                
                if (entry.isDirectory()) {
                    // Only create menu for this directory if it contains relevant files
                    String dirPath = entryName.substring(0, entryName.length() - 1); // remove trailing slash
                    if (relevantDirs.contains(dirPath)) {
                        String parentPath = new File(dirPath).getParent();
                        if (parentPath == null) parentPath = "";
                        
                        JMenu parentPathMenu = menuMap.get(parentPath);
                        if (parentPathMenu != null) {
                            JMenu dirMenu = new JMenu(new File(dirPath).getName());
                            parentPathMenu.add(dirMenu);
                            menuMap.put(dirPath, dirMenu);
                        }
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
                            String uriString = "jar:" + zipPath.toUri() + "!/" + entryName;
                            menuItem.addActionListener(new LoadExample(uriString, _store, _crtPanelController, _frame));
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
        menuItem.addActionListener(new LoadExample(uriString, _store, _crtPanelController, _frame));
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
    private static void buildStaticMenu(JMenu menu, Store store, CrtPanelController crtPanelController, JFrame frame) {

        // Create menu items
        JMenuItem diffeqt = new JMenuItem("demos/diffeqt.asm");
        JMenuItem baby9 = new JMenuItem("demos/Baby9.snp");
        JMenuItem primegen = new JMenuItem("demos/primegen.asm");
        JMenuItem virpet = new JMenuItem("demos/virpet.asm");
        JMenuItem noodleTimer = new JMenuItem("demos/noodletimer.snp");
        
        // Add action listeners for each item
        try {
            diffeqt.addActionListener(new LoadExample(LoadExample.getUriStringForResource("demos/diffeqt.asm"), store, crtPanelController, frame));
            baby9.addActionListener(new LoadExample(LoadExample.getUriStringForResource("demos/Baby9.snp"), store, crtPanelController, frame));
            primegen.addActionListener(new LoadExample(LoadExample.getUriStringForResource("demos/primegen.asm"), store, crtPanelController, frame));
            virpet.addActionListener(new LoadExample(LoadExample.getUriStringForResource("demos/virpet.asm"), store, crtPanelController, frame));
            noodleTimer.addActionListener(new LoadExample(LoadExample.getUriStringForResource("demos/noodletimer.snp"), store, crtPanelController, frame));
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