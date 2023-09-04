package top.speedcubing.server.events.player;

import org.bukkit.entity.Player;
import top.speedcubing.lib.eventbus.CubingEvent;

public class PlayerEvent extends CubingEvent {
    public final Player player;

    public PlayerEvent(Player player) {
        this.player = player;
    }
}
