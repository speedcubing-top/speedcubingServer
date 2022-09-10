package top.speedcubing.server.Commands.overrided;

import com.sun.management.OperatingSystemMXBean;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.spigotmc.AsyncCatcher;
import org.spigotmc.SpigotConfig;
import org.spigotmc.WatchdogThread;
import top.speedcubing.lib.utils.TimeFormatter;
import top.speedcubing.server.commandoverrider.OverrideCommandManager;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

public class tps implements OverrideCommandManager.OverridedCommand {
    public void execute(CommandSender commandSender, String message) {
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
        String[] result = {
                "§7-----------------------------"
                , "§6TPS from last 1m, 5m, 15m: " + format(tps[0]) + ", " + format(tps[1]) + ", " + format(tps[2])
                , "§6Server Lag: §a" + (double) Math.round(recenttps * 10000) / 10000 + "%"
                , "§6Runtime Memory (total, free, used): §a" + total / 1048576 + ", " + free / 1048576
                + ", " + (total - free) / 1048576 + " (MB)§e (" + Math.round((total - free) * 10000D / runtime.maxMemory()) / 100 + "%)"
                , "§6Total CPU Usage: §a" + new DecimalFormat("#.##").format(ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class).getSystemCpuLoad() * 100) + "%"
                , "§6Online: §a" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers()
                , "§6Uptime: §a" + new TimeFormatter(ticks * 50L, TimeUnit.MILLISECONDS).format("%D%d %h%h %m%m %s%s %ms%ms", false) + "§e (" + ticks + " ticks)"
                , "§6Entities: §a" + entities
                , "§6Loaded Chunks: §a" + chunks
                , "§7-----------------------------"};
        commandSender.sendMessage(result);
    }

    private String format(double tps) {
        return ((tps > 18.0) ? "§a" : (tps > 16.0) ? "§e" : "§c") + ((tps > 20.0) ? "*" : "") + Math.min(Math.round(tps * 100.0) / 100.0, 20.0);
    }
}
