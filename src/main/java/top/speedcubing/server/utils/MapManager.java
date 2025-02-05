package top.speedcubing.server.utils;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import top.speedcubing.server.speedcubingServer;

public class MapManager {

    public static World install(String fileName, String map, Difficulty difficulty, int time, boolean autoSave) throws Exception {
        speedcubingServer.getInstance().getLogger().info("[MapManager] copying \"" + fileName + "\" to \"" + map + "\"");
        new ProcessBuilder("rm", "-r", map).start().waitFor();
        String file = fileName + ".tar.gz";
        new ProcessBuilder("cp", "/storage/maps/" + file, "./").start().waitFor();
        new ProcessBuilder("tar", "-xvzf", file).start().waitFor();
        new ProcessBuilder("mv", fileName, map).start().waitFor();
        new ProcessBuilder("rm", file).start().waitFor();
        World world = Bukkit.createWorld(new WorldCreator(map));
        world.setDifficulty(difficulty);
        world.setTime(time);
        world.setAutoSave(autoSave);
        return world;
    }
}
