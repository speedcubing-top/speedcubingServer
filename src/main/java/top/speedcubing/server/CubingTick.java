package top.speedcubing.server;

import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import top.speedcubing.lib.bukkit.pluginMessage.BungeePluginMessage;
import top.speedcubing.lib.utils.ByteArrayDataBuilder;
import top.speedcubing.server.database.DataCenter;
import top.speedcubing.server.database.Database;
import top.speedcubing.server.events.CubingTickEvent;
import top.speedcubing.server.libs.User;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.Timer;
import java.util.TimerTask;

public class CubingTick {

    public static Timer calcTimer;

    public static void init() {
        calcTimer = new Timer("Cubing-Tick-Thread");
        calcTimer.schedule(new TimerTask() {

            double[] tps;
            final CubingTickEvent event = new CubingTickEvent();
            MemoryUsage usage;

            @Override
            public void run() {
                try {
                    long t = System.currentTimeMillis();
                    usage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
                    tps = MinecraftServer.getServer().recentTps;
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
                            speedcubingServer.tcpClient.send(user.tcpPort, new ByteArrayDataBuilder().writeUTF("cps").writeInt(user.id).writeInt(user.leftClick).writeInt(user.rightClick).toByteArray());
                        if (user.leftClick >= config.LeftCpsLimit || user.rightClick >= config.RightCpsLimit)
                            Bukkit.getScheduler().runTask(speedcubingServer.getPlugin(speedcubingServer.class), () -> user.player.kickPlayer("You are clicking too fast !"));
                        user.leftClick = 0;
                        user.rightClick = 0;
                        if (t - user.lastMove > 300000)
                            BungeePluginMessage.switchServer(user.player, "lobby");
                    }
                    DataCenter.onlineCount = Database.systemConnection.select("SUM(onlinecount)").from("proxies").getInt();
                    event.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1000);
    }
}
