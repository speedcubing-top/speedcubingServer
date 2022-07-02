package speedcubing.server.events.player;

import org.bukkit.entity.Player;

public class PlayerEvent {
    public final Player player;
    public PlayerEvent(Player player) {
        this.player = player;
    }
}
