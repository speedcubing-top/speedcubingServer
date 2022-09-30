package top.speedcubing.server.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import top.speedcubing.server.config;
import top.speedcubing.server.libs.User;
import top.speedcubing.server.speedcubingServer;

import java.util.Timer;
import java.util.TimerTask;

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
                User.getUser(e.getPlayer()).leftClick += 1;
                break;
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                User.getUser(e.getPlayer()).rightClick += 1;
                break;
        }
    }

    public void Load() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                for (User user : User.usersByID.values()) {
                    if (user.listened)
                        speedcubingServer.tcp.send(user.tcpPort, "cps|" + user.id + "|" + user.leftClick + "|" + user.rightClick);
                    if (user.leftClick >= config.LeftCpsLimit || user.rightClick >= config.RightCpsLimit)
                        Bukkit.getScheduler().runTask(speedcubingServer.getPlugin(speedcubingServer.class), () -> user.player.kickPlayer("You are clicking too fast !"));
                    user.leftClick = 0;
                    user.rightClick = 0;
                }
            }
        }, 0, 1000);
    }
}
