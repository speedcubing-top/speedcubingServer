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
        player.setVelocity(applyKnockback(player.getVelocity(), User.getUser(player)));
    }

    public static Vector applyKnockback(Vector v, User user) {
        double[] d = user.velocities;
        return d == null ? v : v.setX(v.getX() * d[0]).setY(v.getY() * d[1]).setZ(v.getZ() * d[0]);
    }
}