package speedcubing.server.things.events;

import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class
PlayerDeath implements Listener {
    public void PlayerDeathEvent(PlayerDeathEvent e) {
        e.setDeathMessage("");
    }
}
