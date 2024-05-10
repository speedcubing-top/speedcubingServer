package top.speedcubing.server.events.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class HubEvent extends PlayerEvent implements Cancellable {
    private boolean cancel;

    public HubEvent(Player player) {
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
