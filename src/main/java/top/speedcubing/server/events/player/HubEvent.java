package top.speedcubing.server.events.player;

import org.bukkit.entity.Player;

public class HubEvent extends PlayerEvent {
    public boolean isCancelled;

    public HubEvent(Player player) {
        super(player);
    }
}
