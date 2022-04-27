package speedcubing.server.things;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class froze implements Listener {

    public static Set<UUID> frozed = new HashSet<>();
    @EventHandler
    public void Move(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (frozed.contains(player.getUniqueId()))
            player.teleport(e.getFrom());
    }
}
