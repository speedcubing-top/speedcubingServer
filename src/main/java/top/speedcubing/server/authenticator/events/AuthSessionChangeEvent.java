package top.speedcubing.server.authenticator.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import top.speedcubing.server.player.User;


public class AuthSessionChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private User user;
    private final Boolean status;
    public AuthSessionChangeEvent(Player player, Boolean status) {
        this.player = player;
        this.status = status;
    }
    public AuthSessionChangeEvent(User user, Boolean status) {
        this.user = user;
        this.status = status;
    }

    public Player getPlayer() {
        return player;
    }

    public User getUser() {
        return user;
    }

    public Boolean getStatus() {
        return status;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
