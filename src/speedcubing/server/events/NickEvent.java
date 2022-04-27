package speedcubing.server.events;

import java.util.UUID;

public class NickEvent {
    public String name;
    public String rank;
    public UUID uuid;
    public boolean nick;

    public NickEvent(String name, String rank, UUID uuid, boolean nick) {
        this.name = name;
        this.rank = rank;
        this.uuid = uuid;
        this.nick = nick;
    }
}
