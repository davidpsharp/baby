package com.ccs.baby.menu;

import com.ccs.baby.core.Store;
import com.ccs.baby.ui.CrtPanel;
import com.ccs.baby.io.LoadExample;

import javax.swing.*;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class ExamplesMenu {

    // gate on whether to build menu of demo programs dynamically from whatever is in folder (experimental) OR old hardcoded logic used previously
    private static final boolean DYNAMIC_EXAMPLES = false;


    public static JMenu createExampleMenu(Store store, CrtPanel crtPanel, JFrame frame) {
        JMenu exampleMenu = new JMenu("Examples");

        // build example programs menu from folder/file structure within JAR
        if (DYNAMIC_EXAMPLES) {

            // TODO: getting in a mess at the moment w absolute & relative paths AND also difference between loading files from JAR versus iterating over files in folder on disk
            String uriString;
            try {
                uriString = getClass().getClassLoader().getResource("demos/").toURI().toString(); // prev passed in thsi string but only worked in debugger
                System.out.println("demos Uri:" + uriString);
                buildExamplesMenu(uriString, exampleMenu, store, crtPanel, frame);
            } catch (URISyntaxException exception) {
                System.out.println("can't find demos");
            }
        } else {
            // old static examples menu - worked in JAR or debugger
            JMenuItem diffeqt = new JMenuItem("demos/diffeqt.asm");
            JMenuItem baby9 = new JMenuItem("demos/Baby9.snp");
            JMenuItem primegen = new JMenuItem("demos/primegen.asm");
            JMenuItem virpet = new JMenuItem("demos/virpet.asm");
            JMenuItem noodleTimer = new JMenuItem("demos/noodletimer.snp");

            diffeqt.addActionListener(new LoadExample("demos/diffeqt.asm", store, crtPanel, frame));
            baby9.addActionListener(new LoadExample("demos/Baby9.snp", store, crtPanel, frame));
            primegen.addActionListener(new LoadExample("demos/primegen.asm", store, crtPanel, frame));
            virpet.addActionListener(new LoadExample("demos/virpet.asm", store, crtPanel, frame));
            noodleTimer.addActionListener(new LoadExample("demos/noodletimer.snp", store, crtPanel, frame));

            exampleMenu.add(diffeqt);
            exampleMenu.add(baby9);
            exampleMenu.add(primegen);
            exampleMenu.add(virpet);
            exampleMenu.add(noodleTimer);
        }


        return exampleMenu;
    }

    private void buildExamplesMenu(String uriString, JMenu menu, Store store, CrtPanel crtPanel, JFrame frame) {
        URI uri;
        try {
            uri = new URI(uriString);
            System.out.println("uri:" + uri.toString());
        } catch (URISyntaxException exception) {
            // give up if can't get uri
            System.err.println("Can't find example programs at URI:" + uriString);
            return;
        }

        // TODO: works fine in debugger but BADLY broken as needs to run in two modes, with a folder structure for when running in debugger and with JAR mode when files all in a JAR
        // plus may want to load files from folder outside the JAR if users provide more programs. Could ditch in-JAR files but then less self contained, esp if
        // attempting to run in a WASM JVM for example.

        File dir = new File(uri);
        for (File nextFile : dir.listFiles()) {
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
                // recursively call this method on contents of the next folder down
                buildExamplesMenu("file:" + nextFile.getAbsolutePath(), newSubMenu, store, crtPanel, frame);
            }
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