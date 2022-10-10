package top.speedcubing.server.libs;

import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.speedcubing.lib.utils.SQL.SQLConnection;
import top.speedcubing.server.config;
import top.speedcubing.server.speedcubingServer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
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
        permissions.forEach(a -> {
            String s = a.substring(6);
            if (group.matcher(a).matches() && config.grouppermissions.containsKey(s))
                permissions.addAll(config.grouppermissions.get(s));
        });
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
