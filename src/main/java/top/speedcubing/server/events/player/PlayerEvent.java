package top.speedcubing.server.events.player;

import org.bukkit.entity.Player;
import top.speedcubing.lib.eventbus.LibEventManager;

public class PlayerEvent extends LibEventManager {
    public final Player player;
    public PlayerEvent(Player player) {
        this.player = player;
    }
}
