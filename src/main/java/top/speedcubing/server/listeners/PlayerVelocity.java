package top.speedcubing.server.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;
import top.speedcubing.server.libs.User;

public class PlayerVelocity implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void PlayerVelocityEvent(PlayerVelocityEvent e) {
        Player player = e.getPlayer();
        double[] d = User.getUser(player).velocities;
        if (d != null) {
            Vector old = player.getVelocity();
            player.setVelocity(new Vector(old.getX() * d[0],old.getY() * d[1], old.getZ() * d[0]));
        }
    }
}