package top.speedcubing.server.events.player;

import org.bukkit.entity.Player;
import top.speedcubing.lib.eventbus.CubingEvent;

public abstract class PlayerEvent extends CubingEvent {
    private final Player player;

    public PlayerEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
