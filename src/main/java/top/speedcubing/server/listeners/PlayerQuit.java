package top.speedcubing.server.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.spigotmc.RestartCommand;
import top.speedcubing.server.libs.User;
import top.speedcubing.server.speedcubingServer;

public class PlayerQuit implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerQuitEvent(PlayerQuitEvent e) {
        e.setQuitMessage("");
        Player player = e.getPlayer();
        User.usersByID.remove(User.getUser(player).id);
        User.usersByUUID.remove(player.getUniqueId());
        if (Bukkit.getOnlinePlayers().size() == 1 && speedcubingServer.restartable)
            RestartCommand.restart();
    }
}
