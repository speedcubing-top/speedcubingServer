package speedcubing.server.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

public class PlayerKick implements Listener {
    @EventHandler
    public void PlayerKickEvent(PlayerKickEvent e) {
        if (e.getReason().equals("disconnect.spam"))
            e.setCancelled(true);
    }
}

