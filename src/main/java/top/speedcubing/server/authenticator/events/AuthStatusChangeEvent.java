package top.speedcubing.server.authenticator.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class AuthStatusChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private UUID uuid;
    private Boolean status;

    public AuthStatusChangeEvent(UUID uuid, Boolean status) {
        this.uuid = uuid;
        this.status = status;
    }

    public UUID getUuid() {
        return uuid;
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
