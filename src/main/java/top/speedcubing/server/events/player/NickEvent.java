package top.speedcubing.server.events.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class NickEvent extends PlayerEvent implements Cancellable {
    private boolean cancel;

    public NickEvent(Player player) {
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
