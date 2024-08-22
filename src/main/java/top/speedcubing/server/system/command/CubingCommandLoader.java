package top.speedcubing.server.system.command;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.bukkit.Bukkit;

public class CubingCommandLoader {
    public static void loadCommands(String packageName, Class<?> whateverClass) {
        packageName = packageName.replace(".", "/") + "/";
        try {
            File jarFile = new File(whateverClass.getProtectionDomain().getCodeSource().getLocation().toURI());
            String jarFilePath = jarFile.getAbsolutePath();

            try (JarFile jar = new JarFile(jarFilePath)) {
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String entryName = entry.getName();
                    System.out.println(entryName);
                    if (entryName.endsWith(".class") && entryName.startsWith(packageName)) {
                        String className = entryName.replace('/', '.').substring(0, entryName.length() - 6);
                        try {
                            CubingCommand command = (CubingCommand) Class.forName(className).getDeclaredConstructor().newInstance();
                            if (command.shouldLoad()) {
                                command.load();
                                System.out.println("Loaded command class: " + className + ", command: " + command.getAlias());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (URISyntaxException | IOException ex) {
            ex.printStackTrace();
            Bukkit.getServer().shutdown();
        }
    }
}
