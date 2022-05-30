package speedcubing.server.libs;

import org.apache.commons.lang.ArrayUtils;
import speedcubing.server.speedcubingServer;

import java.util.*;

public class User {
    public static Map<UUID, User> users = new HashMap<>();

    public Set<String> permissions;
    public double[] velocities;
    public int lang;
    public String rank;
    public int tcpPort;

    public User(UUID uuid, String rank, Set<String> permissions) {
        this.lang = speedcubingServer.connection.selectInt("playersdata", "lang", "uuid='" + uuid + "'");
        this.rank = rank;
        this.permissions = permissions;
        this.velocities = ArrayUtils.toPrimitive(speedcubingServer.veloStorage.get(uuid));
        this.tcpPort = speedcubingServer.tcpStorage.get(uuid);
        users.put(uuid, this);
    }

    public static User getUser(UUID uuid) {
        return users.get(uuid);
    }
}
