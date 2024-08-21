package top.speedcubing.server.system.command;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class CubingCommandLoader {
    public static boolean loadCommands(String dir){
        dir = dir.replace(".","/");
        try {
            File jarFile = new File(CubingCommandLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            String jarFilePath = jarFile.getAbsolutePath();

            try (JarFile jar = new JarFile(jarFilePath)) {
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String entryName = entry.getName();
                    if (entryName.endsWith(".class") && entryName.startsWith("top/speedcubing/server/cubingcmd/")) {
                        String className = entryName.replace('/', '.').substring(0, entryName.length() - 6);
                        try {
                            Class.forName(className).getDeclaredConstructor().newInstance();
                            System.out.println("Loaded command: " + className);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return true;
        } catch (URISyntaxException | IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
