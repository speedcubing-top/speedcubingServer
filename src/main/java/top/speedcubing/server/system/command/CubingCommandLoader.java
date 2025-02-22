package top.speedcubing.server.system.command;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.bukkit.Bukkit;
import top.speedcubing.server.speedcubingServer;

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
                    if (!entryName.endsWith(".class") || !entryName.startsWith(packageName)) {
                        continue;
                    }
                    String className = entryName.replace('/', '.').substring(0, entryName.length() - 6);
                    Class<?> clazz;
                    try {
                        clazz = Class.forName(className);
                        if (!CubingCommand.class.isAssignableFrom(clazz)) {
                            continue;
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        continue;
                    }
                    try {
                        CubingCommand command = (CubingCommand) clazz.getDeclaredConstructor().newInstance();
                        if (command.shouldLoad()) {
                            command.load();
                            speedcubingServer.getInstance().getLogger().info("Loaded command class: " + className + ", command: " + command.getAlias());
                        }
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (URISyntaxException | IOException ex) {
            ex.printStackTrace();
            Bukkit.getServer().shutdown();
        }
    }
}
