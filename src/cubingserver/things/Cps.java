package cubingserver.things;

import cubingserver.config;
import cubingserver.connection.SocketUtils;
import cubingserver.speedcubingServer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.*;

public class Cps implements Listener {
    public static Map<UUID, Integer[]> Counter = new HashMap<>();
    public static Set<UUID> CpsListening = new HashSet<>();

    @EventHandler
    public void dwd(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void CpsCheck(PlayerInteractEvent e) {
        switch (e.getAction()) {
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                Counter.get(e.getPlayer().getUniqueId())[0] += 1;
                break;
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                Counter.get(e.getPlayer().getUniqueId())[1] += 1;
                break;
        }
    }

    public void Load() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                for (UUID set : CpsListening) {
                    Integer[] a = Counter.get(set);
                    if (a != null)
                        SocketUtils.sendData(speedcubingServer.BungeeTCPPort, "c|" + set + "|" + a[0] + "|" + a[1], 100);
                }
                Counter.forEach((k, v) -> {
                    if (v[0] >= config.LeftCpsLimit || v[1] >= config.RightCpsLimit)
                        Bukkit.getScheduler().runTask(speedcubingServer.getPlugin(speedcubingServer.class), () -> Bukkit.getPlayer(k).kickPlayer("You are clicking too fast !"));
                });
                Counter.replaceAll((x, v) -> new Integer[]{0, 0});
            }
        }, 0, 1000);
    }
}
