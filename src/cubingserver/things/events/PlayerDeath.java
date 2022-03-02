package cubingserver.things.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class
PlayerDeath implements Listener {
    public void PlayerDeathEvent(PlayerDeathEvent e) {
        e.setDeathMessage("");
    }
}
