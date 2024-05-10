package top.speedcubing.server.events.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class ToggleFlyEvent extends PlayerEvent implements Cancellable {
    private boolean cancel;

    public ToggleFlyEvent(Player player) {
        super(player);
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
