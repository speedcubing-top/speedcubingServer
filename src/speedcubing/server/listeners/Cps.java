package speedcubing.server.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import speedcubing.server.config;
import speedcubing.server.libs.User;
import speedcubing.server.speedcubingServer;

import java.util.*;

public class Cps implements Listener {
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
                for (UUID set : CpsListening) {
                    User a = User.getUser(set);
                    if (a != null) {
                        speedcubingServer.tcp.send(a.tcpPort, "cps|" + set + "|" + a.leftClick + "|" + a.rightClick);
                        if (a.leftClick >= config.LeftCpsLimit || a.rightClick >= config.RightCpsLimit)
                            Bukkit.getScheduler().runTask(speedcubingServer.getPlugin(speedcubingServer.class), () -> Bukkit.getPlayer(set).kickPlayer("You are clicking too fast !"));
                        else {
                            a.leftClick = 0;
                            a.rightClick = 0;
                        }
                    }
                }
            }
        }, 0, 1000);
    }
}
