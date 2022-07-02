package speedcubing.server.events.player;

import org.bukkit.entity.Player;

public class ToggleFlyEvent extends PlayerEvent {
    public boolean isCancelled;
    public ToggleFlyEvent(Player player) {
        super(player);
    }
}
