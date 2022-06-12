package speedcubing.server.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import speedcubing.server.config;
import speedcubing.server.libs.User;
import speedcubing.server.speedcubingServer;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

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
                    User user = a.getValue();
                    if (user.listened) {
                        speedcubingServer.tcp.send(user.tcpPort, "cps|" + a.getKey() + "|" + user.leftClick + "|" + user.rightClick);
                        if (user.leftClick >= config.LeftCpsLimit || user.rightClick >= config.RightCpsLimit)
                            Bukkit.getScheduler().runTask(speedcubingServer.getPlugin(speedcubingServer.class), () -> Bukkit.getPlayer(a.getKey()).kickPlayer("You are clicking too fast !"));
                        else {
                            user.leftClick = 0;
                            user.rightClick = 0;
                        }
                    }
                }
            }
        }, 0, 1000);
    }
}
