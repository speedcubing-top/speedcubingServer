package speedcubing.server.libs;

import org.apache.commons.lang.ArrayUtils;
import speedcubing.lib.utils.Reflections;
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
    public int tcpPort;

    public User() {
    }

    public User(UUID uuid, String rank, Set<String> permissions) {
        this.lang = speedcubingServer.connection.selectInt("playersdata", "lang", "uuid='" + uuid + "'");
        this.rank = rank;
        this.permissions = permissions;
        this.velocities = ArrayUtils.toPrimitive(speedcubingServer.veloStorage.get(uuid));
        this.tcpPort = speedcubingServer.tcpStorage.get(uuid);
        users.put(uuid, this);
    }

    public void init(Object o, UUID uuid) {
        Reflections.copyParentFields(o, getUser(uuid));
    }

    public static User getUser(UUID uuid) {
        return users.get(uuid);
    }
}
