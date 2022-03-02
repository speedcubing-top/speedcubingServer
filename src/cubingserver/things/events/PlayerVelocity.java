package cubingserver.things.events;

import cubingserver.speedcubingServer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

import java.util.UUID;

public class PlayerVelocity implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void PlayerVelocityEvent(PlayerVelocityEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        if (speedcubingServer.velocities.containsKey(uuid)) {
            Double[] d = speedcubingServer.velocities.get(uuid);
            Vector old = player.getVelocity();
            player.setVelocity(new Vector(old.getX() * d[0],old.getY() * d[1], old.getZ() * d[0]));
        }
    }
}