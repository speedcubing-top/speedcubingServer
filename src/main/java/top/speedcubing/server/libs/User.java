package top.speedcubing.server.libs;

import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.speedcubing.server.config;
import top.speedcubing.server.speedcubingServer;

import java.util.*;
import java.util.regex.Pattern;

public class User {

    public static User getUser(int id) {
        return usersByID.get(id);
    }

    public static User getUser(CommandSender sender) {
        return usersByUUID.get(((Player) sender).getUniqueId());
    }

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

    public User(Player player, String rank, Set<String> permissions, int lang, int id, boolean allowOp, String[] bungeeData) {
        this.player = player;
        Set<String> groups = new HashSet<>();
        for (String s : permissions) {
            if (group.matcher(s).matches() && config.grouppermissions.containsKey(s.substring(6)))
                groups.add(s.substring(6));
        }
        groups.forEach(a -> permissions.addAll(config.grouppermissions.get(a)));
        this.permissions = permissions;
        speedcubingServer.preLoginStorage.remove(id);
        if (!bungeeData[6].equals("null"))
            this.velocities = new double[]{Double.parseDouble(bungeeData[6]), Double.parseDouble(bungeeData[7])};
        this.lang = lang;
        this.id = id;
        this.rank = rank;
        this.tcpPort = Integer.parseInt(bungeeData[5]);
        this.allowOp = allowOp;
        usersByID.put(id, this);
        usersByUUID.put(player.getUniqueId(), this);
    }

    public void sendLangMessage(String[] s) {
        player.sendMessage(s[lang]);
    }
}
