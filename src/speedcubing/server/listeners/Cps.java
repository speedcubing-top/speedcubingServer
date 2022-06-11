package speedcubing.server.listeners;

import speedcubing.server.config;
import speedcubing.server.libs.User;
import speedcubing.server.speedcubingServer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.lang.reflect.Member;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Cps implements Listener {

    @EventHandler
    public void dwd(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void CpsCheck(PlayerInteractEvent e) {
        switch (e.getAction()) {
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                User.getUser(e.getPlayer().getUniqueId()).leftClick += 1;
                break;
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                User.getUser(e.getPlayer().getUniqueId()).rightClick += 1;
                break;
        }
    }

    public void Load() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                for (Map.Entry<UUID, User> a : User.users.entrySet()) {
                    User v = a.getValue();
                    speedcubingServer.tcp.send(v.tcpPort, "cps|" + a.getKey() + "|" + v.leftClick + "|" + v.rightClick);
                    if (v.leftClick >= config.LeftCpsLimit || v.rightClick >= config.RightCpsLimit)
                        Bukkit.getScheduler().runTask(speedcubingServer.getPlugin(speedcubingServer.class), () -> Bukkit.getPlayer(a.getKey()).kickPlayer("You are clicking too fast !"));
                    v.leftClick = 0;
                    v.rightClick = 0;
                }
            }
        }, 0, 1000);
    }
}
