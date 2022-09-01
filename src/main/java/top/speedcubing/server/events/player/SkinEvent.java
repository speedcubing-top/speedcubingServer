package top.speedcubing.server.events.player;

import org.bukkit.entity.Player;

public class SkinEvent extends PlayerEvent {
    public boolean isCancelled;

    public SkinEvent(Player player) {
        super(player);
    }
}
