package cubingserver.utils;

import net.minecraft.server.v1_8_R3.GameRules;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

public class WorldUtils {
    public static void clearGameRules(World world) {
        GameRules m = new GameRules();
        TreeMap<String,?> map = ((CraftWorld)world).getHandle().getGameRules().a;
        Set<String> remove = new HashSet<>();
        for (Object s : map.keySet()) {
            if (!m.contains((String) s))
                remove.add((String) s);
        }
        for (String s : remove) {
            map.remove(s);
        }
    }
    public static void resetGameRules(World world) {
        ((CraftWorld) world).getHandle().worldData.K = new GameRules();
    }
}
