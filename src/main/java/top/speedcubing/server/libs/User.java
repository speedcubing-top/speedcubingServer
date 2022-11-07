package top.speedcubing.server.libs;

import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.speedcubing.lib.utils.SQL.SQLConnection;
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

    public User(Player player, String rank, Set<String> permissions, int lang, int id, boolean allowOp, PreLoginData bungeeData) {
        this.player = player;
        Set<String> groups = new HashSet<>();
        for (String s : permissions) {
            if (group.matcher(s).matches() && config.grouppermissions.containsKey(s.substring(6)))
                groups.add(s.substring(6));
        }
        groups.forEach(a -> permissions.addAll(config.grouppermissions.get(a)));
        this.permissions = permissions;
        speedcubingServer.preLoginStorage.remove(id);
        this.listened = bungeeData.cps;
        if (!bungeeData.hor.equals("null"))
            this.velocities = new double[]{Double.parseDouble(bungeeData.hor), Double.parseDouble(bungeeData.ver)};
        this.lang = lang;
        this.id = id;
        this.rank = rank;
        this.tcpPort = bungeeData.port;
        this.allowOp = allowOp;
        usersByID.put(id, this);
        usersByUUID.put(player.getUniqueId(), this);
    }

    public void sendLangMessage(String[] s) {
        player.sendMessage(s[lang]);
    }

    public LangMessage langMessageSender(String[] s) {
        return new LangMessage(s[lang], this);
    }

    public SQLConnection.SQLBuilder dbSelect(String field) {
        return speedcubingServer.connection.select(field).from("playersdata").where("id=" + id);
    }

    public void dbUpdate(String field) {
        speedcubingServer.connection.update("playersdata", field, "id=" + id);
    }

    public static class LangMessage {

        private String s;
        private final User user;

        public LangMessage(String s, User user) {
            this.s = s;
            this.user = user;
        }

        public LangMessage replace(String s1, String s2) {
            s = s.replace(s1, s2);
            return this;
        }

        public void send() {
            user.player.sendMessage(s);
        }
    }
}
