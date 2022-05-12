package speedcubing.server.libs;

import speedcubing.server.speedcubingServer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class User {
    public static Map<UUID, User> users = new HashMap<>();

    public Set<String> permissions;
    public double[] velocities;

    public int lang;
    public String rank;

    public User() {
    }

    public User(UUID uuid, String rank, Set<String> permissions) {
        this.lang = speedcubingServer.connection.selectInt("playersdata", "lang", "uuid='" + uuid + "'");
        this.rank = rank;
        this.permissions = permissions;
        users.put(uuid, this);
    }

    public static User get(UUID uuid) {
        return users.get(uuid);
    }
}
