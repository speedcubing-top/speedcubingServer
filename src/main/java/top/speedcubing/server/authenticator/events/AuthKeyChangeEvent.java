package top.speedcubing.server.authenticator.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class AuthKeyChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final UUID uuid;
    private final String key;
    public AuthKeyChangeEvent(UUID uuid, String key) {
        this.uuid = uuid;
        this.key = key;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getKey() {
        return key;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }

}
