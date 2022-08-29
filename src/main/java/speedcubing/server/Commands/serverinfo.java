package speedcubing.server.Commands;

import com.sun.management.OperatingSystemMXBean;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

public class serverinfo implements CommandExecutor, TabExecutor {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        int ticks = MinecraftServer.currentTick - MinecraftServer.firstTick;
        double[] tps = MinecraftServer.getServer().recentTps;
        double recenttps = (double) Math.round((1.0D - tps[0] / 20.0D) * 100.0D);
        if (recenttps < 0.0D)
            recenttps = 0.0D;
        Runtime runtime = Runtime.getRuntime();
        long total = runtime.totalMemory();
        long free = runtime.freeMemory();
        int entities = 0;
        int chunks = 0;
        for (World world : Bukkit.getWorlds()) {
            entities += world.getEntities().size();
            chunks += world.getLoadedChunks().length;
        }
        String[] message = {
                "§7-----------------------------"
                , "§6TPS from last 1m, 5m, 15m: " + format(tps[0]) + ", " + format(tps[1]) + ", " + format(tps[2])
                , "§6Server Lag: §a" + (double) Math.round(recenttps * 10000) / 10000 + "%"
                , "§6Runtime Memory (total, free, used): §a" + total / 1048576 + ", " + free / 1048576
                + ", " + (total - free) / 1048576 + " (MB)§e (" + Math.round((total - free) * 10000D / runtime.maxMemory()) / 100 + "%)"
                , "§6Total CPU Usage: §a" + new DecimalFormat("#.##").format(ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class).getSystemCpuLoad() * 100) + "%"
                , "§6Online: §a" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers()
                , "§6Uptime: §a" + format(ticks) + "§e (" + ticks + " ticks)"
                , "§6Entities: §a" + entities
                , "§6Loaded Chunks: §a" + chunks
                , "§7-----------------------------"};
        commandSender.sendMessage(message);
        return true;
    }

    public static String format(long seconds) {
        return seconds / 1728000 + "d " + String.format("%02d", (seconds / 72000) % 24) + "h " + String.format("%02d", (seconds / 1200) % 60) + "m " + String.format("%02d", (seconds / 20) % 60) + "s";
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return Collections.emptyList();
    }

    private String format(double tps) {
        return ((tps > 18.0) ? "§a" : (tps > 16.0) ? "§e" : "§c") + ((tps > 20.0) ? "*" : "") + Math.min(Math.round(tps * 100.0) / 100.0, 20.0);
    }
}
