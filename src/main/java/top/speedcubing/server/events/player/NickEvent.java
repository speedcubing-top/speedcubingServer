package top.speedcubing.server.events.player;

import org.bukkit.entity.Player;

public class NickEvent extends PlayerEvent {
    public boolean isCancelled;

    public NickEvent(Player player) {
        super(player);
    }
}
