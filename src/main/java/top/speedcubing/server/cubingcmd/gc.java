package top.speedcubing.server.cubingcmd;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import org.bukkit.command.CommandSender;
import top.speedcubing.lib.utils.SystemUtils;
import top.speedcubing.server.system.command.CubingCommand;


public class gc extends CubingCommand {
    public gc() {
        super("gc");
    }

    @Override
    public void execute(CommandSender sender, String command, String[] args) {
        long t = System.currentTimeMillis();
        sender.sendMessage("Before: " + getMemoryUsageInfo());
        System.gc();
        sender.sendMessage("After: " + getMemoryUsageInfo());
        sender.sendMessage("Elapse: " + (System.currentTimeMillis() - t) + "ms");
    }

    public static String getMemoryUsageInfo() {
        MemoryUsage usage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        return ("Used: " + usage.getUsed() / 1048576 + " mib, Heap: " + usage.getCommitted() / 1048576 + " mib" + ", Max: " + SystemUtils.getXmx() / 1048576 + " mib");
    }
}

