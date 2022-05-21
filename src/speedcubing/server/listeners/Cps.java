package speedcubing.server.listeners;

import speedcubing.server.config;
import speedcubing.server.libs.User;
import speedcubing.server.speedcubingServer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Cps implements Listener {
    public static Map<UUID, Integer[]> Counter = new ConcurrentHashMap<>();
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
                        speedcubingServer.tcp.send(User.getUser(set).tcpPort, "c|" + set + "|" + a[0] + "|" + a[1]);
                }
                for(Map.Entry<UUID,Integer[]> a : Counter.entrySet()){
                    if (a.getValue()[0] >= config.LeftCpsLimit || a.getValue()[1] >= config.RightCpsLimit)
                        Bukkit.getScheduler().runTask(speedcubingServer.getPlugin(speedcubingServer.class), () -> Bukkit.getPlayer(a.getKey()).kickPlayer("You are clicking too fast !"));
                }
                Counter.replaceAll((x, v) -> new Integer[]{0, 0});
            }
        }, 0, 1000);
    }
}
