package speedcubing.server.things.events;

import speedcubing.server.libs.User;
import speedcubing.server.speedcubingServer;
import speedcubing.server.things.Cps;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerQuit implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerQuitEvent(PlayerQuitEvent e) {
        e.setQuitMessage("");
        e.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        UUID uuid = e.getPlayer().getUniqueId();
        Cps.Counter.remove(uuid);
        PlayerJoin.RemovePackets.remove(uuid);
        PlayerJoin.JoinPackets.remove(uuid);
        User.users.remove(uuid);
    }
}
