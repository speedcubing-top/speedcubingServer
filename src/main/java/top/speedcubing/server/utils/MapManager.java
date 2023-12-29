package top.speedcubing.server.utils;

import org.bukkit.*;

import java.net.*;
import java.nio.file.*;

public class MapManager {

    public static World install(String url, String map, Difficulty difficulty, int time, boolean autoSave) throws Exception {
        long t = System.currentTimeMillis();
        World world = Bukkit.getWorld(map);
        System.out.println("[MapManager] installing \"" + map + "\"" + (url == null ? "" : (" from \"" + map + "\"")));
        if (url != null) {
            new ProcessBuilder("rm", "-r", map).start().waitFor();
            String file = url + ".tar.gz";
            HttpURLConnection connection = (HttpURLConnection) new URL("https://speedcubing.top/maps/" + file).openConnection();
            connection.addRequestProperty("User-Agent", "Mozilla/4.0");
            connection.setRequestProperty("X-UUID-Key", "2ad5a999-854a-4c5e-adbc-aa434deb79d3");
            Files.copy(connection.getInputStream(), Paths.get(file), StandardCopyOption.REPLACE_EXISTING);
            new ProcessBuilder("tar", "-xvzf", file).start().waitFor();
            new ProcessBuilder("mv", url, map).start().waitFor();
            new ProcessBuilder("rm", file).start().waitFor();
            world = Bukkit.createWorld(new WorldCreator(map));
        }
        world.setDifficulty(difficulty);
        world.setTime(time);
        world.setAutoSave(autoSave);
        t = System.currentTimeMillis() - t;
        System.out.println("[MapManager] \"" + map + "\" installing finished. (" + String.format("%.3fs", t / 1000D) + ")");
        return world;
    }
}
