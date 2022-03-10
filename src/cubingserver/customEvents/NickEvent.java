package cubingserver.customEvents;

import java.util.UUID;

public class NickEvent {
    public String name;
    public int rank;
    public UUID uuid;
    public boolean nick;

    public NickEvent(String name, int rank, UUID uuid, boolean nick) {
        this.name = name;
        this.rank = rank;
        this.uuid = uuid;
        this.nick = nick;
    }
}
