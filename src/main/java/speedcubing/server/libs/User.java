package speedcubing.server.libs;

import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import speedcubing.server.speedcubingServer;

import java.util.*;
import java.util.regex.Pattern;

public class User {
    public static Map<Integer, User> usersByID = new HashMap<>();
    public static Map<UUID, User> usersByUUID = new HashMap<>();
    public final Player player;

    public Set<String> permissions;
    public double[] velocities;
    public int lang;
    public final int id;
    public String rank;
    public int tcpPort;
    public boolean allowOp;
    public boolean listened;

    public PacketPlayOutScoreboardTeam joinPacket;
    public PacketPlayOutScoreboardTeam leavePacket;
    public int leftClick;
    public int rightClick;

    public static Pattern group = Pattern.compile("^group\\.[^|*.]+$");

    public User(Player player, String rank, Set<String> permissions, int lang, int id, boolean allowOp) {
        this.player = player;
        Set<String> groups = new HashSet<>();
        for (String s : permissions) {
            if (group.matcher(s).matches() && speedcubingServer.grouppermissions.containsKey(s.substring(6)))
                groups.add(s.substring(6));
        }
        groups.forEach(a -> permissions.addAll(speedcubingServer.grouppermissions.get(a)));
        this.permissions = permissions;
        this.velocities = ArrayUtils.toPrimitive(speedcubingServer.veloStorage.get(id));
        this.lang = lang;
        this.id = id;
        this.rank = rank;
        Integer i = speedcubingServer.tcpStorage.get(id);
        this.tcpPort = i == null ? 0 : i;
        this.allowOp = allowOp;
        usersByID.put(id, this);
        usersByUUID.put(player.getUniqueId(), this);
    }

    public User(UUID uuid) {
        User user = usersByUUID.get(uuid);
        this.player = user.player;
        this.permissions = user.permissions;
        this.velocities = user.velocities;
        this.lang = user.lang;
        this.id = user.id;
        this.rank = user.rank;
        this.tcpPort = user.tcpPort;
        this.allowOp = user.allowOp;
        this.listened = user.listened;
        this.joinPacket = user.joinPacket;
        this.leavePacket = user.leavePacket;
    }

    public static User getUser(int id) {
        return usersByID.get(id);
    }

    public static User getUser(Player player) {
        return usersByUUID.get(player.getUniqueId());
    }

    public static User getUser(CommandSender sender) {
        return getUser((Player) sender);
    }
}
