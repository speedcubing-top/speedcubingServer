package top.speedcubing.server.cubinglistener;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import top.speedcubing.common.database.Database;
import top.speedcubing.common.events.CubingTickEvent;
import top.speedcubing.lib.bukkit.PlayerUtils;
import top.speedcubing.lib.bukkit.pluginMessage.BungeePluginMessage;
import top.speedcubing.lib.eventbus.CubingEventHandler;
import top.speedcubing.lib.utils.bytes.ByteArrayBuffer;
import top.speedcubing.lib.utils.sockets.TCPClient;
import top.speedcubing.server.player.User;

public class CubingTick {

    @CubingEventHandler
    public void cubingTickEvent(CubingTickEvent e) {
        long t = System.currentTimeMillis();
        MemoryUsage usage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        double[] tps = MinecraftServer.getServer().recentTps;
        Database.systemConnection.update(
                "servers",
                "onlinecount=" + Bukkit.getOnlinePlayers().size() +
                        ",ram_heap=" + usage.getCommitted() / 1048576 +
                        ",ram_used=" + usage.getUsed() / 1048576 +
                        ",tps1=" + Math.round(tps[0] * 100.0) / 100.0 +
                        ",tps2=" + Math.round(tps[1] * 100.0) / 100.0 +
                        ",tps3=" + Math.round(tps[2] * 100.0) / 100.0,
                "name='" + Bukkit.getServerName() + "'"
        );
        for (User user : User.usersByID.values()) {
            if (user.listened)
                TCPClient.write(user.proxy, new ByteArrayBuffer().writeUTF("cps").writeInt(user.id).writeInt(user.leftCPS).writeInt(user.rightCPS).toByteArray());
                        /*
                        if (user.leftClick >= config.LeftCpsLimit || user.rightClick >= config.RightCpsLimit)
                            Bukkit.getScheduler().runTask(speedcubingServer.getInstance(), () -> user.player.kickPlayer("You are clicking too fast !"));
                        */

            if (user.nickState() && user.vanished)
                PlayerUtils.sendActionBar(user.player, "You are currently §cNICKED §fand §cVANISHED");
            else if (user.vanished)
                PlayerUtils.sendActionBar(user.player, "You are currently §cVANISHED");
            else if (user.nickState())
                PlayerUtils.sendActionBar(user.player, "You are currently §cNICKED");

            if (!Bukkit.getServerName().equals("limbo"))
                if (t - user.lastMove > 300000)
                    BungeePluginMessage.switchServer(user.player, "limbo");
        }
    }
}
